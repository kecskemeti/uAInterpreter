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

; Calculates factorials for any input in the range of 1-9
; This sample shows features like:
; 1. A demonstration of function calls using solely the stack
;    a) Register D is used as a stack pointer
;    b) the whole variable memory space is used for the stack
; 2. pass function parameters (numbers or strings) on the stack
; 3. receive return values from functions via the stack
; 4. recursive "functions" in uA
; 5. pass the return program counter on the stack
; 6. how to print out numbers with more than one digit

CONST prompt Please enter a number in the range of 1<=x<=9, we will calculate its factorial
CONNR promptTerminal 0


entry:
; Set the bottom of the stack:
MVC 9999,D
; Push the return value for printAt
MVC afterprompt,A
ADC 1,D
STR D,A
; Push the string loc
MVC prompt,A
ADC 1,D
STR D,A
; Push the Y coord
MVC 0,A
ADC 1,D
STR D,A
; Push the X coord (the same as Y)
ADC 1,D
STR D,A
JMC printAt
afterprompt:
; Push the return value for readinput
MVC afterinput,A
ADC 1,D
STR D,A
; Push the initial keyboard input to the stack (1st param to readinput)
LDC 2000,A
ADC 1,D
STR D,A
; Ready for the call
JMC readinput
afterinput:
; an exclamation mark to the 2nd char 2nd line to show the factorial
MVC 33,A
STC 81,A
; We will have the last key pressed on the stack
LDR D,A
ADC -1,D
; Let's put it on the screen (1st char 2nd line)
STC 80,A
; Let's transform it to an actual number
ADC -48,A
; Where do we want factorial to return?
MVC afterfact,B
ADC 1,D
STR D,B
; Now we are ready to send the input to factorial
ADC 1,D
STR D,A
JMC fact
afterfact:
; An equals sign in the 3rd char of the 2nd line
MVC 61,A
STC 82,A
; Let's load the actual factorial to A
LDR D,A
ADC -1,D
; Push the return address for our last function call
MVC exit,B
ADC 1,D
STR D,B
; Tell the numprinter function to print from the 4th char of the 2nd line
; Push the loc
MVC 83,B
ADC 1,D
STR D,B
; Push the actual factorial value
ADC 1,D
STR D,A
JMC printNum
exit:
JMC exit


; The actual factorial function
; Stack structure:
;  On function call:
;    D   = the number to have the factorial for
;    D-1 = the return address
;  On return:
;    D   = the factorial for the above number  
fact:
; pop the number for which we need the factorial
LDR D,A
ADC -1,D
MVR A,B
ADC -1,B
JZC notone,B
; factorial of one - is the same - it is in A already
; pop the return address
LDR D,B
ADC -1,D
; push the solution to the stack
ADC 1,D
STR D,A
; Jump where we were told
JMR B
notone:
; Let's save A so we can multiply by it once we receive the response
; we will push it to the stack
ADC 1,D
STR D,A
; We will make sure we know where to return
MVC intermediate,A
ADC 1,D
STR D,A
; B holds the reduced number, for which we need the factorial
; Thus we will ask our factorial function to use this as input
; Let's push B as the factorial in question that will be popped 
;   in fact's first lines
ADC 1,D
STR D,B
; Now we are ready to further calculate the factorial
JMC fact
intermediate:
; After the return we will see the partial factorial on the top of the stack
LDR D,B
ADC -1,D
; We need to restore A that we saved before the call
LDR D,A
ADC -1,D
; Now we are ready to do the final multiplication
MLR B,A
; We need the return address (pop it from the stack)
LDR D,B
ADC -1,D
; We push the final solution to the stack
ADC 1,D
STR D,A
JMR B


; Keyboard input
;  Uses the same approach as we seen in scaninput.ua
;  It uses the stack instead of static memory
;  the previous input is sent on the stack
; Stack structure:
;  On function call:
;    D   = the previous input
;    D-1 = the return address 
;  On return:
;    D   = the new input  
readinput:
LDR D,B
ADC -1,D
inputloop:
LDC 2000,A
; CHECK IF IT IS DIFFERENT THAN BEFORE
MLC -1,B
ADR A,B
JZC change,B
; No it is not different we keep the 'previous' value in B
MVR A,B
JMC inputloop
change:
; We have a useful input
; Load where to return (pop)
LDR D,B
ADC -1,D
; Push the new input to the stack
ADC 1,D
STR D,A
JMR B


; Copy of the printAt function from the hello world example
; Transformed to handle its input using the stack
; Stack structure:
;  On function call:
;    D   = x coord
;	 D-1 = y coord
;    D-2 = the address of the first char in the string
;    D-3 = the return address
;  On return: - 
printAt:
; X coord
LDR D,A
ADC -1,D
; Y coord
LDR D,B
ADC -1,D
MLC 80,B
; B is now linear loc on screen
ADR A,B
; Address of the string
LDR D,A
ADC -1,D
strloop:
; C the curr char to be printed
LDR A, C
STR B, C
ADC 1,A
ADC 1,B
; Looking for the zero at the end of the string
JZC strloop, C
; We found the zero, terminate the printout
; Pop the return address
LDR D,A
ADC -1,D
; Return where we were told so
JMR A


; A function to print larger (non single-digit) numbers
; This function always prints as if the number would have 9 digits
; Only supports positive numbers!
; Stack structure:
;  On function call:
;    D   = the number to print
;    D-1 = the place to print it (screen address)
;    D-2 = the return address
;  On return: - 
printNum:
; pop the number
LDR D,A
ADC -1,D
; pop the target loc
LDR D,B
ADC -1,D
; loc for the first digit
ADC 9,B
hasMoreDigits:
; The actual calculation for the last digit of A
MVR A,C
DVC 10,C
MLC -10,C
ADR A,C
; C is the last digit, now let's convert it to ASCII
ADC 48,C
; Print it to the screen
STR B,C
; Move our loc one digit to the front
ADC -1,B
DVC 10,A
JZC hasMoreDigits,A
; No more digits we are good to go
LDR D,A
ADC -1,D
JMR A