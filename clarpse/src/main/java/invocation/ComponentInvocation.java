package invocation;

import java.util.ArrayList;

/**
 * Represents an invocation of a component in the code base.
 *
 * @author Muntazir Fadhel
 */
public abstract class ComponentInvocation {

    private final String name;
    private final ArrayList<Integer> invocationLineNums = new ArrayList<Integer>();

    public ComponentInvocation(final String invocationComponentName, final int lineNum) {
        name = invocationComponentName;
        invocationLineNums.add(lineNum);
    }

    public ComponentInvocation(final ComponentInvocation invocation) {
        for (final Integer lineNum : invocation.lines()) {
            invocationLineNums.add(lineNum);
        }
        name = invocation.name();
    }

    public String name() {
        return name;
    }

    public void insertLineNum(final int invocationLineNums) {
        if (!this.invocationLineNums.contains(invocationLineNums)) {
            this.invocationLineNums.add(invocationLineNums);
        }
    }

    public ArrayList<Integer> lines() {
        return invocationLineNums;
    }
}
