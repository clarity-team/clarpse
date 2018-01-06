package com.clarity.listener;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.misc.Interval;

import java.util.List;

public class AntlrUtil {

    public static String goLangComments(int componentStartLine, List<String> sourceFile) {
        String comment = "";
        int i = componentStartLine - 2;
        String currLine = sourceFile.get(i).trim();

        while (i > 0 && (currLine.startsWith("//") || currLine.isEmpty())) {
            if (currLine.startsWith("//")) {
                comment = currLine.replace("//", "").trim() + " " + comment;
            }
            i--;
            currLine = sourceFile.get(i).trim();
        }
        return comment.trim();
    }

    public static String originalText(ParserRuleContext ctx) {
        return ctx.getStart().getInputStream().getText(Interval.of(ctx.getStart().getStartIndex(), ctx.getStop().getStopIndex()));
    }
}
