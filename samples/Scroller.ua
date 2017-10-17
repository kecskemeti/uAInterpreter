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

;An infinite scroller on the first line with asterisks

; This sample shows features like:
; 1. Creating busy waiting based delays for a somewhat precise timing.
;	     This enables us to have behaviour linked to the passing of time.
; 2. How to handle variables stored in memory
;        We use the memory address 10000 for our first variable 
;          - this will be used to see where should we put the asterisk next
;               -  1 = to the right of the current pos
;               - -1 = to the left of the current pos
; 3. Setting up the initial conditions, and initialising our variables.
;        See actions directly following the "entry" label.
; 4. How to check if a value is negative or positive 
;        See the operations around the label "posDir".
; 5. How to ensure the key information is kept unchanged while doing other 
;        functions:
;          - Register D is the location of the asterisk, and is not used for
;                any other purpose. From a higher level language's perspective
;                this would mean a variable (e.g., called "location") was 
;                pinned to a register. Notice that changes on D are only done
;                in setup and repaint phases.
;          - The other variable of ours (direction) does not have its own
;                register, it is constantly saved and loaded from memory.
;                See operations like LDC/STC 10000,x .

CONNR delay 100000
entry:
; Setup
; Location of the previous asterisk
MVC 0,D
; our direction - increase
MVC 1,A
; we will store our direction at 10000
STC 10000,A

repaint:
; The basic scroller  
; space
MVC 32,C
STR D,C 
LDC 10000,C
ADR C,D
; asterisk
MVC 42,C
STR D,C

; Checking if the direction is still correct

LDC 10000,C
ADC 1,C
JZC posDir,C
; We were decreasing the value of D
; Have we reached the front of the line?
JZC dodelay,D
; Yes we did, change direction!
JMC dirChange
posDir:
; We are increasing the value of D
MVR D,C
ADC -79,C
; Have we reached the end of the line?
JZC dodelay,C

; Changing directions

dirChange:
LDC 10000,C
MLC -1,C
STC 10000,C

; Delaying the next scrolling
; Does busy waiting (as there is no chance to use a timer)
dodelay:
LDC delay,A
indelay:
MVR A,B
ADC -1,B
MVR B,A
JZC indelay, A
JMC repaint

exit:
JMC exit