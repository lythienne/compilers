package parser;

import scanner.CompileTimeException;
import scanner.Scanner;
import scanner.Token;
import scanner.Scanner.TOKEN_TYPE;

import ast.*;
import ast.Int;

import java.util.List;
import java.util.ArrayList;
/**
 * Parser is a syntax analyzer for slightly less dumb PASCAL that uses a Scanner which provides
 * an input stream of tokens to parses a program with function, procedure, and variable
 * declarations followed by a statement (usually a BEGIN/END block). To do this, the Parser will 
 * store the current token.
 * 
 * A procedure or function declaration is in the PASCAL format:
 * 
 *      procedure identifier(maybeparams);          function identifier(maybeparams): returnType;
 *      var maybevars                               var maybevars
 *      statement                                   statement
 * 
 * Where "maybeparams" represents multiple lists of parameters and their types:
 * 
 *  - value:integer / name:string / flag:boolean (single variable/type pairings)
 *  - idNum,age:integer / name,address:string    (multiple variables with the same type)
 *  - date,hour,min,sec:integer; month:string    (multiple variables of varying types)
 * 
 * And "maybevars" represents lists of local variables to be used in the procedure/function:
 *  - var count:integer
 *  OR
 *  - var
 *    count,total:integer;
 *    flag:boolean;
 * 
 * The parser can parse the following PASCAL statements: 
 *  - BEGIN/END: A list of statements
 *  - WRITE(): prints the expressions inside the parentheses, if no () then prints an empty string
 *  - WRITELN(): printlns the expressions inside the parentheses, if no () then prints a newline
 *  - READLN(): reads user input and assigns to the variable(s) inside readln, variables must be
 *              declared and the type of input much match the variable type, if no () then waits
 *              for a return
 *  - IF/THEN/ELSE: conditional statements with boolean condition expressions
 *  - FOR/WHILE: loops with boolean condition expressions
 *  - := : assigns the variable on the left to the value of the expression on the right
 *  - procedure(): runs the procedure or function with the given params
 * 
 * The parser can parse the following expressions:
 *  - Numerical expressions with (), -, +, -, *, mod, numbers
 *  - Boolean expressions with (), AND, OR, and NOT, booleans
 *  - String expressions with (), +, strings (string concatenation with , in writeln does not work)
 *  - Conditionals (is-a boolean expression) that compare the above expressions with relative 
 *    operators: =, <>, <, >, <=, and >= (of course <, >, <=, and >= do not work for booleans)
 *  - Variables of type integer, boolean, or string (can also be in their corresponding expressions)
 *  - function() calls with parameters (can be in the corresponding expression of its return type)
 * 
 * Demonstrative PASCAL programs are in the TestFiles folder but feel free to write your own PASCAL
 * programs! I made sure that almost? everything is exactly the same as normal PASCAL syntax
 * 
 * the syntax is similar to PASCAL, meaning you need to use parentheses if saying (x<5) OR (x>70)
 * and its caseless
 * 
 * @author Harrison Chen
 * @version 10/22/23
 */
public class Parser
{
    private Scanner in;
    private Token cT;
    private List<String> strings;
    private List<Float> reals;

    /**
     * Creates a parser that will parse an input stream using a Scanner, keeps track of 
     * the current Token and a map of variables and values.
     * @param input the input stream
     * @throws CompileTimeException on Scanner error on getting the first character
     */
    public Parser(Scanner input) throws CompileTimeException
    {
        in = input;
        cT = in.nextToken();
        strings = new ArrayList<String>();
        reals = new ArrayList<Float>();
    }

    /**
     * Compares a token with the current token and gets the next token if they are 
     * equal, otherwise throws a CompileTimeException
     * @param expected the token to compare to cT, expected to match the current token
     * @throws CompileTimeException on error when scanning if the current character and 
     *                            expected character do not match while eating or if
     *                            the scanner sees an unrecognized character.
     */
    private void eat(Token expected)
    {
        if(expected.equals(cT))
            cT = in.nextToken();
        else
            throw new IllegalArgumentException("Illegal token - expected "+
                expected+" and found "+cT);
    }

