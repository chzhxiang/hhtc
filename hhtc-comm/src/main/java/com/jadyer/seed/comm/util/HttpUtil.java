package com.jadyer.seed.comm.util;

import com.jadyer.seed.comm.constant.CodeEnum;
import com.jadyer.seed.comm.exception.HHTCException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.conn.ssl.X509HostnameVerifier;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.protocol.HTTP;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.util.EntityUtils;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.security.KeyStore;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 封装了发送HTTP请求的工具类
 * @version v2.9
 * @history v2.9-->各个方法解码响应报文时，增加更灵活的解码字符集判断
 * @history v2.8-->增加微信支付退款和微信红包接口所需的postWithP12()方法
 * @history v2.7-->抽象出公共的HTTPS支持的设置，加入到httpclient实现的各个方法中，并更名postTSL()为post()
 * @history v2.6-->修复部分细节，增加入参出参的日志打印
 * @history v2.5-->修复<code>postWithUpload()</code>方法的<code>Map<String, String> params</code>参数传入null时无法上传文件的BUG
 * @history v2.4-->重命名GET和POST方法名,全局定义通信报文编码和连接读取超时时间,通信发生异常时修改为直接抛出RuntimeException
 * @history v2.3-->增加<code>sendPostRequestWithUpload()</code><code>sendPostRequestWithDownload()</code>方法,用于上传和下载文件
 * @history v2.2-->增加<code>sendPostRequestBySocket()</code>方法,用于处理请求参数非字符串而是Map的情景
 * @history v2.1-->增加<code>sendTCPRequest()</code>方法,用于发送TCP请求
 * @history v2.0-->HttpClientUtil更名为HttpUtil,同时增加<code>sendPostRequestByJava()</code>和<code>sendPostRequestBySocket()</code>
 * @history v1.7-->修正<code>sendPostRequest()</code>请求的CONTENT_TYPE头信息,并优化各方法参数及内部处理细节
 * @history v1.6-->整理GET和POST请求方法,使之更为适用
 * @history v1.5-->重组各方法,并补充自动获取HTTP响应文本编码的方式,移除<code>sendPostRequestByJava()</code>
 * @history v1.4-->所有POST方法中增加连接超时限制和读取超时限制
 * @history v1.3-->新增<code>java.net.HttpURLConnection</code>实现的<code>sendPostRequestByJava()</code>
 * @history v1.2-->新增<code>sendPostRequest()</code>方法,用于发送HTTP协议报文体为任意字符串的POST请求
 * @history v1.1-->新增<code>sendPostSSLRequest()</code>方法,用于发送HTTPS的POST请求
 * @history v1.0-->新建<code>sendGetRequest()</code>和<code>sendPostRequest()</code>方法
 * Created by 玄玉<http://jadyer.cn/> on 2012/2/1 15:02.
 */
@SuppressWarnings("deprecation")
public final class HttpUtil {
    public static final String DEFAULT_CHARSET = "UTF-8";
    public static final String CHARSET_ISO_8859_1 = "ISO-8859-1";
    private static final int DEFAULT_CONNECTION_TIMEOUT = 1000 * 2;
    private static final int DEFAULT_SO_TIMEOUT = 1000 * 60;

    private HttpUtil(){}

