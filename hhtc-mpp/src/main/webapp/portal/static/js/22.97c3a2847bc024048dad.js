webpackJsonp([22],{115:function(t,e){t.exports={devtool:"cheap-module-source-map",data:function(){return{nowIndex:-1,max_height:parseFloat($("html").css("height"))-1.54*dpi+"px",data:[],parkNo:"",choose:"租赁",floor:"楼层",area:"区域",chooseDay:"请选择▶",opinions:{},communityId:-1,showImage:"",Image:"",count:0}},mounted:function(){},activated:function(){this.floor="楼层",this.area="区域",this.opinions={},this.showImage="",this.Image="",this.srcollInit(),this.getInfo(),this.getWx(),this.getCommunityId(),this.close("ceng"),this.initDate()},created:function(){},watch:{floor:function(){this.area="区域"},parkNo:function(){this.parkNo=this.parkNo.replace(/[^A-Z0-9a-z]/g,"")}},methods:{goRouter:function(t){this.$router.push({path:t})},emptyClick:function(t){t.preventDefault(),t.stopPropagation()},choose_L:function(){$(".radio").eq(0).addClass("checked"),$(".radio").eq(1).removeClass("checked"),this.choose="租赁"},choose_R:function(){$(".radio").eq(1).addClass("checked"),$(".radio").eq(0).removeClass("checked"),this.choose="私有"},initDate:function(){var t=new Date,e=t.getFullYear(),a=t.getMonth(),i=t.getDate();this.chooseDay=e+"-"+a+"-"+i},initPark:function(){var t=this;$.ajax({url:urlDir+"/wx/community/get",type:"GET",data:{id:t.communityId},success:function(e){0==e.code?(t.opinions={},t.opinions=$.parseJSON(e.data.carParkNumberPrefix),t.opinions.carParkNumberPrefix.length<1&&error_msg("暂无楼层信息",2e3)):error_msg(e.code+" 错误 "+e.msg,2e3)},error:function(t){error_msg("网络错误",2e3)}})},getWx:function(){var t=this;$.ajax({url:urlDir+"/wx/jssdk/sign",type:"POST",data:{url:location.href},success:function(e){"string"==typeof e&&(e=toParse(e)),console.log(e),e.data.appid?wx.config({debug:!1,appId:e.data.appid,timestamp:parseInt(e.data.timestamp),nonceStr:e.data.noncestr,signature:e.data.signature,jsApiList:["chooseImage","uploadImage"]}):++t.count<4&&t.getWx()}})},choseImagrs:function(){var t=this;wx.chooseImage({count:1,sizeType:["original"],sourceType:["album","camera"],success:function(e){t.showImage=e.localIds[0],wx.uploadImage({localId:e.localIds[0],isShowProgressTips:1,success:function(e){t.imgUpload(e.serverId)}})}})},imgUpload:function(t){var e=this;$.ajax({url:urlDir+"/wx/media/upload",type:"GET",data:{serverId:t},success:function(t){0==t.code&&(e.Image=t.data.filepath)},error:function(t){error_msg("网络错误",2e3)}})},getInfo:function(){var t=this;$.ajax({url:urlDir+"/wx/goods/get/CarPark",type:"GET",data:{},success:function(e){0==e.code?(t.data=[],e.data.forEach(function(e,a){t.data.push({id:e.id,communityName:e.communityName,parkNumber:e.parkNumber,endTime:e.endTime,state:e.state})}),t.data.length<1&&error_msg("暂无车位",2e3)):error_msg(e.code+" 错误 "+e.msg,2e3)},error:function(t){error_msg("网络错误",2e3)}})},getCommunityId:function(){var t=this;$.ajax({url:urlDir+"/wx/fans/get/communityid",type:"GET",data:{},success:function(e){0==e.code?(t.communityId=e.data,t.initPark()):error_msg(e.code+" 错误 "+e.msg,2e3)},error:function(t){error_msg("网络错误",2e3)}})},checkPark:function(){$.ajax({url:urlDir+"/wx/goods/check/CarPark",type:"GET",data:{communityid:this.communityId,carParkNumber:this.floor+this.area+this.parkNo},success:function(t){0==t.code?1==t.data?(this.close("ceng"),this.show("ceng",3)):this.subbmit():error_msg(t.code+" 错误 "+t.msg,2e3)},error:function(t){error_msg("网络错误",2e3)}})},subbmit:function(){this.close("ceng");var t=this,e="租赁"==t.choose?"short":"long",a="short"==e?t.chooseDay:"";"楼层"==t.floor?error_msg("请正确填写楼层信息"):"区域"==t.floor?error_msg("请正确填写区域信息"):null==t.parkNo||""==t.parkNo?error_msg("请正确填写车位号"):$.ajax({url:urlDir+"/wx/goods/infor/carParkBind",type:"POST",data:{carParkNumber:t.floor+t.area+t.parkNo,carEquityImg:t.Image,carUsefulEndDate:a,carparkstate:e},success:function(e){0==e.code?(t.data=[],null!=e.data&&t.data.push({id:e.data.id,communityName:e.data.communityName,parkNumber:e.data.parkNumber,endTime:e.data.endTime,state:e.data.state})):error_msg(e.code+" 错误 "+e.msg,2e3)},error:function(t){error_msg("网络错误",2e3)}}),t.close("ceng")},srcollInit:function(){var t=this;setTimeout(function(){var e=(new Date).getFullYear(),a=(new Date).toLocaleDateString();$("#chooseDay").mobiscroll().date({theme:"",lang:"zh",display:"bottom",startYear:e-1,endYear:e+10,headerText:function(t){var e=t.split("/");return e[0]+"年"+e[1]+"月"+e[2]+"日"},onSelect:function(e,i){var o=e.split("/");if(1*a.replace(/\//g,"")>=1*o.join(""))return error_msg("截止日期必须大于当前日期",2e3),t.chooseDay="请选择▶",!1;t.chooseDay=o[0]+"-"+o[1]+"-"+o[2]}})},100)},del:function(t){var e=this;$.ajax({url:urlDir+"/wx/goods/infor/carParkLogout",type:"POST",data:{id:e.data[t].id,state:e.data[t].state},success:function(a){0==a.code?e.data.splice(t,1):error_msg(rex.code+" 错误 "+a.msg,2e3)},error:function(t){error_msg("网络错误",2e3)}}),this.close("ceng")},show:function(t,e){$("."+t).css("display","block"),$("."+e).css("display","block")},close:function(t){$("."+t).css("display","none"),$(".1").css("display","none"),$(".2").css("display","none"),$(".3").css("display","none")}}}},132:function(t,e,a){e=t.exports=a(11)(!1),e.push([t.i,".license_info[data-v-0cf0d5cd]{width:100%;background-color:#ececec;height:100%;position:relative;padding-bottom:1.1rem;overflow:hidden}header[data-v-0cf0d5cd]{width:100%;height:.8rem;color:#fff;background-color:#75859f;line-height:.8rem;text-align:center}header span[data-v-0cf0d5cd]{display:inline-block;position:absolute;left:.2rem;top:.2rem;width:.4rem;height:.4rem;background:url("+a(55)+');background-size:contain}.scroll[data-v-0cf0d5cd]{width:100%;height:97%;overflow-y:auto}.item[data-v-0cf0d5cd]{width:7.02rem;height:2rem;background-color:#fff;margin:.4rem auto;text-align:center;overflow:hidden;position:relative;border-radius:.16rem;box-shadow:0 0 .1rem .01rem #dae7ed}.item>div[data-v-0cf0d5cd]{width:100%;overflow:hidden;text-align:left;height:.666rem;line-height:.366rem}.item>div div[data-v-0cf0d5cd]{float:right;margin-right:.2rem}.info[data-v-0cf0d5cd]{color:#647687;text-decoration:underline;line-height:.8rem}.info[data-v-0cf0d5cd],.title[data-v-0cf0d5cd]{display:inline-block;vertical-align:top;height:.8rem}.title[data-v-0cf0d5cd]{text-align:left;margin:.2rem}.btns[data-v-0cf0d5cd]{text-align:right}.btn[data-v-0cf0d5cd]{display:inline-block;vertical-align:top;color:#fff;height:.4rem;text-align:center;width:1rem;line-height:.4rem;background-color:skyblue;border-radius:.1rem;margin:.14rem .06rem}.del[data-v-0cf0d5cd]{background-color:red}.bottom_box[data-v-0cf0d5cd]{width:100%;height:.74rem;border-radius:.2rem .2rem 0 0;position:fixed;bottom:0;opacity:.6;background-color:#75859f}.add[data-v-0cf0d5cd]{display:block;position:absolute;bottom:0;left:3.2rem}.add img[data-v-0cf0d5cd]{width:1rem;height:1rem}.ceng[data-v-0cf0d5cd]{width:100%;height:100%;position:fixed;bottom:0;left:0}.ex-data[data-v-0cf0d5cd]{display:inline-block;width:4.4rem;position:relative}.park_info[data-v-0cf0d5cd]{margin:.1rem auto;position:relative;border:1px solid #acacac;border-radius:.1rem;width:100%;height:.7rem;line-height:.7rem}.ex-data span[data-v-0cf0d5cd]{position:absolute;right:.5rem}.center>div div select[data-v-0cf0d5cd]{display:inline-block;top:.2rem;border:none;height:90%;margin-left:.1rem;width:21%}.center>div[data-v-0cf0d5cd]{display:none}.center>div p[data-v-0cf0d5cd]{text-align:center;margin:.4rem auto}.center>div a[data-v-0cf0d5cd]{margin:.2rem auto}.center>div span[data-v-0cf0d5cd]{margin-left:.2rem}.center>div input[data-v-0cf0d5cd]{border:1px solid #959595;border:none;border-radius:.06rem;width:40%;height:90%;line-height:100%;margin-left:8%}.sub-park[data-v-0cf0d5cd]{width:100%;height:100%;background-color:#777;opacity:.8;position:fixed;bottom:0;left:0}.center[data-v-0cf0d5cd]{position:absolute;background-color:#fff;margin:0 auto;border-radius:.2rem;width:85%;max-width:6.7rem;top:2rem;left:50%;margin-left:-42.5%;padding-top:.2rem;padding-right:8%;padding-left:8%}.sub_btn[data-v-0cf0d5cd]{display:block;color:#fff;height:.6rem;width:3.4rem;line-height:.6rem;text-align:center;background-color:#00bfff;border-radius:.12rem}.center div img[data-v-0cf0d5cd]{width:100%;min-height:1.6rem}.center div .dele_btn[data-v-0cf0d5cd]{display:inline-block;color:#fff;height:.54rem;text-align:center;width:1.6rem;line-height:.54rem;background-color:skyblue;border-radius:.1rem;margin:0 .2rem}.checked[data-v-0cf0d5cd]:before{content:"";position:absolute;width:.16rem;height:.16rem;border-radius:.2rem;top:.044rem;left:.048rem;background-color:#1ba1e2}.radios[data-v-0cf0d5cd]{width:100%;text-align:left;height:.5rem;line-height:.5rem}.radios>span[data-v-0cf0d5cd]{vertical-align:middle;margin-left:.2rem;margin-right:.5rem}.radios>span[data-v-0cf0d5cd]:first-child{margin-left:.8rem}.radio[data-v-0cf0d5cd]{position:relative;display:inline-block;width:.3rem;height:.3rem;line-height:.3rem;margin-top:.01rem;vertical-align:middle;margin-bottom:.1rem;border-radius:.2rem;border:1px solid #1ba1e2}.tipP[data-v-0cf0d5cd]{width:100%;color:#ff314f}',""])},170:function(t,e,a){var i=a(132);"string"==typeof i&&(i=[[t.i,i,""]]),i.locals&&(t.exports=i.locals);a(12)("58cbbfb0",i,!0)},239:function(t,e,a){t.exports={render:function(){var t=this,e=t.$createElement,i=t._self._c||e;return i("div",{staticClass:"license_info"},[i("header",{staticClass:"font30"},[i("span",{on:{click:function(e){t.goRouter("/userinfor")}}}),t._v("\n    车位管理\n  ")]),t._v(" "),i("div",{staticClass:"scroll",style:{height:t.max_height}},t._l(t.data,function(e,a){return i("div",{staticClass:"item"},[i("div",[i("span",{staticClass:"title font28"},[t._v("车位信息：")]),i("span",{staticClass:"info font28"},[t._v(t._s(e.communityName+" "+e.parkNumber))])]),t._v(" "),i("div",[i("span",{staticClass:"title font28"},[t._v("到期时间：")]),i("span",{staticClass:"info font28"},[t._v(t._s(e.endTime))])]),t._v(" "),i("div",{staticClass:"btns"},[i("div",["audit"==e.state?i("a",{staticClass:"btn font26"},[t._v("审核中")]):t._e(),t._v(" "),i("a",{staticClass:"btn font26 del",on:{click:function(e){!function(){t.nowIndex=a,t.show("ceng",2)}()}}},[t._v(t._s("audit"==e.state?"取消":"删除"))])])])])})),t._v(" "),i("div",{staticClass:"ceng"},[i("div",{staticClass:"sub-park",on:{click:function(e){t.close("ceng")}}}),t._v(" "),i("div",{staticClass:"center",on:{click:function(e){t.emptyClick(e)}}},[i("div",{staticClass:"1"},[i("div",{staticClass:"radios"},[t._v("\n          车位类型："),i("span",{staticClass:"radio_box"},[i("span",{staticClass:"radio checked",on:{click:function(e){t.choose_L()}}}),t._v("租赁")]),t._v(" "),i("span",{staticClass:"radio_box"},[i("span",{staticClass:"radio",on:{click:t.choose_R}}),t._v("私有")])]),t._v(" "),i("p",{staticClass:"tipP font28",staticStyle:{"text-align":"left"}},[t._v(t._s("租赁"==t.choose?"租赁请提交合同证书图片":"私有请提交产权图片"))]),t._v(" "),i("div",{staticClass:"park_info"},[i("select",{directives:[{name:"model",rawName:"v-model",value:t.floor,expression:"floor"}],staticClass:"font30",on:{change:function(e){var a=Array.prototype.filter.call(e.target.options,function(t){return t.selected}).map(function(t){return"_value"in t?t._value:t.value});t.floor=e.target.multiple?a:a[0]}}},[i("option",[t._v("楼层")]),t._v(" "),t._l(t.opinions,function(e,a,o){return i("option",[t._v(t._s(a))])})],2),t._v(" "),i("select",{directives:[{name:"model",rawName:"v-model",value:t.area,expression:"area"}],staticClass:"font30",attrs:{id:"area"},on:{change:function(e){var a=Array.prototype.filter.call(e.target.options,function(t){return t.selected}).map(function(t){return"_value"in t?t._value:t.value});t.area=e.target.multiple?a:a[0]}}},[i("option",[t._v("区域")]),t._v(" "),t._l(t.opinions[t.floor],function(e){return i("option",[t._v(t._s(e))])})],2),t._v(" "),i("input",{directives:[{name:"model",rawName:"v-model",value:t.parkNo,expression:"parkNo"}],staticClass:"font30",attrs:{type:"text",name:"",maxlength:"6",placeholder:"请输入车位号"},domProps:{value:t.parkNo},on:{input:function(e){e.target.composing||(t.parkNo=e.target.value)}}})]),t._v(" "),"租赁"==t.choose?i("span",{staticClass:"ex-data font30",attrs:{id:"chooseDay"}},[t._v("截止日期："),i("span",[t._v(t._s(t.chooseDay))])]):t._e(),t._v(" "),i("img",{attrs:{id:"aa",src:t.showImage?t.showImage:"static/img/no_park.png"}}),t._v(" "),i("a",{staticClass:"sub_btn font30",on:{click:function(e){t.checkPark()}}},[t._v("提交申请")])]),t._v(" "),i("div",{staticClass:"2",staticStyle:{"text-align":"center"}},[i("p",{staticClass:"font36"},[t._v("确认删除？")]),t._v(" "),i("a",{staticClass:"dele_btn",on:{click:function(e){t.del(t.nowIndex)}}},[t._v("确认")]),t._v(" "),i("a",{staticClass:"dele_btn",on:{click:function(e){t.close("ceng")}}},[t._v("取消")])]),t._v(" "),i("div",{staticClass:"3",staticStyle:{"text-align":"center"}},[i("p",{staticClass:"font36"},[t._v("确认提交？")]),t._v(" "),i("a",{staticClass:"dele_btn",on:{click:function(e){t.subbmit()}}},[t._v("确认")]),t._v(" "),i("a",{staticClass:"dele_btn",on:{click:function(e){t.close("ceng")}}},[t._v("取消")])])])]),t._v(" "),i("div",{staticClass:"bottom_box"}),t._v(" "),i("a",{staticClass:"add",on:{click:function(e){t.show("ceng",1)}}},[i("img",{attrs:{src:a(70)}})])])},staticRenderFns:[]}},39:function(t,e,a){function i(t){a(170)}var o=a(3)(a(115),a(239),i,"data-v-0cf0d5cd",null);t.exports=o.exports},55:function(t,e,a){t.exports=a.p+"static/img/return.28908f5.png"},70:function(t,e,a){t.exports=a.p+"static/img/add.d1d914e.png"}});