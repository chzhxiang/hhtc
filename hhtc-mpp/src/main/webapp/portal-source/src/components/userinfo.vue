<template>
  <div class="user">
    <div class="info fcw">
      <div class="img"><img :src="fansInfo.headimgurl"></div>
      <div class="name font38">{{fansInfo.nickname||"--"}}</div>
      <div class="phone font32">{{fansInfo.phoneNo|phoneShow}}</div>
      <div class="money font54"><span class="l"><b>{{(isenough.moneyBase||0)*1}}</b><em class="font22">押金</em></span><span class="l"><b>{{(isenough.moneyBalance||0)*1}}</b><em class="font22">余额</em></span></div>
    </div>
    <div class="list font34 fcg95" style="margin-top:.24rem;" @click="goRouter('/order')"><img class="centered_y" src="../assets/images/uxer_order.png">我的订单</div>
    <div class="list font34 fcg95" @click="goRouter('/my_demand')"><img class="centered_y" src="../assets/images/user_icon_1.png">我的需求</div>
    <div class="list font34 fcg95" @click="goRouter('/income')"><img class="centered_y" src="../assets/images/user_income.png">收支明细</div>
    <div class="list font34 fcg95" style="margin-top:.46rem;" @click="deposit()"><img class="centered_y" src="../assets/images/user_icon_2.png">押金充值</div>
    <div class="list font34 fcg95" @click="goRouter('/recharge')"><img class="centered_y" src="../assets/images/user_icon_3.png">余额充值</div>
    <div class="list font34 fcg95" style="margin-top:.46rem;" @click="refunds"><img class="centered_y" src="../assets/images/user_icon_4.png">押金退回</div>
    <div class="list font34 fcg95" @click="withdraw"><img class="centered_y" src="../assets/images/user_money.png">提现</div>
    
    <div class="list font34 fcg95" @click="goRouter('/feedback')"><img class="centered_y" src="../assets/images/user_fee.png">用户反馈</div>
    <!--押金退回成功-->
    <div class="ceng ceng_deposit fcg95 font26" style="display:none;" @click="close('ceng_deposit')">
      <div class="ceng_content centered" @click="emptyClick($event)">
        <div class="title fcb font38"><b>{{operatingType==0?"押金退回":"提现"}}</b><b>申请成功</b></div>
        <p class="word fcg95 font26">我们会在5个工作日内审核你的申请</p>
        <p class="word fcg95 font26">请注意留意您的微信消息通知</p>
        <p class="word fcg95 font26">如有任何疑问请拨打客服</p>
        <p class="word fcg95 font26">电话：15023194640</p>
        <div class="ceng_operating fcw font34" @click="goRouter('order')" style="display:none">查看我的订单</div>
      </div>
    </div>
    <div class="ceng prompt ceng_s fcg95 font26" style="display:none;" @click="close('prompt')">
      <div class="ceng_content centered" @click="emptyClick($event)">
        <div class="title fcb font38">{{operatingType==0?"押金退回申请":"提现申请"}}</div>
        <p class="word fcg95 font26" v-if="operatingType==0">押金退回后发布车位或者预约车位需要重新缴纳押金</p>
        <p class="word fcg95 font26 input" v-if="operatingType==1"><input type="number" v-model="money" name="" placeholder="请输入提现金额">元</p>
        <div class="ceng_operating fcw font34" @click="suer()">确定</div>
      </div>
    </div>
    <!--押金充值-->
    <div class="ceng deposit ceng_s fcg95 font26" style="display:none;" @click="close('deposit')">
      <div class="ceng_content centered" @click="emptyClick($event)">
        <div class="title fcb font38">押金充值</div>
        <p class="word fcg95 font26 input"><input type="number" v-model="money" name="" placeholder="请输入充值金额">元</p>
        <div class="ceng_operating fcw font34" @click="deposit_suer()">确定</div>
      </div>
    </div>
    <div class="ceng recharge ceng_s fcg95 font26" style="display:none;" @click="close('recharge')">
      <div class="ceng_content centered" @click="emptyClick($event)">
        <div class="title fcb font38">充值</div>
        <p class="word fcg95 font26 input"><input type="number" v-model="money" name="" placeholder="请输入充值金额">元</p>
        <div class="ceng_operating fcw font34" @click="suer_recharge()">确定</div>
      </div>
    </div>
  </div>
