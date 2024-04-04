package ast;

import environment.Environment;
import emitter.Emitter;

import java.util.List;
import java.util.ArrayList;
import java.time.LocalDate;

/**
 * Program class represents a PASCAL program with a list of variable, procedure, and function
 * declarations, a statement, and a global environment in which to store program-wide variables.
 * Running the program will assign variables, procedures, and functions and interpret the statement
 * to run the program.
 * @author Harrison Chen
 * @version 11/27/23
 */
public class Program 
{
    private Environment env;
    private List<VariableDeclaration> variables;
    private List<ConstDeclaration> constants;
    private List<ProcedureDeclaration> procedures;
    private List<FunctionDeclaration> functions;
    private Statement stmt;

    private List<String> strings;
    private List<Float> reals;

    /**
     * Creates a program with a statement (bare minimum) and an empty global environment
     * @param statement the single statement of the program
     * @param stringList the list of all the strings in the program
     * @param realList the list of all the reals in the program
     */
    public Program(Statement statement, List<String> stringList, List<Float> realList)
    {
        env = new Environment(null);
        stmt = statement;
        variables = null;
        constants = null;
        procedures = null;
        functions = null;
        strings = stringList;
        reals = realList;
    }

    /**
     * Sets all the variable declarations of the program into a single list, since there might have
     * been multiple instances of var maybevars, which would generate multiple lists of variable
     * declarations. Stores the resulting list into the variable declaration list of the program
     * @param variableList the list of lists of variable declarations to combine
     */
    public void setVariables(List<List<VariableDeclaration>> variableList)
    {
        variables = new ArrayList<VariableDeclaration>();
        for(List<VariableDeclaration> vL : variableList)
            for(VariableDeclaration v : vL)
                variables.add(v);
    }
    /**
     * Sets all the constant declarations of the program into a single list, since there might have
     * been multiple instances of const maybeconsts, which would generate multiple lists of constant
     * declarations. Stores the resulting list into the constant declaration list of the program
     * @param constantList the list of lists of constant declarations to combine
     */
    public void setConstants(List<List<ConstDeclaration>> constantList)
    {
        constants = new ArrayList<ConstDeclaration>();
        for(List<ConstDeclaration> cL : constantList)
            for(ConstDeclaration c : cL)
                constants.add(c);
    }
    /**
     * Sets the list of procedure declarations passed in to the stored procedureList (just to reduce
     * clutter)
     * @param procedureList the list of procedure declarations to store
     */
    public void setProcedures(List<ProcedureDeclaration> procedureList)
    {
        procedures = procedureList;
    }
    /**
     * Sets the list of function declarations passed in to the stored functionlist (just to reduce
     * clutter)
     * @param functionList the list of function declarations to store
     */
    public void setFunctions(List<FunctionDeclaration> functionList)
    {
        functions = functionList;
    }

    /**
     * Runs the program, initializing all the variables with null values in the global environment
     * and stores all the procedures and functions in the global environment as well, then executes
     * the statement
     */
    public void run()
    {
        for(VariableDeclaration v : variables)
            v.exec(env);
        for(ConstDeclaration c : constants)
            c.exec(env);
        for(ProcedureDeclaration p : procedures)
            p.exec(env);
        for(FunctionDeclaration f : functions)
            f.exec(env);
        stmt.exec(env);
    }
    
    /**
     * Compiles the Program, setting up the .data tag under which all the variable declarations are
     * compiled, in addition to the strings \n, true, and false that are used by the program. Every
     * string and real in the program is also added to the data section with ids. Sets up the main
     * label under the .text tag that compiles the single statement, and then writes the MIPS to 
     * exit the program. Underneath, subroutines the program uses like boolToString and stringLength
     * are stored.
     * @param em the Emitter used to write MIPS code for the if statement
     */
    public void compile(Emitter em)
    {
        em.emit("# MIPS 32 Assembly Code generated by a Compiler\n" +
                "# Disclaimer: Autogenerated code to be run in the MARS Simulator DO NOT EDIT\n"+
                "# @author Harrison Chen\n" +
                "# @version "+LocalDate.now()+"\n"+
                "# (backwards smiley for emitter) (:");
        for(FunctionDeclaration f : functions)
            f.addFunctionToEmitter(em);
        em.emit(".data");
        for(VariableDeclaration v : variables)
            v.compile(em);
        em.emit("pgm_newline: .asciiz \"\\n\"");
        em.emit("pgm_true: .asciiz \"true\"");
        em.emit("pgm_false: .asciiz \"false\"");
        em.emit("pgm_notABooleanError: .asciiz \"ERROR: Inputted text is not a boolean\"");

        for(int i = 0; i<strings.size(); i++)
            em.emit("str_"+i+": .asciiz \""+strings.get(i)+"\"");
        for(int i = 0; i<reals.size(); i++)
            em.emit("real_"+i+": .float "+reals.get(i)+"");

        em.emit(".text");
        em.emit(".globl main");

        em.emit("j main");

        em.emit("\n# PROCEDURE/FUNCTION DECLARATIONS:");
        for(ProcedureDeclaration p : procedures) p.compile(em);
        for(FunctionDeclaration f : functions) f.compile(em);

        em.emit("main:");

        stmt.compile(em);

        em.emit("li $v0 10");
        em.emit("syscall");

        em.emit("\n");

        em.emit("# boolean.toString, input($v0): 0=false, -1=true, output($v0): str address\n"+
                "pgm_boolToString:\n\t"+
                    "beqz $v0 pgm_printFalse\n\t"+
                    "la $v0 pgm_true\n\t"+
                    "jr $ra\n"+
                "pgm_printFalse:\n\t"+
                    "la $v0 pgm_false\n"+
                    "jr $ra");

        em.emit("# subroutine to find length of string stored in $a0, returns as word in $v0");
        em.emit("move $v0 $zero");
        em.iterateThroughString("addi $v0 $v0 1", "pgm_strLength", "$a0");
        em.emit("jr $ra");

        em.close();
    }
}
