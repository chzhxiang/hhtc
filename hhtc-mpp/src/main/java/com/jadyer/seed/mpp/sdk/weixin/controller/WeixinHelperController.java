package com.jadyer.seed.mpp.sdk.weixin.controller;

import com.google.common.collect.Maps;
import com.jadyer.seed.comm.constant.Constants;
import com.jadyer.seed.comm.util.HttpUtil;
import com.jadyer.seed.comm.util.JadyerUtil;
import com.jadyer.seed.comm.util.LogUtil;
import com.jadyer.seed.comm.util.XmlUtil;
import com.jadyer.seed.mpp.sdk.weixin.constant.WeixinConstants;
import com.jadyer.seed.mpp.sdk.weixin.helper.WeixinHelper;
import com.jadyer.seed.mpp.sdk.weixin.helper.WeixinTokenHolder;
import com.jadyer.seed.mpp.sdk.weixin.model.WeixinOAuthAccessToken;
import com.jadyer.seed.mpp.web.model.MppFansInfor;
import com.jadyer.seed.mpp.web.model.OrderInfo;
import com.jadyer.seed.mpp.web.service.FansService;
import com.jadyer.seed.mpp.web.service.MppUserService;
import com.jadyer.seed.mpp.web.service.OrderService;
import com.jadyer.seed.mpp.web.service.UserFundsService;
import com.jadyer.seed.mpp.web.service.async.AppidAsync;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Date;
import java.util.Map;

@Controller
@RequestMapping(value="/weixin/helper")
public class WeixinHelperController {
    @Value("${hhtc.contextpath}")
    private String hhtcContextPath;
    @Value("${hhtc.portalUrl.login}")
    private String portalLoginUrl;
    @Value("${hhtc.portalUrl.center}")
    private String portalCenterUrl;
    @Resource
    private AppidAsync appidAsync;
    @Resource
    private FansService fansService;
    @Resource
    private OrderService orderService;
    @Resource
    private UserFundsService userFundsService;
    @Resource
    private MppUserService mppUserService;

