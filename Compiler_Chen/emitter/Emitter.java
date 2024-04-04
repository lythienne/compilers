package emitter;
import java.io.*;
import java.util.HashMap;

import ast.HasLocality;
import ast.For;
import scanner.CompileTimeException;

/**
 * Emitter class is used to write MIPS32 Assembly code to a file to be run by the MARS simulator
 * Emitter keeps track of a map of all the (global) variables and their types used by the program,
 * and a count for the number of statements that are using labels (so that multiple of the same
 * statement dont run into each other, using the same label and breaking)
 * 
 * @author Harrison Chen
 * @version 11/26/23
 */
public class Emitter
{
    private PrintWriter out;
    private HashMap<String, String> variables;
    private HashMap<String, String> local;
    private int labelCount;
    private HasLocality procedureContext;
    private HasLocality higherContext; //when for is in a procedure declaration
    private int excessStackCount;

	/**
     * Creates an emitter for writing to a new file with given name
     * Initializes the label count to 0 and the variable map to be empty
     * @param outputFileName the name of the file where the MIPS code should be outputted
     */
    public Emitter(String outputFileName)
    {
        try
        {
            out = new PrintWriter(new FileWriter(outputFileName), true);
        }
        catch(IOException e)
        {
            throw new RuntimeException(e);
        }
        variables = new HashMap<String,String>();
        labelCount = 0;
        procedureContext = null;
        higherContext = null;
    }

    /**
     * Updates the procedure context (current procedure being compiled)
     * @param procedure the procedure being compiled
     */
    public void setProcedureContext(HasLocality procedure)
    {
        if(procedureContext == null || !(procedure instanceof For))
        {
            procedureContext = procedure;
            local = new HashMap<String, String>();
            excessStackCount = 0;
        }
        else
        {
            higherContext = procedureContext;
            procedureContext = procedure;
        }
    }

    /**
     * Clears the procedure context (current procedure being compiled)
     * sets it to null
     * @return the excessStackCount currently on the stack
     */
    public void clearProcedureContext()
    {
        procedureContext = null;
        higherContext = null;
        local = null;
    }

    /**
     * OVERLOADED, ONLY CALLED FROM FOR LOOPS
     * Removes the current for loop's index variable from the local 
     * @return the excessStackCount currently on the stack
     */
    public void clearProcedureContext(String currentIndexVariable)
    {
        local.remove(currentIndexVariable);
        if(local.isEmpty() && higherContext == null)
        {
            procedureContext = null;
            local = null;
        }
        else
        {
            procedureContext = higherContext;
            higherContext = null;
        }
    }

    /**
     * Returns the next label id so that labels don't have the same names
     * @return the labelCount after incrementing it
     */
    public int nextLabelID() 
    {
        labelCount++;
        return labelCount;
    }

	/**
     * Prints one line of code to file (with non-labels indented)
     * @param code the line of code to print
     */
    public void emit(String code)
    {
        if (!code.endsWith(":"))
            code = "\t" + code;
        out.println(code);
    }

    /**
     * Checks to see if the variable is declared in the context it is called in
     * @param id the identifier (name) of the variable
     * @return true if the variable is declared, false if not
     */
    public boolean hasVar(String id)
    {
        if(procedureContext != null)
            return local.containsKey(id);
        return variables.containsKey(id);
    }

    /**
     * Puts the variable/type pair in the variables map so its type can be referenced later
     * @param id the identifier (name) of the variable
     * @param type the type of the variable
     */
    public String declareVar(String id, String type)
    {
        return variables.put(id, type);
    }

    /**
     * Puts the variable/type pair in the local map so its type can be referenced later
     * @param id the identifier (name) of the variable
     * @param type the type of the variable
     */
    public void declareLocalVar(String id, String type)
    {
        if(local == null) 
            throw new CompileTimeException("Can't declare local variable while not in a local context");
        local.put(id, type);
    }

    /**
     * Returns the type of a variable based on its id
     * @param id the id of the variable
     * @return the type of the variable (stored in the map)
     */
    public String varType(String id)
    {
        if(procedureContext != null && local.containsKey(id))
            return local.get(id);
        if(!variables.containsKey(id)) 
            throw new CompileTimeException("Variable "+id+" not declared");
        return variables.get(id);
    }

