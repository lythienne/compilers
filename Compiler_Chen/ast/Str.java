package ast;

import emitter.Emitter;
import environment.Environment;

/**
 * Str class is an expression that just represents a string
 * Evaluating the string gives its value
 * @author Harrison Chen
 * @version 11/24/23
 */
public class Str extends Expression
{
    private final String val;
    private final int id;

    /**
     * Creates a Str with a string value
     * @param value the value
     * @param idNum the id number for this string (for mips)
     */
    public Str(String value, int idNum)
    {
        val = value;
        id = idNum;
    }

    @Override
    /**
     * Evaluating the Str gives its value
     * @param env the environment in which the string is in (unused)
     * @return the value of the Str
     */
    public Object eval(Environment env) 
    {
        return val;
    }

    @Override
    /**
     * Compiles the String, accessing its value from .data using this strings's id, then storing the
     * address to the string value into $v0
     * @param em the Emitter to use to emit the MIPS code
     */
    public void compile(Emitter em)
    {
        em.emit("la $v0 str_"+id);
    }
}
