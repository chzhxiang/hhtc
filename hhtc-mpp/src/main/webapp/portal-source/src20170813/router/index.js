import Vue from 'vue'
import Router from 'vue-router'
//发布车位publish 我要停车park 个人中心userinfo
Vue.use(Router)
export default new Router({
  routes: [
    {
      path: '/',
      component:function (resolve) {
        require(['../components/wx.vue'], resolve)
      },
      children:[{
        //个人信息
      	path:"userinfo",
      	component:function (resolve) {
		    	require(['../components/userinfo.vue'], resolve)
		    }
      },{
        //我要停车
        path:"park",
        component:function (resolve) {
          require(['../components/park.vue'], resolve)
        }
      },{
        //预约详情
        path:"park_details",
        component:function (resolve) {
          require(['../components/park_details.vue'], resolve)
        }
      },{
        //我的车位
        path:"publish",
        component:function (resolve) {
          require(['../components/publish.vue'], resolve)
        }
      },{
        //发布需求
        path:"demand",
        component:function (resolve) {
          require(['../components/demand.vue'], resolve)
        }
      },{
        //发布车位
        path:"publish_park",
        component:function (resolve) {
          require(['../components/publish_park.vue'], resolve)
        }
      },{
        //添加车位
        path:"/district",
        component:function (resolve) {
          require(['../components/district.vue'], resolve)
        }
      }]
    },{
      //意见反馈
      path:"/feedback",
      component:function (resolve) {
        require(['../components/feedback.vue'], resolve)
      }
    },{
      //我的收益
      path:"/income",
      component:function (resolve) {
        require(['../components/income.vue'], resolve)
      }
    },{
      //银行卡
      path:"/bank",
      component:function (resolve) {
        require(['../components/bank.vue'], resolve)
      }
    },{
      //登录
      path:"/login",
      component:function (resolve) {
        require(['../components/login.vue'], resolve)
      }
    },{
      //选择车牌
      path:"/license_plate",
      component:function (resolve) {
        require(['../components/license_plate.vue'], resolve)
      }
    },{
      //添加排除时间
      path:"/exclude",
      component:function (resolve) {
        require(['../components/exclude.vue'], resolve)
      }
    },{
      //车位详情
      path:"/park_status",
      component:function (resolve) {
        require(['../components/park_status.vue'], resolve)
      }
    },{
      //订单详情
      path:"/order_detail",
      component:function (resolve) {
        require(['../components/order_detail.vue'], resolve)
      }
    },{
      //订单
      path:"/order",
      component:function (resolve) {
        require(['../components/order.vue'], resolve)
      }
    },{
      //充值
      path:"/recharge",
      component:function (resolve) {
        require(['../components/recharge.vue'], resolve)
      }
    },{
      path:"/protocol",
      //健身图标选择
      component:function(resolve){
        require(['../components/protocol.vue'], resolve)
      }
    }
  ]
})
