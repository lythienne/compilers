# This program prompts the user to enter two numbers in the range
# [1000 - 5000] inclusive. Once the user enters the two values successfully
# it prints the sum of the two numbers
#
# @author	Harrison Chen
# @version 	11/17/23
	
	
	.data

msg: 	.asciiz		"Enter a number between 1000 and 5000"
newline: .asciiz 	"\n"

	.text

	.globl main

main:
	move $t1 $zero
	li $t8 1000		#range lower bound
	li $t9 5000		#range upper bound

prompt1:
	li $v0 4
	la $a0 msg
	syscall			#print msg
	
	li $v0 4
	la $a0 newline
	syscall			#print msg
	
	li $v0 5
	syscall
	move $t0 $v0		#prompt user for number 1
	
	blt $t0 $t8 prompt1	#prompt again if less than range
	bgt $t0 $t9 prompt1	#prompt again if greater than range
	
	beqz $t1 prompt2
	j after
prompt2:
	move $t1 $t0		#move first number to t1
	j prompt1

after: 				#number 2 in t0, number 1 in t1
	add $a0 $t1 $t0
	li $v0 1
	syscall

	li $v0, 10
	syscall
	

	

