package com.qiuyongchen.diary.json.fastjson.parser.deserializer;

import java.lang.reflect.Type;
import java.net.URI;

import com.qiuyongchen.diary.json.fastjson.parser.DefaultJSONParser;
import com.qiuyongchen.diary.json.fastjson.parser.JSONToken;

public class URIDeserializer implements ObjectDeserializer {
    public final static URIDeserializer instance = new URIDeserializer();

    @SuppressWarnings("unchecked")
    public <T> T deserialze(DefaultJSONParser parser, Type clazz, Object fieldName) {
        
        String uri = (String) parser.parse();
        
        if (uri == null) {
            return null;
        }
        
        return (T) URI.create(uri);
    }

    public int getFastMatchToken() {
        return JSONToken.LITERAL_STRING;
    }
}
