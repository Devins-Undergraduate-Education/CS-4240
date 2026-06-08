// Generated from tiger.g4 by ANTLR 4.13.2
import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link tigerParser}.
 */
public interface tigerListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link tigerParser#tiger_program}.
	 * @param ctx the parse tree
	 */
	void enterTiger_program(tigerParser.Tiger_programContext ctx);
	/**
	 * Exit a parse tree produced by {@link tigerParser#tiger_program}.
	 * @param ctx the parse tree
	 */
	void exitTiger_program(tigerParser.Tiger_programContext ctx);
	/**
	 * Enter a parse tree produced by {@link tigerParser#declaration_segment}.
	 * @param ctx the parse tree
	 */
	void enterDeclaration_segment(tigerParser.Declaration_segmentContext ctx);
	/**
	 * Exit a parse tree produced by {@link tigerParser#declaration_segment}.
	 * @param ctx the parse tree
	 */
	void exitDeclaration_segment(tigerParser.Declaration_segmentContext ctx);
	/**
	 * Enter a parse tree produced by {@link tigerParser#var_declaration_list}.
	 * @param ctx the parse tree
	 */
	void enterVar_declaration_list(tigerParser.Var_declaration_listContext ctx);
	/**
	 * Exit a parse tree produced by {@link tigerParser#var_declaration_list}.
	 * @param ctx the parse tree
	 */
	void exitVar_declaration_list(tigerParser.Var_declaration_listContext ctx);
	/**
	 * Enter a parse tree produced by {@link tigerParser#var_declaration}.
	 * @param ctx the parse tree
	 */
	void enterVar_declaration(tigerParser.Var_declarationContext ctx);
	/**
	 * Exit a parse tree produced by {@link tigerParser#var_declaration}.
	 * @param ctx the parse tree
	 */
	void exitVar_declaration(tigerParser.Var_declarationContext ctx);
	/**
	 * Enter a parse tree produced by {@link tigerParser#optional_init}.
	 * @param ctx the parse tree
	 */
	void enterOptional_init(tigerParser.Optional_initContext ctx);
	/**
	 * Exit a parse tree produced by {@link tigerParser#optional_init}.
	 * @param ctx the parse tree
	 */
	void exitOptional_init(tigerParser.Optional_initContext ctx);
	/**
	 * Enter a parse tree produced by {@link tigerParser#const_value}.
	 * @param ctx the parse tree
	 */
	void enterConst_value(tigerParser.Const_valueContext ctx);
	/**
	 * Exit a parse tree produced by {@link tigerParser#const_value}.
	 * @param ctx the parse tree
	 */
	void exitConst_value(tigerParser.Const_valueContext ctx);
	/**
	 * Enter a parse tree produced by {@link tigerParser#id_list}.
	 * @param ctx the parse tree
	 */
	void enterId_list(tigerParser.Id_listContext ctx);
	/**
	 * Exit a parse tree produced by {@link tigerParser#id_list}.
	 * @param ctx the parse tree
	 */
	void exitId_list(tigerParser.Id_listContext ctx);
	/**
	 * Enter a parse tree produced by {@link tigerParser#type}.
	 * @param ctx the parse tree
	 */
	void enterType(tigerParser.TypeContext ctx);
	/**
	 * Exit a parse tree produced by {@link tigerParser#type}.
	 * @param ctx the parse tree
	 */
	void exitType(tigerParser.TypeContext ctx);
	/**
	 * Enter a parse tree produced by {@link tigerParser#type_id}.
	 * @param ctx the parse tree
	 */
	void enterType_id(tigerParser.Type_idContext ctx);
	/**
	 * Exit a parse tree produced by {@link tigerParser#type_id}.
	 * @param ctx the parse tree
	 */
	void exitType_id(tigerParser.Type_idContext ctx);
	/**
	 * Enter a parse tree produced by {@link tigerParser#funct_declaration_list}.
	 * @param ctx the parse tree
	 */
	void enterFunct_declaration_list(tigerParser.Funct_declaration_listContext ctx);
	/**
	 * Exit a parse tree produced by {@link tigerParser#funct_declaration_list}.
	 * @param ctx the parse tree
	 */
	void exitFunct_declaration_list(tigerParser.Funct_declaration_listContext ctx);
	/**
	 * Enter a parse tree produced by {@link tigerParser#funct_declaration}.
	 * @param ctx the parse tree
	 */
	void enterFunct_declaration(tigerParser.Funct_declarationContext ctx);
	/**
	 * Exit a parse tree produced by {@link tigerParser#funct_declaration}.
	 * @param ctx the parse tree
	 */
	void exitFunct_declaration(tigerParser.Funct_declarationContext ctx);
	/**
	 * Enter a parse tree produced by {@link tigerParser#param_list_opt}.
	 * @param ctx the parse tree
	 */
	void enterParam_list_opt(tigerParser.Param_list_optContext ctx);
	/**
	 * Exit a parse tree produced by {@link tigerParser#param_list_opt}.
	 * @param ctx the parse tree
	 */
	void exitParam_list_opt(tigerParser.Param_list_optContext ctx);
	/**
	 * Enter a parse tree produced by {@link tigerParser#param_list}.
	 * @param ctx the parse tree
	 */
	void enterParam_list(tigerParser.Param_listContext ctx);
	/**
	 * Exit a parse tree produced by {@link tigerParser#param_list}.
	 * @param ctx the parse tree
	 */
	void exitParam_list(tigerParser.Param_listContext ctx);
	/**
	 * Enter a parse tree produced by {@link tigerParser#param}.
	 * @param ctx the parse tree
	 */
	void enterParam(tigerParser.ParamContext ctx);
	/**
	 * Exit a parse tree produced by {@link tigerParser#param}.
	 * @param ctx the parse tree
	 */
	void exitParam(tigerParser.ParamContext ctx);
	/**
	 * Enter a parse tree produced by {@link tigerParser#ret_type}.
	 * @param ctx the parse tree
	 */
	void enterRet_type(tigerParser.Ret_typeContext ctx);
	/**
	 * Exit a parse tree produced by {@link tigerParser#ret_type}.
	 * @param ctx the parse tree
	 */
	void exitRet_type(tigerParser.Ret_typeContext ctx);
	/**
	 * Enter a parse tree produced by {@link tigerParser#stat_seq}.
	 * @param ctx the parse tree
	 */
	void enterStat_seq(tigerParser.Stat_seqContext ctx);
	/**
	 * Exit a parse tree produced by {@link tigerParser#stat_seq}.
	 * @param ctx the parse tree
	 */
	void exitStat_seq(tigerParser.Stat_seqContext ctx);
	/**
	 * Enter a parse tree produced by {@link tigerParser#stat}.
	 * @param ctx the parse tree
	 */
	void enterStat(tigerParser.StatContext ctx);
	/**
	 * Exit a parse tree produced by {@link tigerParser#stat}.
	 * @param ctx the parse tree
	 */
	void exitStat(tigerParser.StatContext ctx);
	/**
	 * Enter a parse tree produced by {@link tigerParser#if_tail}.
	 * @param ctx the parse tree
	 */
	void enterIf_tail(tigerParser.If_tailContext ctx);
	/**
	 * Exit a parse tree produced by {@link tigerParser#if_tail}.
	 * @param ctx the parse tree
	 */
	void exitIf_tail(tigerParser.If_tailContext ctx);
	/**
	 * Enter a parse tree produced by the {@code callStat}
	 * labeled alternative in {@link tigerParser#stat_id_tail}.
	 * @param ctx the parse tree
	 */
	void enterCallStat(tigerParser.CallStatContext ctx);
	/**
	 * Exit a parse tree produced by the {@code callStat}
	 * labeled alternative in {@link tigerParser#stat_id_tail}.
	 * @param ctx the parse tree
	 */
	void exitCallStat(tigerParser.CallStatContext ctx);
	/**
	 * Enter a parse tree produced by the {@code assignStat}
	 * labeled alternative in {@link tigerParser#stat_id_tail}.
	 * @param ctx the parse tree
	 */
	void enterAssignStat(tigerParser.AssignStatContext ctx);
	/**
	 * Exit a parse tree produced by the {@code assignStat}
	 * labeled alternative in {@link tigerParser#stat_id_tail}.
	 * @param ctx the parse tree
	 */
	void exitAssignStat(tigerParser.AssignStatContext ctx);
	/**
	 * Enter a parse tree produced by {@link tigerParser#expr_list_opt}.
	 * @param ctx the parse tree
	 */
	void enterExpr_list_opt(tigerParser.Expr_list_optContext ctx);
	/**
	 * Exit a parse tree produced by {@link tigerParser#expr_list_opt}.
	 * @param ctx the parse tree
	 */
	void exitExpr_list_opt(tigerParser.Expr_list_optContext ctx);
	/**
	 * Enter a parse tree produced by {@link tigerParser#expr_list}.
	 * @param ctx the parse tree
	 */
	void enterExpr_list(tigerParser.Expr_listContext ctx);
	/**
	 * Exit a parse tree produced by {@link tigerParser#expr_list}.
	 * @param ctx the parse tree
	 */
	void exitExpr_list(tigerParser.Expr_listContext ctx);
	/**
	 * Enter a parse tree produced by {@link tigerParser#lvalue}.
	 * @param ctx the parse tree
	 */
	void enterLvalue(tigerParser.LvalueContext ctx);
	/**
	 * Exit a parse tree produced by {@link tigerParser#lvalue}.
	 * @param ctx the parse tree
	 */
	void exitLvalue(tigerParser.LvalueContext ctx);
	/**
	 * Enter a parse tree produced by {@link tigerParser#lvalue_index_opt}.
	 * @param ctx the parse tree
	 */
	void enterLvalue_index_opt(tigerParser.Lvalue_index_optContext ctx);
	/**
	 * Exit a parse tree produced by {@link tigerParser#lvalue_index_opt}.
	 * @param ctx the parse tree
	 */
	void exitLvalue_index_opt(tigerParser.Lvalue_index_optContext ctx);
	/**
	 * Enter a parse tree produced by {@link tigerParser#lvalue_index}.
	 * @param ctx the parse tree
	 */
	void enterLvalue_index(tigerParser.Lvalue_indexContext ctx);
	/**
	 * Exit a parse tree produced by {@link tigerParser#lvalue_index}.
	 * @param ctx the parse tree
	 */
	void exitLvalue_index(tigerParser.Lvalue_indexContext ctx);
	/**
	 * Enter a parse tree produced by {@link tigerParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterExpr(tigerParser.ExprContext ctx);
	/**
	 * Exit a parse tree produced by {@link tigerParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitExpr(tigerParser.ExprContext ctx);
	/**
	 * Enter a parse tree produced by {@link tigerParser#or_expr}.
	 * @param ctx the parse tree
	 */
	void enterOr_expr(tigerParser.Or_exprContext ctx);
	/**
	 * Exit a parse tree produced by {@link tigerParser#or_expr}.
	 * @param ctx the parse tree
	 */
	void exitOr_expr(tigerParser.Or_exprContext ctx);
	/**
	 * Enter a parse tree produced by {@link tigerParser#and_expr}.
	 * @param ctx the parse tree
	 */
	void enterAnd_expr(tigerParser.And_exprContext ctx);
	/**
	 * Exit a parse tree produced by {@link tigerParser#and_expr}.
	 * @param ctx the parse tree
	 */
	void exitAnd_expr(tigerParser.And_exprContext ctx);
	/**
	 * Enter a parse tree produced by {@link tigerParser#comp_expr}.
	 * @param ctx the parse tree
	 */
	void enterComp_expr(tigerParser.Comp_exprContext ctx);
	/**
	 * Exit a parse tree produced by {@link tigerParser#comp_expr}.
	 * @param ctx the parse tree
	 */
	void exitComp_expr(tigerParser.Comp_exprContext ctx);
	/**
	 * Enter a parse tree produced by {@link tigerParser#comp_op}.
	 * @param ctx the parse tree
	 */
	void enterComp_op(tigerParser.Comp_opContext ctx);
	/**
	 * Exit a parse tree produced by {@link tigerParser#comp_op}.
	 * @param ctx the parse tree
	 */
	void exitComp_op(tigerParser.Comp_opContext ctx);
	/**
	 * Enter a parse tree produced by {@link tigerParser#add_expr}.
	 * @param ctx the parse tree
	 */
	void enterAdd_expr(tigerParser.Add_exprContext ctx);
	/**
	 * Exit a parse tree produced by {@link tigerParser#add_expr}.
	 * @param ctx the parse tree
	 */
	void exitAdd_expr(tigerParser.Add_exprContext ctx);
	/**
	 * Enter a parse tree produced by {@link tigerParser#add_op}.
	 * @param ctx the parse tree
	 */
	void enterAdd_op(tigerParser.Add_opContext ctx);
	/**
	 * Exit a parse tree produced by {@link tigerParser#add_op}.
	 * @param ctx the parse tree
	 */
	void exitAdd_op(tigerParser.Add_opContext ctx);
	/**
	 * Enter a parse tree produced by {@link tigerParser#mul_expr}.
	 * @param ctx the parse tree
	 */
	void enterMul_expr(tigerParser.Mul_exprContext ctx);
	/**
	 * Exit a parse tree produced by {@link tigerParser#mul_expr}.
	 * @param ctx the parse tree
	 */
	void exitMul_expr(tigerParser.Mul_exprContext ctx);
	/**
	 * Enter a parse tree produced by {@link tigerParser#mul_op}.
	 * @param ctx the parse tree
	 */
	void enterMul_op(tigerParser.Mul_opContext ctx);
	/**
	 * Exit a parse tree produced by {@link tigerParser#mul_op}.
	 * @param ctx the parse tree
	 */
	void exitMul_op(tigerParser.Mul_opContext ctx);
	/**
	 * Enter a parse tree produced by {@link tigerParser#primary}.
	 * @param ctx the parse tree
	 */
	void enterPrimary(tigerParser.PrimaryContext ctx);
	/**
	 * Exit a parse tree produced by {@link tigerParser#primary}.
	 * @param ctx the parse tree
	 */
	void exitPrimary(tigerParser.PrimaryContext ctx);
	/**
	 * Enter a parse tree produced by {@link tigerParser#primary_id_tail}.
	 * @param ctx the parse tree
	 */
	void enterPrimary_id_tail(tigerParser.Primary_id_tailContext ctx);
	/**
	 * Exit a parse tree produced by {@link tigerParser#primary_id_tail}.
	 * @param ctx the parse tree
	 */
	void exitPrimary_id_tail(tigerParser.Primary_id_tailContext ctx);
}