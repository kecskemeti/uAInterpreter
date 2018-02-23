/*
 *  ========================================================================
 *  uB Interpreter
 *  ========================================================================
 *  
 *  This file is part of uB Interpreter.
 *  
 *  uB Interpreter is free software: you can redistribute it and/or
 *  modify it under the terms of the GNU General Public License as published
 *  by the Free Software Foundation, either version 3 of the License, or (at
 *  your option) any later version.
 *  
 *  uB Interpreter is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of 
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License along
 *  with uB Interpreter.  If not, see <http://www.gnu.org/licenses/>.
 *  
 *  (C) Copyright 2018, Gabor Kecskemeti (g.kecskemeti@ljmu.ac.uk)
 */
package uk.ac.ljmu.fet.cs.comp.ub.generated;
import uk.ac.ljmu.fet.cs.comp.ub.tokens.*;

%%

%public
%class LexuB
%line
%column
%type UBEx

%{
	StringBuffer string=new StringBuffer();
	String opType=null;
	UBEx left, right;
	boolean doLeft=true;
	boolean addrModifier=false;
	
	private void saveExpr(UBEx e) {
		if((doLeft&&left!=null)||(!doLeft&&right!=null)) {
			throwError("Too complex parameter spec");
		}
		if(doLeft) {
			left=e;
		} else {
			right=e;
		}
		addrModifier=false;
	}
	
	private void switchToInitial() {
		opType=null;
		left=right=null;
		doLeft=true;
		string.setLength(0);
		yybegin(YYINITIAL);
	}
	
	private UBInst genOperation() {
		try {
			UBInst o=UBInst.instFactory(yyline, opType, (UBContainer) left, (UBContainer) right);
			switchToInitial();
			return o;
		} catch(Exception e) {
			throwError("Illegal instruction specification",e);
			return null;
		}
	}
	
	private UBEx genNR() {
		UBNr nc=new UBNr(yyline, left, right);
    	switchToInitial();
    	return nc;
	}
	
	private UBEx genST() {
		UBSc sc = new UBSc(yyline, left, new UBSt(yyline, string.toString()));
      	switchToInitial();
      	return sc;
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
InstructionID = "COND" | "JUMP" | "ADDI" | "MULI" | "DIVI" | "MOVE"
Integer = "-"?[:digit:]+
Register = [ABCD]
Identifier = [a-z]([0-9]|[A-z])*
Comment = "~" {InputCharacter}* {LineTerminator}?

%state PARSPEC STRINGSPEC PRESTRINGWS STRING NRSPEC LABSPEC NRPART

%%
    <PARSPEC> {
    	"@"						 {  addrModifier=true; }
    	{Register}	          	 {	saveExpr(new UBReg(yyline,yytext(),addrModifier)); }
    	{Integer}			  	 {	saveExpr(new UBInt(yyline,yytext(),addrModifier)); }
    	{Identifier}	      	 {	saveExpr(new UBId(yyline,yytext(),addrModifier)); }
    	","				      	 {	doLeft=false; }
    	{InLineWhiteSpace}	  	 { }
    	{LineTerminator}      	 {	return genOperation(); }
    	<<EOF>>				  	 {	return genOperation(); }
    	[^]					  	 {	throwError("Illegal parameter spec"); }
    }
    <STRINGSPEC>   {
    	{Identifier}		 	 { 	yybegin(PRESTRINGWS);
    								string.setLength(0);
    								saveExpr(new UBId(yyline,yytext()));
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
      [\n\r]                     {	return genST(); }
      <<EOF>>					 {  return genST(); }
      \\t                        {	string.append('\t'); }
      [^\n\r]+                   {	string.append( yytext() ); }
    }
    <NRSPEC> {
    	{Identifier}		  	 {	saveExpr(new UBId(yyline,yytext())); doLeft=false; yybegin(NRPART); }
    	{InLineWhiteSpace}    	 { }
    	[^]					  	 {	throwError("Illegal number constant id"); }
    }
    <NRPART> {
    	{Integer}			  	 {	saveExpr(new UBInt(yyline,yytext())); }
    	{InLineWhiteSpace}    	 { }
    	{LineTerminator}	  	 {	return genNR(); }
    	<<EOF>>					 {  return genNR(); }
    	[^]					  	 {	throwError("Illegal number constant value"); }
    }
    <LABSPEC> {
    	{Identifier}			 {  
    								UBLab lab= new UBLab(yyline, new UBId(yyline,yytext()), null);
    								yybegin(YYINITIAL);
    								return lab; }
    	{InLineWhiteSpace}    	 { }
    	[^]					  	 {	throwError("Illegal label specification"); }
    }
    <YYINITIAL> {
	    {InstructionID}       	 {	yybegin(PARSPEC);
	    							opType=yytext();
        							left=right=null;
        							doLeft=true;
        							addrModifier=false;
	    						 }
    	"STR"				  	 {	yybegin(STRINGSPEC); }
    	"NUMBER"			  	 {	yybegin(NRSPEC); }
    	":"						 {  yybegin(LABSPEC); }
        {Comment}|{WhiteSpace}	 {  }
    }
    

    /* error fallback */
    [^]                          { throwError("Illegal character"); }
