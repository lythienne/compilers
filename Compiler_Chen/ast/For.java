package ast;

import emitter.Emitter;
import environment.Environment;
import scanner.CompileTimeException;

/**
 * For class is a statement that represents a for loop with a variable that takes on a range of
 * values and executes a statement for each pass of the loop
 * Executing the for will repeatedly execute the statement with the variable starting at the start
 * value and increasing each time the statement is executed until it executes with the end value
 * 
 * Implements HasLocality because the index variable is a local variable
 * 
 * @author Harrison Chen
 * @version 11/26/23
 */
public class For extends Statement implements HasLocality
{
    private Variable v;
    private Expression start;
    private Expression end;
    private Statement s;

    /**
     * Creates a for statement with a variable, start and end expressions, a statement to be 
     * executed, and its own environment
     * @param var the variable that takes on the values
     * @param startExp the expression that gives what value the variable will start out as
     * @param endExp the expression that gives at what value for the variable the loop will end on
     * @param stmt the statement to be repeatedly executed
     */
    public For(Variable var, Expression startExp, Expression endExp, Statement stmt)
    {
        v = var;
        start = startExp;
        end = endExp;
        s = stmt;
    }

    @Override
    /**
     * Executing the for evaluates the start and end expressions to get the start and end values,
     * it starts by giving the variable its starting value and then executes the statement, each 
     * time the variable increases by 1 and the statement is executed again until the variable
     * is at the ending value.
     * @precondtion the start and end expressions must evaluate to integers
     * @param env the environment the for loop is in
     */
    public void exec(Environment env)
    {
        Object startInt = start.eval(env);
        Object endInt = end.eval(env);
        Environment local = new Environment(env);
        if(!start.isInt())
            throw new IllegalArgumentException("FOR range start not an int");
        if(!end.isInt())
            throw new IllegalArgumentException("FOR range end not an int");
        local.declareVar(v, null);
        int from = (Integer) startInt;
        int to = (Integer) endInt;
        for(int i = from; i <= to; i++)
        {
            local.setVar(v, i);
            s.exec(local);
            i = (Integer) local.getVal(v);
        }
    }

    @Override
    /**
     * Returns the offset of the given variable in the stack (which will be 4 for the end values
     * @param varName the name of the given variable
     * @return the integer offset on the stack (not counting excess items added to the stack),
     *         will be a multiple of 4
     * @throws CompileTimeException if the local variable the offset is trying to get is the index
     */
    public int getOffset(String varName)
    {
        return 4;           //end over var on stack
    }

    /**
     * Compiling the for loop compiles the start and end values of the for and then emits code to
     * run a compiled statement and update the index until it reaches the end value.
     * Uses procedure context because the index is a local variable
     * 
     * @param em the emitter used to write MIPS code for the For loop
     */
    public void compile(Emitter em)
    {
        int id = em.nextLabelID();
        String index = v.toString();
        em.emit("# For "+id);

        start.compile(em);
        em.push("$v0");                         //end over start on stack
        end.compile(em);
        em.push("$v0");

        em.emit("for"+id+":");
        if(em.hasVar(index))
            throw new CompileTimeException("Variable "+index+
                    " already declared in a higher context, can't be used in for");
        em.setProcedureContext(this);
        em.declareLocalVar(index, "INTEGER");

        em.pop("$t0");                          //end in t0
        em.loadVar(index, "INTEGER");           //index in v0
        em.emit("bgt $v0 $t0 afterFor"+id);
        em.push("$t0");                         //end back on stack

        s.compile(em);

        em.loadVar(index, "INTEGER");
        em.emit("addi $v0 $v0 1");              //increment index
        em.storeVar(index, "$v0");

        em.emit("j for"+id);
        em.emit("afterFor"+id+":");
        em.clearProcedureContext(v.toString());
        em.pop("$t0");                          //clear start from stack
    }
}
