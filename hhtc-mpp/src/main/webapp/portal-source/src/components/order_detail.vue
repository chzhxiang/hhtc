<template>
  <div class="order_detail">
    <div class="content">
      <div class="park_info fcw">
        <div class="title font38">{{order.carParkNumber|removedSemicolon}}</div>
        <div class="info"><span class="l font32">车牌号:</span><span class="r font24 add_plate">{{order.carNumber}}</span></div>
        <div class="info"><span class="l font32">最早入场时间:</span><span class="r font24">{{parkGetBeginDay(order)}}</span></div>
        <div class="info"><span class="l font32">最迟出场时间:</span><span class="r font24">{{parkGetEndDay(order)}}</span></div>
        <div class="info"><span class="l font32">停车费:</span><span class="r font24">约 ¥ <em class="font50">{{((order.totalFee||0)/100).toFixed(2)*1}}</em></span></div>
        <div class="info"><span class="l font32">订单状态:</span><span class="r font32">{{getStatusWord(order.orderStatus)}}</span></div>
      </div>
      <div class="excludeday fcgec" v-if="excludeDays.length>0">排除日期: {{excludeDays.join(";")}}</div>
      <div class="operating fcw font38" v-if="order.allowRent"><span @click="show('ceng_del')">转租</span></div>
      <div class="operating fcw font38" v-else><span @click="goBack()">返回</span></div>
    </div>
    <div class="ceng ceng_del ceng_check fcg95 font26" style="display:none;" @click="close('ceng_del')">
      <div class="ceng_content centered" @click="emptyClick($event)">
        <div class="title fcb font38">确定要转租吗</div>
        <p class="word fcg43 font22">转租需要提前一个小时进行，并且转租后您将无法使用该车位</p>
        <div class="ceng_operating fcw font34"><span @click="close('ceng_del')">取消</span><span @click="suer_del">确定</span></div>
      </div>
    </div>
  </div>
</template>

