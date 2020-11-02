;
;  ========================================================================
;  uA Interpreter
;  ========================================================================
;  
;  This file is sample source code for the ua Interpreter.
;  
;  ua Interpreter is free software: you can redistribute it and/or
;  modify it under the terms of the GNU General Public License as published
;  by the Free Software Foundation, either version 3 of the License, or (at
;  your option) any later version.
;  
;  ua Interpreter is distributed in the hope that it will be useful,
;  but WITHOUT ANY WARRANTY; without even the implied warranty of 
;  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
;  General Public License for more details.
;
;  You should have received a copy of the GNU General Public License along
;  with ua Interpreter.  If not, see <http://www.gnu.org/licenses/>.
;  
;  (C) Copyright 2018, Gabor Kecskemeti (g.kecskemeti@ljmu.ac.uk)
;

; Demonstrates how a hello world program could be written in uA
; Follows a similar approach as shown in HelloWorld.ua
;      This program has significantly fewer instructions though.
;
; Utilises registers for passing data in between method calls.

CONST mystr Hello World!

entry:
; Input parameter setup
; On which row to print out?
MVC 12,A
; On which column to print out?
MVC 30,B
; Where is the first letter to print out?
MVC mystr,C
; Where to jump after the printout completes
MVC exit,D
; Do the printout
JMC printAtWithRegisters
exit:





; The actual print method
; handles zero terminated strings
; Inputs accepted in registers 
;     -- A: rows,
;     -- B: cols,
;     -- C: string to print,
;     -- D: return loc.
; Does not guarantee any register values to be the same as before the call!
printAtWithRegisters:
MLC 80,A
ADR A,B
; B now has the address of the screen location where we want to print
; C has the address of the first char to print out

; We are ready to copy the text on the screen with the following loop
loop:
LDR C,A
; A now holds the char to print out,
;    so we can put it on the screen with the next store
STR B,A
; Move the pointers of the screen and the to be printed out character
ADC 1,B
ADC 1,C
; Check if the last char we fetched was a 0
JZC loop, A
; Yes it was, we have reached the end of the printout.
;   We need to return back to where we were called from.
JMR D