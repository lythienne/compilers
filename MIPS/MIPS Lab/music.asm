# Plays among us drip by going through an array of pitches (-1 is rest) and an array of durations
#
# author: Harrison
# version: 11/9/23

.data

# hardcoded, -1 is rest
pitches:   .word 60, 63, 65, 66, 65, 63, 60, 58, 62, 60, -1, 43, 48, 60, 63, 65, 66, 65, 63, 66, -1, 66, 65, 63, 66, 65, 63

# 1 is sixteenth, the rest are multiples of sixteenth, 0 is triplet
durations: .word 2,  2,  2,  2,  2,  2,  6,  1,  1,  4,  2,  2,  4,  2,  2,  2,  2,  2,  2,  6,  2,  0,  0,  0,  0,  0,  0

length:	   .word 27 #length of arrays

.text

main:
	li $t3 60		#duration of sixteenth note
	li $a2 5		#instrument
	li $a3 127		#volume
	
	la $t0 pitches
	la $t1 durations
	move $t2 $zero		#notes played
	
loop:
	li $a3 127		#reset volume
	
	lw $t4 length 
	beq $t2 $t4 end		#if played all pitches, end

	lw $a0 ($t0)		#$a0 is pitch
	lw $a1 ($t1)		#$a1 is duration
	beqz $a1 triplet
	mult $a1, $t3		#multiply by duration of sixteenth

play:
	mflo $a1		#load true duration into $a1
	
	bltzal $a0, rest	#if the pitch is -1, jump to rest
	
	li $v0, 33 		# syscall 33 = synchronous MIDI output
	syscall

	add $t0, $t0, 4		#move to next position in arrays
	add $t1, $t1, 4
	
	add $t2, $t2, 1 	#add 1 to pitches played
	
	j loop

rest:
	li $a3 0 		#set volume to 0
	jr $ra

end:
	li $v0, 10
	syscall

triplet:
	li $t4 4
	mult $t3 $t4
	mflo $a1
	li $t4 3
	div $a1 $t4
	mflo $a1
	j play