// Generated from D:/apache/concurrent/spark/internal/sql/src/antlr4/com/github/spafka/spark/sql\cal.g4 by ANTLR 4.7
package com.github.spafka.spark.sql;
import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link calParser}.
 */
public interface calListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link calParser#prog}.
	 * @param ctx the parse tree
	 */
	void enterProg(calParser.ProgContext ctx);
	/**
	 * Exit a parse tree produced by {@link calParser#prog}.
	 * @param ctx the parse tree
	 */
	void exitProg(calParser.ProgContext ctx);
	/**
	 * Enter a parse tree produced by the {@code printExpr}
	 * labeled alternative in {@link calParser#stat}.
	 * @param ctx the parse tree
	 */
	void enterPrintExpr(calParser.PrintExprContext ctx);
	/**
	 * Exit a parse tree produced by the {@code printExpr}
	 * labeled alternative in {@link calParser#stat}.
	 * @param ctx the parse tree
	 */
	void exitPrintExpr(calParser.PrintExprContext ctx);
	/**
	 * Enter a parse tree produced by the {@code assign}
	 * labeled alternative in {@link calParser#stat}.
	 * @param ctx the parse tree
	 */
	void enterAssign(calParser.AssignContext ctx);
	/**
	 * Exit a parse tree produced by the {@code assign}
	 * labeled alternative in {@link calParser#stat}.
	 * @param ctx the parse tree
	 */
	void exitAssign(calParser.AssignContext ctx);
	/**
	 * Enter a parse tree produced by the {@code blank}
	 * labeled alternative in {@link calParser#stat}.
	 * @param ctx the parse tree
	 */
	void enterBlank(calParser.BlankContext ctx);
	/**
	 * Exit a parse tree produced by the {@code blank}
	 * labeled alternative in {@link calParser#stat}.
	 * @param ctx the parse tree
	 */
	void exitBlank(calParser.BlankContext ctx);
	/**
	 * Enter a parse tree produced by the {@code parens}
	 * labeled alternative in {@link calParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterParens(calParser.ParensContext ctx);
	/**
	 * Exit a parse tree produced by the {@code parens}
	 * labeled alternative in {@link calParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitParens(calParser.ParensContext ctx);
	/**
	 * Enter a parse tree produced by the {@code MulDiv}
	 * labeled alternative in {@link calParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterMulDiv(calParser.MulDivContext ctx);
	/**
	 * Exit a parse tree produced by the {@code MulDiv}
	 * labeled alternative in {@link calParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitMulDiv(calParser.MulDivContext ctx);
	/**
	 * Enter a parse tree produced by the {@code AddSub}
	 * labeled alternative in {@link calParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterAddSub(calParser.AddSubContext ctx);
	/**
	 * Exit a parse tree produced by the {@code AddSub}
	 * labeled alternative in {@link calParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitAddSub(calParser.AddSubContext ctx);
	/**
	 * Enter a parse tree produced by the {@code id}
	 * labeled alternative in {@link calParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterId(calParser.IdContext ctx);
	/**
	 * Exit a parse tree produced by the {@code id}
	 * labeled alternative in {@link calParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitId(calParser.IdContext ctx);
	/**
	 * Enter a parse tree produced by the {@code int}
	 * labeled alternative in {@link calParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterInt(calParser.IntContext ctx);
	/**
	 * Exit a parse tree produced by the {@code int}
	 * labeled alternative in {@link calParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitInt(calParser.IntContext ctx);
}