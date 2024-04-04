# Plays among us drip (sometimes twitchy bc rest timings not consistent but whatever)
#
# author: Harrison Chen
# version: 11/7/23
.data
	quarter: .word 0
	half: .word 0
	whole: .word 0
	eighth: .word 0
	sixteenth: .word 0
	dotQuarter: .word 0
	triplet: .word 0
.text
# Sets up the song, asking for the tempo, then storing the correct lengths of various
# notes, iniitalizes the loop count to 1
setUpSong:
	jal getTempo
	jal getQuarter
	sw $v0 quarter
	jal getHalf
	sw $v0 half
	jal getWhole
	sw $v0 whole
	jal getEighth
	sw $v0 eighth
	jal getSixteenth
	sw $v0 sixteenth
	jal getDotQuarter
	sw $v0 dotQuarter
	jal getTriplet
	sw $v0 triplet
	li $t9 1
# Loops the song 4 times
loop:
	bge $t9 4 end
	jal playAmongUs
	addi $t9 $t9 1
	j loop
# Plays the song (I read the sheet music, it was annoying)
playAmongUs:
	move $s7 $ra
	
	lw $t8 eighth
	move $a1 $t8
	li $a0 6
	jal playC
	move $a0 $t8
	jal rest
	move $a1 $t8
	li $a0 6
	jal playDs
	move $a0 $t8
	jal rest
	move $a1 $t8
	li $a0 6
	jal playF
	move $a0 $t8
	jal rest
	move $a1 $t8
	li $a0 6
	jal playFs
	move $a0 $t8
	jal rest
	move $a1 $t8
	li $a0 6
	jal playF
	move $a0 $t8
	jal rest
	move $a1 $t8
	li $a0 6
	jal playDs
	move $a0 $t8
	jal rest
	
	lw $t8 dotQuarter
	move $a1 $t8
	li $a0 6
	jal playC
	move $a0 $t8
	jal rest
	lw $t8 sixteenth
	move $a1 $t8
	li $a0 5
	jal playAs
	move $a0 $t8
	jal rest
	move $a1 $t8
	li $a0 6
	jal playD
	move $a0 $t8
	jal rest
	lw $t8 quarter
	move $a1 $t8
	li $a0 6
	jal playC
	lw $t8 dotQuarter
	move $a0 $t8
	jal rest
	lw $t8 eighth
	move $a1 $t8
	li $a0 5
	jal playG
	move $a0 $t8
	jal rest
	
	
	lw $t8 quarter
	move $a1 $t8
	li $a0 6
	jal playC
	move $a0 $t8
	jal rest
	lw $t8 eighth
	move $a1 $t8
	li $a0 6
	jal playC
	move $a0 $t8
	jal rest
	move $a1 $t8
	li $a0 6
	jal playDs
	move $a0 $t8
	jal rest
	move $a1 $t8
	li $a0 6
	jal playF
	move $a0 $t8
	jal rest
	move $a1 $t8
	li $a0 6
	jal playFs
	move $a0 $t8
	jal rest
	move $a1 $t8
	li $a0 6
	jal playF
	move $a0 $t8
	jal rest
	move $a1 $t8
	li $a0 6
	jal playDs
	move $a0 $t8
	jal rest
	
	lw $t8 dotQuarter
	move $a1 $t8
	li $a0 6
	jal playFs
	lw $a0 half
	jal rest
	lw $t8 triplet
	move $a1 $t8
	li $a0 6
	jal playFs
	move $a0 $t8
	jal rest
	move $a1 $t8
	li $a0 6
	jal playF
	move $a0 $t8
	jal rest
	move $a1 $t8
	li $a0 6
	jal playDs
	move $a0 $t8
	jal rest
	move $a1 $t8
	li $a0 6
	jal playFs
	move $a0 $t8
	jal rest
	move $a1 $t8
	li $a0 6
	jal playF
	move $a0 $t8
	jal rest
	move $a1 $t8
	li $a0 6
	jal playDs
	move $a0 $t8
	jal rest
	
	lw $t8 quarter
	move $a1 $t8
	li $a0 6
	jal playC
	move $a0 $t8
	jal rest
	
	jr $s7
end:
	li $v0 10
	syscall
