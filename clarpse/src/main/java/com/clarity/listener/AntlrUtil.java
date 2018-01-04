package com.clarity.listener;

import com.clarity.compiler.RawFile;

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

    public static String getOriginalText(RawFile file, int startLine, int endLine, int beginPos, int endPos) {
        String result = "";
        String[] fileLines = file.content().split("\n");
        for (int i = startLine - 1; i <= endLine -1; i ++) {
            int beginIndex = 0;
            int endIndex = fileLines[i].length();
            if (i == startLine - 1) {
                beginIndex = beginPos;
            }
            if (i == endLine - 1) {
                endIndex = endPos;
            }
            result += fileLines[i].substring(beginIndex, endIndex + 1);
        }
        return result;
    }
}
