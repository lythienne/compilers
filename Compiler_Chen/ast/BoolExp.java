package ast;

import environment.Environment;

/**
 * BoolExp is an expression that represents an abstract expression that
 * evaluates to return a boolean.
 * @author Harrison Chen
 * @version 10/14/23
 */
public abstract class BoolExp extends Expression
{
    /**
     * Evaluating a boolean expression will return a boolean
     * @param env the environment the boolean expression will be in
     * @return a boolean value
     */
    public abstract Boolean eval(Environment env);
}
