package ast;

import environment.Environment;

import java.util.ArrayList;
import java.util.List;

import emitter.Emitter;

/**
 * ProcedureCall is an statement that represents a call to a procedure OR FUNCTION of name id
 * with arguments args.
 * Evaluating the ProcedureCall will execute the statement of procedure or function in the 
 * environment given, with the given arguments in args
 * @author Harrison Chen
 * @version 10/22/23
 */
public class ProcedureCall extends Statement
{
    private String id;
    private List<Expression> args;

    /**
     * Creates a ProcedureCall object with an identifier name and a list of arguments
     * @param identifier the name/identifier of the procedure/function
     * @param arguments the list of expressions as arguments into the procedure/function
     */
    public ProcedureCall(String identifier, List<Expression> arguments)
    {
        id = identifier;
        args = arguments;
    }
    
    /**
     * Evaluating the ProcedureCall will find the ProcedureDeclaration object (remember this can
     * be a FunctionDeclaration) in the environment with the same identifier as this object's id.
     * Then it will assign the parameters of the ProcedureDeclaration to the arguments in args, 
     * and execute the FunctionDeclaration statement
     * @precondition the procedure/function with the same name in the environment must exist
     * @param env the environment in which the function is called
     */
    public void exec(Environment env)
    {
        ProcedureDeclaration method = env.getMethod(id);
        Environment methodEnvironment = new Environment(env);
        method.initializeVariables(methodEnvironment);
        List<Object> values = new ArrayList<Object>();
        for(Expression arg : args)
            values.add(arg.eval(env));
        for(int i=0; i<args.size(); i++)
            method.assignArgs(values.get(i), i, methodEnvironment);
        method.execStatement(methodEnvironment);
    }

    /**
     * Compiling the Procedure call will write code to evaluate all the arguments, push them to
     * the stack, and then jump and link to the correct procedure label to run the procedure.
     * After returning from the procedure, it will pop off all the parameters from the stack.
     * @param em the emitter used to write the MIPS code for this procedure call
     */
    public void compile(Emitter em)
    {
        for(Expression arg : args)
        {
            arg.compile(em);
            em.push("$v0");
        }
        em.emit("jal PROC_"+id);
        for(int i = 0; i<args.size(); i++)
        {
            em.pop("$v0");
        }
    }
}
