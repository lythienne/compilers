# Gets 10 numbers as input, prints the sum, average, max, and min, uses an array
#
# author: Harrison Chen
# version: 11/13/23
.data
	arr: .space 40
	length: .word 10
	sum: .asciiz "sum: "
	avg: .asciiz ", average: "
	min: .asciiz ", min: "
	max: .asciiz ", max: "
	pt: .asciiz "."
.text

	lw $t1 length
	li $t2 4
	mult $t1 $t2
	mflo $t9			#t9 space in array

# Gets input from the user, storing it in the array and incrementing the pointer by 4
	move $t0 $zero			#t0 pointer
getNums:
	li $v0 5
	syscall
	sw $v0 arr($t0)
	addi $t0 $t0 4		
	blt $t0 $t9 getNums

# Accesses numbers in array, adding them and incrementing the pointer by 4, and printing the sum
	move $t0 $zero			#t0 pointer
	move $t1 $zero			#t1 sum
addNums:
	lw $t2 arr($t0)
	add $t1 $t1 $t2
	addi $t0 $t0 4
	blt $t0 $t9 addNums
	
	li $v0 4
	la $a0 sum
	syscall				#print sum msg
	
	li $v0 1
	move $a0 $t1
	syscall				#print sum

# Calculates the average and prints it
average:
	lw $t0 length
	div $t1 $t0
	mflo $t1			#calc avg
	mfhi $t2
	
	li $v0 4
	la $a0 avg
	syscall				#print avg msg
	
	li $v0 1
	move $a0 $t1
	syscall				#print avg
	li $v0 4
	la $a0 pt
	syscall				#print .
	li $v0 1
	move $a0 $t2
	syscall				#print decimal

# Finds the min
	move $t0 $zero			#t0 pointer
	lw $t1 arr($zero)		#first val/min
	addi $t0 $t0 4
getMin:
	lw $t2 arr($t0)
	bgt $t2 $t1 minLoop
	move $t1 $t2
minLoop:
	addi $t0 $t0 4
	blt $t0 $t9 getMin
	
	li $v0 4
	la $a0 min
	syscall				#print min msg
	
	li $v0 1
	move $a0 $t1
	syscall				#print min
	
# Finds the max
	move $t0 $zero			#t0 pointer
	lw $t1 arr($zero)		#first val/max
	addi $t0 $t0 4
getMax:
	lw $t2 arr($t0)
	blt $t2 $t1 maxLoop
	move $t1 $t2
maxLoop:
	addi $t0 $t0 4
	blt $t0 $t9 getMax
	
	li $v0 4
	la $a0 max
	syscall				#print max msg
	
	li $v0 1
	move $a0 $t1
	syscall				#print max