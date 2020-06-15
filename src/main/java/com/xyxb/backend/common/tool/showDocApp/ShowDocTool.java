package com.xyxb.backend.common.tool.showDocApp;

import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import com.alibaba.fastjson.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ShowDocTool {

    private static final Log log = LogFactory.get();
    private static String HOST = "47.112.108.146";
    private static String LOGIN_URL = "http://"+HOST+"/showdoc-master/server/index.php?s=/api/user/login";
    private static String PROJECT_URL = "http://"+HOST+"/showdoc-master/server/index.php?s=/api/item/myList";
    private static String UPDATE_BY_API = "http://"+HOST+"/showdoc-master/server/index.php?s=/api/item/updateByApi";

    private static String USER_NAME = "宏锋";
    private static String PASSWORD = "123456";

    private static String API_KEY = "9186da1785ff251331d22d56574e55482102";
    private static String API_TOKEN = "748b6a1fb7cb39ceca76ab2ee5765f117693";

    //登录
    public static void login() {
        Map<String,Object> paramMap = new HashMap<>();
        paramMap.put("username",USER_NAME);
        paramMap.put("password",PASSWORD);
        HttpUtil.LOGIN(LOGIN_URL,paramMap);
        log.info("登录成功");
    }

    //目录
    public static void getCatItem(){
        if(HttpUtil.cookieManager==null){
            login();
        }
        JSONObject rs = HttpUtil.POST(PROJECT_URL,null);
        log.info("执行后:{}",rs.toJSONString());
    }

    public static String createMarkdown(Doc doc){
        StringBuffer sbf = new StringBuffer();

        sbf.append("\n**简要描述**\n");
        sbf.append("\n - "+doc.getDesc());

        sbf.append("\n\n**请求URL**");
        sbf.append("\n- ` "+ doc.getUrl() +"` ");

        sbf.append("\n\n**请求方式** ");
        sbf.append("\n\n- `"+doc.getMethod()+"` \n");

        //设置参数字段说明
        sbf.append("\n\n**参数** ");
        sbf.append("\n\n|参数名|是否必选|类型|说明|");
        sbf.append("\n|:----    |:---|:----- |-----   |");

        Map<String,Object> paramDesc = doc.getParamDesc();

        for(Map.Entry<String, Object> entry : paramDesc.entrySet()){
            String key = entry.getKey();
            String value = entry.getValue()!=null?entry.getValue().toString():"";
            String description = key;
            String required = key.indexOf("!")>-1?"否":"是";

            if (key.indexOf("(") > -1 && key.indexOf(")") > -1) {
                description = key.substring(description.indexOf("(")+1, key.indexOf(")"));
                description = description.replace("!","");
            }
            if (key.indexOf("(") > -1 && key.indexOf(")") > -1) {
                key=key.substring(0,key.indexOf("("));
            }
            boolean isNumber = NumberUtil.isNumber(value);

            //|key |必选  |string |用户名 |
            String template = "|{} |{} |{} |{} |";

            String str = StrUtil.format(template, key, required ,isNumber?"number":"string",description);
            sbf.append("\n"+str);
        }

        sbf.append("\n **返回示例** ");
        sbf.append("\n\n ```\n"+ doc.getResultDemo()+" \n```\n ");

        //设置返回值字段说明
        Map<String,Object>  resultDesc = doc.getResultDesc();
        log.info("返回参数信息:"+resultDesc.toString());

        sbf.append("\n **返回参数说明**  ");
        sbf.append("\n\n |参数名|类型|说明|");
        sbf.append("\n |:----    |:---|:----- |-----   |");

        for(Map.Entry<String, Object> entry : resultDesc.entrySet()){
            String key = entry.getKey();
            String keyDesc =getKey(entry.getKey());

            String value = entry.getValue()!=null?entry.getValue().toString():"";
            String description = key;

            String[] ds = key.split("\\.");
            if(ds.length>0){
                String lsDs =ds[ds.length-1];
                if (lsDs.indexOf("(") > -1 && lsDs.indexOf(")") > -1) {
                    description = lsDs.substring(lsDs.lastIndexOf("(")+1, lsDs.lastIndexOf(")"));
                }else{
                    description="";
                }
            }

            if (key.indexOf("(") > -1 && key.indexOf(")") > -1) {
                key=key.substring(0,key.indexOf("("));
            }
            boolean isNumber = NumberUtil.isNumber(value);

            //|key |必选  |string |用户名 |
            String template = "|{} |{} |{} |";
            String str = StrUtil.format(template, keyDesc, isNumber?"number":"string",description);
            sbf.append("\n"+str);
        }
        return sbf.toString();
    }

    public static void saveApi(Doc doc){
        login();
        Map<String,Object> paramMap = new HashMap<>();
        paramMap.put("api_key",API_KEY);
        paramMap.put("api_token",API_TOKEN);
        paramMap.put("cat_name",doc.getCatName());
        paramMap.put("page_title",doc.getTitle());
        paramMap.put("page_content",createMarkdown(doc));
        paramMap.put("s_number",99);

        JSONObject rs = HttpUtil.POST(UPDATE_BY_API,paramMap);
        //System.out.println(rs);
    }

    private static String getKey(String str){

        int start = str.indexOf("(");
        int end = str.indexOf(")");

        if(start== -1 || end == -1){
            return str;
        }else{
            str = str.replace(str.substring(start,end+1),"");
            return getKey(str);
        }
    }

    public static void main(String[] args) {
        //saveApi("测试3/测试4","测试而已","测试测试",1);
        String str = "ccc(测试).ccc-1(测ccc-1).b.b-1";
        log.info("效果："+getKey(str));
    }

}
