package ast;

import java.util.List;

import emitter.Emitter;
import environment.Environment;

/**
 * Write class is a statement that represents a text output of a list of expressions
 * Executing the write will evaluate the expressions, concatenate them and then print the result
 * @author Harrison Chen
 * @version 11/24/23
 */
public class Write extends Statement
{
    private List<Expression> exps;

    /**
     * Creates a Write statement with a list of expressions
     * @param expressions the list of expressions to be printed
     */
    public Write(List<Expression> expressions)
    {
        exps = expressions;
    }

    /**
     * Executing the write will evaluate the expression and print its value, the expression
     * will either evaluate to a string, boolean, or integer so all have actual toString methods
     * @param env the environment the write is in
     */
    public void exec(Environment env)
    {
        if(exps == null)
            System.out.print("");
        else
        {
            String print = "";
            for(Expression exp : exps) print += exp.eval(env);
            System.out.print(print);
        }
    }

    @Override
    /**
     * Compiles the Write statement, first compiling the expression to write (which will store the
     * result in $v0 unless its a real), then depending on the type of the expression, will emit 
     * the correct syscall to print the result of the expression
     * @param em the Emitter to use to emit the MIPS code for a Write statement
     */
    public void compile(Emitter em)
    {
        em.emit("# Write(ln)");
        if(exps != null)
        {
            for(Expression exp : exps) 
            {
                exp.compile(em);
                if(exp.isInt())
                {
                    em.emit("move $a0 $v0");
                    em.emit("li $v0 1");
                }
                else if(exp.isBool())
                {
                    em.emit("jal pgm_boolToString");
                    em.emit("move $a0 $v0");
                    em.emit("li $v0 4");
                }
                else if(exp.isReal())
                {
                    //pray the real is in $f12
                    em.emit("li $v0 2");
                }
                else if(exp.isStr())
                {
                    em.emit("move $a0 $v0");
                    em.emit("li $v0 4");
                }
                em.emit("syscall");
            }
        }
    }
}
