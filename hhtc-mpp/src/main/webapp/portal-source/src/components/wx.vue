<template>
  <div id="wx">
    <keep-alive><router-view></router-view></keep-alive>
    <div class="wx fcg48">
      <div class="l reservation" :class="chose==1?'curr':''" @click="goRouter('/park',1)">抢车位</div>
      <div class="l order" :class="chose==2?'curr':''" @click="goRouter('/publish',2)">有车位</div>
      <div class="l user" :class="chose==3?'curr':''" @click="goRouter('/userinfo',3)">个人中心</div>
    </div>
  </div>
</template>
<script>
module.exports={
	devtool:'cheap-module-source-map',
    name:"wx",
    data:function(){
      return {
        chose:1
      }
    },
    mounted:function(){
      
    },
    activated:function(){
      console.log(location.href)
      var str=location.href;
      if(str.indexOf("/park")!=-1){
        this.chose=1;
      }else if(str.indexOf("/publish")!=-1){
        this.chose=2;
      }else if(str.indexOf("/userinfo")!=-1){
        this.chose=3;
      }
    },
    created:function(){
      // 组件创建完
      this.getdaynight();
    },
    watch:{
      //监视变化
    },
    methods:{
      goRouter:function(str,index){
        if(index==3){
          var obj=toParse(localStorage.getItem("fansInfo"));
          if(obj.carOwnerStatus!=2&&obj.carParkAuditStatus!=2){
              error_msg("请先注册",2000)
              this.$router.push({path:"/login"})
              return true;
          }
        }
        this.chose=index
        this.$router.push({path:str})
      },
      getdaynight:function(){
        $.ajax({
          url:urlDir+"/wx/common/daynight",
          type:"GET",
          success:function(res){
            if(res.code==0){
              localStorage.setItem("daynight",toJson({
                "timeDay":(res.data.timeDay/100>=10?res.data.timeDay/100:("0"+res.data.timeDay/100))+"",
                "timeNight":(res.data.timeNight/100>=10?res.data.timeNight/100:("0"+res.data.timeNight/100))+""
              }))
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
#wx{width:100%;height:100%;overflow:hidden;position:relative;}
.wx{position:absolute;bottom:0;left:0;background:#fff;width:7.5rem;height:1rem;border-top:1px solid #a0a0a0;}
.wx div{width:2.5rem;text-align:center;height:1rem;padding-top:.5rem;line-height:.5rem;background:#fff url("../assets/images/park.png") no-repeat center .05rem;background-size:.46rem .46rem;}
.wx div.order{background-image:url("../assets/images/publish.png");background-size:.6rem .46rem;}
.wx div.user{background-image:url("../assets/images/user.png");background-size:.4rem .46rem;}
.wx div.reservation.curr{background-image:url("../assets/images/park_b.png");}
.wx div.order.curr{background-image:url("../assets/images/publish_b.png");}
.wx div.user.curr{background-image:url("../assets/images/user_b.png");}
</style>
