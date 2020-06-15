package com.xyxb.backend.common.tool.apiApp;

import cn.hutool.http.Header;
import cn.hutool.http.HttpRequest;
import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;

import java.net.CookieManager;
import java.net.CookieStore;
import java.net.HttpCookie;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HttpUtil {
    private static final Log log = LogFactory.get();
    private static String LONG_URL = "http://47.112.108.146/showdoc-master/server/index.php?s=/api/user/login";
    public static void main(String[] args) {
        Map<String,Object> paramMap = new HashMap<>();
        paramMap.put("username","宏锋");
        paramMap.put("password","123456");

        String result2 = HttpRequest.post(LONG_URL)
                .header(Header.USER_AGENT, "Hutool http")//头信息，多个头信息多次调用此方法即可
                .form(paramMap)//表单内容
                .timeout(20000)//超时，毫秒
                .execute().body();

        CookieManager cookieManager = HttpRequest.getCookieManager();
        CookieStore cookieStore = cookieManager.getCookieStore();

        List<HttpCookie> cookieList = cookieStore.getCookies();
        log.info("--------xxxx----------");
        for(HttpCookie cookie:cookieList){
            log.info("name:{}:{}",cookie.getName(),cookie.getValue());
        }
        System.out.println(result2);
    }
}
