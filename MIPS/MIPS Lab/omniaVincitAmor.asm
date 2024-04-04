# Plays omnia vincit amor (sometimes twitchy bc rest timings not consistent but whatever)
#
# author: Harrison Chen
# version: 11/7/23
.data
	quarter: .word 0
	half: .word 0
	whole: .word 0
	eighth: .word 0
	sixteenth: .word 0
	dotQuarter: .word 0
	dotHalf: .word 0
.text
# Sets up the song, asking for the tempo, then storing the correct lengths of various
# notes, iniitalizes the loop count to 1
setUpSong:
	jal getTempo
	jal getQuarter
	sw $v0 quarter
	jal getHalf
	sw $v0 half
	jal getWhole
	sw $v0 whole
	jal getEighth
	sw $v0 eighth
	jal getSixteenth
	sw $v0 sixteenth
	jal getDotQuarter
	sw $v0 dotQuarter
	jal getDotHalf
	sw $v0 dotHalf
	li $t9 1
	
	jal playOmnia
	jal playEtnos
	jal playOmnia
	li $v0 10
	syscall

# Plays et nos cedamus part of the song (I read the sheet music, it was very. annoying)
playEtnos:
	move $s7 $ra
	
	lw $t8 quarter		#measure 21
	move $a0 $t8
	jal rest		#1
	move $a1 $t8
	li $a0 3
	jal playD		#2
	move $a0 $t8
	jal rest
	move $a1 $t8
	li $a0 3
	jal playG		#3
	move $a0 $t8
	jal rest
	move $a1 $t8
	li $a0 3
	jal playA		#4
	move $a0 $t8
	jal rest
	
	lw $t8 half		#measure 22
	move $a1 $t8
	li $a0 3
	jal playA		#1
	lw $t8 quarter
	move $a0 $t8
	jal rest		#1
	move $a1 $t8
	li $a0 3
	jal playB		#2
	move $a0 $t8
	jal rest
	lw $t8 half
	move $a1 $t8
	li $a0 3
	jal playG		#3
	lw $t8 quarter
	move $a1 $t8
	li $a0 3
	jal playB		#3
	move $a0 $t8
	jal rest
	move $a1 $t8
	li $a0 4
	jal playD		#4
	move $a0 $t8
	jal rest
	
	lw $t8 dotHalf		#measure 23
	move $a1 $t8
	li $a0 3
	jal playG		#1
	lw $t8 quarter
	move $a1 $t8
	li $a0 4
	jal playC		#1
	move $a0 $t8
	jal rest
	move $a1 $t8
	li $a0 3
	jal playB		#2
	move $a0 $t8
	jal rest
	lw $t8 half
	move $a1 $t8
	li $a0 3
	jal playA		#3
	lw $t8 quarter
	move $a0 $t8
	jal rest
	move $a1 $t8
	li $a0 3
	jal playFs		#4
	move $a0 $t8
	jal rest
	
	lw $t8 whole		#measure 24
	move $a1 $t8
	li $a0 3
	jal playG		#1
	lw $t8 quarter
	move $a0 $t8
	jal rest		#1
	move $a1 $t8
	li $a0 4
	jal playD		#2
	move $a0 $t8
	jal rest
	move $a1 $t8
	li $a0 3
	jal playB		#3
	move $a0 $t8
	jal rest
	move $a1 $t8
	li $a0 4
	jal playD		#4
	move $a0 $t8
	jal rest
	
	lw $t8 half		#measure 25
	move $a1 $t8
	li $a0 3
	jal playE		#1
	lw $t8 quarter
	move $a1 $t8
	li $a0 4
	jal playC		#1
	move $a0 $t8
	jal rest
	move $a1 $t8
	li $a0 3
	jal playB		#2
	move $a0 $t8
	jal rest
	lw $t8 half
	move $a1 $t8
	li $a0 3
	jal playFs		#3
	move $a1 $t8
	li $a0 3
	jal playA		#3
	move $a0 $t8
	jal rest
	
	lw $t8 quarter		#measure 26
	move $a1 $t8
	li $a0 3
	jal playG		#1
	move $a0 $t8
	jal rest
	move $a1 $t8
	li $a0 4
	jal playD		#2
	move $a1 $t8
	li $a0 3
	jal playG		#2
	move $a0 $t8
	jal rest
	move $a1 $t8
	li $a0 3
	jal playB		#3
	move $a1 $t8
	li $a0 3
	jal playG		#3
	move $a0 $t8
	jal rest
	move $a1 $t8
	li $a0 4
	jal playD		#4
	move $a1 $t8
	li $a0 3
	jal playG		#4
	move $a0 $t8
	jal rest
	
	lw $t8 half		#measure 27
	move $a1 $t8
	li $a0 4
	jal playE		#1
	move $a1 $t8
	li $a0 3
	jal playG		#1
	move $a0 $t8
	jal rest
	move $a1 $t8
	li $a0 4
	jal playD		#2
	move $a1 $t8
	li $a0 4
	jal playFs		#2
	lw $t8 quarter
	move $a1 $t8
	li $a0 3
	jal playFs		#3
	move $a0 $t8
	jal rest
	move $a1 $t8
	li $a0 3
	jal playD		#4
	move $a0 $t8
	jal rest
	
	lw $t8 whole		#measure 28
	move $a1 $t8
	li $a0 4
	jal playFs
	move $a1 $t8
	li $a0 4
	jal playD
	move $a1 $t8
	li $a0 3
	jal playG
	move $a1 $t8
	li $a0 3
	jal playC
	move $a0 $t8
	jal rest
	
	lw $t8 whole		#measure 29
	move $a1 $t8
	li $a0 4
	jal playE
	move $a1 $t8
	li $a0 4
	jal playC
	move $a1 $t8
	li $a0 3
	jal playG
	move $a1 $t8
	li $a0 3
	jal playC
	move $a0 $t8
	jal rest
	
	lw $t8 quarter		#measure 30
	move $a0 $t8
	jal rest		#1
	move $a1 $t8
	li $a0 4
	jal playD		#2
	move $a1 $t8
	li $a0 3
	jal playG		#2
	move $a0 $t8
	jal rest
	move $a1 $t8
	li $a0 3
	jal playB		#3
	move $a1 $t8
	li $a0 3
	jal playG		#3
	move $a0 $t8
	jal rest
	move $a1 $t8
	li $a0 4
	jal playD		#4
	move $a1 $t8
	li $a0 3
	jal playG		#4
	move $a0 $t8
	jal rest
	
	lw $t8 half		#measure 31
	move $a1 $t8
	li $a0 4
	jal playC		#1
	move $a1 $t8
	li $a0 3
	jal playG		#1
	move $a0 $t8
	jal rest
	lw $t8 quarter
	move $a1 $t8
	li $a0 3
	jal playB		#3
	move $a1 $t8
	li $a0 3
	jal playG		#3
	move $a0 $t8
	jal rest
	move $a1 $t8
	li $a0 3
	jal playB		#4
	move $a1 $t8
	li $a0 3
	jal playG		#4
	move $a0 $t8
	jal rest
	
	lw $t8 whole		#measure 32
	move $a1 $t8
	li $a0 3
	jal playA
	move $a1 $t8
	li $a0 3
	jal playG
	move $a0 $t8
	jal rest
	
	lw $t8 whole		#measure 33
	move $a1 $t8
	li $a0 3
	jal playA
	move $a1 $t8
	li $a0 3
	jal playFs
	move $a0 $t8
	jal rest
	
	jr $s7

