package scanner;
import java.io.*;

/**
 * Scanner is a simple scanner for Compilers and Interpreters (2014-2015) lab exercise 1
 * It scans input text one character at a time, skipping whitespaces and separating the input into 
 * tokens with types and lexeme values. A token can be a:
 *  - Identifier that starts with a letter (a-z, A-Z) and is followed by digits (0-9) or more 
 *    letters
 *  - Keyword which is a specific special identifier (only letters) part of the list found below
 *  - Int which is a series of only digits
 *  - Operand which is a series of only operator symbols (:, =, +, -, /, *, %, <, or >)
 *  - Separator which is a single character that is (, ), or ;
 *  - End which marks the end of the input stream
 * 
 * @author Harrison Chen
 * @version 08/30/23
 *  
 * Usage:
 * Create a scanner with text input from an InputStream (if from file then FileInputStream) or a 
 * String to get the first token use nextToken(), subsequent calls will jump to the next token 
 * and return it a token is of the immutable type Token, use getType() to return the enumerable 
 * type of the token and getLexeme() to return the lexemic String the token represents
 * 
 * NOTEBOOK:
 * if "if" followed by A-Z, 0-9, or _, then keep going and it is part of an identifier
 * if "if" followed by space, new line (, then stop and create token "if" as a keyword, '(' will be 
 * created as a new token as well whereas with new line and space, the scanner will skip over them 
 * as usual
 * 
 * I will usually pass currentChar to eat. The lookahead is useful to confirm what the next
 * character must be. For example, if given a /, and you check that the next character is not a
 * whitespace, =, or another / then for the code to compile, the next character must be a *, being
 * part of a block comment. So explicitly passing * to eat would be a way to confirm that.
 */
public class Scanner
{
    private BufferedReader in;
    private char currentChar;
    private boolean eof;
    private String[] keywords = {"VAR", "PROCEDURE", "FUNCTION", "BEGIN", "RETURN", "END",  "DO", "WHILE", 
            "IF", "ELSE", "WRITELN", "WRITE", "READLN", "IF", "THEN", "ELSE", "WHILE", "DO", "FOR", "TO", 
            "INT", "INTEGER", "BOOLEAN", "REAL", "STRING", "VAR", "CONST"};
    private String[] operatorStrings = {"AND","OR","NOT","MOD"};
    private String[] booleans = {"TRUE", "FALSE"};
    public static enum TOKEN_TYPE
    {IDENTIFIER, KEYWORD, INTEGER, REAL, OPERATOR, RELOP, SEPARATOR, BOOLEAN, STRING, END};

    /**
     * Scanner constructor for construction of a scanner that 
     * uses an InputStream object for input.  
     * Usage: 
     * FileInputStream inStream = new FileInputStream(new File(<file name>);
     * Scanner lex = new Scanner(inStream);
     * @param inStream the input stream to use
     */
    public Scanner(InputStream inStream)
    {
        in = new BufferedReader(new InputStreamReader(inStream));
        eof = false;
        getNextChar();
    }
    /**
     * Scanner constructor for constructing a scanner that 
     * scans a given input string.  It sets the end-of-file flag an then reads
     * the first character of the input string into the instance field currentChar.
     * Usage: Scanner lex = new Scanner(input_string);
     * @param inString the string to scan
     */
    public Scanner(String inString)
    {
        in = new BufferedReader(new StringReader(inString));
        eof = false;
        getNextChar();
    }

