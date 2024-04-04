package ast;

/**
 * Local subroutine interface forces classes that implement it to have methods that have to
 * do with retrieving local variables from the stack. In this lab, For and ProcedureDeclaration
 * will implement it.
 * @author Harrison Chen
 * @version 12/7/23
 */
public interface HasLocality
{
    /**
     * Returns the offset of the given variable in the stack as local variables/params will be 
     * loaded onto the stack when an object with locality is compiled
     * @param varName the name of the given variable
     * @return the integer offset on the stack (not counting excess items added to the stack),
     *         will be a multiple of 4
     */
    public abstract int getOffset(String varName);
}