    /**
     * Parses a PASCAL program, starting with variable, procedure, and function declarations, and
     * followed by a single statement.
     * @precondition if variable declaration is last before the statement, the single statement
     *               cannot start with an identifier (for obvious reasons) --I tested this in 
     *               actual PASCAL and the compiler throws an error because it will parse the 
     *               identifier as another variable declaration and fail
     * @return a Program object with lists of (lists of) variable declarations, procedures, and 
     *         functions and a single statement at the end
     */
    public Program parseProgram()
    {
        List<List<VariableDeclaration>> varList = new ArrayList<List<VariableDeclaration>>();
        List<List<ConstDeclaration>> constList = new ArrayList<List<ConstDeclaration>>();
        List<ProcedureDeclaration> procedureList = new ArrayList<ProcedureDeclaration>();
        List<FunctionDeclaration> funcList = new ArrayList<FunctionDeclaration>();
        String lexeme = cT.getLexeme();
        while(cT.getType().equals(TOKEN_TYPE.KEYWORD) && (lexeme.equals("VAR") 
                || lexeme.equals("PROCEDURE") || lexeme.equals("FUNCTION")
                || lexeme.equals("CONST")))
        {
            if(lexeme.equals("VAR"))
            {
                eat(new Token(TOKEN_TYPE.KEYWORD, "VAR"));
                varList.add(parseVariableDeclarations());
            }
            else if(lexeme.equals("PROCEDURE"))
                procedureList.add(parseProcedureDeclaration());
            else if(lexeme.equals("FUNCTION"))
                funcList.add(parseFunctionDeclaration());
            else if(lexeme.equals("CONST"))
                constList.add(parseConstants());
            lexeme = cT.getLexeme();
        }
        Program p = new Program(parseStatement(), strings, reals);
        p.setVariables(varList);
        p.setConstants(constList);
        p.setProcedures(procedureList);
        p.setFunctions(funcList);
        return p;
    }

    /**
     * Parses a comma separated list of variables, either before a type in variable declarations or
     * in the parentheses of a readln() 
     * 
     * NOT TO BE CONFUSED WITH parseIdentifier (sorry I couldn't think of better names)
     * 
     * @return a list of identifier strings separated by commas
     */
    private List<String> parseIdentifiers()
    {
        List<String> identifiers = new ArrayList<String>();
        identifiers.add(cT.getLexeme());
        eat(new Token(TOKEN_TYPE.IDENTIFIER, cT.getLexeme()));
        while(cT.equals(new Token(TOKEN_TYPE.OPERATOR, ",")))
        {
            eat(new Token(TOKEN_TYPE.OPERATOR, ","));
            identifiers.add(cT.getLexeme());
            eat(new Token(TOKEN_TYPE.IDENTIFIER, cT.getLexeme()));
        }
        return identifiers;
    }

    /**
     * Parses a block of variable declarations, either after var or in the () of a procedure or
     * function declaration as parameters. It will be a ; separated list of variableList-type pairs
     * where the variableList will be a , separated list of variable names (given by the function 
     * above)
     * @return A list of VariableDeclaration objects with identifiers of the variables and the types
     *         they are declared as
     */
    private List<VariableDeclaration> parseVariableDeclarations()
    {
        List<VariableDeclaration> vars = new ArrayList<VariableDeclaration>();
        boolean multipleParams = true;
        while(cT.getType().equals(TOKEN_TYPE.IDENTIFIER) && multipleParams)
        {
            List<String> identifiers = parseIdentifiers();
            eat(new Token(TOKEN_TYPE.SEPARATOR, ":"));
            String type = cT.getLexeme();
            eat(new Token(TOKEN_TYPE.KEYWORD, type));
            for(String id : identifiers)
                vars.add(new VariableDeclaration(id, type));
            if(cT.equals(new Token(TOKEN_TYPE.SEPARATOR, ";")))
            {
                eat(new Token(TOKEN_TYPE.SEPARATOR, ";"));
            }
            else
                multipleParams = false;
        }
        return vars;
    }

