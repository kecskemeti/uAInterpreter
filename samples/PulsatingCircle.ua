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
;  (C) Copyright 2019, Gabor Kecskemeti (g.kecskemeti@ljmu.ac.uk)
;

; Draws ever bigger then ever smaller circles on the screen and removes the previous ones
; This has an effect of a constantly growing and reducing circle in the middle of the screen
 
; This sample shows features like:
; 1. Infinite & conditional terminated loops
; 2. Subtraction with the help of multiplication (see MLC -1,C)
; 3. Updating the full character screen according to a math equation
; 4. Handling floating point errors
; 5. Smaller than operation
; 6. Creating busy waiting based delays for a somewhat precise timing.
;	     This enables us to have behaviour linked to the passing of time.
;        The delay for the busy waiting loop should be updated for every machine this program runs to get perfect timing.
; 7. How to handle temporaries stored in memory
; 8. Uses the following variable mapping:
;      - radius of the current circle -> 10000
;      - radius direction             -> 10001
;      - temporary                    -> 29999


; Our constants
; Radius range
CONNR minradius  1
CONNR maxradius  11
; Where all circles will be centered (centre of the screen) 
CONNR centrex 39
CONNR centrey 12
; Acceptable error for the calculations (because our use of integers would not allow sufficient details on the circles otherwise)
CONNR accept  2
; How long do we wait till we draw the next cicle.
CONNR delay 800000

;THE PROGRAM STARTS HERE
entry:
; Let's set up our variables, first, the current radius to the minimum
LDC minradius,C
STC 10000,C
; Let's remember that we will grow the circle first
MVC 1,C
STC 10001,C

; We can now go to the beginning of the screen drawing procedure
screenreset:
; A  will store the X coordinate on the screen (which column?)
MVC 0,A
; B  will store the Y coordinate on the screen (which row?)
MVC 0,B
; These coordinates will be constantly changed and tested against the circle equation, so we keep this data in the registers all the time

; From now on we are drawing the screen:
inscreen:

; Left side of circle equation starts

; Calculating (X-A)^2 and storing it in the register C
LDC centrex,C
MLC -1,C
ADR A,C
MLR C,C

; Calculating (Y-B)^2 and storing it in the register D
LDC centrey,D
MLC -1,D
ADR B,D
MLR D,D

; Finalizing the calculation of the left side of the circle equation:
; (X - A)^2 + (Y - B)^2 , the result is in C
ADR D,C

; Left side of circle equation done, lets have a look at the right side

; We will put it to do (R^2 will be calculated there)
LDC 10000,D
MLR D,D

; The righs side of the equation is now in D

; Comparing the two sides
MLC -1,D
; The result could have some slight "error" as we are not working with floats
ADR C,D
JZC nonperfectpoint,D
thecircle:
; We will plot every point on the circle with an asterisk
MVC 42,D
; We can draw on the screen right away
JMC updatescreen

; The current coordinates are not exactly on the circle, but they might be close. Let's test this.
nonperfectpoint:
; We have seen how close are we on the circle equation. It was stored in D. 
; But we will need D for another business. Let's store it in a temporary.
STC 29999,D

; Load the acceptable error and adjust it with the radius.
;  Why adjust? With a constant acceptable error in the equation, we would see too much
;   banding around the smaller circles, or too few pixels would show on larger ones.
LDC accept,C
LDC 10000,D
MLR D,C
; Now we multiply the acceptance range by 1.33 this will provide a nice thin circle at the end
MLC 4,C
DVC 3,C

; Now that we have finished our business with D, we can reload its previous contents from temp memory.
LDC 29999,D
; Now we just need to see if the actual error (which we have just restored in D) is below the
;  acceptable that we have calculated in C.
; We will do a lower than conditional here by decreasing both of our inputs till one of them reaches 0 
; Note: Editor.ua has a different implementation for this problem.
allowmoreerror:
ADC -1,C
ADC -1,D
JZC stillnotperfect,D
; D reached 0 earlier, it is smaller. So the actual error is within limts, we can draw the circle still.
JMC thecircle
stillnotperfect:
; D is not yet 0, but the acceptable error in C could turn 0 as well (when D is bigger). Did it?
JZC allowmoreerror,C
; Yes it did, let's clear the location with a space
MVC 32,D
JMC updatescreen


; This "lightweight method" will receive its input in A,B,D.
;  In A and B we will have the coordinates of to be printed character.
;  The character itself will be received in D
updatescreen:
; Now we can determine where are we in the screen memory based on the screen coordinates (in A and B)
MVR B,C
; We have 80 columns in a row, so rowcount is adjusted with this multiplier
MLC 80,C
; We can now add the current column to get the linear address of the currently updated pixel on the screen. This will be stored in C
ADR A,C
; We can write to the calculated memory address (C) whatever we have received in D
STR C,D

; We can now move on the rest of the screen, this will update A and B and allows us to scan the whole screen for possible circle parts.
updatecoords:

; We will move one character to the right first
ADC 1,A

; Is it out of the screen already to the right?
MVR A,C
ADC -80,C
JZC inscreen,C
; Yes, we are out. We move to the next row (towards the bottom), and start the row from its left side.
ADC 1,B
MVC 0,A

; Is it out of the screen already to the bottom?
MVR B,C
ADC -25,C
JZC inscreen,C
; Yes it is out of the screen, we have finished with this circle, let's wait a bit so we don't overwrite it too frequently 
JMC dodelay

; Does busy waiting as uA's machine does not have a timer
; It simply counts down to 0 from the delay constant. In the meantime it does some pointless operations to kill some extra time.
dodelay:
LDC delay,A
indelay:
MVR A,B
ADC -1,B
MVR B,A
JZC indelay, A
; Delay completed, we need to update the radius of the circle and we can then start a new drawing
JMC radiuschange


; We need to change the radius in 10000 according to 10001 (direction)
radiuschange:

; Load our two variables so we have them in the registers again
LDC 10000,C
LDC 10001,D
ADR D,C
; Our radius is now updated, we save it in memory
STC 10000,C
; Let's check if this is the minimum acceptable radius already
LDC minradius,D
MLC -1,D
ADR D,C
JZC testfortoobig,C
; Yes it is, we need to change the direction we are heading

; dirchange reverses the radius' direction by multiplying with -1
; this will effectively turn a growing circle into a shrinking one and a shrinking one into a growing one 
dirchange:
LDC 10001,D
MLC -1,D
STC 10001,D
; direction change complete, we can draw the next circle
JMC screenreset

; The radius was not matching the minimum acceptable, but it could still match the maximum
testfortoobig:
LDC 10000,C
LDC maxradius,D
MLC -1,D
ADR D,C
; have we reached the maximum?
JZC screenreset,C
; Yes we did, we need to change directions
JMC dirchange


; As we have an infinite loop, this is unused, but still needed as all uA programs need an exit label
exit:
