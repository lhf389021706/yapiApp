# yapiApp

#### 介绍
通过 xml 更新api至yapi

#### 软件架构
软件架构说明

#### 安装教程

1. 修改 YapiApp 类中的常量 HOST 为自己的 服务器地址
2. 修改 YapiApp 类中的常量  TOKEN 为自己项目的 token
3. demo.xml 中定义自己api

#### 使用说明

1. 运行 YapiApp 中的main 方法就好了

#### 参数说明
```
<ApiInfo>
    <desc>案例</desc><!-- 文件夹介绍 -->
    <name>demo</name><!-- 文件夹名称 如果不存在会自动创建 -->
    <apis>
        <api>
            <path>/test3</path><!-- 请求路径 -->
            <title>测试233</title><!-- 标题 -->
            <uid>11</uid><!-- 更新人，这里11 -->
            <tag>5</tag><!-- 版本 这里会记录 最多10个 tag 版本迭代 -->
            <reqHeaders><!-- 请求参数头 "()" 里面是需要填写的参数描述,描述里面带"!" 即 不是必须的 -->
                {"aaa(测-aaa)":123,"bbb(测-bbb)":345}
            </reqHeaders>
            <reqQuery><!-- 请求参数 "()" 里面是需要填写的参数描述  描述里面带"!" 即 不是必须的 -->
                {"aaa(测-aaa)":123,"bbb(测-bbb)":345}
            </reqQuery>
            <resBody><!-- 标准JSON格式返回值，只需要在 key 加入 "(key的描述)"-->
                {
                    "ccc(用话ID)":{
                        "ccc-1(测ccc-1)":[
                            {
                                "a":1,
                                "b":[{"b-1":"a"}]
                            }
                        ],
                        "ccc-2":"1792318200684396.8"
                    }
                }
            </resBody>
            <method>GET</method><!-- 请求协议 "-->
        </api>
    </apis>
</ApiInfo>

```


#### TOKEN设置参考
#####Pre-request Script(请求参数处理脚本)
```
//设置 cookies
function setCookie(name,value) 
{ 
    var Days = 30; 
    var exp = new Date(); 
    exp.setTime(exp.getTime() + Days*24*60*60*1000); 
    document.cookie = name + "="+ escape (value) + ";expires=" + exp.toGMTString(); 
} 

//读取cookies 
function getCookie(name) 
{ 
    var arr,reg=new RegExp("(^| )"+name+"=([^;]*)(;|$)");
 
    if(arr=document.cookie.match(reg))
 
        return unescape(arr[2]); 
    else 
        return null; 
}

//设置时间唯一token
var userTimeToken = getCookie('userTimeToken');
if(!userTimeToken){
    var time = new Date().getTime();
    setCookie('userTimeToken',time);
    userTimeToken = time;
}


//处理  token 值
let mock='/mock/'+context.projectId;
if(context.pathname.indexOf(mock) != -1){
    //mock 接口不做任何处理
    
}else {
    var token = storage.getItem('token-----'+userTimeToken);
    context.requestHeader.Tokensd=token;
}
```
#### Pre-response Script(响应数据处理脚本)
```
//设置 cookies
function setCookie(name,value) 
{ 
    var Days = 30; 
    var exp = new Date(); 
    exp.setTime(exp.getTime() + Days*24*60*60*1000); 
    document.cookie = name + "="+ escape (value) + ";expires=" + exp.toGMTString(); 
} 

//读取cookies 
function getCookie(name) 
{ 
    var arr,reg=new RegExp("(^| )"+name+"=([^;]*)(;|$)");
 
    if(arr=document.cookie.match(reg))
 
        return unescape(arr[2]); 
    else 
        return null; 
}

//登录完成后设置值
if (context.pathname.indexOf('/sys/login') != -1) {
	// 登录接口不需要设置
	context.requestHeader.Authorization = null;
	
	//生成时间token
	var userTimeToken = getCookie('userTimeToken');
    if(!userTimeToken){
        var time = new Date().getTime();
        setCookie('userTimeToken',time);
        userTimeToken = time;
    }
	
	var code = context.responseData.code;
	console.log(context.responseData);
	if(code==200){
	    var token = context.responseData.data.token;
	    storage.setItem('token-----'+userTimeToken, token);
	}
}
```





