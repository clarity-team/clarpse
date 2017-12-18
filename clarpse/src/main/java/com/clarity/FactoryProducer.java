package com.clarity;

import java.util.TreeMap;

public final class FactoryProducer {

    public static final String PARSE_KEYWORD = "parser";

    @SuppressWarnings("serial")
    private static final TreeMap<String, AbstractFactory> FACTORY_MAP = new TreeMap<String, AbstractFactory>(
            String.CASE_INSENSITIVE_ORDER) {
        {
            put("PARSER", new com.clarity.compiler.CompilerFactory());
        }
    };

    public AbstractFactory getFactory(final String choice) throws Exception {

        if (FACTORY_MAP.containsKey(choice)) {
            return FACTORY_MAP.get(choice);
        } else {
            throw new Exception("Could not get factory for: " + choice);
        }
    }

    public FactoryProducer() {
    }
}
