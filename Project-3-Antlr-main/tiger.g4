grammar tiger;

// Parser rules
tiger_program
    : MAIN LET declaration_segment IN BEGINKW stat_seq END EOF
    ;

declaration_segment
    : var_declaration_list funct_declaration_list
    ;

var_declaration_list
    : var_declaration*
    ;

var_declaration
    : VAR id_list COLON type optional_init SEMI
    ;

optional_init
    : (ASSIGN const_value)?
    ;

const_value
    : INTLIT
    | FLOATLIT
    ;

id_list
    : ID (COMMA ID)*
    ;

type
    : type_id
    | ARRAY LBRACK INTLIT RBRACK OF type_id
    ;

type_id
    : INT
    | FLOAT
    ;

funct_declaration_list
    : funct_declaration*
    ;

funct_declaration
    : FUNCTION ID LPAREN param_list_opt RPAREN ret_type BEGINKW stat_seq END
    ;

param_list_opt
    : param_list?
    ;

param_list
    : param (COMMA param)*
    ;

param
    : ID COLON type
    ;

ret_type
    : (COLON type)?
    ;

stat_seq
    : stat+
    ;

stat
    : IF expr THEN stat_seq if_tail ENDIF SEMI
    | WHILE expr DO stat_seq ENDDO SEMI
    | FOR ID ASSIGN expr TO expr DO stat_seq ENDDO SEMI
    | BREAK SEMI
    | RETURN expr SEMI
    | LET declaration_segment IN stat_seq END
    | ID stat_id_tail
    ;

if_tail
    : ELSE stat_seq
    | /* empty */
    ;

stat_id_tail
    : LPAREN expr_list_opt RPAREN SEMI                #callStat
    | lvalue_index_opt ASSIGN expr SEMI               #assignStat
    ;

expr_list_opt
    : expr_list?
    ;

expr_list
    : expr (COMMA expr)*
    ;

lvalue
    : ID lvalue_index_opt
    ;

lvalue_index_opt
    : lvalue_index?
    ;

lvalue_index
    : LBRACK expr RBRACK
    ;

expr
    : or_expr
    ;

or_expr
    : and_expr (OR or_expr)?
    ;

and_expr
    : comp_expr (AND and_expr)?
    ;

comp_expr
    : add_expr (comp_op comp_expr)?
    ;

comp_op
    : EQ
    | NEQ
    | LT
    | GT
    | LE
    | GE
    ;

add_expr
    : mul_expr (add_op add_expr)?
    ;

add_op
    : PLUS
    | MINUS
    ;

mul_expr
    : primary (mul_op mul_expr)?
    ;

mul_op
    : MULT
    | DIV
    ;

primary
    : LPAREN expr RPAREN
    | ID primary_id_tail
    | INTLIT
    | FLOATLIT
    ;

primary_id_tail
    : LPAREN expr_list_opt RPAREN
    | lvalue_index_opt
    ;

// Lexer rules
MAIN    : 'main';
ARRAY   : 'array';
BREAK   : 'break';
DO      : 'do';
IF      : 'if';
ELSE    : 'else';
FOR     : 'for';
FUNCTION: 'function';
LET     : 'let';
IN      : 'in';
OF      : 'of';
THEN    : 'then';
TO      : 'to';
VAR     : 'var';
WHILE   : 'while';
ENDIF   : 'endif';
BEGINKW : 'begin';
END     : 'end';
ENDDO   : 'enddo';
RETURN  : 'return';
INT     : 'int';
FLOAT   : 'float';

PLUS    : '+';
MINUS   : '-';
MULT    : '*';
DIV     : '/';
EQ      : '=';
NEQ     : '<>';
LT      : '<';
GT      : '>';
LE      : '<=';
GE      : '>=';
AND     : '&';
OR      : '|';
ASSIGN  : ':=';

COMMA   : ',';
COLON   : ':';
SEMI    : ';';
LPAREN  : '(';
RPAREN  : ')';
LBRACK  : '[';
RBRACK  : ']';

FLOATLIT: DIGIT+ '.' DIGIT*;
INTLIT  : DIGIT+;

ID      : LETTER (LETTER | DIGIT | '_')*;

COMMENT : '/*' .*? '*/' -> skip;
WS      : [ \r\t\n]+ -> skip;

fragment LETTER : [a-zA-Z];
fragment DIGIT  : [0-9];
