package com.hadii.test;

import com.hadii.clarpse.compiler.ProjectFile;
import com.hadii.clarpse.reference.ComponentReference;
import com.hadii.clarpse.reference.SimpleTypeReference;
import com.hadii.clarpse.reference.TypeImplementationReference;
import com.hadii.clarpse.sourcemodel.Component;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.HashSet;
import java.util.LinkedHashSet;

import static org.junit.Assert.assertTrue;


public class ComponentReferenceTest {

    @Test
    public void testEqualReferences() {
        ComponentReference refA = new SimpleTypeReference("test");
        ComponentReference refB = new SimpleTypeReference("test");
        HashSet set = new HashSet<>();
        set.add(refA);
        set.add(refB);
        assert(set.size() == 1);
    }

    @Test
    public void testNonEqualByReferencesByType() {
        ComponentReference refA = new TypeImplementationReference("test");
        ComponentReference refB = new SimpleTypeReference("test");
        HashSet set = new HashSet<>();
        set.add(refA);
        set.add(refB);
        assert(set.size() == 2);
    }

    @Test
    public void testNonEqualByReferencesByTypeAndValue() {
        ComponentReference refA = new TypeImplementationReference("test");
        ComponentReference refB = new SimpleTypeReference("testA");
        HashSet set = new HashSet<>();
        set.add(refA);
        set.add(refB);
        assert(set.size() == 2);
    }
}