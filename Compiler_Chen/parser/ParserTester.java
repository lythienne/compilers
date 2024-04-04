package parser;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import scanner.Scanner;

/**
 * The Parser Tester takes a file as input and creates a parser object to test the parser class.
 * It goes through the tokens given by the scanner object and parses them while getting input
 * for READLN and printing WRITELN output
 *
 * @author Harrison Chen
 * @version 10/3/2023
 */
public class ParserTester 
{
    /**
     * Runs the parser on the file parserTest.txt
     * @param args 
     * @throws FileNotFoundException if the file to be parsed does not exist
     */
    public static void main(String[] args) throws FileNotFoundException
    {
        String[] tstFiles = {"Test","ScopeTest","RecurTest","parserTest7","parserTest8",
                "parserTest85"};
        String[] programs = {"PASCALCalc","PerfectSquare","Primes","TempConvert","TempConvertInt",
                "FibonacciRecursive", "Towers", "PowersOf2", "PrintSquares"};
        FileInputStream in = new FileInputStream(new File("parser/TestFiles/"+programs[8]+".txt"));
        Scanner s = new Scanner(in);
        Parser p = new Parser(s);
        p.parseProgram().run();
    }
}