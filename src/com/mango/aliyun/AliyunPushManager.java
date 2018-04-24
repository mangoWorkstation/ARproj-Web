package com.mango.aliyun;

import com.aliyuncs.http.MethodType;
import com.aliyuncs.http.ProtocolType;
import com.aliyuncs.push.model.v20160801.PushMessageToAndroidRequest;
import com.aliyuncs.push.model.v20160801.PushMessageToAndroidResponse;
import com.aliyuncs.push.model.v20160801.PushNoticeToAndroidRequest;
import com.aliyuncs.push.model.v20160801.PushNoticeToAndroidResponse;

import java.io.InputStream;
import java.util.Properties;

import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;

public class AliyunPushManager {
	/**
     * 推送消息给android
     * <p>
     * 参见文档 https://help.aliyun.com/document_detail/48085.html
     */
	
    protected static String region;
    protected static long appKey;
    protected static String deviceIds;
    protected static String deviceId;
    protected static String accounts;
    protected static String account;
    protected static String aliases;
    protected static String alias;
    protected static String tag;
    protected static String tagExpression;

    protected static DefaultAcsClient client;

    /**
     * 从配置文件中读取配置值，初始化Client
     * <p>
     * 1. 如何获取 accessKeyId/accessKeySecret/appKey 照见README.md 中的说明<br/>
     * 2. 先在 push.properties 配置文件中 填入你的获取的值
     */
    public AliyunPushManager() throws Exception {
        InputStream inputStream = AliyunPushManager.class.getResourceAsStream("push.properties");
        Properties properties = new Properties();
        properties.load(inputStream);

        String accessKeyId = properties.getProperty("accessKeyId");

        String accessKeySecret = properties.getProperty("accessKeySecret");

        String key = properties.getProperty("appKey");

        region = properties.getProperty("regionId");
        appKey = Long.valueOf(key);
//        deviceIds = properties.getProperty("deviceIds");
//        deviceId = properties.getProperty("deviceId");
        accounts = properties.getProperty("accounts");
        account = properties.getProperty("account");
        aliases = properties.getProperty("aliases");
        alias = properties.getProperty("alias");
        tag = properties.getProperty("tag");
        tagExpression = properties.getProperty("tagExpression");

        IClientProfile profile = DefaultProfile.getProfile(region, accessKeyId, accessKeySecret);
        client = new DefaultAcsClient(profile);
    }
    
    /**
     * 推送消息给安卓客户端
     * @param title
     * @param body
     * @param deviceIDs 使用逗号隔开 "device1,device2"
     * @throws Exception
     */
    public boolean pushMessageToAndroid(String title,String body,String deviceIDs) throws Exception {

        PushMessageToAndroidRequest androidRequest = new PushMessageToAndroidRequest();
        //安全性比较高的内容建议使用HTTPS
        androidRequest.setProtocol(ProtocolType.HTTPS);
        //内容较大的请求，使用POST请求
        androidRequest.setMethod(MethodType.POST);
        androidRequest.setAppKey(appKey);
        androidRequest.setTarget("ACCOUNT");
        androidRequest.setTargetValue(deviceIDs);
        androidRequest.setTitle(title);
        androidRequest.setBody(body);
        PushMessageToAndroidResponse pushMessageToAndroidResponse = client.getAcsResponse(androidRequest);
        System.out.printf("RequestId: %s, MessageId: %s\n",
                pushMessageToAndroidResponse.getRequestId(), pushMessageToAndroidResponse.getMessageId());
        if(pushMessageToAndroidResponse.getMessageId()!=null&&pushMessageToAndroidResponse.getMessageId()!=null){
			return true;
	    }
	    return false;

    }

    
    public boolean pushNoticeToAndroid(String title,String body,String deviceIDs) throws Exception {

        PushNoticeToAndroidRequest androidRequest = new PushNoticeToAndroidRequest();
        //安全性比较高的内容建议使用HTTPS
        androidRequest.setProtocol(ProtocolType.HTTPS);
        //内容较大的请求，使用POST请求
        androidRequest.setMethod(MethodType.POST);
        androidRequest.setAppKey(appKey);
        androidRequest.setTarget("ACCOUNT");
        androidRequest.setTargetValue(deviceIDs);
        androidRequest.setTitle(title);
        androidRequest.setBody(body);
//        androidRequest.setExtParameters("{\"k1\":\"v1\"}");

        PushNoticeToAndroidResponse pushNoticeToAndroidResponse = client.getAcsResponse(androidRequest);
        System.out.printf("RequestId: %s, MessageId: %s\n",
                pushNoticeToAndroidResponse.getRequestId(), pushNoticeToAndroidResponse.getMessageId());
        
        if(pushNoticeToAndroidResponse.getMessageId()!=null&&pushNoticeToAndroidResponse.getMessageId()!=null){
    			return true;
        }
        return false;


    }
}