<script>
module.exports={
	devtool:'cheap-module-source-map',
    data:function(){
      return {
        order:{},
        orderId:"",
        excludeDays:[],
        week:["周日","周一","周二","周三","周四","周五","周六"]
      }
    },
    mounted:function(){
      
    },
    activated:function(){
      //每次进入
      this.orderId=getUrl().orderId
      this.order={};
      this.getOrderInfo();
    },
    created:function(){
      // 组件创建完
    },
    watch:{
      //监视变化
    },
    methods:{
      goBack:function(){
        if(history.length<=1){
          this.goRouter("/userinfo")
        }else{
          history.back();
        }
      },
      getStatusWord:function(index){
        switch(index){
          case 0:
          case 1:
            return "待支付";
          case 2:
            return "支付成功";
          case 3:
            return "支付失败";
          case 4:
            return "已关闭";
          case 5:
            return "转入退款";
          case 6:
            return "已撤销";
          case 9:
            return "已转租";
          case 99:
            return "已完成";
        }
      },
      suer_del:function(){
        var that=this;
        $.ajax({
          url:urlDir+"/wx/order/rent",
          type:"POST",
          data:{
            id:this.orderId
          },
          success:function(res){
            error_msg(res.msg,2000)
            if(res.code==0){
              that.getOrderInfo();
            }
          },
          error:function(res){

          }
        })
      },
      getDayList:function(str){
        if(str){
          var arr=str.split("-");
          var word=[],that=this;
          arr.forEach(function(v,i){
            if(v){
              word.push(that.parkGetDay(v));
            }
          })
          return word;
        }
        return []
      },
      parkGetBeginDay:function(obj){
        if(toJson(obj)!="{}"){
          var arr=obj.openFromDates.split("-");
          return this.getParkDay(arr[0])+" "+this.getParkTime(obj.openFromTime)
        }
      },
      parkGetEndDay:function(obj){
        if(toJson(obj)!="{}"){
          var arr=obj.openFromDates.split("-");
          var time=this.getParkDay(arr[arr.length-1]);
          if(obj.openEndTime*1<obj.openFromTime*1){
            time=getTimes(new Date(time.replace(/-/g,"/")).getTime()+24*3600000,"YMD",false)
          }
          return time+" "+this.getParkTime(obj.openEndTime)
        }
      },
      getParkDay:function(index){
        //进来格式20170911
        if(index)
        return index.substr(0,4)+"-"+index.substr(4,2)+"-"+index.substr(6,2);
      },
      getParkTime:function(time){
        //进来格式 930   1700
        return (Math.floor(time/100)>=10?Math.floor(time/100):("0"+Math.floor(time/100)))+":"+(time%100>=10?time%100:("0"+time%100));
      },
      getOrderInfo:function(){
        var that=this;
        $.ajax({
          url:urlDir+"/wx/order/get",
          type:"GET",
          data:{
            id:this.orderId
          },
          success:function(res){
            if(res.code==0){
              that.order=res.data;
              that.close("ceng_del");
              that.showExclude(res.data)
            }
          },
          error:function(res){

          }
        })
      },
      showExclude:function(obj){
        var arr=obj.openFromDates.split("-"),that=this;
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
.order_detail{width:100%;height:100%;position:relative;overflow:hidden;}
.order_detail .content{background:#f7f3f1;width:100%;height:100%;position:absolute;top:0;overflow-y:auto;padding-bottom:1rem;}
.park_info{width:7rem;height:6.19rem;background:url("../assets/images/park_info_bg.png") no-repeat center;background-size:7rem 6.19rem;margin:.32rem auto .2rem;}
.park_info .title{text-align:center;height:.98rem;line-height:.98rem;margin-bottom:.26rem;}
.park_info .info{height:.96rem;line-height:.96rem;overflow:hidden;padding:0 .56rem 0 1.2rem;background:url("../assets/images/icon1.png") no-repeat .34rem;background-size:.6rem .6rem;}
.park_info .info:nth-child(3){background-image:url("../assets/images/icon2.png");}
.park_info .info:nth-child(4){background-image:url("../assets/images/icon3.png");}
.park_info .info:nth-child(5){background-image:url("../assets/images/icon4.png");}
.park_info .info:nth-child(6){background-image:url("../assets/images/icon4.png");}
.park_info .add_plate{background:url("../assets/images/add_plate_icon.png") no-repeat right;background-size:.16rem .24rem;padding-right:.3rem;}
.prompt{padding:0 .6rem;}
.prompt div{height:1.16rem;line-height:1.16rem;}
.prompt p{line-height:.34rem;}
.operating{height:1.96rem;line-height:1.96rem;text-align:center;}
.operating span{display:inline-block;width:2.64rem;height:.88rem;line-height:.88rem;border-radius:.44rem;background:#0098db;margin-right:.62rem;}
.operating span:last-child{margin:0;}
/*弹出层*/
.ceng{width:100%;height:100%;position:absolute;top:0;left:0;background:rgba(255,255,255,.5);}
.ceng .ceng_content{width:5.9rem;background:#fff;border-radius:.24rem;padding:0 .46rem;box-shadow:0 0 1rem 0 #cfebf8;}
/*信息核对*/
.ceng_check .ceng_content{background:#fff;padding-top:.2rem;}
.ceng_check .title{height:1.26rem;line-height:1.26rem;text-align:center;margin-top:.16rem;}
.ceng_check .ceng_operating{height:1.76rem;line-height:1.76rem;text-align:center;}
.ceng_check .ceng_operating span{display:inline-block;width:1.9rem;height:.8rem;line-height:.8rem;background:#0098db;border-radius:.4rem;margin-right:.38rem;}
.ceng_check .ceng_operating span:last-child{margin:0;}
.ceng_check .word{line-height:.54rem;text-align:center;}
.excludeday{line-height:.5rem;padding:0 .3rem;}
</style>
