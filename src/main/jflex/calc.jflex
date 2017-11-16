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
import uk.ac.ljmu.fet.cs.comp.calccomp.tokens.*;

%%

%public
%class LexCalc
%line
%column
%type CalcExpression

%{
	VariableRef targetVar=null;
	// Default: assignment 
	String stKind="";
	CalcExpression left, right;
	boolean doLeft=true;
	
	private void saveExpr(CalcExpression e) {
		if((doLeft&&left!=null)||(!doLeft&&right!=null)) {
			throwError("Too complex parameter spec");
		}
		if(doLeft) {
			left=e;
			doLeft=false;
		} else {
			right=e;
			doLeft=true;			
		}
	}
	
	private void switchToInitial() {
		targetVar=null; stKind=""; left=right=null; doLeft=true;
		yybegin(YYINITIAL);
	}
	
	private Statement genStatement() {
		Statement s=Statement.statementFactory(yyline, stKind, targetVar, left, right);
		switchToInitial();
		return s;
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
Comment = "---" {InputCharacter}* {LineTerminator}?


var = [A-Z]+
num = "-"?[0-9]+
op = {InLineWhiteSpace} [+-/*%!] {InLineWhiteSpace}

%state AFTEREQ VARDEF PRINTST

%%
    <AFTEREQ> {
    	{op}					 { if(stKind=="") {
    								   stKind=yytext().trim();
    							   } else {
    							   	   throwError("Only a single operation is allowed per line");
    							   }
    							 }
    	{var}					 { saveExpr(new VariableRef(yyline, yytext())); }
    	{num}					 { saveExpr(new CalcIntNumber(yyline, yytext())); }
    	{InLineWhiteSpace}    	 { }
    	{Comment}|{LineTerminator}		 { return genStatement(); }
    	[^]						 { throwError("Illegal assignment spec"); }
    }
    <VARDEF> {
    	"="						 { yybegin(AFTEREQ); }
    	{InLineWhiteSpace}    	 { }
    	[^]					  	 {	throwError("Illegal number constant value"); }
    }
    <PRINTST> {
    	{var}				  	 {	if(targetVar==null) {
    									targetVar=new VariableRef(yyline,yytext());
    								} else {
    									throwError("Only a single variable can be printed out at a time");
    								}
    							 }
    	{Comment}|{LineTerminator}
    				 			 { 	return genStatement(); }
    	{InLineWhiteSpace}    	 { }
    	[^]					  	 {	throwError("Illegal print statement"); }
    }
    <YYINITIAL> {
    	"#"						 {  return new AlterScope(yyline); }
    	"print"					 {  yybegin(PRINTST); stKind=yytext(); }
    	{var}					 {  yybegin(VARDEF); targetVar=new VariableRef(yyline, yytext()); }
    	{Comment}				 {  }						
        {WhiteSpace}	 		 {  }
    }
    

    /* error fallback */
    [^]                          { throwError("Illegal character"); }
