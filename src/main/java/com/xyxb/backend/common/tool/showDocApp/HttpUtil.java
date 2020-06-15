package com.xyxb.backend.common.tool.showDocApp;

import cn.hutool.core.text.UnicodeUtil;
import cn.hutool.http.Header;
import cn.hutool.http.HttpRequest;
import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import com.alibaba.fastjson.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.CookieManager;
import java.net.CookieStore;
import java.net.HttpCookie;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HttpUtil {

    private static final Log log = LogFactory.get();

    public static CookieManager cookieManager;

    public static JSONObject LOGIN(String url,Map<String,Object> paramMap){
        HttpRequest request = HttpRequest.get(url);
        String rs = request.header(Header.USER_AGENT, "Hutool http")//头信息，多个头信息多次调用此方法即可
                .form(paramMap).execute().body();
        cookieManager =HttpRequest.getCookieManager();
        return JSONObject.parseObject(rs);
    }

    public static JSONObject GET(String url,HashMap paramMap,HashMap cookieMap) throws Exception{
        String rs = HttpRequest.get(url)
                .header(Header.USER_AGENT, "Hutool http")//头信息，多个头信息多次调用此方法即可
                .form(paramMap)//表单内容
                .timeout(20000)//超时，毫秒
                .execute().body();
        cookieManager=HttpRequest.getCookieManager();

        return JSONObject.parseObject(UnicodeUtil.toString(rs));
    }
    public static JSONObject POST(String url,Map<String,Object> paramMap){
        HttpRequest request = HttpRequest.get(url);
        setCookie(request);
        String rs = request.form(paramMap).execute().body();
        return JSONObject.parseObject(UnicodeUtil.toString(rs));
    }

    public static HttpRequest  setCookie(HttpRequest request){
        CookieStore cookieStore = cookieManager.getCookieStore();
        List<HttpCookie> cookieList = cookieStore.getCookies();

        Map<String,String> parmMap = new HashMap<>();
        StringBuffer  sb =new StringBuffer();
        cookieList.forEach(cookie->{
            parmMap.put(cookie.getName(),cookie.getValue());
            sb.append(cookie.getName()+"="+cookie.getValue()+";");
        });
        request.header("cookie",sb.toString());
        return request;
    }

}
