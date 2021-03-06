package com.xyxb.backend.common.tool.apiApp;

import cn.hutool.core.lang.Console;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import java.util.*;
import java.util.regex.Pattern;

public class ApiJsonTool {
    /**
     * 组装成完整的API更新方法请求体
     * @param api
     * @return
     */
    public static JSONObject getApiUpdateJson(Api api) {
        JSONObject pjson = new JSONObject();

        JSONObject queryPath = new JSONObject();
        queryPath.put("path", api.getPath());//路径
        queryPath.put("params", new String[]{});
        pjson.put("query_path", queryPath);//query查询参数

        pjson.put("res_body_type", "json");
        pjson.put("type", "static");

        pjson.put("title", api.getTitle());
        pjson.put("catid", api.getCatid());
        pjson.put("path", api.getPath());
        pjson.put("uid", api.getUid());

        pjson.put("res_body", getResBody(api.getResBody()).toString());
        pjson.put("method", api.getMethod());
        pjson.put("_id", api.getId());
        pjson.put("project_id", api.getProjectId());
        pjson.put("tag",Arrays.asList(api.getTag()));

        pjson.put("add_time", api.getAddTime());

        JSONArray reqHeaders = getReqQuery(api.getReqHeaders());
        pjson.put("req_headers", reqHeaders);

        String content = JSON.toJSONString(JSONObject.parseObject(api.getResBody()), SerializerFeature.PrettyFormat, SerializerFeature.WriteMapNullValue,
                SerializerFeature.WriteDateUseDateFormat);

        pjson.put("markdown", "```"+content+"```");
        content = content.replaceAll("\\\n", "<br />");
        content = content.replaceAll("\\\t","&emsp;&emsp;");

        pjson.put("desc", content);

        pjson.put("edit_uid", 0);
        pjson.put("req_body_is_json_schema", true);
        pjson.put("res_body_is_json_schema", true);

        JSONArray reqRuery = getReqQuery(api.getReqQuery());
        pjson.put("req_query", reqRuery);

        JSONArray reqBodyForm = getReqQuery(api.getReqBodyForm());
        pjson.put("req_body_form", reqBodyForm);

        pjson.put("req_body_other", getResBody(api.getReqBodyOther()).toString());

        pjson.put("index", 0);
        pjson.put("req_body_type", "form");

        pjson.put("req_params", new String[]{});
        pjson.put("up_time", System.currentTimeMillis());

        pjson.put("api_opened", true);
        pjson.put("status", "undone");

        return pjson;
    }

    /**
     * 组装 yapi 相应返回值
     * @param json
     */
    public static JSONArray getReqQuery(String json) {
        JSONObject tagObject = JSONObject.parseObject(json.trim());
        if(tagObject==null){
            return new JSONArray();
        }

        Iterator it = tagObject.keySet().iterator();
        JSONArray rs = new JSONArray();
        while (it.hasNext()) {
            String cKey = it.next().toString();
            JSONObject query = new JSONObject();
            query.put("required", "1");

            String name = cKey;
            String description = cKey;
            if (cKey.indexOf("(") > -1 && cKey.indexOf(")") > -1) {
                description = cKey.substring(description.indexOf("(")+1, cKey.indexOf(")"));
            }
            if (cKey.indexOf("(") > -1 && cKey.indexOf(")") > -1) {
                name=cKey.substring(0,cKey.indexOf("("));
            }
            query.put("name", name);
            query.put("example", tagObject.get(cKey));
            query.put("desc", description);
            query.put("value", tagObject.get(cKey));
            query.put("type", "text");
            rs.add(query);
        }

        Console.log(rs.toJSONString());
        return rs;
    }

    /**
     * 组装请求参数体
     * @param json
     * @return
     */
    public static JSONObject getResBody(String json) {
        JSONObject tagObject = JSONObject.parseObject(json);
        Map<Integer, String> addrMap = new HashMap<>();
        if(tagObject==null){
            return new JSONObject();
        }
        addrMap.put(tagObject.hashCode(), "");

        LinkedHashMap rsMap = new LinkedHashMap();
        analysisJson(rsMap, tagObject.hashCode(), 0, addrMap, tagObject);

        JSONObject rspObj = new JSONObject();
        rspObj.put("type", "object");
        rspObj.put("title", "empty object");
        rspObj.put("properties", new JSONObject());
        toYapiReqBody(rsMap, rspObj);
        System.out.println(rspObj.toJSONString());
        return new JSONObject(rspObj);
    }

