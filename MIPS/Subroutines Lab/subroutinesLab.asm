# Collection of all the mini programs written using recursive subroutines
# Includes: max of 3 numbers, factorial, fibonacci, and the sum of a linked list
# All input is from the user
#
# author: Harrison Chen
# version: 11/5/23
.data
	maxPrompt: .asciiz "Find the max of three numbers, enter one at a time below:\n"
	maxResult: .asciiz "max: "
	factPrompt: .asciiz "\n\nn!, enter n:\n" 
	factResult: .asciiz "n! = "
	fibPrompt: .asciiz "\n\nFind nth value of the fibonacci sequence, enter n:\n" 
	fibResult: .asciiz "fib(n) = "
	llNumNodesPrompt: .asciiz "\n\nHow many values in list:\n" 
	llValuePrompt1: .asciiz "List value number " 
	llValuePrompt2: .asciiz " : " 
	llResult: .asciiz "\nsum of list = "
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
	
# LinkedList
	li $v0 4
	la $a0 llNumNodesPrompt #prompt user for length of list
	syscall
	li $v0 5
	syscall
	move $t1 $v0 		#number of nodes in list in t1
	
	li $t9 1		#current node count in t9
	move $t2 $zero		#address of next in t2, starts as null

getValuesLoop:
	beqz $t1 sumListMain	#once got all the numbers in the list, go to sum list
	
	li $v0 4
	la $a0 llValuePrompt1
	syscall			#prompt user for the value of
	
	li $v0 1
	move $a0 $t9
	syscall			#the nth node
	
	li $v0 4
	la $a0 llValuePrompt2
	syscall			#in the list
	
	li $v0 5
	syscall
	
	move $a0 $t2		#make a new list node with the address to the next list node in t2
	move $a1 $v0		#and the value of the list node in v0 (from user)
	jal newListNode
	move $t2 $v0		#put the address to the created list node in t2 (will be the next list node's next)
	
	addi $t9 $t9 1		#increment count
	subi $t1 $t1 1		#decrement length of list remaining
	
	j getValuesLoop		#loops until has values for the entire length of the list

sumListMain:
	move $a0 $t2
	jal sumList		#sums the linked list starting from the node with its address in t2
	move $t0 $v0		#stores the sum in t0
	
	li $v0 4
	la $a0 llResult
	syscall			#print result msg
	
	move $a0 $t0		
	li $v0 1
	syscall			#print sum
	
	li $v0 10
	syscall			#exit program

# newListNode subroutine creates a new list node with an address to the next node and a value
# @param value in $a0
# @param address to next node in $a1
# @return address to the created node in $v0
newListNode:
	move $t0 $a0		#move the value of the integer to t0
	li $v0 9
	li $a0 8
	syscall			#allocate 8 bytes of memory
	
	sw $a1 -4($v0)		#put address in memory
	sw $t0 ($v0)		#put value after address in memory
	jr $ra			#return

# sumList adds up all the values of the nodes in a linked list
# @param address to the linked list in $a0
# @return sum of the values in the linked list $v0
sumList:
	move $v0 $zero		#set sum to 0
	lw $t1 -4($a0)		#load value into t1
	subu $sp $sp 4
	sw $t1 ($sp)		#push value on stack
	lw $t0 ($a0)		#load address into t0
	beqz $t0 returnSum	#if address is 0, jump to return
	subu $sp $sp 4
	sw $ra ($sp)		#push return address on stack
	move $a0 $t0
	jal sumList		#sum the sub-list beginning from the next node
	lw $ra ($sp)
	addu $sp $sp 4		#pop return address into ra
returnSum:
	lw $t1 ($sp)
	addu $sp $sp 4		#pop value into t1
	add $v0 $v0 $t1		#add this node's value to sum
	jr $ra			#return

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
