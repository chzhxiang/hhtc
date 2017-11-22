package com.jadyer.seed.comm.util;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Map;

/**
 * 玄玉的开发工具类
 * @version v3.16
 * @history v3.16-->增加leftPadUseZero()字符串左补零的方法
 * @history v3.15-->增加getFullContextPath()用于获取应用的完整根地址并移除两个XML方法至{@link XmlUtil}
 * @history v3.14-->移动requestToBean()和beanCopyProperties()至BeanUtil.java，并移除若干重复造轮子的方法
 * @history v3.13-->增加获取本周第一天、判断是否本周第一天、判断是否本月第一天的三个方法
 * @history v3.12-->增加获取应用运行进程的PID的方法getPID()
 * @history v3.11-->增加十六进制字符串转为byte[]的方法hexToBytes()
 * @history v3.10-->增加通过反射实现的JavaBean之间属性拷贝的方法beanCopyProperties()
 * @history v3.9-->修正打印入参为java.util.Map时可能引发的NullPointerException
 * @history v3.8-->修正部分细节并增加<code>getDetailDate(dateStr)</code>方法
 * @history v3.7-->add method of <code>escapeEmoji()</code> for escape Emoji to *
 * @history v3.6-->add method of <code>bytesToHex()</code> for convert byte to hex
 * @history v3.5-->增加requestToBean()用于将HttpServletRequest参数值转为JavaBean的方法
 * @history v3.4-->增加extractHttpServletRequestMessage用于提取HTTP请求完整报文的两个方法
 * @history v3.3-->增加isAjaxRequest()用于判断是否为Ajax请求的方法
 * @history v3.2-->增加getCurrentWeekStartDate()和getCurrentWeekEndDate()用于获取本周开始和结束的时间
 * @history v3.1-->修改<code>captureScreen()</code>方法增加是否自动打开生成的抓屏图片的功能
 * @history v3.0-->修改<code>htmlEscape()</code>方法名为<code>escapeHtml()</code>,并新增<code>escapeXml</code>方法
 * @history v2.9-->新增<code>getIncreaseDate()</code>用于计算指定日期相隔一定天数后的日期
 * @history v2.8-->移除加解密相关方法到CodecUtil类中,增加了模拟OracleSequence、抓屏、格式化XML字符串、统计代码行数等方法
 * @history v2.7-->新增<code>extractStackTrace()</code>用于提取堆栈信息的方法
 * @history v2.6-->重命名了若干方法,使之更形象,通俗易懂,并删除了getStringSimple()、getStringForInt()等方法
 * @history v2.5-->新增<code> getStringForInt()</code>用于将阿拉伯字节数组转为整型数值的方法
 * @history v2.4-->新增<code>genAESEncrypt()和genAESDecrypt()</code>用于AES加解密的方法
 * @history v2.3-->修改<code>rightPadForByte(),leftPadForByte()</code>方法左右填充的字符为0x00
 * @history v2.2-->新增<code>isNotEmpty()</code>用于判断输入的字符串或字节数组是否为非空的方法
 * @history v2.1-->新增<code>formatToHexStringWithASCII()</code>用于格式化字节数组为十六进制字符串的方法
 * @history v2.0-->局部的StringBuffer一律StringBuilder之(本思路提示自坦克<captmjc@gmail.com>)
 * @history v1.5-->新增<code>getStringSimple()</code>获取一个字符串的简明效果,返回的字符串格式类似于"abcd***hijk"
 * @history v1.4-->新增<code>getHexSign()</code>根据指定的签名密钥和算法签名Map<String,String>
 * @history v1.3-->修改<code>getSysJournalNo()</code>实现细节为<code>java.util.UUID.randomUUID()</code>
 * @history v1.2-->新增<code>getString()</code>字节数组转为字符串方法
 * @history v1.1-->新增<code>getHexSign()</code>通过指定算法签名字符串方法
 * Created by 玄玉<http://jadyer.cn/> on 2012/12/22 19:00.
 */
public final class JadyerUtil {
    private static BigInteger sequenceNo = new BigInteger("0");
    private static BigInteger maxSequenceNo = new BigInteger("999999999");

    private JadyerUtil(){}

    public static String buildStringFromMap(Map<String, ?> map){
        if(null==map || map.isEmpty()){
            return "";
        }
        StringBuilder sb = new StringBuilder();
        sb.append(map.getClass().getName()).append("@").append(map.hashCode()).append("[");
        for(Map.Entry<String, ?> entry : map.entrySet()){
            sb.append("\n").append(entry.getKey()).append("=");
            Object _value = entry.getValue();
            if(_value instanceof String){
                sb.append(_value);
            }
            if(_value instanceof String[]){
                sb.append(Arrays.toString((String[])_value));
            }
            if(_value instanceof byte[]){
                sb.append(new String((byte[])_value));
            }
        }
        return sb.append("\n]").toString();
    }


    public static boolean isAjaxRequest(HttpServletRequest request){
        String requestType = request.getHeader("X-Requested-With");
        if(null!=requestType && "XMLHttpRequest".equals(requestType)){
            return true;
        }
        requestType = request.getHeader("x-requested-with");
        return null!=requestType && "XMLHttpRequest".equals(requestType);
    }


    public static String leftPadUseZero(String str, int size){
        char[] srcArray = str.toCharArray();
        char[] destArray = new char[size];
        Arrays.fill(destArray, '0');
        if(srcArray.length >= size){
            System.arraycopy(srcArray, 0, destArray, 0, size);
        }else{
            System.arraycopy(srcArray, 0, destArray, size-srcArray.length, srcArray.length);
        }
        return String.valueOf(destArray);
    }


    public static String escapeEmoji(String emoji){
        if(StringUtils.isNotBlank(emoji)){
            return emoji.replaceAll("[\\ud800\\udc00-\\udbff\\udfff\\ud800-\\udfff]", "*");
        }else{
            return emoji;
        }
    }


    public static String extractHttpServletRequestHeaderMessage(HttpServletRequest request){
        StringBuilder sb = new StringBuilder();
        sb.append(request.getMethod()).append(" ").append(request.getRequestURI()).append(null==request.getQueryString()?"":"?"+request.getQueryString()).append(" ").append(request.getProtocol()).append("\n");
        String headerName;
        for(Enumeration<String> obj = request.getHeaderNames(); obj.hasMoreElements();){
            headerName = obj.nextElement();
            sb.append(headerName).append(": ").append(request.getHeader(headerName)).append("\n");
        }
        return sb.toString();
    }


    public static String extractHttpServletRequestBodyMessage(HttpServletRequest request){
        try{
            request.setCharacterEncoding("UTF-8");
        }catch(UnsupportedEncodingException e1){
            //ignore
        }
        StringBuilder sb = new StringBuilder();
        BufferedReader br = null;
        try{
            br = request.getReader();
            for(String line; (line=br.readLine())!=null;){
                sb.append(line).append("\n");
            }
        }catch(IOException e){
            throw new RuntimeException(e);
        }finally {
            if(null != br){
                IOUtils.closeQuietly(br);
            }
        }
        return sb.toString();
    }
}