package ast;

import emitter.Emitter;
import environment.Environment;
import scanner.CompileTimeException;

/**
 * Condition class is a boolean expression that represents a relative operator between two 
 * expressions
 * Evaluating the condition will evaluate each expression and then compare the two expressions
 * with the relative operator
 * @author Harrison Chen
 * @version 11/25/23
 */
public class Condition extends BoolExp
{
    private String relop;
    private Expression e1;
    private Expression e2;

    /**
     * Creates a condition with a relative operator and two expressions to compare
     * @param relativeOperator the relative operator
     * @param exp1 the first expression
     * @param exp2 the second expression
     */
    public Condition(String relativeOperator, Expression exp1, Expression exp2)
    {
        relop = relativeOperator;
        e1 = exp1;
        e2 = exp2;
    }

    @Override
    /**
     * Evaluates the condition by checking the types of the expressions to see if they match,
     * then evaluating them, and finally using the operator on them and returning the result.
     * @precondition the expressions should match in type and the operator should be one that
     *               matches the expressions (you can't compare booleans with < for example)
     * @param env the environment (unused)
     * @return the boolean result of the comparison of the two expressions
     * 
     * DOES NOT WORK WITH REALS WILL FIX MAYBE SOMETIME
     */
    public Boolean eval(Environment env)
    {
        Object eval1 = e1.eval(env);
        Object eval2 = e2.eval(env);
        switch(relop)
        {
            case "=": return eval1.equals(eval2);
            case "<>": return !eval1.equals(eval2);
        }
        if(e1.isInt() && e2.isInt())
        {
            Integer val1 = (Integer) eval1;
            Integer val2 = (Integer) eval2;
            switch(relop)
            {
                case "<": return val1 < val2;
                case ">": return val1 > val2;
                case "<=": return val1 <= val2;
                case ">=": return val1 >= val2;
                default: throw new IllegalArgumentException("Operator "+relop+" is not <,>,<=,>=");
            }
        }
        if(e1.isStr() && e2.isStr())
        {
            String val1 = (String) eval1;
            String val2 = (String) eval2;
            switch(relop)
            {
                case "<": return val1.compareTo(val2) < 0;
                case ">": return val1.compareTo(val2) > 0;
                case "<=": return val1.compareTo(val2) <= 0;
                case ">=": return val1.compareTo(val2) >= 0;
                default: throw new IllegalArgumentException("Operator "+relop+" is not <,>,<=,>=");
            }
        }
        else
        {
            throw new IllegalArgumentException(eval1 + " is not the same type as " +eval2);
        }
    }
    
    @Override
    /**
     * Compiles the Condition, by first compiling the two expressions (which will have their values
     * stored in $v0, then on the stack), and then depending on the type of the expressions and the
     * relative operator, Condition will emit MIPS assembly code that compares with the correct
     * operator and stores the boolean result in $v0 (0=false, -1=true)
     * @param em the emitter used to compile the Condition
     * 
     * DOES NOT WORK WITH REALS WILL FIX MAYBE SOMETIME
     */
    public void compile(Emitter em)
    {
        int id = em.nextLabelID();

        e1.compile(em);
        em.push("$v0");
        e2.compile(em);
        em.pop("$t0"); //e1 val in $t0, e2 val in $v0
        em.emit("# Condition");
        String opString;
        if(e1.isInt() && e2.isInt() || e1.isBool() && e2.isBool())
        {
            switch(relop)
            {
                case "=": opString = "beq $t0 $v0 true"+id+"\t#equals"; break;
                case "<>": opString = "bne $t0 $v0 true"+id+"\t#not equals"; break;
                default:
                    if(e1.isInt() && e2.isInt())
                    {
                        switch(relop)
                        {
                            case "<": opString = "blt $t0 $v0 true"+id+"\t#less than"; break;
                            case ">": opString = "bgt $t0 $v0 true"+id+"\t#greater than"; break;
                            case "<=": opString = "ble $t0 $v0 true"+id+"\t#less/equal"; break;
                            case ">=": opString = "bge $t0 $v0 true"+id+"\t#greater/equal"; break;
                            default: throw new CompileTimeException("Expressions are numbers"+
                                "but operator "+relop+" is not <,>,<=,>=");
                        }
                    }
                    else
                    {
                        throw new CompileTimeException("Expressions are booleans but relop "+
                                                        relop+" is not =,<>");
                    }
            }
            em.emit(opString);
        }
        else if(e1.isStr() && e2.isStr())
        {
            em.emit("compareStrLoop"+id+":");
            em.emit("lb $t1 ($t0)");            //str 1 chars in $t1
            em.emit("lb $t2 ($v0)");            //str 2 chars in $t2
            switch(relop)
            {
                case "=":
                    em.emit("bne $t1 $t2 false"+id); //char1 != char2 then str1 != str2
                    em.emit("beqz $t1 true"+id);     //char1 = char2 = null then str1 = str2
                    break;
                case "<>":
                    em.emit("bne $t1 $t2 true"+id);  //char1 != char2 then str1 != str2
                    em.emit("beqz $t1 false"+id);    //char1 = char2 = null then str1 = str2
                    break;
                case "<":
                    em.emit("bgt $t1 $t2 false"+id); //char1 > char2 then str1 > str2
                    em.emit("blt $t1 $t2 true"+id);  //char1 < char2 then str1 < str2
                    em.emit("beqz $t1 false"+id);    //char1 = char2 = null then str1 = str2
                    break;
                case ">":
                    em.emit("bgt $t1 $t2 true"+id);  //char1 > char2 then str1 > str2
                    em.emit("blt $t1 $t2 false"+id); //char1 < char2 then str1 < str2
                    em.emit("beqz $t1 false"+id);    //char1 = char2 = null then str1 = str2
                    break;
                case "<=":
                    em.emit("bgt $t1 $t2 false"+id); //char1 > char2 then str1 > str2
                    em.emit("blt $t1 $t2 true"+id);  //char1 < char2 then str1 < str2
                    em.emit("beqz $t1 true"+id);     //char1 = char2 = null then str1 = str2
                    break;
                case ">=":
                    em.emit("bgt $t1 $t2 true"+id);  //char1 > char2 then str1 > str2
                    em.emit("blt $t1 $t2 false"+id); //char1 < char2 then str1 < str2
                    em.emit("beqz $t1 true"+id);     //char1 = char2 = null then str1 = str2
                    break;
                default: throw new IllegalArgumentException("Operator "+relop+" is not <,>,<=,>=");
            }
            em.emit("addi $t0 $t0 1");
            em.emit("addi $v0 $v0 1");
            em.emit("j compareStrLoop"+id);
        }
        else
        {
            throw new CompileTimeException("expressions do not match in type");
        }
        em.emit("false"+id+":");
        em.emit("li $v0 0");
        em.emit("j after"+id);

        em.emit("true"+id+":");
        em.emit("li $v0 -1");
        em.emit("after"+id+":");
    }
}