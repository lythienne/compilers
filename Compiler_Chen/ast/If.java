package ast;

import emitter.Emitter;
import environment.Environment;

/**
 * If class is a statement that represents a conditional with a boolean expression and one or two
 * statements.
 * Executing the if will check if the expression is true or false, if true it will execute the then
 * statement, if false it will execute the else statement (if available)
 * @author Harrison Chen
 * @version 11/24/23
 */
public class If extends Statement
{
    private Expression cond;
    private Statement thenStmt;
    private Statement elseStmt;

    /**
     * Creates an if statement with a conditional expression, a then statement, and an 
     * else statement
     * @param condition the conditional expression that will be true or false
     * @param thenStatement the statement that evaluates if the condition is true
     * @param elseStatement the statement that evaluates if the condition is false
     */
    public If(Expression condition, Statement thenStatement, Statement elseStatement)
    {
        cond = condition;
        thenStmt = thenStatement;
        elseStmt = elseStatement;
    }

    @Override
    /**
     * Executes the if, evaluating the condition expression and executing the correct statement
     * (or none if the condition is false and there is no else statement)
     * @precondition the condition expression must evaluate to a boolean
     * @param env the environment the if is in
     */
    public void exec(Environment env)
    {
        Object condition = cond.eval(env);
        if(!cond.isBool()) 
            throw new IllegalArgumentException("Not a boolean expression after IF");
        if((Boolean) condition)
            thenStmt.exec(env);
        else
            if(elseStmt != null) elseStmt.exec(env);
    }

    @Override
    /**
     * Compiles the If, first compiling the condition then setting up a branch that will
     * jump to the else statement if the value of the condition is 0, or the then statement
     * if the value of the condition is -1, if there is no else statement, it is not compiled
     * @param em the Emitter used to write MIPS code for the if statement
     */
    public void compile(Emitter em)
    {
        int id = em.nextLabelID();
        em.emit("# IF "+id);
        cond.compile(em);
        em.emit("beqz $v0 else"+id);
        thenStmt.compile(em);
        em.emit("j afterIf"+id);
        em.emit("else"+id+":");
        if(elseStmt != null) elseStmt.compile(em);
        em.emit("afterIf"+id+":");
    }
}
