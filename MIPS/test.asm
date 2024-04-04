.data
	int_y: .word -2000
.text 
	lw $v0 int_y
	move $s1 $v0
	
	li $t0 1
        li $t1 10
        move $t2 $s1
        bge $t2 $zero intLengthLoop1
        addi $t0 $t0 1
intLengthLoop1:
        div $t2 $t1
        mflo $t2
        beqz $t2 returnIntLength1
        addi $t0 $t0 1
        j intLengthLoop1
returnIntLength1:
        move $v0 $t0
	
	move $a0 $v0
	addi $a0 $a0 1
	li $v0 9
	syscall
	move $t7 $v0
	
	li $t3 1			#t3 count
        li $t1 10			#t1 10
        move $t2 $s1			#t2 int
        bge $t2 $zero getIntLoop1
	li $t0 45
	sb $t0 ($v0)			#if negative, store a -
	addi $v0 $v0 1
	subu $t2 $zero $t2		#make num positive
getIntLoop1:
	div $t2 $t1			#div num by 10
        mflo $t2			#quotient in t2
        mfhi $t0			#remainder in t0
        addi $t0 $t0 48			#convert to ascii
        subu $sp $sp 4			
        sw $t0 ($sp)			#push remainder ascii onto stack
        addi $t3 $t3 1
        beqz $t2 getIntEnd1		#if zero go to end
        j getIntLoop1
getIntEnd1:
storeIntLoop1:
	beqz $t3 storeIntEnd1
        lw $t0 ($sp)
        addu $sp $sp 4
        sb $t0 ($v0)
        addi $v0 $v0 1
        subi $t3 $t3 1
        j storeIntLoop1
storeIntEnd1:
	sb $zero ($v0)
        
        move $a0 $t7
        li $v0 4
        syscall
	
end:
	li $v0 10
	syscall