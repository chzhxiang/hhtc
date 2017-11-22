package com.jadyer.seed.mpp.sdk.weixin.helper;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.jadyer.seed.comm.util.BeanUtil;
import com.jadyer.seed.comm.util.CodecUtil;
import com.jadyer.seed.comm.util.HttpUtil;
import com.jadyer.seed.comm.util.LogUtil;
import com.jadyer.seed.comm.util.XmlUtil;
import com.jadyer.seed.mpp.sdk.weixin.constant.WeixinCodeEnum;
import com.jadyer.seed.mpp.sdk.weixin.constant.WeixinConstants;
import com.jadyer.seed.mpp.sdk.weixin.constant.WeixinPayCodeEnum;
import com.jadyer.seed.mpp.sdk.weixin.model.WeixinErrorInfo;
import com.jadyer.seed.mpp.sdk.weixin.model.WeixinFansInfo;
import com.jadyer.seed.mpp.sdk.weixin.model.WeixinOAuthAccessToken;
import com.jadyer.seed.mpp.sdk.weixin.model.custom.WeixinCustomMsg;
import com.jadyer.seed.mpp.sdk.weixin.model.menu.WeixinMenu;
import com.jadyer.seed.mpp.sdk.weixin.model.pay.WeixinPayCloseorderReqData;
import com.jadyer.seed.mpp.sdk.weixin.model.pay.WeixinPayCloseorderRespData;
import com.jadyer.seed.mpp.sdk.weixin.model.pay.WeixinPayDownloadbillReqData;
import com.jadyer.seed.mpp.sdk.weixin.model.pay.WeixinPayDownloadbillRespData;
import com.jadyer.seed.mpp.sdk.weixin.model.pay.WeixinPayOrderqueryReqData;
import com.jadyer.seed.mpp.sdk.weixin.model.pay.WeixinPayOrderqueryRespData;
import com.jadyer.seed.mpp.sdk.weixin.model.pay.WeixinPayRefundReqData;
import com.jadyer.seed.mpp.sdk.weixin.model.pay.WeixinPayRefundRespData;
import com.jadyer.seed.mpp.sdk.weixin.model.pay.WeixinPayRefundqueryReqData;
import com.jadyer.seed.mpp.sdk.weixin.model.pay.WeixinPayRefundqueryRespData;
import com.jadyer.seed.mpp.sdk.weixin.model.pay.WeixinPayUnifiedorderReqData;
import com.jadyer.seed.mpp.sdk.weixin.model.pay.WeixinPayUnifiedorderRespData;
import com.jadyer.seed.mpp.sdk.weixin.model.redpack.WeixinRedpackGethbinfoReqData;
import com.jadyer.seed.mpp.sdk.weixin.model.redpack.WeixinRedpackGethbinfoRespData;
import com.jadyer.seed.mpp.sdk.weixin.model.redpack.WeixinRedpackSendReqData;
import com.jadyer.seed.mpp.sdk.weixin.model.redpack.WeixinRedpackSendRespData;
import com.jadyer.seed.mpp.sdk.weixin.model.template.WeixinTemplate;
import com.jadyer.seed.mpp.sdk.weixin.model.template.WeixinTemplateMsg;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class WeixinHelper {
    private WeixinHelper(){}

    static String getWeixinAccessToken(String appid, String appsecret) throws IllegalAccessException {
        String reqURL = WeixinConstants.URL_WEIXIN_GET_ACCESSTOKEN.replace(WeixinConstants.URL_PLACEHOLDER_APPID, appid).replace(WeixinConstants.URL_PLACEHOLDER_APPSECRET, appsecret);
        String respData = HttpUtil.post(reqURL, null, null);
        LogUtil.getLogger().info("获取微信access_token,微信应答报文为-->{}", respData);
        Map<String, String> map = JSON.parseObject(respData, new TypeReference<Map<String, String>>(){});
        if(respData.contains("access_token")){
            return map.get("access_token");
        }
        String errmsg = WeixinCodeEnum.getMessageByCode(Integer.parseInt((map.get("errcode"))));
        if(StringUtils.isBlank(errmsg)){
            errmsg = map.get("errmsg");
        }
        throw new IllegalAccessException(errmsg);
    }

    static String getWeixinJSApiTicket(String accesstoken) throws IllegalAccessException {
        String reqURL = WeixinConstants.URL_WEIXIN_GET_JSAPI_TICKET.replace(WeixinConstants.URL_PLACEHOLDER_ACCESSTOKEN, accesstoken);
        String respData = HttpUtil.post(reqURL, null, null);
        LogUtil.getLogger().info("获取微信jsapi_ticket,微信应答报文为-->{}", respData);
        Map<String, String> map = JSON.parseObject(respData, new TypeReference<Map<String, String>>(){});
        if("0".equals(map.get("errcode"))){
            return map.get("ticket");
        }
        String errmsg = WeixinCodeEnum.getMessageByCode(Integer.parseInt((map.get("errcode"))));
        if(StringUtils.isBlank(errmsg)){
            errmsg = map.get("errmsg");
        }
        throw new IllegalAccessException(errmsg);
    }

    static WeixinOAuthAccessToken getWeixinOAuthAccessToken(String appid, String appsecret, String code){
        String reqURL = WeixinConstants.URL_WEIXIN_OAUTH2_GET_ACCESSTOKEN.replace(WeixinConstants.URL_PLACEHOLDER_APPID, appid)
                                                                      .replace(WeixinConstants.URL_PLACEHOLDER_APPSECRET, appsecret)
                                                                      .replace(WeixinConstants.URL_PLACEHOLDER_CODE, code);
        String respData = HttpUtil.post(reqURL, null, null);
        LogUtil.getLogger().info("获取微信网页access_token，微信应答报文为-->{}", respData);
        WeixinOAuthAccessToken weixinOauthAccessToken = JSON.parseObject(respData, WeixinOAuthAccessToken.class);
        if(weixinOauthAccessToken.getErrcode() != 0){
            String errmsg = WeixinCodeEnum.getMessageByCode(weixinOauthAccessToken.getErrcode());
            if(StringUtils.isNotBlank(errmsg)){
                weixinOauthAccessToken.setErrmsg(errmsg);
            }
        }
        return weixinOauthAccessToken;
    }

    public static String buildWeixinOAuthCodeURL(String appid, String scope, String state, String redirectURI){
        try {
            return WeixinConstants.URL_WEIXIN_OAUTH2_GET_CODE.replace(WeixinConstants.URL_PLACEHOLDER_APPID, appid)
                                                          .replace(WeixinConstants.URL_PLACEHOLDER_SCOPE, scope)
                                                          .replace(WeixinConstants.URL_PLACEHOLDER_STATE, state)
                                                          .replace(WeixinConstants.URL_PLACEHOLDER_REDIRECT_URI, URLEncoder.encode(redirectURI, HttpUtil.DEFAULT_CHARSET));
        } catch (UnsupportedEncodingException e) {
            return null;
        }
    }

    public static WeixinErrorInfo createWeixinMenu(String accesstoken, WeixinMenu menu){
        String reqURL = WeixinConstants.URL_WEIXIN_GET_CREATE_MENU.replace(WeixinConstants.URL_PLACEHOLDER_ACCESSTOKEN, accesstoken);
        String reqData = JSON.toJSONString(menu);
        LogUtil.getLogger().info("自定义菜单创建-->待发送的JSON为{}", reqData);
        String respData = HttpUtil.post(reqURL, reqData, null);
        LogUtil.getLogger().info("自定义菜单创建-->微信应答JSON为{}", respData);
        WeixinErrorInfo errinfo = JSON.parseObject(respData, WeixinErrorInfo.class);
        if(errinfo.getErrcode() != 0){
            String errmsg = WeixinCodeEnum.getMessageByCode(errinfo.getErrcode());
            if(StringUtils.isNotBlank(errmsg)){
                errinfo.setErrmsg(errmsg);
            }
        }
        return errinfo;
    }

    public static WeixinErrorInfo createWeixinMenu(String accesstoken, String menuJson){
        String reqURL = WeixinConstants.URL_WEIXIN_GET_CREATE_MENU.replace(WeixinConstants.URL_PLACEHOLDER_ACCESSTOKEN, accesstoken);
        LogUtil.getLogger().info("自定义菜单创建-->待发送的JSON为{}", menuJson);
        String respData = HttpUtil.post(reqURL, menuJson, null);
        LogUtil.getLogger().info("自定义菜单创建-->微信应答JSON为{}", respData);
        WeixinErrorInfo errinfo = JSON.parseObject(respData, WeixinErrorInfo.class);
        if(errinfo.getErrcode() != 0){
            String errmsg = WeixinCodeEnum.getMessageByCode(errinfo.getErrcode());
            if(StringUtils.isNotBlank(errmsg)){
                errinfo.setErrmsg(errmsg);
            }
        }
        return errinfo;
    }

    public static WeixinFansInfo getWeixinFansInfo(String accesstoken, String openid){
        String reqURL = WeixinConstants.URL_WEIXIN_GET_FANSINFO.replace(WeixinConstants.URL_PLACEHOLDER_ACCESSTOKEN, accesstoken).replace(WeixinConstants.URL_PLACEHOLDER_OPENID, openid);
        String respData = HttpUtil.post(reqURL, null, null);
        return JSON.parseObject(respData, WeixinFansInfo.class);
    }

    public static WeixinErrorInfo pushWeixinMsgToFans(String accesstoken, WeixinCustomMsg customMsg){
        String reqURL = WeixinConstants.URL_WEIXIN_CUSTOM_PUSH_MESSAGE.replace(WeixinConstants.URL_PLACEHOLDER_ACCESSTOKEN, accesstoken);
        String reqData = JSON.toJSONString(customMsg);
        LogUtil.getLogger().info("客服接口主动推消息-->待发送的JSON为{}", reqData);
        String respData = HttpUtil.post(reqURL, reqData, null);
        LogUtil.getLogger().info("客服接口主动推消息-->微信应答JSON为{}", respData);
        WeixinErrorInfo errinfo = JSON.parseObject(respData, WeixinErrorInfo.class);
        if(errinfo.getErrcode() != 0){
            String errmsg = WeixinCodeEnum.getMessageByCode(errinfo.getErrcode());
            if(StringUtils.isNotBlank(errmsg)){
                errinfo.setErrmsg(errmsg);
            }
        }
        return errinfo;
    }

    public static WeixinErrorInfo pushWeixinTemplateMsgToFans(String accesstoken, WeixinTemplateMsg templateMsg){
        String reqURL = WeixinConstants.URL_WEIXIN_TEMPLATE_PUSH_MESSAGE.replace(WeixinConstants.URL_PLACEHOLDER_ACCESSTOKEN, accesstoken);
        String reqData = JSON.toJSONString(templateMsg);
        LogUtil.getLogger().info("单发主动推模板消息-->发送的JSON为{}", reqData);
        String respData = HttpUtil.post(reqURL, reqData, "application/json; charset="+HttpUtil.DEFAULT_CHARSET);
        LogUtil.getLogger().info("单发主动推模板消息-->微信应答JSON为{}", respData);
        WeixinErrorInfo errinfo = JSON.parseObject(respData, WeixinErrorInfo.class);
        if(errinfo.getErrcode() != 0){
            String errmsg = WeixinCodeEnum.getMessageByCode(errinfo.getErrcode());
            if(StringUtils.isNotBlank(errmsg)){
                errinfo.setErrmsg(errmsg);
            }
        }
        return errinfo;
    }

    public static List<WeixinTemplate> getWeixinTemplateList(String accesstoken){
        String reqURL = WeixinConstants.URL_WEIXIN_TEMPLATE_GETALL.replace(WeixinConstants.URL_PLACEHOLDER_ACCESSTOKEN, accesstoken);
        String respData = HttpUtil.post(reqURL, null, null);
        LogUtil.getLogger().info("获取微信模板消息列表，微信应答报文为-->{}", respData);
        Map<String, String> map = JSON.parseObject(respData, new TypeReference<Map<String, String>>(){});
        String templateListStr = map.get("template_list");
        if(StringUtils.isBlank(templateListStr)){
            return new ArrayList<>();
        }
        return JSON.parseArray(templateListStr, WeixinTemplate.class);
    }

    public static String downloadWeixinTempMediaFile(String accesstoken, String mediaId){
        String reqURL = WeixinConstants.URL_WEIXIN_GET_TEMP_MEDIA_FILE.replace(WeixinConstants.URL_PLACEHOLDER_ACCESSTOKEN, accesstoken).replace(WeixinConstants.URL_PLACEHOLDER_MEDIAID, mediaId);
        Map<String, String> resultMap = HttpUtil.postWithDownload(reqURL, null);
        if("no".equals(resultMap.get("isSuccess"))){
            Map<String, String> errmap = JSON.parseObject(resultMap.get("failReason"), new TypeReference<Map<String, String>>(){});
            String errmsg = WeixinCodeEnum.getMessageByCode(Integer.parseInt((errmap.get("errcode"))));
            if(StringUtils.isBlank(errmsg)){
                errmsg = errmap.get("errmsg");
            }
            throw new RuntimeException("下载微信临时素材" + mediaId + "失败-->" + errmsg);
        }
        return resultMap.get("fullPath");
    }

    public static String createQrcodeTicket(String accesstoken, int type, int expireSeconds, long sceneId, String sceneStr){
        String reqURL = WeixinConstants.URL_WEIXIN_GET_QRCODE_TICKET.replace(WeixinConstants.URL_PLACEHOLDER_ACCESSTOKEN, accesstoken);
        String reqData;
        if(type == 0){
            reqData = "{\"expire_seconds\":" + expireSeconds + ",\"action_name\":\"QR_SCENE\",\"action_info\":{\"scene\":{\"scene_id\":" + sceneId + "}}}";
        }else if(type == 1){
            reqData = "{\"action_name\":\"QR_LIMIT_SCENE\",\"action_info\":{\"scene\":{\"scene_id\":" + sceneId + "}}}";
        }else if(type == 2){
            reqData = "{\"action_name\":\"QR_LIMIT_STR_SCENE\",\"action_info\":{\"scene\":{\"scene_str\":\"" + sceneStr + "\"}}}";
        }else{
            throw new IllegalArgumentException("无法识别的二维码类型-->[" + type + "]");
        }
        LogUtil.getLogger().info("创建二维码ticket-->待发送的JSON为{}", reqData);
        String respData = HttpUtil.post(reqURL, reqData, null);
        LogUtil.getLogger().info("创建二维码ticket-->微信应答JSON为{}", respData);
        if(respData.contains("ticket")){
            return JSON.parseObject(respData, new TypeReference<Map<String, String>>(){}).get("ticket");
        }else{
            WeixinErrorInfo errinfo = JSON.parseObject(respData, WeixinErrorInfo.class);
            if(errinfo.getErrcode() != 0){
                String errmsg = WeixinCodeEnum.getMessageByCode(errinfo.getErrcode());
                if(StringUtils.isBlank(errmsg)){
                    errmsg = errinfo.getErrmsg();
                }
                throw new RuntimeException(errmsg);
            }
            throw new RuntimeException("获取微信二维码时遇到未知异常");
        }
    }

    public static void payVerifyIfSuccess(Map<String, String> dataMap){
        if(!StringUtils.equals("SUCCESS", dataMap.get("return_code"))){
            throw new RuntimeException(dataMap.get("return_msg"));
        }
        if(!StringUtils.equals("SUCCESS", dataMap.get("result_code"))){
            String err_code = dataMap.get("err_code");
            String err_code_des = dataMap.get("err_code_des");
            throw new RuntimeException(StringUtils.isNotBlank(err_code_des) ? err_code_des : WeixinPayCodeEnum.getMessageByCode(err_code));
        }
    }

    public static void payVerifySign(Map<String, String> dataMap, String appid){
        if(dataMap.containsKey("sign")){
            String sign_calc;
            if(StringUtils.isBlank(dataMap.get("sign_type"))){
                sign_calc = CodecUtil.buildHexSign(dataMap, "UTF-8", "MD5", WeixinTokenHolder.getWeixinMchkey(appid));
                if(!StringUtils.equals(dataMap.get("sign"), sign_calc.toUpperCase())){
                    sign_calc = CodecUtil.buildHmacSign(dataMap, WeixinTokenHolder.getWeixinMchkey(appid), "HmacSHA256");
                    if(!StringUtils.equals(dataMap.get("sign"), sign_calc.toUpperCase())){
                        throw new IllegalArgumentException("验签未通过");
                    }
                }
            }else{
                String sign_type = dataMap.get("sign_type");
                if(StringUtils.equals("MD5", sign_type)){
                    sign_calc = CodecUtil.buildHexSign(dataMap, "UTF-8", sign_type, WeixinTokenHolder.getWeixinMchkey(appid));
                }else if(StringUtils.equals("HMAC-SHA256", sign_type)){
                    sign_calc = CodecUtil.buildHmacSign(dataMap, WeixinTokenHolder.getWeixinMchkey(appid), "HmacSHA256");
                }else{
                    throw new IllegalArgumentException("不支持的签名算法");
                }
                if(!StringUtils.equals(dataMap.get("sign"), sign_calc.toUpperCase())){
                    throw new IllegalArgumentException("验签未通过");
                }
            }
        }
    }

    public static WeixinPayUnifiedorderRespData payUnifiedorder(WeixinPayUnifiedorderReqData reqData){
        LogUtil.getLogger().info("微信支付--公众号支付--统一下单接口入参为{}", ReflectionToStringBuilder.toString(reqData, ToStringStyle.MULTI_LINE_STYLE));
        Map<String, String> reqDataMap = BeanUtil.beanToMap(reqData);
        if(null != reqData.getScene_info()){
            reqDataMap.put("scene_info", JSON.toJSONString(new HashMap<String, WeixinPayUnifiedorderReqData.SceneInfo>(){
                private static final long serialVersionUID = -4476694297049282617L;
                {
                    put("store_info", reqData.getScene_info());
                }
            }));
        }
        reqDataMap.put("sign", CodecUtil.buildHexSign(reqDataMap, "UTF-8", reqData.getSign_type(), WeixinTokenHolder.getWeixinMchkey(reqData.getAppid())).toUpperCase());
        String respXml = HttpUtil.post(WeixinConstants.URL_WEIXIN_PAY_UNIFIEDORDER, XmlUtil.mapToXml(reqDataMap), null);
        Map<String, String> respXmlMap = XmlUtil.xmlToMap(respXml);
        payVerifyIfSuccess(respXmlMap);
        payVerifySign(respXmlMap, reqData.getAppid());
        WeixinPayUnifiedorderRespData respData = new WeixinPayUnifiedorderRespData();
        respData.setAppId(reqData.getAppid());
        respData.setTimeStamp(Long.toString(System.currentTimeMillis()/1000));
        respData.setNonceStr(RandomStringUtils.randomNumeric(16));
        respData.setPackage_("prepay_id=" + respXmlMap.get("prepay_id"));
        respData.setSignType("MD5");
        Map<String, String> respDataMap = BeanUtil.beanToMap(respData);
        respDataMap.remove("package_");
        respDataMap.put("package", respData.getPackage_());
        respData.setPaySign(CodecUtil.buildHexSign(respDataMap, "UTF-8", respData.getSignType(), WeixinTokenHolder.getWeixinMchkey(reqData.getAppid())).toUpperCase());
        LogUtil.getLogger().info("微信支付--公众号支付--统一下单接口出参为{}", ReflectionToStringBuilder.toString(respData, ToStringStyle.MULTI_LINE_STYLE));
        return respData;
    }

    public static WeixinPayOrderqueryRespData payOrderquery(WeixinPayOrderqueryReqData reqData){
        LogUtil.getLogger().info("微信支付--公众号支付--查询订单接口入参为{}", ReflectionToStringBuilder.toString(reqData, ToStringStyle.MULTI_LINE_STYLE));
        Map<String, String> reqDataMap = BeanUtil.beanToMap(reqData);
        reqDataMap.put("sign", CodecUtil.buildHexSign(reqDataMap, "UTF-8", reqData.getSign_type(), WeixinTokenHolder.getWeixinMchkey(reqData.getAppid())).toUpperCase());
        String respXml = HttpUtil.post(WeixinConstants.URL_WEIXIN_PAY_ORDERQUERY, XmlUtil.mapToXml(reqDataMap), null);
        Map<String, String> respXmlMap = XmlUtil.xmlToMap(respXml);
        payVerifyIfSuccess(respXmlMap);
        payVerifySign(respXmlMap, reqData.getAppid());
        WeixinPayOrderqueryRespData respData = BeanUtil.mapTobean(respXmlMap, WeixinPayOrderqueryRespData.class);
        if(StringUtils.isNotBlank(respData.getCoupon_count())){
            List<WeixinPayOrderqueryRespData.CouponInfo> couponInfoList = respData.getCouponInfoList();
            for(int i=0; i<Integer.parseInt(respData.getCoupon_count()); i++){
                WeixinPayOrderqueryRespData.CouponInfo couponInfo = new WeixinPayOrderqueryRespData.CouponInfo();
                couponInfo.setCoupon_type(respXmlMap.get("coupon_type_" + i));
                couponInfo.setCoupon_id(respXmlMap.get("coupon_id_" + i));
                couponInfo.setCoupon_fee(respXmlMap.get("coupon_fee_" + i));
                couponInfoList.add(couponInfo);
            }
        }
        LogUtil.getLogger().info("微信支付--公众号支付--查询订单接口出参为{}", ReflectionToStringBuilder.toString(respData, ToStringStyle.MULTI_LINE_STYLE));
        return respData;
    }

    public static WeixinPayCloseorderRespData payCloseorder(WeixinPayCloseorderReqData reqData){
        LogUtil.getLogger().info("微信支付--公众号支付--关闭订单接口入参为{}", ReflectionToStringBuilder.toString(reqData, ToStringStyle.MULTI_LINE_STYLE));
        Map<String, String> reqDataMap = BeanUtil.beanToMap(reqData);
        reqDataMap.put("sign", CodecUtil.buildHexSign(reqDataMap, "UTF-8", reqData.getSign_type(), WeixinTokenHolder.getWeixinMchkey(reqData.getAppid())).toUpperCase());
        String respXml = HttpUtil.post(WeixinConstants.URL_WEIXIN_PAY_CLOSEORDER, XmlUtil.mapToXml(reqDataMap), null);
        Map<String, String> respXmlMap = XmlUtil.xmlToMap(respXml);
        payVerifyIfSuccess(respXmlMap);
        payVerifySign(respXmlMap, reqData.getAppid());
        WeixinPayCloseorderRespData respData = BeanUtil.mapTobean(respXmlMap, WeixinPayCloseorderRespData.class);
        LogUtil.getLogger().info("微信支付--公众号支付--关闭订单接口出参为{}", ReflectionToStringBuilder.toString(respData, ToStringStyle.MULTI_LINE_STYLE));
        return respData;
    }

    public static WeixinPayRefundRespData payRefund(WeixinPayRefundReqData reqData, String filepath){
        LogUtil.getLogger().info("微信支付--公众号支付--申请退款接口入参为{}", ReflectionToStringBuilder.toString(reqData, ToStringStyle.MULTI_LINE_STYLE));
        Map<String, String> reqDataMap = BeanUtil.beanToMap(reqData);
        reqDataMap.put("sign", CodecUtil.buildHexSign(reqDataMap, "UTF-8", reqData.getSign_type(), WeixinTokenHolder.getWeixinMchkey(reqData.getAppid())).toUpperCase());
        String respXml = HttpUtil.postWithP12(WeixinConstants.URL_WEIXIN_PAY_REFUND, XmlUtil.mapToXml(reqDataMap), null, filepath, WeixinTokenHolder.getWeixinMchid(reqData.getAppid()));
        Map<String, String> respXmlMap = XmlUtil.xmlToMap(respXml);
        payVerifyIfSuccess(respXmlMap);
        payVerifySign(respXmlMap, reqData.getAppid());
        WeixinPayRefundRespData respData = BeanUtil.mapTobean(respXmlMap, WeixinPayRefundRespData.class);
        if(StringUtils.isNotBlank(respData.getCoupon_refund_count())){
            List<WeixinPayRefundRespData.CouponInfo> couponInfoList = respData.getCouponInfoList();
            for(int i=0; i<Integer.parseInt(respData.getCoupon_refund_count()); i++){
                WeixinPayRefundRespData.CouponInfo couponInfo = new WeixinPayRefundRespData.CouponInfo();
                couponInfo.setCoupon_type(respXmlMap.get("coupon_type_" + i));
                couponInfo.setCoupon_refund_id(respXmlMap.get("coupon_refund_id_" + i));
                couponInfo.setCoupon_refund_fee(respXmlMap.get("coupon_refund_fee_" + i));
                couponInfoList.add(couponInfo);
            }
        }
        LogUtil.getLogger().info("微信支付--公众号支付--申请退款接口出参为{}", ReflectionToStringBuilder.toString(respData, ToStringStyle.MULTI_LINE_STYLE));
        return respData;
    }

    public static WeixinPayRefundqueryRespData payRefundquery(WeixinPayRefundqueryReqData reqData){
        LogUtil.getLogger().info("微信支付--公众号支付--查询退款接口入参为{}", ReflectionToStringBuilder.toString(reqData, ToStringStyle.MULTI_LINE_STYLE));
        Map<String, String> reqDataMap = BeanUtil.beanToMap(reqData);
        reqDataMap.put("sign", CodecUtil.buildHexSign(reqDataMap, "UTF-8", reqData.getSign_type(), WeixinTokenHolder.getWeixinMchkey(reqData.getAppid())).toUpperCase());
        String respXml = HttpUtil.post(WeixinConstants.URL_WEIXIN_PAY_REFUNDQUERY, XmlUtil.mapToXml(reqDataMap), null);
        Map<String, String> respXmlMap = XmlUtil.xmlToMap(respXml);
        payVerifyIfSuccess(respXmlMap);
        payVerifySign(respXmlMap, reqData.getAppid());
        WeixinPayRefundqueryRespData respData = BeanUtil.mapTobean(respXmlMap, WeixinPayRefundqueryRespData.class);
        if(StringUtils.isNotBlank(respData.getRefund_count())){
            List<WeixinPayRefundqueryRespData.RefundInfo> refundInfoList = respData.getRefundInfoList();
            for(int i=0; i<Integer.parseInt(respData.getRefund_count()); i++){
                WeixinPayRefundqueryRespData.RefundInfo refundInfo = new WeixinPayRefundqueryRespData.RefundInfo();
                refundInfo.setOut_refund_no(respXmlMap.get("out_refund_no_" + i));
                refundInfo.setRefund_id(respXmlMap.get("refund_id_" + i));
                refundInfo.setRefund_channel(respXmlMap.get("refund_channel_" + i));
                refundInfo.setRefund_fee(respXmlMap.get("refund_fee_" + i));
                refundInfo.setSettlement_refund_fee(respXmlMap.get("settlement_refund_fee_" + i));
                refundInfo.setCoupon_type(respXmlMap.get("coupon_type_" + i));
                refundInfo.setCoupon_refund_fee(respXmlMap.get("coupon_refund_fee_" + i));
                refundInfo.setRefund_status(respXmlMap.get("refund_status_" + i));
                refundInfo.setRefund_account(respXmlMap.get("refund_account_" + i));
                refundInfo.setRefund_recv_accout(respXmlMap.get("refund_recv_accout_" + i));
                refundInfo.setRefund_success_time(respXmlMap.get("refund_success_time_" + i));
                String coupon_refund_count = respXmlMap.get("coupon_refund_count_" + i);
                if(StringUtils.isNotBlank(coupon_refund_count)){
                    refundInfo.setCoupon_refund_count(coupon_refund_count);
                    List<WeixinPayRefundqueryRespData.RefundInfo.CouponRefundInfo> couponRefundInfoList = refundInfo.getCouponRefundInfoList();
                    for(int j=0; j<Integer.parseInt(coupon_refund_count); j++){
                        WeixinPayRefundqueryRespData.RefundInfo.CouponRefundInfo couponRefundInfo = new WeixinPayRefundqueryRespData.RefundInfo.CouponRefundInfo();
                        couponRefundInfo.setCoupon_refund_id(respXmlMap.get("coupon_refund_id_" + i + "_" + j));
                        couponRefundInfo.setCoupon_refund_fee(respXmlMap.get("coupon_refund_fee_" + i + "_" + j));
                        couponRefundInfoList.add(couponRefundInfo);
                    }
                }
                refundInfoList.add(refundInfo);
            }
        }
        LogUtil.getLogger().info("微信支付--公众号支付--查询退款接口出参为{}", ReflectionToStringBuilder.toString(respData, ToStringStyle.MULTI_LINE_STYLE));
        return respData;
    }

    public static WeixinPayDownloadbillRespData payDownloadbill(WeixinPayDownloadbillReqData reqData){
        LogUtil.getLogger().info("微信支付--公众号支付--下载对账单接口入参为{}", ReflectionToStringBuilder.toString(reqData, ToStringStyle.MULTI_LINE_STYLE));
        Map<String, String> reqDataMap = BeanUtil.beanToMap(reqData);
        reqDataMap.put("sign", CodecUtil.buildHexSign(reqDataMap, "UTF-8", reqData.getSign_type(), WeixinTokenHolder.getWeixinMchkey(reqData.getAppid())).toUpperCase());
        String respXml = HttpUtil.post(WeixinConstants.URL_WEIXIN_PAY_DOWNLOADBILL, XmlUtil.mapToXml(reqDataMap), null);
        throw new RuntimeException("微信支付--公众号支付--下载对账单--接口实现待补充......");
    }

    public static WeixinRedpackSendRespData redpackSend(WeixinRedpackSendReqData reqData, String filepath){
        LogUtil.getLogger().info("微信红包--发放普通红包接口入参为{}", ReflectionToStringBuilder.toString(reqData, ToStringStyle.MULTI_LINE_STYLE));
        Map<String, String> reqDataMap = BeanUtil.beanToMap(reqData);
        reqDataMap.put("sign", CodecUtil.buildHexSign(reqDataMap, "UTF-8", "MD5", WeixinTokenHolder.getWeixinMchkey(reqData.getWxappid())).toUpperCase());
        String respXml = HttpUtil.postWithP12(WeixinConstants.URL_WEIXIN_REDPACK_SEND, XmlUtil.mapToXml(reqDataMap), null, filepath, WeixinTokenHolder.getWeixinMchid(reqData.getWxappid()));
        Map<String, String> respXmlMap = XmlUtil.xmlToMap(respXml);
        payVerifyIfSuccess(respXmlMap);
        payVerifySign(respXmlMap, reqData.getWxappid());
        WeixinRedpackSendRespData respData = BeanUtil.mapTobean(respXmlMap, WeixinRedpackSendRespData.class);
        LogUtil.getLogger().info("微信红包--发放普通红包接口出参为{}", ReflectionToStringBuilder.toString(respData, ToStringStyle.MULTI_LINE_STYLE));
        return respData;
    }

    public static WeixinRedpackGethbinfoRespData redpackGethbinfo(WeixinRedpackGethbinfoReqData reqData, String filepath){
        LogUtil.getLogger().info("微信红包--查询红包记录接口入参为{}", ReflectionToStringBuilder.toString(reqData, ToStringStyle.MULTI_LINE_STYLE));
        Map<String, String> reqDataMap = BeanUtil.beanToMap(reqData);
        reqDataMap.put("sign", CodecUtil.buildHexSign(reqDataMap, "UTF-8", "MD5", WeixinTokenHolder.getWeixinMchkey(reqData.getAppid())).toUpperCase());
        String respXml = HttpUtil.postWithP12(WeixinConstants.URL_WEIXIN_REDPACK_GETHBINFO, XmlUtil.mapToXml(reqDataMap), null, filepath, WeixinTokenHolder.getWeixinMchid(reqData.getAppid()));
        Map<String, String> respXmlMap = XmlUtil.xmlToMap(respXml);
        payVerifyIfSuccess(respXmlMap);
        payVerifySign(respXmlMap, reqData.getAppid());
        WeixinRedpackGethbinfoRespData respData = BeanUtil.mapTobean(respXmlMap, WeixinRedpackGethbinfoRespData.class);
        try{
            List<WeixinRedpackGethbinfoRespData.Hbinfo> hbinfoList = new ArrayList<>();
            NodeList nodeList = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(IOUtils.toInputStream(respXml, StandardCharsets.UTF_8)).getElementsByTagName("hbinfo");
            for(int i=0; i<nodeList.getLength(); i++){
                WeixinRedpackGethbinfoRespData.Hbinfo hbinfo = new WeixinRedpackGethbinfoRespData.Hbinfo();
                NodeList childNodes = nodeList.item(i).getChildNodes();
                for(int k=0; k<childNodes.getLength(); k++){
                    if(childNodes.item(k).getNodeType() == Node.ELEMENT_NODE){
                        if(StringUtils.equals("openid", childNodes.item(k).getNodeName())){
                            hbinfo.setOpenid(childNodes.item(k).getTextContent());
                        }
                        if(StringUtils.equals("amount", childNodes.item(k).getNodeName())){
                            hbinfo.setAmount(Integer.parseInt(childNodes.item(k).getTextContent()));
                        }
                        if(StringUtils.equals("rcv_time", childNodes.item(k).getNodeName())){
                            hbinfo.setRcv_time(childNodes.item(k).getTextContent());
                        }
                    }
                }
                hbinfoList.add(hbinfo);
            }
            respData.setHbinfolist(hbinfoList);
        }catch(Exception e){
            LogUtil.getLogger().info("微信红包--查询红包记录接口解析裂变红包的领取列表时，发生异常，堆栈轨迹如下", e);
        }
        LogUtil.getLogger().info("微信红包--查询红包记录接口出参为{}", ReflectionToStringBuilder.toString(respData, ToStringStyle.MULTI_LINE_STYLE));
        return respData;
    }
}