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

; Constantly scans for user input and puts it continuously on the screen
; also handles overflows by resetting the cursor to the top of the screen

; This sample shows features like:
; 1. Infinite loops
; 2. Reading from the keyboard
; 3. Resetting the scanned key so we can repeatedly type the same char
; 4. Mirroring the keyboard input on the screen on a continuously changing
;      location
; 5. Checking if the mirrored input would appear off screen


; This label is also used to reset the cursor, not just for starting the
; program the first time
entry:
; Remember to print out at the top of the screen
MVC 0,B

readinput:
; Mark the keyboard's memory address as non-read
; This is basically done by placing -1 in the memory there
MVC -1,A
STC 2000,A
realread:
; Read the last keystroke on the keyboard
LDC 2000,A
; Check if the last keystroke is still -1 or not
MVR A,C
ADC 1,C
JZC change,C
; It was not different, we need a re-read
JMC realread

; There was a new key press detected
change:
; print it out, where B tells us to print
STR B,A
; Move the cursor to the next point
ADC 1,B
; Check if the cursor would reach outside of the screen
MVR B,A
ADC -2000,A
JZC readinput,A
; The cursor is outside of the screen, we need to reset it to the top
JMC entry
; Here comes the unnecessary exit label as we have an infinite loop with
; those JZ/JMPs before. Unfortunately this is still needed as every uA
; program needs one.
exit:
JMC exit
