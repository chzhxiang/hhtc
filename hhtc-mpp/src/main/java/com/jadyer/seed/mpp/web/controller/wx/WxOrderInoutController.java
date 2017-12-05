package com.jadyer.seed.mpp.web.controller.wx;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.jadyer.seed.comm.constant.CodeEnum;
import com.jadyer.seed.comm.constant.CommonResult;
import com.jadyer.seed.comm.exception.HHTCException;
import com.jadyer.seed.comm.util.DateUtil;
import com.jadyer.seed.comm.util.HttpUtil;
import com.jadyer.seed.comm.util.JadyerUtil;
import com.jadyer.seed.comm.util.LogUtil;
import com.jadyer.seed.mpp.web.HHTCHelper;
import com.jadyer.seed.mpp.web.model.*;
import com.jadyer.seed.mpp.web.repository.OrderInoutRepository;
import com.jadyer.seed.mpp.web.repository.TemporaryOutRepository;
import com.jadyer.seed.mpp.web.service.*;
import com.jadyer.seed.mpp.web.service.async.CommunityDeviceCache;
import com.jadyer.seed.mpp.web.service.async.CommunityDeviceFlowAsync;
import com.jadyer.seed.mpp.web.service.async.WeixinTemplateMsgAsync;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Comment by 玄玉<http://jadyer.cn/> on 2017/9/2 10:56.
 */
@Controller
@RequestMapping("/wx/orderinout")
public class WxOrderInoutController {
    @Resource
    private HHTCHelper hhtcHelper;
    @Resource
    private GoodsService goodsService;
    @Resource
    private OrderInforService orderInforService;
    @Resource
    private OrderInoutService orderInoutService;
    @Resource
    private WeixinTemplateMsgAsync weixinTemplateMsgAsync;
    @Resource
    private TemporaryOutRepository temporaryOutRepository;
    @Resource
    private CommunityDeviceFlowAsync communityDeviceFlowAsync;

    @ResponseBody
    @RequestMapping("/reg")
    public CommonResult reg(){
        return new CommonResult();
    }


    /**
     * 找到应该匹配到的那一条记录（同一天存在多笔订单）
     * @param type 1--入场，2--出场
     */
    private OrderInout findMatchInout(List<OrderInout> inoutList, int type){
        OrderInout inout = null;
//        for(OrderInout obj : inoutList){
//            OrderInfo order = orderService.getByOrderNo(obj.getOrderNo());
//            if(order.getOrderStatus() == 99){
//                continue;
//            }
//            if(1==type && new Date().compareTo(hhtcHelper.convertToDate(obj.getEndDate(), obj.getEndTime()))<0){
//                inout = obj;
//                break;
//            }
//            if(2==type && null!=obj.getInTime() && null==obj.getOutTime()){
//                inout = obj;
//                break;
//            }
//        }
        return inout;
    }


