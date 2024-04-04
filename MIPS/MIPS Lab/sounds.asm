# Sounds is a collection of subroutines for playing all notes from ~C0-C10 
# for some duration and rests
# Also has ability to ask for a tempo bpm and convert it into corresponding
# note lengths for quarter, half, whole, eighth, sixteenth, dotted quarter,
# triplet length notes (in milliseconds) and return these values
#
# author: Harrison Chen
# version: 11/7/23
.data
	msg: .asciiz "What tempo (bpm) should the song play at? "
	tempo: .word 100
	quarter: .word 0
	half: .word 0
	whole: .word 0
	eighth: .word 0
	sixteenth: .word 0
	dotQuarter: .word 0
	dotHalf: .word 0
	triplet: .word 0
.text

.globl getTempo		#tempo related subroutines
.globl convertTempo
.globl getQuarter
.globl getHalf
.globl getWhole
.globl getEighth
.globl getSixteenth
.globl getDotQuarter
.globl getDotHalf
.globl getTriplet

# Asks for a tempo in bpm from user, then converts and stores the lengths
# of various notes
getTempo:
	move $s0 $ra
	li $v0 4
	la $a0 msg
	syscall
	li $v0 5
	syscall
	sw $v0 tempo
	jal convertTempo
	jr $s0
# Converts and stores the lengths of various notes based on the stored
# tempo (default 100 bpm)
convertTempo:
	lw $t0 tempo
	li $t1 60000
	div $t1 $t0
	mflo $t0
	sw $t0 quarter
	li $t1 2
	mult $t0 $t1
	mflo $t2
	sw $t2 half
	mult $t2 $t1
	mflo $t2
	sw $t2 whole
	div $t0 $t1
	mflo $t2
	sw $t2 eighth
	div $t2 $t1
	mflo $t2
	sw $t2 sixteenth
	li $t1 3
	div $t0 $t1
	mflo $t2
	sw $t2 triplet
	
	lw $t0 eighth
	li $t1 3
	mult $t0 $t1
	mflo $t2
	sw $t2 dotQuarter
	lw $t0 quarter
	li $t1 3
	mult $t0 $t1
	mflo $t2
	sw $t2 dotHalf
	
	jr $ra

# Returns the length of a quarter note in milliseconds in $v0
getQuarter:
	lw $v0 quarter
	jr $ra
# Returns the length of a half note in milliseconds in $v0
getHalf:
	lw $v0 half
	jr $ra
# Returns the length of a whole note in milliseconds in $v0
getWhole:
	lw $v0 whole
	jr $ra
# Returns the length of a eighth note in milliseconds in $v0
getEighth:
	lw $v0 eighth
	jr $ra
# Returns the length of a sixteenth note in milliseconds in $v0
getSixteenth:
	lw $v0 sixteenth
	jr $ra
# Returns the length of a dotted quarter note in milliseconds in $v0
getDotQuarter:
	lw $v0 dotQuarter
	jr $ra
# Returns the length of a dotted half note in milliseconds in $v0
getDotHalf:
	lw $v0 dotHalf
	jr $ra
# Returns the length of one note in a triplet in milliseconds in $v0
getTriplet:
	lw $v0 triplet
	jr $ra

.globl playC 		#each method is globally visible, $a0 should be 
.globl playCs		#the octave of the note, $a1 the duration (in ms)
.globl playD
.globl playDs
.globl playE
.globl playF
.globl playFs
.globl playG
.globl playGs
.globl playA
.globl playAs
.globl playB
.globl rest

# Converts the inputted octave to the actual pitch used by MIPS.
#
# @param $a0 the number of semitones above C the pitch is (0-11)
# @param $a1 the inputted octave (C4 is middle C)
# @return stores the resulting pitch in $v0
#
# For example, C4 is middle C and has MIPS pitch 60, $a0 should
# be 0 (for C) and $a1 should be 4. The subroutine will add 1 to
# $a1, multiply the result by 12, then add $a0 to get 60
convertOctave:
	addi $a1 $a1 1
	li $t0 12
	mult $a1 $t0
	mflo $t0
	add $v0 $a0 $t0
	jr $ra
