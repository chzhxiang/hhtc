webpackJsonp([36],{153:function(e,t,r){t=e.exports=r(11)(!1),t.push([e.i,".corner[data-v-f7c4446c]{width:85%;height:2rem;border-radius:10px;background:linear-gradient(#fff,#e9e9e9);margin:20px auto;text-align:center}.num[data-v-f7c4446c]{width:75%;height:.65rem;font-size:12px;margin:0 auto;margin-top:10px;background-color:transparent;border-bottom:1px solid #ccc}.btn[data-v-f7c4446c]{width:30%;height:.5rem;margin:0 10px;margin-top:10px;font-size:12px;border-radius:5px;color:#fff;background-color:#52e6ff}",""])},190:function(e,t,r){var o=r(153);"string"==typeof o&&(o=[[e.i,o,""]]),o.locals&&(e.exports=o.locals);r(12)("4e6abbbd",o,!0)},22:function(e,t,r){function o(e){r(190)}var i=r(3)(r(90),r(262),o,"data-v-f7c4446c",null);e.exports=i.exports},262:function(e,t){e.exports={render:function(){var e=this,t=e.$createElement,r=e._self._c||t;return r("div",{staticClass:"guide_page1"},[r("div",{staticClass:"corner"},[r("input",{directives:[{name:"model",rawName:"v-model",value:e.VerifiCode,expression:"VerifiCode"}],staticClass:"num",attrs:{type:"number",placeholder:"请输入验证码"},domProps:{value:e.VerifiCode},on:{input:function(t){t.target.composing||(e.VerifiCode=t.target.value)},blur:function(t){e.$forceUpdate()}}}),e._v(" "),r("br"),e._v(" "),r("input",{staticClass:"btn",attrs:{type:"button",value:"上一步"},on:{click:e.goback}}),r("input",{staticClass:"btn",attrs:{type:"button",value:"确认"},on:{click:e.verification}}),e._v(" "),r("br")])])},staticRenderFns:[]}},90:function(e,t){e.exports={devtool:"cheap-module-source-map",data:function(){return{maxLength:6,VerifiCode:"",phone:""}},mounted:function(){},activated:function(){},created:function(){},watch:{VerifiCode:function(){this.VerifiCode.length>this.maxLength&&(this.VerifiCode=this.VerifiCode.substr(0,this.maxLength))}},methods:{verification:function(){var e=this,t=2;if(e.phone=localStorage.getItem("phoneNo"),null==e.phone||""==e.phone)return error_msg("非法操作错误",2e3),!1;$.ajax({url:urlDir+"/wx/fans/infor/phoneNOBind",type:"POST",data:{phoneNO:e.phone,verifyCod:e.VerifiCode},success:function(r){if(0==r.code)switch(t=e.checkUserPosition(r.data.infor_state)){case 2:e.goRouter("/guide_page2");break;case 3:e.goRouter("/guide_page3");break;case 4:e.goRouter("/park_cantabile");break;default:e.goRouter("/guide_page2")}else error_msg(r.code+" 错误 "+r.msg,2e3)},error:function(e){error_msg("网络错误",2e3)}})},checkUserPosition:function(e){if(e+="",5!=e.length)return-1;var t=(e.charAt(0),e.charAt(1),e.charAt(2)),r=e.charAt(3),o=e.charAt(4);return 1*t==0?2:1*r==0&&1*o==0?3:4},goback:function(){this.$router.back(-1)},goRouter:function(e){this.$router.push({path:e})}}}}});