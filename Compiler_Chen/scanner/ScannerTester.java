package scanner;
import java.io.*;
/**
 * The Scanner Tester takes a file as input and creates a scanner object to test the scanner class.
 * It goes through the tokens given by the scanner object and prints them all until finished
 *
 * @author Anu Datar, Harrison Chen
 * @version 08/30/2023
 */
public class ScannerTester
{
    /**
     * Main tester method 
     *
     * @param  args array of String objects 
     * @throws FileNotFoundException if the input file is not found
     * @throws CompileTimeException if scanner throws an exception
     */
    public static void main(String[] args) throws FileNotFoundException, CompileTimeException
    {
        FileInputStream in = new FileInputStream(new File("scanner/TestFiles/scannerTestAdvanced.txt"));
        Scanner scanner = new Scanner(in);
        
        Token token = scanner.nextToken();

        while(token == null || !token.getType().equals(Scanner.TOKEN_TYPE.END))
        {
            if(token != null)
                System.out.println(token);
            token = scanner.nextToken();
        }
        System.out.println("END");
    }
}
