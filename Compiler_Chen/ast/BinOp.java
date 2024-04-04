package ast;

import emitter.Emitter;
import environment.Environment;
import scanner.CompileTimeException;

/**
 * Binary Operator class is an expression represents a binary operator between two expression
 * Evaluating the binary operator will evaluate each expression and then use the operator on 
 * the two expressions
 * @author Harrison Chen
 * @version 11/27/23
 */
public class BinOp extends Expression
{
    private String op;
    private Expression e1;
    private Expression e2;

    /**
     * Creates a binary operator expression with an operator and two expressions
     * @param operator the operator (+,-,*,/,mod,AND,OR)
     * @param exp1 the first expression
     * @param exp2 the second expression
     */
    public BinOp(String operator, Expression exp1, Expression exp2)
    {
        op = operator;
        e1 = exp1;
        e2 = exp2;
    }

    @Override
    /**
     * Checks if the binary operator will evaluate to be a boolean
     * @return if both the expressions are booleans
     */
    public boolean isBool()
    {
        return e1.isBool() && e2.isBool();
    }
    @Override
    /**
     * Checks if the binary operator will evaluate to be an integer
     * @return if both the expressions are integers
     */
    public boolean isInt()
    {
        return e1.isInt() && e2.isInt();
    }
    @Override
    /**
     * Checks if the binary operator will evaluate to be an real
     * @return if both the expressions are reals
     */
    public boolean isReal()
    {
        boolean int1 = e1.isInt();
        boolean int2 = e2.isInt();
        boolean real1 = e1.isReal();
        boolean real2 = e2.isReal();
        return real1 && real2 || int1 && real2 || int2 && real1;
    }
    @Override
    /**
     * Checks if the binary operator will evaluate to be a string
     * @return if both the expressions are strings
     */
    public boolean isStr()
    {
        return e1.isStr() || e2.isStr();
    }

    @Override
    /**
     * Evaluates the binary operator by checking the types of the expressions to see if they match,
     * then evaluating them, and finally using the operator on them and returning the result.
     * @precondition the expressions should match in type and the operator should be one that
     *               matches the expressions (you can't add booleans for example)
     * @param env the environment (unused)
     * @return the integer, boolean, or string that is the result of doing the operation on the two
     *         expressions
     */
    public Object eval(Environment env)
    {
        Object eval1 = e1.eval(env);
        Object eval2 = e2.eval(env);
        if(isInt() || isReal())
        {
            float val1;
            float val2;
            if(eval1 instanceof Integer) val1 = ((Integer)eval1).floatValue(); 
            else val1 = (Float) eval1;
            if(eval2 instanceof Integer) val2 = ((Integer)eval2).floatValue();
            else val2 = (Float) eval2;

            float result;
            switch(op)
            {
                case "*": result = val1 * val2; break;
                case "/": result =  val1 / val2; break;
                case "MOD": result =  val1 % val2; break;
                case "-": result = val1 - val2; break;
                case "+": result =  val1 + val2; break;
                default: throw new IllegalArgumentException("Operator "+op+" isn't *,/,mod,+,-");
            }
            if(!isReal()) return (int) result;
            return result;
        }
        else if(isBool())
        {
            Boolean val1 = (Boolean) eval1;
            Boolean val2 = (Boolean) eval2;
            switch(op)
            {
                case "AND": return val1 && val2;
                case "OR": return val1 || val2;
                default: throw new IllegalArgumentException("Operator "+op+" isn't AND,OR");
            }
        }
        else if(isStr())
        {
            String val1 = ""+eval1;
            String val2 = ""+eval2;
            switch(op)
            {
                case "+": return val1 + val2;
                default: throw new IllegalArgumentException("Operator "+op+" isn't +");
            }
        }
        else
            throw new IllegalArgumentException(eval1 + " is not the same type as " +eval2);
    }

