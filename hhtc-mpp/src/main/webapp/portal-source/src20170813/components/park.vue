<template>
  <div class="park">
    <div class="top font24 fcg48" @click="show('ceng_district')"><i></i>{{nowName.name}}</div>
    <div class="content">
      <div class="banner">
        <div class="banner_left l"><img class="centered" src="../assets/images/logo.png"></div>
        <div class="banner_right l">
          <div class="banner_right_conent l">
            <p>截止目前已成交{{inventory.dealCount}}个车位</p>
            <p class="banner_yu"><a>{{inventory.remainCount}}</a>剩余车位</p>
          </div>
          <div class="banner_right_but l"><a @click="goDemand">告诉我您的需求</a></div>
        </div>
      </div>
      <div class="tab">
        <ul>
          <!--<li @click="choseParkType"><a>{{parkType==1?"单次停车":"周期停车"}}</a></li>-->
          <li @click="choseBeginDay"><a>{{ajaxParkInfo.beginDay?ajaxParkInfo.beginDay:"开始日期"}}</a></li>
          <li @click="choseEndDay"><a>{{ajaxParkInfo.endDay?ajaxParkInfo.endDay:"结束日期"}}</a></li>
          <li @click="choseBeginTime"><a>{{ajaxParkInfo.beginTime||"开始时段"}}</a></li>
          <li @click="choseEndTime"><a>{{ajaxParkInfo.endTime?(((ajaxParkInfo.endTime.replace(':','')*1<=ajaxParkInfo.beginTime.replace(':','')*1)?"次日":"")+ajaxParkInfo.endTime):"结束时段"}}</a></li>
          <!--<li @click="choseTpye"><a>{{ajaxParkInfo.type}}</a></li>-->
        </ul>
      </div>
      <div class="scroll" :style="{height:max_height}">
        <div class="park_list" v-for="park in parkList">
          <div class="list_content l">
            <p class="list_address">{{park.carParkNumber}}</p>
            <p class="list_day fcg95">时间段:</p>
            <p class="list_day fcb">{{parkGetTime(park.publishFromTime,park.publishEndTime)}}</p>
            <p class="list_day fcg95">起止日期:</p>
            <p class="list_day fcb">{{parkGetDay(park.publishFromDate)+"至"+parkGetDay(park.publishEndDate)}}</p>
            <div class="lsit_money">￥：<a>{{park.price*1}}</a></div>
          </div>
          <div class="list_right r">
            <div class="location centered_x" :style="{backgroundImage:'url('+getParkImg(park.carParkImg)+')'}" @click="showImage(getParkImg(park.carParkImg))"><i class="centered_x"></i><p>查看位置</p></div>
            <a class="list_but centered_x" @click="goDetail(park)">预 定</a>
          </div>
        </div>
      </div>
    </div>
    <ul id="education_list" style="display: none">
      <li v-for="(time,index) in timeList">
        <div :data-value="time">{{time}}</div>
        <ul>
          <li data-value="00">00</li>
          <li data-value="30">30</li>
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
    <!--选择小区-->
    <div class="ceng ceng_district fcg43 font30" @click="close('ceng_district')" style="display:none;">
      <div class="ceng_content centered" @click="emptyClick($event)">
        <div class="title font38">选择小区</div>
        <div class="word"><span class="l">省:</span><span class="r" @click="getProvince">{{nowProvince}}</span></div>
        <div class="word"><span class="l">市:</span><span class="r" @click="getCity">{{nowCity}}</span></div>
        <div class="word"><span class="l">小区名称:</span><span class="r" @click="getName">{{nowName.name}}</span></div>
      </div>
    </div>
    <ul id="education_list3" style="display: none">
      <li v-for="value in showList" :data="value">{{value.name}}</li>
    </ul>
    <ul id="education_list4" style="display: none">
      <li data="1">单次停车</li>
      <li data="2">周期停车</li>
    </ul>
    <!--<ul id="education_list3" style="display: none">
      <li v-for="value in showList" :data="value">{{value}}</li>
    </ul>-->
  </div>
