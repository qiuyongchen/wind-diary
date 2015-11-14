package com.qiuyongchen.diary.json.fastjson.parser.deserializer;

import java.lang.reflect.Type;
import java.util.TimeZone;

import com.qiuyongchen.diary.json.fastjson.parser.DefaultJSONParser;
import com.qiuyongchen.diary.json.fastjson.parser.JSONToken;

public class TimeZoneDeserializer implements ObjectDeserializer {
    public final static TimeZoneDeserializer instance = new TimeZoneDeserializer();

    @SuppressWarnings("unchecked")
    public <T> T deserialze(DefaultJSONParser parser, Type clazz, Object fieldName) {
        
        String id = (String) parser.parse();
        
        if (id == null) {
            return null;
        }
        
        return (T) TimeZone.getTimeZone(id);
    }

    public int getFastMatchToken() {
        return JSONToken.LITERAL_STRING;
    }
}
