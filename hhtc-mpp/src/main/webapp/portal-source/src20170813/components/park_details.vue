<template>
  <div class="park_details">
    <div class="top font24 fcg48"><i></i>{{fansInfo.carOwnerCommunityName||fansInfo.carParkCommunityName}}</div>
    <div class="content">
      <div class="park_info fcw">
        <div class="title font38">{{parkInfo.carParkNumber}}</div>
        <div class="info"><span class="l font32">车牌号:</span><span class="r font24 add_plate" @click="goRouter('/license_plate')">{{carNumber}}</span></div>
        <div class="info"><span class="l font32">最早入场时间:</span><span class="r font24">{{parkGetDay(parkInfo.publishFromDate)}} {{parkGetTime(parkInfo.publishFromTime)}}</span></div>
        <div class="info"><span class="l font32">最迟出场时间:</span><span class="r font24">{{parkGetDay(parkInfo.publishEndDate)}} {{parkGetTime(parkInfo.publishEndTime)}}</span></div>
        <div class="info"><span class="l font32">停车费:</span><span class="r font24">约 ¥: <em class="font50">{{parkInfo.price*1}}</em></span></div>
        <div class="info"><span class="l font32">押金:</span><span class="r font24">¥: <em class="font50">{{(isenough.money||0)*1}}</em></span></div>
      </div>
      <div class="prompt fcg95">
        <div class="title font32 b">温情提示:</div>
        <p class="font22">1、预约成功后，进场前1小时以上可将已预约车位二次发布到“抢车位”的车位预约列表中;</p>
        <p class="font22">2、请按时离场，超时停车将扣缴押金作为罚金来弥补车位主的损失和支付额外产生的管理费用;</p>
        <p class="font22">3、有任何疑问请拨打电话400-000-001</p>
        <!--<p class="font22">4、</p>-->
      </div>
      <div class="operating fcw font38"><span @click="goRouter('/park')">取消</span><span @click="checkout">预约</span></div>
    </div>
    <!--<div class="ceng ceng_check fcg95 font26" style="display:none">
      <div class="ceng_content centered">
        <div class="title font38 b fcb">预约信息核对</div>
        <p class="word">位置：{{parkInfo.carParkNumber}}</p>
        <p class="word">车牌：{{carNumber}}</p>
        <p class="word">入场时间：{{parkGetDay(parkInfo.publishFromDate)}} {{parkGetTime(parkInfo.publishFromTime)}}</p>
        <p class="word">出场时间：{{parkGetDay(parkInfo.publishEndDate)}} {{parkGetTime(parkInfo.publishEndTime)}}</p>
        <p class="word"></p>
        <div class="check_money">
          <p class="word">本次停车费：<em class="fcb font42">{{parkInfo.price}}</em>元</p>
          <p class="word">+押金(补交)：0（当前押金299）</p>
          <p class="word">=总价：<em class="fcb font42">6</em>元</p>
        </div>
        <div class="ceng_operating fcw font34"><span>取消</span><span>确定</span></div>
      </div>
    </div>-->
    <div class="ceng ceng_result fcg95 font26" style="display:none">
      <div class="ceng_content centered">
        <div class="title fcb font38">预定成功</div>
        <p class="word font26">请按时进场，如有任何疑问</p>
        <p class="word font26">请拨打客服电话：400-000-001</p>
        <div class="ceng_operating fcw font34" @click="goRouter('/order')">查看我的订单</div>
      </div>
    </div>
    <!--钱不够去充值或者微信支付-->
    <div class="ceng nomoney ceng_s fcg95 font26" style="display:none;" @click="close('nomoney')">
      <div class="ceng_content centered" @click="emptyClick($event)">
        <div class="title fcb font38">余额不足</div>
        <div class="word font24">本次需支付{{fundsIsenough.moneyFull||0}}元其中押金{{fundsIsenough.moneyBase||0}}元停车费{{fundsIsenough.moneyRent||0}}元</div>
        <div class="ceng_operating fcw font34"><span @click="goRouter('recharge')">去充值</span><span @click="suer_wx">微信支付</span></div>
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
        inventory:{},
        parkInfo:{},
        isCanAjax:true,
        isenough:{},
        carNumber:"",
        ids:"",
        needId:"",
        payType:2,
        fundsIsenough:{}
      }
    },
    mounted:function(){
      
    },
    activated:function(){
      //每次进入
      this.close("ceng")
      this.getFansInfo();
      this.getInventory();
      this.getIsenoughInfo();
      this.payType=2;
      var url=getUrl();
      if(url.needId){
        this.needId=url.needId;
        this.getParkInfo();
      }else{
        this.parkInfo=toParse(localStorage.getItem("parkDetail"));
      }
      //this.getParkInfo();
      //this.ids=getUrl().ids
    },
    created:function(){
      // 组件创建完
    },
    watch:{
      //监视变化
    },
    methods:{
      getParkInfo:function(){
        var that=this
        $.ajax({
          url:urlDir+"/wx/goods/publish/preorder",
          type:"GET",
          data:{
            needId:this.needId
          },
          success:function(res){
            if(res.code==0){
              that.parkInfo=res.data;
            }
          },
          error:function(res){

          }
        })
      },
      getIsenoughInfo:function(){
        var that=this;
        $.ajax({
          url:urlDir+"/wx/userfunds/deposit/isenough",
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
      checkout:function(){
        var that=this;
        if(this.isCanAjax){
          this.isCanAjax=false;
          $.ajax({
          url:urlDir+"/wx/goods/publish/order",
          type:"GET",
          data:{
            ids:this.parkInfo.ids,
            carNumber:this.carNumber,
            doFromDate:this.parkInfo.publishFromDate,
            doEndDate:this.parkInfo.publishEndDate,
            doFromTime:this.parkInfo.publishFromTime,
            doEndTime:this.parkInfo.publishEndTime,
            payType:this.payType
          },
          success:function(res){
            that.isCanAjax=true;
            if(res.code==0){
              if(that.payType==1){
                that.pay(res.data)
              }else{
                localStorage.removeItem("carNumber")
                that.show("ceng_result")
              }
            }else if(res.code==3009){
              that.fundsIsenough=res.data
              that.show("nomoney");
            }else{
              error_msg(res.msg,2000)
            }
          },
          error:function(res){
            that.isCanAjax=true;
          }
        })
        }
        
      },
      suer_wx:function(){
        //微信支付
        this.payType=1;
        this.checkout();
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
                    that.close("nomoney")
                    that.show("ceng_result")
                    localStorage.removeItem("carNumber")
                  }     // 使用以上方式判断前端返回,微信团队郑重提示：res.err_msg将在用户支付成功后返回    ok，但并不保证它绝对可靠。 
              }
            );
          }
        });
      },
      parkGetDay:function(index){
        //进来格式20170911
        if(index)
        return index.substr(0,4)+"-"+index.substr(4,2)+"-"+index.substr(6,2);
      },
      parkGetTime:function(time){
        //进来格式 930   1700
        return (Math.floor(time/100)>=10?Math.floor(time/100):("0"+Math.floor(time/100)))+":"+(time%100>=10?time%100:("0"+time%100));
      },
      getInventory:function(){
        //获取当前车位的情况
        var that=this;
        $.ajax({
          url:urlDir+"/wx/goods/publish/inventory",
          type:"GET",
          success:function(res){
            if(res.code==0){
              that.inventory=res.data
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
              that.carNumber=localStorage.getItem("carNumber")||res.data.carNumber.split("`")[0]
              localStorage.setItem("fansInfo",toJson(res.data));
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
.park_details{width:100%;height:100%;position:relative;overflow:hidden;}
.park_details .top{width:100%;height:.54rem;line-height:.54rem;text-align:center;background:#fff;position:absolute;top:0;z-index:2;box-shadow:0 0 .1rem .01rem #dae7ed;}
.park_details .top i{display:inline-block;width:.24rem;height:.28rem;background:url("../assets/images/icon.png") no-repeat center;background-size:.24rem .28rem;vertical-align:middle;margin-right:.12rem;}
.park_details .content{background:#f7f3f1;width:100%;height:100%;padding-top:.54rem;position:absolute;top:0;overflow-y:auto;padding-bottom:1rem;}
.park_info{width:7rem;height:6.16rem;background:url("../assets/images/park_info_bg.png") no-repeat center;background-size:7rem 6.16rem;margin:.32rem auto .2rem;}
.park_info .title{text-align:center;height:.98rem;line-height:.98rem;margin-bottom:.26rem;}
.park_info .info{height:.96rem;line-height:.96rem;overflow:hidden;padding:0 .56rem 0 1.2rem;background:url("../assets/images/icon1.png") no-repeat .34rem;background-size:.6rem .6rem;}
.park_info .info:nth-child(3){background-image:url("../assets/images/icon2.png");}
.park_info .info:nth-child(4){background-image:url("../assets/images/icon3.png");}
.park_info .info:nth-child(5){background-image:url("../assets/images/icon4.png");}
.park_info .info:nth-child(6){background-image:url("../assets/images/icon4.png");}
.park_info .add_plate{background:url("../assets/images/add_plate_icon.png") no-repeat right;background-size:.16rem .24rem;padding-right:.3rem;}
.prompt{padding:0 .8rem;}
.prompt .title{height:1.16rem;line-height:1.16rem;}
.prompt p{line-height:.34rem;text-indent:-.36rem;padding-left:.36rem;}
.operating{height:1.96rem;line-height:1.96rem;text-align:center;}
.operating span{display:inline-block;width:2.64rem;height:.88rem;line-height:.88rem;border-radius:.44rem;background:#0098db;margin-right:.62rem;}
.operating span:last-child{margin:0;}
/*弹出层*/
.ceng{width:100%;height:100%;position:absolute;top:0;left:0;background:rgba(255,255,255,.5);}
.ceng .ceng_content{width:5.9rem;background:#fff;border-radius:.24rem;padding:0 .46rem;box-shadow:0 0 1rem 0 #cfebf8;}
/*信息核对*/
.ceng_check .title{height:1.14rem;line-height:1.14rem;margin-bottom:.4rem;text-align:center;}
.ceng_check .word{height:.56rem;line-height:.56rem;}
.ceng_operating{height:1.76rem;line-height:1.76rem;text-align:center;}
.ceng_operating span{display:inline-block;width:1.9rem;height:.8rem;line-height:.8rem;background:#0098db;border-radius:.4rem;margin-right:.38rem;}
.ceng_operating span:last-child{margin:0;}
/*预约成功*/
.ceng_result .ceng_content{background:#fff url("../assets/images/yes.png") no-repeat center .4rem;background-size:1.98rem 1.98rem;padding-top:2.38rem;}
.ceng_result .title{height:1.2rem;line-height:1.2rem;text-align:center;}
.ceng_result .word{height:.6rem;line-height:.6rem;text-align:center;}
.ceng_result .ceng_operating{width:3.7rem;height:.8rem;line-height:.8rem;background:#0098db;margin:1.7rem auto .56rem;border-radius:.4rem;}
/*核对*/
.ceng_s .ceng_content{background:#fff url("../assets/images/check_icon.png") no-repeat center .4rem;background-size:2.2rem 2.2rem;padding-top:2.6rem;}
.ceng_s .title{height:1.26rem;line-height:1.26rem;text-align:center;margin-top:.16rem;}
.ceng_s .ceng_operating{height:1.76rem;line-height:1.76rem;text-align:center;}
.ceng_s .ceng_operating span{display:inline-block;width:1.9rem;height:.8rem;line-height:.8rem;background:#0098db;border-radius:.4rem;margin-right:.38rem;}
.ceng_s .ceng_operating span:last-child{margin:0;}
.ceng_s .word{line-height:.54rem;}
.ceng_s .word span.left{display:inline-block;width:1.6rem;text-align:right;}
</style>