# Plays omnia part of the song (I read the sheet music, it was very. annoying)
playOmnia:
	move $s7 $ra
	
	lw $t8 eighth		#measure 5
	move $a1 $t8
	li $a0 3
	jal playG
	move $a0 $t8
	jal rest
	lw $t8 sixteenth
	move $a1 $t8
	li $a0 3
	jal playG
	move $a0 $t8
	jal rest
	move $a1 $t8
	li $a0 3
	jal playG
	move $a0 $t8
	jal rest
	lw $t8 eighth
	move $a1 $t8
	li $a0 3
	jal playG
	move $a0 $t8
	jal rest
	move $a1 $t8
	li $a0 3
	jal playG
	move $a0 $t8
	jal rest
	lw $t8 quarter
	move $a1 $t8
	li $a0 3
	jal playG
	move $a0 $t8
	jal rest
	move $a1 $t8
	li $a0 3
	jal playG
	move $a0 $t8
	jal rest
	
	lw $t8 dotQuarter	#measure 6
	move $a1 $t8
	li $a0 3
	jal playG		#1
	lw $t8 eighth
	move $a1 $t8
	li $a0 3
	jal playA		#1
	move $a0 $t8
	jal rest
	lw $t8 sixteenth
	move $a1 $t8
	li $a0 3
	jal playA		#+
	move $a0 $t8
	jal rest
	move $a1 $t8
	li $a0 3
	jal playA		#a	
	move $a0 $t8
	jal rest
	
	lw $t8 eighth
	move $a1 $t8
	li $a0 3
	jal playA		#2
	move $a0 $t8
	jal rest
	
	move $a1 $t8
	li $a0 3
	jal playA		#+
	move $a1 $t8
	li $a0 3
	jal playG		#+
	move $a0 $t8
	jal rest
	
	lw $t8 quarter
	move $a1 $t8
	li $a0 3
	jal playA		#3
	move $a1 $t8
	li $a0 3
	jal playFs		#3
	move $a0 $t8
	jal rest
	
	lw $t8 quarter
	move $a1 $t8
	li $a0 3
	jal playA		#4
	lw $t8 eighth
	move $a1 $t8
	li $a0 3
	jal playFs		#4
	move $a0 $t8
	jal rest
	move $a1 $t8
	li $a0 3
	jal playA		#+
	move $a0 $t8
	jal rest
	
	lw $t8 dotQuarter	#measure 7
	move $a1 $t8
	li $a0 3
	jal playB		#1
	lw $t8 eighth
	move $a1 $t8
	li $a0 3
	jal playA		#1
	move $a0 $t8
	jal rest
	lw $t8 sixteenth
	move $a1 $t8
	li $a0 3
	jal playA		#+
	move $a0 $t8
	jal rest
	move $a1 $t8
	li $a0 3
	jal playA		#a
	move $a0 $t8
	jal rest
	
	lw $t8 eighth
	move $a1 $t8
	li $a0 3
	jal playG		#2
	move $a0 $t8
	jal rest
	move $a1 $t8
	li $a0 3
	jal playG		#+
	move $a1 $t8
	li $a0 3
	jal playB		#+
	move $a0 $t8
	jal rest
	
	lw $t8 half
	move $a1 $t8
	li $a0 3
	jal playB		#3
	lw $t8 eighth
	move $a1 $t8
	li $a0 3
	jal playG		#3
	move $a0 $t8
	jal rest
	move $a1 $t8
	li $a0 3
	jal playFs		#+
	move $a0 $t8
	jal rest
	
	move $a1 $t8
	li $a0 3
	jal playE		#4
	move $a0 $t8
	jal rest
	move $a1 $t8
	li $a0 3
	jal playD		#+
	move $a0 $t8
	jal rest
	
	lw $t8 quarter		#measure 8
	move $a1 $t8
	li $a0 3
	jal playC		#1
	lw $t8 eighth
	move $a1 $t8
	li $a0 4
	jal playC		#1
	move $a0 $t8
	jal rest
	move $a1 $t8
	li $a0 3
	jal playB		#+
	move $a0 $t8
	jal rest
	
	lw $t8 quarter
	move $a1 $t8
	li $a0 3
	jal playE		#2
	lw $t8 eighth
	move $a1 $t8
	li $a0 3
	jal playA		#2
	move $a0 $t8
	jal rest
	move $a1 $t8
	li $a0 3
	jal playG		#+
	move $a0 $t8
	jal rest
	
	lw $t8 half
	move $a1 $t8
	li $a0 4
	jal playD		#3
	move $a1 $t8
	li $a0 3
	jal playA		#3
	move $a1 $t8
	li $a0 3
	jal playD		#3
	move $a0 $t8
	jal rest
	
	lw $t8 eighth		#measure 9
	move $a1 $t8
	li $a0 3
	jal playG
	move $a0 $t8
	jal rest
	lw $t8 sixteenth
	move $a1 $t8
	li $a0 3
	jal playG
	move $a0 $t8
	jal rest
	move $a1 $t8
	li $a0 3
	jal playG
	move $a0 $t8
	jal rest
	lw $t8 eighth
	move $a1 $t8
	li $a0 3
	jal playG
	move $a0 $t8
	jal rest
	move $a1 $t8
	li $a0 3
	jal playG
	move $a0 $t8
	jal rest
	lw $t8 quarter
	move $a1 $t8
	li $a0 3
	jal playG
	move $a0 $t8
	jal rest
	move $a1 $t8
	li $a0 3
	jal playG
	move $a0 $t8
	jal rest
	
	lw $t8 dotQuarter	#measure 10
	move $a1 $t8
	li $a0 3
	jal playG		#1
	lw $t8 eighth
	move $a1 $t8
	li $a0 3
	jal playA		#1
	move $a0 $t8
	jal rest
	lw $t8 sixteenth
	move $a1 $t8
	li $a0 3
	jal playA		#+
	move $a0 $t8
	jal rest
	move $a1 $t8
	li $a0 3
	jal playA		#a	
	move $a0 $t8
	jal rest
	
	lw $t8 eighth
	move $a1 $t8
	li $a0 3
	jal playA		#2
	move $a0 $t8
	jal rest
	
	move $a1 $t8
	li $a0 3
	jal playA		#+
	move $a1 $t8
	li $a0 3
	jal playG		#+
	move $a0 $t8
	jal rest
	
	lw $t8 quarter
	move $a1 $t8
	li $a0 3
	jal playA		#3
	move $a1 $t8
	li $a0 3
	jal playFs		#3
	move $a0 $t8
	jal rest
	
	lw $t8 quarter
	move $a1 $t8
	li $a0 3
	jal playA		#4
	lw $t8 eighth
	move $a1 $t8
	li $a0 3
	jal playFs		#4
	move $a0 $t8
	jal rest
	move $a1 $t8
	li $a0 3
	jal playA		#+
	move $a0 $t8
	jal rest
	
	lw $t8 dotQuarter	#measure 11
	move $a1 $t8
	li $a0 3
	jal playB		#1
	lw $t8 eighth
	move $a1 $t8
	li $a0 3
	jal playA		#1
	move $a0 $t8
	jal rest
	lw $t8 sixteenth
	move $a1 $t8
	li $a0 3
	jal playA		#+
	move $a0 $t8
	jal rest
	move $a1 $t8
	li $a0 3
	jal playA		#a
	move $a0 $t8
	jal rest
	
	lw $t8 eighth
	move $a1 $t8
	li $a0 3
	jal playG		#2
	move $a0 $t8
	jal rest
	move $a1 $t8
	li $a0 3
	jal playG		#+
	move $a1 $t8
	li $a0 3
	jal playB		#+
	move $a0 $t8
	jal rest
	
	lw $t8 half
	move $a1 $t8
	li $a0 3
	jal playB		#3
	lw $t8 eighth
	move $a1 $t8
	li $a0 3
	jal playG		#3
	move $a0 $t8
	jal rest
	move $a1 $t8
	li $a0 3
	jal playFs		#+
	move $a0 $t8
	jal rest
	
	move $a1 $t8
	li $a0 3
	jal playE		#4
	move $a0 $t8
	jal rest
	move $a1 $t8
	li $a0 3
	jal playD		#+
	move $a0 $t8
	jal rest
	
	lw $t8 quarter		#measure 12
	move $a1 $t8
	li $a0 3
	jal playC		#1
	lw $t8 eighth
	move $a1 $t8
	li $a0 4
	jal playC		#1
	move $a0 $t8
	jal rest
	move $a1 $t8
	li $a0 3
	jal playB		#+
	move $a0 $t8
	jal rest
	
	lw $t8 quarter
	move $a1 $t8
	li $a0 3
	jal playE		#2
	lw $t8 eighth
	move $a1 $t8
	li $a0 3
	jal playA		#2
	move $a0 $t8
	jal rest
	move $a1 $t8
	li $a0 3
	jal playG		#+
	move $a0 $t8
	jal rest
	
	lw $t8 half
	move $a1 $t8
	li $a0 4
	jal playD		#3
	move $a1 $t8
	li $a0 3
	jal playD		#3
	move $a0 $t8
	jal rest
	
	lw $t8 eighth		#measure 13
	move $a0 $t8
	jal rest		#1
	lw $t8 quarter
	move $a1 $t8
	li $a0 4
	jal playE		#+
	move $a1 $t8
	li $a0 4
	jal playC		#+
	move $a1 $t8
	li $a0 3
	jal playG		#+
	move $a0 $t8
	jal rest
	
	lw $t8 eighth
	move $a1 $t8
	li $a0 4
	jal playE		#+
	move $a1 $t8
	li $a0 4
	jal playC		#+
	move $a1 $t8
	li $a0 3
	jal playG		#+
	move $a0 $t8
	jal rest
	
	lw $t8 quarter
	move $a1 $t8
	li $a0 4
	jal playD		#3
	move $a1 $t8
	li $a0 3
	jal playB		#3
	move $a1 $t8
	li $a0 3
	jal playG		#3
	move $a0 $t8
	jal rest
	
	lw $t8 eighth
	move $a1 $t8
	li $a0 4
	jal playD		#4
	move $a1 $t8
	li $a0 3
	jal playB		#4
	move $a1 $t8
	li $a0 3
	jal playG		#4
	move $a0 $t8
	jal rest
	move $a1 $t8
	li $a0 4
	jal playD		#+
	move $a1 $t8
	li $a0 3
	jal playB		#+
	move $a1 $t8
	li $a0 3
	jal playG		#+
	move $a0 $t8
	jal rest
	
	lw $t8 half		#measure 14
	move $a1 $t8
	li $a0 3
	jal playG		#1
	lw $t8 eighth
	move $a1 $t8
	li $a0 4
	jal playC		#1
	move $a0 $t8
	jal rest
	move $a1 $t8
	li $a0 3
	jal playB		#+
	move $a0 $t8
	jal rest
	
	lw $t8 eighth
	move $a1 $t8
	li $a0 3
	jal playA		#2
	move $a0 $t8
	jal rest
	move $a1 $t8
	li $a0 3
	jal playG		#+
	move $a0 $t8
	jal rest
	
	lw $t8 half
	move $a1 $t8
	li $a0 3
	jal playB		#3
	move $a1 $t8
	li $a0 3
	jal playE		#3
	move $a0 $t8
	jal rest
	
	lw $t8 quarter		#measure 15
	move $a1 $t8
	li $a0 4
	jal playC		#1
	move $a1 $t8
	li $a0 3
	jal playC		#1
	move $a0 $t8
	jal rest
	
	move $a1 $t8
	li $a0 3
	jal playB		#2
	move $a1 $t8
	li $a0 3
	jal playD		#2
	move $a0 $t8
	jal rest
	
	move $a1 $t8
	li $a0 3
	jal playA		#3
	move $a1 $t8
	li $a0 3
	jal playE		#3
	move $a0 $t8
	jal rest
	
	move $a1 $t8
	li $a0 3
	jal playG		#4
	move $a1 $t8
	li $a0 3
	jal playG		#4
	move $a1 $t8
	li $a0 3
	jal playE		#4
	move $a0 $t8
	jal rest
	
	lw $t8 whole		#measure 16
	move $a1 $t8
	li $a0 3
	jal playA		#1
	li $a0 3
	jal playD		#1
	lw $t8 half
	move $a1 $t8
	li $a0 3
	jal playG		#1
	move $a0 $t8
	jal rest

	move $a1 $t8
	li $a0 3
	jal playFs		#3
	move $a0 $t8
	jal rest
	
	lw $t8 eighth		#measure 17
	move $a1 $t8
	li $a0 3
	jal playG
	move $a0 $t8
	jal rest
	lw $t8 sixteenth
	move $a1 $t8
	li $a0 3
	jal playG
	move $a0 $t8
	jal rest
	move $a1 $t8
	li $a0 3
	jal playG
	move $a0 $t8
	jal rest
	lw $t8 eighth
	move $a1 $t8
	li $a0 3
	jal playG
	move $a0 $t8
	jal rest
	move $a1 $t8
	li $a0 3
	jal playG
	move $a0 $t8
	jal rest
	lw $t8 quarter
	move $a1 $t8
	li $a0 3
	jal playG
	move $a0 $t8
	jal rest
	move $a1 $t8
	li $a0 3
	jal playG
	move $a0 $t8
	jal rest
	
	lw $t8 dotQuarter	#measure 18
	move $a1 $t8
	li $a0 3
	jal playG		#1
	lw $t8 eighth
	move $a1 $t8
	li $a0 3
	jal playA		#1
	move $a0 $t8
	jal rest
	lw $t8 sixteenth
	move $a1 $t8
	li $a0 3
	jal playA		#+
	move $a0 $t8
	jal rest
	move $a1 $t8
	li $a0 3
	jal playA		#a	
	move $a0 $t8
	jal rest
	
	lw $t8 eighth
	move $a1 $t8
	li $a0 3
	jal playA		#2
	move $a0 $t8
	jal rest
	
	move $a1 $t8
	li $a0 3
	jal playA		#+
	move $a1 $t8
	li $a0 3
	jal playG		#+
	move $a0 $t8
	jal rest
	
	lw $t8 quarter
	move $a1 $t8
	li $a0 3
	jal playA		#3
	move $a1 $t8
	li $a0 3
	jal playFs		#3
	move $a0 $t8
	jal rest
	
	lw $t8 quarter
	move $a1 $t8
	li $a0 3
	jal playA		#4
	lw $t8 eighth
	move $a1 $t8
	li $a0 3
	jal playFs		#4
	move $a0 $t8
	jal rest
	move $a1 $t8
	li $a0 3
	jal playA		#+
	move $a0 $t8
	jal rest
	
	lw $t8 dotQuarter	#measure 19
	move $a1 $t8
	li $a0 3
	jal playB		#1
	lw $t8 eighth
	move $a1 $t8
	li $a0 3
	jal playA		#1
	move $a0 $t8
	jal rest
	lw $t8 sixteenth
	move $a1 $t8
	li $a0 3
	jal playA		#+
	move $a0 $t8
	jal rest
	move $a1 $t8
	li $a0 3
	jal playA		#a
	move $a0 $t8
	jal rest
	
	lw $t8 eighth
	move $a1 $t8
	li $a0 3
	jal playG		#2
	move $a0 $t8
	jal rest
	move $a1 $t8
	li $a0 3
	jal playG		#+
	move $a1 $t8
	li $a0 3
	jal playB		#+
	move $a0 $t8
	jal rest
	
	lw $t8 half
	move $a1 $t8
	li $a0 3
	jal playB		#3
	lw $t8 eighth
	move $a1 $t8
	li $a0 3
	jal playG		#3
	move $a0 $t8
	jal rest
	move $a1 $t8
	li $a0 3
	jal playFs		#+
	move $a0 $t8
	jal rest
	
	move $a1 $t8
	li $a0 3
	jal playE		#4
	move $a0 $t8
	jal rest
	move $a1 $t8
	li $a0 3
	jal playD		#+
	move $a0 $t8
	jal rest
	
	lw $t8 quarter		#measure 20
	move $a1 $t8
	li $a0 3
	jal playC		#1
	lw $t8 eighth
	move $a1 $t8
	li $a0 4
	jal playC		#1
	move $a0 $t8
	jal rest
	move $a1 $t8
	li $a0 3
	jal playB		#+
	move $a0 $t8
	jal rest
	
	lw $t8 quarter
	move $a1 $t8
	li $a0 3
	jal playD		#2
	lw $t8 eighth
	move $a1 $t8
	li $a0 3
	jal playA		#2
	move $a0 $t8
	jal rest
	move $a1 $t8
	li $a0 3
	jal playG		#+
	move $a0 $t8
	jal rest
	
	lw $t8 half
	move $a1 $t8
	li $a0 3
	jal playG		#3
	move $a0 $t8
	jal rest
	
	jr $s7