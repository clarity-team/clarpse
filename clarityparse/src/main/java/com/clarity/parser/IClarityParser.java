package com.clarity.parser;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import com.clarity.sourcemodel.OOPSourceCodeModel;

/**
 * Dictates that any parser implementing this interface should have the ability
 * to return an OOP Source Model representation of the parsed data.
 *
 * @author Muntazir Fadhel
 */
public interface IClarityParser {

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
     * @throws InterruptedException
     *             Exception
     */
    OOPSourceCodeModel extractParseResult(ParseRequestContent rawData) throws Exception;

}
