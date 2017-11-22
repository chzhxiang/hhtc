package com.jadyer.seed.comm.util;

import com.google.common.collect.Maps;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.StringReader;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * XML工具类
 * @version v1.0
 * @history v1.0-->新建此类并添加了四个方法：xml和map互转、美化xml、转义xml
 * Created by 玄玉<http://jadyer.cn/> on 2017/7/8 16:44.
 */
public final class XmlUtil {
    private XmlUtil(){}

    public static Map<String, String> xmlToMap(String xmlStr){
        Map<String, String> dataMap = Maps.newHashMap();
        try {
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            Document document = documentBuilder.parse(IOUtils.toInputStream(xmlStr, StandardCharsets.UTF_8));
            Element rootElement = document.getDocumentElement();
            NodeList childNodeList = rootElement.getChildNodes();
            for(int i=0; i<childNodeList.getLength(); i++){
                Node node = childNodeList.item(i);
                if(node instanceof Element){
                    dataMap.put(node.getNodeName(), node.getTextContent());
                }
            }
        }catch(Exception e){
            LogUtil.getLogger().error("xml字符串转Map时发生异常，堆栈轨迹如下", e);
        }
        return dataMap;
    }


    public static String mapToXml(Map<String, String> dataMap){
        StringBuilder sb = new StringBuilder();
        sb.append("<xml>");
        for(Map.Entry<String,String> entry : dataMap.entrySet()){
            if(StringUtils.isNotEmpty(entry.getValue())){
                sb.append("<").append(entry.getKey()).append(">").append(entry.getValue()).append("</").append(entry.getKey()).append(">");
            }
        }
        sb.append("</xml>");
        return sb.toString();
    }


    public static String escapeXml(String input) {
        if(StringUtils.isBlank(input)){
            return "";
        }
        input = input.replaceAll("&", "&amp;");
        input = input.replaceAll("<", "&lt;");
        input = input.replaceAll(">", "&gt;");
        input = input.replaceAll("\"", "&quot;");
        input = input.replaceAll("'", "&apos;");
        return input;
    }


    public static Map<String, String> formatXMLString(String xmlString) {
        Map<String, String> resultMap = new HashMap<>();
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        transformerFactory.setAttribute("indent-number", 2);
        StringWriter writer = new StringWriter();
        Transformer transformer;
        try {
            transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty("{http://xml.apache.org/xalan}indent-amount", "2");
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.transform(new StreamSource(new StringReader(xmlString)), new StreamResult(writer));
        } catch (TransformerException e) {
            resultMap.put("isPrettySuccess", "no");
            resultMap.put("prettyResultStr", ExceptionUtils.getStackTrace(e));
            return resultMap;
        }
        resultMap.put("isPrettySuccess", "yes");
        resultMap.put("prettyResultStr", writer.toString());
        return resultMap;
    }
}