    /**
     * Parses a block of constant declarations, either after const, in the () of a procedure or
     * function declaration as parameters. It will be a ; separated list of variableList-type pairs
     * where the variableList will be a , separated list of variable names (given by the function 
     * above)
     * @return A list of ConstDeclaration objects with identifiers of the variables and the types
     *         they are declared as
     */
    private List<ConstDeclaration> parseConstants()
    {
        eat(new Token(TOKEN_TYPE.KEYWORD, "CONST"));
        List<ConstDeclaration> consts = new ArrayList<ConstDeclaration>();
        boolean multipleParams = true;
        while(cT.getType().equals(TOKEN_TYPE.IDENTIFIER) && multipleParams)
        {
            List<String> identifiers = parseIdentifiers();
            eat(new Token(TOKEN_TYPE.RELOP, "="));
            Expression exp = parseExpression();
            for(String id : identifiers)
                consts.add(new ConstDeclaration(id, exp));
            if(cT.equals(new Token(TOKEN_TYPE.SEPARATOR, ";")))
            {
                eat(new Token(TOKEN_TYPE.SEPARATOR, ";"));
            }
            else
                multipleParams = false;
        }
        return consts;
    }

    /**
     * Parses a procedure with the format given in the class description
     * @return a ProcedureDeclaration object with an identifier, statement, lists of local variable
     *         declarations and parameters (both lists can be empty)
     */
    private ProcedureDeclaration parseProcedureDeclaration()
    {
        List<VariableDeclaration> params;
        eat(new Token(TOKEN_TYPE.KEYWORD, "PROCEDURE"));
        String identifier = cT.getLexeme();
        eat(new Token(TOKEN_TYPE.IDENTIFIER, identifier));
        if(!cT.equals(new Token(TOKEN_TYPE.SEPARATOR, "(")))
            params = new ArrayList<VariableDeclaration>();
        else
        {
            eat(new Token(TOKEN_TYPE.SEPARATOR, "("));
            params = parseVariableDeclarations();
            eat(new Token(TOKEN_TYPE.SEPARATOR, ")"));
        }
        eat(new Token(TOKEN_TYPE.SEPARATOR, ";"));
        List<VariableDeclaration> vars = new ArrayList<VariableDeclaration>();
        if(cT.equals(new Token(TOKEN_TYPE.KEYWORD, "VAR")))
        {
            eat(new Token(TOKEN_TYPE.KEYWORD, "VAR"));
            vars = parseVariableDeclarations();
        }
        return new ProcedureDeclaration(identifier, vars, params, parseStatement());
    }

    /**
     * Parses a function with the format given in the class description
     * @return a FunctionDeclaration object with an identifier, returnType, statement, and
     *         lists of local variable declarations and parameters (both lists can be empty)
     */
    private FunctionDeclaration parseFunctionDeclaration()
    {
        eat(new Token(TOKEN_TYPE.KEYWORD, "FUNCTION"));
        String identifier = cT.getLexeme();
        eat(new Token(TOKEN_TYPE.IDENTIFIER, identifier));
        eat(new Token(TOKEN_TYPE.SEPARATOR, "("));
        List<VariableDeclaration> params = parseVariableDeclarations();
        eat(new Token(TOKEN_TYPE.SEPARATOR, ")"));
        eat(new Token(TOKEN_TYPE.SEPARATOR, ":"));
        String returnType = cT.getLexeme();
        eat(new Token(TOKEN_TYPE.KEYWORD, returnType));
        eat(new Token(TOKEN_TYPE.SEPARATOR, ";"));
        List<VariableDeclaration> vars = new ArrayList<VariableDeclaration>();
        if(cT.equals(new Token(TOKEN_TYPE.KEYWORD, "VAR")))
        {
            eat(new Token(TOKEN_TYPE.KEYWORD, "VAR"));
            vars = parseVariableDeclarations();
        }
        return new FunctionDeclaration(identifier, returnType, 
                vars, params, parseStatement());
    }

    /**
     * Parses a statement such as a BEGIN-END block, a WRITELN/READLN statment, a IF/THEN/ELSE,
     * a WHILE or FOR loop, or a variable assignment, or a procedure/function call and returns 
     * it as an object
     * @precondition current token is a keyword "BEGIN", "WRITELN", "READLN", "IF", "WHILE", "FOR",
     *               or an identifier that is either a variable or a procedure/function name
     * @postcondition the statement has been parsed into an object of the corresponding type with 
     *                the correct instance variables and is ready to be executed
     * @return a statement object of the correct type with correct instance variables
     */
    private Statement parseStatement()
    {
        String lexeme = cT.getLexeme();
        switch(lexeme) //well too bad checkstyle im too lazy to change your xml file so be that way
        {
            case "BEGIN": return parseBlock();
            case "WRITE": return parseWrite();
            case "WRITELN": return parseWriteln();
            case "READLN": return parseReadln();
            case "IF": return parseIf();
            case "WHILE": return parseWhile();
            case "FOR": return parseFor();
            default:
                if(cT.getType().equals(TOKEN_TYPE.IDENTIFIER)) return parseIdentifier();
                throw new IllegalArgumentException("Parser at: "+lexeme+", not at a statement");
        }
    }

