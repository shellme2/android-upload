
# 关于

中间件上载库，属于中间件的子项目，主要为Android移动端提供上载功能，目前的版本是根据以前的版本添加监听回调，去掉通知栏进度显示。上载默认数据库newbfcdownload.db,默认表newbfcupload。
# 特性

- 简单易用
- 通用

## 版本和项目名称

- 发布版本： 4.0.0
- GitLab项目名： BfcUpload
- 库名称：bfc-Upload
- 需要Android API >= 15

## 功能列表

- 支持添加多文件
- 支持多任务
- 支持删除任务
- 支持查询任务
- 支持重新上载
- 支持监听回调

## 升级清单文档
- 文档名称：UPDATE.md (http://172.28.2.93/bfc/BfcUpload/blob/master/UPDATE.md)

# 使用

#### 注意事项
- 使用前请详细阅读此文档
- 和以前的接口兼容，接口使用一样
- 必须配置和使用我们公司的私有maven仓库(http://172.28.1.147:8081/nexus/content/repositories/thirdparty/)

## 前置条件  
#### 1. 配置

    compile 'com.eebbk.bfc:bfc-uploadsdk:2.1.0'
> 如果依赖使用的[网络配置](http://172.28.2.93/bfc/Bfc/blob/develop/public-config/%E4%BE%9D%E8%B5%96%E4%BD%BF%E7%94%A8%E8%AF%B4%E6%98%8E.md), 请参考网络配置使用; 添加`compile bfcBuildConfig.deps.'bfc-upload`

> 如果提示有support-annotation包中间的注解冲突, 可以在依赖的时候排除掉注解包, 参考如下

  	compile ('com.eebbk.bfc:bfc-upload:2.1.0'){
        exclude group: 'com.android.support'
        exclude module: 'support-annotations'
    }

#### 2. 接口使用
*  上传任务使用

*  回调监听 
    >       UploadListener  mUploadListener = new UploadListener() {
            @Override
            public void onLoading(int id, long total, long current, long remainingMillis, long speed, int precent) {//下载中监听，操作UI，请用handler  
            }
            @Override
            public void onSuccess(int id, String taskName, long total, List<String> urlList) {//下载成功，云任务返回上载成功url集合，非云上载则没有
            }
            @Override
            public void onFailure(int id, String taskName, String msg) {//下载失败，任务名，id,失败消息
            }
        }
        
* http上传 
  >  
        ITask task = new HttpTask(url);
        UploadController mUploadController = new UploadController(context);
        task.setFileName(taskName);//极其重要，不能为空，相当于任务名
        task.addFile(pFileName1, pFilePath1);
        task.setOverWrite(true);//true覆盖，false不覆盖
        task.setNetworkTypes(NetworkType.NETWORK_WIFI|NetworkType.NETWORK_MOBILE) // 设置网络类型
        int row = mUploadController.addTask(mUploadListener, task);//添加监听


* 云上传 
 >
        ITask task = new MultiCloudTask();
        UploadController mUploadController = new UploadController(context);
        task.setFileName(taskName);//极其重要，不能为空，相当于任务名
        task.addFile(pFileName1, pFilePath1);
        task.putExtra(ITask.EXTRA_KEY_BUCKET_ID，"com.eebbk.bfc.app.bfcbehavior");//设置云上传空间的id
        task.setOverWrite(true);//true覆盖，false不覆盖
        task.setNetworkTypes(NetworkType.NETWORK_WIFI|NetworkType.NETWORK_MOBILE) // 设置网络类型
        int row = mUploadController.addTask(mUploadListener, task);//添加监听
>

*  重新上传任务
     >int row =mUploadController.reloadTask(mUploadListener, task);//添加监听
*  删除任务
    > int row = mUploadController.deleteTask(task.getId());
*  查询任务 
  *  //直接通过ID来查询
        >
             ITask task = mUploadController.getTaskById(task.getId());//建议使用此查询
 *  //其他查询
     >
           //第一步：先创建一个查询用的Query
             Query query = new query();
            //第二步：设置查询的条件
           query.setFilterById(task.getId());
           query.setFilterByStatus(UploadManager.STATUS_FAILED);
           query.addFilterByExtras(“TEST”, “123”);
          //第三步：查询
           ArrayList<ITask> taskList = mUploadController.getTask(query);
   >
   
### 提示：
 在其他进程开启下载任务，需要ITask.setSupportProcess(true),然后需要自己写数据库监听，详情参考demo

  
 
### 3.上载库版本信息查看
```
StringBuilder sb = new StringBuilder();
sb.append("\r\n 库名称: " + SDKVersion.getLibraryName());
sb.append("\r\n 版本序号: " + SDKVersion.getSDKInt());
sb.append("\r\n 版本名称: " + SDKVersion.getVersionName());
sb.append("\r\n 构建版本: " + SDKVersion.getBuildName());
sb.append("\r\n 构建时间: " + SDKVersion.getBuildTime());
sb.append("\r\n TAG标签: " + SDKVersion.getBuildTag());
sb.append("\r\n HEAD值: " + SDKVersion.getBuildHead()); 
```
### 4.上载库整体流程图

![整体流程图](http://172.28.2.93/bfc/BfcUpload/blob/master/doc/%E8%AE%BE%E8%AE%A1%E6%96%87%E6%A1%A3/%E6%A6%82%E8%A6%81%E8%AE%BE%E8%AE%A1%E9%99%84%E4%BB%B6/%E4%B8%AD%E9%97%B4%E4%BB%B6%E4%B8%8A%E8%BD%BD%E5%BA%93%E6%95%B4%E4%BD%93%E6%B5%81%E7%A8%8B%E5%9B%BE_%E9%B2%81%E5%A4%A7%E5%85%89_V2.0.0_20161121.jpg)

### 5.其他

#### 上载库状态码说明
```
        190, 即将上载
        192, 正在上载
        193, app上载暂停
        194, 等待重新上载
        195, 等待网络
        196, 队列等待
        200, 上载成功
      
```
#### 上载库错误码说明
```
        479, 不支持多任务异常
        480, 服务器返回url异常
        481, http token异常
        482, http io异常
        483, http释放异常
        484, FTP登录异常
        485, FTP上载文件异常
        486, FTP创建文件夹异常
        487, FTP退出异常
        497, htpp解析异常
        496, http异常
        491, 未知异常
      
```

## 源码保存地址
GitLab源码地址: http://172.28.2.93/bfc/BfcUpload.git

## 相关文档获取方式
可以从GitLab下载项目(http://172.28.2.93/bfc/BfcUpload.git)，项目根目录下有doc文件夹,保存有齐全的使用说明文档和项目设计文档。也可以在wiki上找到中间件栏目查看相关文档。

## 问题反馈
使用过程成遇到任何问题，有任何建议或者意见都可以使用下面这个地址反馈给我们。欢迎大家提出问题，我们将会在最短的时间内解决问题。
**地址：http://172.28.2.93/bfc/BfcUpload/issues**


##集成后常见问题
会遇到编译不过的情况，试试将compileSdkVersion改成26，对应的support包也改成26.1.0
例如：compile 'com.android.support:appcompat-v7:26.1.0'
                     
