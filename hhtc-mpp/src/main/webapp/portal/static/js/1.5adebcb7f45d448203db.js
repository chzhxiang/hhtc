webpackJsonp([1],{116:function(t,e){t.exports={devtool:"cheap-module-source-map",data:function(){return{Lsrc:"",Rsrc:"",fansInfo:{},isPark:!1,isLicense:!1}},mounted:function(){},activated:function(){this.getFansInfo()},created:function(){},watch:{isPark:function(){0==this.isPark?($(".R_icon").eq(0).css("display","none"),$(".R_icon").eq(1).css("display","inline-block")):($(".R_icon").eq(0).css("display","inline-block"),$(".R_icon").eq(1).css("display","none"))},isLicense:function(){0==this.isLicense?($(".L_icon").eq(0).css("display","none"),$(".L_icon").eq(1).css("display","inline-block")):($(".L_icon").eq(0).css("display","inline-block"),$(".L_icon").eq(1).css("display","none"))}},methods:{getFansInfo:function(){var t=this;$.ajax({url:urlDir+"/wx/fans/get",type:"GET",success:function(e){if(0==e.code){t.fansInfo=e.data,localStorage.setItem("fansInfo",toJson(e.data));switch(t.checkUserPosition(e.data.infor_state)){case 4:break;case 0:t.goRouter("/accredit");break;case 1:t.goRouter("/guide_page1");break;case 2:t.goRouter("/guide_page2");break;case 3:t.goRouter("/guide_page3");break;case-1:error_msg("错误数据",2e3)}}else error_msg(e.code+" 错误 "+e.msg,2e3)},error:function(t){error_msg("网络错误",2e3)}}),t.initState()},initState:function(){var t=this;0==t.isLicense?($(".L_icon").eq(0).css("display","none"),$(".L_icon").eq(1).css("display","inline-block")):($(".L_icon").eq(0).css("display","inline-block"),$(".L_icon").eq(1).css("display","none")),0==t.isPark?($(".R_icon").eq(0).css("display","none"),$(".R_icon").eq(1).css("display","inline-block")):($(".R_icon").eq(0).css("display","inline-block"),$(".R_icon").eq(1).css("display","none"))},goRouter:function(t){this.$router.push({path:t})},emptyClick:function(t){t.preventDefault(),t.stopPropagation()},checkUserPosition:function(t){var e=this;if(t+="",5!=t.length)return-1;var i=t.charAt(0),s=t.charAt(1),a=t.charAt(2),n=t.charAt(3),o=t.charAt(4);return 1*i==0?0:1*s==0?1:1*a==0?2:(e.isPark=1*n==1,e.isLicense=1*o==1,1*n==0&&1*o==0?3:4)}}}},150:function(t,e,i){e=t.exports=i(11)(!1),e.push([t.i,".user[data-v-e1e6ab68]{width:100%;background-color:#ececec;height:100%;position:relative;padding-bottom:1.1rem;overflow:hidden;overflow-y:auto}.info[data-v-e1e6ab68]{width:7.02rem;height:2.66rem;background-color:#fff;margin:.1rem auto;text-align:center;overflow:hidden;position:relative;border-radius:.16rem;box-shadow:0 0 .1rem .01rem #dae7ed}.img[data-v-e1e6ab68]{margin-top:.42rem;margin-bottom:.12rem;width:2.2rem;float:left}.img img[data-v-e1e6ab68]{width:1.8rem;height:1.8rem;border:1px solid #fff;border-radius:50%;overflow:hidden;margin:0 auto;background:#ccc}.name[data-v-e1e6ab68]{height:.6rem;width:4.2rem;line-height:.6rem;float:left;margin-left:.3rem;margin-top:.86rem;background-color:#bfcdda}.status[data-v-e1e6ab68]{height:.42rem;width:4.2rem;margin-left:.3rem;bottom:0;float:left;margin-top:.2rem}.status div[data-v-e1e6ab68]{display:inline-block;margin-right:.4rem}.status span[data-v-e1e6ab68]{display:inline-block;width:1.2rem;height:.42rem;color:#000;line-height:.42rem;overflow:hidden}.status .i[data-v-e1e6ab68]{display:inline-block;width:.42rem;font-style:normal;height:.42rem;border-radius:.21rem;background-color:#fff;background-size:contain}.list[data-v-e1e6ab68]{width:7.02rem;height:.8rem;line-height:.8rem;padding-left:1.2rem;border-radius:.16rem;box-shadow:0 0 .1rem .01rem #dae7ed;background:url("+i(59)+') no-repeat 6.4rem;background-size:.38rem .38rem;margin:0 auto;position:relative}.list img[data-v-e1e6ab68]{height:.46rem;left:.4rem}.line[data-v-e1e6ab68]:after{content:"";position:absolute;width:90%;height:.02rem;background-color:#acacac;top:0;left:5%}.block[data-v-e1e6ab68]{background:linear-gradient(#fff,#dbdbdb);width:7.02rem;margin:.2rem auto;border-radius:.16rem;position:relative}',""])},187:function(t,e,i){var s=i(150);"string"==typeof s&&(s=[[t.i,s,""]]),s.locals&&(t.exports=s.locals);i(12)("86b0603c",s,!0)},259:function(t,e,i){t.exports={render:function(){var t=this,e=t.$createElement,s=t._self._c||e;return s("div",{staticClass:"user"},[s("div",{staticClass:"info fcw block"},[s("div",{staticClass:"img"},[s("img",{attrs:{src:t.fansInfo.headimgurl}})]),t._v(" "),s("div",{staticClass:"name font38"},[t._v(t._s(t.fansInfo.nickname||"用户昵称"))]),t._v(" "),t._m(0)]),t._v(" "),s("div",{staticClass:"block"},[s("div",{staticClass:"list font34 fcg95",staticStyle:{"margin-top":".46rem"},on:{click:function(e){t.goRouter("/reginfo")}}},[s("img",{staticClass:"centered_y",attrs:{src:i(74)}}),t._v("登记信息\n    ")]),t._v(" "),s("div",{staticClass:"list font34 fcg95 line",on:{click:function(e){t.goRouter("/license_manage")}}},[s("img",{staticClass:"centered_y",attrs:{src:i(63)}}),t._v("车牌管理\n    ")]),t._v(" "),s("div",{staticClass:"list font34 fcg95 line",on:{click:function(e){t.goRouter("/park_manage")}}},[s("img",{staticClass:"centered_y",attrs:{src:i(75)}}),t._v("车位管理\n    ")])]),t._v(" "),s("div",{staticClass:"block"},[s("div",{staticClass:"list font34 fcg95",staticStyle:{"margin-top":".46rem"},on:{click:function(e){t.goRouter("/pledge_manage")}}},[s("img",{staticClass:"centered_y",attrs:{src:i(72)}}),t._v("押金管理\n    ")]),t._v(" "),s("div",{staticClass:"list font34 fcg95 line",on:{click:function(e){t.goRouter("/balance")}}},[s("img",{staticClass:"centered_y",attrs:{src:i(73)}}),t._v("余额管理\n    ")])]),t._v(" "),s("div",{staticClass:"block"},[s("div",{staticClass:"list font34 fcg95",staticStyle:{"margin-top":".24rem"},on:{click:function(e){t.goRouter("/order")}}},[s("img",{staticClass:"centered_y",attrs:{src:i(60)}}),t._v("我的订单\n    ")]),t._v(" "),s("div",{staticClass:"list font34 fcg95 line",on:{click:function(e){t.goRouter("/feedback")}}},[s("img",{staticClass:"centered_y",attrs:{src:i(71)}}),t._v("我要申述\n    ")])])])},staticRenderFns:[function(){var t=this,e=t.$createElement,s=t._self._c||e;return s("div",{staticClass:"status font30"},[s("div",[s("img",{staticClass:"i L_icon",attrs:{src:i(76)}}),s("img",{staticClass:"i L_icon",attrs:{src:i(61)}}),s("span",[t._v("车主验证")])]),t._v(" "),s("div",[s("img",{staticClass:"i R_icon",attrs:{src:i(76)}}),s("img",{staticClass:"i R_icon",attrs:{src:i(61)}}),s("span",[t._v("车位验证")])])])}]}},48:function(t,e,i){function s(t){i(187)}var a=i(3)(i(116),i(259),s,"data-v-e1e6ab68",null);t.exports=a.exports},59:function(t,e,i){t.exports=i.p+"static/img/icon9.5cde05d.png"},60:function(t,e,i){t.exports=i.p+"static/img/uxer_order.7dadc97.png"},61:function(t,e,i){t.exports=i.p+"static/img/close.2201c72.png"},63:function(t,e,i){t.exports=i.p+"static/img/user_icon_1.9341394.png"},71:function(t,e,i){t.exports=i.p+"static/img/user_fee.bada6f0.png"},72:function(t,e,i){t.exports=i.p+"static/img/user_icon_2.dcf7673.png"},73:function(t,e,i){t.exports=i.p+"static/img/user_icon_3.74c54d6.png"},74:function(t,e,i){t.exports=i.p+"static/img/user_icon_4.4af9ec1.png"},75:function(t,e,i){t.exports=i.p+"static/img/user_income.f7f8fc6.png"},76:function(t,e,i){t.exports=i.p+"static/img/J_icon.563ea8e.png"}});