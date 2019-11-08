package com.xyxb.backend.common.tool.apiApp;

import com.alibaba.fastjson.JSONObject;

public class Test {
    public static void main(String[] args) {
        String json = "{'aa':123,'bb':{'aa':\"456\"}}";

        JSONObject obj = JSONObject.parseObject(json);
        System.out.println(obj.getJSONObject("bb").toJSONString());
    }
}
