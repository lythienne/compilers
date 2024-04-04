package scanner;

/**
 * CompileTimeException is a sub class of Exception and is thrown to indicate a 
 * compiling error.  Usually, the scanning error is the result of an illegal 
 * character in the input stream.  The error is also thrown when the expected
 * value of the character stream does not match the actual value.
 * @author Mr. Page
 * @version 11/24/23
 *
 */
public class CompileTimeException extends RuntimeException
{
    /**
     * default constructor for ScanErrorObjects
     */
    public CompileTimeException()
    {
        super();
    }
    /**
     * Constructor for ScanErrorObjects that includes a reason for the error
     * @param reason the reason the CompileTimeException was thrown
     */
    public CompileTimeException(String reason)
    {
        super(reason);
    }
}
