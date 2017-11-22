package com.jadyer.seed.mpp.web;

import com.google.common.collect.Maps;
import com.jadyer.seed.comm.constant.CommonResult;
import com.jadyer.seed.mpp.sdk.weixin.helper.WeixinHelper;
import com.jadyer.seed.mpp.sdk.weixin.helper.WeixinTokenHolder;
import com.jadyer.seed.mpp.web.service.GoodsNeedService;
import com.jadyer.seed.mpp.web.service.SmsService;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

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
import java.net.URL;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

@Controller
public class CommonController {
    @Value("${hhtc.contextpath}")
    private String hhtcContextPath;
    @Value("${hhtc.publishTime.day}")
    private int timeDay;
    @Value("${hhtc.publishTime.night}")
    private int timeNight;
    @Resource
    private HHTCHelper hhtcHelper;
    @Resource
    private SmsService smsService;
    @Resource
    private GoodsNeedService needService;

    @GetMapping("/view")
    String view(String url, HttpServletRequest request){
        Map<String, String[]> paramMap = request.getParameterMap();
        for(Map.Entry<String,String[]> entry : paramMap.entrySet()){
            if(!"url".equals(entry.getKey())){
                request.setAttribute(entry.getKey(), entry.getValue()[0]);
            }
        }
        return url;
    }


    @RequestMapping("/MP_verify_rZmB6x3GB9xYo6R6.txt")
    void rZmB6x3GB9xYo6R6(HttpServletResponse response) throws IOException {
        response.setContentType("text/plain; charset=UTF-8");
        PrintWriter out = response.getWriter();
        out.write("rZmB6x3GB9xYo6R6");
        out.flush();
        out.close();
    }


    @RequestMapping("/common/uploadImg")
    void uploadImg(MultipartFile imgData, HttpServletResponse response) throws IOException {
        response.setContentType("text/plain; charset=UTF-8");
        PrintWriter out = response.getWriter();
        if(null==imgData || 0==imgData.getSize()){
            out.print("1`请选择文件后上传");
            out.flush();
            out.close();
            return;
        }
        String filePath = hhtcHelper.buildUploadFilePath(imgData.getOriginalFilename());
        //这样写也可以实现文件的保存：imgData.transferTo(new File(""));
        FileUtils.copyInputStreamToFile(imgData.getInputStream(), new File(filePath));
        out.print("0`" + FilenameUtils.getName(filePath));
        out.flush();
        out.close();
    }


    @ResponseBody
    @GetMapping(value="/wx/common/sms/send")
    public CommonResult smsSend(String phoneNo, int type){
        smsService.smsSend(phoneNo, type);
        return new CommonResult();
    }


    @GetMapping(value="/wx/common/file/get")
    void fileGet(String filePath, HttpServletRequest request, HttpServletResponse response){
        InputStream is;
        String filename;
        try {
            if (filePath.endsWith("qrcode.jpg")) {
                filename = "qrcode.jpg";
                is = new URL(hhtcContextPath + "/img/qrcode.jpg").openStream();
            } else {
                filename = FilenameUtils.getName(filePath);
                is = FileUtils.openInputStream(new File(hhtcHelper.getFilePath() + filePath));
            }
            response.setContentType("application/octet-stream");
            response.setHeader("Content-disposition", "attachment; filename=" + new String(filename.getBytes("UTF-8"), "ISO8859-1"));
            OutputStream os = new BufferedOutputStream(response.getOutputStream());
            byte[] buff = new byte[1024];
            int len;
            while ((len = is.read(buff)) > -1) {
                os.write(buff, 0, len);
            }
            os.flush();
            is.close();
            os.close();
        }catch(Exception e){
            //ignore
        }
    }


    @ResponseBody
    @RequestMapping(value="/wx/jssdk/sign")
    public CommonResult jssdkSign(String url, HttpSession session) throws UnsupportedEncodingException {
        String appid = hhtcHelper.getWxAppidFromSession(session);
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
        return new CommonResult(resultMap);
    }


    @ResponseBody
    @RequestMapping(value="/wx/media/upload")
    public CommonResult mediaUpload(String serverId, HttpSession session) throws IOException {
        String appid = hhtcHelper.getWxAppidFromSession(session);
        String fullPath = WeixinHelper.downloadWeixinTempMediaFile(WeixinTokenHolder.getWeixinAccessToken(appid), serverId);
        String filePath = hhtcHelper.buildUploadFilePath(fullPath);
        FileUtils.copyInputStreamToFile(FileUtils.openInputStream(new File(fullPath)), new File(filePath));
        new File(fullPath).delete();
        return new CommonResult(new HashMap<String, String>(){
            private static final long serialVersionUID = -6756337144914658636L;
            {
                put("filepath", FilenameUtils.getName(filePath));
            }
        });
    }


    @ResponseBody
    @RequestMapping(value="/wx/common/daynight")
    public CommonResult daynight(){
        return new CommonResult(new HashMap<String, String>(){
            private static final long serialVersionUID = -6756337144914658636L;
            {
                put("timeDay", timeDay+"");
                put("timeNight", timeNight+"");
            }
        });
    }
}