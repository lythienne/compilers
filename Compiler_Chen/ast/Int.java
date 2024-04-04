package ast;

import emitter.Emitter;
import environment.Environment;

/**
 * Int class is an expression that just represents an integer
 * Evaluating the integer gives its value
 * @author Harrison Chen
 * @version 11/24/23
 */
public class Int extends Expression
{
    private final int val;

    /**
     * Creates a Int with a integer value
     * @param value the value
     */
    public Int(int value)
    {
        val = value;
    }

    @Override
    /**
     * Evaluating the Int gives its value
     * @param env the environment in which the integer is in (unused)
     * @return the value of the Int
     */
    public Integer eval(Environment env)
    {
        return val;
    }

    @Override
    /**
     * Compiles the integer by loading its value into $v0
     * @param em the emitter used to compile the integer
     */
    public void compile(Emitter em)
    {
        if(val == 0) em.emit("move $v0 $zero");
        else em.emit("li $v0 "+val);
    }
}