    /**
     * Emits code to store the value of a register into a variable
     * @param id the name of the variable
     * @param register the register containing the value of an integer/boolean/real, or the
     *                 address of a string
     */
    public void storeVar(String id, String register)
    {
        if(procedureContext != null && local.containsKey(id))
        {
            int offset = procedureContext.getOffset(id) + excessStackCount;
            emit("sw "+register+" "+offset+"($sp)");
        }
        else
        {
            String type = varType(id);
            switch(type)
            {
                case "INTEGER","BOOLEAN", "STRING": emit("sw "+register+" "+type+"_"+id); break;
                case "REAL": emit("s.s "+register+" "+type+"_"+id); break; //coproc register
            }
        }
    }

    /**
     * Gets the variable's value from .data using this variable's id or from the
     * stack if this is a local variable, then stores the value into $v0
     * @param id the identifier for the variable
     */
    public void loadVar(String id, String type)
    {
        if(procedureContext != null && local.containsKey(id))
        {
            int offset = procedureContext.getOffset(id) + excessStackCount;
            emit("lw $v0 "+offset+"($sp)");
        }
        else
        {
            emit("la $t0 "+type+"_"+id);
            switch(type)
            {
                case "INTEGER", "BOOLEAN", "STRING": emit("lw $v0 ($t0)"); break;
                case "REAL": emit("l.s $f12 ($t0)"); break;
            }
        }
        
    }

    /**
     * Moves down stack, then pushes word from register onto the new bottom of the stack
     * @param reg the word to push onto the stack
     */
    public void push(String reg)
    {
        emit("subi $sp $sp 4");
        emit("sw "+reg+" ($sp)\t#push "+reg+" onto stack");
        excessStackCount += 4;
    }
    
    /**
     * Stores word at the bottom of the stack into the register, then pops the bottom of the stack
     * @param reg the word to push onto the stack
     */
    public void pop(String reg)
    {
        emit("lw "+reg+" ($sp)");
        emit("addi $sp $sp 4\t#pop from stack to "+reg);
        excessStackCount -= 4;
    }

    /**
     * Moves down stack, then pushes word from register onto the new bottom of the stack
     * @precondition register needs to be a coprocessor (floating point unit) register
     * @param reg the word to push onto the stack
     */
    public void pushReal(String reg)
    {
        emit("subi $sp $sp 4");
        emit("s.s "+reg+" ($sp)\t#push "+reg+" onto stack");
        excessStackCount += 4;
    }
    
    /**
     * Stores word at the bottom of the stack into the register, then pops the bottom of the stack
     * @precondition register needs to be a coprocessor (floating point unit) register
     * @param reg the word to push onto the stack
     */
    public void popReal(String reg)
    {
        emit("l.s "+reg+" ($sp)");
        emit("addi $sp $sp 4\t#pop from stack to "+reg);
        excessStackCount -= 4;
    }

    /**
     * Emits code that iterates through a string stored in a register executing some code each time
     * @param code the code to execute
     * @param labelName the name of the labels to be used
     * @param register the register of the address of the string
     */
    public void iterateThroughString(String code, String labelName, String register)
    {
        emit(labelName+":");
        emit(
                "lb $t0 ("+register+")\n"+
            labelName+"Loop:\n"+
                "\tbeqz $t0 "+labelName+"After\n\t"+
                code+"\n\t"+
                "addi "+register+" "+register+" 1\n\t"+
                "lb $t0 ("+register+")\n\t"+
                "j "+labelName+"Loop\n"+
            labelName+"After:\n\t"
        );
    }

    /**
     * Emits code to print an error message and then exit the program
     * @param errorString the .asciiz where the error message is stored
     */
    public void throwError(String errorString)
    {
        emit("la $a0 "+errorString);
        emit("li $v0 4");
        emit("syscall");

        emit("li $v0 10");
        emit("syscall");
    }

	/**
     * Closes the file.  should be called after all calls to emit.
     */
    public void close()
    {
        out.close();
    }
}