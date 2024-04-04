.data
	msg: .asciiz "hi"
	num: .word 0
.text 
.globl	printMsg


printMsg:
	li $v0 4
	la $a0 msg
	syscall
	li $v0 1
	lw $a0 num
	syscall
	li $t0 123456
	sw $t0 num
	jr $ra
