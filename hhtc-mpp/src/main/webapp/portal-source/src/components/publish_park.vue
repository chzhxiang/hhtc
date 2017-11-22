<template>
  <div class="publish_park">
    <div class="top fcw font46 centered_x">
      <p>本平台已成交{{inventory.dealCount}}个车位</p>
      <p>还有{{inventory.remainCount}}个空余车位</p>
    </div>
    <div class="content">
      <div class="tab font32"><span :class="timeType==1?'curr':''" @click="changeTimeType(1)">白天</span><span :class="timeType==2?'curr':''" @click="changeTimeType(2)">夜间</span><span :class="timeType==3?'curr':''" @click="changeTimeType(3)">全天</span></div>
      <div class="time_chose fcg43 font32">
        <div><span class="l">开始日期:</span><span class="r font28"><i id="birthday" :class="birthday=='请选择开始日期'?'fcgb1':''">{{birthday}}</i></span></div>
        <div><span class="l">结束日期:</span><span class="r font28"><i id="birthday1" :class="birthday1=='请选择结束日期'?'fcgb1':''">{{birthday1}}</i></span></div>
        <div class="last"><span class="l">时段:</span><span class="r font28" id="birthday3">{{endTime}}</span><span class="r font28" id="birthday2">{{beginTime}}</span></div>
      </div>
      <div class="time_chose">
        <div class="repeat_top font32 fcg43"><em>按星期排除:</em></div>
        <div class="repeat font32 fcg43"><span v-for="(value,index) in week" :class="weekCheckout.indexOf(index)!=-1?'curr':''" @click="changeWeekCheckout(index)">{{value}}</span></div>
        <div class="repeat_bottom font32 fcg43"><em>按日期排除:</em><div class="repeat_top r font32 fcg43" @click="goExclude">{{excludeList.length==0?'请选择排除日期':excludeList.join(';')}}</div></div>
      </div>
      <div class="checkout fcw font34" @click="publish">立即发布</div>
    </div>
    <!--发布成功-->
    <div class="ceng ceng_result fcg95 font26" style="display:none;" @click="close('ceng_result')">
      <div class="ceng_content centered" @click="emptyClick($event)">
        <div class="title fcb font38">发布成功</div>
        <div class="ceng_operating fcw font34" @click="goBack()">返回车位详情</div>
      </div>
    </div>
    <!--钱不够去充值或者微信支付-->
    <div class="ceng nomoney ceng_s fcg95 font26" style="display:none;" @click="close('nomoney')">
      <div class="ceng_content centered" @click="emptyClick($event)">
        <div class="title fcb font38">押金不足</div>
        <div class="word font26" style="text-align:center;">本次需支付押金{{fundsIsenough.moneyBase*1||0}}元</div>
        <div class="ceng_operating fcw font34"><span @click="goRouter('/recharge?type=11&goodsId='+goodsId+'&moneyBase='+fundsIsenough.moneyBase+'&moneyRent='+fundsIsenough.moneyRent)">去充值</span></div><!--<span @click="suer_wx">微信支付</span>-->
      </div>
    </div>
    <!--发布时可以合并-->
    <div class="ceng ceng_check detection fcg95 font26" style="display:none;" @click="close('detection')">
      <div class="ceng_content centered" @click="emptyClick($event)">
        <div class="title fcb font38">存在可以合并的发布信息</div>
        <p class="word fcg43 font22">您发布过与当前时间连续时间段，是否合并发布信息</p>
        <div class="ceng_operating fcw font34"><span @click="close('detection')">取消</span><span @click="normalOrder">确定</span></div>
      </div>
    </div>
    <!--信息核对-->
    <div class="ceng ceng_check ceng_check_w fcg95 font26" style="display:none;" @click="close('ceng_check_w')">
      <div class="ceng_content centered" @click="emptyClick($event)">
        <div class="title fcb font38">发布信息核对</div>
        <p class="word fcg43 font22"><span class="left">放租模式：</span><span class="fcb">{{publishInfo.publishTypeWord}}</span></p>
        <p class="word fcg43 font22"><span class="left">放租日期：</span><span class="fcb">{{publishInfo.dayWord}}</span></p>
        <p class="word fcg43 font22"><span class="left">发布时段：</span><span class="fcb">{{publishInfo.timeWord}}</span></p>
        <p class="word fcg43 font22"><span class="left">排除：</span><span class="fcb exclude_word">{{publishInfo.outWord}}</span></p>
        <div class="ceng_operating fcw font34"><span @click="close('ceng_check_w')">取消</span><span @click="suer">确定</span></div>
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
  </div>
