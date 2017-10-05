import uk.ac.ljmu.fet.cs.comp.interpreter.Interpret;

%%

%class LexuA
%unicode
%line
%column
%standalone

%{
%}

LineTerminator = \r|\n|\r\n
InputCharacter = [^\r\n]
WhiteSpace     = {LineTerminator} | [ \t\f]

InstructionID = "LD" | "ST" | "JZ" | "JM" | "AD" | "ML" | "DV" | "MV"
InputType = [CR]
Register = [ABCD]
Integer = [:jdigit:]+
Identifier = [:jletterdigit:]+
Comment = {WhiteSpace}* ";" {InputCharacter}* {LineTerminator}?

%state OPERATION INSTSPEC CONSPEC STRING

%%
    <YYINITIAL> {InstructionID}      { yybegin(OPERATION); return Interpret.Operation.valueOf(yytext()); }

    /* error fallback */
    [^]                              { throw new Error("Illegal character <"+
                                                        yytext()+">"); }
