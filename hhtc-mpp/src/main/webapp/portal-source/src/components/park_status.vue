<template>
  <div class="park_status">
    <div class="bod">
      <div class="scroll">
        <div class="district_info font32">
        <div class="word"><span class="l fcg43">小区名称:</span><span class="r fcgb1 chose_district"><input type="text" name="" placeholder="请选择小区" class="font32" readonly @click="choseName" v-model="nowName.name?nowName.name:showName"></span></div>
        <div class="word"><span class="l fcg43">车位号:</span><span class="r duan fcg43"><input class="number inputnumber font32" type="text" name="" placeholder="请输入您车位号" v-model="carParkNumber"></span><span class="r chose_district" :class="quyu=='请选择'?'fcg95':''" @click="choseQuyu">{{quyu}}</span><span class="r chose_district" :class="floor=='请选择'?'fcg95':''" @click="choseFloor">{{floor}}</span></div>
        <div class="word"><span class="l fcg43">业主电话:</span><span class="r"><input class="font32" type="number" name="" placeholder="请输入您手机号" v-model="phone" readonly></span></div>
        <!--<div class="word"><span class="l fcg43">车位产权证明:</span><span class="r"><img :src="showImage?showImage:'static/img/no_ss.png'" @click="choseImagrs"></span></div>-->
        <div class="word"><span class="l fcg43">车位有效期:</span><span class="r chose_district" @click="choseDay(2)" :class="endDay=='结束日期'?'fcg95':''">{{endDay}}</span><span class="r chose_district" style="margin-right:.2rem;" @click="choseDay(1)" :class="beginDay=='开始日期'?'fcg95':''">{{beginDay}}</span></div>
        <div class="word"><span class="l fcg43">车位状态:</span><span class="r fcg43 font28">{{getStatus(parkInfo)}}</span></div>
        <div class="word" v-if="parkInfo.carAuditRemark"><span class="l fcg43">拒绝原因:</span><span class="r fcg43 font28">{{parkInfo.carAuditRemark}}</span></div>
      </div>
      <div class="checkout font34 fcw" v-if="nowStatus>=3" @click="goRouter('/publish_park?goodsId='+goodsId)">{{nowStatus==3?"立即发布":"发布其它时间段"}}</div>
      <div class="towbutton font34 fcw" v-if="nowStatus==2"><span @click="modify">修改</span><span @click="delPark">删除</span></div>
      <div class="towbutton font34 fcw" v-if="nowStatus<2"><span @click="goBack()">返回</span></div>
      <div class="time_list fcg95 font26" v-if="nowStatus>=4&&parkInfoList.length>0">
        <div class="fcg43 time_list_title">历史发布时段</div>
          <div class="time_lists" v-for="park in parkInfoList" @click="showExclude(park)">
            <div class="top info"><span class="l">模式: {{getParkType(park.publishType)}}</span><span class="l">时段: {{getParkTime(park.publishFromTime,park.publishEndTime)}}</span></div>
            <div class="bottom info"><span class="l">日期: {{getParkDays(park.publishFromDates)}}</span></div>
            <div class="btn centered_y fcw" @click="stopPark($event,park)" v-if="getParkNowType(park.status)=='未锁定'">停止共享</div>
            <div class="park_now_status" v-else>{{getParkNowType(park.status)}}</div>
        </div>
      </div>
      </div>
    </div>
    
    <!--发布成功-->
    <div class="ceng ceng_district fcg43 font30" @click="close('ceng_district')" style="display:none;">
      <div class="ceng_content centered" @click="emptyClick($event)">
        <div class="title font38">选择小区</div>
        <div class="word"><span class="l">省:</span><span class="r" @click="getProvince">{{nowProvince}}</span></div>
        <div class="word"><span class="l">市:</span><span class="r" @click="getCity">{{nowCity}}</span></div>
        <div class="word"><span class="l">小区名称:</span><span class="r" @click="getName">{{nowName.name}}</span></div>
      </div>
    </div>
    <div class="ceng ceng_check ceng_check_w fcg95 font26" style="display:none;" @click="close('ceng_check_w')">
      <div class="ceng_content centered" :style="{paddingBottom:isShowexclude?'.3rem':0}" @click="emptyClick($event)">
        <div class="title fcb font38">{{isShowexclude?"发布详情":"确定不共享该时段吗"}}</div>
        <p class="word fcg43 font22"><span class="left">放租模式：</span><span class="fcb">{{getParkType(stopParkInfo.publishType)}}</span></p>
        <p class="word fcg43 font22"><span class="left">放租时间：</span><span class="fcb">{{getParkDays(stopParkInfo.publishFromDates)}}</span></p>
        <p class="word fcg43 font22"><span class="left">发布时段：</span><span class="fcb">{{getParkTime(stopParkInfo.publishFromTime,stopParkInfo.publishEndTime)}}</span></p>
        <p class="word fcg43 font22 noxianz" v-if="isShowexclude"><span class="left">排除时间：</span><span class="fcb">{{excludeDays.length==0?'无':excludeDays.join(' ; ')}}</span></p>
        <div class="ceng_operating fcw font34" v-if="!isShowexclude"><span @click="close('ceng_check_w')">取消</span><span @click="suer">确定</span></div>
      </div>
    </div>
    <div class="ceng ceng_del ceng_check fcg95 font26" style="display:none;" @click="close('ceng_del')">
      <div class="ceng_content centered" @click="emptyClick($event)">
        <div class="title fcb font38">确定要删除该车位吗</div>
        <p class="word fcg43 font22">删除车位后需要重新审核才能进行发布哦！</p>
        <div class="ceng_operating fcw font34"><span @click="close('ceng_del')">取消</span><span @click="suer_del">确定</span></div>
      </div>
    </div>
    <ul id="education_list" style="display: none">
      <li v-for="value in showList" :data="value">{{value.name}}</li>
    </ul>
    <ul id="education_list5" style="display: none">
      <li v-for="(value,key) in carParkNumberPrefix" :data="key">{{key}}</li>
    </ul>
    <ul id="education_list6" style="display: none">
      <li v-for="value in carParkNumberPre" :data="value">{{value}}</li>
    </ul>
    <div id="education_list3" style="display: none">
    </div>
    <div id="education_list4" style="display: none">
    </div>
  </div>
