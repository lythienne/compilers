package ast;

import emitter.Emitter;
import environment.Environment;

/**
 * VariableDeclaration represents a variable declaration with the name of a variable and a type.
 * This is very similar to a variable except that it represents a variable that *will* be declared
 * (but hasn't been yet). Executing the variable declaration will actually initialize the variable
 * with its type and a null value in the environment it is supposed to be in (for locality).
 * @author Harrison Chen
 * @version 11/24/23
 */
public class VariableDeclaration
{
    private String id;
    private String type;

    /**
     * Creates a VariableDeclaration object with the identifier and type of the variable
     * @param identifier the identifier of the variable
     * @param variableType the type of the variable
     */
    public VariableDeclaration(String identifier, String variableType)
    {
        id = identifier;
        type = variableType;
    }

    /**
     * Executing the VariableDeclaration will initialize the variable in the correct environment 
     * (scope) with a value of null, the variable will need to be assigned later to be used
     * @param env the environment in which the variable will be used
     */
    public void exec(Environment env)
    {
        env.declareVar(new Variable(id, type), null);
    }

    /**
     * Returns the identifier of the variable that will be initialized
     * @return id
     */
    public String getId()
    {
        return id;
    }

    /**
     * Returns the type of the variable that will be initialized
     * @return type
     */
    public String getType()
    {
        return type;
    }

    /**
     * Compiling the VariableDeclaration will write code to declare a variable in the .data section
     * of MIPS, the variable will be named type_id and will be a .word (for number value, or 0/-1
     * for boolean, or the address to the string in memory)
     * @param em the Emitter used to write the variable declaration MIPS code to a file
     */
    public void compile(Emitter em)
    {
        em.declareVar(id, type);
        switch(type)
        {
            case "INTEGER", "BOOLEAN", "STRING": em.emit(type+"_"+id+": .word 0"); break;
            case "REAL": em.emit(type+"_"+id+": .float 0.0"); break;
        }
    }
}
