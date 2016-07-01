package com.clarity;

import com.clarity.parser.ClarpseParser;

/**
 * @author Muntazir Fadhel
 */
public abstract class AbstractFactory {

    public abstract ClarpseParser getParsingTool(String type) throws Exception;

}
