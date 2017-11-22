<template>
  <div class="recharge">
    <div class="chooseMony">
      <div class="l fcg95" :class="chooseMoney==30?'curr':''" @click="chooseMony(30)"><span class="font30">30元</span></div>
      <div class="l fcg95" :class="chooseMoney==40?'curr':''" @click="chooseMony(40)"><span class="font30">40元</span></div>
      <div class="l fcg95" :class="chooseMoney==120?'curr':''" @click="chooseMony(120)"><span class="font30">120元</span></div>
      <div class="l fcg95" :class="chooseMoney==200?'curr':''" @click="chooseMony(200)"><span class="font30">200元</span></div>
      <div class="l fcg95" :class="chooseMoney==300?'curr':''" @click="chooseMony(300)"><span class="font30">300元</span></div>
      <div class="l fcg95" :class="chooseMoney==400?'curr':''" @click="chooseMony(400)"><span class="font30">400元</span></div>
    </div>
    <div class="otherMony fcg95"><input class="font46" type="number" v-model="otherMoney" name="" max="999999" min="1" placeholder="其他金额"><b class="centered_y">元</b></div>
    <div class="weixin"><span>微信支付</span><i class="centered_y"></i></div>
    <div class="checkout font40 fcw" @click="checkout">确认支付</div>
    <!--充值成功-->
    <div class="ceng ceng_result fcg95 font26" style="display:none;">
      <div class="ceng_content centered" @click="emptyClick($event)">
        <div class="title fcb font38">充值成功</div>
        <div class="ceng_operating fcw font34" @click="goBack()">返回</div>
      </div>
    </div>
  </div>
</template>

<script>
module.exports={
	devtool:'cheap-module-source-map',
    data:function(){
      return {
        money:"",
        chooseMoney:"",
        otherMoney:"",
        goodsId:"",
        moneyBase:"",
        moneyRent:"",
        type:"10"
      }
    },
    ready:function(){
      
    },
    activated:function(){
      //每次进入
      this.money="";
      this.otherMoney="";
      this.chooseMoney="";
      var url=getUrl();
      this.goodsId=url.goodsId||"";
      this.moneyBase=url.moneyBase||"";
      this.moneyRent=url.moneyRent||"";
      this.type=url.type||"10"
      if(this.moneyBase>0&&(!this.moneyRent||this.moneyRent==0)){
        this.otherMoney=this.moneyBase*1;
      }
    },
    created:function(){
      // 组件创建完
    },
    watch:{
      //监视变化
      otherMoney:function(){
        var that=this;
        if(this.otherMoney!=""&&this.otherMoney.length<=6){
          this.chooseMoney=0;
        }else{
          this.otherMoney=this.otherMoney.substr(0,6);
          this.chooseMoney=0;
        }
      }
    },
    methods:{
      chooseMony:function(index){
        this.chooseMoney=index;
        this.otherMoney="";
      },
      checkout:function(){
        var that=this;
        if(!(this.chooseMoney>=1||this.otherMoney>=1)){
          error_msg("充值金额必须大于1元",2000)
          return true;
        }
        if(this.type==10){
          this.moneyRent=this.chooseMoney||this.otherMoney
        }
        $.ajax({
            url:urlDir+"/wx/userfunds/recharge",
            type:"POST",
            data:{
              money:this.chooseMoney||this.otherMoney,
              goodsId:this.goodsId,
              moneyBase:this.moneyBase,
              moneyRent:this.moneyRent,
              type:this.type
            },
            success:function(res){
              if(res.code==0){
                that.pay(res.data);
              }
            },
            error:function(res){

            }
          })
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
                    that.show("ceng_result")
                  }     // 使用以上方式判断前端返回,微信团队郑重提示：res.err_msg将在用户支付成功后返回    ok，但并不保证它绝对可靠。 
              }
            );
          }
        });
      },
      goBack:function(){
        this.close("ceng_result")
        history.back();
      },
      goRouter:function(str){
        this.close("ceng")
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
.recharge{width:100%;height:100%;position:relative;overflow:hidden;padding-top:.2rem;}
.chooseMony{overflow:hidden;padding:.26rem .24rem;}
.chooseMony div{width:2.14rem;height:.96rem;box-shadow:0 0 .1rem .01rem #dae7ed;background:#fff;text-align:center;margin-right:.24rem;margin-bottom:.5rem;position:relative;overflow:hidden;line-height:.96rem;border-radius:.16rem;}
.chooseMony div:nth-child(3n){margin-right:0;}
.chooseMony div.curr{background:#0098db;color:#fff;}
.chooseMony div span{display:block;}
.chooseMony div.one span{line-height:.96rem;}
.chooseMony div b{display:block;line-height:.2rem;}
.chooseMony_top div{width:3.36rem;margin-bottom:0;}
.chooseMony_top div:last-child{margin-right:0;}
.otherMony{margin:0 auto;width:7.02rem;height:.98rem;position:relative;box-shadow:0 0 .1rem .01rem #dae7ed;}
.otherMony input{width:100%;height:100%;text-align:center;background:#fff;}
.otherMony b{position:absolute;right:.2rem;}
.weixin{width:100%;height:1.2rem;line-height:1.2rem;background:#fff url("../assets/images/wechat.png") no-repeat .24rem;background-size:.98rem .98rem;padding:0 .24rem 0 1.44rem;position:relative;margin-top:1.5rem;}
.weixin span{display:inline-block;}
.weixin i{width:.42rem;height:.42rem;background:url("../assets/images/icon15.png") no-repeat center;background-size:.4rem .4rem;position:absolute;right:.24rem;}
.checkout{width:4.5rem;height:.88rem;line-height:.88rem;text-align:center;margin:.66rem auto 0;background:#0098db;}
/*弹出层*/
.ceng{width:100%;height:100%;position:absolute;top:0;left:0;background:rgba(255,255,255,.5);}
.ceng .ceng_content{width:5.9rem;background:#fff;border-radius:.24rem;padding:0 .46rem;box-shadow:0 0 1rem 0 #cfebf8;}
/*发布成功*/
.ceng_result .ceng_content{background:#fff url("../assets/images/yes.png") no-repeat center .4rem;background-size:1.98rem 1.98rem;padding-top:2.38rem;}
.ceng_result .title{height:1.2rem;line-height:1.2rem;text-align:center;}
.ceng_result .word{height:.6rem;line-height:.6rem;text-align:center;}
.ceng_result .ceng_operating{width:3.7rem;height:.8rem;line-height:.8rem;background:#0098db;margin:1rem auto 1rem;border-radius:.4rem;text-align:center;}
</style>
