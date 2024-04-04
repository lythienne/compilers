package ast;

import java.util.List;

import emitter.Emitter;
import environment.Environment;
import scanner.CompileTimeException;

/**
 * ProcedureDeclaration class represents a procedure or function declaration that has an identifier,
 * list of parameters, list of local variables, statement, and local environment in which the
 * variables are stored
 * Executing the ProcedureDeclaration will store the ProcedureDeclaration to the environment and
 * iniitalize the parameters and local variables.
 * 
 * ProcedureDeclaration is NOT a statement because Ms. Datar let me (my syntax is different)
 * Implements HasLocality because the local variable
 * 
 * @author Harrison Chen
 * @version 10/22/23
 */
public class ProcedureDeclaration implements HasLocality
{
    /**
     * The identifier i dont know why I have to javadoc protected variables but i just need
     * functionDeclaration to be able to see this and I dont want to write 5 getters
     */
    protected String id;
    /**
     * The list of parameters
     */
    protected List<VariableDeclaration> params;
    /**
     * the statement
     */
    protected Statement stmt;
    /**
     * the list of local variables used by the procedure
     */
    protected List<VariableDeclaration> vars;

    /**
     * Creates a ProcedureDeclaration object with an identifier, return type, list of local 
     * variable declarations, list of parameter variable declarations, and a statement
     * @param identifier the name of the procedure
     * @param variables the list of local variables to be initalized (can be empty)
     * @param parameters the list of parameters to be assigned values (can be empty)
     * @param statement the statement the procedure will execute
     */
    public ProcedureDeclaration(String identifier, List<VariableDeclaration> variables, 
            List<VariableDeclaration> parameters, Statement statement)
    {
        id = identifier;
        vars = variables;
        stmt = statement;
        params = parameters;
    }

    /**
     * Initializes local variables and parameter variables in the local environment of the method
     * @param local the local environment of the procedure
     */
    public void initializeVariables(Environment local)
    {
        for(VariableDeclaration var : vars)
            var.exec(local);
        for(VariableDeclaration param : params)
            param.exec(local);
    }

    /**
     * Assigns the parameter at an index to a value
     * @param value the value to assign to the parameter
     * @param index the index at which the parameter is at
     * @param local the local environment of the procedure
     */
    public void assignArgs(Object value, int index, Environment local)
    {
        Variable var = local.getVar(new Variable(params.get(index).getId(), null));
        local.setVar(var, value);
    }

    /**
     * Executes the ProcedureDeclaration, adding it to the environment given and adding the local 
     * variables and parameters to the local enviroment for this procedure
     * 
     * does not create a local environment, the environment is in the procedure call
     * 
     * @param env the environment to add the procedure to (NOT THE LOCAL ENVRIONMENT)
     */
    public void exec(Environment env)
    {
        env.setMethod(id, this);
    }

    /**
     * Executes the statement of this parameter with the variables in the local environment
     * @param local the local environment of the procedure
     */
    public void execStatement(Environment local)
    {
        stmt.exec(local);
    }

    @Override
    /**
     * Returns the offset of the given variable in the stack as local variables/params will be 
     * loaded onto the stack when an object with locality is compiled
     * Offset starts at 4 for the return address
     * @param varName the name of the given variable
     * @return the integer offset on the stack (not counting excess items added to the stack),
     *         will be a multiple of 4
     */
    public int getOffset(String varName)
    {
        int offset = 4;
        for(int i = vars.size()-1; i >= 0; i--)
        {
            if(vars.get(i).getId().equals(varName)) return offset;
            offset += 4;
        }
        for(int i = params.size()-1; i >= 0; i--)
        {
            if(params.get(i).getId().equals(varName)) return offset;
            offset += 4;
        }
        throw new CompileTimeException(varName + " not a local variable or parameter");
    }

    /**
     * Compiles code to execute a procedure declaration. Pushes local variables (initialized
     * to 0) for 
     * local variables and the return address, 
     * execute the statement in the procedure, and return back to the return address. Pops
     * off all values of local variables before returning to reset stack
     * @param em the emitter used to write the MIPS code for this procedure declaration
     */
    public void compile(Emitter em)
    {
        em.emit("PROC_"+id+":");
        for(int i = 0; i<vars.size(); i++) em.push("$zero");
        em.push("$ra");
        em.setProcedureContext(this);
        for(VariableDeclaration p : params) em.declareLocalVar(p.getId(), p.getType());
        for(VariableDeclaration v : vars)
        {
            String varName = v.getId();
            if(em.hasVar(varName)) throw new CompileTimeException("Local variable "
                    +varName+" can't be declared because it is already a parameter");
            em.declareLocalVar(varName, v.getType());
        }
        stmt.compile(em);
        em.clearProcedureContext();
        em.pop("$ra");
        for(int i = 0; i<vars.size(); i++) em.pop("$t0");
        em.emit("jr $ra");
    }
}
