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
     * @throws ScanErrorException if scanner throws an exception
     */
    public static void main(String[] args) throws FileNotFoundException, ScanErrorException, IOException
    {
        FileReader in = new FileReader(new File("./TestFiles/essay.txt"));
        Scanner scanner = new Scanner(in);

        String token = scanner.nextToken();

        while(!scanner.yyatEof())
        {
            System.out.println(token);
            token = scanner.nextToken();
        }
        System.out.println("END");
    }
}