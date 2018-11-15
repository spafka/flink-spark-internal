package com.github.spafka.spark.sql;


import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;

public class Antlr4Test {

    public static void main(String[] args) {
        ANTLRInputStream inputStream = new ANTLRInputStream("1 + 2 + 3 * 4+ 6 / 2");
        calLexer lexer = new calLexer(inputStream);

        CommonTokenStream tokenStream = new CommonTokenStream(lexer);
        calParser parser = new calParser(tokenStream);
        ParseTree parseTree = parser.prog();
        MyCalVisitot visitor = new MyCalVisitot();
        Integer rtn = visitor.visit(parseTree);
        System.out.println("#result#"+rtn.toString());
    }
}
