package com.clarity.java;

import org.junit.Assert;
import org.junit.Test;

import com.clarity.parser.Lang;
import com.clarity.parser.ParseRequestContent;
import com.clarity.parser.ClarpseProject;
import com.clarity.parser.RawFile;
import com.clarity.sourcemodel.OOPSourceCodeModel;
import com.clarity.sourcemodel.OOPSourceModelConstants.ComponentInvocations;

/**
 * Ensure component type extensions invocations are accurate.
 *
 * @author Muntazir Fadhel
 */
public class TypeExtensionTest {

	@Test
	public void testAccurateExtendedTypes() throws Exception {

		String code = "package com; \n public class ClassA extends ClassD { }";
		OOPSourceCodeModel generatedSourceModel;
		final ParseRequestContent rawData = new ParseRequestContent(Lang.JAVA);
		rawData.insertFile(new RawFile("file1", code));
		final ClarpseProject parseService = new ClarpseProject(rawData);
		generatedSourceModel = parseService.result();
		Assert.assertTrue(generatedSourceModel.getComponent("com.ClassA").componentInvocations(ComponentInvocations.EXTENSION)
				.get(0).invokedComponent().equals("com.ClassD"));
	}

	@Test
	public void testAccurateExtendedTypesSize() throws Exception {

		String code = "package com; \n public class ClassA extends ClassD { }";
		OOPSourceCodeModel generatedSourceModel;
		final ParseRequestContent rawData = new ParseRequestContent(Lang.JAVA);
		rawData.insertFile(new RawFile("file1", code));
		final ClarpseProject parseService = new ClarpseProject(rawData);
		generatedSourceModel = parseService.result();
		Assert.assertTrue(generatedSourceModel.getComponent("com.ClassA").componentInvocations(ComponentInvocations.EXTENSION)
				.size() == 1);
	}

	@Test
	public void testAccurateExtendedTypesForNestedClass() throws Exception {

		String code = "package com; \n public class ClassA { public class ClassB extends ClassD{} }";
		OOPSourceCodeModel generatedSourceModel;
		final ParseRequestContent rawData = new ParseRequestContent(Lang.JAVA);
		rawData.insertFile(new RawFile("file1", code));
		final ClarpseProject parseService = new ClarpseProject(rawData);
		generatedSourceModel = parseService.result();
		Assert.assertTrue(generatedSourceModel.getComponent("com.ClassA.ClassB").componentInvocations(ComponentInvocations.EXTENSION)
				.get(0).invokedComponent().equals("com.ClassD"));

		Assert.assertTrue(generatedSourceModel.getComponent("com.ClassA.ClassB").componentInvocations(ComponentInvocations.EXTENSION)
				.size() == 1);
	}
	
	@Test
	public void testAccurateExtendedTypesSizeForNestedClass() throws Exception {

		String code = "package com; \n public class ClassA { public class ClassB extends ClassD{} }";
		OOPSourceCodeModel generatedSourceModel;
		final ParseRequestContent rawData = new ParseRequestContent(Lang.JAVA);
		rawData.insertFile(new RawFile("file1", code));
		final ClarpseProject parseService = new ClarpseProject(rawData);
		generatedSourceModel = parseService.result();
		Assert.assertTrue(generatedSourceModel.getComponent("com.ClassA.ClassB").componentInvocations(ComponentInvocations.EXTENSION)
				.size() == 1);
	}
}
