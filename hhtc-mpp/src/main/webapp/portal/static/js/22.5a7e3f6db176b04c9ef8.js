webpackJsonp([22],{110:function(e,t){e.exports={devtool:"cheap-module-source-map",data:function(){return{parkList:[{id:"1",communityName:"Name1",carParkNumber:"Num1"},{id:"2",communityName:"Name2",carParkNumber:"Num2"},{id:"3",communityName:"Name3",carParkNumber:"Num3"},{id:"4",communityName:"Name4",carParkNumber:"Num4"}],selected:"",beginDay:"2017-12-12",beginTime:"12:00",endDay:"2018-12-12",endTime:"12:00",hourList:["00","01","02","03","04","05","06","07","08","09","10","11","12","13","14","15","16","17","18","19","20","21","22","23"],minutesList:["00","30"],timeChoose:0,beginFlag:!0,endFlag:!0}},mounted:function(){},activated:function(){this.srcollInit(),this.initPark()},created:function(){},watch:{},methods:{goRouter:function(e,t){this.$router.push({path:e})},initPark:function(){var e=this;$.ajax({url:urlDir+"/wx/market/post/getcarparks",type:"GET",success:function(t){0==t.code?(e.parkList=[],t.data.forEach(function(t,a){e.parkList.push({id:t.id,carParkNumber:t.carParkNumber,communityName:t.communityName})}),e.parkList.length<1?error_msg("您当前没有车位提供发布",2e3):e.selected=e.parkList[0].id):error_msg("错误",2e3)},error:function(e){error_msg("网络错误",2e3)}})},getdaynight:function(){$.ajax({url:urlDir+"/wx/common/daynight",type:"GET",success:function(e){0==e.code&&localStorage.setItem("daynight",toJson({timeDay:(e.data.timeDay/100>=10?e.data.timeDay/100:"0"+e.data.timeDay/100)+"",timeNight:(e.data.timeNight/100>=10?e.data.timeNight/100:"0"+e.data.timeNight/100)+""}))},error:function(e){}})},beginTimeChoose:function(){this.timeChoose=0,$("#timeChooseList_dummy").focus()},endTimeChoose:function(){this.timeChoose=1,$("#timeChooseList_dummy").focus()},srcollInit:function(){var e=this,t=[[{values:["2016","2017","2018","2019","2020","2021","2022","2024","2025"],label:"年"}],[{values:["01","02","03","04","05","06","07","08","09","10","11","12"],label:"月"}],[{values:["01","02","03","04","05","06","07","08","09","10","11","12","13","14","15","16","17","18","19","20","21","22","23","24","25","26","27","28","29","30","31"],label:"日"}],[{values:["00","01","02","03","04","05","06","07","08","09","10","11","12","13","14","15","16","17","18","19","20","21","22","23"],label:"时"}],[{values:["00","30"],label:"分"}]];setTimeout(function(){var a=new Date,i=a.getFullYear(),s=a.getMonth(),n=a.getDay(),r=a.toLocaleDateString(),o=a.getHours(),l=a.getMinutes(),c=t[0][0].values[1*i-2016],d=t[1][0].values[1*s-1],u=t[2][0].values[1*n-1],m=t[3][0].values[1*o-1],g=l<30?t[4][0].values[0]:t[4][0].values[1];$("#beginDayChoose").mobiscroll().scroller({theme:"android-ics",lang:"zh",showLabel:!0,rows:3,display:"bottom",wheels:t,defaultValue:[c,d,u,m,g],formatResult:function(t){console.log(t),error_msg(t[0]+"-"+t[1]+"-"+t[2]+"-"+t[3]+"-"+t[4],2e3),e.beginFlag?(e.beginFlag=!1,e.beginDay=r.replace(/\//g,"-"),e.beginTime=o+":"+(l<30?"00":"30")):(e.beginDay=t[0]+"-"+t[1]+"-"+t[2],e.beginTime=t[3]+":"+t[4])}}),$("#endDayChoose").mobiscroll().scroller({theme:"android-ics",lang:"zh",showLabel:!0,rows:3,display:"bottom",wheels:t,defaultValue:[c,d,u,m,g],formatResult:function(t){console.log(t),e.endFlag?(e.endFlag=!1,e.endDay=r.replace(/\//g,"-"),e.endTime=o+":"+(l<30?"00":"30")):(e.endDay=t[0]+"-"+t[1]+"-"+t[2],e.endTime=t[3]+":"+t[4])}})},100)},publish:function(){var e=this,t=e.beginDay.replace(/-/g,"")+e.beginTime.replace(/:/g,""),a=e.endDay.replace(/-/g,"")+e.endTime.replace(/:/g,""),i=new Date,s=i.toLocaleDateString(),n=i.getHours(),r=i.getMinutes(),o=s.replace(/\//g,"")+""+n+r;null==e.selected||""==e.selected?error_msg("请先选择要发布的车位",2e3):isNaN(t)||isNaN(a)||1*t>=1*a?error_msg("错误的起止时间，请检查",2e3):1*t<=o?error_msg("开始时间必须大于当前时间",2e3):$.ajax({url:urlDir+"/wx/market/post/postCarpark",type:"POST",data:{goodsId:e.selected,starttime:e.beginDay+" "+e.beginTime,endtime:e.endDay+" "+e.endTime},success:function(t){0==t.code?(error_msg("发布成功",2e3),e.parkList.forEach(function(t,a){t.id==e.selected&&e.parkList.splice(a,1)}),e.beginDay="----/--/--",e.beginTime="--:--",e.endDay="----/--/--",e.endTime="--:--"):error_msg(t.code+" 错误 "+t.msg,2e3)},error:function(e){error_msg("网络错误",2e3)}})},choseImagrs:function(){var e=this;wx.chooseImage({count:1,sizeType:["original"],sourceType:["album","camera"],success:function(t){e.showImage=t.localIds[0],wx.uploadImage({localId:t.localIds[0],isShowProgressTips:1,success:function(t){e.imgUpload(t.serverId)}})}})}}}},150:function(e,t,a){t=e.exports=a(11)(!1),t.push([e.i,".publish_park_cantabile[data-v-f5267496]{width:100%;height:100%;text-align:center;padding:8%}.head img[data-v-f5267496]{width:50%;margin:0 auto}.parkChoose[data-v-f5267496]{width:100%;height:50px;background:linear-gradient(#fff,#e9e9e9);border-radius:12px;line-height:50px;padding:0 10px;margin:15px 0}.parkChoose span[data-v-f5267496]{width:100%;text-align:left}.parkChoose select[data-v-f5267496]{width:70%;height:28px;float:right;margin:11px 0;background-color:#f9f9f9}.timeChoose[data-v-f5267496]{width:100%;height:80px;border-radius:12px;padding:0 10px;background:linear-gradient(#fff,#e9e9e9)}.imgCenter[data-v-f5267496]{width:15px;height:15px;margin:12px 10px}.begin[data-v-f5267496],.end[data-v-f5267496]{width:100%;height:50%;line-height:40px}.publish[data-v-f5267496]{width:100%;height:50px;margin:10px 0;line-height:50px;padding-left:30px;padding-right:30px}.publish input[data-v-f5267496]{width:100%;height:60%;border-radius:7px;color:#fff;background-color:#ff383f}.margin-l[data-v-f5267496]{margin-left:10px}",""])},187:function(e,t,a){var i=a(150);"string"==typeof i&&(i=[[e.i,i,""]]),i.locals&&(e.exports=i.locals);a(12)("af13ea58",i,!0)},206:function(e,t,a){e.exports=a.p+"static/img/logo4.4cf6b16.png"},259:function(e,t,a){e.exports={render:function(){var e=this,t=e.$createElement,i=e._self._c||t;return i("div",{staticClass:"publish_park_cantabile"},[e._m(0),e._v(" "),i("div",{staticClass:"parkChoose"},[i("span",{staticClass:"l font34"},[e._v("车位选择: "),i("select",{directives:[{name:"model",rawName:"v-model",value:e.selected,expression:"selected"}],on:{change:function(t){var a=Array.prototype.filter.call(t.target.options,function(e){return e.selected}).map(function(e){return"_value"in e?e._value:e.value});e.selected=t.target.multiple?a:a[0]}}},e._l(e.parkList,function(t){return i("option",{domProps:{value:t.id}},[e._v(e._s(t.communityName+" "+t.carParkNumber))])}))])]),e._v(" "),i("div",{staticClass:"timeChoose"},[i("div",{staticClass:"begin"},[i("span",{staticClass:"l font34"},[e._v("起始时间: ")]),i("span",{staticClass:"r font28 margin-l",attrs:{id:"beginTimeChoose"},on:{click:e.beginTimeChoose}},[e._v(e._s(e.beginTime)+" "),i("img",{staticClass:"r imgCenter",attrs:{src:a(78)}})]),i("span",{staticClass:"r font28",attrs:{id:"beginDayChoose"}},[e._v(e._s(e.beginDay))])]),e._v(" "),i("div",{staticClass:"end"},[i("span",{staticClass:"l font34"},[e._v("结束时间: ")]),i("span",{staticClass:"r font28 margin-l",attrs:{id:"endTimeChoose"},on:{click:e.endTimeChoose}},[e._v(e._s(e.endTime)+" "),i("img",{staticClass:"r imgCenter",attrs:{src:a(78)}})]),i("span",{staticClass:"r font28",attrs:{id:"endDayChoose"}},[e._v(e._s(e.endDay))])])]),e._v(" "),i("div",{staticClass:"publish"},[i("input",{staticClass:"font36",attrs:{type:"button",value:"发布车位"},on:{click:e.publish}})]),e._v(" "),i("ul",{staticStyle:{display:"none"},attrs:{id:"timeChooseList"}},e._l(e.hourList,function(t){return i("li",[i("div",{attrs:{"data-value":t}},[e._v(e._s(t))]),e._v(" "),i("ul",e._l(e.minutesList,function(t){return i("li",{attrs:{"data-value":t}},[e._v(e._s(t))])}))])}))])},staticRenderFns:[function(){var e=this,t=e.$createElement,i=e._self._c||t;return i("div",{staticClass:"head"},[i("img",{attrs:{src:a(206)}})])}]}},44:function(e,t,a){function i(e){a(187)}var s=a(3)(a(110),a(259),i,"data-v-f5267496",null);e.exports=s.exports},78:function(e,t,a){e.exports=a.p+"static/img/time_choose.e868de7.png"}});