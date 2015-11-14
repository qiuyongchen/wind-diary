package com.qiuyongchen.diary.json.fastjson.parser.deserializer;

import com.qiuyongchen.diary.json.fastjson.parser.DefaultJSONParser;
import com.qiuyongchen.diary.json.fastjson.parser.JSONLexer;
import com.qiuyongchen.diary.json.fastjson.parser.JSONToken;
import com.qiuyongchen.diary.json.fastjson.util.TypeUtils;

import java.lang.reflect.Type;
import java.util.concurrent.atomic.AtomicLong;

public class LongDeserializer implements ObjectDeserializer {

    public final static LongDeserializer instance = new LongDeserializer();

    @SuppressWarnings("unchecked")
    public <T> T deserialze(DefaultJSONParser parser, Type clazz, Object fieldName) {
        final JSONLexer lexer = parser.getLexer();

        Long longObject;
        if (lexer.token() == JSONToken.LITERAL_INT) {
            long longValue = lexer.longValue();
            lexer.nextToken(JSONToken.COMMA);
            longObject = Long.valueOf(longValue);
        } else {

            Object value = parser.parse();

            if (value == null) {
                return null;
            }

            longObject = TypeUtils.castToLong(value);
        }

        if (clazz == AtomicLong.class) {
            return (T) new AtomicLong(longObject.longValue());
        }

        return (T) longObject;
    }

    public int getFastMatchToken() {
        return JSONToken.LITERAL_INT;
    }
}
