#Multiplies two numbers
#@author Harrison Chen
#@version 10/27/23
.data
	msg: .asciiz "input a number:\n"
	times: .asciiz " times "
	equals: .asciiz " equals "

.text
	li $v0 4
	la $a0 msg 
	syscall		#print message asking for number
	
	li $v0 5
	syscall		#read user input
	move $t0 $v0
	
	li $v0 4
	la $a0 msg
	syscall		#print message asking for number
	
	li $v0 5
	syscall		#read user input
	move $t1 $v0

	mult $t0 $t1	#multiplies the two numbers
	mflo $t2	#sets the result to $t2
	
	li $v0 1
	move $a0 $t0
	syscall		#prints first number
	li $v0 4
	la $a0 times
	syscall		#print times
	li $v0 1
	move $a0 $t1
	syscall		#prints second number
	li $v0 4
	la $a0 equals
	syscall		#prints equals
	li $v0 1
	move $a0 $t2
	syscall		#prints the result

	j end
