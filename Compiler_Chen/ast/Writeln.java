package ast;

import java.util.List;

import emitter.Emitter;
import environment.Environment;

/**
 * Writeln class is a statement that represents a text output of a list of expressions
 * Executing the writeln will evaluate the expressions, concatenate them and then println the result
 * @author Harrison Chen
 * @version 11/24/23
 */
public class Writeln extends Write
{
    /**
     * Creates a Writeln statement with a list of expressions
     * @param expressions the list of expressions to be printed
     */
    public Writeln(List<Expression> expressions)
    {
        super(expressions);
    }

    @Override
    /**
     * Executing the writeln will evaluate the expression and println its value, the expression
     * will either evaluate to a string, boolean, or integer so all have actual toString methods
     * @param env the environment the writeln is in
     */
    public void exec(Environment env)
    {
        super.exec(env);
        System.out.println();
    }

    @Override
    /**
     * Compiles the Writeln statement, using the write superclass to compile the expression and
     * then compile code to write the expression, and then WriteLn will write a newline
     * @param em the Emitter to use to emit the MIPS code for a Write statement
     */
    public void compile(Emitter em)
    {
        super.compile(em);
        em.emit("li $v0 4");
        em.emit("la $a0 pgm_newline");
        em.emit("syscall");
    }
}
