package com.jadyer.seed.mpp.sdk.weixin.msg;

import com.jadyer.seed.mpp.sdk.weixin.constant.WeixinConstants;
import com.jadyer.seed.mpp.sdk.weixin.msg.out.WeixinOutCustomServiceMsg;
import com.jadyer.seed.mpp.sdk.weixin.msg.out.WeixinOutImageMsg;
import com.jadyer.seed.mpp.sdk.weixin.msg.out.WeixinOutMsg;
import com.jadyer.seed.mpp.sdk.weixin.msg.out.WeixinOutNewsMsg;
import com.jadyer.seed.mpp.sdk.weixin.msg.out.WeixinOutTextMsg;

public class WeixinOutMsgXmlBuilder {
    private WeixinOutMsgXmlBuilder(){}

    public static String build(WeixinOutMsg outMsg){
        if(null == outMsg){
            throw new IllegalArgumentException("空的回复消息");
        }
        if("text".equals(outMsg.getMsgType())){
            return buildOutTextMsg((WeixinOutTextMsg)outMsg);
        }
        if("image".equals(outMsg.getMsgType())){
            return buildOutImageMsg((WeixinOutImageMsg)outMsg);
        }
        if("news".equals(outMsg.getMsgType())){
            return buildOutNewsMsg((WeixinOutNewsMsg)outMsg);
        }
        if("transfer_customer_service".equals(outMsg.getMsgType())){
            return buildOutCustomServiceMsg((WeixinOutCustomServiceMsg)outMsg);
        }
        throw new RuntimeException("未知的消息类型" + outMsg.getMsgType() + ", 请查阅微信公众平台开发者文档http://mp.weixin.qq.com/wiki/home/index.html.");
    }

    private static String buildOutTextMsg(WeixinOutTextMsg outTexgMsg){
        if(WeixinConstants.NOT_NEED_REPLY_FLAG.equals(outTexgMsg.getContent())){
            return WeixinConstants.NOT_NEED_REPLY_FLAG;
        }
        StringBuilder sb = new StringBuilder();
        sb.append("<xml>")
          .append("<ToUserName><![CDATA[").append(outTexgMsg.getToUserName()).append("]]></ToUserName>")
          .append("<FromUserName><![CDATA[").append(outTexgMsg.getFromUserName()).append("]]></FromUserName>")
          .append("<CreateTime>").append(outTexgMsg.getCreateTime()).append("</CreateTime>")
          .append("<MsgType><![CDATA[").append(outTexgMsg.getMsgType()).append("]]></MsgType>")
          .append("<Content><![CDATA[").append(outTexgMsg.getContent()).append("]]></Content>")
          .append("</xml>");
        return sb.toString();
    }

    private static String buildOutImageMsg(WeixinOutImageMsg outImageMsg){
        StringBuilder sb = new StringBuilder();
        sb.append("<xml>")
          .append("<ToUserName><![CDATA[").append(outImageMsg.getToUserName()).append("]]></ToUserName>")
          .append("<FromUserName><![CDATA[").append(outImageMsg.getFromUserName()).append("]]></FromUserName>")
          .append("<CreateTime>").append(outImageMsg.getCreateTime()).append("</CreateTime>")
          .append("<MsgType><![CDATA[").append(outImageMsg.getMsgType()).append("]]></MsgType>")
          .append("<Image>")
          .append("<MediaId><![CDATA[").append(outImageMsg.getMediaId()).append("]]></MediaId>")
          .append("</Image>")
          .append("</xml>");
        return sb.toString();
    }

    private static String buildOutNewsMsg(WeixinOutNewsMsg outNewsMsg){
        StringBuilder sb = new StringBuilder();
        sb.append("<xml>")
          .append("<ToUserName><![CDATA[").append(outNewsMsg.getToUserName()).append("]]></ToUserName>")
          .append("<FromUserName><![CDATA[").append(outNewsMsg.getFromUserName()).append("]]></FromUserName>")
          .append("<CreateTime>").append(outNewsMsg.getCreateTime()).append("</CreateTime>")
          .append("<MsgType><![CDATA[").append(outNewsMsg.getMsgType()).append("]]></MsgType>")
          .append("<ArticleCount>").append(outNewsMsg.getArticleCount()).append("</ArticleCount>")
          .append("<Articles>");
        if(!outNewsMsg.getArticles().isEmpty()){
            for(WeixinOutNewsMsg.WeixinNews news : outNewsMsg.getArticles()){
                sb.append("<item>")
                  .append("<Title><![CDATA[").append(news.getTitle()).append("]]></Title>")
                  .append("<Description><![CDATA[").append(news.getDescription()).append("]]></Description>")
                  .append("<PicUrl><![CDATA[").append(news.getPicUrl()).append("]]></PicUrl>")
                  .append("<Url><![CDATA[").append(news.getUrl()).append("]]></Url>")
                  .append("</item>");
            }
        }
        sb.append("</Articles>")
          .append("</xml>");
        return sb.toString();
    }

    private static String buildOutCustomServiceMsg(WeixinOutCustomServiceMsg outCustomServiceMsg){
        StringBuilder sb = new StringBuilder();
        sb.append("<xml>")
          .append("<ToUserName><![CDATA[").append(outCustomServiceMsg.getToUserName()).append("]]></ToUserName>")
          .append("<FromUserName><![CDATA[").append(outCustomServiceMsg.getFromUserName()).append("]]></FromUserName>")
          .append("<CreateTime>").append(outCustomServiceMsg.getCreateTime()).append("</CreateTime>")
          .append("<MsgType><![CDATA[").append(outCustomServiceMsg.getMsgType()).append("]]></MsgType>")
          .append("</xml>");
        return sb.toString();
    }
}