package com.jadyer.seed.mpp.sdk.weixin.helper;

import com.jadyer.seed.comm.util.LogUtil;
import com.jadyer.seed.mpp.sdk.weixin.model.WeixinOAuthAccessToken;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

public class WeixinTokenHolder {
    private static final String FLAG_WEIXIN_ACCESSTOKEN = "weixin_access_token_";
    private static final String FLAG_WEIXIN_JSAPI_TICKET = "weixin_jsapi_ticket_";
    private static final String FLAG_WEIXIN_ACCESSTOKEN_EXPIRETIME = FLAG_WEIXIN_ACCESSTOKEN + "expire_time_";
    private static final String FLAG_WEIXIN_JSAPI_TICKET_EXPIRETIME = FLAG_WEIXIN_JSAPI_TICKET + "expire_time_";
    private static final long WEIXIN_TOKEN_EXPIRE_TIME_MILLIS = 7000 * 1000;
    private static AtomicBoolean weixinAccessTokenRefreshing = new AtomicBoolean(false);
    private static AtomicBoolean weixinJSApiTicketRefreshing = new AtomicBoolean(false);
    private static ConcurrentHashMap<String, Object> tokenMap = new ConcurrentHashMap<>();

    private WeixinTokenHolder(){}

    public static void setWeixinAppidMch(String appid, String mchid, String mchkey){
        tokenMap.put(appid + "_mchid", mchid);
        tokenMap.put(appid + "_mchkey", mchkey);
    }

    public static String getWeixinMchid(String appid){
        String mchid = (String)tokenMap.get(appid + "_mchid");
        if(StringUtils.isBlank(mchid)){
            throw new IllegalArgumentException("未登记微信mchid");
        }
        return mchid;
    }

    public static String getWeixinMchkey(String appid){
        String mchkey = (String)tokenMap.get(appid + "_mchkey");
        if(StringUtils.isBlank(mchkey)){
            throw new IllegalArgumentException("未登记微信mchkey");
        }
        return mchkey;
    }

    public static String setWeixinAppidAppsecret(String appid, String appsecret){
        for(Map.Entry<String,Object> entry : tokenMap.entrySet()){
            if(entry.getKey().endsWith("_"+appid)){
                tokenMap.remove(entry.getKey());
            }
        }
        tokenMap.put(appid, appsecret);
        return getWeixinAppsecret(appid);
    }

    public static String getWeixinAppsecret(String appid){
        String appsecret = (String)tokenMap.get(appid);
        if(StringUtils.isBlank(appsecret)){
            throw new IllegalArgumentException("未登记微信appsecret");
        }
        return appsecret;
    }

    public static String setMediaIdFilePath(String appid, String mediaId, String localFileFullPath){
        tokenMap.put(appid+"_"+mediaId, localFileFullPath);
        return getMediaIdFilePath(appid, mediaId);
    }

    public static String getMediaIdFilePath(String appid, String mediaId){
        String localFileFullPath = (String)tokenMap.get(appid+"_"+mediaId);
        if(StringUtils.isBlank(localFileFullPath)){
            throw new IllegalArgumentException("不存在的本地媒体文件appid="+appid+",mediaId="+mediaId);
        }
        return localFileFullPath;
    }

    public static String getWeixinAccessToken(String appid){
        Long expireTime = (Long)tokenMap.get(FLAG_WEIXIN_ACCESSTOKEN_EXPIRETIME + appid);
        if(null!=expireTime && expireTime>=System.currentTimeMillis()){
            return (String)tokenMap.get(FLAG_WEIXIN_ACCESSTOKEN + appid);
        }
        if(weixinAccessTokenRefreshing.compareAndSet(false, true)){
            String accessToken;
            try {
                accessToken = WeixinHelper.getWeixinAccessToken(appid, getWeixinAppsecret(appid));
                tokenMap.put(FLAG_WEIXIN_ACCESSTOKEN + appid, accessToken);
                tokenMap.put(FLAG_WEIXIN_ACCESSTOKEN_EXPIRETIME + appid, System.currentTimeMillis()+WEIXIN_TOKEN_EXPIRE_TIME_MILLIS);
            } catch (Exception e) {
                LogUtil.getLogger().error("获取微信appid=["+appid+"]的access_token失败", e);
            }
            weixinAccessTokenRefreshing.set(false);
        }
        return (String)tokenMap.get(FLAG_WEIXIN_ACCESSTOKEN + appid);
    }

    public static String getWeixinJSApiTicket(String appid){
        Long expireTime = (Long)tokenMap.get(FLAG_WEIXIN_JSAPI_TICKET_EXPIRETIME + appid);
        if(null!=expireTime && expireTime>=System.currentTimeMillis()){
            return (String)tokenMap.get(FLAG_WEIXIN_JSAPI_TICKET + appid);
        }
        if(weixinJSApiTicketRefreshing.compareAndSet(false, true)){
            String jsapiTicket;
            try {
                jsapiTicket = WeixinHelper.getWeixinJSApiTicket(getWeixinAccessToken(appid));
                tokenMap.put(FLAG_WEIXIN_JSAPI_TICKET + appid, jsapiTicket);
                tokenMap.put(FLAG_WEIXIN_JSAPI_TICKET_EXPIRETIME + appid, System.currentTimeMillis()+WEIXIN_TOKEN_EXPIRE_TIME_MILLIS);
            } catch (Exception e) {
                LogUtil.getLogger().error("获取微信appid=["+appid+"]的jsapi_ticket失败", e);
            }
            weixinJSApiTicketRefreshing.set(false);
        }
        return (String)tokenMap.get(FLAG_WEIXIN_JSAPI_TICKET + appid);
    }

    public static WeixinOAuthAccessToken getWeixinOAuthAccessToken(String appid, String code){
        return WeixinHelper.getWeixinOAuthAccessToken(appid, getWeixinAppsecret(appid), code);
    }
}