</template>

<script>
module.exports={
	devtool:'cheap-module-source-map',
    data:function(){
      return {
        max_height:0,
        parkList:[],
        inventory:{},
        fansInfo:{},
        timeList:["00","01","02","03","04","05","06","07","08","09","10","11","12","13","14","15","16","17","18","19","20","21","22","23"],
        dayList:{},
        showList:[],
        ajaxParkInfo:{
          beginDay:"",
          endDay:"",
          beginTime:"",
          endTime:""
          /*type:"白天"*/
        },
        nowProvince:"请选择省份",
        nowCity:"请选择城市",
        nowName:{"name":"请选择小区","id":1},
        nowChoseDayType:"beginDay",
        count:0,
        parkType:1
      }
    },
    mounted:function(){
      var that=this;
      setScroll_event($(".scroll"),"noscroll");
      this.max_height=parseFloat($("html").css("height"))-(0.54+2.86+2)*dpi+"px";
      this.setDayList();
      $("#education_list4").mobiscroll().treelist({
          theme: "",  
          lang: "zh",  
          display: 'bottom', 
          // wheels:[], 设置此属性可以只显示年月，此处演示，就用下面的onBeforeShow方法,另外也可以用treelist去实现  
          //onBeforeShow: function (inst) { inst.settings.wheels[0].length>2?inst.settings.wheels[0].pop():null; }, //弹掉“日”滚轮
          headerText: function (valueText) { //自定义弹出框头部格式
            console.log(valueText)
          },  
          onSelect: function (valueText,inst) {
            that.parkType=valueText*1+1;
          }  
        });
      //设置日期
    },
    activated:function(){
      //每次进入
      this.close("ceng")
      this.getInventory();
      this.getFansInfo();
      this.getWx();
    },
    created:function(){
      // 组件创建完
    },
    watch:{
      //监视变化
      ajaxParkInfo:{
        deep:true,
        handler:function(val){
          if(this.ajaxParkInfo.beginDay&&this.ajaxParkInfo.endDay){
            this.parkList=[];
            this.getParkList();
          }
          
        }
      },
      nowName:{
        deep:true,
        handler:function(val) {
          this.parkList=[];
          this.getParkList();
        }
      },
      parkType:function(){
        this.parkList=[];
        this.getParkList();
      }
      //单个属性监视
      /*'ajaxParkInfo.type': function (val, oldVal) {
        console.log(val)
      }*/
    },
    methods:{
      choseParkType:function(){
        $("input[id^=education_list4_dummy]").focus();
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
            console.log(data)
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
      getParkImg:function(carParkImg){
        return carParkImg?('/wx/common/file/get?filePath='+carParkImg):'/static/img/list_bg.png'
      },
      showImage:function(url){
        wx.previewImage({
            current:location.protocol+"//"+location.host+url, // 当前显示图片的http链接
            urls:[location.protocol+"//"+location.host+url]// 需要预览的图片http链接列表
        });
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
      goDetail:function(obj){
        localStorage.setItem("parkDetail",toJson(obj))
        if(this.fansInfo.carOwnerStatus){
          if(this.fansInfo.carOwnerStatus==2){
            this.goRouter("/park_details?ids="+obj.ids)
          }else{
            error_msg("身份审核中,请稍后再试",2000)
          }
        }else{
          error_msg("请先注册成为车主",2000)
          this.goRouter("/login?userType=1");
        }
      },
      parkGetDay:function(index){
        //进来格式20170911
        return index.substr(0,4)+"-"+index.substr(4,2)+"-"+index.substr(6,2);
      },
      parkGetTime:function(begin,end){
        //进来格式 930   1700
        var beginTime=(Math.floor(begin/100)>=10?Math.floor(begin/100):("0"+Math.floor(begin/100)))+":"+(begin%100>=10?begin%100:("0"+begin%100));
        var endTime=(Math.floor(end/100)>=10?Math.floor(end/100):("0"+Math.floor(end/100)))+":"+(end%100>=10?end%100:("0"+end%100));
        return beginTime+"-"+((begin*1>=end*1)?("次日"+endTime):endTime);
      },
      getParkList:function(){
        var that=this;
        var arrBegin=this.ajaxParkInfo.beginDay.split(" "),arrEnd=this.ajaxParkInfo.endDay.split(" ");
        $.ajax({
          url:urlDir+"/wx/goods/publish/matchlist",
          type:"GET",
          data:{
            communityId:this.nowName.id,
            publishFromDate:arrBegin[0].replace(/-/g,""),
            publishEndDate:arrEnd[0].replace(/-/g,""),
            publishFromTime:arrBegin[1]?arrBegin[1].replace(":","")*1:"",
            publishEndTime:arrEnd[1]?arrEnd[1].replace(":","")*1:"",
            parkType:this.parkType
          },
          success:function(res){
            if(res.code==0){
              that.parkList=res.data;
            }
          },
          error:function(res){

          }
        })
      },
      choseBeginDay:function(){
        var that=this;
        this.nowChoseDayType="beginDay";
        
        this.setDayList();
        setTimeout(function(){
           that.setDayScroll()
        },100)

      },
      choseEndDay:function(){
        var that=this;
        if(this.ajaxParkInfo.beginDay){
          var begin=this.ajaxParkInfo.beginDay.replace(/-/g,"/");
          var date=new Date(begin);
          this.setDayList(date.getTime());
        }else{
          error_msg("请先选择开始日期",2000)
          return true;
        }
        this.nowChoseDayType="endDay"
        
        setTimeout(function(){
          that.setDayScroll()
        },100)
      },
      setDayScroll:function(){
        var that=this;
        $("#education_list2").mobiscroll().treelist({
          theme: "",  
          lang: "zh",  
          display: 'bottom',
          // wheels:[], 设置此属性可以只显示年月，此处演示，就用下面的onBeforeShow方法,另外也可以用treelist去实现  
          //onBeforeShow: function (inst) { inst.settings.wheels[0].length>2?inst.settings.wheels[0].pop():null; }, //弹掉“日”滚轮
          headerText: function (valueText) { //自定义弹出框头部格式
            console.log(valueText)
            var arr=valueText.split(" ");
            var begin=$("#education_list2").find(".year").eq(arr[0]).text()+"-"+$("#education_list2").find(".year").eq(arr[0]).siblings("ul").find(".month").eq(arr[1]).text()+"-"+$("#education_list2").find(".year").eq(arr[0]).siblings("ul").find(".month").eq(arr[1]).siblings("ul").find(".day").eq(arr[2]).text()
            return begin;
            //return that.timeList[arr[0]]+":00";
          },  
          onSelect: function (valueText,inst){
            var arr=valueText.split(" ");
            var begin=$("#education_list2").find(".year").eq(arr[0]).text()+"-"+$("#education_list2").find(".year").eq(arr[0]).siblings("ul").find(".month").eq(arr[1]).text()+"-"+$("#education_list2").find(".year").eq(arr[0]).siblings("ul").find(".month").eq(arr[1]).siblings("ul").find(".day").eq(arr[2]).text()
            that.ajaxParkInfo[that.nowChoseDayType]=begin
          }
        });
        $("input[id^=education_list2_dummy]").focus();
      },
      choseBeginTime:function(){
        this.setTimeListScroll(0);
      },
      choseEndTime:function(){
        var that=this;
        if(that.ajaxParkInfo.beginTime){
          /*var minTime=that.ajaxParkInfo.beginTime.split(":")[0];
          var arr=[];
          for(var i=that.timeList.length-1;i>=0;i--){
            if(that.timeList[i].replace("0","3")>=minTime){
              arr.unshift(that.timeList[i])
            }else{
              arr.push(that.timeList[i])
            }
          }
          that.timeList=arr;
          console.log(that.timeList)*/
          setTimeout(function(){
            that.setTimeListScroll(1);
          },100)
        }else{
          error_msg("请先选择开始时段",2000)
        }
        
      },
      choseTpye:function(){
        $("input[id^=education_list3_dummy]").focus();
      },
      setTimeListScroll:function(index){
        var that=this;
        var count=that.timeList.indexOf((that.ajaxParkInfo.beginTime?that.ajaxParkInfo.beginTime.split(":")[0]:"09"))
        $("#education_list").mobiscroll().treelist({
          theme: "",  
          lang: "zh",  
          display: 'bottom',
          defaultValue:[index?((count+1)>=that.timeList.length?0:(count+1)):count,0],
          // wheels:[], 设置此属性可以只显示年月，此处演示，就用下面的onBeforeShow方法,另外也可以用treelist去实现  
          //onBeforeShow: function (inst) { inst.settings.wheels[0].length>2?inst.settings.wheels[0].pop():null; }, //弹掉“日”滚轮
          headerText: function (valueText) { //自定义弹出框头部格式
            console.log(valueText)
            var arr=valueText.split(" ");
            if(index!=0){
              return (that.timeList[arr[0]]<=that.ajaxParkInfo.beginTime.split(":")[0]?("次日 "+that.timeList[arr[0]]):that.timeList[arr[0]])+(arr[1]==0?":00":":30");
            }else{
              return that.timeList[arr[0]]+(arr[1]==0?":00":":30")
            }
            
          },  
          onSelect: function (valueText, inst) {
            var arr=valueText.split(" ");
            if(index==0){
              that.ajaxParkInfo.beginTime=that.timeList[arr[0]]+(arr[1]==0?":00":":30")
            }else{
              that.ajaxParkInfo.endTime=that.timeList[arr[0]]+(arr[1]==0?":00":":30")
            }
            
          }  
        });
        $("input[id^=education_list_dummy]").focus();
      },
      setDayList:function(minTime){
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
              if(res.data.carOwnerCommunityName||res.data.carParkCommunityName){
                that.nowName={
                  name:res.data.carOwnerCommunityName||res.data.carParkrHouseName,
                  id:res.data.carOwnerCommunityId||res.data.carParkCommunityId
                }
              }
              localStorage.setItem("fansInfo",toJson(res.data))
            }
            that.getParkList();
          },
          error:function(res){

          }
        })
      },
      getInventory:function(){
        //获取当前车位的情况
        var that=this;
        $.ajax({
          url:urlDir+"/wx/goods/publish/inventory",
          type:"GET",
          success:function(res){
            if(res.code==0){
              that.inventory=res.data
            }
          },
          error:function(res){

          }
        })
      },
      goDemand:function(){
        if(this.fansInfo.carOwnerStatus){
          if(this.fansInfo.carOwnerStatus==2){
            this.goRouter("/demand");
          }else{
            error_msg("身份审核中,请稍后再试",2000)
          }
        }else{
          error_msg("请先注册成为车主",2000)
          this.goRouter("/login?userType=1");
        }
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
    }
  }
