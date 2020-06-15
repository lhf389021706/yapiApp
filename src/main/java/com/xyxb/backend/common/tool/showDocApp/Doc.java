package com.xyxb.backend.common.tool.showDocApp;

import com.alibaba.fastjson.JSONObject;

import java.util.Map;

public class Doc {

    private String desc;
    private String title;
    private String catName;
    private String url;
    private String method;
    private Map<String,Object> paramDesc;
    private Map<String,Object> resultDesc;
    private String resultDemo;

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getResultDemo() {
        return resultDemo;
    }

    public void setResultDemo(String resultDemo) {
        this.resultDemo = resultDemo;
    }

    public Map<String, Object> getParamDesc() {
        return paramDesc;
    }

    public void setParamDesc(Map<String, Object> paramDesc) {
        this.paramDesc = paramDesc;
    }

    public Map<String, Object> getResultDesc() {
        return resultDesc;
    }

    public void setResultDesc(Map<String, Object> resultDesc) {
        this.resultDesc = resultDesc;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCatName() {
        return catName;
    }

    public void setCatName(String catName) {
        this.catName = catName;
    }
}
