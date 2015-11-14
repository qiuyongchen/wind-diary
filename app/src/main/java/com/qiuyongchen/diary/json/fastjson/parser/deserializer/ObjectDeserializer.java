package com.qiuyongchen.diary.json.fastjson.parser.deserializer;

import com.qiuyongchen.diary.json.fastjson.parser.DefaultJSONParser;

import java.lang.reflect.Type;

public interface ObjectDeserializer {
    <T> T deserialze(DefaultJSONParser parser, Type type, Object fieldName);
    
    int getFastMatchToken();
}