    @RequestMapping(value="/oauth/{appid}")
    public String oauth(@PathVariable String appid, String code, String state, HttpServletRequest request, HttpServletResponse response) throws IOException{
        LogUtil.getLogger().info("网页授权-->收到微信服务器回调code=[{}], state=[{}]", code, state);
        String fullURI;
        if(state.contains("=")){
            String uri = state.substring(0, state.indexOf("="));
            uri = uri.substring(0, uri.lastIndexOf("/"));
            String params = state.substring(uri.length()+1);
            params = params.replaceAll("/", "&");
            fullURI = uri + "?" + params;
        }else{
            fullURI = state;
        }
        fullURI = fullURI.replace("#wechat_redirect", "").replace("#?", "#/")
                .replace("/index.html/", "/index.html#/").replace("/@/", "/#/");
        LogUtil.getLogger().info("网页授权-->还原粉丝请求的资源得到state=[{}]", fullURI);
        if(StringUtils.isNotBlank(code)){
            WeixinOAuthAccessToken oauthAccessToken = WeixinTokenHolder.getWeixinOAuthAccessToken(appid, code);
            if(0==oauthAccessToken.getErrcode() && StringUtils.isNotBlank(oauthAccessToken.getOpenid())){
                HttpSession session = request.getSession();
                session.setMaxInactiveInterval(3*24*60*60);
                session.setAttribute(Constants.WEB_SESSION_WX_APPID, appid);
                session.setAttribute(Constants.WEB_SESSION_WX_OPENID, oauthAccessToken.getOpenid());
                if(fullURI.endsWith(this.portalCenterUrl)){
                    MppFansInfor fansInfor = fansService.getByOpenid(oauthAccessToken.getOpenid());
                    if('1'!=fansInfor.getInfor_state().charAt(Constants.INFOR_STATE_CARPARK_BIT)
                            && '1'!=fansInfor.getInfor_state().charAt(Constants.INFOR_STATE_CARNUMBE_BIT) ){
                        response.sendRedirect(hhtcContextPath + portalLoginUrl);
                        return null;
                    }
                }
                response.sendRedirect(fullURI);
                return null;
            }
        }
        response.setCharacterEncoding(HttpUtil.DEFAULT_CHARSET);
        response.setContentType("text/plain; charset=" + HttpUtil.DEFAULT_CHARSET);
        response.setHeader("Cache-Control", "no-cache");
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0);
        PrintWriter out = response.getWriter();
        out.print("系统繁忙Unauthorized\r\n请联系您关注的微信公众号");
        out.flush();
        out.close();
        return null;
    }


    @ResponseBody
    @RequestMapping(value="/jssdk/sign")
    public Map<String, String> jssdkSign(String appid, String url) throws UnsupportedEncodingException {
        url = URLDecoder.decode(url, "UTF-8");
        String noncestr = RandomStringUtils.randomNumeric(16);
        String timestamp = Long.toString(System.currentTimeMillis() / 1000);
        StringBuilder sb = new StringBuilder();
        sb.append("jsapi_ticket=").append(WeixinTokenHolder.getWeixinJSApiTicket(appid)).append("&")
                .append("noncestr=").append(noncestr).append("&")
                .append("timestamp=").append(timestamp).append("&")
                .append("url=").append(url);
        Map<String, String> resultMap = Maps.newHashMap();
        resultMap.put("appid", appid);
        resultMap.put("timestamp", String.valueOf(timestamp));
        resultMap.put("noncestr", noncestr);
        resultMap.put("signature", DigestUtils.sha1Hex(sb.toString()));
        return resultMap;
    }


    @RequestMapping(value="/tempMediaFile/get/{appid}/{mediaId}")
    public void tempMediaFileGet(@PathVariable String appid, @PathVariable String mediaId, HttpServletResponse response) throws Exception {
        String fullPath = WeixinHelper.downloadWeixinTempMediaFile(WeixinTokenHolder.getWeixinAccessToken(appid), mediaId);
        WeixinTokenHolder.setMediaIdFilePath(appid, mediaId, fullPath);
        response.setContentType("application/octet-stream");
        response.setHeader("Content-disposition", "attachment; filename=" + new String(("get_"+FilenameUtils.getName(fullPath)).getBytes("UTF-8"), "ISO8859-1"));
        InputStream is = FileUtils.openInputStream(new File(fullPath));
        OutputStream os = new BufferedOutputStream(response.getOutputStream());
        byte[] buff = new byte[1024];
        int len;
        while((len=is.read(buff)) > -1){
            os.write(buff, 0, len);
        }
        os.flush();
        is.close();
        os.close();
    }


    @ResponseBody
    @RequestMapping(value="/tempMediaFile/delete/{appid}/{mediaId}")
    public boolean tempMediaFileDelete(@PathVariable String appid, @PathVariable String mediaId){
        String localFileFullPath = WeixinTokenHolder.getMediaIdFilePath(appid, mediaId);
        try {
            return new File(localFileFullPath).delete();
        } catch (Exception e) {
            LogUtil.getLogger().info("删除存储在本地的微信临时媒体文件mediaId=["+mediaId+"],fullPath=["+localFileFullPath+"]失败,堆栈轨迹如下", e);
            return false;
        }
    }


    @RequestMapping(value="/getQrcodeURL")
    public void getQrcodeURL(String appid, int type, String expireSeconds, String sceneId, String sceneStr, HttpServletResponse response) throws IOException{
        if(StringUtils.isBlank(expireSeconds)){
            expireSeconds = "2";
        }
        if(StringUtils.isBlank(sceneId)){
            sceneId = "2";
        }
        String ticket = WeixinHelper.createQrcodeTicket(WeixinTokenHolder.getWeixinAccessToken(appid), type, Integer.parseInt(expireSeconds), Long.parseLong(sceneId), sceneStr);
        String qrcodeURL = WeixinConstants.URL_WEIXIN_GET_QRCODE.replace(WeixinConstants.URL_PLACEHOLDER_QRCODE_TICKET, ticket);
        response.setCharacterEncoding(HttpUtil.DEFAULT_CHARSET);
        response.setContentType("text/plain; charset=" + HttpUtil.DEFAULT_CHARSET);
        response.setHeader("Cache-Control", "no-cache");
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0);
        PrintWriter out = response.getWriter();
        out.print(qrcodeURL);
        out.flush();
        out.close();
    }

    /**
     * TOKGO 平台收到用户充值
     * */

    @RequestMapping("/pay/notify")
    void payNotify(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String reqData = JadyerUtil.extractHttpServletRequestBodyMessage(request);
        LogUtil.getLogger().info("微信支付--公众号支付--支付结果通知-->收到报文-->[{}]", reqData);
        Map<String, String> dataMap = XmlUtil.xmlToMap(reqData);
        WeixinHelper.payVerifyIfSuccess(dataMap);
        WeixinHelper.payVerifySign(dataMap, dataMap.get("appid"));
        OrderInfo orderInfo =null;
        if(!StringUtils.equals(orderInfo.getTotalFee()+"", dataMap.get("total_fee"))){
            throw new IllegalArgumentException("微信公众号支付后台通知订单总金额与商户订单金额不符");
        }
        if(orderInfo.getIsNotify() != 1){
            String appid = dataMap.get("appid");
            String transaction_id = dataMap.get("transaction_id");
            orderInfo.setIsSubscribe(dataMap.get("is_subscribe"));
            orderInfo.setBankType(dataMap.get("bank_type"));
            orderInfo.setCashFee(Long.parseLong(dataMap.get("cash_fee")));
            orderInfo.setTransactionId(transaction_id);
            orderInfo.setTimeEnd(dataMap.get("time_end"));
            orderInfo.setNotifyTime(new Date());
            orderInfo.setIsNotify(1);



            orderInfo = orderService.upsert(orderInfo);
            orderService.query(orderInfo, 0);
        }
//        userFundsService.
        response.setCharacterEncoding(HttpUtil.DEFAULT_CHARSET);
        response.setContentType("text/plain; charset=" + HttpUtil.DEFAULT_CHARSET);
        response.setHeader("Cache-Control", "no-cache");
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0);
        PrintWriter out = response.getWriter();
        LogUtil.getLogger().info("微信支付--公众号支付--支付结果通知-->应答内容为：表示成功的XML");
        out.print("<xml><return_code><![CDATA[SUCCESS]]></return_code><return_msg><![CDATA[OK]]></return_msg></xml>");
        out.flush();
        out.close();
    }
}