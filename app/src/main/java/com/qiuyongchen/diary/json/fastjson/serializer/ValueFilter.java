package com.qiuyongchen.diary.json.fastjson.serializer;

public interface ValueFilter {

    Object process(Object source, String name, Object value);
}
