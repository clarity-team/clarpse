package com.clarity;

import com.clarity.parser.IClarityParser;

/**
 * @author Muntazir Fadhel
 */
public abstract class AbstractFactory {

    public abstract IClarityParser getParsingTool(String type) throws Exception;

}
