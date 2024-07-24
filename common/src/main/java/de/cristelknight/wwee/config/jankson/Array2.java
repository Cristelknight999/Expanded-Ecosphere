package de.cristelknight.wwee.config.jankson;

import blue.endless.jankson.JsonArray;
import blue.endless.jankson.JsonGrammar;


public class Array2 extends JsonArray {

    @Override
    public String toJson(JsonGrammar grammar, int depth) {
        return super.toJson(ConfigUtil.JSON_GRAMMAR_BUILDER.get().printWhitespace(false).build(), depth);
    }
}