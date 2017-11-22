<template>
  <div class="order">
    <!--<div class="tab">
      <a :class="orderType==''?'curr':''" @click="changeType('')">全 部</a>
      <a :class="orderType==7?'curr':''" @click="changeType(7)">已预约</a>
      <a :class="orderType==8?'curr':''" @click="changeType(8)">停车中</a>
      <a :class="orderType==9?'curr':''" @click="changeType(9)">超时中</a>
      <a :class="orderType==10?'curr':''" @click="changeType(10)">已完成</a>
    </div>-->
    <div class="scroll">
        <div class="list" v-for="order in orderList" @click="goDetail(order)">
            <div class="list_div"><a class="l list_address">{{order.communityName}}</a><p class="list_in r" :class="getType(order.orderStatus).cssWord">{{getType(order.orderStatus).word}}</p></div>
            <div class="list_div"><span class="l ordercode">{{order.outTradeNo|orderCode}}</span><span class="r fcgec">￥ {{order.spbillCreateIp}}</span></div>
            <div class="list_div">
              <div class="l">
                <div class=""><span class="max_width"><i class="list_location_i"></i><a>{{order.carParkNumber|removedSemicolon}}</a></span> <span class="max_width" style="width:1.6rem"><i class="list_card_i"></i><a>{{order.carNumber}}</a></span></div>
                <div class="" style="margin-top:.2rem;"><i class="list_time_i l"></i><a>最早入场: {{parkGetBeginDay(order)}}</a></div>
                <div class="" style="margin-top:.2rem;"><i class="list_time_i list_time_i_n l"></i><a>最晚出场: {{parkGetEndDay(order)}} <em class="fcgec" v-if="showExclude(order)">(有排除)</em></a></div>
              </div>
              <div class="r"><a class="list_but" v-if="order.allowRent" @click="subletting($event,order.id)">转租车位</a></div>
            </div>
        </div>
        <div v-if="orderList.length==0" class="centered fcg95 font24" @click="goRouter('/park')" :class="ajaxEnd?'checkout':''">{{ajaxEnd?"去抢车位":"加载中"}}</div>
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
        orderType:"",
        orderList:[],
        sublettingId:"",
        ajaxEnd:false
      }
    },
    mounted:function(){
      
    },
    activated:function(){
      //每次进入
      this.orderList=[];
      this.ajaxEnd=false;
      this.getOrderList();
    },
    created:function(){
      // 组件创建完
    },
    watch:{
      //监视变化
    },
    methods:{
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
                return true
              }
            }
          }
          return false;
        },
        getOrderType:function(index){
          console.log(index)
          switch(index+""){
            case "":
              return ""
            case "7":
              return "已预约"
            case "8":
              return "停车中"
            case "9":
              return "超时中"
            case "10":
              return "已完成"
          }
        },
        suer_del:function(){
          var that=this;
          $.ajax({
            url:urlDir+"/wx/order/rent",
            type:"POST",
            data:{
              id:this.sublettingId
            },
            success:function(res){
              if(res.code==0){
                that.close("ceng_del");
                that.getOrderList();
              }else{
                error_msg(res.msg,2000)
              }
            },
            error:function(res){

            }
          })
        },
        goDetail:function(order){
          this.goRouter("/order_detail?orderId="+order.id)
        },
        subletting:function(e,id){
            e.stopPropagation();
            var that=this;
            this.sublettingId=id;
            this.show("ceng_del")
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
            index=index+"";
            return index.substr(0,4)+"-"+index.substr(4,2)+"-"+index.substr(6,2);
        },
        getParkTime:function(time){
          //进来格式 930   1700
          return (Math.floor(time/100)>=10?Math.floor(time/100):("0"+Math.floor(time/100)))+":"+(time%100>=10?time%100:("0"+time%100));
        },
        changeType:function(index){
            this.orderType=index;
            this.ajaxEnd=false;
            this.getOrderList();
        },
        getOrderList:function(){
            var that=this;
            $.ajax({
              url:urlDir+"/wx/order/list",
              type:"GET",
              data:{
                orderStatus:this.orderType
              },
              success:function(res){
                that.ajaxEnd=true
                if(res.code==0){
                  that.orderList=res.data
                }else{
                  error_msg(res.msg,2000)
                }
              },
              error:function(res){

              }
            })
        },
        getType:function(index){
            switch(index){
                case 0:
                case 1:
                    return {word:"支付中",pd:false,cssWord:"fcr"}
                case 2:
                    return {word:"支付成功",pd:false,cssWord:"fcr"}
                case 3:
                    return {word:"支付失败",pd:false,cssWord:"fcr"}
                case 4:
                    return {word:"已关闭",pd:true,cssWord:"fcr"}
                case 5:
                    return {word:"转入退款",pd:false,cssWord:"fcg"}
                case 6:
                    return {word:"已撤销",pd:false,cssWord:"fco"}
                case 9:
                    return {word:"已转租",pd:false,cssWord:"fcb"}
                case 99:
                    return {word:"已完成",pd:false,cssWord:"fcg"}
            }
            return {word:"",pd:false,cssWord:""}
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
.order{width:100%;height:100%;overflow:hidden;}
.tab{width: 7.5rem;height: 1.4rem;padding: .3rem .2rem 0rem .2rem;position:absolute;top:0;z-index:2;background:#f5f5f5;}
.tab a{width: 1.3rem; height: .8rem; float:left;text-align: center;line-height: .8rem;display: block; border:1px #0098db solid;border-radius: .16rem;margin: 0 .06rem;color:#0098db;}
.tab a.curr{border:none;background: #0098db;color: #fff;box-shadow:0 0 .1rem .01rem #dae7ed;}
.scroll{width:100%;height:100%;position:absolute;top:0;overflow-y:auto;padding-bottom:.1rem;}
.list{width: 7rem;margin:.25rem;padding:.2rem; background: #fff; border-radius: .2rem;box-shadow:0 0 .1rem .01rem #dae7ed;}
.list_left{width: 4.7rem;padding:.0 .3rem;}
.list_div{width:100%;padding: .1rem 0; color: #959595; font-size: .24rem;overflow:hidden;}
.list_address{font-size: .36rem; color: #0098db;margin-right:.3rem;}
.list_div span{overflow:hidden;text-overflow:ellipsis;white-space:nowrap;vertical-align:middle;}
.list_div span a{display:inline-block;}
.list_div span.r{text-align:right;}
.list_div span.ordercode{background:url("../assets/images/uxer_order.png") no-repeat left;background-size:.28rem .32rem;padding-left:.48rem;}
.list_location_i{display:inline-block;vertical-align:middle; width:.28rem; height:.32rem;background: url("../assets/images/icon_location.png") no-repeat;background-size: .28rem .32rem;margin-right: .2rem;}
.list_card_i{display:inline-block;vertical-align:middle; width:.28rem; height:.32rem;background: url("../assets/images/icon_card.png") no-repeat;background-size: .28rem .32rem;margin-right: .2rem;}
.list_time_i{display:inline-block;vertical-align:middle; width:.28rem; height:.32rem;background: url("../assets/images/icon_time.png") no-repeat;background-size: .28rem .32rem;margin-right: .2rem;}
.list_time_i_n{background:none;}
.max_width{width:3rem;overflow:hidden;text-overflow:ellipsis;white-space:nowrap;vertical-align:middle;display:inline-block;}
.list_right{width: 1.9rem; text-align: right;}
.list_in{font-size: .24rem;}
.fcg{color: #22ac38;}
.fcb{color:#0098db;}
.fco{color: #eb6100;}
.fcr{color: #ff0000;}
.list_but{display: block;width: 1.56rem; height: .8rem; text-align: center;line-height: .8rem;background:#0098db; color:#fff;font-size: .26rem;float: right;border-radius: .16rem;margin-top:.6rem;}
/*弹出层*/
.ceng{width:100%;height:100%;position:absolute;top:0;left:0;background:rgba(255,255,255,.5);}
.ceng .ceng_content{width:5.9rem;background:#fff;border-radius:.24rem;padding:0 .46rem;box-shadow:0 0 1rem 0 #cfebf8;}
/*核对*/
.ceng_check .ceng_content{background:#fff;padding-top:.2rem;}
.ceng_check .title{height:1.26rem;line-height:1.26rem;text-align:center;margin-top:.16rem;}
.ceng_check .ceng_operating{height:1.76rem;line-height:1.76rem;text-align:center;}
.ceng_check .ceng_operating span{display:inline-block;width:1.9rem;height:.8rem;line-height:.8rem;background:#0098db;border-radius:.4rem;margin-right:.38rem;}
.ceng_check .ceng_operating span:last-child{margin:0;}
.ceng_check .word{line-height:.54rem;text-align:center;}
.checkout{width:2.64rem;height:.88rem;line-height:.88rem;border-radius:.44rem;background:#0098db;color:#fff;text-align:center;}
</style>