    /**
     * The getNextChar method attempts to get the next character from the input
     * stream. It sets the eof flag true if the end of file or a period is reached on
     * the input stream.  Otherwise, it reads the next character from the stream
     * and converts it to a char
     * @postcondition The input stream is advanced one character if it is not at
     *                end of file and the currentChar instance field is set to the 
     *                character read from the input stream. The flag eof is set true 
     *                if the input stream is exhausted.
     */
    private void getNextChar()
    {
        try
        {
            int input = in.read();
            if(input == -1)
                eof = true;
            else
                currentChar = (char) input;  
        }
        catch (IOException e)
        {
            e.printStackTrace();
            System.exit(-1);
        }
    }
    /**
     * Compares a character with the current character and gets the next character if they are 
     * equal, otherwise throws a CompileTimeException
     * @param expected the string to compare to currentChar, expected to match the current character
     * @throws CompileTimeException if other and current character do not match
     */
    private void eat(char expected) throws CompileTimeException
    {
        if(expected == currentChar)
            getNextChar();
        else
            throw new CompileTimeException("Illegal character - expected "+
                expected+" and found "+currentChar);
    }
    /**
     * Checks if the given character is a digit
     * @param character the char to check
     * @return true if character is a digit, false otherwise
     */
    private static boolean isDigit(char character)
    {
        return character >= '0' && character <= '9';
    }
    /**
     * Checks if the given character is a letter
     * @param character the char to check
     * @return true if character is a-z or A-Z, false otherwise
     */
    private static boolean isLetter(char character)
    {
        return character >= 'a' && character <= 'z' || character >= 'A' && character <= 'Z';
    }
    /**
     * Checks if the given character is a whitespace
     * @param character the char to check
     * @return true if character is a space, new line, tab, or carriage return, false otherwise
     */
    private static boolean isWhiteSpace(char character)
    {
        if(character == ' ' || character == '\n' || character == '\t' || character == '\r')
            return true;
        return false;
    }
    /**
     * Returns given character's type
     * @precondition character is not a whitespace, comment, or part of an identifier, keyword, or number
     * @param character the character to check
     * @return the type of the character
     * @throws CompileTimeException if an unrecognized character is found
     */
    private static TOKEN_TYPE typeOf(char character) throws CompileTimeException
    {
        switch(character)
        {
            case '+', '-', '/', '*', '%', ',': return TOKEN_TYPE.OPERATOR;
            case '=', '<', '>': return TOKEN_TYPE.RELOP;
            case '(',')',';',':': return TOKEN_TYPE.SEPARATOR;
            default: throw new CompileTimeException("Unrecognized character found: " + character);
        }
    }
    /**
     * Checks if the given character is a comment (follows /)
     * @precondition the previous character was a /
     * @param character the char to check
     * @return true if character is / or *
     */
    private static boolean isComment(char character)
    {
        return character == '/' || character == '*';
    }
    /**
     * Checks if there is a next character in the input stream
     * @return true if there is a next character, false otherwise
     */
    public boolean hasNext()
    {
        return currentChar!='.' && !eof;
    }

