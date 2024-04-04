package ast;

import emitter.Emitter;
import environment.Environment;

/**
 * While class is a statement that represents a while loop with a boolean expression and a 
 * statement.
 * Executing the if will repeatedly check if the expression is true and executing the
 * statement until the expression is false.
 * @author Harrison Chen
 * @version 11/24/23
 */
public class While extends Statement
{
    private Expression cond;
    private Statement stmt;

    /**
     * Creates an while statement with a conditional expression, and a statement to be executed
     * @param condition the conditional expression
     * @param statement the statement to be repeatedly executed
     */
    public While(Expression condition, Statement statement)
    {
        cond = condition;
        stmt = statement;
    }

    /**
     * Executing the while will continue executing the statement so long as the expression stays
     * true, the loop will check the expressions value before executing the statement each time
     * @param env the environment the while loop is in
     */
    public void exec(Environment env)
    {
        if(!cond.isBool()) 
            throw new IllegalArgumentException("Not a boolean expression after WHILE");
        while((Boolean) cond.eval(env)) stmt.exec(env);
    }

    @Override
    /**
     * Compiles the While, first compiling the condition then setting up a branch that will
     * jump to end if the value of the condition is 0 (false), or continue running the statement
     * if the value of the condition is -1 (true), and jumping back to the loop
     * @param em the Emitter used to write MIPS code for the while statement
     */
    public void compile(Emitter em)
    {
        int id = em.nextLabelID();
        em.emit("# WHILE "+id);
        em.emit("while"+id+":");
        cond.compile(em);
        em.emit("beqz $v0 afterWhile"+id);
        stmt.compile(em);
        em.emit("j while"+id);
        em.emit("afterWhile"+id+":");
    }
}
