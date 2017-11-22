<template>
  <div class="feedback">
    <div class="top font22 fcg95">吼吼停车让你可以快速找到停车位，不再为没有车位发愁。 客服电话：400-000-000</div>
    <div class="textarea"><textarea class="font30" placeholder="请输入您的意见..." v-model="word" maxlength="300"></textarea></div>
    <div class="checkout font34 fcw" @click="checkout">提交反馈</div>
    <!--押金退回成功-->
    <div class="ceng ceng_deposit fcg95 font26" style="display:none;" @click="close('ceng_deposit')">
      <div class="ceng_content centered" @click="emptyClick($event)">
        <div class="title fcb font38">提交成功</div>
        <p class="word fcg95 font26">非常感谢您提出的宝贵意见，我们重视每一个为我们项目提供帮助的人!</p>
        <div class="ceng_operating fcw font34" @click="goRouter('/userinfo')">返回个人中心</div>
      </div>
    </div>
  </div>
</template>

<script>
module.exports={
	devtool:'cheap-module-source-map',
    data:function(){
      return {
        word:""
      }
    },
    mounted:function(){
      
    },
    activated:function(){
      //每次进入
      this.close("ceng")
    },
    created:function(){
      // 组件创建完
    },
    watch:{
      //监视变化
    },
    methods:{
      checkout:function(){
        var that=this;
        if(this.word.length>1){
          $.ajax({
            url:urlDir+"/wx/advice/add",
            type:"POST",
            data:{
              content:this.word
            },
            success:function(res){
              if(res.code==0){
                that.show("ceng_deposit")
              }else{
                error_msg(res.msg,2000)
              }
            },
            error:function(res){

            }
          })
        }else{
          error_msg("请输入您的意见",2000)
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
.feedback{width:100%;height:100%;position:relative;background:#f7f3f1;padding:0 .24rem;}
.top{line-height:.36rem;padding:.3rem 0;}
.textarea{width:7rem;height:3.16rem;background:#fff;border-radius:.16rem;box-shadow:0 0 .1rem .01rem #dae7ed;overflow:hidden;}
.textarea textarea{width:100%;height:100%;padding:.2rem .28rem;}
.checkout{background:#0098db;width:3.7rem;height:.88rem;line-height:.88rem;text-align:center;border-radius:.44rem;margin:2.8rem auto 0;}
/*弹出层*/
.ceng{width:100%;height:100%;position:absolute;top:0;left:0;background:rgba(255,255,255,.5);}
.ceng .ceng_content{width:5.9rem;background:#fff;border-radius:.24rem;padding:0 .46rem;box-shadow:0 0 1rem 0 #cfebf8;}
.ceng_deposit .ceng_content{background:#fff url("../assets/images/yes.png") no-repeat center .4rem;background-size:1.98rem 1.98rem;padding-top:2.38rem;}
.ceng_deposit .title{line-height:.5rem;text-align:center;padding:.34rem 0;}
.ceng_deposit .title b{display:block;}
.ceng_deposit .word{height:.48rem;line-height:.48rem;text-align:center;}
.ceng_deposit .ceng_operating{width:3.7rem;height:.8rem;line-height:.8rem;background:#0098db;margin:1rem auto .56rem;border-radius:.4rem;text-align:center;}
</style>
