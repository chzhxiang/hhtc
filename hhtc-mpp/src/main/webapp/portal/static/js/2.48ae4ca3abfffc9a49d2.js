webpackJsonp([2],{114:function(e,t){e.exports={devtool:"cheap-module-source-map",data:function(){return{fansInfo:{},inventory:{},parkInfo:{},isCanAjax:!0,isenough:{},carNumber:"",ids:"",needId:"",payType:2,fundsIsenough:{},excludeDays:[],week:["周日","周一","周二","周三","周四","周五","周六"]}},mounted:function(){},activated:function(){this.parkInfo=toParse(localStorage.getItem("parkDetail")),this.close("ceng"),this.getFansInfo(),this.payType=2,this.showExclude(this.parkInfo)},created:function(){},watch:{},methods:{showExclude:function(e){var t=e.publishFromDates.split("-"),a=this;if(this.excludeDays=[],this.isShowexclude=!0,t.length>1){var n=this.getParkDay(t[0]).replace(/-/g,"/"),i=this.getParkDay(t[t.length-1]).replace(/-/g,"/");console.log(n+",,"+i);for(var r=new Date(n).getTime(),o=new Date(i).getTime(),s="",c=r;c<=o;c+=864e5)s=getTimes(c,"YMD",!1),console.log(s),-1==t.indexOf(s.replace(/-/g,""))&&a.excludeDays.push(s+"("+a.week[new Date(s.replace(/-/g,"/")).getDay()]+")")}},getParkInfo:function(){var e=this;$.ajax({url:urlDir+"/wx/goods/publish/preorder",type:"GET",data:{needId:this.needId},success:function(t){0==t.code&&(e.parkInfo=t.data)},error:function(e){}})},getIsenoughInfo:function(e){var t=this;$.ajax({url:urlDir+"/wx/userfunds/deposit/isenough",type:"GET",data:{ids:this.parkInfo.ids,carNumber:e},success:function(e){0==e.code&&(t.isenough=e.data)},error:function(e){}})},checkout:function(){var e=this;this.isCanAjax&&(this.isCanAjax=!1,$.ajax({url:urlDir+"/wx/goods/publish/order",type:"GET",data:{ids:this.parkInfo.ids,carNumber:this.carNumber},success:function(t){e.isCanAjax=!0,0==t.code?(localStorage.removeItem("carNumber"),e.show("ceng_result")):3009==t.code?(e.fundsIsenough=t.data,e.show("nomoney")):error_msg(t.msg,2e3)},error:function(t){e.isCanAjax=!0}}))},suer_wx:function(){this.payType=1,this.checkout()},pay:function(e){var t=this;wx.config({debug:!1,appId:e.appId,timestamp:e.timeStamp+"",nonceStr:e.nonceStr,signature:e.paySign,jsApiList:["chooseWXPay"]}),wx.ready(function(){function a(){WeixinJSBridge.invoke("getBrandWCPayRequest",{appId:e.appId,timeStamp:e.timeStamp+"",nonceStr:e.nonceStr,package:e.package_,signType:e.signType,paySign:e.paySign},function(e){"get_brand_wcpay_request:ok"==e.err_msg&&(t.close("nomoney"),t.show("ceng_result"),localStorage.removeItem("carNumber"))})}"undefined"==typeof WeixinJSBridge?document.addEventListener?document.addEventListener("WeixinJSBridgeReady",a,!1):document.attachEvent&&(document.attachEvent("WeixinJSBridgeReady",a),document.attachEvent("onWeixinJSBridgeReady",a)):a()})},parkGetBeginDay:function(e){if("{}"!=toJson(e)){var t=e.publishFromDates.split("-");return this.getParkDay(t[0])+" "+this.getParkTime(e.publishFromTime)}},parkGetEndDay:function(e){if("{}"!=toJson(e)){var t=e.publishFromDates.split("-"),a=this.getParkDay(t[t.length-1]);return 1*e.publishEndTime<1*e.publishFromTime&&(a=getTimes(new Date(a.replace(/-/g,"/")).getTime()+864e5,"YMD",!1)),a+" "+this.getParkTime(e.publishEndTime)}},getParkDay:function(e){if(e)return e.substr(0,4)+"-"+e.substr(4,2)+"-"+e.substr(6,2)},getParkTime:function(e){return(Math.floor(e/100)>=10?Math.floor(e/100):"0"+Math.floor(e/100))+":"+(e%100>=10?e%100:"0"+e%100)},getInventory:function(e){var t=this;$.ajax({url:urlDir+"/wx/goods/publish/order/inventory",type:"GET",data:{communityId:e},success:function(e){0==e.code&&(t.inventory=e.data)},error:function(e){}})},getFansInfo:function(){var e=this;$.ajax({url:urlDir+"/wx/fans/get",type:"GET",success:function(t){0==t.code&&(e.fansInfo=t.data,e.carNumber=localStorage.getItem("carNumber")||t.data.carNumber.split("`")[0],localStorage.setItem("fansInfo",toJson(t.data)),e.getInventory(t.data.carOwnerCommunityId||t.data.carParkCommunityId))},error:function(e){}})},goRouter:function(e){this.$router.push({path:e})},show:function(e){$("."+e).css("display","block")},close:function(e){$("."+e).css("display","none")},emptyClick:function(e){e.preventDefault(),e.stopPropagation()}}}},133:function(e,t,a){t=e.exports=a(11)(!1),t.push([e.i,".park_details[data-v-1949fd8a]{width:100%;height:100%;position:relative;overflow:hidden}.park_details .top[data-v-1949fd8a]{width:100%;height:.54rem;line-height:.54rem;text-align:center;background:#fff;position:absolute;top:0;z-index:2;box-shadow:0 0 .1rem .01rem #dae7ed}.park_details .top i[data-v-1949fd8a]{display:inline-block;width:.24rem;height:.28rem;background:url("+a(60)+") no-repeat 50%;background-size:.24rem .28rem;vertical-align:middle;margin-right:.12rem}.park_details .content[data-v-1949fd8a]{background:#f7f3f1;width:100%;height:100%;padding-top:.54rem;position:absolute;top:0;overflow-y:auto;padding-bottom:1rem}.park_info[data-v-1949fd8a]{width:7rem;height:5.2rem;background:url("+a(75)+") no-repeat 50%;background-size:7rem 5.2rem;margin:.32rem auto .2rem}.park_info .title[data-v-1949fd8a]{text-align:center;height:.98rem;line-height:.98rem;margin-bottom:.26rem}.park_info .info[data-v-1949fd8a]{height:.96rem;line-height:.96rem;overflow:hidden;padding:0 .56rem 0 1.2rem;background:url("+a(72)+") no-repeat .34rem;background-size:.6rem .6rem}.park_info .info[data-v-1949fd8a]:nth-child(3){background-image:url("+a(73)+")}.park_info .info[data-v-1949fd8a]:nth-child(4){background-image:url("+a(74)+")}.park_info .info[data-v-1949fd8a]:nth-child(5){background-image:url("+a(65)+")}.park_info .info[data-v-1949fd8a]:nth-child(6){background-image:url("+a(65)+")}.park_info .add_plate[data-v-1949fd8a]{background:url("+a(71)+") no-repeat 100%;background-size:.16rem .24rem;padding-right:.3rem}.prompt[data-v-1949fd8a]{padding:0 .8rem}.prompt .title[data-v-1949fd8a]{height:1.16rem;line-height:1.16rem}.prompt p[data-v-1949fd8a]{line-height:.34rem;text-indent:-.36rem;padding-left:.36rem}.operating[data-v-1949fd8a]{height:1.96rem;line-height:1.96rem;text-align:center}.operating span[data-v-1949fd8a]{display:inline-block;width:2.64rem;height:.88rem;line-height:.88rem;border-radius:.44rem;background:#0098db;margin-right:.62rem}.operating span[data-v-1949fd8a]:last-child{margin:0}.ceng[data-v-1949fd8a]{width:100%;height:100%;position:absolute;top:0;left:0;background:hsla(0,0%,100%,.5)}.ceng .ceng_content[data-v-1949fd8a]{width:5.9rem;background:#fff;border-radius:.24rem;padding:0 .46rem;box-shadow:0 0 1rem 0 #cfebf8}.ceng_check .title[data-v-1949fd8a]{height:1.14rem;line-height:1.14rem;margin-bottom:.4rem;text-align:center}.ceng_check .word[data-v-1949fd8a]{height:.56rem;line-height:.56rem}.ceng_operating[data-v-1949fd8a]{height:1.76rem;line-height:1.76rem;text-align:center}.ceng_operating span[data-v-1949fd8a]{display:inline-block;width:1.9rem;height:.8rem;line-height:.8rem;background:#0098db;border-radius:.4rem;margin-right:.38rem}.ceng_operating span[data-v-1949fd8a]:last-child{margin:0}.ceng_result .ceng_content[data-v-1949fd8a]{background:#fff url("+a(53)+") no-repeat center .4rem;background-size:1.98rem 1.98rem;padding-top:2.38rem}.ceng_result .title[data-v-1949fd8a]{height:1.2rem;line-height:1.2rem;text-align:center}.ceng_result .word[data-v-1949fd8a]{height:.6rem;line-height:.6rem;text-align:center}.ceng_result .ceng_operating[data-v-1949fd8a]{width:3.7rem;height:.8rem;line-height:.8rem;background:#0098db;margin:1.7rem auto .56rem;border-radius:.4rem}.ceng_s .ceng_content[data-v-1949fd8a]{background:#fff url("+a(58)+") no-repeat center .4rem;background-size:2.2rem 2.2rem;padding-top:2.6rem}.ceng_s .title[data-v-1949fd8a]{height:1.26rem;line-height:1.26rem;text-align:center;margin-top:.16rem}.ceng_s .ceng_operating[data-v-1949fd8a]{height:1.76rem;line-height:1.76rem;text-align:center}.ceng_s .ceng_operating span[data-v-1949fd8a]{display:inline-block;width:1.9rem;height:.8rem;line-height:.8rem;background:#0098db;border-radius:.4rem;margin-right:.38rem}.ceng_s .ceng_operating span[data-v-1949fd8a]:last-child{margin:0}.ceng_s .word[data-v-1949fd8a]{line-height:.54rem}.ceng_s .word span.left[data-v-1949fd8a]{display:inline-block;width:1.6rem;text-align:right}.excludeday[data-v-1949fd8a]{line-height:.5rem;padding:0 .3rem}",""])},171:function(e,t,a){var n=a(133);"string"==typeof n&&(n=[[e.i,n,""]]),n.locals&&(e.exports=n.locals);a(12)("0f4694b8",n,!0)},240:function(e,t){e.exports={render:function(){var e=this,t=e.$createElement,a=e._self._c||t;return a("div",{staticClass:"park_details"},[a("div",{staticClass:"top font24 fcg48"},[a("i"),e._v(e._s(e.fansInfo.carOwnerCommunityName||e.fansInfo.carParkCommunityName))]),e._v(" "),a("div",{staticClass:"content"},[a("div",{staticClass:"park_info fcw"},[a("div",{staticClass:"title font38"},[e._v(e._s(e._f("removedSemicolon")(e.parkInfo.carParkNumber)))]),e._v(" "),a("div",{staticClass:"info"},[a("span",{staticClass:"l font32"},[e._v("车牌号:")]),a("span",{staticClass:"r font24 add_plate",on:{click:function(t){e.goRouter("/license_plate")}}},[e._v(e._s(e._f("carShow")(e.carNumber)))])]),e._v(" "),a("div",{staticClass:"info"},[a("span",{staticClass:"l font32"},[e._v("最早入场时间:")]),a("span",{staticClass:"r font24"},[e._v(e._s(e.parkGetBeginDay(e.parkInfo)))])]),e._v(" "),a("div",{staticClass:"info"},[a("span",{staticClass:"l font32"},[e._v("最迟出场时间:")]),a("span",{staticClass:"r font24"},[e._v(e._s(e.parkGetEndDay(e.parkInfo)))])]),e._v(" "),a("div",{staticClass:"info"},[a("span",{staticClass:"l font32"},[e._v("停车费:")]),a("span",{staticClass:"r font24"},[e._v("约 ¥ "),a("em",{staticClass:"font50"},[e._v(e._s(1*e.parkInfo.price))])])])]),e._v(" "),e.excludeDays.length>0?a("div",{staticClass:"excludeday fcgec"},[e._v("排除日期: "+e._s(e.excludeDays.join(";")))]):e._e(),e._v(" "),e._m(0),e._v(" "),a("div",{staticClass:"operating fcw font38"},[a("span",{on:{click:function(t){e.goRouter("/park")}}},[e._v("取消")]),a("span",{on:{click:e.checkout}},[e._v("预约")])])]),e._v(" "),a("div",{staticClass:"ceng ceng_result fcg95 font26",staticStyle:{display:"none"}},[a("div",{staticClass:"ceng_content centered"},[a("div",{staticClass:"title fcb font38"},[e._v("预定成功")]),e._v(" "),a("p",{staticClass:"word font26"},[e._v("请按时进场，如有任何疑问")]),e._v(" "),a("p",{staticClass:"word font26"},[e._v("请拨打客服电话：15023194640")]),e._v(" "),a("div",{staticClass:"ceng_operating fcw font34",on:{click:function(t){e.goRouter("/order")}}},[e._v("查看我的订单")])])]),e._v(" "),a("div",{staticClass:"ceng nomoney ceng_s fcg95 font26",staticStyle:{display:"none"},on:{click:function(t){e.close("nomoney")}}},[a("div",{staticClass:"ceng_content centered",on:{click:function(t){e.emptyClick(t)}}},[a("div",{staticClass:"title fcb font38"},[e._v("余额不足")]),e._v(" "),a("div",{staticClass:"word font26",staticStyle:{"text-align":"center"}},[e._v("本次还需支付"+e._s(1*e.fundsIsenough.moneyFull||0)+"元")]),e._v(" "),a("div",{staticClass:"ceng_operating fcw font34"},[a("span",{on:{click:function(t){e.goRouter("/recharge?type=12&moneyBase="+e.fundsIsenough.moneyBase+"&moneyRent="+e.fundsIsenough.moneyRent)}}},[e._v("去充值")])])])])])},staticRenderFns:[function(){var e=this,t=e.$createElement,a=e._self._c||t;return a("div",{staticClass:"prompt fcg95"},[a("div",{staticClass:"title font32 b"},[e._v("温情提示:")]),e._v(" "),a("p",{staticClass:"font22"},[e._v("1、预约成功后，最早入场时间前1小时以上可将已预约车位二次发布到“抢车位”的车位预约列表中;")]),e._v(" "),a("p",{staticClass:"font22"},[e._v("2、请按时离场，超时停车将扣缴押金作为罚金来弥补车位主的损失和支付额外产生的管理费用;")]),e._v(" "),a("p",{staticClass:"font22"},[e._v("3、有任何疑问请拨打电话15023194640")])])}]}},38:function(e,t,a){function n(e){a(171)}var i=a(3)(a(114),a(240),n,"data-v-1949fd8a",null);e.exports=i.exports},53:function(e,t,a){e.exports=a.p+"static/img/yes.1465027.png"},58:function(e,t,a){e.exports=a.p+"static/img/check_icon.4a429cf.png"},60:function(e,t,a){e.exports=a.p+"static/img/icon.66d3b8d.png"},65:function(e,t,a){e.exports=a.p+"static/img/icon4.c0ae2c4.png"},71:function(e,t,a){e.exports=a.p+"static/img/add_plate_icon.451bbb6.png"},72:function(e,t,a){e.exports=a.p+"static/img/icon1.b2f54ba.png"},73:function(e,t,a){e.exports=a.p+"static/img/icon2.8c7d97a.png"},74:function(e,t,a){e.exports=a.p+"static/img/icon3.e527cf8.png"},75:function(e,t,a){e.exports=a.p+"static/img/park_info_bg.e9effd2.png"}});