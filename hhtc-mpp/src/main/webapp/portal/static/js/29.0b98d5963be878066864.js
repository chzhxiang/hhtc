webpackJsonp([29],{103:function(e,a){e.exports={devtool:"cheap-module-source-map",data:function(){return{parkSpace:"",endDay:"结束日期",choose:"租赁",floor:"楼层",area:"区域",opinions:{},communityId:-1,showImage:"",Image:"",count:0}},mounted:function(){},activated:function(){this.parkSpace="",this.floor="楼层",this.area="区域",this.close("ceng"),this.srcollInit(),this.getCommunityId(),this.initDate(),this.getWx()},created:function(){},watch:{floor:function(){this.area="区域"}},methods:{show:function(e,a){$("."+e).css("display","block")},close:function(e){$("."+e).css("display","none")},srcollInit:function(){var e=this;setTimeout(function(){var a=(new Date).getFullYear(),t=(new Date).toLocaleDateString();$("#endDay").mobiscroll().date({theme:"",lang:"zh",display:"bottom",startYear:a-1,endYear:a+10,headerText:function(e){var a=e.split("/");return a[0]+"年"+a[1]+"月"+a[2]+"日"},onSelect:function(a,r){var o=a.split("/");if(1*t.replace(/\//g,"")>=1*o.join(""))return error_msg("结束日期必须大于当前日期",2e3),e.endDay="结束日期",!1;e.endDay=o[0]+"-"+o[1]+"-"+o[2]}})},100)},choose_L:function(){$(".radio").eq(0).addClass("checked"),$(".radio").eq(1).removeClass("checked"),this.choose="租赁"},choose_R:function(){$(".radio").eq(1).addClass("checked"),$(".radio").eq(0).removeClass("checked"),this.choose="私有"},checkPark:function(){var e=this;return"楼层"==e.floor?(error_msg("请填写楼层信息",2e3),!1):"区域"==e.floor?(error_msg("请填写区域信息",2e3),!1):null==e.parkSpace||""==e.parkSpace?(error_msg("请填写车位信息",2e3),!1):"结束日期"==e.endDay?(error_msg("请选择车位到期时间",2e3),!1):void $.ajax({url:urlDir+"/wx/goods/check/CarPark",type:"GET",data:{communityid:e.communityId,carParkNumber:e.floor+e.area+e.parkSpace},success:function(a){0==a.code?1==a.data?e.show("ceng"):e.commit():error_msg(a.code+" 错误 "+a.msg,2e3)},error:function(e){error_msg("网络错误",2e3)}})},initDate:function(){var e=new Date,a=e.getFullYear(),t=e.getMonth(),r=e.getDate();this.endDay=a+"-"+t+"-"+r},initPark:function(){var e=this;$.ajax({url:urlDir+"/wx/community/get",type:"GET",data:{id:e.communityId},success:function(a){0==a.code?(e.opinions={},e.opinions=$.parseJSON(a.data.carParkNumberPrefix),e.opinions.carParkNumberPrefix.length<1&&error_msg("暂无楼层信息",2e3)):error_msg(a.code+" 错误 "+a.msg,2e3)},error:function(e){error_msg("网络错误",2e3)}})},getCommunityId:function(){var e=this;$.ajax({url:urlDir+"/wx/fans/get/communityid",type:"GET",data:{},success:function(a){0==a.code?(e.communityId=a.data,e.initPark()):error_msg(a.code+" 错误 "+a.msg,2e3)},error:function(e){error_msg("网络错误",2e3)}})},getFansInfo:function(){var e=this;$.ajax({url:urlDir+"/wx/fans/get",type:"GET",success:function(a){0==a.code&&(e.phone=a.data.phoneNo)},error:function(e){}})},getWx:function(){var e=this;$.ajax({url:urlDir+"/wx/jssdk/sign",type:"POST",data:{url:location.href},success:function(a){"string"==typeof a&&(a=toParse(a)),console.log(a),a.data.appid?wx.config({debug:!1,appId:a.data.appid,timestamp:parseInt(a.data.timestamp),nonceStr:a.data.noncestr,signature:a.data.signature,jsApiList:["chooseImage","uploadImage"]}):++e.count<4&&e.getWx()}})},choseImagrs:function(){var e=this;wx.chooseImage({count:1,sizeType:["original"],sourceType:["album","camera"],success:function(a){e.showImage=a.localIds[0],wx.uploadImage({localId:a.localIds[0],isShowProgressTips:1,success:function(a){e.imgUpload(a.serverId)}})}})},imgUpload:function(e){var a=this;$.ajax({url:urlDir+"/wx/media/upload",type:"GET",data:{serverId:e},success:function(e){0==e.code?(alert("成功—引导页： "+e.data.filepath),a.Image=e.data.filepath):error_msg(" 图片上传服务器失败 ",2e3)},error:function(e){error_msg("网络错误",2e3)}})},commit:function(){var e=this;alert("commit Image -> "+e.Image);var a="租赁"==e.choose?"short":"long";$.ajax({url:urlDir+"/wx/goods/infor/carParkBind",type:"POST",data:{carParkNumber:e.floor+e.area+e.parkSpace,carEquityImg:e.Image,carUsefulEndDate:e.endDay,carparkstate:a},success:function(a){0==a.code?e.goRouter("/guide_page4"):error_msg(a.code+" 错误 "+a.msg,2e3)},error:function(e){error_msg("网络错误",2e3)}}),this.close("ceng")},goRouter:function(e){this.$router.push({path:e})}}}},157:function(e,a,t){a=e.exports=t(11)(!1),a.push([e.i,'.corner[data-v-a951fb68]{width:85%;min-height:5rem;border-radius:10px;background-color:#fff;margin:20px auto;padding-top:10px;text-align:center}.sub-park[data-v-a951fb68],ceng[data-v-a951fb68]{width:100%;height:100%;position:fixed;bottom:0;left:0}.sub-park[data-v-a951fb68]{background-color:#777;opacity:.8}.center[data-v-a951fb68]{text-align:center;position:absolute;width:5rem;min-height:2.2rem;background-color:#fff;top:4.2rem;left:1.2rem;margin:0 auto;border-radius:.2rem}.center>div p[data-v-a951fb68]{text-align:center;margin:.4rem auto}.center>div a[data-v-a951fb68]{margin:.2rem auto}.center div .dele_btn[data-v-a951fb68]{display:inline-block;color:#fff;height:.54rem;text-align:center;width:1.6rem;line-height:.54rem;background-color:skyblue;border-radius:.1rem;margin:0 .2rem}.msg[data-v-a951fb68]{width:100%;height:.75rem;line-height:.75rem;margin:5px 0 0;text-align:left;padding-left:12.5%;padding-right:12.5%}.checked[data-v-a951fb68]:before{content:"";position:absolute;width:.16rem;height:.16rem;border-radius:.2rem;top:.044rem;left:.048rem;background-color:#1ba1e2}.park_info[data-v-a951fb68]{width:75%;height:.6rem;margin:.2rem auto;border:1px solid #acacac;border-radius:.1rem;padding:-.5rem}.park_info select[data-v-a951fb68]{float:left;height:.5rem;top:.2rem;border:none}.park_info input[data-v-a951fb68]{width:1.9rem;height:.52rem;margin:0;border:1px solid #959595;border:none;border-radius:.06rem}.radios[data-v-a951fb68]{width:75%;text-align:left;margin:0 auto}.radios>span[data-v-a951fb68]{vertical-align:top;margin-left:.2rem;margin-right:.5rem}.radios>span[data-v-a951fb68]:first-child{margin-left:.8rem}.radio[data-v-a951fb68]{position:relative;display:inline-block;width:.3rem;height:.3rem;line-height:.3rem;margin-top:.01rem;vertical-align:top;border-radius:.2rem;border:1px solid #1ba1e2}.corner img[data-v-a951fb68]{width:75%;height:2rem;margin:0 auto}.corner p[data-v-a951fb68]{width:75%;margin:.1rem auto;color:#ff314f}.num[data-v-a951fb68]{width:75%;height:.65rem;font-size:12px}.btn[data-v-a951fb68]{width:50%;height:.5rem;margin:10px auto;font-size:12px;border-radius:5px;color:#fff;background-color:#52e6ff}',""])},195:function(e,a,t){var r=t(157);"string"==typeof r&&(r=[[e.i,r,""]]),r.locals&&(e.exports=r.locals);t(12)("79bc2310",r,!0)},265:function(e,a,t){e.exports={render:function(){var e=this,a=e.$createElement,r=e._self._c||a;return r("div",{staticClass:"guide_page3_park_space"},[r("div",{staticClass:"ceng"},[r("div",{staticClass:"sub-park",on:{click:function(a){e.close("ceng")}}}),e._v(" "),r("div",{staticClass:"center",on:{click:function(a){e.emptyClick(a)}}},[r("div",[r("p",{staticClass:"font36"},[e._v("确认提交？")]),e._v(" "),r("a",{staticClass:"dele_btn",on:{click:e.commit}},[e._v("确认")]),e._v(" "),r("a",{staticClass:"dele_btn",on:{click:function(a){e.close("ceng")}}},[e._v("取消")])])])]),e._v(" "),r("div",{staticClass:"corner"},[r("div",{staticClass:"radios"},[e._v("\n      车位类型："),r("span",{staticClass:"radio_box"},[r("span",{staticClass:"radio checked",on:{click:function(a){e.choose_L()}}}),e._v("租赁")]),e._v(" "),r("span",{staticClass:"radio_box"},[r("span",{staticClass:"radio",on:{click:e.choose_R}}),e._v("私有")])]),e._v(" "),r("p",{staticStyle:{"text-align":"left"}},[e._v(e._s("租赁"==e.choose?"租赁请提交合同证书图片":"私有请提交产权图片"))]),e._v(" "),r("div",{staticClass:"park_info"},[r("select",{directives:[{name:"model",rawName:"v-model",value:e.floor,expression:"floor"}],on:{change:function(a){var t=Array.prototype.filter.call(a.target.options,function(e){return e.selected}).map(function(e){return"_value"in e?e._value:e.value});e.floor=a.target.multiple?t:t[0]}}},[r("option",[e._v("楼层")]),e._v(" "),e._l(e.opinions,function(a,t,o){return r("option",[e._v(e._s(t))])})],2),e._v(" "),r("select",{directives:[{name:"model",rawName:"v-model",value:e.area,expression:"area"}],on:{change:function(a){var t=Array.prototype.filter.call(a.target.options,function(e){return e.selected}).map(function(e){return"_value"in e?e._value:e.value});e.area=a.target.multiple?t:t[0]}}},[r("option",[e._v("区域")]),e._v(" "),e._l(e.opinions[e.floor],function(a){return r("option",[e._v(e._s(a))])})],2),e._v(" "),r("input",{directives:[{name:"model",rawName:"v-model",value:e.parkSpace,expression:"parkSpace"}],attrs:{type:"text",name:"",onkeyup:"this.value=this.value.replace(/\\D/g,'')",onafterpaste:"this.value=this.value.replace(/\\D/g,'')",maxlength:"4",placeholder:"请输入车位号"},domProps:{value:e.parkSpace},on:{input:function(a){a.target.composing||(e.parkSpace=a.target.value)}}})]),e._v(" "),r("div",{staticClass:"msg"},[r("span",[e._v("截止日期")]),e._v(" "),r("span",{staticStyle:{float:"right"},attrs:{id:"endDay"}},[e._v(e._s(e.endDay)),r("img",{staticStyle:{display:"inline-block",margin:"auto 10px 0",width:"10px",height:".25rem"},attrs:{src:t(60)}})])]),e._v(" "),r("img",{attrs:{src:e.showImage?e.showImage:"static/img/no_park.png"},on:{click:e.choseImagrs}}),e._v(" "),r("input",{staticClass:"btn",attrs:{type:"button",value:"提交审核"},on:{click:e.checkPark}}),e._v(" "),r("br")])])},staticRenderFns:[]}},27:function(e,a,t){function r(e){t(195)}var o=t(3)(t(103),t(265),r,"data-v-a951fb68",null);e.exports=o.exports},60:function(e,a,t){e.exports=t.p+"static/img/icon10.bb7857d.png"}});