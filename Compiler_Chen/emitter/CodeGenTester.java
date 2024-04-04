package emitter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import parser.Parser;
import scanner.Scanner;

/**
 * CodeGenTester is a tester for the MIPS Code Generation part of the lab
 * Has two string arrays of test files and programs to try, outputs MIPS code to test.txt,
 * use MARS simulator to test
 *
 * @author Harrison Chen
 * @version 11/9/2023
 */
public class CodeGenTester 
{
    /**
     * 
     * @param args 
     * @throws FileNotFoundException if the file to be parsed does not exist
     */
    public static void main(String[] args) throws FileNotFoundException
    {
        String[] tstFiles = {"Test","FunctionTest","RecurTest"};
        String[] programs = {"PowersOf2","PASCALCalc","FibonacciRecursive","TempConvertInt",
                "Towers"};
        FileInputStream in = new FileInputStream(new File("emitter/TestFiles/"+tstFiles[0]+".txt"));
        Scanner s = new Scanner(in);
        Parser p = new Parser(s);
        Emitter em = new Emitter("test.asm");
        p.parseProgram().compile(em);
    }
}