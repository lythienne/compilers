package ast;

import emitter.Emitter;
import environment.Environment;

/**
 * Unary Operator class is an expression represents a unary operator with an expression
 * Evaluating the unary operator will evaluate the expression and then use the unary operator on it
 * @author Harrison Chen
 * @version 11/24/23
 */
public class UnOp extends Expression
{
    private String op;
    private Expression exp;

    /**
     * Creates a unary operator expression with an operator and an expression
     * @param operator the operator
     * @param expression the expression
     */
    public UnOp(String operator, Expression expression)
    {
        op = operator;
        exp = expression;
    }

    @Override
    /**
     * Checks if the binary operator will evaluate to be a boolean
     * @return if the expressions is a boolean
     */
    public boolean isBool()
    {
        return exp.isBool();
    }
    @Override
    /**
     * Checks if the binary operator will evaluate to be an integer
     * @return if the expressions is an integer
     */
    public boolean isInt()
    {
        return exp.isInt();
    }
    @Override
    /**
     * Checks if the binary operator will evaluate to be an real
     * @return if the expressions is an real
     */
    public boolean isReal()
    {
        return exp.isReal();
    }

    @Override
    /**
     * Evaluates the unary operator by evaluating the expression, using the operator on it,
     * and returning the result.
     * @precondition the expression should match in type with the operator (you can't have a 
     *               negative boolean for example)
     * @param env the environment (unused)
     * @return the integer or boolean that is the result of doing the operation on the expression
     */
    public Object eval(Environment env)
    {
        Object eval = exp.eval(env);
        if(exp.isBool() && op.equals("NOT"))
            return !((Boolean) eval);
        else if (exp.isInt() && op.equals("-"))
            return -(Integer) eval;
        else if (exp.isReal() && op.equals("-"))
            return -(Float) eval;
        else
            throw new IllegalArgumentException(eval + "does not match operator "+op);
    }

    @Override
    /**
     * Compiles the UnOp, by first compiling the expression (which will have its value stored in 
     * $v0) and then depending on the type of the expression and the operator, UnOp will emit MIPS
     * assembly code that executes the correct operation and stores the result in $v0
     * @param em the emitter used to compile the UnOp
     * 
     * DOES NOT WORK WITH REALS WILL FIX MAYBE SOMETIME
     */
    public void compile(Emitter em)
    {
        exp.compile(em);
        em.emit("# UnOp");
        String opString = "# operator does not match expression type";
        if(exp.isBool() && op.equals("NOT"))
            opString = "not $v0 $v0";
        else if (exp.isInt() && op.equals("-"))
            opString = "sub $v0 $zero $v0";
        em.emit(opString);
    }
}