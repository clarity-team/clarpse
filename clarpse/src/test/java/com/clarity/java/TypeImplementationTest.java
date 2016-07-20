package com.clarity.java;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.clarity.ClarpseUtil;
import com.clarity.invocation.TypeExtension;
import com.clarity.invocation.TypeImplementation;
import com.clarity.parser.Lang;
import com.clarity.parser.ParseRequestContent;
import com.clarity.parser.ClarpseProject;
import com.clarity.parser.RawFile;
import com.clarity.sourcemodel.OOPSourceCodeModel;
import com.clarity.sourcemodel.OOPSourceModelConstants.ComponentInvocations;

/**
 * Ensure component invocation data of a given class is accurate.
 *
 * @author Muntazir Fadhel
 */
public class TypeImplementationTest {

	@Test
	public void testAccurateImplementedTypes() throws Exception {

		String code = "package com; \n public class ClassA implements ClassD { }";
		OOPSourceCodeModel generatedSourceModel;
		final ParseRequestContent rawData = new ParseRequestContent(Lang.JAVA);
		rawData.insertFile(new RawFile("file1", code));
		final ClarpseProject parseService = new ClarpseProject(rawData);
		generatedSourceModel = parseService.result();
		Assert.assertTrue(generatedSourceModel.getComponent("com.ClassA").componentInvocations(ComponentInvocations.IMPLEMENTATION)
				.get(0).invokedComponent().equals("com.ClassD"));
	}
	
	@Test
	public void testAccurateMultipleImplementedTypes() throws Exception {

		String code = "package com; \n public class ClassA implements ClassD, ClassE { }";
		OOPSourceCodeModel generatedSourceModel;
		final ParseRequestContent rawData = new ParseRequestContent(Lang.JAVA);
		rawData.insertFile(new RawFile("file1", code));
		final ClarpseProject parseService = new ClarpseProject(rawData);
		generatedSourceModel = parseService.result();
		Assert.assertTrue(generatedSourceModel.getComponent("com.ClassA").componentInvocations(ComponentInvocations.IMPLEMENTATION)
				.get(0).invokedComponent().equals("com.ClassD"));
		Assert.assertTrue(generatedSourceModel.getComponent("com.ClassA").componentInvocations(ComponentInvocations.IMPLEMENTATION)
				.get(0).invokedComponent().equals("com.ClassE"));
	}

	@Test
	public void testAccurateImplementedTypesSize() throws Exception {

		String code = "package com; \n public class ClassA extends ClassD { }";
		OOPSourceCodeModel generatedSourceModel;
		final ParseRequestContent rawData = new ParseRequestContent(Lang.JAVA);
		rawData.insertFile(new RawFile("file1", code));
		final ClarpseProject parseService = new ClarpseProject(rawData);
		generatedSourceModel = parseService.result();
		Assert.assertTrue(generatedSourceModel.getComponent("com.ClassA").componentInvocations(ComponentInvocations.IMPLEMENTATION)
				.size() == 1);
	}
	
	@Test
	public void testAccurateMultipleImplementedTypesSize() throws Exception {

		String code = "package com; \n public class ClassA extends ClassD, ClassE { }";
		OOPSourceCodeModel generatedSourceModel;
		final ParseRequestContent rawData = new ParseRequestContent(Lang.JAVA);
		rawData.insertFile(new RawFile("file1", code));
		final ClarpseProject parseService = new ClarpseProject(rawData);
		generatedSourceModel = parseService.result();
		Assert.assertTrue(generatedSourceModel.getComponent("com.ClassA").componentInvocations(ComponentInvocations.IMPLEMENTATION)
				.size() == 2);
	}

	@Test
	public void testAccurateImplementedTypesForNestedClass() throws Exception {

		String code = "package com; \n public class ClassA { public class ClassB implements ClassD{} }";
		OOPSourceCodeModel generatedSourceModel;
		final ParseRequestContent rawData = new ParseRequestContent(Lang.JAVA);
		rawData.insertFile(new RawFile("file1", code));
		final ClarpseProject parseService = new ClarpseProject(rawData);
		generatedSourceModel = parseService.result();
		Assert.assertTrue(generatedSourceModel.getComponent("com.ClassA.ClassB").componentInvocations(ComponentInvocations.IMPLEMENTATION)
				.get(0).invokedComponent().equals("com.ClassD"));

		Assert.assertTrue(generatedSourceModel.getComponent("com.ClassA").componentInvocations(ComponentInvocations.IMPLEMENTATION)
				.size() == 1);
	}
	
	@Test
	public void testAccurateExtendedTypesSizeForNestedClass() throws Exception {

		String code = "package com; \n public class ClassA { public class ClassB implements ClassD{} }";
		OOPSourceCodeModel generatedSourceModel;
		final ParseRequestContent rawData = new ParseRequestContent(Lang.JAVA);
		rawData.insertFile(new RawFile("file1", code));
		final ClarpseProject parseService = new ClarpseProject(rawData);
		generatedSourceModel = parseService.result();
		Assert.assertTrue(generatedSourceModel.getComponent("com.ClassA.ClassB").componentInvocations(ComponentInvocations.IMPLEMENTATION)
				.size() == 1);
	}
}
