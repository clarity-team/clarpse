package com.hadii.clarpse.listener;

import com.hadii.clarpse.reference.ComponentReference;
import com.hadii.clarpse.reference.TypeExtensionReference;
import com.hadii.clarpse.reference.TypeImplementationReference;
import com.hadii.clarpse.sourcemodel.Component;
import com.hadii.clarpse.sourcemodel.OOPSourceCodeModel;
import com.hadii.clarpse.sourcemodel.OOPSourceModelConstants;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.misc.Interval;

import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Stack;

/**
 * Contains utility operations shared amongst Clarpse Compilers.
 */
class ParseUtil {

    static String goLangComments(int componentStartLine, List<String> sourceFile) {
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

    static void copyRefsToParents(Component originalCompoennt, Stack<Component> componentStack) {
        // bubble up the completing component's invocations to it's parent components
        // that are currently on the stack
        for (final Component parentCmp : componentStack) {
            final Iterator<ComponentReference> invocationIterator = originalCompoennt.references().iterator();
            while (invocationIterator.hasNext()) {
                ComponentReference invocation = invocationIterator.next();
                // We do not want to bubble up type implementations and
                // extensions to the parent component because a child class for example
                // could extend its containing class component. Without this check
                // this would cause the parent class to have a type extension to itself
                // which will cause problems down the line.
                if (!(originalCompoennt.componentType() == OOPSourceModelConstants.ComponentType.FIELD
                        && parentCmp.componentType() == OOPSourceModelConstants.ComponentType.CONSTRUCTOR)
                        && !(invocation instanceof TypeExtensionReference || invocation instanceof TypeImplementationReference)) {
                    parentCmp.insertComponentRef(invocation);
                }
            }
        }
    }

    /**
     * Retrieves the most recently inserted base component on the stack.
     */
    static Component newestBaseComponent(Stack<Component> componentStack) throws Exception {
        Component latestBaseCmp = null;
        for (Component cmp : componentStack) {
            if (cmp.componentType().isBaseComponent()) {
                latestBaseCmp = cmp;
            }
        }
        if (latestBaseCmp != null) {
            return latestBaseCmp;
        } else {
            throw new Exception("There are no base components on the stack right now!");
        }
    }

    /**
     * Retrieves the most recently inserted base component on the stack.
     */
    static Component newestMethodComponent(Stack<Component> componentStack) throws Exception {
        Component latestMethodCmp = null;
        for (Component cmp : componentStack) {
            if (cmp.componentType().isMethodComponent()) {
                latestMethodCmp = cmp;
            }
        }

        if (latestMethodCmp != null) {
            return latestMethodCmp;
        } else {
            throw new Exception("There are no method components on the stack right now!");
        }
    }

    /**
     * Generates appropriate name for the component. Uses the current stack of
     * parents components as prefixes to the name.
     */
    static String generateComponentName(final String identifier, Stack<Component> componentStack) {
        String componentName = "";

        if (!componentStack.isEmpty()) {
            final Component completedCmp = componentStack.peek();
            componentName = completedCmp.componentName() + "." + identifier;
        } else {
            componentName = identifier;
        }
        return componentName;
    }

    // class component cyclo complexity is a weighted average of its method children complexities.
    static int calculateClassCyclo(Component component, OOPSourceCodeModel srcModel) {
        int childCount = 0;
        int complexityTotal = 0;
        for (String childrenName : component.children()) {
            Optional<Component> child = srcModel.getComponent(childrenName);
            if (child.isPresent() && child.get().componentType().isMethodComponent()) {
                childCount += 1;
                complexityTotal += child.get().cyclo();
            }
        }
        if (childCount != 0 && complexityTotal != 0) {
            return (complexityTotal / childCount);
        } else {
            return 0;
        }
    }

    static String originalText(ParserRuleContext ctx) {
        return ctx.getStart().getInputStream().getText(Interval.of(ctx.getStart().getStartIndex(), ctx.getStop().getStopIndex()));
    }

    static void pointParentsToGivenChild(Component childCmp, Stack<? extends Component> componentStack) {

        if (!componentStack.isEmpty()) {
            final String parentName = childCmp.parentUniqueName();
            for (int i = componentStack.size() - 1; i >= 0; i--) {
                if (componentStack.get(i).uniqueName().equals(parentName)) {
                    componentStack.get(i).insertChildComponent(childCmp.uniqueName());
                }
            }
        }
    }


    static boolean componentStackContainsMethod(Stack<? extends Component> componentStack) {
        return componentStackContainsComponentType(componentStack, OOPSourceModelConstants.ComponentType.METHOD,
                OOPSourceModelConstants.ComponentType.CONSTRUCTOR);
    }

    static boolean componentStackContainsInterface(Stack<? extends Component> componentStack) {
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