    /**
     * Scans input, advancing in the input stream and adding subsequent digits to the end 
     * of the starting digit until no further digit is found. Returns the full number as a token.
     * @precondition currentChar is a digit
     * @return a token with the type INTEGER or REAL and the complete number as the lexeme
     * @throws CompileTimeException on scanning error when eating
     */
    private Token scanNumber() throws CompileTimeException
    {
        String number = "";
        TOKEN_TYPE type = TOKEN_TYPE.INTEGER;
        while(!eof && isDigit(currentChar))
        {
            number += currentChar;
            eat(currentChar);
            if(type == TOKEN_TYPE.INTEGER && currentChar == '.')
            {
                number+=currentChar;
                eat('.');
                type = TOKEN_TYPE.REAL;
            }
        }
        return new Token(type, number);
    }
    /**
     * Scans input, adding subsequent letters and digits to the end of the starting letter 
     * until no further letters or digits are found. Returns the full identifier or operator, 
     * or keyword as a token.
     * @precondition currentChar is a letter
     * @return a token with the type IDENTIFIER, OPERATOR, or KEYWORD and the complete identifier 
     *         or keyword (if part of list) as the lexeme
     * @throws CompileTimeException on scanning error when eating
     */
    private Token scanIdentifier() throws CompileTimeException
    {
        String identifier = "";
        TOKEN_TYPE type = TOKEN_TYPE.IDENTIFIER;
        while(hasNext() && (isDigit(currentChar) || isLetter(currentChar)))
        {
            identifier += currentChar;
            eat(currentChar);                
        }
        identifier = identifier.toUpperCase();
        for(String kw : keywords) if(kw.equals(identifier)) type = TOKEN_TYPE.KEYWORD;
        if(identifier.equals("INT")) identifier = "INTEGER";
        for(String op : operatorStrings) if(identifier.equals(op)) type = TOKEN_TYPE.OPERATOR;
        for(String bool : booleans) if(identifier.equals(bool)) type = TOKEN_TYPE.BOOLEAN;
        return new Token(type, identifier);    
    }
    /**
     * Scans input, adding subsequent letters and digits to the end of the starting letter 
     * until no further letters or digits are found. Returns the full string as a token
     * @precondition currentChar is a letter
     * @return a token with the type stringING
     * @throws CompileTimeException on scanning error when eating
     */
    private Token scanString() throws CompileTimeException
    {
        eat('\'');
        String string = "";
        while(!eof && currentChar != '\'')
        {
            string += currentChar;
            eat(currentChar);                
        }
        eat('\'');
        return new Token(TOKEN_TYPE.STRING, string);    
    }
    /**
     * Eats input until reaches a new line if the comment starts with a single line comment,
     * or reaches the closing comment if the comment starts with an opening block comment.
     * @precondition currentChar was a '/', is currently either a '/' (for single line comments)
     *               or a '*' (for block comments)
     * @postcondition inputstream is advanced past the full comment, either a line or to the end
     *                of the block comment, creating no tokens along the way
     * @throws CompileTimeException on scanning error when eating
     */
    private void scanComment() throws CompileTimeException
    {
        if(currentChar == '/')
        {
            while(!eof && currentChar != '\n')
                eat(currentChar);
        }
        else if(currentChar == '*')
        {
            scanBlockComment('/');
        }
    }
    /**
     * Eats input until reaches a new line if the comment starts with a single line comment,
     * or reaches the closing comment if the comment starts with an opening block comment.
     * @precondition currentChar was a '/', is currently either a '/' (for single line comments)
     *               or a '*' (for block comments)
     * @postcondition inputstream is advanced past the full comment, either a line or to the end
     *                of the block comment, creating no tokens along the way
     * @throws CompileTimeException on scanning error when eating
     */
    private void scanBlockComment(char opening) throws CompileTimeException
    {
        char closing = '/';
        if (opening == '(') closing = ')';
        eat('*');
        boolean foundEndComment = false;
        while(!eof && !foundEndComment)
        {
            if(currentChar == '*')
            {
                eat(currentChar);
                if(currentChar == closing)
                    foundEndComment = true;
            }
            eat(currentChar);
        }
    }
    /**
     * Skips whitespace until reaches end of file or the next character. Reads character
     * and scans subsequent input until it obtains the full token. Returns the token found
     * or the end of file token if the end of file is reached.
     * @postcondition inputstream is advanced past the next token
     * @return the next token in the input stream with a type and a lexeme
     * @throws CompileTimeException on error with eating or if it receives an unrecognized 
     *                            character (probably a special character)
     */
    public Token nextToken() throws CompileTimeException
    {
        try
        {
            while(hasNext() && isWhiteSpace(currentChar))
                eat(currentChar);
            if(!hasNext())
                return new Token(TOKEN_TYPE.END, "EOF");
            if(isLetter(currentChar))
                return scanIdentifier();
            else if(isDigit(currentChar))
                return scanNumber();
            else if(currentChar == '\'')
                return scanString();
            else
            {
                String lexeme = currentChar+"";
                TOKEN_TYPE type = typeOf(currentChar);
                switch(currentChar)
                {
                    case '(': //me when theres exceptions
                        eat('(');
                        if(currentChar == '*')
                        {    
                            scanBlockComment('(');
                            return nextToken();
                        }
                        break;
                    case '/':
                        eat('/');
                        if(isComment(currentChar))
                        {
                            scanComment();
                            return nextToken();
                        }
                        break;
                    case ':':
                        eat(':');
                        if(currentChar == '=')
                        {
                            eat(currentChar);
                            type = TOKEN_TYPE.OPERATOR;
                            lexeme = ":=";
                        }
                        break;
                    case '<':
                        eat('<');
                        if(currentChar == '>')
                        {
                            eat(currentChar);
                            lexeme = "<>";
                        }
                        else if(currentChar == '=')
                        {
                            eat(currentChar);
                            lexeme = "<=";
                        }
                        break;
                    case '>':
                        eat('>');
                        if(currentChar == '=')
                        {
                            eat(currentChar);
                            lexeme = ">=";
                        }
                        break;
                    default: eat(currentChar);
                }
                return new Token(type, lexeme);
            }
        }
        catch(CompileTimeException e)
        {
            e.printStackTrace();
            System.exit(-1);
            return null;
        }
    }
}    
