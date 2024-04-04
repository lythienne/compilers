package ast;

import emitter.Emitter;
import environment.Environment;

/**
 * ConstDeclaration represents a constant declaration with the name, a type, and a value.
 * This is very similar to a variable except that it represents a variable that *will* be declared
 * with a value (but hasn't been yet). Executing the constant declaration will actually initialize
 * the variable with its type and a its value in the environment it is supposed to be in (for 
 * locality).
 * @author Harrison Chen
 * @version 10/25/23
 */
public class ConstDeclaration
{
    private String id;
    private Expression val;

    /**
     * Creates a ConstDeclaration with an identifier, value, and type
     * @param identifier the name of the constant to be declared
     * @param value the expression of the value of the constant
     */
    public ConstDeclaration(String identifier, Expression value)
    {
        id = identifier;
        val = value;
    }

    /**
     * Executes the ConstDeclaration, declaring the variable in the given environment with a value
     * @param env the environment to declare the variable in
     */
    public void exec(Environment env)
    {
        Object value = val.eval(env);
        String type = "STRING";
        if(value instanceof Integer) type = "INTEGER";
        else if(value instanceof Float) type = "DOUBLE";
        else if(value instanceof Boolean) type = "BOOLEAN";
        env.declareVar(new Variable(id, type), val.eval(env));
    }

    /**
     * I didnt have enough time to do this :(
     * @param em hi
     */
    public void compile(Emitter em)
    {
        throw new RuntimeException("Implement me!!!!!");
    }
}
