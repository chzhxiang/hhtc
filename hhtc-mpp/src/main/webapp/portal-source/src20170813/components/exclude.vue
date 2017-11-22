<template>
  <div class="exclude">
    <div class="content">
      <div class="add font32 fcw" id="birthday">添加排除时间</div>
      <div class="exclude_list fcg95 font32" v-for="exclude in excludeList">{{exclude}} <i class="centered_y" @click="delExclude(exclude)"></i></div>
    </div>
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
        beginDay:"",
        endDay:"",
        excludeList:[],
        weekCheckout:[],
        dayList:{},
        type:1
      }
    },
    mounted:function(){
      var that=this;
      $("#birthday").on("click",function(){
        var o=0;
        for(var key in that.dayList){
          o++
        }
        if(o==0){
          error_msg("所有时间都被排除了",2000)
          return
        }
        setTimeout(function(){
          $("#education_list2").mobiscroll().treelist({
            theme: "",  
            lang: "zh",  
            display: 'bottom', 
            // wheels:[], 设置此属性可以只显示年月，此处演示，就用下面的onBeforeShow方法,另外也可以用treelist去实现  
            //onBeforeShow: function (inst) { inst.settings.wheels[0].length>2?inst.settings.wheels[0].pop():null; }, //弹掉“日”滚轮
            headerText: function (valueText) { //自定义弹出框头部格式
              var arr=valueText.split(" ");
              return $("#education_list2").find(".year").eq(arr[0]).text()+"-"+$("#education_list2").find(".year").eq(arr[0]).siblings("ul").find(".month").eq(arr[1]).text()+"-"+$("#education_list2").find(".year").eq(arr[0]).siblings("ul").find(".month").eq(arr[1]).siblings("ul").find(".day").eq(arr[2]).text()
            },  
            onSelect: function (valueText, inst) {
              var arr=valueText.split(" ");
              var word=$("#education_list2").find(".year").eq(arr[0]).text()+"-"+$("#education_list2").find(".year").eq(arr[0]).siblings("ul").find(".month").eq(arr[1]).text()+"-"+$("#education_list2").find(".year").eq(arr[0]).siblings("ul").find(".month").eq(arr[1]).siblings("ul").find(".day").eq(arr[2]).text();
              if(that.excludeList.indexOf(word)==-1){
                that.excludeList.unshift(word);
                localStorage.setItem("exclude"+that.type,toJson(that.excludeList));
                that.setDayList(that.beginTime,that.endTime);
              }else{
                error_msg("已排除该日期",2000);
              }
            }  
          });
          $("input[id^=education_list2_dummy]").focus();
        },100)
      })
    },
    activated:function(){
      //每次进入
      this.close("ceng")
      var url=getUrl();
      var beginDay=url.beginDay;
      var endDay=url.endDay;
      this.weekCheckout=url.weekCheckout.split(",");
      this.type=url.type;
      this.excludeList=[];
      if(localStorage.getItem("exclude"+this.type)){
        this.excludeList=toParse(localStorage.getItem("exclude"+this.type));
      }
      this.endTime=new Date(endDay.replace(/-/g,"/")).getTime();
      this.beginTime=new Date(beginDay.replace(/-/g,"/")).getTime();
      console.log(this.endTime,this.beginTime)
      this.setDayList(this.beginTime,this.endTime);
      
    },
    created:function(){
      // 组件创建完
    },
    watch:{
      //监视变化
    },
    methods:{
      delExclude:function(str){
        this.excludeList.splice(this.excludeList.indexOf(str),1);
        localStorage.setItem("exclude"+this.type,toJson(this.excludeList));
      },
      setDayList:function(beginTime,endTime){
        var that=this,month="",day="";
        var date=new Date();
        this.dayList={};
        for(var time=beginTime;time<=endTime;time+=24*3600000){
          console.log(time)
            date.setTime(time);
            var dateWord=getTimes(time,"YMD",false);
            if(that.weekCheckout.indexOf(date.getDay()+"")==-1&&that.excludeList.indexOf(dateWord)==-1){
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
      }
    }
  }
</script>
<style scoped>
.exclude{width:100%;height:100%;position:relative;overflow:hidden;}
.exclude .content{width:100%;height:100%;overflow-y:auto;position:absolute;top:0;left:0;background:#fff;}
.add{width:7rem;height:.8rem;line-height:.8rem;background:#0098db;text-align:center;margin:.4rem auto 0;border-radius:.16rem;}
.exclude_list{margin:.4rem auto 0;height:.8rem;line-height:.8rem;width:7rem;border-radius:.16rem;padding:0 .7rem 0 .3rem;box-shadow:0 0 .1rem .01rem #dae7ed;background:#fff;position:relative;}
.exclude_list i{width:.8rem;height:.8rem;display:block;background:url("../assets/images/icon_del.png") no-repeat center;background-size:.48rem .48rem;right:0;}
</style>
