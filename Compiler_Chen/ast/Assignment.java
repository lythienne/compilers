package ast;

import emitter.Emitter;
import environment.Environment;

/**
 * Assignment class is a statement that represents an assignment of a variable to an expression
 * Executing the assignment will assign the variable a type if it doesn't have one (given by
 * the expression) and then putting it (or replacing it) in the environment
 * @author Harrison Chen
 * @version 11/24/23
 */
public class Assignment extends Statement
{
    private Variable var;
    private Expression exp;

    /**
     * Creates an Assignment statement with a variable and an expression
     * @param variable the variable
     * @param expression the expression
     */
    public Assignment(Variable variable, Expression expression)
    {
        var = variable;
        exp = expression;
    }

    /**
     * Executes the assignment, checks if the variable is declared or not and sets the value of the
     * variable to the value of the expression if it has been declared. The value of the expression
     * should match in type with the variable or the environment will throw an error if the types
     * don't match.
     * @precondition the variable should be declared
     * @param env the environment to put the variable in
     */
    public void exec(Environment env)
    {
        Variable v = env.getVar(var);
        Object expValue = exp.eval(env);
        if(v.isInt() && exp.isReal())
            expValue = ((Float) expValue).intValue();
        else if(v.isReal() && exp.isInt())
            expValue = ((Integer) expValue).floatValue();
        env.setVar(v, expValue);
    }

    @Override
    /**
     * Compiles the assignment, by first compiling the expression (which will have its value stored 
     * in $v0), and then storing the value of the expression into the variable
     * @param em the emitter used to compile the assignment
     */
    public void compile(Emitter em)
    {
        em.emit("# Assignment");
        exp.compile(em);
        em.storeVar(var.toString(), "$v0");
    }
}