</template>

<script>
module.exports={
	devtool:'cheap-module-source-map',
    data:function(){
      return {
        scrollInfo:{"pd":true,"limit":0,"last":false},
        carEquityImg:"",//产权证明
        showImage:"",//本地图片预览
        carParkNumber:"",//车位号
        nowProvince:"请选择省份",
        nowCity:"请选择城市",
        nowName:{"name":""},
        phone:"",
        count:0,
        showList:[],
        nowType:"",
        goodsId:"",
        parkInfo:{},
        nowStatus:0,//1审核中 2审核拒绝 3审核通过(没有发不过) 4发布中 5共享中
        canOperating:false,
        publishInfo:{},
        parkInfoList:[],
        stopParkInfo:{},
        showName:"",
        parkAllInfo:{},
        beginDay:"开始日期",
        endDay:"结束日期",
        floor:"请选择",
        quyu:"请选择",
        carParkNumberPrefix:{},
        carParkNumberPre:[],
        excludeDays:[],
        isShowexclude:false,
        week:["周日","周一","周二","周三","周四","周五","周六"]
      }
    },
    mounted:function(){
      var that=this;
      setScroll_event($(".bod"),"scroll")
      $(".bod").on("scroll",function(){
        var scrollTop=$(this).scrollTop();
        var height=$(this).offset().height;
        var all_height=$(".scroll").offset().height;
        if(scrollTop+height>=all_height-10){

          if(!that.scrollInfo.last){
            if(that.scrollInfo.pd){
              that.scrollInfo.limit+=1;
              that.scrollInfo.pd=true;
              that.getParkInfo();
            }
          }
        }
      })
      if(this.canOperating){
        $(".inputnumber").removeAttr("readonly");
      }else{
        $(".inputnumber").attr("readonly",true);
      }
      
    },
    activated:function(){
      //每次进入
      this.count=0;
      //this.getWx();
      this.getFansInfo();
      this.goodsId=getUrl().goodsId;
      this.scrollInfo={"pd":true,"limit":0,"last":false};
      this.parkInfoList=[];
      this.getParkInfo();
      this.parkInfo=toParse(localStorage.getItem("parkInfo"))
      this.showImage=this.parkInfo.carEquityImg?("/wx/common/file/get?filePath="+this.parkInfo.carEquityImg):'';
      this.carEquityImg=this.parkInfo.carEquityImg;
      this.showName=this.parkInfo.communityName;
      this.nowName.name=this.parkInfo.communityName;
      this.nowName.id=this.parkInfo.communityId;
      this.getCommunityInfo(this.nowName.id);
      var carParkNumber=this.parkInfo.carParkNumber.split(";");
      this.floor=carParkNumber[0]||"-";
      this.quyu=carParkNumber[1]||"-";
      this.carParkNumber=carParkNumber[2]||"-";
      this.beginDay=this.getParkDay(this.parkInfo.carUsefulFromDate);
      this.endDay=this.getParkDay(this.parkInfo.carUsefulEndDate);
      this.close("ceng");
      this.scrollInit();
    },
    created:function(){
      // 组件创建完
    },
    watch:{
      //监视变化
      floor:function(){
        this.carParkNumberPre=this.carParkNumberPrefix[this.floor];
      },
      nowStatus:function(){
        if(this.nowStatus==2){
          this.canOperating=true;
        }else{
          this.canOperating=false;
        }
        if(this.canOperating){
          $(".inputnumber").removeAttr("readonly");
        }else{
          $(".inputnumber").attr("readonly",true);
        }
      }
    },
    methods:{
      goBack:function(){
        history.back();
      },
      scrollInit:function(){
        var that=this,currYear=(new Date()).getFullYear();
        setTimeout(function(){
          $("#education_list3").mobiscroll().date({
          theme: "",  
          lang: "zh",  
          display: 'bottom',
          startYear: currYear - 5, //开始年份currYear-5不起作用的原因是加了minDate: new Date()
          endYear: currYear + 15,
          // wheels:[], 设置此属性可以只显示年月，此处演示，就用下面的onBeforeShow方法,另外也可以用treelist去实现  
          //onBeforeShow: function (inst) { inst.settings.wheels[0].length>2?inst.settings.wheels[0].pop():null; }, //弹掉“日”滚轮
          headerText: function (valueText) { //自定义弹出框头部格式
            var array = valueText.split('/');  
            return array[0] + "年" + array[1] + "月"+array[2]+"日"; 
          },  
          onSelect: function (valueText, inst) {
            var array = valueText.split('/');
            if(that.canOperating){
              if(that.beginDay=="开始日期"){
                error_msg("请先选择开始日期",2000)
                return false;
              }
              if(that.beginDay.replace(/-/g,"")*1>array.join("")*1){
                error_msg("结束日期必须大于开始日期",2000);
                that.endDay="结束日期";
                return false;
              }
              that.endDay=array[0] + "-" + array[1] + "-"+array[2]; 
            }
          }  
        });
        $("#education_list4").mobiscroll().date({
            theme: "",  
            lang: "zh",  
            display: 'bottom',
            startYear: currYear - 5, //开始年份currYear-5不起作用的原因是加了minDate: new Date()
            endYear: currYear + 15,
            // wheels:[], 设置此属性可以只显示年月，此处演示，就用下面的onBeforeShow方法,另外也可以用treelist去实现  
            //onBeforeShow: function (inst) { inst.settings.wheels[0].length>2?inst.settings.wheels[0].pop():null; }, //弹掉“日”滚轮
            headerText: function (valueText) { //自定义弹出框头部格式
              var array = valueText.split('/');  
              return array[0] + "年" + array[1] + "月"+array[2]+"日"; 
            },  
            onSelect: function (valueText, inst) {
              var array = valueText.split('/');
              if(that.canOperating){
                if(that.endDay!="结束日期"&&that.endDay.replace(/-/g,"")*1<array.join("")*1){
                  error_msg("结束日期必须大于开始日期",2000);
                  that.endDay="结束日期";
                }
                that.beginDay=array[0] + "-" + array[1] + "-"+array[2]; 
              }
            }  
          });
      },100)
      },
      choseDay:function(index){
        var that=this,currYear=(new Date()).getFullYear();
        if(!that.canOperating){
          return false
        }
        if(index==1){
          $("#education_list4").click();
        }else{
          $("#education_list3").click();
        }
        
      },
      choseQuyu:function(){
        var that=this;
        if(!that.canOperating){
          return false
        }
        if(that.quyu=="-"){
          return false;
        }else if(that.floor=="请选择"){
          error_msg("请先选择楼层",2000);
          return false;
        }
        $("#education_list6").mobiscroll().treelist({
          theme: "",  
          lang: "zh",  
          display: 'bottom', 
          // wheels:[], 设置此属性可以只显示年月，此处演示，就用下面的onBeforeShow方法,另外也可以用treelist去实现  
          //onBeforeShow: function (inst) { inst.settings.wheels[0].length>2?inst.settings.wheels[0].pop():null; }, //弹掉“日”滚轮
          headerText: function (valueText) { //自定义弹出框头部格式

          },  
          onSelect: function (valueText, inst) {
            that.quyu=$("#education_list6").find("li").eq(valueText).text();
          }  
        });
        $("input[id^=education_list6_dummy]").focus();
      },
      choseFloor:function(){
        var that=this;
        if(!that.canOperating){
          return false
        }
        if(toJson(this.carParkNumberPrefix)=="{}"){
          error_msg("请先选择小区",2000);
          return false;
        }
        $("#education_list5").mobiscroll().treelist({
          theme: "",  
          lang: "zh",  
          display: 'bottom', 
          // wheels:[], 设置此属性可以只显示年月，此处演示，就用下面的onBeforeShow方法,另外也可以用treelist去实现  
          //onBeforeShow: function (inst) { inst.settings.wheels[0].length>2?inst.settings.wheels[0].pop():null; }, //弹掉“日”滚轮
          headerText: function (valueText) { //自定义弹出框头部格式

          },  
          onSelect: function (valueText, inst) {
            that.floor=$("#education_list5").find("li").eq(valueText).text();
            that.carParkNumberPre=that.carParkNumberPrefix[that.floor];
            if(that.carParkNumberPre.length==0){
              that.quyu="-";
            }
          }  
        });
        $("input[id^=education_list5_dummy]").focus();
      },
      getCommunityInfo:function(id){
        //获取小区详情
        var that=this;
        if(id){
          $.ajax({
            url:urlDir+"/wx/community/get",
            type:"GET",
            data:{
              id:id
            },
            success:function(res){
              if(res.code==0){
				        that.nowProvince=res.data.provinceName;
                that.nowCity=res.data.cityName;
                that.carParkNumberPrefix=JSON.parse(res.data.carParkNumberPrefix);
                if(that.floor!="请选择")
                  that.carParkNumberPre=that.carParkNumberPrefix[that.floor];
              }
            }
          })
        }
      },
      getParkNowType:function(index){
        switch(index){
          case 0:
            return "未锁定";
          case 1:
            return "已锁定";
          case 2:
            return "被预约";
          case 3:
            return "已过期";
        }
      },
      getParkInfo:function(){
        var that=this;
        if(this.scrollInfo.pd){
          this.scrollInfo.pd=false
          $.ajax({
            url:urlDir+"/wx/goods/publish/order/listByGoodsId",
            type:"GET",
            data:{
              goodsId:this.goodsId,
              pageNo:this.scrollInfo.limit
            },
            success:function(res){
              that.scrollInfo.pd=true
              if(res.code==0){
                that.parkInfoList=that.parkInfoList.concat(res.data.content);
                that.scrollInfo.last=res.data.last;
                if(that.parkInfoList.length==0){
                  that.parkInfo.isUsed=0;
                  localStorage.setItem("parkInfo",toJson(that.parkInfo));
                }
              }
            },
            error:function(res){

            }
          })
        }
      },
      modify:function(){
        //修改
        var that=this;
        if(!this.nowName.id){
          error_msg("请先选择小区名称",2000)
          return true;
        }
        if(!this.carParkNumber){
          error_msg("请先输入车位号",2000)
          return true;
        }
        /*if(!this.carEquityImg){
          error_msg("请先上传车位产权证明",2000)
          return true;
        }*/
        if(this.beginDay=="开始日期"||this.endDay=="结束日期"){
          error_msg("请选择车位起止日期",2000)
          return true;
        }
        if(this.floor=="请选择"||this.quyu=="请选择"){
          error_msg("请选择车位号区域",2000)
          return true;
        }
        /*carEquityImg:this.carEquityImg,*/
        $.ajax({
          url:urlDir+"/wx/goods/update",
          type:"POST",
          data:{
            id:this.goodsId,
            communityId:this.nowName.id,
            carParkNumber:this.floor+";"+(this.quyu=="-"?"":this.quyu)+";"+this.carParkNumber,
            
            carUsefulFromDate:this.beginDay.replace(/-/g,""),
            carUsefulEndDate:this.endDay.replace(/-/g,"")
          },
          success:function(res){
            error_msg(res.msg,2000)
            if(res.code==0){
              history.back();
            }
          },
          error:function(res){

          }
        })
      },
      delPark:function(){
        //删除
        this.show("ceng_del");
      },
      suer_del:function(){
        var that=this;
        $.ajax({
          url:urlDir+"/wx/goods/del",
          type:"POST",
          data:{
            id:this.goodsId
          },
          success:function(res){
            error_msg(res.msg,2000)
            if(res.code==0){
              that.close("ceng_del");
              history.back();
            }
          },
          error:function(res){

          }
        })
      },
      suer:function(){
        //停止共享
        var that=this;
        $.ajax({
          url:urlDir+"/wx/goods/publish/order/cancel",
          type:"POST",
          data:{
            id:this.stopParkInfo.id
          },
          success:function(res){
            error_msg(res.msg,2000)
            if(res.code==0){
              that.close("ceng_check_w");
              that.scrollInfo={"pd":true,"limit":0,"last":false};
              that.parkInfoList=[];
              that.getParkInfo();
            }
          },
          error:function(res){

          }
        })
      },
      stopPark:function(event,obj){
        event.stopPropagation();
        this.stopParkInfo=obj;
        this.isShowexclude=false;
        this.show('ceng_check_w');
      },
      showExclude:function(obj){
        var arr=obj.publishFromDates.split("-"),that=this;
        this.excludeDays=[],
        this.isShowexclude=true;
        if(arr.length>1){
          var start=this.getParkDay(arr[0]).replace(/-/g,"/"),end=this.getParkDay(arr[arr.length-1]).replace(/-/g,"/");
          console.log(start+",,"+end)
          var startTime=new Date(start).getTime(),endTime=new Date(end).getTime();
          var date="";
          for(var i=startTime;i<=endTime;i+=24*3600000){
            date=getTimes(i,"YMD",false);
            console.log(date)
            if(arr.indexOf(date.replace(/-/g,""))==-1){
              that.excludeDays.push(date+"("+that.week[new Date(date.replace(/-/g,"/")).getDay()]+")");
            }
          }
        }
        this.stopParkInfo=obj;
        this.show('ceng_check_w');
      },
      getParkType:function(index){
        //获取当前发布时段的状态模式
        switch(index){
          case 1:
            return "白天";
          case 2:
            return "夜间";
          case 3:
            return "全天"
        }
      },
      getParkTime:function(beginTime,endTime){
        //获取显示的时间段
        var zeng=Math.floor(beginTime/100)
        var beginWord=(zeng>=10?zeng:("0"+zeng))+":"+((beginTime%100)==30?"30":"00");
        zeng=Math.floor(endTime/100)
        var endWord=(zeng>=10?zeng:("0"+zeng))+":"+((endTime%100)==30?"30":"00");
        return beginWord+"-"+(beginTime>=endTime?"次日":"")+endWord;
      },
      getParkDay:function(index){
        //进来格式20170911
        if(index){
          index=index+""
          return index.substr(0,4)+"-"+index.substr(4,2)+"-"+index.substr(6,2);
        }
      },
      getParkDays:function(days){
        if(days){
          var arr=days.split("-");
          return this.getParkDay(arr[0])+" 至 "+this.getParkDay(arr[arr.length-1]);
        }
        return "";
      },
      getStatus:function(obj){
        //获取当前车位状态
        switch(obj.carAuditStatus){
          case 1:
            this.nowStatus=1;
            return "审核中";
          case 3:
            this.nowStatus=2;
            return "审核拒绝"
        }
        switch(obj.isUsed){
          case 0:
            this.nowStatus=3;
            return "待发布";
          case 1:
            this.nowStatus=4;
            return "发布中";
          case 2:
            this.nowStatus=5;
            return "发布中";
        }
      },
      getFansInfo:function(){
        //获取用户信息
        var that=this;
        $.ajax({
          url:urlDir+"/wx/fans/get",
          type:"GET",
          success:function(res){
            if(res.code==0){
              that.phone=res.data.phoneNo
            }
          },
          error:function(res){

          }
        })
      },
      getWx:function(){
        //微信签名
        var that=this;
        $.ajax({
          url:urlDir+"/wx/jssdk/sign",
          type:"POST",
          data:{
            url:location.href
          },
          success:function(data){
            if(typeof(data)=="string"){
              data=toParse(data)
            }
            if(data.data.appid){
              wx.config({
                  debug: false, // 开启调试模式,调用的所有api的返回值会在客户端alert出来，若要查看传入的参数，可以在pc端打开，参数信息会通过log打出，仅在pc端时才会打印。
                  appId: data.data.appid, // 必填，公众号的唯一标识
                  timestamp: parseInt(data.data.timestamp), // 必填，生成签名的时间戳
                  nonceStr: data.data.noncestr, // 必填，生成签名的随机串
                  signature:data.data.signature,// 必填，签名，见附录1
                  jsApiList: ["chooseImage","uploadImage","previewImage"]
              });
            }else{
              that.count++;
              if(that.count<4)
                that.getWx();
            }
          }
        })
      },
      choseImagrs:function(){
        //选择图片
        var that=this;
        if(this.canOperating){
          wx.chooseImage({
            count:1, // 默认9
            sizeType: ['original'], // 可以指定是原图还是压缩图，默认二者都有
            sourceType: ['album', 'camera'], // 可以指定来源是相册还是相机，默认二者都有
            success: function (res) {
              that.showImage=res.localIds[0];
              wx.uploadImage({
                localId:res.localIds[0], // 需要上传的图片的本地ID，由chooseImage接口获得
                isShowProgressTips:1, // 默认为1，显示进度提示
                success: function (res){
                  that.imgUpload(res.serverId)
                     // 返回图片的服务器端ID
                    //;
                }
            });
            }
          });
        }else{
          wx.previewImage({
              current:location.protocol+"//"+location.host+this.showImage, // 当前显示图片的http链接
              urls:[location.protocol+"//"+location.host+this.showImage]// 需要预览的图片http链接列表
          });
        }
      },
      imgUpload:function(serverId){
        var that=this
        $.ajax({
          url:urlDir+"/wx/media/upload",
          type:"GET",
          data:{
            serverId:serverId
          },
          success:function(res){
            if(res.code==0){
              that.carEquityImg=res.data.filepath
            }
          },
          error:function(res){

          }
        })
      },
      choseName:function(){
        if(this.canOperating){
          this.nowProvince="请选择省份",
          this.nowCity="请选择城市",
          this.nowName={name:"请选择小区",id:""};
          this.show('ceng_district');
        }
      },
      goRouter:function(str){
        localStorage.removeItem("exclude1")
        this.$router.push({path:str})
      },
      show:function(str){
          $("."+str).css("display","block")
      },
      close:function(str){
        $("."+str).css("display","none")
      },
      emptyClick:function(event){
        event.preventDefault();
        event.stopPropagation();
      },
      setScrollList:function(){
        var that=this;
        $("#education_list").mobiscroll().treelist({
          theme: "",  
          lang: "zh",  
          display: 'bottom', 
          // wheels:[], 设置此属性可以只显示年月，此处演示，就用下面的onBeforeShow方法,另外也可以用treelist去实现  
          //onBeforeShow: function (inst) { inst.settings.wheels[0].length>2?inst.settings.wheels[0].pop():null; }, //弹掉“日”滚轮
          headerText: function (valueText) { //自定义弹出框头部格式

          },  
          onSelect: function (valueText, inst) {

            that.setNowTypeName(that.showList[valueText])
          }  
        });
        $("input[id^=education_list_dummy]").focus();
      },
      setNowTypeName:function(str){
        switch(this.nowType){
          case "province":
            this.nowProvince=str.name;
            break;
          case "city":
            this.nowCity=str.name;
            break;
          case "name":
            this.floor="请选择";
            this.quyu="请选择";
            this.carParkNumberPrefix=JSON.parse(str.carParkNumberPrefix)
            this.nowName=str;
            this.close("ceng_district");
            break;
        }
        this.showList=[];
      },
      getProvince:function(){
        //获取省份列表
        var that=this;
        $.ajax({
          url:urlDir+"/wx/community/province/list",
          type:"GET",
          success:function(res){
            if(res.code==0){
              that.nowType="province"
              that.showList=[];
                res.data.provinceNameList.forEach(function(v,i){
                  that.showList.push({name:v})
                })
                setTimeout(function(){
                  that.setScrollList()
                },100)
            }
          },
          error:function(res){

          }
        })
      },
      getCity:function(){
        //获取城市列表
        var that=this;
        if(this.nowProvince!="请选择省份"){
          $.ajax({
            url:urlDir+"/wx/community/city/list",
            type:"GET",
            data:{
              provinceName:this.nowProvince
            },
            success:function(res){
              if(res.code==0){
                that.nowType="city"
                that.showList=[];
                res.data.cityNameList.forEach(function(v,i){
                  that.showList.push({name:v})
                })
                setTimeout(function(){
                  that.setScrollList()
                },100)
              }
            },
            error:function(res){

            }
          })
        }else{
          error_msg("请先选择省份",2000)
        }
      },
      getName:function(){
        //获取小区列表
        var that=this;
        if(this.nowCity!="请选择城市"){
          $.ajax({
            url:urlDir+"/wx/community/listByCityName",
            type:"GET",
            data:{
              cityName:this.nowCity
            },
            success:function(res){
              if(res.code==0){
                that.nowType="name"
                that.showList=res.data;
                setTimeout(function(){
                  that.setScrollList()
                },100)
              }
            },
            error:function(res){

            }
          })
        }else{
          error_msg("请先选择城市",2000)
        }
      }
    }
  }
