package com.xyxb.backend.common.tool.apiApp;

import cn.hutool.Hutool;
import cn.hutool.core.codec.Base64;
import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import com.alibaba.fastjson.JSONObject;

public class Test {
    private static final Log log = LogFactory.get();
    public static void main(String[] args) {
        String plainCredentials = "b5dcfcde36fa4a16ad6c75ab0d4e4b92:22cb9c85e57f4105ad0317b545bae677";
        // base64Credentials 就是你要的 Authorization 值，是一个使用 Base64 算法编码的 Credential

        String base64Credentials = new String(Base64.encode(plainCredentials.getBytes()));
        log.info(base64Credentials);


    }
}
