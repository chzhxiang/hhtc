webpackJsonp([32],{100:function(e,t,o){"use strict";Object.defineProperty(t,"__esModule",{value:!0});var r=o(56);t.default={devtool:"cheap-module-source-map",data:function(){return{options:[],selected:"",doorInfo:""}},mounted:function(){},activated:function(){r.a.$emit("changPosition","2")},created:function(){this.initAddr()},watch:{},methods:{getFansInfo:function(){var e=this;$.ajax({url:urlDir+"/wx/fans/get",type:"GET",success:function(t){0==t.code&&(e.phone=t.data.phoneNo)},error:function(e){}})},submit:function(){var e=this;if(null==e.selected||""==e.selected)return error_msg("请选择地址",2e3),!1;if(null==e.doorInfo||""==e.doorInfo)return error_msg("请填写正确门牌信息",2e3),!1;var t=3;$.ajax({url:urlDir+"/wx/fans/infor/communityBind",type:"POST",data:{communityID:e.selected,houseNumber:e.doorInfo},success:function(o){if(0==o.code)switch(t=e.checkUserPosition(o.data.infor_state)){case 3:e.goRouter("/guide_page3");break;case 4:e.goRouter("/park_cantabile");break;default:e.goRouter("/guide_page3")}else error_msg(o.code+" 错误"+o.msg,2e3)},error:function(e){error_msg("网络错误",2e3)}})},checkUserPosition:function(e){if(e+="",5!=e.length)return-1;var t=(e.charAt(0),e.charAt(1),e.charAt(2),e.charAt(3)),o=e.charAt(4);return 1*t==0&&1*o==0?3:4},initAddr:function(){var e=this;$.ajax({url:urlDir+"/wx/community/listByCityName?cityName=重庆市",type:"GET",success:function(t){0==t.code?(e.options=[],t.data.forEach(function(t,o){e.options.push({id:t.id,name:t.name})}),e.options.length<1?error_msg("当前没有小区提供选择",2e3):e.selected=e.options[0].id):error_msg("错误",2e3)},error:function(e){error_msg("网络错误",2e3)}})},goRouter:function(e){this.$router.push({path:e})}}}},154:function(e,t,o){t=e.exports=o(11)(!1),t.push([e.i,".corner[data-v-99a1501a]{width:85%;height:3rem;border-radius:10px;margin:20px auto;text-align:center;background:linear-gradient(#fff,#e9e9e9)}.num[data-v-99a1501a],.select[data-v-99a1501a]{width:75%;height:.65rem;font-size:12px;margin:0 auto;margin-top:10px;background-color:transparent}.num[data-v-99a1501a]{border-bottom:1px solid #ccc}.btn[data-v-99a1501a]{width:50%;height:.5rem;margin:0 auto;margin-top:10px;font-size:12px;border-radius:5px;color:#fff;background-color:#52e6ff}.bottom[data-v-99a1501a]{position:absolute;bottom:0;left:0;width:100%;height:1rem;line-height:1rem;text-align:center;color:#fff;font-size:14px;background-color:#6781ec}",""])},192:function(e,t,o){var r=o(154);"string"==typeof r&&(r=[[e.i,r,""]]),r.locals&&(e.exports=r.locals);o(12)("11123ed4",r,!0)},24:function(e,t,o){function r(e){o(192)}var n=o(3)(o(100),o(262),r,"data-v-99a1501a",null);e.exports=n.exports},262:function(e,t){e.exports={render:function(){var e=this,t=e.$createElement,o=e._self._c||t;return o("div",{staticClass:"guide_page2"},[o("div",{staticClass:"corner"},[o("div",[o("select",{directives:[{name:"model",rawName:"v-model",value:e.selected,expression:"selected"}],staticClass:"select",on:{change:function(t){var o=Array.prototype.filter.call(t.target.options,function(e){return e.selected}).map(function(e){return"_value"in e?e._value:e.value});e.selected=t.target.multiple?o:o[0]}}},e._l(e.options,function(t){return o("option",{domProps:{value:t.id}},[e._v(e._s(t.name))])}))]),e._v(" "),o("div",[o("input",{directives:[{name:"model",rawName:"v-model",value:e.doorInfo,expression:"doorInfo"}],staticClass:"num",attrs:{type:"text",placeholder:"请输入门牌信息"},domProps:{value:e.doorInfo},on:{input:function(t){t.target.composing||(e.doorInfo=t.target.value)}}})]),e._v(" "),o("div",[o("input",{staticClass:"btn",attrs:{type:"button",value:"提交审核"},on:{click:e.submit}})])])])},staticRenderFns:[]}},56:function(e,t,o){"use strict";o.d(t,"a",function(){return n});var r=o(0),n=new r.a}});