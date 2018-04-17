package com.clarity.listener;

import com.clarity.sourcemodel.Component;
import com.clarity.sourcemodel.OOPSourceModelConstants;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.misc.Interval;

import java.util.List;
import java.util.Stack;

/**
 * Contains utility operations shared amongst Clarpse Compilers.
 */
public class ParseUtil {

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

    public static void pointParentsToGivenChild(Component childCmp, Stack<? extends Component> componentStack) {

        if (!componentStack.isEmpty()) {
            final String parentName = childCmp.parentUniqueName();
            for (int i = componentStack.size() - 1; i >= 0; i--) {
                if (componentStack.get(i).uniqueName().equals(parentName)) {
                    componentStack.get(i).insertChildComponent(childCmp.uniqueName());
                }
            }
        }
    }


    public static boolean componentStackContainsMethod(Stack<? extends Component> componentStack) {
        return componentStackContainsComponentType(componentStack, OOPSourceModelConstants.ComponentType.METHOD,
                OOPSourceModelConstants.ComponentType.CONSTRUCTOR);
    }

    public static boolean componentStackContainsInterface(Stack<? extends Component> componentStack) {
        return componentStackContainsComponentType(componentStack, OOPSourceModelConstants.ComponentType.INTERFACE);
    }

    private static boolean componentStackContainsComponentType(Stack<? extends Component> componentStack,
                                                               OOPSourceModelConstants.ComponentType... componentTypes) {
        for (Component cmp : componentStack) {
            for (OOPSourceModelConstants.ComponentType type : componentTypes) {
                if (cmp.componentType() == type) {
                    return true;
                }
            }
        }
        return false;
    }
}
