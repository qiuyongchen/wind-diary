package com.qiuyongchen.diary.json.fastjson.serializer;

public interface NameFilter {

    String process(Object source, String name, Object value);
}
