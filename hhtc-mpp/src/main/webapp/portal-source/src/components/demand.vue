<template>
  <div class="demand">
    <div class="top font24 fcg48"><i></i>{{fansInfo.carOwnerCommunityName||fansInfo.carParkCommunityName}}</div>
    <div class="content">
      <div class="tab font32"><span :class="timeType==1?'curr':''" @click="changeTimeType(1)">白天</span><span :class="timeType==2?'curr':''" @click="changeTimeType(2)">夜间</span><span :class="timeType==3?'curr':''" @click="changeTimeType(3)">全天</span></div>
      <div class="time_chose fcg43 font32"><!--@click="show('ceng_district')"-->
        <div><span class="l">小区名称:</span><span class="r font28">{{nowName.name}}</span></div>
        <div><span class="l">车牌号:</span><span class="r font28" @click="goRouter('/license_plate')">{{carNumber}}</span></div>
        <div><span class="l">停车日期:</span><span class="r font28"><i id="birthday" :class="choseBeginTime=='请选择停车日期'?'fcg95':''">{{choseBeginTime}}</i></span></div>
        <!--<div><span class="l">结束时间:</span><span class="r"><input type="text" name="" id="birthday1" readonly placeholder="请选择结束时间"></span></div>
-->        
      <div class="last"><span class="l">时段:</span><span class="r font28" id="birthday3">{{endTime}}</span><span class="r font28" id="birthday2">{{beginTime}}</span></div>
      </div>
      <!--<div class="time_chose">
        <div class="repeat_top font32 fcg43"><em>按星期排除:</em></div>
        <div class="repeat font32 fcg43"><span v-for="(value,index) in week" :class="weekCheckout.indexOf(index)!=-1?'curr':''" @click="changeWeekCheckout(index)">{{value}}</span></div>
        <div class="repeat_bottom font32 fcg43"><em>按日期排除:</em><div class="repeat_top r font32 fcg43" @click="goExclude">{{excludeList.length==0?'请选择排除日期':excludeList.join(';')}}</div></div>
      </div>-->
      <div class="checkout fcw font34" @click="publish">立即发布</div>
      <div class="list font34 fcg95" @click="goRouter('/my_demand')"><img class="centered_y" src="../assets/images/user_icon_1.png">查看我的需求</div>
    </div>
    <!--选择小区-->
    <div class="ceng ceng_district fcg43 font30" @click="close('ceng_district')" style="display:none;">
      <div class="ceng_content centered" @click="emptyClick($event)">
        <div class="title font38">选择小区</div>
        <div class="word"><span class="l">省:</span><span class="r" @click="getProvince">{{nowProvince}}</span></div>
        <div class="word"><span class="l">市:</span><span class="r" @click="getCity">{{nowCity}}</span></div>
        <div class="word"><span class="l">小区名称:</span><span class="r" @click="getName">{{nowName.name}}</span></div>
      </div>
    </div>
    <!--信息核对-->
    <div class="ceng ceng_check ceng_s fcg95 font26" style="display:none;" @click="close('ceng_check')">
      <div class="ceng_content centered" @click="emptyClick($event)">
        <div class="title fcb font38">发布需求信息核对</div>
        <p class="word fcg43 font22"><span class="left">停车模式：</span><span class="fcb">{{publishInfo.publishTypeWord}}</span></p>
        <p class="word fcg43 font22"><span class="left">停车时间：</span><span class="fcb">{{publishInfo.dayWord}}</span></p>
        <p class="word fcg43 font22"><span class="left">停车时段：</span><span class="fcb">{{publishInfo.timeWord}}</span></p>
        <!--<p class="word fcg43 font22"><span class="left">排除：</span><span class="fcb exclude_word">{{publishInfo.outWord}}</span></p>-->
        <div class="ceng_operating fcw font34"><span @click="close('ceng_check')">取消</span><span @click="suer">确定</span></div>
      </div>
    </div>
    <!--钱不够去充值或者微信支付-->
    <div class="ceng nomoney ceng_s fcg95 font26" style="display:none;" @click="close('nomoney')">
      <div class="ceng_content centered" @click="emptyClick($event)">
        <div class="title fcb font38">余额不足</div>
        <div class="word font26" style="text-align:center;">本次还需支付{{fundsIsenough.moneyFull*1||0}}元</div>
        <div class="ceng_operating fcw font34"><span @click="goRouter('/recharge?type=13&goodsId='+goodsId+'&moneyBase='+fundsIsenough.moneyBase+'&moneyRent='+fundsIsenough.moneyRent)">去充值</span></div><!--<span @click="suer_wx">微信支付</span>-->
      </div>
    </div>
    <!--发布成功-->
    <div class="ceng ceng_result fcg95 font26" style="display:none;" @click="close('ceng_result')">
      <div class="ceng_content centered" @click="emptyClick($event)">
        <div class="title fcb font38">发布成功</div>
        <div class="word font24">系统正在马不停蹄的为您匹配</div>
        <div class="ceng_operating fcw font34" @click="goRouter('/park')">返回</div>
      </div>
    </div>
    <ul id="education_list" style="display: none">
      <li v-for="(time,index) in timeList">
        <div :data-value="time">{{time}}</div>
        <ul>
          <li data-value="00">00</li>
          <li data-value="30" v-if="timeType!=3&&index<timeList.length-1">30</li>
        </ul>
      </li>
    </ul>
    <ul id="education_list2" style="display: none">
      <li v-for="(year,key_y,index) in dayList">
        <div class="year" :data="key_y">{{key_y}}</div>
        <ul>
          <li v-for="(month,key_m,index2) in year">
            <div class="month" :data="key_m>=10?key_m:('0'+key_m)">{{key_m>=10?key_m:('0'+key_m)}}</div>
            <ul>
              <li class="day" :data="day" v-for="day in month">{{day}}</li>
            </ul>
          </li>
        </ul>
      </li>
    </ul>
    <ul id="education_list3" style="display: none">
      <li v-for="value in showList" :data="value">{{value.name}}</li>
    </ul>
  </div>
