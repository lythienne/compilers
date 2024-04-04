.data
	msg: .asciiz "hello"
.text
	jal printMsg
	
	li $v0 4
	la $a0 msg
	syscall
	
	jal printMsg
	
	j end
