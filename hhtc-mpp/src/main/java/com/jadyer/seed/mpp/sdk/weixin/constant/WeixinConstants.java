package com.jadyer.seed.mpp.sdk.weixin.constant;

public interface WeixinConstants {
    String NOT_NEED_REPLY_FLAG = "success";

    String WEIXIN_OAUTH_SCOPE_SNSAPI_BASE     = "snsapi_base";
    String WEIXIN_OAUTH_SCOPE_SNSAPI_USERINFO = "snsapi_userinfo";

    String URL_PLACEHOLDER_APPID         = "{appid}";
    String URL_PLACEHOLDER_APPSECRET     = "{appsecret}";
    String URL_PLACEHOLDER_ACCESSTOKEN   = "{accesstoken}";
    String URL_PLACEHOLDER_OPENID        = "{openid}";
    String URL_PLACEHOLDER_REDIRECT_URI  = "{redirecturi}";
    String URL_PLACEHOLDER_SCOPE         = "{scope}";
    String URL_PLACEHOLDER_STATE         = "{state}";
    String URL_PLACEHOLDER_CODE          = "{code}";
    String URL_PLACEHOLDER_MEDIAID       = "{mediaid}";
    String URL_PLACEHOLDER_QRCODE_TICKET = "{qrcodeticket}";

    //获取access token
    String URL_WEIXIN_GET_ACCESSTOKEN        = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=" + URL_PLACEHOLDER_APPID + "&secret=" + URL_PLACEHOLDER_APPSECRET;
    //获取用户基本信息
    String URL_WEIXIN_GET_FANSINFO           = "https://api.weixin.qq.com/cgi-bin/user/info?lang=zh_CN&openid=" + URL_PLACEHOLDER_OPENID + "&access_token=" + URL_PLACEHOLDER_ACCESSTOKEN;
    //自定义菜单之创建
    String URL_WEIXIN_GET_CREATE_MENU        = "https://api.weixin.qq.com/cgi-bin/menu/create?access_token=" + URL_PLACEHOLDER_ACCESSTOKEN;
    //客服接口主动推消息
    String URL_WEIXIN_CUSTOM_PUSH_MESSAGE    = "https://api.weixin.qq.com/cgi-bin/message/custom/send?access_token=" + URL_PLACEHOLDER_ACCESSTOKEN;
    //单发主动推模板消息
    String URL_WEIXIN_TEMPLATE_PUSH_MESSAGE  = "https://api.weixin.qq.com/cgi-bin/message/template/send?access_token=" + URL_PLACEHOLDER_ACCESSTOKEN;
    //获取模板列表
    String URL_WEIXIN_TEMPLATE_GETALL        = "https://api.weixin.qq.com/cgi-bin/template/get_all_private_template?access_token=" + URL_PLACEHOLDER_ACCESSTOKEN;
    //网页授权获取用户信息的Code地址
    String URL_WEIXIN_OAUTH2_GET_CODE        = "https://open.weixin.qq.com/connect/oauth2/authorize?appid=" + URL_PLACEHOLDER_APPID + "&redirect_uri=" + URL_PLACEHOLDER_REDIRECT_URI + "&response_type=code&scope=" + URL_PLACEHOLDER_SCOPE + "&state=" + URL_PLACEHOLDER_STATE + "#wechat_redirect";
    //通过code换取网页授权access_token
    String URL_WEIXIN_OAUTH2_GET_ACCESSTOKEN = "https://api.weixin.qq.com/sns/oauth2/access_token?appid=" + URL_PLACEHOLDER_APPID + "&secret=" + URL_PLACEHOLDER_APPSECRET +"&code=" + URL_PLACEHOLDER_CODE + "&grant_type=authorization_code";
    //获取微信jsapi_ticket
    String URL_WEIXIN_GET_JSAPI_TICKET       = "https://api.weixin.qq.com/cgi-bin/ticket/getticket?access_token=" + URL_PLACEHOLDER_ACCESSTOKEN + "&type=jsapi";
    //获取微信临时素材
    String URL_WEIXIN_GET_TEMP_MEDIA_FILE    = "https://api.weixin.qq.com/cgi-bin/media/get?access_token=" + URL_PLACEHOLDER_ACCESSTOKEN + "&media_id=" + URL_PLACEHOLDER_MEDIAID;
    //获取微信二维码ticket
    String URL_WEIXIN_GET_QRCODE_TICKET      = "https://api.weixin.qq.com/cgi-bin/qrcode/create?access_token=" + URL_PLACEHOLDER_ACCESSTOKEN;
    //获取微信二维码
    String URL_WEIXIN_GET_QRCODE             = "https://mp.weixin.qq.com/cgi-bin/showqrcode?ticket=" + URL_PLACEHOLDER_QRCODE_TICKET;
    //微信支付--公众号支付--统计下单
    String URL_WEIXIN_PAY_UNIFIEDORDER       = "https://api.mch.weixin.qq.com/pay/unifiedorder";
    //微信支付--公众号支付--查询订单
    String URL_WEIXIN_PAY_ORDERQUERY         = "https://api.mch.weixin.qq.com/pay/orderquery";
    //微信支付--公众号支付--关闭订单
    String URL_WEIXIN_PAY_CLOSEORDER         = "https://api.mch.weixin.qq.com/pay/closeorder";
    //微信支付--公众号支付--申请退款
    String URL_WEIXIN_PAY_REFUND             = "https://api.mch.weixin.qq.com/secapi/pay/refund";
    //微信支付--公众号支付--查询退款
    String URL_WEIXIN_PAY_REFUNDQUERY        = "https://api.mch.weixin.qq.com/pay/refundquery";
    //微信支付--公众号支付--下载对账单
    String URL_WEIXIN_PAY_DOWNLOADBILL       = "https://api.mch.weixin.qq.com/pay/downloadbill";
    //微信红包--发放普通红包
    String URL_WEIXIN_REDPACK_SEND           = "https://api.mch.weixin.qq.com/mmpaymkttransfers/sendredpack";
    //微信红包--查询红包记录
    String URL_WEIXIN_REDPACK_GETHBINFO      = "https://api.mch.weixin.qq.com/mmpaymkttransfers/gethbinfo";
}