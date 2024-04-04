package environment;

import java.util.HashMap;
import java.util.Map;

import ast.Variable;
import scanner.CompileTimeException;
import ast.ProcedureDeclaration;

/**
 * Environment class is (for now) a global environment with a map to store the values of variables
 * The environment class will be passed into the run time methods that need it (eval, exec, isType)
 * @author Harrison Chen
 * @version 10/14/23
 */
public class Environment 
{
    private Map<Variable, Object> vars;
    private Map<String, ProcedureDeclaration> methods;
    private Environment parent;

    /**
     * Creates an environment, initializing its HashMap for variables and values
     * @param parentEnvironment the parent environment for this environment, will be the scope
     *                          outside this environment's scope which is usually the global
     *                          environment, or null for the global environment
     */
    public Environment(Environment parentEnvironment)
    {
        vars = new HashMap<Variable, Object>();
        methods = new HashMap<String, ProcedureDeclaration>();
        parent = parentEnvironment;
    }

    /**
     * Puts a variable in vars with a value, initializing it with a new value
     * @precondition variable should not be declared in this environment yet
     * @param var the variable that have its value changed
     * @param val the new value associated with the variable
     */
    public void declareVar(Variable var, Object val)
    {
        if(vars.containsKey(var))
            throw new CompileTimeException("variable "+var+" already declared");
        vars.put(var, val);
    }

    /**
     * Puts a variable in the correct map with a value replacing the value of the variable, checks
     * the current environment for the variable first, if it doesn't exist then checks the parent
     * @precondition the variable should exist in this environment or a parent environment
     * @param var the variable that have its value changed
     * @param val the new value associated with the variable
     */
    public void setVar(Variable var, Object val)
    {  
        Environment toPutVarIn = this;
        while(!toPutVarIn.vars.containsKey(var) && toPutVarIn.parent != null) 
            toPutVarIn = toPutVarIn.parent;
        if(!toPutVarIn.vars.containsKey(var))
            throw new CompileTimeException(var+" not declared in local/global environment(s)");
        if(toPutVarIn.vars.containsKey(var) && val != null)
        {
            if(var.isInt() && !(val instanceof Integer))
                throw new CompileTimeException(val+" not an integer but variable "+var+" is");
            else if(var.isBool() && !(val instanceof Boolean))
                throw new CompileTimeException(val+" not an boolean but variable "+var+" is");
            else if(var.isStr() && !(val instanceof String))
                throw new CompileTimeException(val+" not an string but variable "+var+" is");
            else if(var.isReal() && !(val instanceof Float))
                throw new CompileTimeException(val+" not an real but variable "+var+" is");
        }
        toPutVarIn.vars.put(var, val);
    }

    /**
     * Gets the variable with the same name as a variable in the correct map, checks the current
     * environment for the variable first, if it doesn't exist then checks the parent
     * @precondition the variable should exist in this environment or a parent environment
     * @param var the variable with the same name as the returned variable
     * @return the variable stored in the variable map with the same name as var
     */
    public Variable getVar(Variable var)
    {
        Environment toGetVarFrom = this;
        while(!toGetVarFrom.vars.containsKey(var) && toGetVarFrom.parent != null) 
            toGetVarFrom = toGetVarFrom.parent;
        for(Variable v : toGetVarFrom.vars.keySet()) if(v.equals(var)) return v;
        throw new CompileTimeException("Variable "+var+" not declared");
    }

    /**
     * Gets the value of a variable in the correct map, checks the current environment for the 
     * variable first, if it doesn't exist then checks the parent
     * @precondition the variable should exist in this environment or a parent environment
     * @param var the variable to get the value of
     * @return the value of the variable stored in the variable map
     */
    public Object getVal(Variable var)
    {
        Environment toGetValFrom = this;
        while(!toGetValFrom.vars.containsKey(var) && toGetValFrom.parent != null) 
            toGetValFrom = toGetValFrom.parent;
        if(!toGetValFrom.vars.containsKey(var)) 
            throw new CompileTimeException("Variable "+var+" not declared");
        return toGetValFrom.vars.get(var);
    }

    /**
     * Puts a method in the global envrionment's methods map with a method declaration 
     * (procedure or function) by either creating a new space if the method name does 
     * not yet exist in the map or replacing the Method of the method if it already exists
     * @param id identifier of the method that will be put in the map or have its value changed
     * @param method the new Method associated with the identifier
     */
    public void setMethod(String id, ProcedureDeclaration method)
    {
        if(parent == null)
            methods.put(id, method);
        else
            parent.setMethod(id, method);
    }

    /**
     * Gets the method declaration of a method with the name id from the global environment's 
     * methods map
     * @param id the name of the method to get
     * @return the Method with the name id
     */
    public ProcedureDeclaration getMethod(String id)
    {
        if(parent == null)
            return methods.get(id);
        else
            return parent.getMethod(id);
    }
}