package ast;

import emitter.Emitter;
import environment.Environment;

/**
 * Real class is an expression that just represents a real number
 * Evaluating the number gives its value
 * @author Harrison Chen
 * @version 11/24/23
 */
public class Real extends Expression
{
    private final float val;
    private final int id;

    /**
     * Creates a Real with a float value
     * @param value the value
     * @param idNum the id number for this real (for mips)
     */
    public Real(float value, int idNum)
    {
        val = value;
        id = idNum;
    }

    @Override
    /**
     * Evaluating the Real gives its value
     * @param env the environment in which the number is in (unused)
     * @return the value of the Real
     */
    public Float eval(Environment env)
    {
        return val;
    }

    @Override
    /**
     * Compiles the Real, accessing its value from .data using this real's id, then storing the
     * (single precision) float value into $f12
     * @param em the Emitter to use to emit the MIPS code
     */
    public void compile(Emitter em)
    {
        em.emit("l.s $f12 real_"+id);
    }
}
