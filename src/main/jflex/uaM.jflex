/*
 *  ========================================================================
 *  uA Interpreter
 *  ========================================================================
 *  
 *  This file is part of ua Interpreter.
 *  
 *  ua Interpreter is free software: you can redistribute it and/or
 *  modify it under the terms of the GNU General Public License as published
 *  by the Free Software Foundation, either version 3 of the License, or (at
 *  your option) any later version.
 *  
 *  ua Interpreter is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of 
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License along
 *  with ua Interpreter.  If not, see <http://www.gnu.org/licenses/>.
 *  
 *  (C) Copyright 2017, Gabor Kecskemeti (g.kecskemeti@ljmu.ac.uk)
 */
package uk.ac.ljmu.fet.cs.comp.interpreter.generated;
import uk.ac.ljmu.fet.cs.comp.interpreter.tokens.*;

%%

%public
%class LexuA
%line
%column
%type Expression

%{
	StringBuffer string=new StringBuffer();
	String opType=null;
	Operation.AttKind inspec=null;
	Expression left, right;
	boolean doLeft=true;
	
	private void saveExpr(Expression e) {
		if((doLeft&&left!=null)||(!doLeft&&right!=null)) {
			throwError("Too complex parameter spec");
		}
		if(doLeft) {
			left=e;
		} else {
			right=e;
		}
	}
	
	private void switchToInitial() {
		opType=null;
		left=right=null;
		inspec=null;
		doLeft=true;
		string.setLength(0);
		yybegin(YYINITIAL);
	}
	
	private Operation genOperation() {
		try {
			Operation o=Operation.opFactory(yyline, opType, left, (Register)right, inspec);
			switchToInitial();
			return o;
		} catch(Exception e) {
			throwError("Illegal instruction specification",e);
			return null;
		}
	}
	
	private void throwError(String text, Throwable e) {
		throw new Error(text+" at line "+ (yyline+1) + " on character "+ (yycolumn+1), e);
	} 
	
	private void throwError(String text) {
		throwError(text,null);
	}
%}

LineTerminator    = \r|\n|\r\n
InputCharacter    = [^\r\n]
InLineWhiteSpace  = [ \t\f]
WhiteSpace        = {LineTerminator} | {InLineWhiteSpace}

InstructionID = "LD" | "ST" | "JZ" | "JM" | "AD" | "ML" | "DV" | "MV" | "SB"
InputType = [CR]
Register = [ABCD]
Integer = "-"?[:digit:]+
Identifier = ([0-9]|[a-z])([0-9]|[A-z])*
Comment = {InLineWhiteSpace}* ";" {InputCharacter}* {LineTerminator}?

%state INSTSPEC PARSPEC STRINGSPEC PRESTRINGWS STRING NRSPEC LABSPEC NRPART VARDEF

%%
    <INSTSPEC> {
        {InputType}			 	 { 	yybegin(PARSPEC);
        							inspec=Operation.AttKind.valueOf(yytext());
        							left=right=null;
        							doLeft=true;
        						 }
    	[^]					 	 {	throwError("Illegal input type spec"); }
    }
    <PARSPEC> {
    	{Register}	          	 {	saveExpr(new Register(yyline,yytext())); }
    	{Integer}			  	 {	saveExpr(new IntNumber(yyline,yytext())); }
    	{Identifier}	      	 {	saveExpr(new Identifier(yyline,yytext())); }
    	","				      	 {	doLeft=false; }
    	{InLineWhiteSpace}	  	 { }
    	{LineTerminator}      	 {	return genOperation(); }
    	<<EOF>>				  	 {	return genOperation(); }
    	[^]					  	 {	throwError("Illegal parameter spec"); }
    }
    <STRINGSPEC>   {
    	{Identifier}		 	 { 	yybegin(PRESTRINGWS);
    								string.setLength(0);
    								saveExpr(new Identifier(yyline,yytext()));
    								doLeft=false;
    							 }
    	{InLineWhiteSpace}   	 { }
    	[^]					 	 {	throwError("Illegal string identifier"); }
    }
    <PRESTRINGWS> {
    	{InLineWhiteSpace}		 { }
    	[^]						 { yybegin(STRING); string.append(yytext());}
    }
    <STRING> {
      [\n\r]                     {	StringConstant sc = new StringConstant(yyline, left, new StringValue(yyline, string.toString()));
      								switchToInitial();
      								return sc;
      							 }
      \\t                        {	string.append('\t'); }
      [^\n\r]+                   {	string.append( yytext() ); }
    }
    <NRSPEC> {
    	{Identifier}		  	 {	saveExpr(new Identifier(yyline,yytext())); doLeft=false; yybegin(NRPART); }
    	{InLineWhiteSpace}    	 { }
    	[^]					  	 {	throwError("Illegal number constant id"); }
    }
    <NRPART> {
    	{Integer}			  	 {	saveExpr(new IntNumber(yyline,yytext())); }
    	{InLineWhiteSpace}    	 { }
    	{LineTerminator}	  	 {	NumberConstant nc=new NumberConstant(yyline, left, right);
    								switchToInitial();
    								return nc;
    							 }
    	[^]					  	 {	throwError("Illegal number constant value"); }
    }
    <LABSPEC> {
    	":"					  	 {	CodeLabel cl = new CodeLabel(yyline, (Identifier)left, right);
    								switchToInitial();
    								return cl;
    							 }
    	{InLineWhiteSpace}    	 { }
    	[^]					  	 {	throwError("Illegal label specification"); }
    }
    <VARDEF> {
    	{Identifier}			 {  saveExpr(new Identifier(yyline,yytext())); doLeft=false; }
    	{InLineWhiteSpace}		 { }
    	{LineTerminator}		 {  VariableDefinition vd=new VariableDefinition(yyline, (Identifier)left);
    								switchToInitial();
    							    return vd; 
    							 }
    	[^]						 { throwError("Illegal variable identifier"); }
    	
    }
    <YYINITIAL> {
	    {InstructionID}       	 {	yybegin(INSTSPEC);
	    							opType=yytext();
	    							inspec=null;
	    						 }
    	"CONST"				  	 {	yybegin(STRINGSPEC); }
    	"CONNR"				  	 {	yybegin(NRSPEC); }
    	"VAR"					 {  yybegin(VARDEF); }
    	{Identifier}		  	 {	saveExpr(new Identifier(yyline,yytext()));
    								yybegin(LABSPEC);
    							 }
        {Comment}|{WhiteSpace}	 {  }
    }
    

    /* error fallback */
    [^]                          { throwError("Illegal character"); }
