webpackJsonp([24],{106:function(e,t){e.exports={devtool:"cheap-module-source-map",data:function(){return{nowIndex:-1,data:[],cityList:["京","津","冀","渝","晋","蒙","辽","吉","黑","沪","苏","浙","皖","闽","赣","鲁","豫","鄂","湘","粤","桂","琼","渝","川","贵","云","藏","陕","甘","青","宁","新"],carNumber:"",carHead:"",licenseMsg:"",cancelDelFlag:!1,count:0,showImage:"",imgServerUrl:""}},mounted:function(){},activated:function(){this.getInfo(),this.word="",this.srcollInit(),this.close("ceng"),this.getWx(),this.closeOne("corner")},created:function(){},watch:{nowIndex:function(){var e=this;e.cancelDelFlag="audit"!=e.data[e.nowIndex].state},carHead:function(){-1!=this.carHead.indexOf("沪")||-1!=this.carHead.indexOf("粤")?this.maxLength=7:this.maxLength=6},carNumber:function(e){var t=this;this.carNumber=this.carNumber.replace(/[^A-Z0-9a-z]/g,""),this.carNumber.length>t.maxLength&&(this.carNumber=this.carNumber.substr(0,t.maxLength)),1==e.length&&(/^[A-Za-z]/g.test(e)||(t.carNumber=""))}},methods:{goRouter:function(e){this.$router.push({path:e})},emptyClick:function(e){e.preventDefault(),e.stopPropagation()},srcollInit:function(){var e=this;setTimeout(function(){$("#carChoose").mobiscroll().treelist({theme:"",lang:"zh",display:"bottom",onSelect:function(t,a){e.carHead=e.cityList[t]}})},100)},carChoose:function(){$("#carChoose_dummy").focus()},getInfo:function(){var e=this;$.ajax({url:urlDir+"/wx/fans/get/CarNuber",type:"GET",data:{},success:function(t){0==t.code?(e.data=[],t.data.forEach(function(t,a){e.data.push({id:t.id,state:t.state,infor:t.infor})})):error_msg(t.code+"错误 "+t.msg,2e3)},error:function(e){error_msg("网络错误",2e3)}})},subbmit:function(e){var t=this;$.ajax({url:urlDir+"/wx/fans/infor/carNumberBind",type:"POST",data:{carNumber:t.licenseMsg,carNumberImg:t.imgServerUrl},success:function(e){0==e.code?null!=e.data&&t.data.push({id:e.data.id,state:e.data.state,infor:e.data.infor}):error_msg(e.code+" 错误 "+e.msg,2e3)},error:function(e){error_msg("网络错误",2e3)}}),t.close(e)},del:function(e){var t=this;$.ajax({url:urlDir+"/wx/fans/infor/carNumberLogout",type:"POST",data:{id:t.data[e].id,state:t.data[e].state},success:function(a){0==a.code?t.data.splice(e,1):error_msg(a.code+" 错误 "+a.msg,2e3)},error:function(e){error_msg("网络错误",2e3)}}),t.close("ceng")},addLicense:function(){var e=this;e.data.length>=2?error_msg("同一个用户最多两个车牌",2e3):e.showOne("corner")},show:function(e,t){$("."+e).css("display","block"),$("."+t).css("display","block")},showOne:function(e){$(".cengComm").css("display","block"),$("."+e).css("display","block")},closeOne:function(e){$(".cengComm").css("display","none"),$("."+e).css("display","none")},close:function(e){$("."+e).css("display","none"),$(".1").css("display","none"),$(".2").css("display","none")},getWx:function(){var e=this;$.ajax({url:urlDir+"/wx/jssdk/sign",type:"POST",data:{url:location.href},success:function(t){"string"==typeof t&&(t=toParse(t)),console.log(t),t.data.appid?wx.config({debug:!1,appId:t.data.appid,timestamp:parseInt(t.data.timestamp),nonceStr:t.data.noncestr,signature:t.data.signature,jsApiList:["chooseImage","uploadImage"]}):++e.count<4&&e.getWx()}})},choseImagrs:function(){var e=this;wx.chooseImage({count:1,sizeType:["original"],sourceType:["album","camera"],success:function(t){e.showImage=t.localIds[0],wx.uploadImage({localId:t.localIds[0],isShowProgressTips:1,success:function(t){e.imgUpload(t.serverId)}})}})},imgUpload:function(e){var t=this;$.ajax({url:urlDir+"/wx/media/upload",type:"GET",data:{serverId:e},success:function(e){0==e.code?t.imgServerUrl=e.data.filepath:error_msg("图片上传失败 "+e.msg,2e3)},error:function(e){error_msg("网络错误",2e3)}})}}}},146:function(e,t,a){t=e.exports=a(11)(!1),t.push([e.i,".license_info[data-v-5715ed80]{width:100%;background-color:#ececec;height:100%;position:relative;padding-bottom:1.1rem;overflow:hidden}header[data-v-5715ed80]{width:100%;height:.8rem;color:#fff;background-color:#75859f;line-height:.8rem;text-align:center}header span[data-v-5715ed80]{display:inline-block;position:absolute;left:.2rem;top:.2rem;width:.4rem;height:.4rem;background:url("+a(55)+");background-size:contain}.item[data-v-5715ed80]{width:7.02rem;height:.8rem;background-color:#fff;margin:.4rem auto;text-align:center;overflow:hidden;position:relative;border-radius:.16rem;box-shadow:0 0 .1rem .01rem #dae7ed}.item div[data-v-5715ed80]{display:inline-block;float:right}.item span[data-v-5715ed80]{float:left}.info[data-v-5715ed80]{color:#647687;text-decoration:underline;line-height:.8rem}.info[data-v-5715ed80],.title[data-v-5715ed80]{display:inline-block;vertical-align:top;height:.8rem}.title[data-v-5715ed80]{text-align:left;margin:.2rem}.btn[data-v-5715ed80]{display:inline-block;vertical-align:top;height:.4rem;width:1rem;line-height:.4rem;background-color:skyblue;border-radius:.1rem;margin-top:.2rem}.del[data-v-5715ed80]{background-color:red}.bottom_box[data-v-5715ed80]{width:100%;height:.74rem;border-radius:.2rem .2rem 0 0;position:fixed;bottom:0;opacity:.6;background-color:#75859f}.add[data-v-5715ed80]{display:block;position:absolute;bottom:0;left:3.2rem}.add img[data-v-5715ed80]{width:1rem;height:1rem}.ceng[data-v-5715ed80]{width:100%;height:100%;position:fixed;bottom:0;left:0}.center div[data-v-5715ed80]{display:none;text-align:center}.center div p[data-v-5715ed80]{text-align:center;margin:.4rem auto}.center div a[data-v-5715ed80]{margin:.2rem auto}.center div input[data-v-5715ed80]{width:4.4rem;height:.5rem;margin:.4rem 0 0 .3rem;border:1px solid #959595;border-radius:.06rem}.sub-park[data-v-5715ed80]{width:100%;height:100%;background-color:#777;opacity:.8;position:fixed;bottom:0;left:0}.center[data-v-5715ed80]{position:absolute;width:5rem;min-height:2.2rem;background-color:#fff;top:4.2rem;left:1.2rem;margin:0 auto;border-radius:.2rem}.sub_btn[data-v-5715ed80]{display:inline-block;color:#fff;height:.4rem;width:3.4rem;line-height:.4rem;text-align:center;background-color:#00bfff;border-radius:.12rem;margin:.2rem .8rem}.center div img[data-v-5715ed80]{width:4.4rem;height:1.6rem;margin:.2rem .3rem}.center div .dele_btn[data-v-5715ed80]{display:inline-block;color:#fff;height:.54rem;text-align:center;width:1.6rem;line-height:.54rem;background-color:skyblue;border-radius:.1rem;margin:0 .2rem}.cengComm[data-v-5715ed80]{width:100%;height:100%;position:fixed;bottom:0;left:0}.corner[data-v-5715ed80]{position:absolute;width:85%;min-height:2.2rem;top:2rem;left:50%;margin-left:-42.5%;border-radius:10px;background-color:#fff;text-align:center;line-height:1rem}.corner img[data-v-5715ed80]{width:75%;min-height:2rem;margin:0 auto;clear:both}.time_chose[data-v-5715ed80]{width:80%;margin:.2rem auto .1rem;padding:.13rem .3rem}.time_chose div[data-v-5715ed80]{height:.9rem;line-height:.9rem;overflow:hidden}.time_chose div span.r[data-v-5715ed80]{padding-right:.01rem;padding-left:.05rem}.time_chose div span.r input[data-v-5715ed80]{text-align:left;width:3.4rem;height:.5rem;line-height:.5rem}.time_chose div span.duan input[data-v-5715ed80]{width:1.7rem;height:.5rem;line-height:.5rem}.btn[data-v-5715ed80]{width:50%;height:.5rem;margin:0 auto;margin-top:10px;font-size:12px;border-radius:5px;color:#fff;background-color:#52e6ff}",""])},184:function(e,t,a){var i=a(146);"string"==typeof i&&(i=[[e.i,i,""]]),i.locals&&(e.exports=i.locals);a(12)("258e8899",i,!0)},253:function(e,t,a){e.exports={render:function(){var e=this,t=e.$createElement,i=e._self._c||t;return i("div",{staticClass:"license_infor"},[i("header",{staticClass:"font30"},[i("span",{on:{click:function(t){e.goRouter("/userinfor")}}}),e._v("\n    车牌管理\n  ")]),e._v(" "),i("div",e._l(e.data,function(t,a){return i("div",{staticClass:"item"},[i("span",{staticClass:"title font28"},[e._v("车牌号：")]),e._v(" "),i("span",{staticClass:"info font26"},[e._v(e._s(t.infor))]),e._v(" "),i("div",["audit"==t.state?i("a",{staticClass:"btn font26"},[e._v("审核中")]):e._e(),e._v(" "),i("a",{staticClass:"btn font26 del",on:{click:function(t){!function(){e.nowIndex=a,e.show("ceng",2)}()}}},[e._v(e._s("audit"==t.state?"取消":"删除"))])])])})),e._v(" "),i("div",{staticClass:"ceng"},[i("div",{staticClass:"sub-park",on:{click:function(t){e.close("ceng")}}}),e._v(" "),i("div",{staticClass:"center",on:{click:function(t){e.emptyClick(t)}}},[i("div",{staticClass:"1"},[i("input",{directives:[{name:"model",rawName:"v-model",value:e.licenseMsg,expression:"licenseMsg"}],attrs:{type:"text",name:"",placeholder:"请输入车牌信息"},domProps:{value:e.licenseMsg},on:{input:function(t){t.target.composing||(e.licenseMsg=t.target.value)}}}),e._v(" "),i("img",{attrs:{src:e.showImage?e.showImage:"static/img/no_park.png"},on:{click:e.choseImagrs}}),e._v(" "),i("a",{staticClass:"sub_btn",on:{click:function(t){e.subbmit("ceng")}}},[e._v("提交申请")])]),e._v(" "),i("div",{staticClass:"2"},[i("p",{staticClass:"font36"},[e._v(e._s(e.cancelDelFlag?"确认删除？":"确认取消？"))]),e._v(" "),i("a",{staticClass:"dele_btn",on:{click:function(t){e.del(e.nowIndex)}}},[e._v("确认")]),e._v(" "),i("a",{staticClass:"dele_btn",on:{click:function(t){e.close("ceng")}}},[e._v("取消")])])])]),e._v(" "),i("div",{staticClass:"cengComm"},[i("div",{staticClass:"sub-park",on:{click:function(t){e.closeOne("corner")}}}),e._v(" "),i("div",{staticClass:"corner"},[i("div",{staticClass:"time_chose fcg43 font32"},[i("div",[i("span",{staticClass:"l"},[e._v("车牌号:")]),e._v(" "),i("span",{staticClass:"r duan"},[i("input",{directives:[{name:"model",rawName:"v-model",value:e.carNumber,expression:"carNumber"}],staticClass:"font28",attrs:{type:"text",name:"",placeholder:"请添加车牌号"},domProps:{value:e.carNumber},on:{input:function(t){t.target.composing||(e.carNumber=t.target.value)}}})]),e._v(" "),i("span",{staticClass:"r font28",class:e.carHead?"":"fcgb1",on:{click:e.carChoose}},[e._v(e._s(e.carHead||"请选择"))])])]),e._v(" "),i("img",{attrs:{src:e.showImage?e.showImage:"static/img/no_park.png"},on:{click:e.choseImagrs}}),e._v(" "),i("input",{staticClass:"btn",attrs:{type:"button",value:"提交审核"},on:{click:function(t){e.subbmit("cengComm")}}})])]),e._v(" "),i("div",{staticClass:"bottom_box"}),e._v(" "),i("a",{staticClass:"add",on:{click:e.addLicense}},[i("img",{attrs:{src:a(70)}})]),e._v(" "),i("ul",{staticStyle:{display:"none"},attrs:{id:"carChoose"}},e._l(e.cityList,function(t){return i("li",[e._v(e._s(t))])}))])},staticRenderFns:[]}},30:function(e,t,a){function i(e){a(184)}var n=a(3)(a(106),a(253),i,"data-v-5715ed80",null);e.exports=n.exports},55:function(e,t,a){e.exports=a.p+"static/img/return.28908f5.png"},70:function(e,t,a){e.exports=a.p+"static/img/add.d1d914e.png"}});