</template>
<script>
module.exports={
	devtool:'cheap-module-source-map',
    data:function(){
      return {
        timeList:["09","10","11","12","13","14","15","16","17"],
        allList:["00","01","02","03","04","05","06","07","08","09","10","11","12","13","14","15","16","17","18","19","20","21","22","23"],
        dayList:{},
        timeType:1,
        beginTime:"09:00",
        endTime:"17:00",
        weekCheckout:[],
        week:["日","一","二","三","四","五","六"],
        publishInfo:{
          publishTypeWord:"白天",
          dayWord:"",
          timeWord:"",
          outWord:"无"
        },
        excludeList:[],
        effectiveWord:[],
        goodsId:"",
        inventory:{},
        isenough:{},
        daynight:{},
        payType:2,
        fundsIsenough:{},
        parkInfo:{},
        birthday:"请选择开始日期",
        birthday1:"请选择结束日期",
        isAjaxEnd:true
      }
    },
    mounted:function(){
      var that=this;
      this.parkInfo=toParse(localStorage.getItem("parkInfo"));
      this.setDayList(function(){});
      this.beginTime=this.daynight.timeDay+":00";
      this.endTime=this.daynight.timeNight+":00";
      this.scrollInit()
    },
    activated:function(){
      //每次进入
      this.isAjaxEnd=true;
      this.close("ceng");
      this.parkInfo=toParse(localStorage.getItem("parkInfo"))
      this.goodsId=getUrl().goodsId;
      this.getInventory();
      this.weekCheckout=[];
      this.payType=2;
      this.excludeList=[];
      if(localStorage.getItem("exclude1")){
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
        var that=this;
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
                  that[$($this).attr("id")]=$("#education_list2").find(".year").eq(arr[0]).text()+"-"+$("#education_list2").find(".year").eq(arr[0]).siblings("ul").find(".month").eq(arr[1]).text()+"-"+$("#education_list2").find(".year").eq(arr[0]).siblings("ul").find(".month").eq(arr[1]).siblings("ul").find(".day").eq(arr[2]).text();
                  if(that.birthday.replace(/-/g,"")>that.birthday1.replace(/-/g,"")){
                    that.birthday1="请选择结束时间"
                  }
                }  
              });
              $("input[id^=education_list2_dummy]").focus();
          }
          $("#birthday,#birthday1").off().on("click",function(){
            $this=this;
            if($(this).attr("id")=="birthday1"){
              var minTime=that.birthday;
              if(minTime!="请选择开始日期"){
                that.setDayList(setMobiscroll,new Date(minTime.replace(/-/g,"/")).getTime());
              }else{
                error_msg("请先选择开始日期",2000)
                return true;
              }
            }
            if($(this).attr("id")=="birthday"){
              that.setDayList(setMobiscroll);
            }
          })
          
          $("#birthday2,#birthday3").off().on("click",function(){
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
                  console.log(that.timeList[arr[0]]<=that.daynight.timeDay)
                  return (that.timeList[arr[0]]<=that.daynight.timeDay||that.timeType==3?(($($this).attr("id")=="birthday3"&&(that.beginTime.split(":")[0]>that.daynight.timeDay||that.timeType==3)?"次日 ":"")+that.timeList[arr[0]]):that.timeList[arr[0]])+(arr[1]==0?":00":":30");
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
              setTimeout(function(){
                $("input[id^=education_list_dummy]").focus();
              },50)
            },100)
          })
        },100)
      },
      goBack:function(){
        history.back();
      },
      getInventory:function(){
        //获取当前车位的情况
        var that=this;
        $.ajax({
          url:urlDir+"/wx/goods/publish/order/inventory",
          type:"GET",
          data:{
            communityId:0
          },
          success:function(res){
            if(res.code==0){
              that.inventory=res.data
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
            if(getTimes(time+i*24*3600000,"YMD",false).replace(/-/g,"")*1<=this.parkInfo.carUsefulEndDate*1){
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
            }else{
              break
            }
          }
        }
        setTimeout(function(){
          if(fun){
            fun()
          }
        },100)
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
        var beginDay=$("#birthday").text(),endDay=$("#birthday1").text();
        if(beginDay=="请选择开始日期"||endDay=="请选择结束日期"){
          error_msg("请选择开始日期和结束日期",2000);
          return true;
        }
        this.goRouter("/exclude?beginDay="+beginDay+"&endDay="+endDay+"&weekCheckout="+this.weekCheckout.join(",")+"&type=1");
      },
      goRouter:function(str){
        this.close("ceng")
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
        var beginDay=$("#birthday").text(),endDay=$("#birthday1").text();
        if(beginDay=="请选择开始日期"||endDay=="请选择结束日期"){
          error_msg("请选择开始日期和结束日期",2000);
          return true;
        }
        var endTime=new Date(endDay.replace(/-/g,"/")).getTime();
        var beginTime=new Date(beginDay.replace(/-/g,"/")).getTime();
        var nowTimeList=this.exclude(beginTime,endTime);
        if(nowTimeList.effectiveWord.length==0){
          error_msg("所有时间都被排除了",2000);
          return true;
        }
        this.effectiveWord=nowTimeList.effectiveWord;
        this.publishInfo={
          publishTypeWord:publishTypeWord,
          dayWord:conversion(beginDay,false)+" - "+conversion(endDay,false).split("年")[1],
          timeWord:this.beginTime+" - "+(this.endTime.split(":")[0]<this.daynight.timeDay?("次日 "+this.endTime):this.endTime),
          outWord:nowTimeList.excludeWord.length>0?nowTimeList.excludeWord.join(","):"无"
        }

        this.payType=2;
        this.show("ceng_check_w");
      },
      suer:function(){
        //确认发布
        var that=this;
        if(!this.isAjaxEnd){
          return false;
        }
        this.isAjaxEnd=false;
        $.ajax({
          url:urlDir+"/wx/goods/publish/add/canMerge",
          type:"POST",
          data:{
            goodsId:this.goodsId,
            publishType:this.timeType,
            publishFromTime:this.beginTime.replace(":","")*1,
            publishEndTime:this.endTime.replace(":","")*1,
            publishFromDates:this.effectiveWord.join(",").replace(/-/g,"").replace(/,/g,"-"),
          },
          success:function(res){
            that.isAjaxEnd=true;
            if(res.code==0){
              that.close("ceng_check_w");
              if(res.data){
                that.show("detection")
              }else{
                that.normalOrder();
              }
            }else{
              error_msg(res.msg,2000);
            }
          },
          error:function(res){

          }
        })
      },
      suer_wx:function(){
        //微信支付
        this.payType=1;
        this.normalOrder();
      },
      normalOrder:function(){
        var that=this;
        $.ajax({
          url:urlDir+"/wx/goods/publish/add",
          type:"POST",
          data:{
            goodsId:this.goodsId,
            publishType:this.timeType,
            publishFromTime:this.beginTime.replace(":","")*1,
            publishEndTime:this.endTime.replace(":","")*1,
            publishFromDates:this.effectiveWord.join(",").replace(/-/g,"").replace(/,/g,"-"),
            payType:this.payType
          },
          success:function(res){
            if(res.code==0){
              that.close("detection");
              if(that.payType==1){
                that.pay(res.data)
              }else{
                localStorage.removeItem("exclude1");
                that.close("ceng_check_w");
                that.show("ceng_result");
                that.parkInfo.isUsed=1;
                localStorage.setItem("parkInfo",toJson(that.parkInfo));
              }
            }else if(res.code==3009){
              that.close("detection");
              that.fundsIsenough=res.data
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
                    localStorage.removeItem("exclude1");
                    that.close('deposit');
                    that.close("nomoney")
                    that.show("ceng_result");
                  }     // 使用以上方式判断前端返回,微信团队郑重提示：res.err_msg将在用户支付成功后返回    ok，但并不保证它绝对可靠。 
              }
            );
          }
        });
      }
    }
  }
