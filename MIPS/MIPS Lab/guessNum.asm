#Guesses a number the user is thinking of in their head
#@author Harrison Chen
#@version 10/27/23

.data
	welcome: .asciiz "Think of a number, I'll try to guess it\n"
	guess: .asciiz "My guess is: "
	ask: .asciiz "\nWas my guess:\n1) high\n2) low\n3) correct\n"
	answer: .asciiz ""
	num: .word 0
	top: .word 0
	bottom: .word 0
.text
	li $v0 4
	la $a0 welcome
	syscall			#prints welcome statement
	
	li $s0 2		#this is for multiplying and dividing by 2
	
	loop:
	
	li $v0 4
	la $a0 guess
	syscall			
	li $v0 1
	lw $a0 num
	syscall			#print the guess
	
	li $v0 4
	la $a0 ask
	syscall
	li $v0 5
	syscall			#ask for user input 1 = guess too high, 2 = guess too low, 3 = guess is correct
	move $t0 $v0		#store user input in t0
	
	beq $t0 1 high		#1 = guess too high
	beq $t0 2 low		#2 = guess too low
	beq $t0 3 end		#3 = guess is correct, end the program
	j loop			#else re-ask question
	
	high:			#if guess is too high
	lw $t0 num		#load guess in t0
	sw $t0 top		#the number must be lower than the guessed number, top of range is guessed number
	lw $t1 bottom		#load bottom of range in t1
	beq $t1 0 noBottom	#if we aren't sure of a range bottom yet goto noBottom
	j hAvg			#else go straight to averaging
	noBottom:
	addi $t9 $t9 -1		#t9 represents what the program guesses the bottom will be, decrement t9
	mult $t9 $s0
	mflo $t9		#double t9
	move $t1 $t9		#set the temporary bottom to t9
	hAvg:
	add $t0 $t0 $t1
	div $t0 $s0
	mflo $t0		#average the top and (possibly temporary) bottom
	sw $t0 num		#set the average to be the new guess
	j loop			#redo the loop
	
	low:			#if guess is too low
	lw $t0 num		#load guess in t0
	sw $t0 bottom		#the number must be higher than the guessed number, bottom of range is guessed number
	lw $t1 top		#load top of range in t1
	beq $t1 0 noTop		#if we aren't sure of a range top yet goto noTop
	j lAvg			#else go straight to averaging
	noTop:
	addi $t8 $t8 1		#t8 represents what the program guesses the top will be, increment t8
	mult $t8 $s0
	mflo $t8		#double t8
	move $t1 $t8		#set the temporary top to t8
	lAvg:
	add $t0 $t0 $t1
	div $t0 $s0
	mflo $t0		#average the bottom and (possibly temporary) top
	sw $t0 num		#set the average to be the new guess
	j loop			#redo the loop
