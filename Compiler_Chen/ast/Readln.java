package ast;

import environment.Environment;
import scanner.CompileTimeException;

import java.util.List;
import java.util.Scanner;

import emitter.Emitter;

/**
 * Readln class is a statement that represents an assignment of one or more variables to the same
 * amount of user inputted values
 * Executing the readln will prompt the user for input for each variable and assign the inputted
 * value to the variable
 * @author Harrison Chen
 * @version 11/27/23
 */
public class Readln extends Statement
{
    private List<String> vars;

    /**
     * Creates a Readln statement with a list of variable names
     * @param variables the list of variables that will be assigned values
     */
    public Readln(List<String> variables)
    {
        vars = variables;
    }

    @Override
    /**
     * Executing the readln will prompt the user for a value and put that value with the variable 
     * in the environment map. The value of the user input must match the type of the variable
     * @precondition each variable must be declared
     * @param env the environment in which the variables are in to store the values
     */
    public void exec(Environment env)
    {
        Scanner in = new Scanner(System.in);
        if(vars == null) in.nextLine();
        else
            for(String s : vars)
            {
                Variable v = env.getVar(new Variable(s, null)); //error if variable not declared
                if(v.isBool()) env.setVar(v, in.nextBoolean());
                else if(v.isInt()) env.setVar(v, in.nextInt());
                else if(v.isStr()) env.setVar(v, in.nextLine());
                else if(v.isReal()) env.setVar(v, in.nextFloat());
                else throw new CompileTimeException("Variable "+v+" not string/integer/boolean");
            }
    }

    /**
     * Compiles the ReadLn, for each variable, it checks its type and chooses the correct input
     * syscall to read the input from the user. If it is a boolean or a string, the resulting
     * string needs to be preprocessed to get rid of the LF tag at the end, and booleans are
     * compared back to "true" and "false" to obtain their value.
     * 
     * Max string length is 128 chars
     * 
     * @param em the Emitter used to write MIPS assembly code for the ReadLn
     */
    public void compile(Emitter em)
    {
        em.emit("# ReadLn");
        if(vars == null) em.emit("li $v0 8\n\tsyscall");
        else
            for(String s : vars)
            {
                String type = em.varType(s);
                switch (type) 
                {
                    case "INTEGER": 
                        em.emit("li $v0 5");
                        em.emit("syscall");
                        break;
                    case "STRING", "BOOLEAN": 
                        em.emit("li $v0 9");
                        em.emit("li $a0 128");      //allocate 128 bytes for string
                        em.emit("syscall");         //max str length is 128 chars
                        em.emit("move $s0 $v0");    //put address in s0
                        em.emit("move $a1 $a0");
                        em.emit("move $a0 $v0");
                        em.emit("li $v0 8");
                        em.emit("syscall");
                        preprocessString("$s0", em); //result string address in $v0
                        break;
                    default: 
                        throw new CompileTimeException("Variable "+s+" not string/integer/boolean");
                }
                if(type.equals("BOOLEAN"))
                {
                    int id = em.nextLabelID();
                    em.push("$v0");
                    em.emit("la $t0 pgm_false");           //"false" in $t0
                    em.emit("compareToFalseLoop"+id+":");
                    em.emit("lb $t1 ($t0)");               //"false" chars in $t1
                    em.emit("lb $t2 ($v0)");               //str 2 chars in $t2
                    em.emit("bne $t1 $t2 compareToTrue"+id);    //char1 != char2 then str1 != str2 then check true
                    em.emit("beqz $t1 false"+id);               //char1 = char2 = null then str1 = str2 then false
                    em.emit("addi $t0 $t0 1");
                    em.emit("addi $v0 $v0 1");             //increment chars
                    em.emit("j compareToFalseLoop"+id);

                    em.emit("compareToTrue"+id+":");
                    em.pop("$v0");
                    em.emit("la $t0 pgm_true");            //"true" in $t0
                    em.emit("compareToTrueLoop"+id+":");
                    em.emit("lb $t1 ($t0)");               //"true" chars in $t1
                    em.emit("lb $t2 ($v0)");               //str 2 chars in $t2
                    em.emit("bne $t1 $t2 notABooleanError"+id); //char1 != char2 then str1 != str2 then throw error
                    em.emit("beqz $t1 true"+id);                //char1 = char2 = null then str1 = str2 then true
                    em.emit("addi $t0 $t0 1");
                    em.emit("addi $v0 $v0 1");             //increment chars
                    em.emit("j compareToTrueLoop"+id);

                    em.emit("notABooleanError"+id+":");
                    em.throwError("pgm_notABooleanError");

                    em.emit("false"+id+":");
                    em.emit("li $v0 0");                //false = 0
                    em.emit("j after"+id);
                    em.emit("true"+id+":");
                    em.emit("li $v0 -1");               //true = -1
                    em.emit("after"+id+":");
                }
                em.storeVar(s, "$v0");
            }
    }

    /**
     * Helper method to preprocess string in a register to get rid of the LF tag at the end of it, finds the
     * length of the string, subtracts one, allocates space, and then puts the string into the space
     * @param reg the register the string to be preprocessed is in
     * @param em the emitter used to write MIPS assembly code to preprocess the string
     */
    private void preprocessString(String reg, Emitter em)
    {
        int id = em.nextLabelID();
        em.emit("move $v0 $zero");
        em.emit("jal pgm_strLength");
        em.emit("subi $v0 $v0 1");  //length of user input string -1
        em.emit("move $a0 $v0");    //length of actual string
        em.emit("li $v0 9");
        em.emit("syscall");         //allocate str length of space
        em.push("$v0");              //address of space in stack
        em.emit(    "move $t2 "+reg+"\n"+
                "preprocess"+id+"Loop:\n\t"+
                    "beqz $a0 preprocess"+id+"After\n\t"+
                    "lb $t0 ($t2)\n\t"+
                    "addi $t2 $t2 1\n\t"+
                    "sb $t0 ($v0)\n\t"+ 
                    "addi $v0 $v0 1\n\t"+
                    "subi $a0 $a0 1\n\t"+
                    "j preprocess"+id+"Loop\n"+
                "preprocess"+id+"After:"
        );
        em.pop("$v0");               //address of start of space back in $v0
    }
}
