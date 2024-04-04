
/**
* This file defines a lexer for English essays to find correct MLA headers, MLA intext and works cited citations
*
* @author  Harrison Chen, Juliana Li
* @version 9/12/23
*
*/
import java.io.*;

%%
/* lexical functions */
/* specify that the class will be called Scanner and the function to get the next
 * token is called nextToken.
 */
%class Scanner
%unicode
%line
%column
%public
%function nextToken
/*  return String objects - the actual lexemes */
/*  returns the String "END: at end of file */
%type String
%eofval{
return "END";
%eofval}


/**
 * Pattern definitions
 * LineTerminator, WhiteSpace, Ltr, and Num find their corresponding single characters
 * Year finds 4 digits in a row
 * Page number is a single multi-digit number or two connected by a dash
 */
LineTerminator = \r|\n|\r\n
WhiteSpace = {LineTerminator} | [ \t\f]
Ltr = [a-zA-Z]
Num = [0-9]
Year = {Num}{Num}{Num}{Num}
PageNum = ({Num}+(-{Num}+)?)
Punctuation = [\.,:;—!\?]
EOS = (\.[\”\"]?)|(\![\”\"]?)|(\?[\”\"]?)
CapWord = [A-Z][a-z]*
Name = ({CapWord}(-{CapWord})?)|([A-Z]"\.")
ListOfNames = {Name},(" "{Name})+((", and "{Name}" "{Name})|(", et al"))?

%%
/**
 * lexical rules
 */
{Name}" "{Name}\n{CapWord}\.?" "{Name}\n.*\n{Num}+" "{CapWord}" "{Year}\n {return "MLA HEADER [\n"+yytext()+"]";}
[\“\"]({Ltr}|[-,\‘’'?!…:—\/\[\]\. ])+[\”\"]" \("({Name}" ")?((I|V|X)+\.(i|v|x)+\.)?{PageNum}"\)"({Punctuation}|" ") {return "INTEXT CITATION ["+yytext()+"] in paragraph "+yyline;}
{ListOfNames}"\. "[A-Za-z :—-]+"\."(" Edited by "(({Name}(" "{Name})*)|{ListOfNames})",")?([0-9a-z\. ]+",")?[A-Za-z\. ]+", "{Year}"\." {return "BOOK CITATION ["+yytext()+"]";}
\n"Works Cited"" "*\n {return "WORKS CITED PRESENT";}
[\“\"]?{CapWord}({Ltr}|[-,\‘’'…:—\/\[\] \“\"\”])*{EOS} {return "SENTENCE ["+yytext()+"]";}
{WhiteSpace}		{}
. {}