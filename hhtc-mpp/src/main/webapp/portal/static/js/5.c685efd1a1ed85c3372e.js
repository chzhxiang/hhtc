webpackJsonp([5],{100:function(t,i){t.exports={devtool:"cheap-module-source-map",data:function(){return{communityList:[],parkList:[{orderID:"orderID",communityName:"community",carParkNumber:"carNumber",carParkImg:"carParkImg",price:"price",publishFromDates:"publishFromDates",publishFromTime:"publishFromTime",publishEndTime:"publishEndTime"}],parkChooseIndex:0,chooseCurrCommunity:"再看看（别的）",chooseCurrTime:"----/--/--",max_height:0,btnStr:"再看看别的",resultFlag:!1}},mounted:function(){this.max_height=parseFloat($("html").css("height"))-4.5*dpi+"px"},activated:function(){this.srcollInit(),this.initCommunityList()},created:function(){},watch:{resultFlag:function(){this.resultFlag?btnStr="继续逛逛":btnStr="再看看别的"}},methods:{initCommunityList:function(){var t=this;$.ajax({url:urlDir+"/wx/community/listByCityName?cityName=重庆市",type:"GET",success:function(i){0==i.code?(t.communityList=[],i.data.forEach(function(i,e){t.communityList.push({id:i.id,name:i.name})}),t.communityList.length<1?error_msg("当前没有小区提供选择",2e3):(t.chooseCurrCommunity=t.communityList[0].name,t.loadPark(t.communityList[0].id))):error_msg(i.code+" 错误 "+i.msg,2e3)},error:function(t){error_msg("网络错误",2e3)}})},loadPark:function(t){var i=this;$.ajax({url:urlDir+"/wx/market/post/inventory",type:"GET",data:{communityId:t},success:function(t){0==t.code?(i.parkList=[],t.data.forEach(function(t,e){i.parkList.push({orderID:t.orderID,communityName:t.communityName,carParkNumber:t.carParkNumber,carParkImg:t.carParkImg,price:t.price,publishFromDates:t.publishFromDates,publishFromTime:t.publishFromTime,publishEndTime:t.publishEndTime})}),i.parkList.length<1&&error_msg("当前没有订单提供",2e3)):error_msg(t.code+" 错误 "+t.msg,2e3)},error:function(t){error_msg("网络错误",2e3)}})},srcollInit:function(){var t=this;setTimeout(function(){var i=(new Date).getFullYear(),e=(new Date).toLocaleDateString();$("#chooseDay").mobiscroll().date({theme:"",lang:"zh",display:"bottom",startYear:i-1,endYear:i+10,headerText:function(t){var i=t.split("/");return i[0]+"年"+i[1]+"月"+i[2]+"日"},onSelect:function(i,r){var a=i.split("/");if(1*e.replace(/\//g,"")>1*a.join(""))return error_msg("日期必须晚于当前",2e3),t.chooseCurrTime="----/--/--",!1;t.chooseCurrTime=a[0]+"-"+a[1]+"-"+a[2]}}),$("#communityList").mobiscroll().treelist({theme:"",lang:"zh",display:"bottom",defaultValue:[communityList[0]],headerText:function(t){},onSelect:function(i,e){t.chooseCurrCommunity=t.communityList[i].name,t.loadPark(t.communityList[i].id)}})},100)},chooseCommunity:function(){$("#communityList_dummy").focus()},getFansInfo:function(){var t=this;$.ajax({url:urlDir+"/wx/fans/get",type:"GET",success:function(i){0==i.code&&(t.phone=i.data.phoneNo)},error:function(t){}})},show:function(t,i){this.parkChooseIndex=1*i,$("."+t).css("display","block")},payForPark:function(t){var i=this;$.ajax({url:urlDir+"/wx/market/transaction/reservation",type:"POST",data:{orderid:i.parkList[t].orderID,CarNuber:i.parkList[t].CarNuber},success:function(e){0==e.code?i.resultFlag=!0:(i.resultFlag=!1,error_msg(""+e.msg,2e3)),i.close("ceng"),i.show("result",t)},error:function(t){error_msg("网络错误",2e3),i.close("ceng")}})},close:function(t){$("."+t).css("display","none")},goRouter:function(t){this.$router.push({path:t})}}}},119:function(t,i,e){i=t.exports=e(11)(!1),i.push([t.i,".park[data-v-2c5dc185]{max-width:7.5rem;height:100%;overflow:hidden;background-color:#ececec}.choose[data-v-2c5dc185]{background-color:#3c3b3d;width:100%;height:2.2rem;border-bottom-right-radius:1em;border-bottom-left-radius:1em}.choose div[data-v-2c5dc185]{width:100%;height:1.1rem}.choose div img[data-v-2c5dc185]{width:.5rem;height:.5rem;margin:10px 5px}.choose div p[data-v-2c5dc185]{height:20px;line-height:20px;margin-top:10px}.choose div span[data-v-2c5dc185]{width:50%;line-height:1.1rem;text-align:right;overflow:hidden;background-color:#ffd300}.choose div span img[data-v-2c5dc185]{width:.5rem;height:.5rem;margin:10px 5px}.about[data-v-2c5dc185]{width:100%;height:1.25rem;padding:0 15px}.about img[data-v-2c5dc185]{width:100%;height:1.25rem;margin-top:.05rem}.scroll[data-v-2c5dc185]{width:100%;height:280px;overflow-y:auto;padding-left:15px;padding-right:15px}.order[data-v-2c5dc185]{width:100%;height:2.5rem;border-radius:7px;margin-top:8px;padding-left:15px;padding-right:15px;background-color:#fff}.orderMsg[data-v-2c5dc185]{width:100%;height:1.6rem}.orderMsg div[data-v-2c5dc185]{height:1.6rem}.orderMsg .divMsg[data-v-2c5dc185]{width:65%;height:1.6rem;padding-top:5px;padding-bottom:5px}.orderMsg .divIcon[data-v-2c5dc185]{width:35%;height:1.6rem;padding:10px}.orderMsg div p[data-v-2c5dc185]{width:100%;height:.65rem;line-height:.65rem}.orderMsg div img[data-v-2c5dc185]{width:100%;height:100%}.orderPay[data-v-2c5dc185]{clear:both;width:100%;height:.9rem;border-top:1px solid #a0a0a0}.orderPay .btn[data-v-2c5dc185]{width:60%;height:.9rem;padding-top:.18rem;padding-bottom:.2rem}.orderPay .btn div[data-v-2c5dc185]{width:70%;height:100%;background:#3c3b3d;border-radius:5px;text-align:center;line-height:.52rem}.orderPay .icon[data-v-2c5dc185]{width:40%;height:.9rem}.orderPay .icon img[data-v-2c5dc185]{width:100%;height:.9rem}.ceng[data-v-2c5dc185]{width:100%;height:100%;position:absolute;top:0;left:0;background:hsla(0,0%,100%,.75)}.ceng .ceng_content[data-v-2c5dc185]{width:5rem;height:3.9rem;background:#fff;border-radius:.24rem;padding:.3rem .46rem;margin:4.6rem auto;vertical-align:middle;box-shadow:0 0 1rem 0 #c9c9c9;border:1px solid #cfebf8;text-align:center}.ceng .ceng_content img[data-v-2c5dc185]{width:100%;height:2rem}.ceng .ceng_content p[data-v-2c5dc185]{width:100%;height:.5rem;margin:.15rem 0;line-height:.5rem}.ceng .ceng_content input[data-v-2c5dc185]{width:100%;height:.5rem;background:#75859f;border-radius:.12rem;color:#fff}",""])},154:function(t,i,e){var r=e(119);"string"==typeof r&&(r=[[t.i,r,""]]),r.locals&&(t.exports=r.locals);e(12)("a7252b34",r,!0)},197:function(t,i,e){t.exports=e.p+"static/img/orderPay.5fae573.png"},201:function(t,i,e){t.exports=e.p+"static/img/position.5a92ab5.png"},208:function(t,i,e){t.exports=e.p+"static/img/time_choose1.5404260.png"},221:function(t,i,e){t.exports={render:function(){var t=this,i=t.$createElement,r=t._self._c||i;return r("div",{staticClass:"park"},[r("div",{staticClass:"choose"},[r("div",[r("img",{staticClass:"l",attrs:{src:e(201)}}),t._v(" "),r("p",{staticClass:"l fcw font38"},[t._v("当前查看小区：")]),t._v(" "),r("span",{staticClass:"r fcw font28",on:{click:t.chooseCommunity}},[t._v(t._s(t.chooseCurrCommunity)),r("img",{staticClass:"r",attrs:{src:e(75)}})])]),t._v(" "),r("div",[r("img",{staticClass:"l",attrs:{src:e(208)}}),t._v(" "),r("p",{staticClass:"l fcw font38"},[t._v("当前日期选择：")]),t._v(" "),r("span",{staticClass:"r fcw font26",attrs:{id:"chooseDay"}},[t._v(t._s(t.chooseCurrTime)),r("img",{staticClass:"r",attrs:{src:e(75)}})])])]),t._v(" "),t._m(0),t._v(" "),r("div",{staticClass:"scroll",style:{height:t.max_height}},t._l(t.parkList,function(i,e){return r("div",{staticClass:"order"},[r("div",{staticClass:"orderMsg"},[r("div",{staticClass:"l divMsg"},[r("p",{staticClass:"font34"},[t._v("车位信息： "+t._s(i.communityName))]),t._v(" "),r("p",{staticClass:"font34"},[t._v("出租时间： "),r("span",{staticClass:"font14"},[t._v(t._s(i.publishFromDates))])])]),t._v(" "),t._m(1,!0)]),t._v(" "),r("div",{staticClass:"orderPay",on:{click:function(i){t.show("predetermined",e)}}},[r("div",{staticClass:"l btn"},[r("div",{staticClass:"fcw font32"},[t._v(t._s(i.price)+"元抢拍")])]),t._v(" "),t._m(2,!0)])])})),t._v(" "),r("div",{staticClass:"ceng predetermined",staticStyle:{display:"none"}},[r("div",{staticClass:"ceng_content"},[r("span",{staticStyle:{"background-color":"#fff","border-radius":".3rem",position:"absolute",top:"0","margin-top":"4.4rem",right:"1.05rem"},on:{click:function(i){t.close("ceng")}}},[r("img",{staticStyle:{width:".6rem",height:".6rem"},attrs:{src:e(74)}})]),t._v(" "),r("img",{attrs:{src:e(49)}}),t._v(" "),r("p",{staticClass:"font32"},[t._v("花费"+t._s(t.parkList[0].price)+"元预定这个车位吗？")]),t._v(" "),r("input",{staticClass:"font32",attrs:{type:"button",value:"确认"},on:{click:function(i){t.payForPark(t.parkChooseIndex)}}})])]),t._v(" "),r("div",{staticClass:"ceng result",staticStyle:{display:"none"}},[r("div",{staticClass:"ceng_content"},[r("span",{staticStyle:{"background-color":"#fff","border-radius":".3rem",position:"absolute",top:"0","margin-top":"4.4rem",right:"1.05rem"},on:{click:function(i){t.close("ceng")}}},[r("img",{staticStyle:{width:".6rem",height:".6rem"},attrs:{src:e(74)}})]),t._v(" "),r("img",{attrs:{src:e(49)}}),t._v(" "),r("p",{staticClass:"font32"},[t._v(t._s(t.resultFlag?"预定成功":"下手晚了，订单被抢走了"))]),t._v(" "),r("input",{directives:[{name:"model",rawName:"v-model",value:t.btnStr,expression:"btnStr"}],staticClass:"font30",attrs:{type:"button"},domProps:{value:t.btnStr},on:{click:function(i){t.close("ceng")},input:function(i){i.target.composing||(t.btnStr=i.target.value)}}})])]),t._v(" "),r("ul",{staticStyle:{display:"none"},attrs:{id:"communityList"}},t._l(t.communityList,function(i){return r("li",[r("div",{attrs:{"data-value":i.name}},[t._v(t._s(i.name))])])}))])},staticRenderFns:[function(){var t=this,i=t.$createElement,r=t._self._c||i;return r("div",{staticClass:"about"},[r("img",{attrs:{src:e(49)}})])},function(){var t=this,i=t.$createElement,r=t._self._c||i;return r("div",{staticClass:"l divIcon"},[r("img",{attrs:{src:e(49)}})])},function(){var t=this,i=t.$createElement,r=t._self._c||i;return r("div",{staticClass:"l icon"},[r("img",{attrs:{src:e(197)}})])}]}},35:function(t,i,e){function r(t){e(154)}var a=e(3)(e(100),e(221),r,"data-v-2c5dc185",null);t.exports=a.exports},49:function(t,i,e){t.exports=e.p+"static/img/no_park.e528370.png"},74:function(t,i,e){t.exports=e.p+"static/img/close.2201c72.png"},75:function(t,i,e){t.exports=e.p+"static/img/icon16.284d803.png"}});