# Plays a given note for a given duration on a piano at volume 127
# @param $a0 the number of semitones above C the pitch is (0-11)
# @param $a1 the inputted octave (C4 is middle C)
# @param $a2 the duration of the note in milliseconds
playNote:
	move $s2 $ra
        jal convertOctave
        move $a0 $v0
	li $v0 31
	move $a1 $a2
        li $a2 0
        li $a3 127
        syscall
        jr $s2
# Rests (no sound) for a given duration
# @param $a0 the duration of the rest in milliseconds
rest:
	li $v0, 32
	syscall
	jr $ra
# Plays a C at a given octave for a given duration
# @param $a0 the inputted octave (C4 is middle C)
# @param $a1 the duration of the note in milliseconds
playC:
	move $s0 $ra
	move $a2 $a1
	move $a1 $a0
	li $a0 0
        jal playNote
        jr $s0
# Plays a C# at a given octave for a given duration
# @param $a0 the inputted octave
# @param $a1 the duration of the note in milliseconds
playCs:
	move $s0 $ra
	move $a2 $a1
	move $a1 $a0
	li $a0 1
        jal playNote
        jr $s0
# Plays a D at a given octave for a given duration
# @param $a0 the inputted octave
# @param $a1 the duration of the note in milliseconds
playD:
	move $s0 $ra
	move $a2 $a1
	move $a1 $a0
	li $a0 2
        jal playNote
        jr $s0
# Plays a D# at a given octave for a given duration
# @param $a0 the inputted octave
# @param $a1 the duration of the note in milliseconds
playDs:
	move $s0 $ra
	move $a2 $a1
	move $a1 $a0
	li $a0 3
        jal playNote
        jr $s0
# Plays an E at a given octave for a given duration
# @param $a0 the inputted octave
# @param $a1 the duration of the note in milliseconds
playE:
	move $s0 $ra
	move $a2 $a1
	move $a1 $a0
	li $a0 4
        jal playNote
        jr $s0
# Plays an F at a given octave for a given duration
# @param $a0 the inputted octave
# @param $a1 the duration of the note in milliseconds
playF:
	move $s0 $ra
	move $a2 $a1
	move $a1 $a0
	li $a0 5
        jal playNote
        jr $s0
# Plays an F# at a given octave for a given duration
# @param $a0 the inputted octave
# @param $a1 the duration of the note in milliseconds
playFs:
	move $s0 $ra
	move $a2 $a1
	move $a1 $a0
	li $a0 6
        jal playNote
        jr $s0
# Plays a G at a given octave for a given duration
# @param $a0 the inputted octave
# @param $a1 the duration of the note in milliseconds
playG:
	move $s0 $ra
	move $a2 $a1
	move $a1 $a0
	li $a0 7
        jal playNote
        jr $s0
# Plays a G# at a given octave for a given duration
# @param $a0 the inputted octave
# @param $a1 the duration of the note in milliseconds
playGs:
	move $s0 $ra
	move $a2 $a1
	move $a1 $a0
	li $a0 8
        jal playNote
        jr $s0
# Plays an A at a given octave for a given duration
# @param $a0 the inputted octave
# @param $a1 the duration of the note in milliseconds
playA:
	move $s0 $ra
	move $a2 $a1
	move $a1 $a0
	li $a0 9
        jal playNote
        jr $s0
# Plays an A# at a given octave for a given duration
# @param $a0 the inputted octave
# @param $a1 the duration of the note in milliseconds
playAs:
	move $s0 $ra
	move $a2 $a1
	move $a1 $a0
	li $a0 10
        jal playNote
        jr $s0
# Plays a B at a given octave for a given duration
# @param $a0 the inputted octave
# @param $a1 the duration of the note in milliseconds
playB:
	move $s0 $ra
	move $a2 $a1
	move $a1 $a0
	li $a0 11
        jal playNote
        jr $s0
