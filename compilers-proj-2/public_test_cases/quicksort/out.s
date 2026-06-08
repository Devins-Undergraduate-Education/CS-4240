.data
newline: .asciiz "\n"


.text
.globl quicksort
quicksort:
	sw $ra, 0($sp)
	sw $fp, -4($sp)
	move $fp, $sp
	addi $sp, $sp, -52
	sw $a0, -8($fp)
	sw $a1, -12($fp)
	sw $a2, -16($fp)
	li $t0, 0
	sw $t0, -44($fp)
	li $t0, 0
	sw $t0, -48($fp)
	lw $t0, -12($fp)
	lw $t1, -16($fp)
	bge $t0, $t1, quicksort_end
	lw $t0, -12($fp)
	lw $t1, -16($fp)
	add $t2, $t0, $t1
	sw $t2, -36($fp)
	lw $t0, -36($fp)
	li $t1, 2
	div $t0, $t1
	mflo $t2
	sw $t2, -36($fp)
	lw $t0, -36($fp)
	sll $t0, $t0, 2
	lw $t1, -8($fp)
	add $t0, $t0, $t1
	lw $t2, 0($t0)
	sw $t2, -40($fp)
	lw $t0, -12($fp)
	li $t1, 1
	sub $t2, $t0, $t1
	sw $t2, -44($fp)
	lw $t0, -16($fp)
	li $t1, 1
	add $t2, $t0, $t1
	sw $t2, -48($fp)
quicksort_loop0:
quicksort_loop1:
	lw $t0, -44($fp)
	li $t1, 1
	add $t2, $t0, $t1
	sw $t2, -44($fp)
	lw $t0, -44($fp)
	sll $t0, $t0, 2
	lw $t1, -8($fp)
	add $t0, $t0, $t1
	lw $t2, 0($t0)
	sw $t2, -32($fp)
	lw $t0, -32($fp)
	sw $t0, -20($fp)
	lw $t0, -20($fp)
	lw $t1, -40($fp)
	blt $t0, $t1, quicksort_loop1
quicksort_loop2:
	lw $t0, -48($fp)
	li $t1, 1
	sub $t2, $t0, $t1
	sw $t2, -48($fp)
	lw $t0, -48($fp)
	sll $t0, $t0, 2
	lw $t1, -8($fp)
	add $t0, $t0, $t1
	lw $t2, 0($t0)
	sw $t2, -32($fp)
	lw $t0, -32($fp)
	sw $t0, -24($fp)
	lw $t0, -24($fp)
	lw $t1, -40($fp)
	bgt $t0, $t1, quicksort_loop2
	lw $t0, -44($fp)
	lw $t1, -48($fp)
	bge $t0, $t1, quicksort_exit0
	lw $t2, -20($fp)
	lw $t0, -48($fp)
	sll $t0, $t0, 2
	lw $t1, -8($fp)
	add $t0, $t0, $t1
	sw $t2, 0($t0)
	lw $t2, -24($fp)
	lw $t0, -44($fp)
	sll $t0, $t0, 2
	lw $t1, -8($fp)
	add $t0, $t0, $t1
	sw $t2, 0($t0)
	j quicksort_loop0
quicksort_exit0:
	lw $t0, -48($fp)
	li $t1, 1
	add $t2, $t0, $t1
	sw $t2, -28($fp)
	lw $a0, -8($fp)
	lw $a1, -12($fp)
	lw $a2, -48($fp)
	jal quicksort
	lw $t0, -48($fp)
	li $t1, 1
	add $t2, $t0, $t1
	sw $t2, -48($fp)
	lw $a0, -8($fp)
	lw $a1, -48($fp)
	lw $a2, -16($fp)
	jal quicksort
quicksort_end:
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
	addi $sp, $sp, -420
	li $t0, 0
	sw $t0, -408($fp)
	li $v0, 5
	syscall
	sw $v0, -416($fp)
	lw $t0, -416($fp)
	li $t1, 100
	bgt $t0, $t1, main_return
	lw $t0, -416($fp)
	li $t1, 1
	sub $t2, $t0, $t1
	sw $t2, -416($fp)
	li $t0, 0
	sw $t0, -412($fp)
main_loop0:
	lw $t0, -412($fp)
	lw $t1, -416($fp)
	bgt $t0, $t1, main_exit0
	li $v0, 5
	syscall
	sw $v0, -408($fp)
	lw $t2, -408($fp)
	lw $t0, -412($fp)
	sll $t0, $t0, 2
	addi $t1, $fp, -8
	add $t0, $t0, $t1
	sw $t2, 0($t0)
	lw $t0, -412($fp)
	li $t1, 1
	add $t2, $t0, $t1
	sw $t2, -412($fp)
	j main_loop0
main_exit0:
	addi $a0, $fp, -8
	li $a1, 0
	lw $a2, -416($fp)
	jal quicksort
	li $t0, 0
	sw $t0, -412($fp)
main_loop1:
	lw $t0, -412($fp)
	lw $t1, -416($fp)
	bgt $t0, $t1, main_exit1
	lw $t0, -412($fp)
	sll $t0, $t0, 2
	addi $t1, $fp, -8
	add $t0, $t0, $t1
	lw $t2, 0($t0)
	sw $t2, -408($fp)
	lw $a0, -408($fp)
	li $v0, 1
	syscall
	li $a0, 10
	li $v0, 11
	syscall
	lw $t0, -412($fp)
	li $t1, 1
	add $t2, $t0, $t1
	sw $t2, -412($fp)
	j main_loop1
main_exit1:
main_return:
	j __exit


__exit:
	li $v0, 10
	syscall
