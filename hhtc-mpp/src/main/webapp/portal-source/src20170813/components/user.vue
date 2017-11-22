<template>
  <div class="user">
    
  </div>
</template>

<script>
module.exports={
	devtool:'cheap-module-source-map',
    data:function(){
      return {
        data:{}
      }
    },
    mounted:function(){
      
    },
    created:function(){
      // 组件创建完
    },
    watch:{
      //监视变化
    },
    methods:{

    }
  }
</script>
<style scoped>
.user{background:#33354e;}
.user_info{height:2.98rem;width:7.5rem;background:url("../assets/user_top_bg.png") no-repeat center;background-size:7.5rem 2.98rem;padding:0 .24rem;}
.user_info .user_avatar{width:1.46rem;height:1.46rem;border:1px solid #fff;border-radius:50%;overflow:hidden;margin-top:.76rem;}
.user_info .user_avatar img{width:1.46rem;height:1.46rem;}
.user_info .right{margin-left:.3rem;}
.user_info .right p{line-height:.72rem;}
.user_btn{height:1.3rem;padding-top:.24rem;line-height:1.06rem;padding:.24rem .24rem 0;}
.user_btn .btn{display:inline-block; width:2.18rem;height:.8rem;line-height:.8rem;text-align:center;background:url("../assets/user_btn.png") no-repeat center;margin-right:.24rem;background-size:2.18rem .8rem;}
.user_btn .btn:last-child{margin-right:0;}
.user_btn .btn i{display:inline-block;vertical-align:middle;}
.user_btn .btn img{display:inline-block;width:.3rem;height:.3rem;vertical-align:middle;margin-left:.1rem;}
.user_type{height:.46rem;line-height:.46rem;text-align:center;}
.user_data{padding-top:.12rem;margin:0 .24rem;background:#28293c;overflow:hidden;border-radius:.04rem;overflow:hidden;margin-bottom:.14rem;}
.user_data div.l{width:2.28rem;height:1.86rem;border-right:1px solid #1a1b28;border-left:#4b4c69;text-align:center;}
.user_data div.l:last-child{border-right:0;}
.user_data div.l:first-child{border-left:0;}
.user_data div.l .top{height:.54rem;line-height:.54rem;}
.user_data div.l .center{height:.72rem;line-height:.72rem;}
.user_data div.l .bottom{height:.46rem;line-height:.46rem;}
/**/
.user_data_img{padding:0 .24rem;overflow:hidden;}
.user_data_img div.l{width:2.18rem;height:3.34rem;border-radius:.04rem;margin-right:.24rem;margin-bottom:.24rem;}
.user_data_img div.l img{width:2.18rem;height:3.34rem;}
.user_data_img div.l:nth-child(3n){margin-right:0;}
/**/
.user_operating{height:1.14rem;line-height:1.14rem;text-align:center;margin-bottom:.24rem;}
.user_operating span{width:2.18rem;height:.8rem;line-height:.8rem;display:inline-block;background:url("../assets/user_btn.png") no-repeat center;background-size:2.18rem .8rem;}
/**/
.user_operating_t{overflow:hidden;}
.user_operating_t p{height:.88rem;line-height:.88rem;border-bottom:1px solid #33354e;position:relative;background:#28293c url("../assets/user_order.png") no-repeat .24rem;background-size:.46rem .42rem;padding-left:1.03rem;}
.user_operating_t p:after{content:"";width:.16rem;height:.24rem;background:url("../assets/user_right.png") no-repeat center;background-size:.16rem .24rem;right:.3rem;position:absolute;top:50%;-webkit-transform:translateY(-50%);transform:translateY(-50%);}
.user_operating_t p:nth-child(2){background-image:url("../assets/user_ka.png");background-size:.46rem .34rem;}
.user_operating_t p:nth-child(3){background-image:url("../assets/user_e.png");background-size:.46rem .46rem;}
</style>
