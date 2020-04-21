package com.clarity.compiler;

import java.util.Comparator;

public class LengthComp implements Comparator<String> {
    @Override
    public int compare(String o1, String o2) {
        return Integer.compare(o2.length(), o1.length());
    }
}
