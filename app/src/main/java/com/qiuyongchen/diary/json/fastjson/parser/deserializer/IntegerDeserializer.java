package com.qiuyongchen.diary.json.fastjson.parser.deserializer;

import com.qiuyongchen.diary.json.fastjson.parser.DefaultJSONParser;
import com.qiuyongchen.diary.json.fastjson.parser.JSONLexer;
import com.qiuyongchen.diary.json.fastjson.parser.JSONToken;
import com.qiuyongchen.diary.json.fastjson.util.TypeUtils;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.concurrent.atomic.AtomicInteger;

public class IntegerDeserializer implements ObjectDeserializer {

    public final static IntegerDeserializer instance = new IntegerDeserializer();

    @SuppressWarnings("unchecked")
    public <T> T deserialze(DefaultJSONParser parser, Type clazz, Object fieldName) {
        final JSONLexer lexer = parser.getLexer();

        if (lexer.token() == JSONToken.NULL) {
            lexer.nextToken(JSONToken.COMMA);
            return null;
        }

        Integer intObj;
        if (lexer.token() == JSONToken.LITERAL_INT) {
            int val = lexer.intValue();
            lexer.nextToken(JSONToken.COMMA);
            intObj = Integer.valueOf(val);
        } else if (lexer.token() == JSONToken.LITERAL_FLOAT) {
            BigDecimal decimalValue = lexer.decimalValue();
            lexer.nextToken(JSONToken.COMMA);
            intObj = Integer.valueOf(decimalValue.intValue());
        } else {
            Object value = parser.parse();

            intObj = TypeUtils.castToInt(value);
        }

        if (clazz == AtomicInteger.class) {
            return (T) new AtomicInteger(intObj.intValue());
        }

        return (T) intObj;
    }

    public int getFastMatchToken() {
        return JSONToken.LITERAL_INT;
    }
}
