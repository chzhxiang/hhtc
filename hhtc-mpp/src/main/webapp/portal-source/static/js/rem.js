var urlDir="",errorMsgTime="",dpi=0;
(function (doc, win) {
  var docEl = doc.documentElement,
    resizeEvt = 'orientationchange' in window ? 'orientationchange' : 'resize',
    recalc = function (e){
      e.preventDefault();
      e.stopPropagation();
      var clientWidth = docEl.clientWidth;
      if (!clientWidth) return;
      else if(clientWidth>750) clientWidth=750;
      docEl.style.fontSize = 100 * (clientWidth / 750) + 'px';
      dpi=100 * (clientWidth / 750);
    };
  if (!doc.addEventListener) return;
  win.addEventListener(resizeEvt, recalc, false);
  doc.addEventListener('DOMContentLoaded', recalc, false);
})(document, window);
function setScroll_event(obj,child_class){
  var bod_start=0,
      bod_end=0;
  var first_y=0,last_y=0,first_t=0,last_t=0,touch_interval="";
  var bod=obj;
  obj.on("touchstart",function(e){
      bod_start=e.changedTouches[0].pageY;
      clearInterval(touch_interval);
      first_y=bod_start;
      first_t=new Date().getTime();
  }).on("touchmove",function(e){
      e.preventDefault();
      e.stopPropagation();
      bod_end=e.changedTouches[0].pageY;
      $(this).scrollTop($(this).scrollTop()+bod_start-bod_end);
      bod_start=e.changedTouches[0].pageY;
  }).on("touchend",function(e){
      last_y=bod_start=e.changedTouches[0].pageY;
      last_t=new Date().getTime();
      var distance=Math.abs(last_y-first_y);
      var v=distance/(last_t-first_t)
      var that=this;
      if(v>=0.7){
        v*=30
        touch_interval=setInterval(function(){
          v-=1;
          if(last_y-first_y>0){
            //手指向上划
            $(that).scrollTop($(that).scrollTop()-v);
          }else{
            $(that).scrollTop($(that).scrollTop()+v);
          }
          if(v<=0||obj.children("."+child_class).length>0&&$(that).scrollTop()>=obj.children("."+child_class).offset().height-$(that).offset().height-10)
            clearInterval(touch_interval);
        },10)
      }
  })
}
function error_msg(word,time){
  clearTimeout(errorMsgTime);
  $("#msg").css("display","block").find(".word").text(word);
  errorMsgTime=setTimeout(function(){
    $("#msg").css("display","none")
  },time)
}
function getUrl(){
  var url=decodeURIComponent(location.href).split("?"),obj={};
  if(url.length>1){
    var arr=url[1].split("&");
    arr.forEach(function(v,i){
      var obj_keyarr=v.split("=");
      obj[obj_keyarr[0]]=obj_keyarr[1];
    })
  }
  return obj;
}
function getTimeSF(time){
  var date=new Date();
  date.setTime(time);
  return (date.getHours()>=10?date.getHours():("0"+date.getHours()))+":"+(date.getMinutes()>=10?date.getMinutes():("0"+date.getMinutes()));
}
function getTimes(time,str,isChina){
var date=new Date();
  date.setTime(time);
  var arr=str.split(""),ret_str="";
  arr.forEach(function(v,i){
    ret_str+=getInfo(date,v,i==arr.length-1,isChina);
  })
return ret_str;
}
function getInfo(time,num,isLast,isChina){
  var str=""
  switch(num){
    case "Y":
      str=time.getFullYear()+(isChina?"年":"-");
      break;
    case "M":
      str=((time.getMonth()+1)>=10?(time.getMonth()+1):("0"+(time.getMonth()+1)))+(isLast?(isChina?"月":""):(isChina?"月":"-"));
      break;
    case "D":
      str=(time.getDate()>=10?time.getDate():("0"+time.getDate()))+(isLast?(isChina?"日":""):(isChina?"日 ":" "));
      break;
    case "h":
      str=(time.getHours()>=10?time.getHours():("0"+time.getHours()))+(isLast?"":":");
      break;
    case "m":
      str=(time.getMinutes()>=10?time.getMinutes():("0"+time.getMinutes()))+(isLast?"":":");
      break;
    case "s":
      str=time.getSeconds()>=10?time.getSeconds():("0"+time.getSeconds());
      break;
  }
  return str
}
function toJson(obj){
  return JSON.stringify(obj)
}
function toParse(str){
  return JSON.parse(str)
}
function count_time(arr){
  var arr_l=arr.split(":");
  return arr_l[0]*60+arr_l[1]*1;
}
function untied_time(num){
  var str1=Math.floor(num/60),str2=Math.floor(num%60)
  return (str1>=10?str1:("0"+str1))+":"+(str2>=10?str2:("0"+str2))
}
function conversion(time,type){
  //真实年转 - 假为 -转年
  console.log(time)
  if(type){
    return time.replace("年","-").replace("月","-").replace("日","")
  }else{
    var arr=time.split(" ");
    return arr[0].replace("-","年").replace("-","月")+(arr[1]?(" "+arr[1]):"日")
  }
  
}
window.onpageshow=function(){
  var str=location.href;
  $(".mbsc-mobiscroll").remove();
  if(str.indexOf("/park")!=-1){
    $(".wx .reservation").addClass("curr").siblings(".l").removeClass("curr");
  }else if(str.indexOf("/publish")!=-1){
    $(".wx .order").addClass("curr").siblings(".l").removeClass("curr");
  }else if(str.indexOf("/userinfo")!=-1){
    $(".wx .user").addClass("curr").siblings(".l").removeClass("curr");
  }
}
window.addEventListener("popstate", function(e){
  $(".mbsc-mobiscroll").remove();
}, false);
window.onload=function(){
  getFenXiang();
}
function getFenXiang(){
  $.ajax({
    url:urlDir+"/wx/jssdk/sign",
    type:"POST",
    data:{
      url:location.href.split("#")[0]
    },
    success:function(data){
      if(typeof(data)=="string"){
        data=toParse(data)
      }
      console.log(data)
      if(data.data.appId){
        wx.config({
            debug: false, // 开启调试模式,调用的所有api的返回值会在客户端alert出来，若要查看传入的参数，可以在pc端打开，参数信息会通过log打出，仅在pc端时才会打印。
            appId: data.data.appId, // 必填，公众号的唯一标识
            timestamp: parseInt(data.data.timestamp), // 必填，生成签名的时间戳
            nonceStr: data.data.nonceStr, // 必填，生成签名的随机串
            signature:data.data.signature,// 必填，签名，见附录1
            jsApiList:[
              'onMenuShareTimeline',
              'onMenuShareAppMessage',
              'onMenuShareQQ',
              'chooseWXPay',
              'scanQRCode',
              'onMenuShareQZone',
              "chooseImage",
              "uploadImage",
              "previewImage"
          ]
        });
        wx.ready(function(){
          var obj={
            title:"吼吼停车",
            desc:"我要抢车位方便加一倍！",
            link:"https://open.weixin.qq.com/connect/oauth2/authorize?appid=wxb4c63222cebf7762&redirect_uri=http://"+location.host+"/weixin/helper/oauth/wxb4c63222cebf7762&response_type=code&scope=snsapi_base&state=http://"+location.host+"/portal/index.html#/park#wechat_redirect",
            imgUrl:"http://"+location.host+"/static/img/logo2.png"
          }
          wx.onMenuShareTimeline({
            title:obj.title, // 分享标题
            link:obj.link, // 分享链接，该链接域名或路径必须与当前页面对应的公众号JS安全域名一致
            imgUrl:obj.imgUrl, // 分享图标
            desc:obj.desc
          });
          wx.onMenuShareAppMessage({
            title:obj.title, // 分享标题
            link:obj.link, // 分享链接，该链接域名或路径必须与当前页面对应的公众号JS安全域名一致
            imgUrl:obj.imgUrl, // 分享图标
            desc:obj.desc
          });
          wx.onMenuShareQQ({
            title:obj.title, // 分享标题
            link:obj.link, // 分享链接，该链接域名或路径必须与当前页面对应的公众号JS安全域名一致
            imgUrl:obj.imgUrl, // 分享图标
            desc:obj.desc
          });
          wx.onMenuShareWeibo({
            title:obj.title, // 分享标题
            link:obj.link, // 分享链接，该链接域名或路径必须与当前页面对应的公众号JS安全域名一致
            imgUrl:obj.imgUrl, // 分享图标
            desc:obj.desc
          });
          wx.onMenuShareQZone({
            title:obj.title, // 分享标题
            link:obj.link, // 分享链接，该链接域名或路径必须与当前页面对应的公众号JS安全域名一致
            imgUrl:obj.imgUrl, // 分享图标
            desc:obj.desc
          });
        })
      }
    }
  })
}