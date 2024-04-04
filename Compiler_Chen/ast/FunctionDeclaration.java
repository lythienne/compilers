package ast;

import java.util.List;

import emitter.Emitter;
import environment.Environment;
import scanner.CompileTimeException;

/**
 * FunctionDeclaration is a ProcedureDeclaration that also has a return type and can be executed to
 * return a value
 * @author Harrison Chen
 * @version 10/22/23
 */
public class FunctionDeclaration extends ProcedureDeclaration
{
    private String returnType;

    /**
     * Creates a FunctionDeclaration with an identifier, return type, list of local variable 
     * declarations, list of parameter variable declarations, and a statement
     * @param identifier the name of the function
     * @param type the return type of the function
     * @param variables the list of local variables to be initalized (can be empty)
     * @param parameters the list of parameters to be assigned values (can be empty)
     * @param statement the statement the function will execute
     */
    public FunctionDeclaration(String identifier, String type, List<VariableDeclaration> variables, 
            List<VariableDeclaration> parameters, Statement statement)
    {
        super(identifier, variables, parameters, statement);
        returnType = type;
    }

    /**
     * Returns the return type of this function
     * @return returnType
     */
    public String getReturnType() 
    {
        return returnType;
    }

    /**
     * Executes the statement using the local environment which will have the local variables and 
     * values of the arguments and returns the value stored in the local environment with the name
     * of the function
     * @param local the local environment of the function
     * @return the value of the variable in the local environment with name of the function (the
     *         return value)
     */
    public Object execStatementAndReturn(Environment local)
    {
        local.declareVar(new Variable(id, returnType), null);
        execStatement(local);
        return local.getVal(new Variable(id, returnType));
    }

    @Override
    /**
     * Returns the offset of the given variable in the stack as local variables/params will be 
     * loaded onto the stack when an object with locality is compiled. If the variable to get
     * is the name of the function the return value will be on top of the stack, otherwise adds
     * 4 to the offset for the return value
     * @param varName the name of the given variable
     * @return the integer offset on the stack (not counting excess items added to the stack),
     *         will be a multiple of 4
     */
    public int getOffset(String varName)
    {
        if(varName.equals(id)) return 0;
        return super.getOffset(varName) + 4;
    }

    /**
     * Adds this function to the emitter's hashmap of functions and their return types
     * @param em the Emitter with the hashmap to add this function to
     */
    public void addFunctionToEmitter(Emitter em)
    {
        em.declareVar(id, returnType);
    }

    /**
     * Compiles a function declaration, creating a label with the name of the function and
     * compiling code to push initial values of 0 for local variables, the return value, and
     * an initial value of 0 for the return value. Then de
     * execute the statement in the procedure, and return back to the return address. Pops
     * off all values of local variables before returning to reset stack
     * @param em the emitter used to write the MIPS code for this procedure declaration
     */
    public void compile(Emitter em)
    {
        em.emit("PROC_"+id+":");
        for(int i = 0; i<vars.size(); i++) em.push("$zero");
        em.push("$ra");     //push return address to stack
        em.push("$zero");   //push return value to stack
        em.setProcedureContext(this);
        em.declareLocalVar(id, returnType);
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
        em.pop("$v0");      //pop return value to v0
        em.pop("$ra");      //pop return address to ra
        for(int i = 0; i<vars.size(); i++) em.pop("$t0");    //take out all local vars
        em.emit("jr $ra");
    }
}
