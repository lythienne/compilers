package ast;

import emitter.Emitter;
import environment.Environment;

/**
 * Expression class represents an abstract PASCAL expression that can be evaluated and will return
 * an Integer, Boolean, or String
 * @author Harrison Chen
 * @version 11/24/23
 */
public abstract class Expression 
{
    /**
     * Evaluates the expression, returning its value
     * @param env the environment the expression will be in
     * @return an integer, boolean, or string result
     */
    public abstract Object eval(Environment env);

    /**
     * Checks if the expression will evaluate to an integer
     * @return true if it will be an integer, false if not
     */
    public boolean isInt()
    {
        return this instanceof Int;
    }
    /**
     * Checks if the expression will evaluate to an real
     * @return true if it will be an real, false if not
     */
    public boolean isReal()
    {
        return this instanceof Real;
    }
    /**
     * Checks if the expression will evaluate to a string
     * @return true if it will be an string, false if not
     */
    public boolean isStr()
    {
        return this instanceof Str;
    }

    /**
     * Checks if the expression will evaluate to an boolean
     * @return true if it will be an boolean, false if not
     */
    public boolean isBool()
    {
        return this instanceof BoolExp;
    }

    /**
     * Compiles the expression and stores its value into $v0
     * @param em the emitter used to compile the expression
     */
    public abstract void compile(Emitter em);
}