    @Override
    /**
     * Compiles the BinOp, by first compiling the two expressions (which will have their values
     * stored in $v0, then on the stack), and then depending on the type of this object (which
     * depends on the types of the expressions) and the operator, BinOp will emit MIPS assembly code
     * that executes the correct operation (string concat ate my soul) and stores the result in $v0
     * @param em the emitter used to compile the BinOp
     * 
     * DOES NOT WORK WITH REALS WILL FIX MAYBE SOMETIME
     */
    public void compile(Emitter em)
    {
        e1.compile(em);
        em.push("$v0");
        e2.compile(em);
        em.pop("$t0"); //e1 val in $t0, e2 val in $v0

        em.emit("# BinOp");
        String opString;
        if(isInt())
        {
            switch(op)
            {
                case "*": opString = "mult $t0 $v0\n\tmflo $v0\t#multiplying"; break;
                case "/": opString = "div $t0 $v0\n\tmflo $v0\t#dividing"; break;
                case "MOD": opString = "div $t0 $v0\n\tmfhi $v0\t#mod"; break;
                case "-": opString = "sub $v0 $t0 $v0\t#subtract"; break;
                case "+": opString = "add $v0 $t0 $v0\t#add"; break;
                default: throw new CompileTimeException("Expressions are numbers and operator "
                                                            +op+" isn't *,/,mod,+,-");
            }
        }
        else if(isBool())
        {
            switch(op)
            {
                case "AND": opString = "and $v0 $t0 $v0\t#and"; break;
                case "OR": opString = "or $v0 $t0 $v0\t#or"; break;
                default: throw new CompileTimeException("Expressions are booleans and operator "
                                                            +op+" isn't AND,OR");
            }
        }
        else if(isStr())
        {
            if(!op.equals("+")) 
                    throw new CompileTimeException("Expression is string but operator isn't +");
            int lblId = em.nextLabelID();
            em.emit("move $s1 $t0");      //string 1 in s1
            em.emit("move $s2 $v0");      //string 2 in s2
            
            getStrLength(e1, "$s1", lblId, em);
            getStrLength(e2, "$s2", lblId, em);

            em.pop("$v0");                 //string 2 length in $v0
            em.pop("$a0");                 //string 1 length in $a0
            em.emit("addu $a0 $a0 $v0");  //str 1 length + str 2 length in $a0
            em.emit("addi $a0 $a0 1");    //+1 for null ending
            em.emit("li $v0 9");
            em.emit("syscall");           //allocates space = str 1 length + str 2 length + 1
            em.emit("move $t7 $v0");      //address to space in $t7 and in $v0

            putStringInSpace(e1, "$s1", lblId, em);
            lblId = em.nextLabelID();
            putStringInSpace(e2, "$s2", lblId, em);
            em.emit("sb $zero ($v0)");    //adds null ending
            em.emit("move $v0 $t7");      //put address in $v0
            opString = "";                     //im not bothering with this
        }
        else
        {
            throw new CompileTimeException("expressions do not match in type");
        }
        em.emit(opString);
    }

    /**
     * Helper method to take an expression with its value stored in a register and emits the MIPS
     * code to store the length of the string/int/boolean on the stack
     * @param e the expression (is an integer, real, boolean, or string)
     * @param reg the register containing the value of the expression
     * @param lblId the label id to use to make labels
     * @param em the emitter used to emit the code to store the string in the space
     */
    private void getStrLength(Expression e, String reg, int lblId, Emitter em)
    {
        if(e.isStr())
        {
            em.emit("move $a0 "+reg);
            em.emit("move $v0 $zero");
            em.emit("jal pgm_strLength\t#stores string length into $v0");
        }
        else if(e.isBool())
        {
            em.emit("move $v0 "+reg);
            em.emit("jal pgm_boolToString\t#converts bool to string, stores address in v0");
            em.emit("move $a0 $v0");
            em.emit("move $v0 $zero");
            em.emit("jal pgm_strLength\t#stores bool string length into $v0");
        }
        else if(e.isInt())
        {
            em.emit("li $t0 1\t#t0 = count = 1");
            em.emit("li $t1 10\t#t1 = 10");
            em.emit("move $t2 "+reg+"\t#t2 = int value");
            em.emit("bge $t2 $zero intLengthLoop"+lblId);
            em.emit("addi $t0 $t0 1\t#if num<0 then add 1 to length for - sign");
            em.emit("intLengthLoop"+lblId+":");
            em.emit("div $t2 $t1\t#div by 10");
            em.emit("mflo $t2\t#store result back in t2");
            em.emit("beqz $t2 returnIntLength"+lblId+"\t#if t2 = 0, out of digits");
            em.emit("addi $t0 $t0 1\t#increment count");
            em.emit("j intLengthLoop"+lblId);
            em.emit("returnIntLength"+lblId+":");
            em.emit("move $v0 $t0\t#save count to $v0");
        }
        else if(e.isReal()) 
            throw new CompileTimeException("you should probably implement this some day");
        else
            throw new CompileTimeException("Expression not a integer, string, real, or boolean");
        em.push("$v0");
    }

