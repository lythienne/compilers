# MIPS 32 Assembly Code generated by a Compiler
# Disclaimer: Autogenerated code to be run in the MARS Simulator DO NOT EDIT
# @author Harrison Chen
# @version 1/7/24
# (backwards smiley for emitter) (:
	.data
	pgm_newline: .asciiz "\n"
	pgm_true: .asciiz "true"
	pgm_false: .asciiz "false"
	pgm_notABooleanError: .asciiz "ERROR: Inputted text is not a boolean"
	.text
	.globl main
	j main

# PROCEDURE/FUNCTION DECLARATIONS:
PROC_TESTFOR:
	subi $sp $sp 4
	sw $ra ($sp)	#push $ra onto stack
	# For 1
	li $v0 1
	subi $sp $sp 4
	sw $v0 ($sp)	#push $v0 onto stack
	li $v0 10
	subi $sp $sp 4
	sw $v0 ($sp)	#push $v0 onto stack
for1:
	lw $t0 ($sp)
	addi $sp $sp 4	#pop from stack to $t0
	lw $v0 8($sp)
	bgt $v0 $t0 afterFor1
	subi $sp $sp 4
	sw $t0 ($sp)	#push $t0 onto stack
	# For 2
	li $v0 1
	subi $sp $sp 4
	sw $v0 ($sp)	#push $v0 onto stack
	li $v0 10
	subi $sp $sp 4
	sw $v0 ($sp)	#push $v0 onto stack
for2:
	lw $t0 ($sp)
	addi $sp $sp 4	#pop from stack to $t0
	lw $v0 16($sp)
	bgt $v0 $t0 afterFor2
	subi $sp $sp 4
	sw $t0 ($sp)	#push $t0 onto stack
	# Write(ln)
	lw $v0 20($sp)
	subi $sp $sp 4
	sw $v0 ($sp)	#push $v0 onto stack
	lw $v0 24($sp)
	lw $t0 ($sp)
	addi $sp $sp 4	#pop from stack to $t0
	# BinOp
	mult $t0 $v0
	mflo $v0	#multiplying
	move $a0 $v0
	li $v0 1
	syscall
	li $v0 4
	la $a0 pgm_newline
	syscall
	lw $v0 20($sp)
	addi $v0 $v0 1
	sw $v0 20($sp)
	j for2
afterFor2:
	lw $t0 ($sp)
	addi $sp $sp 4	#pop from stack to $t0
	lw $v0 16($sp)
	addi $v0 $v0 1
	sw $v0 16($sp)
	j for1
afterFor1:
	lw $t0 ($sp)
	addi $sp $sp 4	#pop from stack to $t0
	lw $ra ($sp)
	addi $sp $sp 4	#pop from stack to $ra
	jr $ra
main:
	jal PROC_TESTFOR
	li $v0 10
	syscall
	

	# boolean.toString, input($v0): 0=false, -1=true, output($v0): str address
pgm_boolToString:
	beqz $v0 pgm_printFalse
	la $v0 pgm_true
	jr $ra
pgm_printFalse:
	la $v0 pgm_false
jr $ra
	# subroutine to find length of string stored in $a0, returns as word in $v0
	move $v0 $zero
pgm_strLength:
	lb $t0 ($a0)
pgm_strLengthLoop:
	beqz $t0 pgm_strLengthAfter
	addi $v0 $v0 1
	addi $a0 $a0 1
	lb $t0 ($a0)
	j pgm_strLengthLoop
pgm_strLengthAfter:
	
	jr $ra
