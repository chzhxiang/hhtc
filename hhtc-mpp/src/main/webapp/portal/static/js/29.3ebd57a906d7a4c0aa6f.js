webpackJsonp([29],{101:function(e,t){e.exports={devtool:"cheap-module-source-map",data:function(){return{del_id:"",type:"",nowIndex:-1,orderType:"",orderList:[],page:1}},mounted:function(){},activated:function(){this.page=1,this.close("ceng"),$(".tab li")[0].click()},created:function(){},watch:{del_id:function(){localStorage.setItem("noworder",this.del_id)}},methods:{changeType:function(e){switch(this.orderType=e+"",this.getOrderList(),e+""){case"":return"";case"7":return this.close("reserved"),this.close("under_way"),this.close("finish"),this.show("release"),"发布中";case"8":return this.show("reserved"),this.close("under_way"),this.close("finish"),this.close("release"),"已预订";case"9":return this.close("reserved"),this.show("under_way"),this.close("finish"),this.close("release"),"进行中";case"10":return this.close("reserved"),this.close("under_way"),this.show("finish"),this.close("release"),"已完成"}},getOrderList:function(){var e=this;e.orderList=[],"7"==e.orderType?$.ajax({url:urlDir+"/wx/market/post/getorder",type:"GET",data:{},success:function(t){0==t.code?e.orderList=t.data:error_msg(t.code+"错误 "+t.msg,2e3)},error:function(e){error_msg("网络错误",2e3)}}):"8"==e.orderType?$.ajax({url:urlDir+"/wx/market/transaction/get/order",type:"GET",data:{type:1},success:function(t){0==t.code?e.orderList=t.data:error_msg(t.code+"错误 "+t.msg,2e3)},error:function(e){error_msg("网络错误",2e3)}}):"9"==e.orderType?$.ajax({url:urlDir+"/wx/market/transaction/get/order",type:"GET",data:{type:2},success:function(t){0==t.code?e.orderList=t.data:error_msg(t.code+"错误 "+t.msg,2e3)},error:function(e){error_msg("网络错误",2e3)}}):"10"==e.orderType&&$.ajax({url:urlDir+"/wx/order/gethistory",type:"GET",data:{pageNo:e.page},success:function(t){0==t.code?(e.orderList=t.data,1==e.page?e.orderList=t.data:t.data.forEach(function(t,i){e.orderList.push({communityName:t.communityName,carParkNumber:t.carParkNumber,begintime:t.begintime,endtime:t.endtime,price:t.price,phone:t.price})})):error_msg(t.code+"错误 "+t.msg,2e3)},error:function(e){error_msg("网络错误",2e3)}}),e.orderList=[{communityName:"龙湖原山A1-310",begintime:"17-11-13 07:00",Defaultprice:2,endtime:"17-11-13 17:00",price:11,phone:"15630322745",flog:0,orderID:22},{position:"龙湖原山A1-310",begintime:"17-11-13 07:00",endtime:"17-11-13 17:00",benefit:11,phone:"15630322745",flog:1,orderID:212},{position:"龙湖原山A1-310",begintime:"17-11-13 07:00",endtime:"17-11-13 17:00",benefit:11,phone:"15630322745",flog:1,orderID:25},{position:"龙湖原山A1-310",begintime:"17-11-13 07:00",endtime:"17-11-13 17:00",benefit:11,phone:"15630322745",flog:0,orderID:29},{position:"龙湖原山A1-310",begintime:"17-11-13 07:00",endtime:"17-11-13 17:00",benefit:11,phone:"15630322745",flog:0,orderID:29},{position:"龙湖原山A1-310",begintime:"17-11-13 07:00",endtime:"17-11-13 17:00",benefit:11,phone:"15630322745",flog:0,orderID:29},{position:"龙湖原山A1-310",begintime:"17-11-13 07:00",endtime:"17-11-13 17:00",benefit:11,phone:"15630322745",flog:0,orderID:29},{position:"龙湖原山A1-310",begintime:"17-11-13 07:00",endtime:"17-11-13 17:00",benefit:11,phone:"15630322745",flog:0,orderID:29},{position:"龙湖原山A1-310",begintime:"17-11-13 07:00",endtime:"17-11-13 17:00",benefit:11,phone:"15630322745",flog:0,orderID:29},{position:"11111111-310",begintime:"17-11-13 07:00",endtime:"17-11-13 17:00",benefit:11,phone:"15630322745",flog:0,orderID:29}]},del:function(e){var t=this;"7"==t.orderType&&$.ajax({url:urlDir+"/wx/market/post/cancel",type:"POST",data:{orderid:t.del_id},success:function(i){0==i.code?(t.orderList.splice(e,1),t.close("ceng")):error_msg(i.code+" 错误 "+i.msg,2e3)},error:function(e){error_msg("网络错误",2e3)}}),"8"==t.orderType&&$.ajax({url:urlDir+"/wx/market/transaction /cancel",type:"POST",data:{orderid:t.del_id,type:t.type},success:function(i){0==i.code?(t.orderList.splice(e,1),t.close("ceng")):error_msg(i.code+" 错误 "+i.msg,2e3)},error:function(e){error_msg("网络错误",2e3)}})},getType:function(e){switch(e){case 0:case 1:return{word:"支付中",pd:!1,cssWord:"fcr"};case 2:return{word:"支付成功",pd:!1,cssWord:"fcr"};case 3:return{word:"支付失败",pd:!1,cssWord:"fcr"};case 4:return{word:"已关闭",pd:!0,cssWord:"fcr"};case 5:return{word:"转入退款",pd:!1,cssWord:"fcg"};case 6:return{word:"已撤销",pd:!1,cssWord:"fco"};case 9:return{word:"已转租",pd:!1,cssWord:"fcb"};case 99:return{word:"已完成",pd:!1,cssWord:"fcg"}}return{word:"",pd:!1,cssWord:""}},goRouter:function(e){this.$router.push({path:e})},show:function(e,t){$("."+e).css("display","block"),$("."+t).css("display","block")},close:function(e){$("."+e).css("display","none"),$(".1").css("display","none"),$(".2").css("display","none"),$(".3").css("display","none")},emptyClick:function(e){e.preventDefault(),e.stopPropagation()}}}},149:function(e,t,i){t=e.exports=i(11)(!1),t.push([e.i,".order[data-v-d9822ffe]{width:100%;height:100%;overflow:hidden;background-color:#ececec}header[data-v-d9822ffe]{width:100%;height:.8rem;color:#fff;background-color:#75859f;line-height:.8rem;text-align:center}header span[data-v-d9822ffe]{display:inline-block;position:absolute;left:.2rem;top:.2rem;width:.4rem;height:.4rem;background:url("+i(53)+');background-size:contain}.orange[data-v-d9822ffe]{color:#ff4500}.tab[data-v-d9822ffe]{width:100%;height:1rem;background:#808ea6;padding-top:.2rem}.tab ul[data-v-d9822ffe]{overflow:hidden;width:100%}.tab ul li[data-v-d9822ffe]{float:left;width:25%;text-align:center;position:relative}.tab ul li[data-v-d9822ffe]:after{content:"";display:inline-block;width:.02rem;height:.6rem;margin-top:.1rem;right:0;background:#303030;position:absolute}.tab ul li a[data-v-d9822ffe]{width:1.3rem;height:.8rem;text-align:center;line-height:.8rem;display:inline-block;color:#fff}.tab ul li[data-v-d9822ffe]:last-child:after{display:none}.tab ul li.curr a[data-v-d9822ffe]{border:none;border-bottom:.08rem solid #fff}.scroll[data-v-d9822ffe]{width:100%;height:88%;overflow-y:auto;padding-bottom:.1rem}.item[data-v-d9822ffe]{width:7.02rem;min-height:3rem;background-color:#fff;margin:.4rem auto;text-align:center;overflow:hidden;position:relative;border-radius:.16rem;box-shadow:0 0 .1rem .01rem #dae7ed}.item>div[data-v-d9822ffe]{width:100%;overflow:hidden;text-align:left;height:.666rem;line-height:.366rem}.info[data-v-d9822ffe]{line-height:.8rem}.info[data-v-d9822ffe],.title[data-v-d9822ffe]{display:inline-block;vertical-align:top;height:.8rem}.title[data-v-d9822ffe]{text-align:left;margin:.2rem}.btns[data-v-d9822ffe]{margin-top:.2rem}.word[data-v-d9822ffe]{line-height:.48rem;text-align:center}.item .btns div[data-v-d9822ffe]{text-align:center}.btn[data-v-d9822ffe]{display:inline-block;color:#fff;height:.54rem;text-align:center;width:2.6rem;line-height:.54rem;background-color:skyblue;vertical-align:top;border-radius:.1rem;margin:0 .1rem}.del[data-v-d9822ffe]{background-color:red}.ceng[data-v-d9822ffe]{width:100%;height:100%;position:fixed;bottom:0;left:0}.center div[data-v-d9822ffe]{display:none;text-align:center}.center div p[data-v-d9822ffe]{text-align:center;margin:.4rem auto}.center div a[data-v-d9822ffe]{margin:.2rem auto}.sub-park[data-v-d9822ffe]{width:100%;height:100%;background-color:#777;opacity:.8;position:fixed;bottom:0;left:0}.center[data-v-d9822ffe]{position:absolute;width:5rem;min-height:2.2rem;background-color:#fff;top:4.2rem;left:1.2rem;margin:0 auto;border-radius:.2rem}.center div .dele_btn[data-v-d9822ffe]{display:inline-block;color:#fff;height:.54rem;text-align:center;width:1.6rem;line-height:.54rem;background-color:skyblue;border-radius:.1rem;margin:0 .2rem .2rem}',""])},186:function(e,t,i){var s=i(149);"string"==typeof s&&(s=[[e.i,s,""]]),s.locals&&(e.exports=s.locals);i(12)("708c1ae2",s,!0)},258:function(e,t){e.exports={render:function(){var e=this,t=e.$createElement,i=e._self._c||t;return i("div",{staticClass:"order"},[i("header",{staticClass:"font30"},[i("span",{on:{click:function(t){e.goRouter("/userinfor")}}}),e._v(" 我的订单\n  ")]),e._v(" "),i("div",{staticClass:"tab"},[i("ul",[i("li",{class:7==e.orderType?"curr":"",on:{click:function(t){e.changeType(7)}}},[i("a",{staticClass:"font28"},[e._v("发布中")])]),e._v(" "),i("li",{class:8==e.orderType?"curr":"",on:{click:function(t){e.changeType(8)}}},[i("a",{staticClass:"font28"},[e._v("已预订")])]),e._v(" "),i("li",{class:9==e.orderType?"curr":"",on:{click:function(t){e.changeType(9)}}},[i("a",{staticClass:"font28"},[e._v("进行中")])]),e._v(" "),i("li",{class:10==e.orderType?"curr":"",on:{click:function(t){e.changeType(10)}}},[i("a",{staticClass:"font28"},[e._v("已完成")])])])]),e._v(" "),i("div",{staticClass:"scroll"},[e._l(e.orderList,function(t,s){return i("div",{staticClass:"release",staticStyle:{display:"block"}},[i("div",{staticClass:"item"},[i("div",[i("span",{staticClass:"title font28"},[e._v("车位信息：")]),i("span",{staticClass:"info font28"},[e._v(e._s(t.communityName)+e._s(t.carParkNumber))])]),e._v(" "),i("div",[i("span",{staticClass:"title font28"},[e._v("出租时间：")]),i("span",{staticClass:"info font28"},[e._v(e._s(t.publishFromTime)+"至"+e._s(t.publishEndTime))])]),e._v(" "),i("div",[i("span",{staticClass:"title font28"},[e._v("预期收入：")]),i("span",{staticClass:"info font28 orange"},[e._v("¥"+e._s(t.price))])]),e._v(" "),i("div",{staticClass:"btns"},[i("div",[i("a",{staticClass:"btn font26 del R",on:{click:function(i){!function(){e.nowIndex=s,e.del_id=t.orderID,e.show("ceng",2)}()}}},[e._v("取消订单\n            ")])])])])])}),e._v(" "),e._l(e.orderList,function(t,s){return i("div",{staticClass:"reserved",staticStyle:{display:"none"}},[i("div",{staticClass:"item"},[i("div",[i("span",{staticClass:"title font28"},[e._v("车位信息：")]),i("span",{staticClass:"info font28"},[e._v(e._s(t.communityName)+e._s(t.carParkNumber))])]),e._v(" "),i("div",[i("span",{staticClass:"title font28"},[e._v("出租时间：")]),i("span",{staticClass:"info font28"},[e._v(e._s(t.begintime)+"至"+e._s(t.endtime))])]),e._v(" "),i("div",[i("span",{staticClass:"title font28"},[e._v("预期收入：")]),i("span",{staticClass:"info font28 orange"},[e._v("¥"+e._s(t.price))])]),e._v(" "),i("div",{staticClass:"phone"},[i("span",{staticClass:"title font28"},[e._v("对方手机号：")]),i("span",{staticClass:"info font28"},[e._v(e._s(t.phone))])]),e._v(" "),i("div",{staticClass:"btns"},[i("div",[i("a",{staticClass:"btn font26 del R",on:{click:function(i){!function(){e.nowIndex=s,e.del_id=t.orderID,e.type=t.type,e.show("ceng",2)}()}}},[e._v("取消订单")])])])])])}),e._v(" "),e._l(e.orderList,function(t,s){return i("div",{staticClass:"under_way",staticStyle:{display:"none"}},[i("div",{staticClass:"item"},[i("div",[i("span",{staticClass:"title font28"},[e._v("车位信息：")]),i("span",{staticClass:"info font28"},[e._v(e._s(t.communityName)+e._s(t.carParkNumber))])]),e._v(" "),i("div",[i("span",{staticClass:"title font28"},[e._v("出租时间：")]),i("span",{staticClass:"info font28"},[e._v(e._s(t.begintime)+"至"+e._s(t.endtime))])]),e._v(" "),i("div",[i("span",{staticClass:"title font28"},[e._v("预期收入：")]),i("span",{staticClass:"info font28 orange"},[e._v("¥"+e._s(t.price))])]),e._v(" "),i("div",{staticClass:"phone"},[i("span",{staticClass:"title font28"},[e._v("车主手机号：")]),i("span",{staticClass:"info font28"},[e._v(e._s(t.phone))])]),e._v(" "),i("div",{staticClass:"btns"},[i("div",[i("a",{staticClass:"btn font26 L",style:t.Defaultprice?"display:inline-block":"display:none",on:{click:function(t){!function(){e.nowIndex=s,e.show("ceng",3)}()}}},[e._v("\n              订单结算")]),e._v(" "),i("a",{staticClass:"btn font26 del R",on:{click:function(i){!function(){e.del_id=t.orderID,e.goRouter("/feedback")}()}}},[e._v("\n              订单申述")])])])])])}),e._v(" "),e._l(e.orderList,function(t,s){return i("div",{staticClass:"finish",staticStyle:{display:"none"}},[i("div",{staticClass:"item"},[i("div",[i("span",{staticClass:"title font28"},[e._v("车位信息：")]),i("span",{staticClass:"info font28"},[e._v(e._s(t.communityName)+e._s(t.carParkNumber))])]),e._v(" "),i("div",[i("span",{staticClass:"title font28"},[e._v("出租时间：")]),i("span",{staticClass:"info font28"},[e._v(e._s(t.begintime)+"至"+e._s(t.endtime))])]),e._v(" "),i("div",[i("span",{staticClass:"title font28"},[e._v("预期收入：")]),i("span",{staticClass:"info font28 orange"},[e._v("¥"+e._s(t.price))])]),e._v(" "),i("div",{staticClass:"phone"},[i("span",{staticClass:"title font28"},[e._v("车主手机号：")]),i("span",{staticClass:"info font28"},[e._v(e._s(t.phone))])]),e._v(" "),i("div",{staticClass:"btns"},[i("div",[i("a",{staticClass:"btn font26 del R",style:t.Defaultprice?"display:inline-block":"display:none",on:{click:function(i){!function(){e.nowIndex=s,e.del_id=t.orderID,e.type=t.type,e.show("ceng",1)}()}}},[e._v("超时补款")])])])])])})],2),e._v(" "),i("div",{staticClass:"ceng"},[i("div",{staticClass:"sub-park",on:{click:function(t){e.close("ceng")}}}),e._v(" "),i("div",{staticClass:"center",staticStyle:{display:"block"},on:{click:function(t){e.emptyClick(t)}}},[i("div",{staticClass:"1"},[i("p",{staticClass:"fcb font36"},[e._v("超时补款")]),e._v(" "),i("p",{staticClass:"word fcg95 font26"},[e._v("超时补款将从押金中扣取相应的金额，确认补款？")]),e._v(" "),i("a",{staticClass:"dele_btn",on:{click:function(t){e.del(e.nowIndex)}}},[e._v("确认")]),e._v(" "),i("a",{staticClass:"dele_btn",on:{click:function(t){e.close("ceng")}}},[e._v("取消")])]),e._v(" "),i("div",{staticClass:"2"},[i("p",{staticClass:"font36"},[e._v("确认取消？")]),e._v(" "),i("a",{staticClass:"dele_btn",on:{click:function(t){e.del(e.nowIndex)}}},[e._v("确认")]),e._v(" "),i("a",{staticClass:"dele_btn",on:{click:function(t){e.close("ceng")}}},[e._v("取消")])]),e._v(" "),i("div",{staticClass:"3"},[i("p",{staticClass:"fcb font36"},[e._v("订单结算")]),e._v(" "),i("p",{staticClass:"word fcg95 font26"},[e._v("本次结算将获得收益，确认结算？")]),e._v(" "),i("a",{staticClass:"dele_btn",on:{click:function(t){e.del(e.nowIndex)}}},[e._v("确认")]),e._v(" "),i("a",{staticClass:"dele_btn",on:{click:function(t){e.close("ceng")}}},[e._v("取消")])])])])])},staticRenderFns:[]}},33:function(e,t,i){function s(e){i(186)}var n=i(3)(i(101),i(258),s,"data-v-d9822ffe",null);e.exports=n.exports},53:function(e,t,i){e.exports=i.p+"static/img/return.28908f5.png"}});