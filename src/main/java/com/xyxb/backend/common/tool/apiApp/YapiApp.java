package com.xyxb.backend.common.tool.apiApp;

import cn.hutool.http.HttpRequest;
import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

public class YapiApp {

    private static int PROJECT_ID;//项目ID,这里不需要填写
    private static final String HOST ="http://localhost:3000";//填写你得服务地址
    private static final String TOKEN ="8e086d7cb05f4738293174e3cdc5bae1c7108cd50da25d5b0464b2f9008fa250";//填写你的项目token

    private static final String SAVE_OR_UPDATE_API =HOST+"/api/interface/save?token="+TOKEN;//新增或修改API
    private static final String PROJECT_INFO =HOST+"/api/project/get?token="+TOKEN;//获取项目信息
    private static final String LIST_MENU =HOST+"/api/interface/list_menu?token="+TOKEN;//获取菜单列表
    private static final String ADD_CAT =HOST+"/api/interface/add_cat?token="+TOKEN;//获取菜单列表
    private static        String LIST_CAT =HOST+"/api/interface/list_cat?token="+TOKEN;//某个分类下的接口
    private static final String CAT_MENU =HOST+"/api/interface/getCatMenu?token="+TOKEN+"&project_id="+PROJECT_ID;//目录分类信息
    private static final String GET_API =HOST+"/api/interface/get?token="+TOKEN+"&id=";//目录分类信息

    private static final Log log = LogFactory.get();