    /**
     * 组装请求参数体
     * @param json
     * @return
     */
    public static Map<String,Object> getResJSON(String json) {
        JSONObject tagObject = JSONObject.parseObject(json);
        Map<Integer, String> addrMap = new HashMap<>();
        if(tagObject==null){
            return new HashMap<>();
        }
        addrMap.put(tagObject.hashCode(), "");

        LinkedHashMap rsMap = new LinkedHashMap();
        analysisJson(rsMap, tagObject.hashCode(), 0, addrMap, tagObject);

        return rsMap;
    }

    private static int countString(String str,String s) {
        int count = 0;
        for(int i= 0; i<=str.length(); i++){
            if(str.indexOf(s) == i){
                str = str.substring(i+1,str.length());
                count++;
            }
        }
        return count;
    }

    /**
     * 把JSON 解析为键值对 形式  a.c.d=1
     * @param rsMap         返回结果
     * @param fastHashCode 传入的json hashCode
     * @param currHashCode 当前json hashCode 默认0
     * @param addrMap       存放 hashCode 和 key 关系，因为 解析json 关系的时候
     * @param objJson       目标 json
     */
    public static void analysisJson(LinkedHashMap<String, Object> rsMap, int fastHashCode, int currHashCode, Map<Integer, String> addrMap, Object objJson) {

        if (objJson instanceof JSONObject) {
            int hashCode = objJson.hashCode();

            JSONObject jsonObject = (JSONObject) objJson;
            Iterator it = jsonObject.keySet().iterator();

            while (it.hasNext()) {
                String cKey = it.next().toString();

                String pKey;
                if (hashCode == fastHashCode) {
                    pKey = cKey;
                } else if (currHashCode != 0) {
                    pKey = addrMap.get(currHashCode) + "." + cKey;
                } else {
                    pKey = addrMap.get(hashCode) + "." + cKey;
                }
                Object object = jsonObject.get(cKey);

                if (object instanceof JSONArray) {
                    addrMap.put(object.hashCode(), pKey);

                    JSONArray array = new JSONArray();
                    if(((JSONArray) object).size()>0 ){
                        if (isInteger(((JSONArray) object).get(0).toString())) { //如果index位置的字符是数字  返回true
                            String str = ((JSONArray) object).toJSONString();
                            int a = countString(str,"\"");
                            int b = countString(str,"'");
                            if(a>0 || b>0){
                                array.add("a");
                            }else{
                                array.add(1);
                            }
                        } else if (((JSONArray) object).get(0) instanceof String) { //如果index位置的字符是数字  返回true
                            array.add("a");
                        }
                    }

                    rsMap.put(pKey,array);

                    List<Object> list = JSONArray.parseArray(object.toString(), Object.class);
                    list.forEach(item -> {
                        analysisJson(rsMap, fastHashCode, object.hashCode(), addrMap, item);
                    });
                } else if (object instanceof JSONObject) {
                    addrMap.put(object.hashCode(), pKey);
                    analysisJson(rsMap, fastHashCode, 0, addrMap, object);
                    rsMap.put(pKey, new JSONObject());
                } else {
                    rsMap.put(pKey, object);
                }
            }
        }
    }

