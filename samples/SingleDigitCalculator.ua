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

; Simple single digit subtraction with prompts
; Extends the Subtract.ua with a nice UI

; This sample shows features like:
; 1. Multiple functions handled in a single program: printAt, readinput
; 2. Shows how the main program flow is interrupted and returned to by
;      function calls (see lines about labels firstinput, secondinput etc.)
; 3. Shows a simple example on how to combine the foundational examples of
;      ScanInput.ua, Subtract.ua and HelloWorld.ua
;      The location of the copies of these files and the changes applied to
;        them are all listed in comments before the copies are shown
; 4. Shows how to determine the length of a string (by using printAt and 
;      its originally unused output on register C)
; 5. Uses the following variable mapping:
;      - return address from functions -> 10003
;              (notice that this disallows function call nesting!)
;      - return value of readinput     -> 10004
;      - input number 1                -> 10005
;      - input number 2                -> 10006
;      - final result                  -> 10005 
;              (notice that the no longer required input 1 is dropped)


CONST q1 Please enter a 'large' single digit number
; Making of a zero terminated string
CONNR term1 0
CONST q2 Please enter a 'smaller' single digit number
; Making of a zero terminated string
CONNR term2 0
CONST resp Subtracting your second from the first results in:
; Making of a zero terminated string
CONNR term3 0

entry:
MVC q1,A
STC 10000,A
MVC 0,A
STC 10001,A 
MVC 0,A
STC 10002,A 
; telling printat where to return after the call
MVC firstinput, A
STC 10003,A
; calling printat
JMC printAt

firstinput:
MVC secondquestion,A
STC 10003,A
JMC readinput

secondquestion:
; Saves the return value from readinput to 10005
LDC 10004,A
STC 10005,A
; And outputs to the second line of the screen
STC 80,A

; Prints out the next question
MVC q2,A
STC 10000,A
MVC 0,A
STC 10001,A 
MVC 2,A
STC 10002,A 
; telling printat where to return after the call
MVC secondinput, A
STC 10003,A
; calling printat
JMC printAt

secondinput:
MVC calculation,A
STC 10003,A
JMC readinput

calculation:
; Saves the return value from readinput to 10006
LDC 10004,A
STC 10006,A
; And outputs to the fourth line of the screen
STC 240,A

; copy of subtract (except in1 and in2 handling + output)
;  we have to replace in1 and in2 with the mem locations
;  we used to store the keyboard input (i.e., 10005/6)
LDC 10005, A
; Memory contains the ASCII code of the number
; As if we have read it from the keyboard
ADC -48, A
LDC 10006, B
ADC -48, B
; No subtraction operation!
; So we have to do a multiplication first
MLC -1,B
ADR B,A
ADC 48,A
; We store the result of our calculation to 10005
STC 10005, A

; Prints out the constant part of the response
MVC resp,A
STC 10000,A
MVC 0,A
STC 10001,A 
MVC 4,A
STC 10002,A 
; telling printat where to return after the call
MVC resultprompt, A
STC 10003,A
; calling printat
JMC printAt
resultprompt:
; At this point register C holds the pointer to the end of
;     the response string on the screen, we should print there
; We recall the result of the calculation
LDC 10005,A
; Print out to the screen at the end of the response
STR C,A
exit:
JMC exit

; Copy of the printAt function from the hello world example
; Notice that C will point after the last char printed
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

; Copy of the scanner example (with reduced register use)
; Preserves the scanned input in 10004
; Expects the return location in 10003
readinput:
LDC 10004,B
LDC 2000,A
; CHECK IF IT IS DIFFERENT THAN BEFORE
MVR A,C
MLC -1,C
ADR B,C
JZC change,C
JMC readinput
change:
STC 10004,A
LDC 10003,A
JMR A
