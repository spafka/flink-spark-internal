package com.github.spafka.spark.sql;

public class MyCalVisitot extends calBaseVisitor<Integer> {

    @Override
    public Integer visitAssign(calParser.AssignContext ctx) {
        String id = ctx.ID().getText();
        Integer value = visit(ctx.expr());
        return value;

    }

    @Override
    public Integer visitInt(calParser.IntContext ctx) {
        return Integer.valueOf(ctx.INT().getText());
    }

    @Override
    public Integer visitMulDiv(calParser.MulDivContext ctx) {
        Integer left = visit(ctx.expr(0));
        Integer right = visit(ctx.expr(1));

        if (ctx.op.getType() == calParser.MUL){
            return left * right;
        }else{
            return left / right;
        }

    }
}