    /**
     * 把键值转换形式 转换成 YAPI 所需要的 格式
     * @param rsMap
     * @param rspObj
     */
    public static void toYapiReqBody(Map<String, Object> rsMap, JSONObject rspObj) {
        Iterator it = rsMap.keySet().iterator();
        while (it.hasNext()) {
            String cKey = it.next().toString();
            String[] cKeys = cKey.split("\\.");
            String lastKey = "";
            String lastKeyDesc = "";

            JSONObject jsonObject = rspObj;

            String currLastKey = "";
            String currLastKeyDesc = "";
            JSONObject parObj = rspObj;
            for (int i = 0; i < cKeys.length; i++) {
                String keyDesc = cKeys[i];
                currLastKeyDesc+=keyDesc;

                String key = getKeyName(cKeys[i]);
                currLastKey += key;
                String s = rsMap.get(currLastKeyDesc) + "";
                currLastKey += ".";
                currLastKeyDesc += ".";

                if (!jsonObject.containsKey("properties")) {
                    jsonObject.put("properties", new JSONObject());
                }
                jsonObject = jsonObject.getJSONObject("properties");
                if ("[]".equals(s)) {
                    if (!jsonObject.containsKey(key)) {
                        jsonObject.put(key, new JSONObject());
                        jsonObject = jsonObject.getJSONObject(key);
                        lastKey = key;
                        lastKeyDesc = keyDesc;
                    } else {
                        jsonObject = jsonObject.getJSONObject(key);
                    }

                    jsonObject.put("type", "array");
                    if (!jsonObject.containsKey("items")) {
                        jsonObject.put("items", new JSONObject());
                    }
                    jsonObject = jsonObject.getJSONObject("items");
                    jsonObject.put("type", "object");

                }else if("[1]".equals(s)){
                    if (!jsonObject.containsKey(key)) {
                        jsonObject.put(key, new JSONObject());
                        jsonObject = jsonObject.getJSONObject(key);
                        lastKey = key;
                        lastKeyDesc = keyDesc;
                    } else {
                        jsonObject = jsonObject.getJSONObject(key);
                    }
                    if (!jsonObject.containsKey("items")) {
                        JSONObject numberObj = new JSONObject();
                        numberObj.put("type","number");
                        jsonObject.put("items",numberObj );
                    }
                }else if("[\"a\"]".equals(s)){
                    if (!jsonObject.containsKey(key)) {
                        jsonObject.put(key, new JSONObject());
                        jsonObject = jsonObject.getJSONObject(key);
                        lastKey = key;
                        lastKeyDesc = keyDesc;
                    } else {
                        jsonObject = jsonObject.getJSONObject(key);
                    }
                    if (!jsonObject.containsKey("items")) {
                        JSONObject numberObj = new JSONObject();
                        numberObj.put("type","string");
                        jsonObject.put("items",numberObj );
                    }
                } else {
                    if (!jsonObject.containsKey(key)) {
                        jsonObject.put(key, new JSONObject());
                        jsonObject = jsonObject.getJSONObject(key);
                        lastKey = key;
                        lastKeyDesc = keyDesc;
                    } else {
                        jsonObject = jsonObject.getJSONObject(key);
                        lastKey = key;
                        lastKeyDesc = keyDesc;

                        if(jsonObject.containsKey("type") && "array".equals(jsonObject.getString("type"))){
                            JSONObject tempJsonObject = jsonObject.getJSONObject("items");
                            if(tempJsonObject==null){
                                jsonObject.put("items",new JSONObject());
                            }
                            jsonObject = jsonObject.getJSONObject("items");
                        }
                    }

                }
                if (i == cKeys.length - 2) {
                    parObj = jsonObject;
                }
            }
            if (!parObj.containsKey("required")) {
                parObj.put("required", new JSONArray());
            }

            JSONArray required = parObj.getJSONArray("required");
            required.add(lastKey);

            Object value = rsMap.get(cKey);
            String description = lastKeyDesc;
            if (lastKeyDesc.indexOf("(") > -1 && lastKeyDesc.indexOf(")") > -1) {
                description = lastKeyDesc.substring(lastKeyDesc.indexOf("(")+1, lastKeyDesc.indexOf(")"));
            }
            if(description.equals("a")){
                int a=0;
            }
            jsonObject.put("description", description);

            if (value instanceof JSONObject || value == null) {
                jsonObject.put("type", "object");
            } else if ("true".equals(value.toString().trim()) || "false".equals(value.toString().trim())) {
                jsonObject.put("type", "boolean");
            } else if ( value instanceof JSONArray ) {
                jsonObject.put("type", "array");
            } else if (isInteger(value.toString()) || "[1]".equals(value.toString())) { //如果index位置的字符是数字  返回true
                jsonObject.put("type", "number");
                JSONObject mock = new JSONObject();
                mock.put("mock", "@id");
                jsonObject.put("mock", mock);
            } else {//如果index位置的字符是字母 返回true
                JSONObject mock = new JSONObject();
                mock.put("mock", "@string");
                jsonObject.put("mock", mock);
                jsonObject.put("type", "string");
            }
        }
    }

    //判断是否是数字
    public static boolean isInteger(String str) {
        Pattern pattern = Pattern.compile("^[-\\+]?[\\d]*$");
        return pattern.matcher(str).matches();
    }

    //把key和备注分解出来
    public static String getKeyName(String key){
        String keyName=key;
        if(key.indexOf("(")>-1){
            keyName = key.substring(0,key.indexOf("("));
        }
        return keyName;
    }

}
