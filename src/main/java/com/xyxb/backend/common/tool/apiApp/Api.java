package com.xyxb.backend.common.tool.apiApp;

public class Api {

    private String path;//资源路径
    private String title;//标题
    private int catid;//目录路径
    private int uid;//更新人
    private int projectId;//项目ID
    private String tag;//版本标志
    private String reqQuery;//Query请求参数 {"a(参数描述)":b}
    private String reqHeaders;//Headers请求参数 {"a(参数描述)":b}
    private String reqBodyForm;//body请求参数 {"a(参数描述)":b}
    private String reqBodyOther;//body请求参数 {"a(参数描述)":b}

    private String resBody;//返回参数 {"a(参数描述)":b}
    private String method;//返回参数 {"a(参数描述)":b}
    private int id;//API id 更新根据 path 进行更新，这里id 没啥用
    private long addTime;//更新时间
    private String desc; //描述 和 markdown 一起更新

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getCatid() {
        return catid;
    }

    public void setCatid(int catid) {
        this.catid = catid;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public int getProjectId() {
        return projectId;
    }

    public void setProjectId(int projectId) {
        this.projectId = projectId;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getResBody() {
        return resBody;
    }

    public void setResBody(String resBody) {
        this.resBody = resBody;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public long getAddTime() {
        return addTime;
    }

    public void setAddTime(long addTime) {
        this.addTime = addTime;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getReqQuery() {
        return reqQuery;
    }

    public void setReqQuery(String reqQuery) {
        this.reqQuery = reqQuery;
    }

    public String getReqHeaders() {
        return reqHeaders;
    }

    public void setReqHeaders(String reqHeaders) {
        this.reqHeaders = reqHeaders;
    }

    public String getReqBodyOther() {
        return reqBodyOther;
    }

    public void setReqBodyOther(String reqBodyOther) {
        this.reqBodyOther = reqBodyOther;
    }

    public String getReqBodyForm() {
        return reqBodyForm;
    }

    public void setReqBodyForm(String reqBodyForm) {
        this.reqBodyForm = reqBodyForm;
    }


}