</script>
<style scoped>
.park{width:100%;height:100%;position:relative;padding-bottom:1rem;overflow:hidden;}
.park .top{width:100%;height:.54rem;line-height:.54rem;text-align:center;background:#fff;position:absolute;top:0;z-index:2;box-shadow:0 0 .1rem .01rem #dae7ed;}
.park .top i{display:inline-block;width:.24rem;height:.28rem;background:url("../assets/images/icon.png") no-repeat center;background-size:.24rem .28rem;vertical-align:middle;margin-right:.12rem;}
.content{padding-top:.55rem;}
.banner{width: 7.5rem;height: 2.86rem; padding:.3rem 0rem; ;background: url("../assets/images/banner.png") no-repeat; background-size: 7.5rem 2.86rem;}
.banner_left{height: 2.3rem; width: 1.8rem; padding:0 .3rem;text-align: center;line-height: 2.3rem;font-size:.48rem;color: #fff;font-weight: bold;border-right:1px #fff solid;position:relative;}
.banner_left img{width:1.6rem;}
.banner_right{padding:.1rem .2rem; }
.banner_right_conent{width: 2.9rem;}
.banner_right_conent p{display:block;width: 2.9rem; font-size: .34rem;color: #fefefe;}
.banner_right_conent p.banner_yu{padding-top: .15rem;width:4.5rem;}
.banner_right_conent p a{font-size: 1rem;margin-right: .1rem;}
.banner_right_but{width: 2.3rem;padding-left: .1rem; text-align: center;padding-top: .4rem;}
.banner_right_but a{display: block;width: 2.3rem; height: 1.3rem;background: #fff;border-radius: .2rem;box-shadow: 0 0 .15rem #fff;text-align: center;font-size: .32rem;color: #0098db; padding:.2rem .4rem; ;font-weight: bold;font-family: "微软雅黑"}
.tab{width: 7.5rem;height:.6rem;margin-top: .4rem; }
.tab ul{width:7.5rem;height: .6rem;}
.tab ul li{display:block;float: left;height:.5rem;line-height:.5rem;margin:0 .18rem;width:1.5rem;text-align: center;border-bottom: 2px #0098db solid;}
.tab ul li a{display: block;line-height: .3rem;font-size: .28rem; color: #535353;}
.scroll{width:100%;overflow-y:auto;}
.park_list{height: 2.3rem; width: 7rem;margin:.25rem;background: #fff; border-radius: .2rem;box-shadow: 0 0 .15rem #a7d8ed;}
.location{width: 2rem;height:1.2rem;border-radius:.16rem;background:url("../assets/images/list_bg.png") no-repeat center;background-size:2rem 1.2rem; text-align: center;top:.1rem;overflow:hidden;}
.location i{width: .5rem; height: .5rem;display:block;background:url("../assets/images/big_location.png") no-repeat; background-size: .5rem .5rem;top:.1rem;}
.location p{display:block;color:#fff; font-size: .26rem;height:.5rem;padding-top:.7rem;}
.list_content{width: 4.5rem;height:100%;padding:0 .2rem;position:relative;padding-left:.2rem;}
.list_address{display: block;color: #0098db;font-size: .3rem;overflow:hidden;line-height:.45rem;}
.list_bai{color:#959595; font-size: .3rem;padding:.15rem 0;}
.list_content span{display: block;padding-top: .15rem;}
.list_day{font-size: .24rem;height: .45rem; line-height: .45rem;}
.list_right{width: 2.5rem;height:100%;position:relative;}
.lsit_money{color: #ec6941; font-size: .24rem;position:absolute;top:0;right:0;line-height:.8rem;}
.lsit_money a{font-size: .4rem;font-weight: bold;}
.list_but{width: 1.9rem; height: .7rem;text-align: center; line-height: .7rem;display: block;border-radius:.35rem; background: #0098db;box-shadow: 0 0 .15rem #a7d8ed; color: #fff;font-size: .36rem;bottom:.1rem;}
/*弹出层*/
.ceng{width:100%;height:100%;position:absolute;top:0;left:0;background:rgba(255,255,255,.5);}
.ceng .ceng_content{width:5.9rem;background:#fff;border-radius:.24rem;padding:0 .46rem;box-shadow:0 0 1rem 0 #cfebf8;}
/*选择小区*/
.ceng_district{padding:.12rem 0 .6rem;}
.ceng_district .title{height:1.14rem;line-height:1.14rem;text-align:center;}
.ceng_district .word{height:.96rem;line-height:.96rem;}
.ceng_district .word span.r{background:url("../assets/images/sanjiao.png") no-repeat right;background-size:.16rem .23rem;padding-right:.44rem;}
</style>
