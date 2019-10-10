package com.xyxb.backend.common.tool.apiApp;

import java.util.List;

public class ApiInfo {
    private String projectId;
    private String desc;
    private String name;
    private int catid;

    private List<Api> api;

    public ApiInfo(){}

    public ApiInfo(String desc,String name){
        this.desc=desc;
        this.name=name;
    }

    public List<Api> getApi() {
        return api;
    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public void setApi(List<Api> api) {

        this.api = api;
    }

    public String getDesc() {
        return desc;
    }

    public int getCatid() {
        return catid;
    }

    public void setCatid(int catid) {
        this.catid = catid;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
