<template>
  <div class="income">
    <div class="scroll">
      <div class="list fcg95 font24" v-for="icome in icomeList">
      <!--<span class="fcb font34 name">{{icome.communityName}}</span>-->
        <!--<div class="title"><span class="ordercode">{{icome.bizDate}}</span><span class="r fcgec">{{getType(icome)}}</span></div>-->
        <div class="list_info">{{icome.inOutDesc}}</div><!--<span class="address" v-if="icome.carParkNumber">{{icome.carParkNumber}}</span><span class="plate" v-if="icome.carNumber">{{icome.carNumber}}</span>-->
        <div class="list_info"><span class="l time">{{icome.bizDateTime.replace("-","年").replace("-","月").replace(" ","日 ")}}</span><i class="r" :class="icome.inOut=='in'?'b':'fcgec'">¥ <em class="font54">{{icome.inOut=='in'?'+':'-'}}{{Math.abs(icome.money*1)}}</em></i></div>

      </div>
      <div class="centered" v-if="icomeList.length==0">{{ajaxEnd?"暂无收支明细":"加载中"}}</div>
    </div>
  </div>
</template>

<script>
module.exports={
	devtool:'cheap-module-source-map',
    data:function(){
      return {
        icomeList:[],
        scrollInfo:{"pd":true,"limit":0,"last":false},
        ajaxEnd:false
      }
    },
    mounted:function(){
      var that=this;
      setScroll_event($(".income"),"scroll")
      $(".income").on("scroll",function(){
        var scrollTop=$(this).scrollTop();
        var height=$(this).offset().height;
        var all_height=$(".scroll").offset().height;
        if(scrollTop+height>=all_height-10){
          console.log("到底了")
          if(!that.scrollInfo.last){
            if(that.scrollInfo.pd){
              that.scrollInfo.limit+=1;
              that.scrollInfo.pd=true;
              that.getIcomeList();
            }
          }
        }
      })
    },
    activated:function(){
      //每次进入
      this.scrollInfo={"pd":true,"limit":0,"last":false};
      this.icomeList=[];
      this.ajaxEnd=false;
      this.getIcomeList();
    },
    created:function(){
      // 组件创建完
    },
    watch:{
      //监视变化
    },
    methods:{
      getType:function(obj){
        switch(obj.inOutType*1){
          case 1:
            return "缴纳押金";
          case 2:
            return "车位出租";
          case 3:
            return "提取押金";
          case 4:
            return "提现";
          case 5:
            return "扣除押金";
        }
      },
      getIcomeList:function(){
        var that=this;
        $.ajax({
          url:urlDir+"/wx/userfunds/flow/list",
          type:"GET",
          data:{
            pageNo:this.scrollInfo.limit
          },
          success:function(res){
            that.ajaxEnd=true;
            that.scrollInfo.pd=true;
            if(res.code==0){
              that.icomeList=that.icomeList.concat(res.data.content);
              that.scrollInfo.last=res.data.last;
            }else{
              error_msg(res.msg,2000)
            }
          },
          error:function(res){

          }
        })
      }
    }
  }
</script>
<style scoped>
.income{width:100%;height:100%;position:relative;overflow:hidden;overflow-y:auto;}
.list{width:7rem;background:#fff;border-radius:.16rem;box-shadow:0 0 .1rem .01rem #dae7ed;margin:.28rem auto 0;padding:.06rem 0 .14rem;}
.title{height:.76rem;line-height:.76rem;padding:0 .32rem;}
.title .name{margin-right:.4rem;}
.list_info{line-height:.6rem;padding:0 .4rem 0 .24rem;overflow:hidden;}
.list_info span{display:inline-block;background:url("../assets/images/address.png") no-repeat left;background-size:.36rem .3rem;padding-left:.52rem;}
.list_info span.plate{background-image:url("../assets/images/plate.png");margin-left:.7rem;}
.list_info span.time{background-image:url("../assets/images/time.png");}
.title span.ordercode{display:inline-block;background:url("../assets/images/uxer_order.png") no-repeat left;background-size:.3rem .36rem;padding-left:.52rem;}
.list_info .b{color:#0098db;}
</style>