</template>

<script>
module.exports={
	devtool:'cheap-module-source-map',
    data:function(){
      return {
        fansInfo:{},
        inventory:{},
        timeList:["09","10","11","12","13","14","15","16","17"],
        allList:["00","01","02","03","04","05","06","07","08","09","10","11","12","13","14","15","16","17","18","19","20","21","22","23"],
        dayList:{},
        timeType:1,
        beginTime:"09:00",
        endTime:"17:00",
        weekCheckout:[0,6],
        week:["日","一","二","三","四","五","六"],
        publishInfo:{
          publishTypeWord:"白天",
          dayWord:"",
          timeWord:"",
          outWord:"无"
        },
        excludeList:[],
        goodsId:"",
        nowProvince:"请选择省份",
        nowCity:"请选择城市",
        nowName:{"name":""},
        showList:[],
        effectiveWord:[],
        payType:2,
        fundsIsenough:{},
        daynight:{},
        carNumber:"",
        choseBeginTime:"请选择停车日期",
        isAjaxEnd:true
      }
    },
    mounted:function(){
      var that=this;
      this.beginTime=this.daynight.timeDay+":00";
      this.endTime=this.daynight.timeNight+":00";
      this.setDayList();
      
    },
    activated:function(){
      //每次进入
      this.isAjaxEnd=true;
      this.close("ceng")
      this.getFansInfo();
      this.payType=2;
      this.choseBeginTime="请选择停车日期";
      if(localStorage.getItem("exclude2")){
        this.excludeList=toParse(localStorage.getItem("exclude1"));
      }
      this.scrollInit()
    },
    created:function(){
      // 组件创建完
      this.daynight=toParse(localStorage.getItem("daynight")||{"timeDay":"09","timeNight":"17"});
    },
    watch:{
      //监视变化
    },
    methods:{
      scrollInit:function(){
        var that=this
        setTimeout(function(){
          var $this="";
          var setMobiscroll=function(){
            $("#education_list2").mobiscroll().treelist({
                theme: "",  
                lang: "zh",  
                display: 'bottom',
                // wheels:[], 设置此属性可以只显示年月，此处演示，就用下面的onBeforeShow方法,另外也可以用treelist去实现  
                //onBeforeShow: function (inst) { inst.settings.wheels[0].length>2?inst.settings.wheels[0].pop():null; }, //弹掉“日”滚轮
                headerText: function (valueText) { //自定义弹出框头部格式
                  console.log(valueText)
                  var arr=valueText.split(" ");
                  return $("#education_list2").find(".year").eq(arr[0]).text()+"年"+$("#education_list2").find(".year").eq(arr[0]).siblings("ul").find(".month").eq(arr[1]).text()+"月"+$("#education_list2").find(".year").eq(arr[0]).siblings("ul").find(".month").eq(arr[1]).siblings("ul").find(".day").eq(arr[2]).text()+"日"
                  //return that.timeList[arr[0]]+":00";
                },  
                onSelect: function (valueText, inst) {
                  var arr=valueText.split(" ");
                  that.choseBeginTime=$("#education_list2").find(".year").eq(arr[0]).text()+"-"+$("#education_list2").find(".year").eq(arr[0]).siblings("ul").find(".month").eq(arr[1]).text()+"-"+$("#education_list2").find(".year").eq(arr[0]).siblings("ul").find(".month").eq(arr[1]).siblings("ul").find(".day").eq(arr[2]).text()
                }  
              });
              $("input[id^=education_list2_dummy]").focus();
          }
          $("#birthday,#birthday1").on("click",function(){
            $this=this;
            if($(this).attr("id")=="birthday1"){
              var minTime=$("#birthday").text();
              if(minTime=="请选择停车日期"){
                that.setDayList(setMobiscroll,new Date(minTime.replace(/-/g,"/")).getTime());
              }else{
                error_msg("请选择停车日期",2000)
                return true;
              }
            }
            if($(this).attr("id")=="birthday"){
              that.setDayList(setMobiscroll);
            }
          })
          $("#birthday2,#birthday3").on("click",function(){
            var $this=this;
            if($(this).attr("id")=="birthday3"){
              if(that.timeType!=3){
                var minTime=that.beginTime.split(":")[0];
                var arr=[];
                for(var i=that.timeList.length-1;i>=0;i--){
                  if(that.timeType==1){
                    if(that.timeList[i]==that.daynight.timeNight-1&&i==that.timeList.length-1){
                      arr.unshift(that.daynight.timeNight)
                      if(that.timeList[i]>minTime){
                        arr.unshift(that.timeList[i])
                      }
                    }else if(that.timeList[i]>minTime){
                      arr.unshift(that.timeList[i])
                    }
                  }else{
                    if(that.timeList[i]==that.daynight.timeDay-1&&arr.indexOf(that.daynight.timeDay)==-1){
                      arr.unshift(that.daynight.timeDay)
                    }
                    if(minTime<=that.daynight.timeDay){
                      if(that.timeList[i]>minTime&&that.timeList[i]<=that.daynight.timeDay)
                        arr.unshift(that.timeList[i])
                    }else{
                      if(that.timeList[i]>minTime||that.timeList[i]<=that.daynight.timeDay)
                        arr.unshift(that.timeList[i])
                    }
                  }
                }
                that.timeList=arr;
              }else{
                that.timeList=[that.daynight.timeDay,that.daynight.timeNight]
              }
              console.log(that.timeList)
            }
            if($(this).attr("id")=="birthday2"){
              if(that.timeType==1){
                that.timeList=that.allList.slice(that.allList.indexOf(that.daynight.timeDay),that.allList.indexOf(that.daynight.timeNight));
              }else if(that.timeType==2){
                that.timeList=that.allList.slice(0,that.allList.indexOf(that.daynight.timeDay)).concat(that.allList.slice(that.allList.indexOf(that.daynight.timeNight)));
              }else if(that.timeType==3){
                that.timeList=[that.daynight.timeDay,that.daynight.timeNight]
              }
            }
            setTimeout(function(){
              var defaultValue=that.timeType==2?that.timeList.indexOf(that.daynight.timeNight):that.timeList.indexOf(that.daynight.timeDay);
              if(defaultValue==-1){
                var bt=that.beginTime.split(":")[0]*1+1;
                defaultValue=that.timeList.indexOf(bt>=10?(bt+""):("0"+bt));
              }
              defaultValue=defaultValue==-1?0:defaultValue;
              $("#education_list").mobiscroll().treelist({
                theme: "",  
                lang: "zh",  
                display: 'bottom',
                defaultValue:[defaultValue,0],
                // wheels:[], 设置此属性可以只显示年月，此处演示，就用下面的onBeforeShow方法,另外也可以用treelist去实现  
                //onBeforeShow: function (inst) { inst.settings.wheels[0].length>2?inst.settings.wheels[0].pop():null; }, //弹掉“日”滚轮
                headerText: function (valueText) { //自定义弹出框头部格式
                  console.log(valueText)
                  var arr=valueText.split(" ");
                  return (that.timeList[arr[0]]<=that.daynight.timeDay?(($($this).attr("id")=="birthday3"&&that.beginTime.split(":")[0]>that.daynight.timeDay?"次日 ":"")+that.timeList[arr[0]]):that.timeList[arr[0]])+(arr[1]==0?":00":":30");
                },  
                onSelect: function (valueText, inst) {
                  var arr=valueText.split(" ");
                  if(that.timeType!=3){
                    if($($this).attr("id")=="birthday2"){
                      that.beginTime=that.timeList[arr[0]]+(arr[1]==0?":00":":30")
                      if(that.timeType==1){
                        that.endTime=that.daynight.timeNight+":00";
                      }else if(that.timeType==2){
                        that.endTime=that.daynight.timeDay+":00";
                      }else if(that.timeType==3){
                        that.endTime=that.daynight.timeDay+":00";
                      }
                    }else{
                      that.endTime=that.timeList[arr[0]]+(arr[1]==0?":00":":30")
                    }
                  }else{
                    that.beginTime=that.timeList[arr[0]]+":00";
                    that.endTime=that.timeList[arr[0]]+":00";
                  }
                  
                }  
              });
              $("input[id^=education_list_dummy]").focus();
            },100)
          })
        },100)
      },
      getFansInfo:function(){
        //获取用户信息
        var that=this;
        $.ajax({
          url:urlDir+"/wx/fans/get",
          type:"GET",
          success:function(res){
            if(res.code==0){
              that.fansInfo=res.data;
              that.carNumber=localStorage.getItem("carNumber")||res.data.carNumber.split("`")[0]
              that.nowName={
                name:res.data.carOwnerCommunityName,
                id:res.data.carOwnerCommunityId
              };
			        localStorage.setItem("fansInfo",toJson(res.data))
            }
          },
          error:function(res){

          }
        })
      },
      changeWeekCheckout:function(index){
        if(this.weekCheckout.indexOf(index)==-1){
          this.weekCheckout.push(index);
          return true;
        }
        var that=this;
        this.weekCheckout.some(function(v,i){
          if(v==index){
            that.weekCheckout.splice(i,1);
            return true;
          }
        })
      },
      setDayList:function(fun,minTime){
        console.log(minTime)
        var that=this,month="",day="";
        var date=new Date();
        var time=date.getTime();
        this.dayList={};
        for(var i=0;i<90;i++){
          if(!minTime||minTime<=time+i*24*3600000){
            date.setTime(time+i*24*3600000);
            month=(date.getMonth()+1);
            day=date.getDate()>=10?(date.getDate()+""):("0"+date.getDate());
            if(this.dayList[date.getFullYear()]){
              if(this.dayList[date.getFullYear()][month]){
                this.dayList[date.getFullYear()][month].push(day)
              }else{
                this.dayList[date.getFullYear()][month]=[day];
              }
            }else{
              this.dayList[date.getFullYear()]={};
              this.dayList[date.getFullYear()][month]=[day];
            }
          }
        }
        setTimeout(function(){
          if(fun){
            fun()
          }
        },100)
      },
      exclude:function(beginTime,endTime){
        var that=this,excludeWord=[],effectiveWord=[];
        var date=new Date(),dateWord="";
        for(var time=beginTime;time<=endTime;time+=24*3600000){
            date.setTime(time);
            dateWord=getTimes(time,"YMD",false);
            console.log(dateWord)
            if(that.weekCheckout.indexOf(date.getDay())==-1){
              if(that.excludeList.indexOf(dateWord)==-1){
                effectiveWord.push(dateWord)//有效时间
              }else{
                excludeWord.push(dateWord)
              }
            }else{
              excludeWord.push(dateWord)//排除时间
            }
        }
        for(var i=this.excludeList.length-1;i>=0;i--){
          if(excludeWord.indexOf(this.excludeList[i])==-1){
            this.excludeList.splice(i,1);
            error_msg("已为您自动去除超出时间段的排除时间",2000)
          }
        }
        localStorage.setItem("exclude1",toJson(this.excludeList));
        return {
          effectiveWord:effectiveWord,
          excludeWord:excludeWord
        }
      },
      changeTimeType:function(index){
        this.timeType=index;
        if(this.timeType==1){
          this.timeList=this.allList.slice(this.allList.indexOf(this.daynight.timeDay)+1,this.allList.indexOf(this.daynight.timeNight)+1);
          this.beginTime=this.daynight.timeDay+":00";
          this.endTime=this.daynight.timeNight+":00";
        }else if(this.timeType==2){
          this.timeList=this.allList.slice(0,this.allList.indexOf(this.daynight.timeDay)+1).concat(this.allList.slice(this.allList.indexOf(this.daynight.timeNight)));
          this.beginTime=this.daynight.timeNight+":00";
          this.endTime=this.daynight.timeDay+":00";
        }else if(this.timeType==3){
          this.timeList=[this.daynight.timeDay,this.daynight.timeNight]
          this.beginTime=this.daynight.timeDay+":00";
          this.endTime=this.daynight.timeDay+":00";
        }
      },
      goExclude:function(){
        var beginDay=$("#birthday").text(),endDay=$("#birthday1").val();
        if(beginDay=="请选择停车日期"){
          error_msg("请选择停车日期",2000);
          return true;
        }
        this.goRouter("/exclude?beginDay="+beginDay+"&endDay="+endDay+"&weekCheckout="+this.weekCheckout.join(",")+"&type=2");
      },
      goRouter:function(str){
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
      setNowTypeName:function(str){
        switch(this.nowType){
          case "province":
            this.nowProvince=str.name;
            break;
          case "city":
            this.nowCity=str.name;
            break;
          case "name":
            this.nowName=str;
            break;
        }
        this.showList=[];
      },
      setScrollList:function(){
        var that=this;
        $("#education_list3").mobiscroll().treelist({
          theme: "",  
          lang: "zh",  
          display: 'bottom', 
          // wheels:[], 设置此属性可以只显示年月，此处演示，就用下面的onBeforeShow方法,另外也可以用treelist去实现  
          //onBeforeShow: function (inst) { inst.settings.wheels[0].length>2?inst.settings.wheels[0].pop():null; }, //弹掉“日”滚轮
          headerText: function (valueText) { //自定义弹出框头部格式
            console.log(valueText)
          },  
          onSelect: function (valueText, inst) {
            console.log(valueText, inst)
            that.setNowTypeName(that.showList[valueText])
          }  
        });
        $("input[id^=education_list3_dummy]").focus();
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
      },
      publish:function(){
        //发布
        var publishTypeWord="";
        switch(this.timeType){
          case 1:
            publishTypeWord="白天";
            break;
          case 2:
            publishTypeWord="夜间";
            break;
          case 3:
            publishTypeWord="全天";
            break;
        }
        var beginDay=$("#birthday").text(),endDay=$("#birthday1").val();
        if(!beginDay||beginDay=="请选择停车日期"){
          error_msg("请选择停车日期",2000);
          return true;
        }
        /*var endTime=new Date(endDay.replace(/-/g,"/")).getTime();
        var beginTime=new Date(beginDay.replace(/-/g,"/")).getTime();
        var nowTimeList=this.exclude(beginTime,endTime);
        if(nowTimeList.effectiveWord.length==0){
          error_msg("所有时间都被排除了",2000);
          return true;
        }*/
        //this.effectiveWord=nowTimeList.effectiveWord;
        this.publishInfo={
          publishTypeWord:publishTypeWord,
          dayWord:beginDay,
          timeWord:this.beginTime+" - "+(this.beginTime.split(":")[0]>=this.daynight.timeDay&&this.endTime.split(":")[0]<=this.daynight.timeDay||this.beginTime.split(":")[0]==this.endTime.split(":")[0]?("次日 "+this.endTime):this.endTime),
          outWord:"无"
        }
        this.show("ceng_check");
      },
      suer_wx:function(){
        //微信支付
        this.payType=1;
        this.suer();
      },
      suer:function(){
        //确认发布
        var beginDay=$("#birthday").text(),endDay=beginDay.replace(/-/g,"/");
        if(this.timeType==2&&this.beginTime.split(":")[0]*1>this.daynight.timeDay&&this.endTime.split(":")[0]*1<=this.daynight.timeDay||this.timeType==3){
          var date=new Date(endDay);
          endDay=getTimes(date.getTime()+24*3600000,"YMD",false).replace(/-/g,"");
          console.log(endDay)
        }
        var that=this;
        if(!this.isAjaxEnd){
          return false;
        }
        this.isAjaxEnd=false;
        $.ajax({
          url:urlDir+"/wx/goods/need/add",
          type:"POST",
          data:{
            needType:this.timeType,
            needFromTime:this.beginTime.replace(":","")*1,
            needEndTime:this.endTime.replace(":","")*1,
            needFromDate:$("#birthday").text().replace(/-/g,""),
            needEndDate:endDay.replace(/\//g,""),
            payType:this.payType
          },
          success:function(res){
            that.isAjaxEnd=true;
            if(res.code==0){
              error_msg(res.msg,2000)
              if(that.payType==1){
                that.pay(res.data)
              }else{
                localStorage.removeItem("carNumber")
                that.close("ceng_check");
                that.show("ceng_result");
              }
            }else if(res.code==3009){
              that.fundsIsenough=res.data
              that.close("ceng_check");
              that.show("nomoney");
            }else{
              error_msg(res.msg,2000)
            }
          },
          error:function(res){

          }
        })
        
      },
      pay:function(obj){
        var that=this;
        wx.config({
          debug:false,
          appId:obj.appId,
          timestamp:obj.timeStamp+"",
          nonceStr:obj.nonceStr,
          signature:obj.paySign,
          jsApiList: [
              'chooseWXPay'
            ]
        });
        wx.ready(function(){
          if (typeof WeixinJSBridge == "undefined"){
              if( document.addEventListener ){
                 document.addEventListener('WeixinJSBridgeReady', onBridgeReady, false);
              }else if (document.attachEvent){
                 document.attachEvent('WeixinJSBridgeReady', onBridgeReady); 
                 document.attachEvent('onWeixinJSBridgeReady', onBridgeReady);
              }
          }else{
             onBridgeReady();
          }
          function onBridgeReady(){
            //alert('appId:'+resp.appId+'timeStamp'+resp.timeStamp+"nonceStr"+);
            WeixinJSBridge.invoke(
              'getBrandWCPayRequest',{
              "appId":obj.appId,     //公众号名称，由商户传入     
              "timeStamp":obj.timeStamp+"",         //时间戳，自1970年以来的秒数     
              "nonceStr":obj.nonceStr, //随机串     
              "package":obj.package_,
              "signType":obj.signType,    //微信签名方式：     
              "paySign":obj.paySign //微信签名 
              },
              function(res){
                  if(res.err_msg=="get_brand_wcpay_request:ok"){
                    localStorage.removeItem("carNumber")
                    that.close("nomoney");
                    that.show("ceng_result");
                  }     // 使用以上方式判断前端返回,微信团队郑重提示：res.err_msg将在用户支付成功后返回    ok，但并不保证它绝对可靠。 
              }
            );
          }
        });
      },
    }
  }
</script>
<style scoped>
.demand{width:100%;height:100%;position:relative;padding-bottom:1rem;overflow:hidden;}
.demand .top{width:100%;height:.54rem;line-height:.54rem;text-align:center;background:#fff;position:absolute;top:0;z-index:2;box-shadow:0 0 .1rem .01rem #dae7ed;}
.demand .top i{display:inline-block;width:.24rem;height:.28rem;background:url("../assets/images/icon.png") no-repeat center;background-size:.24rem .28rem;vertical-align:middle;margin-right:.12rem;}
.demand .content{background:#e8e4e2;width:100%;height:100%;padding-top:.54rem;position:absolute;top:0;}
.content .tab{height:1.54rem;line-height:1.54rem;text-align:center;}
.content .tab span{display:inline-block;width:1.78rem;height:.86rem;line-height:.86rem;border:1px solid #0098db;color:#0098db;border-radius:.43rem;margin-right:.46rem;}
.content .tab span:last-child{margin-right:0;}
.content .tab span.curr{background:#0098db;color:#fff;}
.time_chose{width:7rem;border-radius:.16rem;box-shadow:0 0 .1rem .01rem #dae7ed;margin:.12rem auto .44rem;background:#fff;padding:.13rem .3rem;}
.time_chose div{height:.9rem;line-height:.9rem;overflow:hidden;}
.time_chose div input{width:3.4rem;text-align:right;background:#fff;}
.time_chose div span.r{background:url("../assets/images/sanjiao.png") no-repeat right;background-size:.16rem .23rem;padding-right:.44rem;}
.time_chose div span.r:nth-child(3){margin-right:.28rem;}
.repeat{height:1.46rem;line-height:1.46rem;text-align:center;}
.repeat span{display:inline-block; width:.8rem;height:.8rem;line-height:.8rem;text-align:center;margin-left:.08rem;border:1px solid #0098db;color:#0098db;border-radius:50%;}
.repeat span.curr{background:#0098db;color:#fff;}
.repeat_bottom{margin-top:.3rem;}
.repeat_bottom div.r{width:4.5rem;text-align:right;background:url("../assets/images/sanjiao.png") no-repeat right;background-size:.16rem .23rem;padding-right:.44rem;text-overflow:ellipsis;overflow:hidden;white-space:nowrap;}
.repeat_top{height:.8rem;line-height:.8rem;text-align:left;}
.checkout{width:3.7rem;height:.88rem;line-height:.88rem;text-align:center;background:#0098db;border-radius:.44rem;margin:.5rem auto;}
/*弹出层*/
.ceng{width:100%;height:100%;position:absolute;top:0;left:0;background:rgba(255,255,255,.5);}
.ceng .ceng_content{width:5.9rem;background:#fff;border-radius:.24rem;padding:0 .46rem;box-shadow:0 0 1rem 0 #cfebf8;}
/*选择小区*/
.ceng_district{padding:.12rem 0 .6rem;}
.ceng_district .title{height:1.14rem;line-height:1.14rem;text-align:center;}
.ceng_district .word{height:.96rem;line-height:.96rem;}
.ceng_district .word span.r{background:url("../assets/images/sanjiao.png") no-repeat right;background-size:.16rem .23rem;padding-right:.44rem;}
/*核对*/
.ceng_s .ceng_content{background:#fff url("../assets/images/check_icon.png") no-repeat center .4rem;background-size:2.2rem 2.2rem;padding-top:2.6rem;}
.ceng_s .title{height:1.26rem;line-height:1.26rem;text-align:center;margin-top:.16rem;}
.ceng_s .ceng_operating{height:1.76rem;line-height:1.76rem;text-align:center;}
.ceng_s .ceng_operating span{display:inline-block;width:1.9rem;height:.8rem;line-height:.8rem;background:#0098db;border-radius:.4rem;margin-right:.38rem;}
.ceng_s .ceng_operating span:last-child{margin:0;}
.ceng_s .word{line-height:.54rem;}
.ceng_s .word span.left{display:inline-block;width:1.6rem;text-align:right;}
.exclude_word{display:inline-block;width:3rem;text-overflow:ellipsis;white-space:nowrap;overflow:hidden;vertical-align:middle;}
/*余额不足*/
.nomoney .ceng_content{background:#fff;padding-top:0;}
/*发布成功*/
.ceng_result .ceng_content{background:#fff url("../assets/images/yes.png") no-repeat center .4rem;background-size:1.98rem 1.98rem;padding-top:2.38rem;}
.ceng_result .title{height:1.2rem;line-height:1.2rem;text-align:center;}
.ceng_result .word{height:.6rem;line-height:.6rem;text-align:center;}
.ceng_result .ceng_operating{width:3.7rem;height:.8rem;line-height:.8rem;background:#0098db;margin:1.7rem auto 1.8rem;border-radius:.4rem;text-align:center;}
.list{width:7.02rem;height:1.2rem;line-height:1.2rem;padding-left:1.2rem;border-radius:.16rem;box-shadow:0 0 .1rem .01rem #dae7ed;background:#fff url("../assets/images/icon9.png") no-repeat 6.4rem;background-size:.38rem .38rem; margin:.26rem auto 0;position:relative;}
.list img{height:.46rem;left:.4rem;}
</style>
