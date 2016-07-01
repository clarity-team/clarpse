package com.clarity.parser;

import com.clarity.sourcemodel.OOPSourceCodeModel;

/**
 * Dictates that any parser implementing this interface should have the ability
 * to return an OOP Source Model representation of the parsed data.
 *
 * @author Muntazir Fadhel
 */
public interface ClarpseParser {

    /**
     * @return Source Model generated from parsing the project.
     */
    OOPSourceCodeModel extractParseResult(ParseRequestContent rawData) throws Exception;

}
