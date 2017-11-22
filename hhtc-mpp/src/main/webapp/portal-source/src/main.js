// The Vue build version to load with the `import` command
// (runtime-only or standalone) has been set in webpack.base.conf with an alias.
import Vue from 'vue'
import App from './App'
import router from './router'

Vue.config.productionTip = false

/* eslint-disable no-new */
new Vue({
  el: '#app',
  router,
  template: '<App/>',
  components: { App }
})
Vue.filter('phoneShow', function (value) {
	if(value)
		return value.substr(0,3)+"****"+value.substr(-4,4)
	return ""
});
Vue.filter('orderCode', function (value) {
	if(value)
		return value.substr(0,8)+"**"+value.substr(-6,6)
	return ""
});
Vue.filter('orderCode2', function (value) {
	if(value)
		return value.substr(0,8)+"**"+value.substr(-6,6)
	return ""
});
Vue.filter('removedSemicolon', function (value) {
	if(value)
		return value.replace(/;/g,"")
	return ""
});
Vue.filter('carShow', function (value) {
	if(value)
		return value.substr(0,2)+" "+value.slice(2)
	return ""
});
