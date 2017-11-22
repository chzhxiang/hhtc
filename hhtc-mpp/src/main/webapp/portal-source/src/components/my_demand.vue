<template>
  <div class="order">
    <div class="scroll">
        <div class="list" v-for="order in orderList">
            <div class="list_div">
              <span class="l"><i class="list_card_i l"></i><a>{{order.carNumber|removedSemicolon}}</a></span>
              <span class="r fcgec">￥ <em class="font40">{{order.moneyRent}}</em> <em style="margin-left:.3rem">{{typeList[order.needType-1]}}</em></span>
            </div>
            <div class="list_div">
                <span class="l"><i class="list_time_i l"></i><a>{{getShowTime(order)}}</a></span>
                <span class="r list_in fcb">{{order.status==2?"已匹配":"匹配中"}}</span>
            </div>
        </div>
        <div v-if="orderList.length==0" class="centered fcg95 font24" @click="goRouter('/park')" :class="ajaxEnd?'checkout':''" v-html="ajaxEnd?'去发布我的需求':'加载中'"></div>
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
        ajaxEnd:false,
        typeList:["白天","夜间","全天"],
        pageNo:0
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
        goDetail:function(order){
          this.goRouter("/order_detail?orderId="+order.id)
        },
        subletting:function(e,id){
            e.stopPropagation();
            var that=this;
            this.sublettingId=id;
            this.show("ceng_del")
        },
        getShowTime:function(order){
            //2017年6月10日 16:00-17:00

            return this.getParkDay(order.needFromDate)+" "+this.getParkTime(order.needFromTime,order.needEndTime)
        },
        parkGetBeginDay:function(obj){
          if(toJson(obj)!="{}"){
            var arr=obj.publishFromDates.split("-");
            return this.getParkDay(arr[0])+" "+this.getParkTime(obj.publishFromTime)
          }
        },
        parkGetEndDay:function(obj){
          if(toJson(obj)!="{}"){
            var arr=obj.publishFromDates.split("-");
            var time=this.getParkDay(arr[arr.length-1]);
            if(obj.publishEndTime*1<obj.publishFromTime*1){
              time=getTimes(new Date(time.replace(/-/g,"/")).getTime()+24*3600000,"YMD",false)
            }
            return time+" "+this.getParkTime(obj.publishEndTime)
          }
        },
        getParkDay:function(index){
            //进来格式20170911
            index=index+"";
            return index.substr(0,4)+"年"+index.substr(4,2)+"月"+index.substr(6,2)+"日";
        },
        getParkTime:function(begin,end){
            //进来格式 930   1700
            var beginTime=(Math.floor(begin/100)>=10?Math.floor(begin/100):("0"+Math.floor(begin/100)))+":"+(begin%100>=10?begin%100:("0"+begin%100));
            var endTime=(Math.floor(end/100)>=10?Math.floor(end/100):("0"+Math.floor(end/100)))+":"+(end%100>=10?end%100:("0"+end%100));
            return beginTime+"--"+((begin*1>=end*1)?("次日"+endTime):endTime);
        },
        changeType:function(index){
            this.orderType=index;
            this.ajaxEnd=false;
            this.getOrderList();
        },
        getOrderList:function(){
            var that=this;
            $.ajax({
              url:urlDir+"/wx/goods/need/list",
              type:"GET",
              data:{
                pageNo:this.pageNo
              },
              success:function(res){
                that.ajaxEnd=true
                if(res.code==0){
                  that.orderList=res.data.content
                }else{
                  error_msg(res.msg,2000)
                }
              },
              error:function(res){

              }
            })
        },
        goRouter:function(str){
          if(this.ajaxEnd)
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
.list{overflow:hidden; width: 7rem;margin:.25rem;padding:.2rem; background: #fff; border-radius: .2rem;box-shadow:0 0 .1rem .01rem #dae7ed;}
.list_left{width: 4.7rem;padding:.0 .3rem;}
.list_div{width:100%;padding: .1rem 0; color: #959595; font-size: .24rem;display: inline-block;}
.list_address{font-size: .36rem; color: #0098db;margin-right:.3rem;}
.list_div span{overflow:hidden;text-overflow:ellipsis;white-space:nowrap;vertical-align:middle;}
.list_div span.r{text-align:right;}
.list_div span.ordercode{background:url("../assets/images/uxer_order.png") no-repeat left;background-size:.28rem .32rem;padding-left:.48rem;}
.list_location_i{display:block; width:.28rem; height:.32rem;background: url("../assets/images/icon_location.png") no-repeat;background-size: .28rem .32rem;margin-right: .2rem;}
.list_card_i{display:block; width:.28rem; height:.32rem;background: url("../assets/images/icon_card.png") no-repeat;background-size: .28rem .32rem;margin-right: .2rem;}
.list_div span a{display:inline-block;}
.list_time_i{display:block; width:.28rem; height:.32rem;background: url("../assets/images/icon_time.png") no-repeat;background-size: .28rem .32rem;margin-right: .2rem;}
.list_right{width: 1.9rem; text-align: right;}
.list_in{font-size: .24rem;}
.fcg{color: #22ac38;}
.fcb{color:#0098db;}
.fco{color: #eb6100;}
.fcr{color: #ff0000;}
.list_but{display: block;width: 1.56rem; height: .8rem; text-align: center;line-height: .8rem;background:#0098db; color:#fff;font-size: .26rem;float: right;border-radius: .16rem;}
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
