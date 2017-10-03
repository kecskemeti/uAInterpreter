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
;  (C) Copyright 2017, Gabor Kecskemeti (g.kecskemeti@ljmu.ac.uk)
;

; Demonstrates how a hello world program could be written in uA
; One can vary the location of the printout by setting the memory
;   locations of 10001 and 10001 (for x and y coordinates respectively)

CONST mystr Hello world!
entry:
; setting up the parameters for printat
MVC mystr,A
STC 10000,A
MVC 30,A
STC 10001,A 
MVC 12,A
STC 10002,A 
; telling printat to return to our exit infinite loop
MVC exit, A
STC 10003,A
; calling printat
JMC printAt
exit:
JMC exit

printAt:
; Par2: X
MVC 10001,A
LDR A, C
; Par3: Y
MVC 10002,A
LDR A, D
; Row conversion
MLC 80,D
; C is now linear loc
ADR D,C
; Par1: string
MVC 10000,A
LDR A,B
loop:
LDR B, A
STR C, A
ADC 1,B
ADC 1,C
JZC loop, A
MVC 10003, A
LDR A,B
; Return where we were told so
JMR B
