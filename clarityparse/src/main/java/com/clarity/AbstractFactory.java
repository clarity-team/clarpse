package com.clarity;

import com.clarity.parser.IClarityParser;

/**
 * @author Muntazir Fadhel
 */
public abstract class AbstractFactory {

    /**
     * @param type
     *            String representing type of parser to get.
     * @return parser object
     * @throws Exception
     *             when factory cannot handle/process input.
     */
    public abstract IClarityParser getParsingTool(String type) throws Exception;

}
