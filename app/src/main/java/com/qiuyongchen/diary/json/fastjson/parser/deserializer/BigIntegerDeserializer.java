package com.qiuyongchen.diary.json.fastjson.parser.deserializer;

import com.qiuyongchen.diary.json.fastjson.parser.DefaultJSONParser;
import com.qiuyongchen.diary.json.fastjson.parser.JSONLexer;
import com.qiuyongchen.diary.json.fastjson.parser.JSONToken;
import com.qiuyongchen.diary.json.fastjson.util.TypeUtils;

import java.lang.reflect.Type;
import java.math.BigInteger;

public class BigIntegerDeserializer implements ObjectDeserializer {

    public final static BigIntegerDeserializer instance = new BigIntegerDeserializer();

    @SuppressWarnings("unchecked")
    public static <T> T deserialze(DefaultJSONParser parser) {
        final JSONLexer lexer = parser.getLexer();
        if (lexer.token() == JSONToken.LITERAL_INT) {
            String val = lexer.numberString();
            lexer.nextToken(JSONToken.COMMA);
            return (T) new BigInteger(val);
        }

        Object value = parser.parse();

        if (value == null) {
            return null;
        }

        return (T) TypeUtils.castToBigInteger(value);
    }

    @SuppressWarnings("unchecked")
    public <T> T deserialze(DefaultJSONParser parser, Type clazz, Object fieldName) {
        return (T) deserialze(parser);
    }

    public int getFastMatchToken() {
        return JSONToken.LITERAL_INT;
    }
}
