#Checks if a number is even or odd
#@author Harrison Chen
#@version 10/27/23

.data
	msg: .asciiz "check if number is even or odd\n"
	oddmsg: .asciiz " is odd."
	evenmsg: .asciiz " is even."
	num: .word 0
.text
.globl end
	
	li $v0 4	
	la $a0 msg
	syscall		#print the message
	
	li $v0 5
	syscall		#read user input
	sw $v0 num 	#store user input in num
	
	lw $t0 num	#put number in t0
	li $s0 2	
	div $t0 $s0	#divide number by 2
	mfhi $t1	#store the remainder in t1
	
	li $v0 1
	lw $a0 num
	syscall		#prints the number
	li $v0 4	
	beq $t1 1 odd	#if remainder is 1 goto odd
	la $a0 evenmsg 	#else set argument to "is even"
	j after
	odd:
	la $a0 oddmsg	#set argument to "is odd"
	after:
	syscall		#print the argument

end:
	li $v0 10
	syscall
