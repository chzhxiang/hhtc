<template>
  <div class="bank">
    <div class="top fcw font46 centered_x">添加银行卡</div>
    <div class="content">
      <div class="bank_info font30 chang"><span class="l fcg43">银行名称</span><span class="r"><input type="text" placeholder="请选择银行名称" name="" readonly></span></div>
      <div class="bank_info font30 chang"><span class="l fcg43">银行卡号</span><span class="r"><input type="text" placeholder="请输入银行卡号" name=""></span></div>
      <div class="bank_info font30 chang1"><span class="l fcg43">开户行名称</span><span class="r"><input type="text" placeholder="请输入开户行名称" name=""></span></div>
      <div class="bank_info font30 chang2"><span class="l fcg43">姓名</span><span class="r"><input type="text" placeholder="请输入姓名" name=""></span></div>
      <div class="checkout fcw font34">确认添加</div>
      <div class="my_bank">
        <div class="title fcg43 font30">我的银行卡</div>
        <div class="card_list fcw gs">
          <div class="bg"></div>
          <div class="name font26">招商银行</div>
          <div class="type font18">信用卡</div>
          <div class="number font18"><samp>**** **** **** 5478</samp></div>
        </div>
        <div class="card_list fcw ny">
          <div class="bg"></div>
          <div class="name font26">农业银行</div>
          <div class="type font18">储蓄卡</div>
          <div class="number font18"><samp>**** **** **** 5478</samp></div>
        </div>
      </div>
    </div>
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
    activated:function(){
      //每次进入
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
.bank{width:100%;height:100%;position:relative;padding-bottom:1rem;overflow:hidden;background:#f7f3f1;overflow-y:auto;}
.bank .top{height:2.04rem;line-height:2.04rem;width:7rem;border-bottom-right-radius:.16rem;border-bottom-left-radius:.16rem;background:#0098db;position:absolute;top:0;z-index:2;text-align:center;}
.bank .content{background:#e8e4e2;width:100%;height:100%;padding-top:2.34rem;position:absolute;top:0;}
.bank_info{height:1.22rem;line-height:1.22rem;padding:.24rem;}
.bank_info span{border-radius:.16rem;padding:0 .3rem;height:.84rem;line-height:.84rem;background:#fff;text-align:center;box-shadow:0 0 .1rem .01rem #dae7ed;}
.bank_info:first-child span.r{background:#fff url("../assets/images/icon10.png") no-repeat 4.3rem;background-size:.24rem .24rem;}
.bank_info.chang input{width:4.12rem;}
.bank_info.chang1 input{width:3.85rem;}
.bank_info.chang2 input{width:4.75rem;}
/**/
.checkout{width:3.7rem;height:.88rem;line-height:.88rem;text-align:center;border-radius:.44rem;margin:.9rem auto .7rem;background:#0098db;}
.my_bank{width:7rem;margin:0 auto;padding:.16rem 0;background:#fff;box-shadow:0 0 .1rem .01rem #dae7ed;border-radius:.16rem;}
.my_bank .title{height:1rem;line-height:1rem;text-align:center;}
.card_list{width:6.66rem;height:1.86rem;border-radius:.1rem;padding:.3rem .36rem 0 1rem;margin:0 auto .24rem;position:relative;}
.card_list:nth-child{margin-bottom:0;}
.card_list .bg{width:.6rem;height:.6rem;background:url("../assets/images/nongye.png") no-repeat center;background-size:.58rem .58rem;position:absolute;top:.2rem;left:.2rem;}
.card_list .name{height:.42rem;line-height:.42rem;}
.card_list .type{height:.36rem;line-height:.36rem;}
.card_list .number{height:.6rem;line-height:.6rem;margin-top:.16rem;text-align:right;letter-spacing:.06rem;}
.card_list.gs{background:-webkit-linear-gradient(left bottom,#e85c65,#e75079);}
.card_list.ny{background:-webkit-linear-gradient(left bottom,#19b38f,#099eb6);}
.card_list.gs .bg{background-image:url("../assets/images/zhaoshang.png");}
.card_list.ny .bg{background-image:url("../assets/images/nongye.png");}
</style>
