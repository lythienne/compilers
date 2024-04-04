# Prompts the user for their name then print's Hello user name
# 
# @author Harrison Chen
# @version 11/17/23s

	.data
	
msg:	.asciiz		"Enter your name:"	# prompt for the user to enter name
newline: .asciiz		"\n"
hello:	.asciiz 	"Hello "		# used to display Hello user name
name:	.space		20			# space to save the name in memory
len:	.word		20			# max characters allowed in name


	.text

	.globl main

main:

	# Your code goes here
	
	li $v0 4
	la $a0 msg
	syscall			#print msg
	
	li $v0 4
	la $a0 newline
	syscall			#print newline
	
	li $v0 8
	lw $a1 len
	la $a0 name
	syscall			#prompt user for name, store in name, maximum length given by len
	
	li $v0 4
	la $a0 newline
	syscall			#print newline
	
	li $v0 4
	la $a0 hello	
	syscall			#print hello
	
	li $v0 4
	la $a0 name
	syscall			#print name

	# Normal termination
	li $v0, 10
	syscall
