package com.clarity.parser;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import com.clarity.sourcemodel.OOPSourceCodeModel;

/**
 * Abstract parser that will parse projects and generate output that Clarity can
 * understand and use.
 * 
 * @author Muntazir Fadhel
 */
public abstract class AbstractClarityParser {

	/**
	 * @return Source Model generated from parsing the project.
	 * @throws IllegalAccessException
	 *             Exception
	 * @throws InstantiationException
	 *             Exception
	 * @throws ClassNotFoundException
	 *             Exception
	 * @throws IllegalArgumentException
	 *             Exception
	 * @throws InvocationTargetException
	 *             Exception
	 * @throws NoSuchMethodException
	 *             Exception
	 * @throws SecurityException
	 *             Exception
	 * @throws IOException
	 *             Exception
	 */
	public abstract OOPSourceCodeModel extractSourceModel() throws IllegalAccessException, InstantiationException,
	        ClassNotFoundException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException,
	        SecurityException, IOException;

}
