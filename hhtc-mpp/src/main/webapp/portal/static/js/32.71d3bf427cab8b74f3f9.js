webpackJsonp([32],{129:function(t,a,e){a=t.exports=e(11)(!1),a.push([t.i,".font_16px[data-v-5f5823b8]{font-size:16px}.center[data-v-5f5823b8]{margin:0 auto;text-align:center;padding-bottom:20px}.marginl35px[data-v-5f5823b8]{margin-left:35px}.border-bottom[data-v-5f5823b8]{margin:40px;border-bottom:1px solid #b0b0b0}.center_button[data-v-5f5823b8]{width:30%;height:.7rem;margin:0 15px;border-radius:7px;color:#fff;line-height:.7rem}.btn_div[data-v-5f5823b8]{margin-top:25px}.p_gyey[data-v-5f5823b8]{color:#616161;margin-top:5px}.grey[data-v-5f5823b8]{background-color:#c4c4c4;color:#575757}.black[data-v-5f5823b8]{background-color:#3a3a3a}",""])},13:function(t,a,e){function o(t){e(164)}var n=e(3)(e(78),e(231),o,"data-v-5f5823b8",null);t.exports=n.exports},164:function(t,a,e){var o=e(129);"string"==typeof o&&(o=[[t.i,o,""]]),o.locals&&(t.exports=o.locals);e(12)("537618b3",o,!0)},195:function(t,a,e){t.exports=e.p+"static/img/logo3.ac2c6b0.png"},231:function(t,a,e){t.exports={render:function(){var t=this,a=t.$createElement,e=t._self._c||a;return e("div",{staticClass:"authorise"},[t._m(0),t._v(" "),e("div",[e("p",{staticClass:"marginl35px"},[t._v("该程序将获取以下授权:")]),t._v(" "),e("p",{staticClass:"marginl35px p_gyey"},[t._v("* 获得您的公开信息（昵称、头像等）")]),t._v(" "),e("div",{staticClass:"center btn_div"},[e("input",{staticClass:"center_button grey font_16px",attrs:{type:"button",value:"拒绝"},on:{click:t.reject}}),t._v(" "),e("input",{staticClass:"center_button black font_20px",attrs:{type:"button",value:"允许"},on:{click:t.allow}})])])])},staticRenderFns:[function(){var t=this,a=t.$createElement,o=t._self._c||a;return o("div",{staticClass:"center border-bottom"},[o("img",{staticClass:"center",attrs:{src:e(195)}}),t._v(" "),o("p",{staticClass:"p_gyey"},[t._v("重庆吼吼科技有限公司")])])}]}},78:function(t,a){t.exports={devtool:"cheap-module-source-map",data:function(){return{}},mounted:function(){},activated:function(){},created:function(){},watch:{},methods:{getFansInfo:function(){var t=this;$.ajax({url:urlDir+"/wx/fans/get",type:"GET",success:function(a){0==a.code&&(t.phone=a.data.phoneNo)},error:function(t){}})},reject:function(){this.goRouter("/")},allow:function(){var t=this;t.goRouter("/guide"),$.ajax({url:urlDir+"/wx/fans/infor/accredit/allow",type:"POST",data:{},success:function(a){0==a.code?t.goRouter("/guide"):error_msg("服务器错误",2e3)}})},getWx:function(){var t=this;$.ajax({url:urlDir+"/wx/jssdk/sign",type:"POST",data:{url:location.href},success:function(a){"string"==typeof a&&(a=toParse(a)),console.log(a),a.data.appid?wx.config({debug:!1,appId:a.data.appid,timestamp:parseInt(a.data.timestamp),nonceStr:a.data.noncestr,signature:a.data.signature,jsApiList:["chooseImage","uploadImage"]}):++t.count<4&&t.getWx()}})},goRouter:function(t){this.$router.push({path:t})}}}}});