    /**
     * Parses a block of statements, creating a block object with the list of statements between 
     * the BEGIN and END;
     * @return the block object with the list of statements
     */
    private Block parseBlock()
    {
        List<Statement> statements = new ArrayList<Statement>();
        Block b = new Block(statements);
        eat(new Token(TOKEN_TYPE.KEYWORD, "BEGIN"));
        while(!cT.equals(new Token(TOKEN_TYPE.KEYWORD, "END")))
            statements.add(parseStatement());
        eat(new Token(TOKEN_TYPE.KEYWORD, "END"));
        eat(new Token(TOKEN_TYPE.SEPARATOR, ";"));
        return b;
    }
    
    /**
     * Parses a WRITELN statement, creating a Writeln object with the list of expressions 
     * it will println
     * @return the Writeln object with the list of expressions
     */
    private Writeln parseWriteln()
    {
        eat(new Token(TOKEN_TYPE.KEYWORD, "WRITELN"));
        if(!cT.equals(new Token(TOKEN_TYPE.SEPARATOR, "(")))
        {
            eat(new Token(TOKEN_TYPE.SEPARATOR, ";"));
            return new Writeln(null);
        }
        eat(new Token(TOKEN_TYPE.SEPARATOR, "("));
        Writeln w = new Writeln(parseArgs());
        eat(new Token(TOKEN_TYPE.SEPARATOR, ")"));
        eat(new Token(TOKEN_TYPE.SEPARATOR, ";"));
        return w;
    }
    /**
     * Parses a WRITE statement, creating a Write object with the list of expressions it will print
     * @return the Write object with the list of expressions
     */
    private Write parseWrite()
    {
        eat(new Token(TOKEN_TYPE.KEYWORD, "WRITE"));
        if(!cT.equals(new Token(TOKEN_TYPE.SEPARATOR, "(")))
        {
            eat(new Token(TOKEN_TYPE.SEPARATOR, ";"));
            return new Write(null);
        }
        eat(new Token(TOKEN_TYPE.SEPARATOR, "("));
        Write w = new Write(parseArgs());
        eat(new Token(TOKEN_TYPE.SEPARATOR, ")"));
        eat(new Token(TOKEN_TYPE.SEPARATOR, ";"));
        return w;
    }

    /**
     * Parses a READLN statement, creating a Readln object with one or more variables that it 
     * will assign user-inputted values when executed
     * @return the created Readln object with a list of variable names
     */
    private Readln parseReadln()
    {
        eat(new Token(TOKEN_TYPE.KEYWORD, "READLN"));
        if(!cT.equals(new Token(TOKEN_TYPE.SEPARATOR, "(")))
        {
            eat(new Token(TOKEN_TYPE.SEPARATOR, ";"));
            return new Readln(null);
        }
        eat(new Token(TOKEN_TYPE.SEPARATOR, "("));
        Readln r = new Readln(parseIdentifiers());
        eat(new Token(TOKEN_TYPE.SEPARATOR, ")"));
        eat(new Token(TOKEN_TYPE.SEPARATOR, ";"));
        return r;
    }

    /**
     * Parses an IF/THEN/ELSE block, creating an If object with the boolean condition expression and
     * the one or two statements that will be executed depending on the value of the condition
     * @return the if object with the boolean expression and the one or two statements (then/else)
     */
    private If parseIf()
    {
        eat(new Token(TOKEN_TYPE.KEYWORD, "IF"));
        Expression b = parseExpression();
        eat(new Token(TOKEN_TYPE.KEYWORD, "THEN"));
        Statement s1 = parseStatement();
        Statement s2 = null;
        if(cT.equals(new Token(TOKEN_TYPE.KEYWORD, "ELSE")))
        {
            eat(new Token(TOKEN_TYPE.KEYWORD, "ELSE"));
            s2 = parseStatement();
        }
        return new If(b, s1, s2);
    }

