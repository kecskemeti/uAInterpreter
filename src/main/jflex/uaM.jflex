import uk.ac.ljmu.fet.cs.comp.interpreter.Interpret;

%%

%class LexuA
%unicode
%line
%column
%standalone

%{
	StringBuffer string=new StringBuffer();
%}

LineTerminator    = \r|\n|\r\n
InputCharacter    = [^\r\n]
InLineWhiteSpace  = [ \t\f]
WhiteSpace        = {LineTerminator} | {InLineWhiteSpace}

InstructionID = "LD" | "ST" | "JZ" | "JM" | "AD" | "ML" | "DV" | "MV"
InputType = [CR]
Register = [ABCD]
Integer = "-"?[:digit:]+
Identifier = ([0-9]|[a-z])+
Comment = {InLineWhiteSpace}* ";" {InputCharacter}* {LineTerminator}?

%state INSTSPEC STRINGSPEC STRING

%%
    <INSTSPEC> {
        {InputType}			 { yybegin(YYINITIAL); System.out.println("Input type is: "+yytext()); return 10; }
    	[^]					 { throw new Error("Illegal input type spec at line "+ yyline + " on character "+ yychar); }
    }
    <STRINGSPEC>   {
    	{Identifier}		 { yybegin(STRING); string.setLength(0); System.out.println("Constlabel:" + yytext()); }
    	{InLineWhiteSpace}   { }
    	[^]					 { throw new Error("Illegal string identifier at line "+ yyline + " on character "+ yychar); }
    }
    <STRING> {
      [\n\r]                         { yybegin(YYINITIAL); System.out.println("String detected: "+ string.toString()); return 11; }
      \\t                            { string.append('\t'); }
      [^\n\r]+                       { string.append( yytext() ); }
    }
    <YYINITIAL> {
	    {InstructionID}      { yybegin(INSTSPEC); System.out.println("Instruction detected: "+yytext()); }
    	"CONST"				 { yybegin(STRINGSPEC); System.out.println("String constant detected "); }
    	"CONNR"				 { System.out.println("Numeric constant detected"); return 18; }
    	{Register}			 { System.out.println("Register detected: "+yytext()); }
        {Integer}			 { System.out.println("Integer detected: "+yytext()); return 12; }
    	{Identifier}		 { System.out.println("Identifier detected: "+yytext()); return 13; }
        ":"					 { System.out.println(":::"); return 17; }
        ","					 { System.out.println("comma"); return 16; }
        {Comment}|{WhiteSpace}	 {  }
    }
    

    /* error fallback */
    [^]                              { throw new Error("Illegal character at line "+ yyline + " on character "+ yychar+" <"+
                                                        yytext()+">"); }
