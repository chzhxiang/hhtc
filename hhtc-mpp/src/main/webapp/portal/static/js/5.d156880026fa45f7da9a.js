webpackJsonp([5],{108:function(e,t,a){var i=a(85);"string"==typeof i&&(i=[[e.i,i,""]]),i.locals&&(e.exports=i.locals);a(12)("62967e8b",i,!0)},131:function(e,t,a){e.exports=a.p+"static/img/login_chose.b039172.png"},132:function(e,t,a){e.exports=a.p+"static/img/login_chose_no.c23ea41.png"},133:function(e,t,a){e.exports=a.p+"static/img/login_password.b45fe37.png"},134:function(e,t,a){e.exports=a.p+"static/img/login_phone.f4f6bb0.png"},162:function(e,t){e.exports={render:function(){var e=this,t=e.$createElement,a=e._self._c||t;return a("div",{staticClass:"login"},[a("div",{staticClass:"tab font36"},[e.showCarOwner?a("span",{class:1==e.userType?"curr":"",on:{click:function(t){e.changeUserType(1)}}},[e._v("我是车主")]):e._e(),e.showCarPark?a("span",{class:2==e.userType?"curr":"",on:{click:function(t){e.changeUserType(2)}}},[e._v("我是车位主")]):e._e()]),e._v(" "),a("div",{staticClass:"phone input"},[a("input",{directives:[{name:"model",rawName:"v-model",value:e.phone,expression:"phone"}],staticClass:"phone_number font28",attrs:{type:"text",readonly:e.canEnter,placeholder:"请输入您的手机号"},domProps:{value:e.phone},on:{input:function(t){t.target.composing||(e.phone=t.target.value)}}})]),e._v(" "),a("div",{staticClass:"code input"},[a("input",{directives:[{name:"model",rawName:"v-model",value:e.code,expression:"code"}],staticClass:"font28",attrs:{type:"text",placeholder:"请输入验证码"},domProps:{value:e.code},on:{input:function(t){t.target.composing||(e.code=t.target.value)}}}),a("span",{directives:[{name:"show",rawName:"v-show",value:e.iscanalign,expression:"iscanalign"}],staticClass:"centered_y fcw",on:{click:e.sendCode}},[e._v("获取验证码")]),a("span",{directives:[{name:"show",rawName:"v-show",value:!e.iscanalign,expression:"!iscanalign"}],staticClass:"centered_y fcw"},[e._v("重新获取("+e._s(e.alignTime)+")s")])]),e._v(" "),a("div",{directives:[{name:"show",rawName:"v-show",value:1==e.userType,expression:"userType==1"}],staticClass:"time_chose fcg43 font32"},[a("div",[a("span",{staticClass:"l"},[e._v("小区名称:")]),a("span",{staticClass:"r font28"},[a("i",{class:"请选择小区"==e.nowName.name?"fcgb1":"",on:{click:function(t){e.show("ceng_district")}}},[e._v(e._s(e.nowName.name))])])]),e._v(" "),a("div",[a("span",{staticClass:"l"},[e._v("车牌号:")]),a("span",{staticClass:"r duan"},[a("input",{directives:[{name:"model",rawName:"v-model",value:e.carNumber,expression:"carNumber"}],staticClass:"font28",attrs:{type:"text",name:"",placeholder:"请添加车牌号"},domProps:{value:e.carNumber},on:{input:function(t){t.target.composing||(e.carNumber=t.target.value)}}})]),a("span",{staticClass:"r font28",class:e.carHead?"":"fcgb1",on:{click:e.choseCar}},[e._v(e._s(e.carHead||"请选择"))])]),e._v(" "),a("div",[a("span",{staticClass:"l"},[e._v("门牌号:")]),a("span",{staticClass:"r"},[a("input",{directives:[{name:"model",rawName:"v-model",value:e.houseNumber,expression:"houseNumber"}],staticClass:"font28",attrs:{type:"text",placeholder:"请输入您的门牌号",name:""},domProps:{value:e.houseNumber},on:{input:function(t){t.target.composing||(e.houseNumber=t.target.value)}}})])])]),e._v(" "),a("div",{directives:[{name:"show",rawName:"v-show",value:2==e.userType,expression:"userType==2"}],staticClass:"time_chose fcg43 font32"},[a("div",[a("span",{staticClass:"l"},[e._v("车位所在小区:")]),a("span",{staticClass:"r font28"},[a("i",{class:"请选择小区"==e.nowName.name?"fcgb1":"",on:{click:function(t){e.show("ceng_district")}}},[e._v(e._s(e.nowName.name))])])]),e._v(" "),a("div",[a("span",{staticClass:"l"},[e._v("车位号:")]),a("span",{staticClass:"r duan font28"},[a("input",{directives:[{name:"model",rawName:"v-model",value:e.carParkNumber,expression:"carParkNumber"}],staticClass:"font28",attrs:{type:"text",name:"",placeholder:"请添加车位号"},domProps:{value:e.carParkNumber},on:{input:function(t){t.target.composing||(e.carParkNumber=t.target.value)}}})]),a("span",{staticClass:"r font28",class:"请选择"==e.quyu?"fcgb1":"",on:{click:e.choseQuyu}},[e._v(e._s(e.quyu))]),a("span",{staticClass:"r",class:"请选择"==e.floor?"fcgb1":"",on:{click:e.choseFloor}},[e._v(e._s(e.floor))])]),e._v(" "),a("div",[a("span",{staticClass:"l"},[e._v("车位有效期:")]),a("span",{staticClass:"r font28",attrs:{id:"education_list3"}},[e._v(e._s(e.endDay))]),a("span",{staticClass:"r font28",staticStyle:{"margin-right":".2rem"},attrs:{id:"education_list4"}},[e._v(e._s(e.beginDay))])])]),e._v(" "),a("div",{staticClass:"protocol fcb font18",class:e.readProtocol?"curr":""},[a("i",{on:{click:e.changeProtocol}}),a("span",{},[e._v("我同意 "),a("em",{on:{click:function(t){e.goRouter("/protocol")}}},[e._v("《吼吼停车服务协议》")])])]),e._v(" "),a("div",{staticClass:"checkout fcw font40",on:{click:e.login}},[e._v("提交注册申请")]),e._v(" "),a("div",{staticClass:"word fcb font18"},[e._v("*仅用于小区物业审核与平台注册，不向第三方泄露公开、透露个人信息")]),e._v(" "),a("div",{staticClass:"ceng ceng_result fcg95 font26",staticStyle:{display:"none"}},[a("div",{staticClass:"ceng_content centered",on:{click:function(t){e.emptyClick(t)}}},[a("div",{staticClass:"title fcb font38"},[e._v("注册成功")]),e._v(" "),a("div",{staticClass:"word"},[e._v("我们会在5个工作日内审核你的申请")]),e._v(" "),a("div",{staticClass:"word"},[e._v("如有任何疑问请拨打客服")]),e._v(" "),a("div",{staticClass:"word"},[e._v("电话：15023194640")]),e._v(" "),a("div",{directives:[{name:"show",rawName:"v-show",value:2==e.userType&&e.canRegistered,expression:"userType==2&&canRegistered"}],staticClass:"ceng_operating fcw font34",on:{click:function(t){e.close("ceng_result")}}},[e._v("继续注册成为车位主")]),e._v(" "),a("div",{directives:[{name:"show",rawName:"v-show",value:2==e.userType,expression:"userType==2"}],staticClass:"ceng_operating fcw font34",on:{click:function(t){e.goRouter("/park")}}},[e._v("去看看空余车位")]),e._v(" "),a("div",{directives:[{name:"show",rawName:"v-show",value:1==e.userType&&e.canRegistered,expression:"userType==1&&canRegistered"}],staticClass:"ceng_operating fcw font34",on:{click:function(t){e.close("ceng_result")}}},[e._v("继续注册成为车主")]),e._v(" "),a("div",{directives:[{name:"show",rawName:"v-show",value:1==e.userType,expression:"userType==1"}],staticClass:"ceng_operating fcw font34",on:{click:function(t){e.goRouter("/publish")}}},[e._v("去发布车位")])])]),e._v(" "),a("div",{staticClass:"ceng ceng_district fcg43 font30",staticStyle:{display:"none"},on:{click:function(t){e.close("ceng_district")}}},[a("div",{staticClass:"ceng_content centered",on:{click:function(t){e.emptyClick(t)}}},[a("div",{staticClass:"title font38"},[e._v("选择小区")]),e._v(" "),a("div",{staticClass:"word"},[a("span",{staticClass:"l"},[e._v("省:")]),a("span",{staticClass:"r",on:{click:e.getProvince}},[e._v(e._s(e.nowProvince))])]),e._v(" "),a("div",{staticClass:"word"},[a("span",{staticClass:"l"},[e._v("市:")]),a("span",{staticClass:"r",on:{click:e.getCity}},[e._v(e._s(e.nowCity))])]),e._v(" "),a("div",{staticClass:"word"},[a("span",{staticClass:"l"},[e._v("小区名称:")]),a("span",{staticClass:"r",on:{click:e.getName}},[e._v(e._s(e.nowName.name))])])])]),e._v(" "),a("ul",{staticStyle:{display:"none"},attrs:{id:"education_list"}},e._l(e.showList,function(t){return a("li",{attrs:{data:t}},[e._v(e._s(t.name))])})),e._v(" "),a("ul",{staticStyle:{display:"none"},attrs:{id:"education_list5"}},e._l(e.carParkNumberPrefix,function(t,i){return a("li",{attrs:{data:i}},[e._v(e._s(i))])})),e._v(" "),a("ul",{staticStyle:{display:"none"},attrs:{id:"education_list6"}},e._l(e.carParkNumberPre,function(t){return a("li",{attrs:{data:t}},[e._v(e._s(t))])})),e._v(" "),a("ul",{staticStyle:{display:"none"},attrs:{id:"education_list2"}},e._l(e.carList,function(t){return a("li",[a("div",{attrs:{"data-value":t}},[e._v(e._s(t))]),e._v(" "),a("ul",e._l(e.carList2,function(t){return a("li",{attrs:{"data-value":t}},[e._v(e._s(t))])}))])}))])},staticRenderFns:[]}},21:function(e,t,a){function i(e){a(108)}var r=a(3)(a(60),a(162),i,"data-v-441bca9c",null);e.exports=r.exports},36:function(e,t,a){e.exports=a.p+"static/img/yes.1465027.png"},38:function(e,t,a){e.exports=a.p+"static/img/sanjiao.c1357e8.png"},60:function(e,t){e.exports={devtool:"cheap-module-source-map",name:"login",data:function(){return{iscanalign:!0,time:"",alignTime:60,iscansend:!1,iscango:!1,phone:"",code:"",userType:1,nowProvince:"请选择省份",nowCity:"请选择城市",nowName:{name:"请选择小区"},showList:[],nowType:"",houseNumber:"",carNumber:"",carParkNumber:"",readProtocol:!0,carEquityImg:"",showImage:"",count:0,canRegistered:!0,isCanAjax:!0,carList:["京","津","冀","渝","晋","蒙","辽","吉","黑","沪","苏","浙","皖","闽","赣","鲁","豫","鄂","湘","粤","桂","琼","渝","川","贵","云","藏","陕","甘","青","宁","新"],carList2:["A","B","C","D","E","F","G","H","I","J","K","L","M","N","O","P","Q","R","S","T","U","V","W","X","Y","Z"],carHead:"",maxLength:5,userLoginInfo:{},beginDay:"开始日期",endDay:"结束日期",carParkNumberPrefix:{},carParkNumberPre:[],floor:"请选择",quyu:"请选择",showCarPark:!0,showCarOwner:!0,canEnter:!1}},mounted:function(){},created:function(){},activated:function(){this.close("ceng"),-1!=location.href.indexOf("userType")&&(this.userType=1*getUrl().userType),this.count=0,this.getFansInfo(),this.srcollInit()},watch:{floor:function(){this.carParkNumberPre=this.carParkNumberPrefix[this.floor]},phone:function(){/^1[3|4|5|7|8]\d{9}$/.test(this.phone)?(this.iscansend=!0,4==this.code.length&&(this.iscango=!0),this.canEnter):(this.iscansend=!1,this.iscango=!1)},code:function(){6==this.code.length&&/^1[3|4|5|7|8]\d{9}$/.test(this.phone)?this.iscango=!0:this.iscango=!1},carHead:function(){-1!=this.carHead.indexOf("沪")?this.maxLength=6:this.maxLength=5},carNumber:function(){this.carNumber=this.carNumber.replace(/[^a-zA-Z0-9]/g,""),this.carNumber.length>this.maxLength&&(this.carNumber=this.carNumber.substr(0,this.maxLength))}},methods:{verification:function(){var e=this;$.ajax({url:urlDir+"/wx/fans/isreg",type:"GET",data:{phoneNo:this.phone},success:function(t){0==t.code&&t.data&&(e.phone="",error_msg("该手机号已注册,请重新输入",2e3))},error:function(e){}})},srcollInit:function(){var e=this;setTimeout(function(){$("#education_list2").mobiscroll().treelist({theme:"",lang:"zh",display:"bottom",headerText:function(e){},onSelect:function(t,a){var i=t.split(" ");e.carHead=e.carList[i[0]]+""+e.carList2[i[1]]}});var t=(new Date).getFullYear();$("#education_list3").mobiscroll().date({theme:"",lang:"zh",display:"bottom",startYear:t-5,endYear:t+15,headerText:function(e){var t=e.split("/");return t[0]+"年"+t[1]+"月"+t[2]+"日"},onSelect:function(t,a){var i=t.split("/");return"开始日期"==e.beginDay?(error_msg("请先选择开始日期",2e3),!1):1*e.beginDay.replace(/-/g,"")>1*i.join("")?(error_msg("结束日期必须大于开始日期",2e3),e.endDay="结束日期",!1):void(e.endDay=i[0]+"-"+i[1]+"-"+i[2])}}),$("#education_list4").mobiscroll().date({theme:"",lang:"zh",display:"bottom",startYear:t-5,endYear:t+15,headerText:function(e){var t=e.split("/");return t[0]+"年"+t[1]+"月"+t[2]+"日"},onSelect:function(t,a){var i=t.split("/");"结束日期"!=e.endDay&&1*e.endDay.replace(/-/g,"")<1*i.join("")&&(error_msg("结束日期必须大于开始日期",2e3),e.endDay="结束日期"),e.beginDay=i[0]+"-"+i[1]+"-"+i[2]}})},100)},choseQuyu:function(){var e=this;return"-"!=e.quyu&&("请选择"==e.floor?(error_msg("请先选择楼层",2e3),!1):($("#education_list6").mobiscroll().treelist({theme:"",lang:"zh",display:"bottom",headerText:function(e){console.log(e)},onSelect:function(t,a){e.quyu=$("#education_list6").find("li").eq(t).text()}}),void $("input[id^=education_list6_dummy]").focus()))},choseFloor:function(){var e=this;if("{}"==toJson(this.carParkNumberPrefix))return error_msg("请先选择小区",2e3),!1;$("#education_list5").mobiscroll().treelist({theme:"",lang:"zh",display:"bottom",headerText:function(e){console.log(e)},onSelect:function(t,a){e.floor=$("#education_list5").find("li").eq(t).text(),e.carParkNumberPre=e.carParkNumberPrefix[e.floor],0==e.carParkNumberPre.length&&(e.quyu="-")}}),$("input[id^=education_list5_dummy]").focus()},choseCar:function(){$("input[id^=education_list2_dummy]").focus()},getUserCar:function(){var e=this;$.ajax({url:urlDir+"/wx/goods/listAllByOpenid",type:"GET",success:function(t){if(0==t.code){var a=t.data[0].carParkNumber.split(";");e.floor=a[0]||"-",e.quyu=a[1]||"-",e.carParkNumber=a[2]||"-"}},error:function(e){}})},getFansInfo:function(){var e=this;$.ajax({url:urlDir+"/wx/fans/get",type:"GET",success:function(t){0==t.code&&(e.userLoginInfo=t.data,t.data.phoneNo?(e.canEnter=!0,e.phone=t.data.phoneNo,e.nowName={name:t.data.carOwnerCommunityName||t.data.carParkCommunityName,id:t.data.carOwnerCommunityId||t.data.carParkCommunityId},e.getCommunityInfo(e.nowName.id),e.carNumber=t.data.carNumber.split("`")[0].substr(2),e.carHead=t.data.carNumber.split("`")[0].substr(0,2),e.houseNumber=t.data.houseNumber,2==t.data.carOwnerAuditStatus&&(e.showCarOwner=!1,e.userType=2),2==t.data.carParkAuditStatus?(e.showCarPark=!1,e.userType=1):1!=t.data.carParkAuditStatus&&3!=t.data.carParkAuditStatus||e.getUserCar()):e.canEnter=!1)},error:function(e){}})},getWx:function(){var e=this;$.ajax({url:urlDir+"/wx/jssdk/sign",type:"POST",data:{url:location.href},success:function(t){"string"==typeof t&&(t=toParse(t)),console.log(t),t.data.appid?wx.config({debug:!1,appId:t.data.appid,timestamp:parseInt(t.data.timestamp),nonceStr:t.data.noncestr,signature:t.data.signature,jsApiList:["chooseImage","uploadImage"]}):++e.count<4&&e.getWx()}})},getCommunityInfo:function(e){var t=this;e&&$.ajax({url:urlDir+"/wx/community/get",type:"GET",data:{id:e},success:function(e){0==e.code&&(t.nowProvince=e.data.provinceName,t.nowCity=e.data.cityName,t.carParkNumberPrefix=JSON.parse(e.data.carParkNumberPrefix),t.floor&&(t.carParkNumberPre=t.carParkNumberPrefix[t.floor]))}})},choseImagrs:function(){var e=this;wx.chooseImage({count:1,sizeType:["original"],sourceType:["album","camera"],success:function(t){e.showImage=t.localIds[0],wx.uploadImage({localId:t.localIds[0],isShowProgressTips:1,success:function(t){e.imgUpload(t.serverId)}})}})},imgUpload:function(e){var t=this;$.ajax({url:urlDir+"/wx/media/upload",type:"GET",data:{serverId:e},success:function(e){0==e.code&&(t.carEquityImg=e.data.filepath)},error:function(e){}})},changeProtocol:function(){this.readProtocol=!this.readProtocol},goRouter:function(e){this.$router.push({path:e})},show:function(e){$("."+e).css("display","block")},close:function(e){$("."+e).css("display","none")},emptyClick:function(e){e.preventDefault(),e.stopPropagation()},setNowTypeName:function(e){switch(this.nowType){case"province":this.nowProvince=e.name;break;case"city":this.nowCity=e.name;break;case"name":this.nowName=e,this.floor="请选择",this.quyu="请选择",this.carParkNumberPrefix=JSON.parse(e.carParkNumberPrefix),this.close("ceng_district")}this.showList=[]},setScrollList:function(){var e=this;$("#education_list").mobiscroll().treelist({theme:"",lang:"zh",display:"bottom",headerText:function(e){console.log(e)},onSelect:function(t,a){console.log(t,a),e.setNowTypeName(e.showList[t])}}),$("input[id^=education_list_dummy]").focus()},getProvince:function(){var e=this;$.ajax({url:urlDir+"/wx/community/province/list",type:"GET",success:function(t){0==t.code&&(e.nowType="province",e.showList=[],t.data.provinceNameList.forEach(function(t,a){e.showList.push({name:t})}),setTimeout(function(){e.setScrollList()},100))},error:function(e){}})},getCity:function(){var e=this;"请选择省份"!=this.nowProvince?$.ajax({url:urlDir+"/wx/community/city/list",type:"GET",data:{provinceName:this.nowProvince},success:function(t){0==t.code&&(e.nowType="city",e.showList=[],t.data.cityNameList.forEach(function(t,a){e.showList.push({name:t})}),setTimeout(function(){e.setScrollList()},100))},error:function(e){}}):error_msg("请先选择省份",2e3)},getName:function(){var e=this;"请选择城市"!=this.nowCity?$.ajax({url:urlDir+"/wx/community/listByCityName",type:"GET",data:{cityName:this.nowCity},success:function(t){0==t.code&&(e.nowType="name",e.showList=t.data,setTimeout(function(){e.setScrollList()},100))},error:function(e){}}):error_msg("请先选择城市",2e3)},changeUserType:function(e){this.userType=e},alignGo:function(){var e=this;clearInterval(this.time),e.iscanalign=!1,this.time=setInterval(function(){e.alignTime-=1,e.alignTime<0&&(e.iscanalign=!0,e.alignTime=60,clearInterval(e.time))},1e3)},sendCode:function(){var e=this;this.iscansend?(this.iscansend=!1,$.ajax({url:urlDir+"/wx/common/sms/send",type:"GET",data:{phoneNo:this.phone,type:this.userType},success:function(t){e.iscansend=!0,0==t.code?(e.alignTime=60,e.alignGo()):3010!=t.code&&3011!=t.code&&3012!=t.code||(error_msg("短信发布太频繁",2e3),e.alignTime=1*t.data,e.alignGo())},error:function(e){}})):error_msg("请输入正确的手机号",2e3)},login:function(){var e=this;if(this.isCanAjax){if(!this.readProtocol)return error_msg("请先阅读并同意《吼吼停车服务协议》",2e3),!0;if(!this.iscango)return error_msg("请输入正确的手机号与验证码",2e3),!0;if(!this.nowName.id)return error_msg("请先选择小区名称",2e3),!0;if(1==this.userType){if(!this.carHead)return error_msg("请先选择车牌号",2e3),!0;if(!this.carNumber)return error_msg("请先输入车牌号码",2e3),!0;if(!this.houseNumber)return error_msg("请先输入门牌号码",2e3),!0;this.isCanAjax=!1,$.ajax({url:urlDir+"/wx/fans/reg/carOwner",type:"POST",data:{phoneNo:this.phone,verifyCode:this.code,carOwnerCommunityId:this.nowName.id,carNumber:this.carHead+this.carNumber,houseNumber:this.houseNumber},success:function(t){e.isCanAjax=!0,error_msg(t.msg,2e3),0==t.code&&(e.code="",e.userType=2,$(".phone_number").attr("readonly"),e.show("ceng_result"),e.canRegistered=t.data)},error:function(t){e.isCanAjax=!0}})}else{if(!this.carParkNumber)return error_msg("请先输入车位号",2e3),!0;if("开始日期"==this.beginDay||"结束日期"==this.endDay)return error_msg("请选择车位起止日期",2e3),!0;if("请选择"==this.floor||"请选择"==this.quyu)return error_msg("请选择车位号区域",2e3),!0;this.isCanAjax=!1,$.ajax({url:urlDir+"/wx/fans/reg/carPark",type:"POST",data:{phoneNo:this.phone,verifyCode:this.code,carParkCommunityId:this.nowName.id,carParkNumber:this.floor+";"+("-"==this.quyu?"":this.quyu)+";"+this.carParkNumber,carUsefulFromDate:this.beginDay.replace(/-/g,""),carUsefulEndDate:this.endDay.replace(/-/g,"")},success:function(t){error_msg(t.msg,2e3),e.isCanAjax=!0,0==t.code&&($(".phone_number").attr("readonly"),e.code="",e.userType=1,e.show("ceng_result"),e.canRegistered=t.data)},error:function(t){e.isCanAjax=!0}})}}}}}},85:function(e,t,a){t=e.exports=a(11)(!1),t.push([e.i,".login[data-v-441bca9c]{width:100%;height:100%;overflow-y:auto;background:#fff}.input[data-v-441bca9c]{height:.76rem;line-height:.76rem;width:5.9rem;margin:.6rem auto 0;border-bottom:1px solid #959595;background:url("+a(134)+") no-repeat .2rem;background-size:.34rem .44rem;padding-left:1.12rem}.input input[data-v-441bca9c]{height:.7rem;line-height:.7rem}.code[data-v-441bca9c]{background-image:url("+a(133)+");position:relative}.code span[data-v-441bca9c]{display:block;padding:0 .24rem;border-radius:.24rem;height:.48rem;line-height:.48rem;text-align:center;right:0;background:#0098db}.tab[data-v-441bca9c]{height:2.1rem;line-height:2.1rem;text-align:center}.tab span[data-v-441bca9c]{display:inline-block;width:2.62rem;height:.86rem;line-height:.86rem;border:1px solid #0098db;color:#0098db;border-radius:.43rem;margin-right:.64rem}.tab span[data-v-441bca9c]:last-child{margin-right:0}.tab span.curr[data-v-441bca9c]{background:#0098db;color:#fff}.time_chose[data-v-441bca9c]{width:7rem;border-radius:.16rem;box-shadow:0 0 .1rem .01rem #dae7ed;margin:.6rem auto .44rem;background:#fff;padding:.13rem .3rem}.time_chose div[data-v-441bca9c]{height:.9rem;line-height:.9rem;overflow:hidden}.time_chose div span.r[data-v-441bca9c]{background:url("+a(38)+") no-repeat 100%;background-size:.16rem .23rem;padding-right:.3rem;padding-left:.15rem}.time_chose div span.r input[data-v-441bca9c]{text-align:right;width:3.4rem;height:.5rem;line-height:.5rem}.time_chose div span.duan input[data-v-441bca9c]{width:2rem;height:.5rem;line-height:.5rem}.protocol[data-v-441bca9c]{height:1rem;line-height:1rem;text-align:center}.protocol i[data-v-441bca9c]{width:.3rem;height:.3rem;background:url("+a(132)+") no-repeat 50%;background-size:.3rem .3rem;margin-right:.2rem}.protocol i[data-v-441bca9c],.protocol span[data-v-441bca9c]{display:inline-block;vertical-align:middle}.protocol.curr i[data-v-441bca9c]{background-image:url("+a(131)+")}.checkout[data-v-441bca9c]{width:5rem;height:.88rem;line-height:.88rem;text-align:center;border-radius:.44rem;background:#0098db;margin:0 auto}.word[data-v-441bca9c]{height:.84rem;line-height:.84rem;text-align:center}.ceng[data-v-441bca9c]{width:100%;height:100%;position:absolute;top:0;left:0;background:hsla(0,0%,100%,.5)}.ceng .ceng_content[data-v-441bca9c]{width:5.9rem;background:#fff;border-radius:.24rem;padding:0 .46rem;box-shadow:0 0 1rem 0 #cfebf8}.ceng_result .ceng_content[data-v-441bca9c]{background:#fff url("+a(36)+") no-repeat center .4rem;background-size:1.98rem 1.98rem;padding-top:2.38rem}.ceng_result .title[data-v-441bca9c]{height:1.2rem;line-height:1.2rem;text-align:center}.ceng_result .word[data-v-441bca9c]{height:.6rem;line-height:.6rem;text-align:center}.ceng_result .ceng_operating[data-v-441bca9c]{width:3.7rem;height:.8rem;line-height:.8rem;background:#0098db;margin:.2rem auto .4rem;border-radius:.4rem;text-align:center}.ceng_result .ceng_operating[data-v-441bca9c]:last-child{background:#fff;border:1px solid #0098db;color:#0098db}.ceng_district[data-v-441bca9c]{padding:.12rem 0 .6rem}.ceng_district .title[data-v-441bca9c]{height:1.14rem;line-height:1.14rem;text-align:center}.ceng_district .word[data-v-441bca9c]{height:.96rem;line-height:.96rem}.ceng_district .word span.r[data-v-441bca9c]{background:url("+a(38)+") no-repeat 100%;background-size:.16rem .23rem;padding-right:.44rem}.show_list[data-v-441bca9c]{width:100%;height:100%;overflow:hidden;position:absolute;top:0;left:0;z-index:20;background:rgba(0,0,0,.5)}.show_list .content[data-v-441bca9c]{max-height:5rem;overflow-y:auto;width:100%;position:absolute;bottom:0;background:#fff}.show_list .content p[data-v-441bca9c]{line-height:.5rem;text-align:center}input[data-v-441bca9c]{background:#fff}",""])}});