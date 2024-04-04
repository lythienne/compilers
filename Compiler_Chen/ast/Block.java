package ast;

import java.util.List;

import emitter.Emitter;
import environment.Environment;

/**
 * Block class is a statment that represents a list of statements in order
 * Executing the block class executes each statement in the list in order
 * @author Harrison Chen
 * @version 11/24/23
 */
public class Block extends Statement
{
    private List<Statement> statements;

    /**
     * Creates a block statement with a list of statements
     * @param statements the list of statements
     */
    public Block(List<Statement> statements)
    {
        this.statements = statements;
    }

    /**
     * Executes the block by executing each statement in the block in order
     * @param env the environment the block is in
     */
    public void exec(Environment env)
    {
        for (Statement statement : statements) 
        {
            statement.exec(env);
        }
    }

    @Override
    /**
     * Compiles the block by compiling each statement in the block in order
     * Inserts new lines between statements for readability
     * @param em the emitter used to compile the block
     */
    public void compile(Emitter em)
    {
        for (Statement statement : statements) 
        {
            statement.compile(em);
        }
    }
}
