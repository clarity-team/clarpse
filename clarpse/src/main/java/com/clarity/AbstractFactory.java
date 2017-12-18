package com.clarity;

import com.clarity.compiler.ClarpseCompiler;

/**
 * @author Muntazir Fadhel
 */
public abstract class AbstractFactory {

    public abstract ClarpseCompiler getParsingTool(String type) throws Exception;

}