    /**
     * TOKGO  小区开 闸门开关信息
     * @param
     * @param deviceId    设备信息ID
     */
    @RequestMapping("/{deviceId}")
    public String receiveMsg(@PathVariable long deviceId, HttpServletRequest request, HttpServletResponse response) throws IOException, ParseException {
        Date dtime = DateUtils.parseDate("20000101000000", "yyyyMMddHHmmss");
        String respMsg;
        String reqBodyMsg = JadyerUtil.extractHttpServletRequestBodyMessage(request);
        LogUtil.getLogger().info("收到一体机消息如下\n{}", JadyerUtil.extractHttpServletRequestHeaderMessage(request)+"\n"+reqBodyMsg);
        //原工程注释
        //        Map<String, Map<String, Map<String, Map<String, String>>>> dataMap = JSON.parseObject(reqBodyMsg, new TypeReference<Map<String, Map<String, Map<String, Map<String, String>>>>>(){});
//        String carNumber = dataMap.get("AlarmInfoPlate").get("result").get("PlateResult").get("license");
        Map<String, String> dataMap = JSON.parseObject(reqBodyMsg, new TypeReference<Map<String, String>>(){});
        dataMap = JSON.parseObject(dataMap.get("AlarmInfoPlate"), new TypeReference<Map<String, String>>(){});
        dataMap = JSON.parseObject(dataMap.get("result"), new TypeReference<Map<String, String>>(){});
        dataMap = JSON.parseObject(dataMap.get("PlateResult"), new TypeReference<Map<String, String>>(){});
        //获取车牌号
        String carNumber = dataMap.get("license");
        carNumber = carNumber.toUpperCase();
        CommunityDevice device = CommunityDeviceCache.get(deviceId);
        if (device== null)
            throw new HHTCException(CodeEnum.SYSTEM_PARAM_DATA_ERROR);
        CommunityDeviceFlow communityDeviceFlow = new CommunityDeviceFlow();
        //出入类型：1--进场，2--出场
        if(null==device.getId() || device.getId()==0){
            respMsg = "配置错误：无效的设备ID";
        }
        //TODO 添加订单流水
//        List<OrderInout> inoutList = orderInoutRepository.findByCommunityIdAndCarNumberAndFromDateInOrderByFromDateAscFromTimeAsc(device.getCommunityId(), carNumber, fromDateList);
        OrderInfor order =null;

        switch (orderInoutService.CheckCarNumber(carNumber,device,order)){
            case 1:respMsg = "进出无效通知：非平台下单车主";break;
            case 2:respMsg = "入场重复通知：车主已经入场了";break;
            case 3:respMsg = "出场重复通知：车主已经出场了";break;
            case 4:respMsg = "进入成功";
                if (Open(communityDeviceFlow,device))
                    //更新订单信息
                    orderInforService.UpdateInoutState(1,order.getOrderId());
                else {
                    respMsg = "开闸失败（进口）";
                    //TODO 开闸失败需要通知运营
                }
                break;
            case 5:respMsg = "出库成功";
                if (Open(communityDeviceFlow,device))
                    //更新订单信息
                    orderInforService.UpdateInoutState(0,order.getOrderId());
                else {
                    respMsg = "开闸失败（出口）";
                    //TODO 开闸失败需要通知运营
                }
            case 6:respMsg = "出库成功";
                if (Open(communityDeviceFlow,device))
                    temporaryOutRepository.delete(temporaryOutRepository.findByCommunityIdAndCarNumber(
                            device.getCommunityId(),carNumber));
                else {
                    respMsg = "开闸失败（出口）";
                    //TODO 开闸失败需要通知运营
                }
                break;
             default:respMsg="进出无效通知：非平台下单车主";break;
        }
        communityDeviceFlow.setDeviceId(device.getId());
        communityDeviceFlow.setCommunityId(device.getCommunityId());
        communityDeviceFlow.setCommunityName(device.getCommunityName());
        communityDeviceFlow.setScanTime(new Date());
        communityDeviceFlow.setScanCarNumber(carNumber);
        communityDeviceFlow.setOpenRemark(respMsg);
        //TODO 订单流水可以在这里做
        communityDeviceFlowAsync.add(communityDeviceFlow);
        String respData = "{\"Response_AlarmInfoPlate\":{\"manualTigger\":\"ok\",\"msg\":\""+ respMsg +"\"}}";
        LogUtil.getLogger().info("应答给一体机消息为{}", respData);
        response.setCharacterEncoding(HttpUtil.DEFAULT_CHARSET);
        response.setContentType("text/plain; charset=" + HttpUtil.DEFAULT_CHARSET);
        response.setHeader("Cache-Control", "no-cache");
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0);
        PrintWriter out = response.getWriter();
        out.print(respData);
        out.flush();
        out.close();
        return null;
    }
    private boolean Open(CommunityDeviceFlow communityDeviceFlow,CommunityDevice device){
        communityDeviceFlow.setScanAllowOpen(1);
        if(hhtcHelper.openDoor(device.getSerialnoRelays(), device.getRelaysDoorid())){
            communityDeviceFlow.setOpenResult(1);
            communityDeviceFlow.setOpenTime(new Date());
            return true;
        }else{
            return false;
        }
    }

}
/*
//下面是“简略”的消息
[170902173302][http-bio-8081-exec-3][WxOrderInoutController.receiveMsg]收到一体机消息如下
POST /wx/orderinout/1 HTTP/1.0
host: test1.houhoutech.com
x-forwarded-for: 113.206.196.178
x-real-ip: 113.206.196.178
x-scheme: http
connection: close
content-length: 460
accept: * /*
content-type: application/Json

{
    "AlarmInfoPlate" : {
        "channel" : 0,
        "deviceName" : "IVS",
        "ipaddr" : "192.168.1.101",
        "result" : {
            "PlateResult" : {
                "colorType" : 1,
                "direction" : 0,
                "imagePath" : "%2Fmmc%2FVzIPCCap%2F2017_09_02%2F1734169004_%D4%C1FQK883.jpg",
                "license" : "粤FQK883",
                "triggerType" : 4,
                "type" : 1
            }
        },
        "serialno" : "33ae5105-d7732a0d"
    }
}

[170902173302][http-bio-8081-exec-3][GlobalExceptionHandler.process]Exception Occured URL=http://test1.houhoutech.com/wx/orderinout/1,堆栈轨迹如下
*/
/*
//下面是“全部”的消息
{
	//车牌识别结果
    "AlarmInfoPlate":{
    	//默认通道号（预留）
        "channel":0,
        //设备名称
        "deviceName":"IVS",
        //设备IP地址
        "ipaddr":"192.168.1.100",
        //实际数据
        "result":{
        	//车牌识别信息
            "PlateResult":{
            	//预留
                "bright":0,
                //车身亮度（预留）
                "carBright":0,
                //车身颜色（预留）
                "carColor":0,
                //车牌颜色0：未知、1：蓝色、2：黄色、3：白色、4：黑色、5：绿色
                "colorType":0,
                //预留
                "colorValue":0,
                //识别结果可行度1-100
                "confidence":0,
                //车的行进方向（预留）
                "direction":0,
                //截图的http地址路径
                "imagePath":"%2Fmmc%2FVzIPCCap%2F2015_09_09%2F1714224504__%CE%DE_.jpg",
                //车牌号字符串，如“京AAAAAA”
                "license":"_无_",
                //车牌在图片中位置
                "location":{
                	//位置为矩形区域
                    "RECT":{
                    	//left\right\top\bottom：车牌在图片中位置
                        "bottom":0,
                        "left":0,
                        "right":0,
                        "top":0
                    }
                },
                //识别结果对应帧的时间戳
                "timeStamp":{
                	//时间戳结构体类型
                    "Timeval":{
                    	//从1970年1月1日到对应帧的秒和毫秒
                        "sec":1441815171,
                        "usec":672241
                    }
                },
                //识别所用时间（预留）
                "timeUsed":0,
                //当前结果的触发类型：由以下值取并的结果：1：自动触发类型、2：外部输入触发（IO输入）、4：软件触发（SDK）、8：虚拟线圈触发
                "triggerType":4,
                //车牌类型0：未知车牌:、1：蓝牌小汽车、2：黑牌小汽车、3：单排黄牌、4：双排黄牌、5：警车车牌、6：武警车牌、7：个性化车牌、8：单排军车牌、9：双排军车牌、10：使馆车牌、11：香港进出中国大陆车牌、12：农用车牌、13：教练车牌、14：澳门进出中国大陆车牌、15：双层武警车牌、16：武警总队车牌、17：双层武警总队车牌
                "type":0
            }
        },
        //设备序列号
        "serialno":"eff50e18-e3d3862b"
    }
}
*/
/*
{
    "Response_AlarmInfoPlate":{
        //回复ok开闸
        "info":"ok",
        //回复ok进行手动触发
        "manualTigger":"ok",
        //可选，不触发截图可不添加该字段
        "TriggerImage":{
            //回复截图内容端口号（可选，不填则默认使用http页面配置端口）
            "port":80,
            //回复截图内容相对路径（可选，不触发截图可不添加该字段）
            "snapImageRelativeUrl":" /devicemanagement/php/receivedeviceinfo.php",
            //回复截图内容绝对路径（可选，不触发截图可不添加该字段）
            "snapImageAbsolutelyUrl":"http://192.168.1.106/devicemanagement/php/receivedeviceinfo.php"
        },
        "is_pay":"true",
        //回复串口数据可以发送到相应串口
        "serialData":[
            //数据1，可以有或者没有，收到后将发送到对应串口
            {
                "serialChannel":0,
                "data":"…",
                "dataLen":123
            },
            {
                "serialChannel":1,
                "data":"…",
                "dataLen":123
            }
        ]
    }
}
*/