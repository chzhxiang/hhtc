webpackJsonp([24],{131:function(e,t,a){t=e.exports=a(11)(!1),t.push([e.i,".license_plate[data-v-543bae4b]{width:100%;height:100%;position:relative;padding-bottom:1rem;overflow:hidden}.license_plate .top[data-v-543bae4b]{width:100%;height:.54rem;line-height:.54rem;text-align:center;background:#fff;position:absolute;top:0;z-index:2;box-shadow:0 0 .1rem .01rem #dae7ed}.license_plate .top i[data-v-543bae4b]{display:inline-block;width:.24rem;height:.28rem;background:url("+a(56)+") no-repeat 50%;background-size:.24rem .28rem;vertical-align:middle;margin-right:.12rem}.license_plate .content[data-v-543bae4b]{background:#e8e4e2;width:100%;height:100%;padding-top:.54rem;position:absolute;top:0}.title[data-v-543bae4b]{height:1.32rem;line-height:1.32rem;text-align:center;margin-top:.14rem}.choseplate[data-v-543bae4b]{height:.84rem;line-height:.84rem;padding:0 .24rem}.choseplate span[data-v-543bae4b]{display:inline-block;height:.84rem;border-radius:.16rem;box-shadow:0 0 .1rem .01rem #dae7ed;background:#fff;overflow:hidden}.choseplate span[data-v-543bae4b]:first-child{width:1.69rem;background:#fff url("+a(52)+") no-repeat 1.22rem;background-size:.16rem .23rem;padding-left:.46rem;margin-right:.36rem}.choseplate span[data-v-543bae4b]:nth-child(2){width:4.96rem}.choseplate span input[data-v-543bae4b]{display:inline-block;width:100%;height:.5rem;line-height:.5rem;padding-left:.44rem;background:#fff}.add[data-v-543bae4b]{width:3.7rem;height:.88rem;line-height:.88rem;text-align:center;background:#0098db;border-radius:.44rem;margin:1.16rem auto .54rem}.title2[data-v-543bae4b]{height:.9rem;line-height:.9rem;text-align:center}.time_chose[data-v-543bae4b]{width:7rem;border-radius:.16rem;box-shadow:0 0 .1rem .01rem #dae7ed;margin:.12rem auto .44rem;background:#fff;padding:.13rem .3rem}.time_chose div[data-v-543bae4b]{height:.9rem;line-height:.9rem;overflow:hidden}",""])},168:function(e,t,a){var r=a(131);"string"==typeof r&&(r=[[e.i,r,""]]),r.locals&&(e.exports=r.locals);a(12)("25351403",r,!0)},240:function(e,t){e.exports={render:function(){var e=this,t=e.$createElement,a=e._self._c||t;return a("div",{staticClass:"license_plate"},[a("div",{staticClass:"top font24 fcg48"},[a("i"),e._v(e._s(e.fansInfo.carOwnerCommunityName||e.fansInfo.carParkCommunityName))]),e._v(" "),a("div",{staticClass:"content"},[a("div",{staticClass:"title font38 fcg43"},[e._v("选择车牌")]),e._v(" "),a("div",{staticClass:"choseplate"},[a("span",{on:{click:e.choseCar}},[e._v(e._s(e.carHead||"请选择"))]),a("span",[a("input",{directives:[{name:"model",rawName:"v-model",value:e.carNumberRear,expression:"carNumberRear"}],staticClass:"font28",attrs:{type:"text",name:"",placeholder:"请输入车牌号"},domProps:{value:e.carNumberRear},on:{input:function(t){t.target.composing||(e.carNumberRear=t.target.value)}}})])]),e._v(" "),a("div",{staticClass:"add fcw font34",on:{click:e.checkout}},[e._v("确认添加")]),e._v(" "),a("div",{directives:[{name:"show",rawName:"v-show",value:e.carNmberList.length>0,expression:"carNmberList.length>0"}],staticClass:"title2 font38 fcg43"},[e._v("使用过的车牌")]),e._v(" "),a("div",{directives:[{name:"show",rawName:"v-show",value:e.carNmberList.length>0,expression:"carNmberList.length>0"}],staticClass:"time_chose fcg43 font32"},e._l(e.carNmberList,function(t){return a("div",{on:{click:function(a){e.goCheckout(t)}}},[a("span",{staticClass:"l"},[e._v("车牌")]),a("span",{staticClass:"r"},[e._v(e._s(e._f("carShow")(t)))])])}))]),e._v(" "),a("ul",{staticStyle:{display:"none"},attrs:{id:"education_list"}},e._l(e.carList,function(t){return a("li",[a("div",{attrs:{"data-value":t}},[e._v(e._s(t))]),e._v(" "),a("ul",e._l(e.carList2,function(t){return a("li",{attrs:{"data-value":t}},[e._v(e._s(t))])}))])}))])},staticRenderFns:[]}},30:function(e,t,a){function r(e){a(168)}var i=a(3)(a(96),a(240),r,"data-v-543bae4b",null);e.exports=i.exports},52:function(e,t,a){e.exports=a.p+"static/img/sanjiao.c1357e8.png"},56:function(e,t,a){e.exports=a.p+"static/img/icon.66d3b8d.png"},96:function(e,t){e.exports={devtool:"cheap-module-source-map",data:function(){return{fansInfo:{},carNmberList:[],carList:["京","津","冀","渝","晋","蒙","辽","吉","黑","沪","苏","浙","皖","闽","赣","鲁","豫","鄂","湘","粤","桂","琼","渝","川","贵","云","藏","陕","甘","青","宁","新"],carList2:["A","B","C","D","E","F","G","H","I","J","K","L","M","N","O","P","Q","R","S","T","U","V","W","X","Y","Z"],carHead:"",carNumberRear:"",maxLength:5}},mounted:function(){},activated:function(){this.carHead="",this.carNumberRear="",this.getFansInfo(),this.srcollInit()},created:function(){},watch:{carHead:function(){-1!=this.carHead.indexOf("沪")?this.maxLength=6:this.maxLength=5},carNumberRear:function(){this.carNumberRear=this.carNumberRear.replace(/[^a-zA-Z0-9]/g,""),this.carNumberRear.length>this.maxLength&&(this.carNumberRear=this.carNumberRear.substr(0,this.maxLength))}},methods:{srcollInit:function(){var e=this;setTimeout(function(){$("#education_list").mobiscroll().treelist({theme:"",lang:"zh",display:"bottom",headerText:function(e){},onSelect:function(t,a){var r=t.split(" ");e.carHead=e.carList[r[0]]+""+e.carList2[r[1]]}})},100)},choseCar:function(){$("input[id^=education_list_dummy]").focus()},goCheckout:function(e){localStorage.setItem("carNumber",e),setTimeout(function(){history.back()},100)},checkout:function(){var e=this;if(!this.carHead||this.carNumberRear.length<5)return error_msg("请填写完整的车牌号",2e3),!1;$.ajax({url:urlDir+"/wx/fans/carNumber/add",type:"POST",data:{carNumber:this.carHead+this.carNumberRear},success:function(t){0==t.code&&(localStorage.setItem("carNumber",e.carHead+e.carNumberRear),setTimeout(function(){history.back()},100))},error:function(e){}})},getFansInfo:function(){var e=this;$.ajax({url:urlDir+"/wx/fans/get",type:"GET",success:function(t){0==t.code&&(e.fansInfo=t.data,e.carNmberList=t.data.carNumber.split("`"),localStorage.setItem("fansInfo",toJson(t.data)))},error:function(e){}})}}}}});