</script>
<style scoped>
.publish_park{width:100%;height:100%;position:relative;padding-bottom:1rem;overflow:hidden;}
.publish_park .top{height:2.04rem;width:7rem;border-bottom-right-radius:.16rem;border-bottom-left-radius:.16rem;background:#0098db;line-height:.64rem;padding:.3rem 0 0 .3rem;position:absolute;top:0;z-index:2;}
.publish_park .content{background:#e8e4e2;width:100%;height:100%;padding-top:2.02rem;position:absolute;top:0;overflow-y:auto;padding-bottom:1rem;}
.content .tab{height:1.54rem;line-height:1.54rem;text-align:center;}
.content .tab span{display:inline-block;width:1.78rem;height:.86rem;line-height:.86rem;border:1px solid #0098db;color:#0098db;border-radius:.43rem;margin-right:.46rem;}
.content .tab span:last-child{margin-right:0;}
.content .tab span.curr{background:#0098db;color:#fff;}
.time_chose{width:7rem;border-radius:.16rem;box-shadow:0 0 .1rem .01rem #dae7ed;margin:.12rem auto .44rem;background:#fff;padding:.13rem .3rem;overflow:hidden;}
.time_chose div{height:.9rem;line-height:.9rem;overflow:hidden;}
.time_chose div input{width:3.4rem;text-align:right;background:#fff;}
.time_chose div.last input{width:1rem;}
.time_chose div span.r{background:url("../assets/images/sanjiao.png") no-repeat right;background-size:.16rem .23rem;padding-right:.44rem;}
.time_chose div span.r:nth-child(3){margin-right:.28rem;}
.repeat{height:1.46rem;line-height:1.46rem;text-align:center;}
.repeat span{display:inline-block; width:.8rem;height:.8rem;line-height:.8rem;text-align:center;margin-left:.08rem;border:1px solid #0098db;color:#0098db;border-radius:50%;}
.repeat span.curr{background:#0098db;color:#fff;}
.repeat_bottom{margin-top:.3rem;}
.repeat_bottom div.r{width:4.5rem;text-align:right;background:url("../assets/images/sanjiao.png") no-repeat right;background-size:.16rem .23rem;padding-right:.44rem;text-overflow:ellipsis;overflow:hidden;white-space:nowrap;}
.repeat_top{height:.8rem;line-height:.8rem;text-align:left;}
.checkout{width:3.7rem;height:.88rem;line-height:.88rem;text-align:center;background:#0098db;border-radius:.44rem;margin:.5rem auto;}
.exclude_word{display:inline-block;width:3rem;text-overflow:ellipsis;white-space:nowrap;overflow:hidden;vertical-align:middle;}
/*弹出层*/
.ceng{width:100%;height:100%;position:absolute;top:0;left:0;background:rgba(255,255,255,.5);}
.ceng .ceng_content{width:5.9rem;background:#fff;border-radius:.24rem;padding:0 .46rem;box-shadow:0 0 1rem 0 #cfebf8;}
/*发布成功*/
.ceng_result .ceng_content{background:#fff url("../assets/images/yes.png") no-repeat center .4rem;background-size:1.98rem 1.98rem;padding-top:2.38rem;}
.ceng_result .title{height:1.2rem;line-height:1.2rem;text-align:center;}
.ceng_result .word{height:.6rem;line-height:.6rem;text-align:center;}
.ceng_result .ceng_operating{width:3.7rem;height:.8rem;line-height:.8rem;background:#0098db;margin:1.7rem auto 1.8rem;border-radius:.4rem;text-align:center;}
/*核对*/
.ceng_check .ceng_content{background:#fff url("../assets/images/check_icon.png") no-repeat center .4rem;background-size:2.2rem 2.2rem;padding-top:2.6rem;}
.ceng_check .title{height:1.26rem;line-height:1.26rem;text-align:center;margin-top:.16rem;}
.ceng_check .ceng_operating{height:1.76rem;line-height:1.76rem;text-align:center;}
.ceng_check .ceng_operating span{display:inline-block;width:1.9rem;height:.8rem;line-height:.8rem;background:#0098db;border-radius:.4rem;margin-right:.38rem;}
.ceng_check .ceng_operating span:last-child{margin:0;}
.ceng_check .word{height:.54rem;line-height:.54rem;}
.ceng_check .word span.left{display:inline-block;width:1.6rem;text-align:right;}
/*押金*/
.deposit .ceng_content{background:#fff;padding-top:0;}
.deposit .word{text-align:center;}
/*核对*/
.ceng_s .ceng_content{background:#fff url("../assets/images/check_icon.png") no-repeat center .4rem;background-size:2.2rem 2.2rem;padding-top:2.6rem;}
.ceng_s .title{height:1.26rem;line-height:1.26rem;text-align:center;margin-top:.16rem;}
.ceng_s .ceng_operating{height:1.76rem;line-height:1.76rem;text-align:center;}
.ceng_s .ceng_operating span{display:inline-block;width:1.9rem;height:.8rem;line-height:.8rem;background:#0098db;border-radius:.4rem;margin-right:.38rem;}
.ceng_s .ceng_operating span:last-child{margin:0;}
.ceng_s .word{line-height:.54rem;}
.ceng_s .word span.left{display:inline-block;width:1.6rem;text-align:right;}
</style>
