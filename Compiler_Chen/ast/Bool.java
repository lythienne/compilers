package ast;

import emitter.Emitter;
import environment.Environment;

/**
 * Bool class is a boolean expression that just represents a boolean (true/false)
 * Evaluating the boolean gives its value
 * @author Harrison Chen
 * @version 11/24/23
 */
public class Bool extends BoolExp
{
    private final boolean val;

    /**
     * Creates a Bool with a boolean value
     * @param value the value
     */
    public Bool(boolean value)
    {
        val = value;
    }

    @Override
    /**
     * Evaluating the Bool gives its value
     * @param env the environment in which the boolean is in (unused)
     * @return the value of the Bool
     */
    public Boolean eval(Environment env)
    {
        return val;
    }

    @Override
    /**
     * Compiles the boolean by loading its bit value into $v0 (0=false, -1=true)
     * @param em the emitter used to compile the boolean
     */
    public void compile(Emitter em)
    {
        if(val) em.emit("li $v0 -1");
        else em.emit("move $v0 $zero");
    }
}