    /**
     * Parses a WHILE loop, creating a While object with a boolean condition expression and the
     * statement that will be repeated as long as the condition is true
     * @return the created While object with an expression and statement
     */
    private While parseWhile()
    {
        eat(new Token(TOKEN_TYPE.KEYWORD, "WHILE"));
        Expression b = parseExpression();
        eat(new Token(TOKEN_TYPE.KEYWORD, "DO"));
        Statement s = parseStatement();
        return new While(b, s);
    }

    /**
     * Parses a FOR loop, creating a For object with a variable and two expressions giving the start
     * and end values for the variable, and a statement that gets repeatedly executed
     * @return the created For object with its variable, two expressions, and statement
     */
    private For parseFor()
    {
        eat(new Token(TOKEN_TYPE.KEYWORD, "FOR"));
        Variable v = new Variable(cT.getLexeme(), "INTEGER");
        eat(new Token(TOKEN_TYPE.IDENTIFIER, cT.getLexeme()));
        eat(new Token(TOKEN_TYPE.OPERATOR, ":="));
        Expression e1 = parseExpression();
        eat(new Token(TOKEN_TYPE.KEYWORD, "TO"));
        Expression e2 = parseExpression();
        eat(new Token(TOKEN_TYPE.KEYWORD, "DO"));
        Statement s = parseStatement();
        return new For(v, e1, e2, s);
    }

    /**
     * Parses a , separated list of expressions that are arguments to a function or procedure call
     * @return the list of expressions
     */
    private List<Expression> parseArgs()
    {
        List<Expression> args = new ArrayList<Expression>();
        if(!cT.equals(new Token(TOKEN_TYPE.SEPARATOR, ")")))
        {
            args.add(parseExpression());
            while(cT.equals(new Token(TOKEN_TYPE.OPERATOR, ",")))
            {
                eat(new Token(TOKEN_TYPE.OPERATOR, ","));
                args.add(parseExpression());
            }
        }
        return args;
    }

    /**
     * Parses a statement that begins with an identifier, which is either: 
     *  - Variable assignment, returning an Assignment object with a variable and its expression
     *  - Procedure/Function call, returning a ProcedureCall object with the name of the 
     *    procedure/method and a list of arguments
     * @return the correct statement object with correct type and instance variables
     */
    private Statement parseIdentifier()
    {
        String lexeme = cT.getLexeme();
        eat(new Token(TOKEN_TYPE.IDENTIFIER, lexeme));
        if(cT.equals(new Token(TOKEN_TYPE.SEPARATOR, "(")) || 
                cT.equals(new Token(TOKEN_TYPE.SEPARATOR, ";")))
        {
            List<Expression> args = new ArrayList<Expression>();
            if(cT.equals(new Token(TOKEN_TYPE.SEPARATOR, ";")))
            {
                eat(new Token(TOKEN_TYPE.SEPARATOR, ";"));
                return new ProcedureCall(lexeme, args);
            }
            eat(new Token(TOKEN_TYPE.SEPARATOR, "("));
            args = parseArgs();
            eat(new Token(TOKEN_TYPE.SEPARATOR, ")"));
            eat(new Token(TOKEN_TYPE.SEPARATOR, ";"));
            return new ProcedureCall(lexeme, args);
        }
        else
        {
            eat(new Token(TOKEN_TYPE.OPERATOR, ":="));
            Variable v = new Variable(lexeme, null);
            Assignment a = new Assignment(v, parseExpression());
            eat(new Token(TOKEN_TYPE.SEPARATOR, ";"));
            return a;
        }  
    }