    public YapiApp(){
        try {
            PROJECT_ID =  getProject();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //项目信息
    public int getProject() throws Exception{
        String result = HttpRequest.get(PROJECT_INFO)
                .timeout(20000)//超时，毫秒
                .execute().body();

        JSONObject o = JSONObject.parseObject(result);
        if(o.getInteger("errcode")==0){
            return o.getJSONObject("data").getInteger("_id");
        }else{
            String msg = "获取项目信息失败,请检查token信息！";
            log.error(msg);
            throw new Exception(msg);
        }
    }

    //项目信息
    public JSONObject getApi(String catId,String path) throws Exception{

        List<JSONObject> catApis = getListCat(catId);
        Optional<JSONObject> apiItem = catApis.stream().filter( item -> item.get("path").equals(path)).findFirst();

        if(!apiItem.isPresent()){
            return null;
        }

        String result = HttpRequest.get(GET_API+apiItem.get().getInteger("_id"))
                .timeout(20000)//超时，毫秒
                .execute().body();

        JSONObject o = JSONObject.parseObject(result);
        if(o.getInteger("errcode")==0){
            return o.getJSONObject("data");
        }else{
            String msg = "获取API失败,请检查API id ";
            log.error(msg);
            throw new Exception(msg);
        }
    }

    //获取目录和所有API列表
    public JSONObject getListMenu() throws Exception{
        String result = HttpRequest.get(LIST_MENU)
                .timeout(20000)//超时，毫秒
                .execute().body();
        JSONObject o = JSONObject.parseObject(result);
        if(o.getInteger("errcode")==0){
            return o;
        }else{
            String msg = "获取菜单列表失败！";
            log.error(msg);
            throw new Exception(msg);
        }
    }

    //获取目录
    public JSONObject getCatMenu() throws Exception{
        String result = HttpRequest.get(CAT_MENU)
                .timeout(20000)//超时，毫秒
                .execute().body();

        JSONObject o = JSONObject.parseObject(result);
        if(o.getInteger("errcode")==0){
            return o;
        }else{
            String msg = "获取API分类列表失败！";
            log.error(msg);
            throw new Exception(msg);
        }
    }

    //检查是否存在目录,如果不存在会执行新增操作
    public JSONObject checkCat(String name,String desc) throws Exception {
        JSONObject o = getCatMenu();
        List<JSONObject> array = JSONArray.parseArray(o.getJSONArray("data").toString(),JSONObject.class);

        Optional<JSONObject> json = array.stream().filter(item -> item.getString("name").equals(name)).findFirst();
        if(json.isPresent()){
            return json.get();
        }else{
            return addCat(name,desc);
        }
    }

    //检查参数同步,参数以线上的为准
    public void checkParm(JSONObject currApi) throws Exception {

        JSONObject oldApi = getApi(currApi.getString("catid"),currApi.getString("path"));
        if(oldApi==null){
            return;
        }

        List<JSONObject> currReqList = JSONArray.parseArray(currApi.getJSONArray("req_query").toString(),JSONObject.class);
        List<JSONObject> oldReqList = JSONArray.parseArray(oldApi.getJSONArray("req_query").toString(),JSONObject.class);

        List<JSONObject> rsReqList = new ArrayList<>();
        currReqList.forEach(curr->{
            Optional<JSONObject> old = oldReqList.stream().filter( oldReq -> oldReq.get("name").equals(curr.getString("name"))).findFirst();
            if(old.isPresent()){
                rsReqList.add(old.get());
            }else{
                rsReqList.add(curr);
            }
        });

        List<JSONObject> currHeadersList = JSONArray.parseArray(currApi.getJSONArray("req_headers").toString(),JSONObject.class);
        List<JSONObject> oldHeadersList = JSONArray.parseArray(oldApi.getJSONArray("req_headers").toString(),JSONObject.class);

        List<JSONObject> rsHeadersList = new ArrayList<>();
        currHeadersList.forEach(curr->{
            Optional<JSONObject> old = oldHeadersList.stream().filter( oldReq -> oldReq.get("name").equals(curr.getString("name"))).findFirst();
            if(old.isPresent()){
                rsHeadersList.add(old.get());
            }else{
                rsHeadersList.add(curr);
            }
        });

        currApi.put("req_query",rsReqList);
        currApi.put("req_headers",rsHeadersList);
    }

    //检查参数同步,参数以线上的为准
    public void checkTag(JSONObject currApi) throws Exception {

        JSONObject oldApi = getApi(currApi.getString("catid"),currApi.getString("path"));
        if(oldApi==null){
            return;
        }

        List<String> currTagList = JSONArray.parseArray(currApi.getJSONArray("tag").toString(),String.class);
        List<String> oldTagList = JSONArray.parseArray(oldApi.getJSONArray("tag").toString(),String.class);
        String currTag="";
        if(currTagList.size()>0){
            currTag = currTagList.get(0);
            if(oldTagList.size()>0 && ! oldTagList.get(oldTagList.size()-1).equals(currTag)){
                if(oldTagList.size()>=10){
                    oldTagList.remove(0);
                }
                oldTagList.add(currTag);
            }
        }
        currApi.put("tag",oldTagList);
    }

    //获取当前分类的目录信息
    public List<JSONObject> getListCat(String catid) throws Exception{
        HashMap<String, Object> paramMap = new HashMap<>();

        paramMap.put("catid",catid);
        paramMap.put("page",1);
        paramMap.put("limit",10000);
        paramMap.put("project_id", PROJECT_ID);

        String result = HttpRequest.get(LIST_CAT)
                .form(paramMap)
                .timeout(20000)//超时，毫秒
                .execute().body();
        JSONObject o = JSONObject.parseObject(result);

        if(o.getInteger("errcode")==0){
            return JSONArray.parseArray(o.getJSONObject("data").getJSONArray("list").toString(),JSONObject.class);
        }else{
            log.error("获取当前分类的所有接口失败:"+o.getString("errmsg"));
            throw new Exception();
        }
    }

    //新增分类目录
    public JSONObject addCat(String name,String desc) throws Exception{
        try{
            HashMap<String, Object> paramMap = new HashMap<>();
            paramMap.put("project_id", PROJECT_ID);
            paramMap.put("desc",desc);
            paramMap.put("name",name);

            String result = HttpRequest.post(ADD_CAT)
                    .form(paramMap)
                    .timeout(20000)//超时，毫秒
                    .execute().body();

            JSONObject o = JSONObject.parseObject(result);
            if(o.getInteger("errcode")==0){
                return o.getJSONObject("data");
            }else{
                String msg = "获取API分类列表失败！";
                log.error(msg);
                throw new Exception(msg);
            }
        }catch (Exception e){
            log.error(e.getMessage(),e);
            throw new Exception("新增目录分类失败！");
        }
    }

    /**
     *  更新API
     * @param xmlfileDir 这里是文件目录
     */
    public void saveOrUpdate(String xmlfileDir){
        try {
            ArrayList<File> list = getFiles(xmlfileDir);
            list.forEach(xmlfile->{
                String fileName = xmlfile.getName();
                if(fileName.indexOf(".xml")>-1){
                    saveOrUpdate(xmlfile);
                }
            });
        } catch (Exception e) {
            log.error(e.getMessage(),e);
        }
    }

    //更新单个文件API
    public void saveOrUpdate(File xmlfile){
        ApiInfo apiInfo;
        try {
            apiInfo = analysis(xmlfile,PROJECT_ID);

            JSONObject cat =checkCat(apiInfo.getName(),apiInfo.getDesc());
            apiInfo.setCatid(cat.getInteger("_id"));
            apiInfo.setDesc(cat.getString("desc"));
            apiInfo.setName(cat.getString("name"));

        } catch (Exception e) {
            log.error(e.getMessage(),e);
            return;
        }
        apiInfo.getApi().forEach(api->{
            try{
                api.setCatid(apiInfo.getCatid());
                api.setDesc(apiInfo.getDesc());
                api.setProjectId(PROJECT_ID);

                JSONObject rs = ApiJsonTool.getApiUpdateJson(api);

                checkParm(rs);

                checkTag(rs);

                String result2 = HttpRequest.post(SAVE_OR_UPDATE_API)
                        .header("content-type", "application/json; charset=utf-8")
                        .body(rs.toString())
                        .timeout(20000)//超时，毫秒
                        .execute().body();

                JSONObject object = JSONObject.parseObject(result2);
                if(object.getInteger("errcode")==0){
                    System.out.println("更新Api成功！");
                }else{
                    log.error("更新Api失败！"+api.getTitle()+":"+api.getPath());
                }
            }catch (Exception e){
                log.error("更新Api异常！",e);
            }
        });
    }

    /**
     *  解析 xml 信息到 yapi 对象
     * @param xmlfile xml 路径
     * @param projectId 项目ID
     * @return
     * @throws DocumentException
     */
    public static ApiInfo analysis(File xmlfile,int projectId) throws Exception {
        SAXReader reader = new SAXReader();
        Document document = reader.read(xmlfile);

        Element root = document.getRootElement();

        ApiInfo apiInfo = new ApiInfo(root.element("desc").getText(),root.element("name").getText());

        List<Element> childElements = root.element("apis").elements();

        List<Api> apis = new ArrayList<>();

        int line=0;
        String fileName=xmlfile.getName();
        for (Element child : childElements) {
            line+=1;
            Element path = child.element("path");
            Element title = child.element("title");
            Element uid = child.element("uid");
            Element tag = child.element("tag");
            Element resBody = child.element("resBody");
            Element reqQuery = child.element("reqQuery");
            Element reqBodyForm = child.element("reqBodyForm");
            Element method = child.element("method");
            Element reqHeaders = child.element("reqHeaders");
            Element reqBodyOther = child.element("reqBodyOther");



            if(path==null){
                throw new Exception(fileName+"("+line+"):"+child.getStringValue()+"标签<path></path>不能没有");
            }
            if(title==null){
                throw new Exception("标签<title></title>不能没有");
            }
            if(uid==null){
                throw new Exception("标签<uid></uid>不能没有");
            }
            if(tag==null){
                throw new Exception("标签<tag></tag>不能没有");
            }
            if(resBody==null){
                throw new Exception("标签<resBody></resBody>不能没有");
            }
            if(reqQuery==null){
                throw new Exception("标签<reqQuery></reqQuery>不能没有");
            }
            if(reqBodyForm==null){
                throw new Exception("标签<reqBodyForm></reqBodyForm>不能没有");
            }
            if(method==null){
                throw new Exception("标签<method></method>不能没有");
            }
            if(reqHeaders==null){
                throw new Exception("标签<reqHeaders></reqHeaders>不能没有");
            }
            if(reqBodyOther==null){
                throw new Exception("标签<reqBodyOther></reqBodyOther>不能没有");
            }

            Api api = new Api();
            api.setPath(path.getText());
            api.setTitle(title.getText());
            api.setCatid(apiInfo.getCatid());
            api.setUid(Integer.parseInt(uid.getText()));
            api.setProjectId(projectId);
            api.setTag(tag.getText());
            api.setResBody(resBody.getText());
            api.setReqBodyForm(reqBodyForm.getText());
            api.setMethod(method.getText());
            api.setReqQuery(reqQuery.getText());
            api.setReqHeaders(reqHeaders.getText());
            api.setReqBodyOther(reqBodyOther.getText());

            apis.add(api);
        }
        apiInfo.setApi(apis);
        return apiInfo;
    }
    //获取当前目录下的文件
    public static ArrayList<File> getFiles(String path) throws Exception {
        ArrayList<File> fileList = new ArrayList<File>();
        File file = new File(path);
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            for (File fileIndex : files) {
                //如果这个文件是目录，则进行递归搜索
                if (fileIndex.isDirectory()) {
                    getFiles(fileIndex.getPath());
                } else {
                    //如果文件是普通文件，则将文件句柄放入集合中
                    fileList.add(fileIndex);
                }
            }
        }
        return fileList;
    }

    public static void main(String[] args) {
        String filePath = ApiInfo.class.getResource("").getPath();//获取当前类的相对路径
        filePath = filePath.replace("target/classes","src/main/java");//class 路径转换为 文件的路径

        YapiApp app = new YapiApp();
        app.saveOrUpdate(filePath);
    }

}
