package de.cristelknight999.wwoo.config.r;

import blue.endless.jankson.JsonArray;
import blue.endless.jankson.JsonGrammar;

import static de.cristelknight999.wwoo.config.r.ConfigUtil.JSON_GRAMMAR_BUILDER;

public class Array2 extends JsonArray {

    @Override
    public String toJson(JsonGrammar grammar, int depth) {
        return super.toJson(JSON_GRAMMAR_BUILDER.get().printWhitespace(false).build(), depth);
    }
}