    /**
     * Parses the highest precedence expression operations — such as pure numbers, booleans, or
     * strings, an expression in (), a variable, a function call, or a unary operator on an 
     * expression (NOT or -)
     * @precondition current token is a separator "(", operator "-"/"NOT", number, boolean, 
     *               string, or identifier
     * @postcondition the expression has been parsed and the an Expression object of the correct
     *                class and instance variables has been created and returned
     * @return the Expression object of the correct class with the correct instance variables given
     *         the input
     */
    private Expression parseE3()
    {
        Expression e;
        if(cT.getLexeme().equals("("))
        {
            eat(new Token(TOKEN_TYPE.SEPARATOR, "("));
            e = parseExpression();
            eat(new Token(TOKEN_TYPE.SEPARATOR, ")"));
        }
        else if(cT.getLexeme().equals("-"))
        {
            eat(new Token(TOKEN_TYPE.OPERATOR, "-"));
            e = new UnOp("-", parseE3());
        }
        else if(cT.getType().equals(TOKEN_TYPE.INTEGER))
        {
            e = new Int(Integer.parseInt(cT.getLexeme()));
            eat(new Token(TOKEN_TYPE.INTEGER, cT.getLexeme()));
        }
        else if(cT.getType().equals(TOKEN_TYPE.REAL))
        {
            float real = Float.parseFloat(cT.getLexeme());
            e = new Real(real, reals.size());
            reals.add(real);
            eat(new Token(TOKEN_TYPE.REAL, cT.getLexeme()));
        }
        else if(cT.getType().equals(TOKEN_TYPE.IDENTIFIER))
        {
            String lexeme = cT.getLexeme();
            eat(new Token(TOKEN_TYPE.IDENTIFIER, lexeme));
            if(cT.equals(new Token(TOKEN_TYPE.SEPARATOR, "(")))
            {
                eat(new Token(TOKEN_TYPE.SEPARATOR, "("));
                List<Expression> args = parseArgs();
                eat(new Token(TOKEN_TYPE.SEPARATOR, ")"));
                e = new FunctionCall(lexeme, args);
            }
            else
                e = new Variable(lexeme, null);
        }
        else if(cT.getLexeme().equals("NOT"))
        {
            eat(new Token(TOKEN_TYPE.OPERATOR, "NOT"));
            e = new UnOp("NOT", parseE3());
        }
        else if(cT.getType().equals(TOKEN_TYPE.BOOLEAN))
        {
            e = new Bool(Boolean.parseBoolean(cT.getLexeme()));
            eat(new Token(TOKEN_TYPE.BOOLEAN, cT.getLexeme()));
        }
        else if(cT.getType().equals(TOKEN_TYPE.STRING))
        {
            e = new Str(cT.getLexeme(), strings.size());
            strings.add(cT.getLexeme());
            eat(new Token(TOKEN_TYPE.STRING, cT.getLexeme()));
        }
        else
        {
            throw new IllegalArgumentException("Not an expression");
        }
        return e;
    }
    
    /**
     * Parses the second highest precedence expression operations: binary operator 
     * expressions with *, /, mod, or AND and creates and returns an BinOp object 
     * with the operator and the two (or more) expressions on either side
     * @precondition current token is the beginning of an expression
     * @postcondition the entire binary operator expression has been parsed and returned. 
     *                The current token follows the final expression
     * @return the BinOp object with the correct operator and expressions on both sides
     */
    private Expression parseE2()
    {
        Expression e = parseE3();
        String lexeme = cT.getLexeme();

        while(lexeme.equals("*") || lexeme.equals("/") || lexeme.equals("MOD") 
                || lexeme.equals("AND"))
        {
            eat(new Token(TOKEN_TYPE.OPERATOR, lexeme));
            e = new BinOp(lexeme, e, parseE3());
            lexeme = cT.getLexeme();
        }
        return e;
    }

    /**
     * Parses the lowest precedence expression operations: binary operator expressions with +, -, 
     * or OR and creates an BinOp object with the operator and the two (or more) expressions on 
     * either side. After parsing all the the +/-/OR, checks for a relative operator ONCE (per 
     * expression), and if it is there then it will parse the expression on the other side of it and
     * create a Condition object with the two expressions and the relative operator.
     * @precondition current token is the beginning of an expression
     * @postcondition the entire binary operator expression has been parsed and returned. 
     *                The current token follows the final expression
     * @return the BinOp object with the correct operator and expressions on both sides OR Condition
     *         object with the BinOp expression, another expression and the relative operator
     */
    private Expression parseExpression()
    {
        Expression e = parseE2();
        String lexeme = cT.getLexeme();

        while(lexeme.equals("+") || lexeme.equals("-") 
                || lexeme.equals("OR"))
        {
            eat(new Token(TOKEN_TYPE.OPERATOR, lexeme));
            e = new BinOp(lexeme, e, parseE2());
            lexeme = cT.getLexeme();
        }
        if(cT.getType().equals(TOKEN_TYPE.RELOP))
        {
            String relop = cT.getLexeme();
            eat(new Token(TOKEN_TYPE.RELOP, relop));
            return new Condition(relop, e, parseExpression());
        }
        return e;
    }
}