package com.clarity.parser;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.RuleContext;
import org.antlr.v4.runtime.misc.Interval;

/**
 * Utility class for Antlr.
 *
 * @author Muntazir Fadhel
 */
public final class AntlrUtil {

    /**
     * @param context
     *            context whose String we need to get the formatted text off.
     * @return formatted text as a string
     */
    public static String getFormattedText(final ParserRuleContext context) {

        try {
            if ((context.start == null) || (context.stop == null) || (context.start.getStartIndex() < 0)
                    || (context.stop.getStopIndex() < 0)) {
                return context.getText(); // fallback
            }

            return context.start.getInputStream().getText(
                    Interval.of(context.start.getStartIndex(), context.stop.getStopIndex()));
        } catch (final StringIndexOutOfBoundsException e) {
            e.printStackTrace();
            return "";
        }
    }

    private AntlrUtil() {

    }

    /**
     * A terrible way of getting multi-line comments associated with a given
     * parser rule context. Given the source code file with all the previous
     * contexts removed up to the current one for which the comment is required,
     * this method searches for the comment begin and end strings and copies the
     * content in between if they exist. In the future it would be nice to have
     * the ANTLR grammar process the comments so that it can be handled in much
     * neater way in the ANTLR generated ParseTreeListener.
     *
     * @param newCtx
     *            parser rule context
     * @param fileSourceCode
     *            The source code file
     * @param commentBeginSymbol
     *            String representing the beginning of a block comment eg) '/*'
     *            in java
     * @param commentEndSymbol
     *            commentEndSymbol String representing the ending of a block
     *            comment eg) an asterisk followed by a slash in java
     * @return Doc comment associated with the context.
     */
    public static String getContextMultiLineComment(final RuleContext newCtx, final String fileSourceCode,
            final String commentBeginSymbol, final String commentEndSymbol) {

        String multiLineComment = "";
        final String contextCode = getFormattedText((ParserRuleContext) newCtx);
        final int rangeEnd = fileSourceCode.indexOf(contextCode);
        final String searchString = fileSourceCode.substring(0, rangeEnd);
        if (searchString.contains(commentBeginSymbol) && searchString.contains(commentEndSymbol)) {
            final int commentBeginPos = searchString.lastIndexOf(commentBeginSymbol);
            final int commentEndPos = searchString.lastIndexOf(commentEndSymbol);
            if ((commentEndPos >= 0) && (commentBeginPos >= 0) && (commentEndPos > commentBeginPos)) {
                final String comment = fileSourceCode.substring(commentBeginPos,
                        commentEndPos + commentEndSymbol.length());
                final int contentInBetweenCommentAndComponentBegin = fileSourceCode.indexOf(comment) + comment.length();
                final String contentInBetweenCommentAndComponent = fileSourceCode.substring(
                        contentInBetweenCommentAndComponentBegin, rangeEnd);
                if (contentInBetweenCommentAndComponent.trim().equals("")) {
                    multiLineComment = comment;
                }
            }
        }
        return multiLineComment;
    }
}
