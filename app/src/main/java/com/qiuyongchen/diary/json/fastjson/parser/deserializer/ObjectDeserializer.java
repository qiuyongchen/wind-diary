package com.qiuyongchen.diary.json.fastjson.parser.deserializer;

import java.lang.reflect.Type;

import com.qiuyongchen.diary.json.fastjson.parser.DefaultJSONParser;

public interface ObjectDeserializer {
    <T> T deserialze(DefaultJSONParser parser, Type type, Object fieldName);
    
    int getFastMatchToken();
}
