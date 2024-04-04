package ast;

import emitter.Emitter;
import environment.Environment;

/**
 * Statement class represents an abstract PASCAL statement that can be executed
 * @author Harrison Chen
 * @version 11/24/23
 */
public abstract class Statement 
{
    /**
     * Executing the statement will do what the statement does (yup, im so smart)
     * @param env the environment the statement is in
     */
    public abstract void exec(Environment env);

    /**
     * Compiling the statement writes MIPS assembly code that executes the statement when run
     * @param em the Emitter used to write the MIPS code fot this statement to a file
     */
    public abstract void compile(Emitter em);
}
