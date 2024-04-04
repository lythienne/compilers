.data
	maxPrompt: .asciiz "Find the max of three numbers, enter one at a time below:\n"
	maxResult: .asciiz "max: "
	factPrompt: .asciiz "\n\nn!, enter n:\n" 
	factResult: .asciiz "n! = "
	fibPrompt: .asciiz "\n\nFind nth value of the fibonacci sequence, enter n:\n" 
	fibResult: .asciiz "fib(n) = "
.text 
.globl main
main:
# Max of 3 numbers
	li $v0 4
	la $a0 maxPrompt
	syscall			#print prompt
	
	li $v0 5
	syscall			#ask for number 1
	move $a0 $v0	
	
	li $v0 5
	syscall			#ask for number 2
	move $a1 $v0
	
	li $v0 5
	syscall			#ask for number 3
	move $a2 $v0
	
	jal max3		#find max
	move $t0 $v0		#store max in t0
	
	li $v0 4
	la $a0 maxResult
	syscall			#print result msg
	
	move $a0 $t0
	li $v0 1
	syscall			#print max

# Factorial
	li $v0 4
	la $a0 factPrompt
	syscall			#print prompt
	
	li $v0 5
	syscall			#ask for n
	move $a0 $v0	
	
	jal fact		#find n!
	move $t0 $v0		#store n! in t0
	
	li $v0 4
	la $a0 factResult
	syscall			#print result msg
	
	move $a0 $t0
	li $v0 1		
	syscall			#print n!

# Fibonacci
	li $v0 4
	la $a0 fibPrompt
	syscall			#print prompt
	
	li $v0 5
	syscall			#ask for n
	move $a0 $v0

	jal fib			#find the nth value of the fibonacci sequence (@ n=1,2 fib(n)=1)
	move $t0 $v0		#store value in t0
	
	li $v0 4
	la $a0 fibResult
	syscall			#print result msg
	
	move $a0 $t0
	li $v0 1
	syscall			#print value
	
	li $v0 10
	syscall

# Fibonacci subroutine finds the nth number in the fibonacci sequence where at n=1,2 fib(n)=1
# @param n is in $a0
# @return value in $v0
fib:
	bgt $a0 2 moreFib	#base case n<2
	li $v0 1		#then fib(n)=1
	jr $ra
moreFib:
	subu $sp $sp 4
	sw $ra ($sp)		#push return address on stack
	subu $sp $sp 4
	sw $a0 ($sp)		#push n on stack
	subu $a0 $a0 1
	jal fib			#calculate fib(n-1)
	lw $a0 ($sp)		#pop n off stack
	sw $v0 ($sp)		#push fib(n-1) on stack
	subu $a0 $a0 2
	jal fib			#calculate fib(n-2)
	lw $t0 ($sp)		#pop fib(n-1) to t0
	addu $sp $sp 4
	addu $v0 $t0 $v0	#add fib(n-1) and f(n-2)
	lw $ra ($sp)		#pop return address off stack
	addu $sp $sp 4	
	jr $ra			#return

# Factorial subroutine finds n!
# @param n is in $a0
# @return n! is in $v0
fact:
	bgtz $a0 moreFact	#base case 0! = 1
	li $v0 1
	jr $ra
moreFact:
	subu $sp $sp 4
	sw $ra ($sp)		#push return address on stack
	subu $sp $sp 4
	sw $a0 ($sp)		#push current n on stack
	subu $a0 $a0 1
	jal fact		#calculate (n-1)!
	lw $t0 ($sp)		#pop n off stack
	addu $sp $sp 4
	mult $t0 $v0		#multiply n(n-1)!
	mflo $v0
	lw $ra ($sp)		#pop return address off stack
	addu $sp $sp 4
	jr $ra			#return

# Max3 subroutine finds the max of three numbers
# @param numbers are in $a0, $a1, $a2
# @return max is in $v0
max3:
	subu $sp $sp 4
	sw $ra ($sp)		#push return address on stack
	jal max2		#calc max of a0 and a1
	move $a0 $v0
	move $a1 $a2
	jal max2		#calc max of the previous max and a2
	lw $ra ($sp)		#pop return address off stack
	addu $sp $sp 4
	jr $ra			#return
# Max2 subroutine finds the max of two numbers
# @param numbers are in $a0, $a1
# @return max is in $v0
max2:
	bgt $a0 $a1 a0Greater	#if a0 is greater jump to a0Greater
	move $v0 $a1		#otherwise return a1
	jr $ra
a0Greater:
	move $v0 $a0		#return a0
	jr $ra	