</script>
<style scoped>
.park_status{width:100%;height:100%;position:relative;overflow:hidden;background:#f7f3f1;overflow-y:auto;}
.district_info{width:7rem;margin:.42rem auto;box-shadow:0 0 .1rem .01rem #dae7ed;background:#fff;padding:.15rem .3rem 0;border-radius:.16rem;}
.district_info .word .number{text-align:right;}
.district_info .word{height:.9rem;line-height:.9rem;}
.district_info .word img{width:.51rem;height:.51rem;display:inline-block;vertical-align:middle;}
.district_info .word b{display:inline-block;width:1.68rem;height:.48rem;line-height:.48rem;text-align:center;border-radius:.24rem;background:#0098db;}
.district_info .word b.cant{background:#f0f0f0;}
.district_info .word input{display:inline-block;width:2.7rem;text-align:right;}
.district_info .word .duan input{width:2rem;}
.district_info .chose_district{background:url("../assets/images/icon10.png") no-repeat right;background-size:.24rem .24rem;padding-right:.4rem;}
.checkout{background:#0098db;width:3.7rem;height:.88rem;line-height:.88rem;text-align:center;border-radius:.44rem;margin:1rem auto 0;}
/*弹出层*/
.ceng{width:100%;height:100%;position:absolute;top:0;left:0;background:rgba(255,255,255,.5);}
.ceng .ceng_content{width:5.9rem;background:#fff;border-radius:.24rem;padding:0 .46rem;box-shadow:0 0 1rem 0 #cfebf8;}
/*选择小区*/
.ceng_district{padding:.12rem 0 .6rem;}
.ceng_district .title{height:1.14rem;line-height:1.14rem;text-align:center;}
.ceng_district .word{height:.96rem;line-height:.96rem;}
.ceng_district .word span.r{background:url("../assets/images/sanjiao.png") no-repeat right;background-size:.16rem .23rem;padding-right:.44rem;}
/*列表显示*/
.show_list{width:100%;height:100%;overflow:hidden;position:absolute;top:0;left:0;z-index:20;background:rgba(0,0,0,.5);}
.show_list .content{max-height:5rem;overflow-y:auto;width:100%;position:absolute;bottom:0;background:#fff;}
.show_list .content p{line-height:.5rem;text-align:center;}
/*按钮*/
.towbutton{height:1rem;line-height:1rem;text-align:center;margin:1rem auto 0;}
.towbutton span{display:inline-block;width:2.64rem;height:.88rem;line-height:.88rem;border-radius:.44rem;background:#0098db;margin-right:.62rem;}
.towbutton span:last-child{margin:0;}
/*发布过的时间段*/
.time_list{width:7rem;margin:.5rem auto;}
.time_list .time_list_title{height:.6rem;line-height:.6rem;}
.time_lists{background:#fff;margin:.1rem 0;border-radius:.16rem;box-shadow:0 0 .1rem .01rem #dae7ed;position:relative;}
.time_lists div.info{overflow:hidden;line-height:.5rem;height:.5rem;padding:0 .2rem;}
.time_lists .top span.l{margin-right:1.2rem;}
.time_lists .btn{height:.8rem;line-height:.4rem;padding:0 .2rem;right:.2rem;width:1rem;text-align:center;background:#0098db;border-radius:.16rem;}
/*核对*/
.ceng_check .ceng_content{background:#fff;padding-top:.2rem;}
.ceng_check .title{height:1.26rem;line-height:1.26rem;text-align:center;margin-top:.16rem;}
.ceng_check .ceng_operating{height:1.76rem;line-height:1.76rem;text-align:center;}
.ceng_check .ceng_operating span{display:inline-block;width:1.9rem;height:.8rem;line-height:.8rem;background:#0098db;border-radius:.4rem;margin-right:.38rem;}
.ceng_check .ceng_operating span:last-child{margin:0;}
.ceng_check .word{height:.54rem;line-height:.54rem;}
.ceng_check .word span.left{display:inline-block;width:1.6rem;text-align:right;}
.ceng_check .noxianz{height:auto;}
/*删除*/
.ceng_del .word{text-align:center;}
/**/
.bod{height:100%;width:100%;overflow-y:auto;}
.park_now_status{position:absolute;top:0;right:.2rem;line-height:.5rem;color:#0098db;}
input{background:#fff;}
</style>
