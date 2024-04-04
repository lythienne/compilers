package ast;

import emitter.Emitter;
import environment.Environment;
import scanner.CompileTimeException;

/**
 * Variable class is an expression that represents a PASCAL variable with a identifier and a type
 * Evaluating the variable gets its value from the environment map based on its identifier
 * @author Harrison Chen
 * @version 11/24/23
 */
public class Variable extends Expression
{
    private String id;
    private String type; //integer, real, boolean, string

    /**
     * Creates a variable expression with an identifier and and a type
     * @param identifier the identifier
     * @param varType the type (integer, boolean, string)
     */
    public Variable(String identifier, String varType)
    {
        id = identifier;
        type = varType;
    }

    @Override
    /**`    
     * Evaluating the variable gets the value of the variable from the environment map with the
     * identifier of this variable and returns it
     * @param env the environment the variable is in
     * @return the value of this variable from the current environment
     */
    public Object eval(Environment env)
    {
        Object value = env.getVal(this);
        if(type == null)
        {
            if(value instanceof Integer) type = "INTEGER";
            else if(value instanceof Boolean) type = "BOOLEAN";
            else if(value instanceof String) type = "STRING";
            else if(value instanceof Float) type = "REAL";
            else throw new CompileTimeException(id+" value "+value
                    +" isnt a boolean/integer/string/real");
        }
        return value;
    }

    @Override
    /**
     * Checks if this variable is a number by checking its type or checking the type of its value
     * in the map
     * @return true if the variable is a number, false if not
     */
    public boolean isInt()
    {
        return type.equals("INTEGER");
    }
    @Override
    /**
     * Checks if this variable is a number by checking its type or checking the type of its value
     * in the map
     * @return true if the variable is a number, false if not
     */
    public boolean isReal()
    {
        return type.equals("REAL");
    }
    @Override
    /**
     * Checks if this variable is a boolean by checking its type or checking the type of its value
     * in the map
     * @return true if the variable is a boolean, false if not
     */
    public boolean isBool()
    {
        return type.equals("BOOLEAN");
    }
    @Override
    /**
     * Checks if this variable is a string by checking its type or checking the type of its value
     * in the map
     * @return true if the variable is a string, false if not
     */
    public boolean isStr()
    {
        return type.equals("STRING");
    }

    @Override
    /**
     * Returns the string representation of this object, its identifier
     * @return id
     */
    public String toString()
    {
        return id;
    }

    @Override
    /**
     * Checks if this variable is equal to another by comparing their identifiers (used by the 
     * environment map)
     * @precondition the other variable must be a variable
     * @param other the other variable to be compared to
     * @return true if their identifiers are equal, false otherwise
     */
    public boolean equals(Object other)
    {
        if(other instanceof Variable)
            return this.id.equals(((Variable) other).id);
        else
            throw new IllegalArgumentException("Passed object is not a Variable");
    }
    @Override
    /**
     * Returns the hashcode of the identifier (used by the environment map)
     * @return the hashcode of the identifier
     */
    public int hashCode()
    {
        return id.hashCode();
    }

    @Override
    /**
     * Compiles the Variable, accessing its value from .data using this variable's id or from the
     * stack if this is a local variable, then storing the value into $v0
     * @param em the Emitter to use to emit the MIPS code
     */
    public void compile(Emitter em)
    {
        type = em.varType(id);
        em.loadVar(id, type);
    }
}
