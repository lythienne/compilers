package ast;

import java.util.ArrayList;
import java.util.List;

import emitter.Emitter;
import environment.Environment;
import scanner.CompileTimeException;

/**
 * FunctionCall is an expression that represents a call to a function of name id with arguments args
 * Evaluating the FunctionCall will return the value of the function with name id which it will find
 * in the environment given, with the given arguments in args
 * @author Harrison Chen
 * @version 10/22/23
 */
public class FunctionCall extends Expression
{
    private String id;
    private List<Expression> args;
    private String returnType;

    /**
     * Creates a FunctionCall object with an identifier name and a list of arguments
     * @param identifier the name/identifier of the function
     * @param arguments the list of expressions as arguments into the function
     */
    public FunctionCall(String identifier, List<Expression> arguments)
    {
        id = identifier;
        args = arguments;
    }

    @Override
    /**
     * Returns if the function evaluates to a boolean value
     * @return true if the function's return type is boolean, false otherwise
     */
    public boolean isBool()
    {
        return returnType.equals("BOOLEAN");
    }
    @Override
    /**
     * Returns if the function evaluates to an integer value
     * @return true if the function's return type is integer, false otherwise
     */
    public boolean isInt()
    {
        return returnType.equals("INTEGER");
    }
    @Override
    /**
     * Returns if the function evaluates to an real
     * @return true if the function's return type is float, false otherwise
     */
    public boolean isReal()
    {
        return returnType.equals("REAL");
    }
    @Override
    /**
     * Returns if the function evaluates to a string value
     * @return true if the function's return type is string, false otherwise
     */
    public boolean isStr()
    {
        return returnType.equals("STRING");
    }
    
    @Override
    /**
     * Evaluating the FunctionCall will find the FunctionDeclaration object in the environment
     * with the same name (it has the statement) and assign the parameters of the 
     * FunctionDeclaration to the arguments in args, then it will execute the FunctionDeclaration
     * statement and return the resulting value
     * @precondition the function with the same name in the environment must exist and be a function
     *               (returns a value), not a procedure
     * @param env the environment in which the function is called (NOT THE LOCAL ENVIRONMENT IN 
     *            THE FUNCTION)
     * @return the value of the function after executing the statement given the arguments
     */
    public Object eval(Environment env)
    {
        ProcedureDeclaration m = env.getMethod(id); //must be a Function
        if(!(m instanceof FunctionDeclaration)) 
            throw new CompileTimeException(id + " not a function");
        returnType = ((FunctionDeclaration) m).getReturnType();
        Environment methodEnvironment = new Environment(env);
        m.initializeVariables(methodEnvironment);
        List<Object> values = new ArrayList<Object>();
        for(Expression arg : args)
            values.add(arg.eval(env));
        for(int i=0; i<args.size(); i++)
            m.assignArgs(values.get(i), i, methodEnvironment);
        return ((FunctionDeclaration) m).execStatementAndReturn(methodEnvironment);
    }

    /**
     * Compiling the Function call will write code to evaluate all the arguments, push them to
     * the stack, and then jump and link to the correct function label to run the function. After 
     * returning from the function, it will pop off all the parameters from the stack.
     * Also updates this function's return type using the hashmap in the emitter (for type checking)
     * @param em the emitter used to write the MIPS code for this function call
     */
    public void compile(Emitter em)
    {
        returnType = em.varType(id);
        for(Expression arg : args)
        {
            arg.compile(em);
            em.push("$v0");
        }
        em.emit("jal PROC_"+id);
        for(int i = 0; i<args.size(); i++)
        {
            em.pop("$t0");
        }
    }
}