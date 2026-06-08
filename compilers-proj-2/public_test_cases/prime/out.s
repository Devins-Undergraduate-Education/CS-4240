.data
newline: .asciiz "\n"


.text
.globl divisible
divisible:
	sw $ra, 0($sp)
	sw $fp, -4($sp)
	move $fp, $sp
	addi $sp, $sp, -20
	sw $a0, -8($fp)
	sw $a1, -12($fp)
	lw $t0, -8($fp)
	lw $t1, -12($fp)
	div $t0, $t1
	mflo $t2
	sw $t2, -16($fp)
	lw $t0, -16($fp)
	lw $t1, -12($fp)
	mult $t0, $t1
	mflo $t2
	sw $t2, -16($fp)
	lw $t0, -8($fp)
	lw $t1, -16($fp)
	bne $t0, $t1, divisible_label0
	li $v0, 1
	move $sp, $fp
	lw $fp, -4($sp)
	lw $ra, 0($sp)
	jr $ra
divisible_label0:
	li $v0, 0
	move $sp, $fp
	lw $fp, -4($sp)
	lw $ra, 0($sp)
	jr $ra


.text
.globl main
main:
	sw $ra, 0($sp)
	sw $fp, -4($sp)
	move $fp, $sp
	addi $sp, $sp, -64
	li $t0, 0
	sw $t0, -24($fp)
	li $t0, 2
	sw $t0, -12($fp)
	li $t0, 3
	sw $t0, -16($fp)
	li $t0, 6
	sw $t0, -20($fp)
	li $t0, 0
	sw $t0, -56($fp)
	li $v0, 5
	syscall
	sw $v0, -28($fp)
	lw $t0, -28($fp)
	li $t1, 1
	bgt $t0, $t1, main_label0
	li $t0, 0
	sw $t0, -32($fp)
	lw $t0, -32($fp)
	sw $t0, -8($fp)
	j main_print
main_label0:
	lw $t0, -28($fp)
	li $t1, 3
	bgt $t0, $t1, main_label1
	li $t0, 1
	sw $t0, -32($fp)
	lw $t0, -32($fp)
	sw $t0, -8($fp)
	j main_print
main_label1:
	lw $a0, -28($fp)
	lw $a1, -12($fp)
	jal divisible
	sw $v0, -40($fp)
	lw $t0, -56($fp)
	sw $t0, -32($fp)
	lw $t0, -32($fp)
	sw $t0, -8($fp)
	lw $t0, -40($fp)
	li $t1, 1
	beq $t0, $t1, main_label2
	lw $a0, -28($fp)
	lw $a1, -16($fp)
	jal divisible
	sw $v0, -40($fp)
	lw $t0, -56($fp)
	sw $t0, -32($fp)
	lw $t0, -32($fp)
	sw $t0, -8($fp)
	lw $t0, -40($fp)
	li $t1, 1
	beq $t0, $t1, main_label2
	j main_label3
main_label2:
	j main_print
main_label3:
	li $t0, 5
	sw $t0, -24($fp)
main_loop:
	lw $t0, -24($fp)
	lw $t1, -24($fp)
	mult $t0, $t1
	mflo $t2
	sw $t2, -36($fp)
	lw $t0, -36($fp)
	lw $t1, -28($fp)
	bgt $t0, $t1, main_exit
	lw $a0, -28($fp)
	lw $a1, -24($fp)
	jal divisible
	sw $v0, -40($fp)
	lw $t0, -56($fp)
	sw $t0, -32($fp)
	li $t0, 0
	sw $t0, -48($fp)
	li $t0, 0
	sw $t0, -60($fp)
	lw $t0, -32($fp)
	sw $t0, -8($fp)
	lw $t0, -40($fp)
	li $t1, 1
	beq $t0, $t1, main_label2
	lw $t0, -24($fp)
	li $t1, 2
	add $t2, $t0, $t1
	sw $t2, -44($fp)
	lw $a0, -28($fp)
	lw $a1, -44($fp)
	jal divisible
	sw $v0, -40($fp)
	lw $t0, -56($fp)
	sw $t0, -32($fp)
	lw $t0, -32($fp)
	sw $t0, -8($fp)
	lw $t0, -40($fp)
	li $t1, 1
	beq $t0, $t1, main_label2
	lw $t0, -24($fp)
	li $t1, 6
	add $t2, $t0, $t1
	sw $t2, -24($fp)
	j main_loop
main_exit:
	lw $t0, -48($fp)
	sw $t0, -52($fp)
	lw $t0, -60($fp)
	sw $t0, -32($fp)
	li $t0, 1
	sw $t0, -32($fp)
	lw $t0, -32($fp)
	sw $t0, -8($fp)
main_print:
	lw $a0, -8($fp)
	li $v0, 1
	syscall
	li $a0, 10
	li $v0, 11
	syscall
	j __exit


__exit:
	li $v0, 10
	syscall
