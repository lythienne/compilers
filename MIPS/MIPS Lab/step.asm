#Prints a range of numbers by a step from a lower value to an upper value
#@author Harrison Chen
#@version 10/27/23

.data
	msg: .asciiz "Prints all numbers in a range by step.\ninput the low and high ends of the range and the step:\n"
	space: .asciiz " "
	low: .word 0
	high: .word 0
	step: .word 0
.text
	li $v0 4
	la $a0 msg
	syscall		#prints msg
	
	li $v0 5
	syscall		#get user input
	sw $v0 low	#stores the first number into low
	li $v0 5
	syscall		#get user input
	sw $v0 high	#stores the second number into high
	li $v0 5
	syscall		#get user input
	sw $v0 step	#stores the third number into step
	
	lw $t0 low	
	lw $t1 high
	lw $t2 step	#loads low, high, and step into temporary registers
	loop:
	bgt $t0 $t1 end	#if low > high then end the loop
	li $v0 1	
	move $a0 $t0
	syscall		#otherwise print the current number
	li $v0 4
	la $a0 space	#and a space
	syscall
	add $t0 $t0 $t2	#then increment the number by step
	j loop		#and return to the beginning of the loop
	end:
	
	j end