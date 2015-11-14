package com.qiuyongchen.diary.json.fastjson.serializer;

public interface ValueFilter extends SerializeFilter {

    Object process(Object source, String name, Object value);
}
