<template>
  <div class="my_park">
    <div class="top fcw font46 centered_x">
      <p>本平台目前已成交{{inventory.dealCount}}个车位</p>
      <p>还有{{inventory.remainCount}}个空余车位</p>
    </div>
    <div class="content">
      <div class="no" v-show="parkList.length==0">
        <img src="../assets/images/no_park.png">
        <p class="font34 fcb">目前还没有车位</p>
      </div>
      <div class="has" v-show="parkList.length>0">
        <div class="park_list fcg95 font22" v-for="park in parkList" @click="goPublish(park)"><span class="fcb font32">{{park.communityName}}</span><span>{{park.carParkNumber|removedSemicolon}}</span><span>{{getStatus(park)}}</span></div>
        <!--:class="park.carAuditStatus==2&&park.isUsed==2?'curr':''"-->
      </div>
      <div class="add_park font32 fcw" @click="goDistrict()">添加车位</div>
    </div>
  </div>
</template>

<script>
module.exports={
	devtool:'cheap-module-source-map',
    data:function(){
      return {
        parkList:[],
        inventory:{},
        fansInfo:{}
      }
    },
    mounted:function(){
      
    },
    activated:function(){
      //每次进入
      this.close("ceng")
      this.getParkList();
      this.getInventory();
      this.getFansInfo();
    },
    created:function(){
      // 组件创建完
    },
    watch:{
      //监视变化
    },
    methods:{
      getFansInfo:function(fun){
        //获取用户信息
        var that=this;
        $.ajax({
          url:urlDir+"/wx/fans/get",
          type:"GET",
          success:function(res){
            if(res.code==0){
              that.fansInfo=res.data
			        localStorage.setItem("fansInfo",toJson(res.data))
              if(fun){
                fun();
              }
            }
          },
          error:function(res){

          }
        })
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
      getStatus:function(obj){
        //获取当前车位状态
        switch(obj.carAuditStatus){
          case 1:
            return "审核中";
          case 3:
            return "审核拒绝"
        }
        switch(obj.isUsed){
          case 0:
            return "待发布";
          case 1:
            return "发布中";
          case 2:
            return "发布中";
        }
      },
      goPublish:function(obj){
        localStorage.removeItem("exclude");
        localStorage.setItem("parkInfo",toJson(obj))
        this.goRouter("/park_status?goodsId="+obj.id);
          //this.goRouter("/publish_park?goodsId="+obj.id);
        /*if(obj.carAuditStatus!=1){
        }else{
          error_msg("当前车位审核中无法查看详情",2000)
        }*/
      },
      getParkList:function(){
        var that=this;
        $.ajax({
          url:urlDir+"/wx/goods/listAllByOpenid",
          type:"GET",
          success:function(res){
            console.log(res)
            if(res.code==0){
              that.parkList=res.data;
            }
          },
          error:function(res){
            
          }
        })
      },
      goDistrict:function(){
        var that=this;
        this.getFansInfo(function(){
          if(that.fansInfo.carParkAuditStatus){
            if(that.fansInfo.carParkAuditStatus==2){
              that.goRouter("/district");
            }else if(that.fansInfo.carParkAuditStatus==3){
              error_msg("身份审核拒绝,请重新填写注册",2000)
              that.goRouter("/login?userType=2");
            }else{
              error_msg("身份审核中,请稍后再试",2000)
            }
          }else{
            error_msg("请先注册成为车位主",2000)
            that.goRouter("/login?userType=2");
          }
        })
        
      }
    }
  }
</script>
<style scoped>
.my_park{width:100%;height:100%;position:relative;padding-bottom:1rem;overflow:hidden;}
.my_park .top{height:2.04rem;width:7rem;border-bottom-right-radius:.16rem;border-bottom-left-radius:.16rem;background:#0098db;line-height:.64rem;padding:.3rem 0 0 .3rem;position:absolute;top:0;z-index:2;}
.my_park .content{width:100%;height:100%;position:absolute;top:0;left:0;padding-top:2.04rem;background:#f7f3f1;overflow-y:auto;padding-bottom:1rem;}
.content .no{margin-top:1.94rem;}
.content .no img{width:3.9rem;height:2.68rem;margin:0 auto;}
.content .no p{height:1.22rem;line-height:1.22rem;text-align:center;}
.content .add_park{width:3.7rem;height:.88rem;line-height:.88rem;margin:1.24rem auto .6rem;text-align:center;background:#0098db;border-radius:.44rem;}
.content .has{margin-top:.24rem;}
.park_list{margin:.4rem auto 0;height:1.2rem;line-height:1.2rem;width:7rem;border-radius:.16rem;padding:0 .7rem 0 .3rem;box-shadow:0 0 .1rem .01rem #dae7ed;background:#fff url("../assets/images/icon9.png") no-repeat 6.4rem;background-size:.38rem .38rem;}
.park_list span{display:inline-block;overflow:hidden;white-space:nowrap;text-overflow:ellipsis;vertical-align:middle;}
.park_list span:nth-child(1){width:2.5rem;}
.park_list span:nth-child(2){width:2rem;}
.park_list span:nth-child(3){width:1.5rem;text-align:right;}
.park_list.curr{background-color:#0098db;background-image:url("../assets/images/icon9_w.png");}
.park_list.curr span{color:#fff;}
</style>