</template>

<script>
module.exports={
	devtool:'cheap-module-source-map',
    data:function(){
      return {
        fansInfo:{},
        isenough:{},
        operatingType:0,//0是押金退还1是体现
        money:""
      }
    },
    mounted:function(){
      
    },
    activated:function(){
      //每次进入
      this.close("ceng")
      this.getFansInfo();
      this.getIsenough();
    },
    created:function(){
      // 组件创建完
    },
    watch:{
      //监视变化
      money:function(){
        if(this.money.length>6)
          this.money=this.money.substr(0,6);
      }
    },
    methods:{
      deposit:function(){
        //押金充值
        this.money="";
        this.show("deposit")
      },
      deposit_suer:function(){
        var that=this;
        if(this.money>=1){
          $.ajax({
            url:urlDir+"/wx/userfunds/recharge",
            type:"POST",
            data:{
              money:this.money,
              moneyBase:this.money,
              type:10
            },
            success:function(res){
              if(res.code==0){
                that.pay(res.data);
              }
            },
            error:function(res){

            }
          })
        }else{
          error_msg("充值金额必须大于1元",2000)
        }
      },
      pay:function(obj){
        var that=this;
        wx.config({
          debug:false,
          appId:obj.appId,
          timestamp:obj.timeStamp+"",
          nonceStr:obj.nonceStr,
          signature:obj.paySign,
          jsApiList: [
              'chooseWXPay'
            ]
        });
        wx.ready(function(){
          if (typeof WeixinJSBridge == "undefined"){
              if( document.addEventListener ){
                 document.addEventListener('WeixinJSBridgeReady', onBridgeReady, false);
              }else if (document.attachEvent){
                 document.attachEvent('WeixinJSBridgeReady', onBridgeReady); 
                 document.attachEvent('onWeixinJSBridgeReady', onBridgeReady);
              }
          }else{
             onBridgeReady();
          }
          function onBridgeReady(){
            //alert('appId:'+resp.appId+'timeStamp'+resp.timeStamp+"nonceStr"+);
            WeixinJSBridge.invoke(
              'getBrandWCPayRequest',{
              "appId":obj.appId,     //公众号名称，由商户传入     
              "timeStamp":obj.timeStamp+"",         //时间戳，自1970年以来的秒数     
              "nonceStr":obj.nonceStr, //随机串     
              "package":obj.package_,
              "signType":obj.signType,    //微信签名方式：     
              "paySign":obj.paySign //微信签名 
              },
              function(res){
                  if(res.err_msg=="get_brand_wcpay_request:ok"){
                    that.close("deposit");
                    that.getIsenough();
                  }     // 使用以上方式判断前端返回,微信团队郑重提示：res.err_msg将在用户支付成功后返回    ok，但并不保证它绝对可靠。 
              }
            );
          }
        });
      },
      recharge:function(){
        //充值
        this.money=""
        this.show("recharge")
      },
      suer_recharge:function(){
        //确定充值调微信支付
      },
      suer:function(){
        var that=this;
        if(this.operatingType==0){
          $.ajax({
            url:urlDir+"/wx/refund/apply/refund",
            type:"POST",
            success:function(res){
              error_msg(res.msg,4000)
              that.close("prompt")
              if(res.code==0){
                that.show("ceng_deposit")
                that.getIsenough()
              }
            },
            error:function(res){

            }
          })
        }else{
          if(this.money<1){
            error_msg("提现金额必须大于1元",2000)
            return true;
          }
          $.ajax({
            url:urlDir+"/wx/refund/apply/withdraw",
            type:"POST",
            data:{
              money:this.money
            },
            success:function(res){
              error_msg(res.msg,2000)
              that.close("prompt")
              if(res.code==0){
                that.show("ceng_deposit")
                that.getIsenough()
              }
            },
            error:function(res){

            }
          })
        }
      },
      withdraw:function(){
        //体现
        var that=this;
        this.operatingType=1
        this.money=""
        this.show("prompt")
      },
      refunds:function(){
        //退押金
        var that=this;
        this.operatingType=0
        this.show("prompt")
      },
      getIsenough:function(){
        var that=this;
        $.ajax({
          url:urlDir+"/wx/userfunds/get",
          type:"GET",
          success:function(res){
            if(res.code==0){
              that.isenough=res.data;
            }
          },
          error:function(res){

          }
        })
      },
      getFansInfo:function(){
        //获取用户信息
        var that=this;
        $.ajax({
          url:urlDir+"/wx/fans/get",
          type:"GET",
          success:function(res){
            if(res.code==0){
              that.fansInfo=res.data;
              localStorage.setItem("fansInfo",toJson(res.data));
              if(res.data.carOwnerStatus!=2&&res.data.carParkAuditStatus!=2){
                error_msg("请先注册",2000)
                that.goRouter("/login");
              }
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
    }
  }
</script>
<style scoped>
.user{width:100%;height:100%;position:relative;padding-bottom:1.1rem;overflow:hidden;overflow-y:auto;}
.info{width:7.02rem;height:4.86rem;background:url("../assets/images/user_bg.png") no-repeat center;background-size:7.02rem 4.86rem;margin:.1rem auto;text-align:center;overflow:hidden;position:relative;border-radius:.16rem;box-shadow:0 0 .1rem .01rem #dae7ed;}
.img{margin-top:.36rem;margin-bottom:.12rem;}
.img img{width:1.8rem;height:1.8rem;border:1px solid #fff;border-radius:50%;overflow:hidden;margin:0 auto;background:#fff;}
.name{height:.6rem;line-height:.6rem;}
.phone{height:.54rem;line-height:.54rem;}
.money{height:1.28rem;width:100%;position:absolute;bottom:0;background:rgba(26,163,223,.4);}
.money:after{content:"";width:1px;height:.9rem;background:#fff;position:absolute;left:50%;top:50%;-webkit-transform:translate(-50%,-50%);transform:translate(-50%,-50%);}
.money span{width:3.5rem;overflow:hidden;}
.money span em{display:block;height:.36rem;line-height:.36rem;}
.money span b{height:.6rem;line-height:.6rem;display:block;margin-top:.2rem;}
.list{width:7.02rem;height:.8rem;line-height:.8rem;padding-left:1.2rem;border-radius:.16rem;box-shadow:0 0 .1rem .01rem #dae7ed;background:#fff url("../assets/images/icon9.png") no-repeat 6.4rem;background-size:.38rem .38rem; margin:.1rem auto 0;position:relative;}
.list img{height:.46rem;left:.4rem;}
/*弹出层*/
.ceng{width:100%;height:100%;position:fixed;top:0;left:0;background:rgba(255,255,255,.5);}
.ceng .ceng_content{width:5.9rem;background:#fff;border-radius:.24rem;padding:0 .46rem;box-shadow:0 0 1rem 0 #cfebf8;}
.ceng_deposit .ceng_content{background:#fff url("../assets/images/yes.png") no-repeat center .4rem;background-size:1.98rem 1.98rem;padding-top:2.38rem;}
.ceng_deposit .title{line-height:.5rem;text-align:center;padding:.34rem 0;}
.ceng_deposit .title b{display:block;}
.ceng_deposit .word{height:.48rem;line-height:.48rem;text-align:center;}
.ceng_deposit .ceng_operating{width:3.7rem;height:.8rem;line-height:.8rem;background:#0098db;margin:.56rem auto;border-radius:.4rem;text-align:center;}

.ceng_s .ceng_content{background:#fff;padding-top:.2rem;}
.ceng_s .title{line-height:.5rem;text-align:center;padding:.34rem 0;}
.ceng_s .title b{display:block;}
.ceng_s .word{line-height:.48rem;text-align:center;}
.ceng_s .input{border:1px solid #f0f0f0;border-radius:.16rem;width:4rem;margin:0 auto;overflow:hidden;}
.ceng_s .input input{padding-left:.1rem;display:inline-block;width:3.5rem;height:.4rem;line-height:.4rem;vertical-align:middle;}
.ceng_s .ceng_operating{width:3.7rem;height:.8rem;line-height:.8rem;background:#0098db;margin:.56rem auto;border-radius:.4rem;text-align:center;}
</style>