    public static String postWithP12(String reqURL, String reqData, String contentType, String filepath, String password){
        LogUtil.getLogger().info("请求{}的报文为-->>[{}]", reqURL, reqData);
        String respData = "";
        HttpClient httpClient = new DefaultHttpClient();
        httpClient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, DEFAULT_CONNECTION_TIMEOUT);
        httpClient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, DEFAULT_SO_TIMEOUT);
        HttpPost httpPost = new HttpPost(reqURL);
        if(StringUtils.isBlank(contentType)){
            httpPost.setHeader(HTTP.CONTENT_TYPE, "application/x-www-form-urlencoded; charset=" + DEFAULT_CHARSET);
        }else{
            httpPost.setHeader(HTTP.CONTENT_TYPE, contentType);
        }
        httpPost.setEntity(new StringEntity(null==reqData?"":reqData, DEFAULT_CHARSET));
        try{
            httpClient = addTLSSupport(httpClient, filepath, password);
            HttpResponse response = httpClient.execute(httpPost);
            HttpEntity entity = response.getEntity();
            if(null != entity){
                String decodeCharset;
                ContentType respContentType = ContentType.get(entity);
                if(null == respContentType){
                    decodeCharset = DEFAULT_CHARSET;
                }else if(null == respContentType.getCharset()){
                    decodeCharset = DEFAULT_CHARSET;
                }else{
                    decodeCharset = respContentType.getCharset().displayName();
                }
                respData = EntityUtils.toString(entity, decodeCharset);
            }
            LogUtil.getLogger().info("请求{}得到应答<<--[{}]", reqURL, respData);
            return respData;
        }catch(ConnectTimeoutException cte){
            throw new HHTCException(CodeEnum.SYSTEM_BUSY.getCode(), "请求通信[" + reqURL + "]时连接超时", cte);
        }catch(SocketTimeoutException ste){
            throw new HHTCException(CodeEnum.SYSTEM_BUSY.getCode(), "请求通信[" + reqURL + "]时读取超时", ste);
        }catch(Exception e){
            throw new HHTCException(CodeEnum.SYSTEM_ERROR.getCode(), "请求通信[" + reqURL + "]时遇到异常", e);
        }finally{
            httpClient.getConnectionManager().shutdown();
        }
    }


    public static String post(String reqURL, String reqData, String contentType){
        LogUtil.getLogger().info("请求{}的报文为-->>[{}]", reqURL, reqData);
        String respData = "";
        HttpClient httpClient = new DefaultHttpClient();
        httpClient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, DEFAULT_CONNECTION_TIMEOUT);
        httpClient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, DEFAULT_SO_TIMEOUT);
        HttpPost httpPost = new HttpPost(reqURL);
        if(StringUtils.isBlank(contentType)){
            httpPost.setHeader(HTTP.CONTENT_TYPE, "application/x-www-form-urlencoded; charset=" + DEFAULT_CHARSET);
        }else{
            httpPost.setHeader(HTTP.CONTENT_TYPE, contentType);
        }
        httpPost.setEntity(new StringEntity(null==reqData?"":reqData, DEFAULT_CHARSET));
        try{
            httpClient = addTLSSupport(httpClient);
            HttpResponse response = httpClient.execute(httpPost);
            HttpEntity entity = response.getEntity();
            if(null != entity){
                String decodeCharset;
                ContentType respContentType = ContentType.get(entity);
                if(null == respContentType){
                    decodeCharset = DEFAULT_CHARSET;
                }else if(null == respContentType.getCharset()){
                    decodeCharset = DEFAULT_CHARSET;
                }else{
                    decodeCharset = respContentType.getCharset().displayName();
                }
                respData = EntityUtils.toString(entity, decodeCharset);
            }
            LogUtil.getLogger().info("请求{}得到应答<<--[{}]", reqURL, respData);
            return respData;
        }catch(ConnectTimeoutException cte){
            throw new HHTCException(CodeEnum.SYSTEM_BUSY.getCode(), "请求通信[" + reqURL + "]时连接超时", cte);
        }catch(SocketTimeoutException ste){
            throw new HHTCException(CodeEnum.SYSTEM_BUSY.getCode(), "请求通信[" + reqURL + "]时读取超时", ste);
        }catch(Exception e){
            throw new HHTCException(CodeEnum.SYSTEM_ERROR.getCode(), "请求通信[" + reqURL + "]时遇到异常", e);
        }finally{
            httpClient.getConnectionManager().shutdown();
        }
    }


    public static String post(String reqURL, Map<String, String> params){
        LogUtil.getLogger().info("请求{}的报文为-->>{}", reqURL, JadyerUtil.buildStringFromMap(params));
        String respData = "";
        HttpClient httpClient = new DefaultHttpClient();
        httpClient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, DEFAULT_CONNECTION_TIMEOUT);
        httpClient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, DEFAULT_SO_TIMEOUT);
        try {
            HttpPost httpPost = new HttpPost(reqURL);
            if(null != params){
                List<NameValuePair> formParams = new ArrayList<>();
                for(Map.Entry<String,String> entry : params.entrySet()){
                    formParams.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
                }
                httpPost.setEntity(new UrlEncodedFormEntity(formParams, DEFAULT_CHARSET));
            }
            httpClient = addTLSSupport(httpClient);
            HttpResponse response = httpClient.execute(httpPost);
            HttpEntity entity = response.getEntity();
            if(null != entity){
                String decodeCharset;
                ContentType respContentType = ContentType.get(entity);
                if(null == respContentType){
                    decodeCharset = DEFAULT_CHARSET;
                }else if(null == respContentType.getCharset()){
                    decodeCharset = DEFAULT_CHARSET;
                }else{
                    decodeCharset = respContentType.getCharset().displayName();
                }
                respData = EntityUtils.toString(entity, decodeCharset);
            }
            LogUtil.getLogger().info("请求{}得到应答<<--[{}]", reqURL, respData);
            return respData;
        }catch(ConnectTimeoutException cte){
            throw new HHTCException(CodeEnum.SYSTEM_BUSY.getCode(), "请求通信[" + reqURL + "]时连接超时", cte);
        }catch(SocketTimeoutException ste){
            throw new HHTCException(CodeEnum.SYSTEM_BUSY.getCode(), "请求通信[" + reqURL + "]时读取超时", ste);
        }catch(Exception e){
            throw new HHTCException(CodeEnum.SYSTEM_ERROR.getCode(), "请求通信[" + reqURL + "]时遇到异常", e);
        }finally{
            httpClient.getConnectionManager().shutdown();
        }
    }


    public static Map<String, String> postWithDownload(String reqURL, Map<String, String> params){
        LogUtil.getLogger().info("请求{}的报文为-->>{}", reqURL, JadyerUtil.buildStringFromMap(params));
        Map<String, String> resultMap = new HashMap<>();
        HttpClient httpClient = new DefaultHttpClient();
        httpClient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, DEFAULT_CONNECTION_TIMEOUT);
        httpClient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, DEFAULT_SO_TIMEOUT);
        HttpPost httpPost = new HttpPost(reqURL);
        HttpEntity entity = null;
        try{
            if(null != params){
                List<NameValuePair> formParams = new ArrayList<>();
                for(Map.Entry<String,String> entry : params.entrySet()){
                    formParams.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
                }
                httpPost.setEntity(new UrlEncodedFormEntity(formParams, DEFAULT_CHARSET));
            }
            httpClient = addTLSSupport(httpClient);
            HttpResponse response = httpClient.execute(httpPost);
            entity = response.getEntity();
            if(null==entity || null==entity.getContentType() || (!entity.getContentType().getValue().startsWith(ContentType.APPLICATION_OCTET_STREAM.getMimeType())) && !entity.getContentType().getValue().contains("image/jpeg")){
                String decodeCharset;
                ContentType respContentType = ContentType.get(entity);
                if(null == respContentType){
                    decodeCharset = DEFAULT_CHARSET;
                }else if(null == respContentType.getCharset()){
                    decodeCharset = DEFAULT_CHARSET;
                }else{
                    decodeCharset = respContentType.getCharset().displayName();
                }
                resultMap.put("failReason", null==entity ? "" : EntityUtils.toString(entity, decodeCharset));
                resultMap.put("isSuccess", "no");
            }else{
                String filename = null;
                for(Header header : response.getAllHeaders()){
                    if(header.toString().startsWith("Content-Disposition")){
                        filename = header.toString().substring(header.toString().indexOf("filename=")+10);
                        filename = filename.substring(0, filename.length()-1);
                        break;
                    }
                }
                if(StringUtils.isBlank(filename)){
                    Header contentHeader = response.getFirstHeader("Content-Disposition");
                    if(null != contentHeader){
                        HeaderElement[] values = contentHeader.getElements();
                        if(values.length == 1){
                            NameValuePair param = values[0].getParameterByName("filename");
                            if(null != param){
                                filename = param.getValue();
                            }
                        }
                    }
                }
                if(StringUtils.isBlank(filename)){
                    filename = RandomStringUtils.randomNumeric(16);
                }
                File _file = new File(System.getProperty("java.io.tmpdir") + "/" + filename);
                FileUtils.copyInputStreamToFile(entity.getContent(), _file);
                resultMap.put("fullPath", _file.getCanonicalPath());
                resultMap.put("isSuccess", "yes");
            }
            LogUtil.getLogger().info("请求{}得到应答<<--{}", reqURL, JadyerUtil.buildStringFromMap(resultMap));
            return resultMap;
        }catch(ConnectTimeoutException cte){
            throw new HHTCException(CodeEnum.SYSTEM_BUSY.getCode(), "请求通信[" + reqURL + "]时连接超时", cte);
        }catch(SocketTimeoutException ste){
            throw new HHTCException(CodeEnum.SYSTEM_BUSY.getCode(), "请求通信[" + reqURL + "]时读取超时", ste);
        }catch(Exception e){
            throw new HHTCException(CodeEnum.SYSTEM_ERROR.getCode(), "请求通信[" + reqURL + "]时遇到异常", e);
        }finally{
            try{
                EntityUtils.consume(entity);
            }catch(IOException e){
                LogUtil.getLogger().error("请求通信[" + reqURL + "]时关闭远程应答文件流时发生异常,堆栈轨迹如下", e);
            }
            httpClient.getConnectionManager().shutdown();
        }
    }


    private static HttpClient addTLSSupport(HttpClient httpClient) throws Exception {
        X509TrustManager trustManager = new X509TrustManager(){
            @Override
            public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {}
            @Override
            public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {}
            @Override
            public X509Certificate[] getAcceptedIssuers() {return null;}
        };
        X509HostnameVerifier hostnameVerifier = new X509HostnameVerifier(){
            @Override
            public void verify(String host, SSLSocket ssl) throws IOException {}
            @Override
            public void verify(String host, X509Certificate cert) throws SSLException {}
            @Override
            public void verify(String host, String[] cns, String[] subjectAlts) throws SSLException {}
            @Override
            public boolean verify(String arg0, SSLSession arg1) {return true;}
        };
        SSLContext sslContext = SSLContext.getInstance(SSLSocketFactory.TLS);
        sslContext.init(null, new TrustManager[]{trustManager}, null);
        SSLSocketFactory socketFactory = new SSLSocketFactory(sslContext, hostnameVerifier);
        httpClient.getConnectionManager().getSchemeRegistry().register(new Scheme("https", 443, socketFactory));
        return httpClient;
    }


    private static HttpClient addTLSSupport(HttpClient httpClient, String filepath, String password) throws Exception {
        KeyStore keyStore = KeyStore.getInstance("PKCS12");
        FileInputStream fis = new FileInputStream(new File(filepath));
        try {
            keyStore.load(fis, password.toCharArray());
        } finally {
            IOUtils.closeQuietly(fis);
        }
        SSLContext sslcontext = SSLContexts.custom().loadKeyMaterial(keyStore, password.toCharArray()).build();
        SSLSocketFactory socketFactory = new SSLSocketFactory(sslcontext);
        httpClient.getConnectionManager().getSchemeRegistry().register(new Scheme("https", 443, socketFactory));
        return httpClient;
    }
}