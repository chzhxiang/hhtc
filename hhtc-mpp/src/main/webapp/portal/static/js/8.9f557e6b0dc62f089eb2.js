webpackJsonp([8],{130:function(t,e,i){e=t.exports=i(11)(!1),e.push([t.i,".guide_page[data-v-2ed214af]{width:100%;height:100%;background-color:#e2e2e2}#guide[data-v-2ed214af]{height:2.4rem;overflow-y:auto;margin-left:20px;margin-right:20px;padding-bottom:10px;border-bottom:1px solid #2c2c2c}.head[data-v-2ed214af]{width:100%;height:100%;overflow:hidden}.head .picture div img[data-v-2ed214af]{width:.2rem;float:left}.head .picture div div[data-v-2ed214af]{width:.2rem;height:.2rem;float:left;text-align:center}.head .picture div .row[data-v-2ed214af]{width:1.5rem;height:.2rem;float:left}.content[data-v-2ed214af]{height:.8rem;overflow:hidden}.content div[data-v-2ed214af]{margin-top:.1rem;width:25%;text-align:center;height:.6rem;line-height:.6rem;font-size:10px}#page[data-v-2ed214af]{height:70%;overflow-y:auto}.jump[data-v-2ed214af]{width:1rem;height:.5rem;text-align:center;line-height:.5rem;background-color:#e2e2e2;color:#969696;margin-right:.5rem}.ed[data-v-2ed214af]{background-image:url("+i(221)+");background-position:50%}.curr[data-v-2ed214af]{background-image:url("+i(220)+");background-position:50%}.un[data-v-2ed214af]{background-image:url("+i(222)+");background-position:50%}",""])},168:function(t,e,i){var a=i(130);"string"==typeof a&&(a=[[t.i,a,""]]),a.locals&&(t.exports=a.locals);i(12)("423ec530",a,!0)},20:function(t,e,i){function a(t){i(168)}var o=i(3)(i(91),i(237),a,"data-v-2ed214af",null);t.exports=o.exports},220:function(t,e,i){t.exports=i.p+"static/img/position_curr.649b55a.png"},221:function(t,e,i){t.exports=i.p+"static/img/position_ed.694c8f4.png"},222:function(t,e,i){t.exports=i.p+"static/img/position_un.164586c.png"},237:function(t,e,i){t.exports={render:function(){var t=this,e=t.$createElement,a=t._self._c||e;return a("div",{staticClass:"guide_page"},[a("div",{attrs:{id:"guide"}},[a("div",{staticClass:"head"},[a("div",{staticClass:"picture",staticStyle:{width:"100%",height:"1.4rem",position:"relative","padding-left":"11%"}},[a("div",{staticStyle:{width:"88%",height:".2rem",margin:".1rem auto","margin-top":".3rem",bottom:".2rem",position:"absolute"}},[a("div",{class:t.pos<=1?"curr":"ed"}),t._v(" "),a("img",{staticClass:"row",attrs:{src:i(71)}}),t._v(" "),a("div",{class:t.pos<2?"un":2==t.pos?"curr":"ed"}),t._v(" "),a("img",{staticClass:"row",attrs:{src:i(71)}}),t._v(" "),a("div",{class:t.pos<3?"un":3==t.pos?"curr":"ed"}),t._v(" "),a("img",{staticClass:"row",attrs:{src:i(71)}}),t._v(" "),a("div",{class:t.pos<4?"un":4==t.pos?"curr":"ed"})])]),t._v(" "),t._m(0)])]),t._v(" "),a("div",{attrs:{id:"page"}},[a("keep-alive",[a("router-view")],1),t._v(" "),a("div",{staticClass:"jump r"},[a("p",{staticClass:"font28",on:{click:function(e){t.goRouter("/park_cantabile")}}},[t._v("跳过")])])],1)])},staticRenderFns:[function(){var t=this,e=t.$createElement,i=t._self._c||e;return i("div",{staticClass:"content"},[i("div",{staticClass:"l "},[t._v("手机绑定")]),t._v(" "),i("div",{staticClass:"l "},[t._v("地址绑定")]),t._v(" "),i("div",{staticClass:"l "},[t._v("身份注册")]),t._v(" "),i("div",{staticClass:"l "},[t._v("登记完成")])])}]}},54:function(t,e,i){"use strict";i.d(e,"a",function(){return o});var a=i(0),o=new a.a},71:function(t,e,i){t.exports=i.p+"static/img/row.1971f23.png"},91:function(t,e,i){"use strict";Object.defineProperty(e,"__esModule",{value:!0});var a=i(54);e.default={devtool:"cheap-module-source-map",data:function(){return{pos:1}},mounted:function(){},activated:function(){},created:function(){var t=this;a.a.$on("changPosition",function(e){t.pos=1*e})},watch:{},methods:{goRouter:function(t){this.$router.push({path:t})}}}}});