    /**
     * Helper method emits code to put the value of an expression into a space
     * @precondition the space should contain enough space for the string, another string, and a 
     *               null character, the address of the space should be in $v0
     * @postcondition the address of the next open byte in the space is stored in $v0
     * @param e the expression with a type
     * @param value_reg the register where the value of the string to print is stored, the value
     *                  can be an integer, a boolean (as an integer), or an address to a string
     * @param lblId the label id to use to make labels
     * @param em the emitter used to emit the code to store the string in the space
     */
    private void putStringInSpace(Expression e, String value_reg, int lblId, Emitter em)
    {
        if(e.isStr())
        {
            em.iterateThroughString("sb $t0 ($v0)\n\taddi $v0 $v0 1", 
                    "storeString"+lblId, value_reg);
        }
        else if(e.isBool())
        {
            em.push("$v0");
            em.emit("move $v0 "+value_reg);
            em.emit("jal pgm_boolToString");
            em.emit("move "+value_reg+" $v0");
            em.pop("$v0");
            em.iterateThroughString("sb $t0 ($v0)\n\taddi $v0 $v0 1", 
                    "storeBool"+lblId, value_reg);
        }
        else if(e.isInt())
        {
            //copied from my test file
            em.emit("\tli $t3 0\t\t\t#t3 count\n" + //
                    "        li $t1 10\t\t\t#t1 10\n" + //
                    "        move $t2 "+value_reg+"\t\t\t#t2 int\n" + //
                    "        bge $t2 $zero getIntLoop"+lblId+"\n" + //
                    "\tli $t0 45\n" + //
                    "\tsb $t0 ($v0)\t\t\t#if negative, store a -\n" + //
                    "\taddi $v0 $v0 1\n" + //
                    "\tsubu $t2 $zero $t2\t\t#make num positive\n" + //
                    "getIntLoop"+lblId+":\n" + //
                    "\tdiv $t2 $t1\t\t\t#div num by 10\n" + //
                    "        mflo $t2\t\t\t#quotient in t2\n" + //
                    "        mfhi $t0\t\t\t#remainder in t0\n" + //
                    "        addi $t0 $t0 48\t\t\t#convert to ascii\n" + //
                    "        subu $sp $sp 4\t\t\t\n" + //
                    "        sw $t0 ($sp)\t\t\t#push remainder ascii onto stack\n" + //
                    "        addi $t3 $t3 1\n" + //
                    "        beqz $t2 storeIntLoop"+lblId+"\t\t#if zero go to end\n" + //
                    "        j getIntLoop"+lblId+"\n" + //
                    "storeIntLoop"+lblId+":\n" + //
                    "\tbeqz $t3 storeIntEnd"+lblId+"\n" + //
                    "        lw $t0 ($sp)\n" + //
                    "        addu $sp $sp 4\n" + //
                    "        sb $t0 ($v0)\n" + //
                    "        addi $v0 $v0 1\n" + //
                    "        subi $t3 $t3 1\n" + //
                    "        j storeIntLoop"+lblId+"\n" + //
                    "storeIntEnd"+lblId+":");
        }
        else if(e.isReal()) 
            throw new CompileTimeException("you should probably implement this some day");
        else
            throw new CompileTimeException("Expression not a integer, string, real, or boolean");
    }
}