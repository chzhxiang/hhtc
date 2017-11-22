<template>
  <div class="login">
    <div class="tab font36"><span :class="userType==1?'curr':''" @click="changeUserType(1)">我是车主</span><span :class="userType==2?'curr':''" @click="changeUserType(2)">我是车位主</span></div>
    <div class="phone input"><input class="phone_number" type="text" v-model="phone" placeholder="请输入您的手机号"></div>
    <div class="code input"><input type="text" v-model="code" placeholder="请输入验证码"><span class="centered_y fcw" v-show="iscanalign" @click="sendCode">获取验证码</span><span class="centered_y fcw" v-show="!iscanalign">重新获取({{alignTime}})s</span></div>
    
    <!--车主-->
    <div class="time_chose fcg43 font32" v-show="userType==1">
      <div><span class="l">小区名称:</span><span class="r"><input type="text" name="" placeholder="请选择小区" readonly @click="show('ceng_district')" v-model="nowName.name=='请选择小区'?'':nowName.name"></span></div>
      <div><span class="l">车牌号:</span><span class="r duan"><input type="text" name="" placeholder="请添加车牌号" v-model="carNumber"></span><span class="r" @click="choseCar">{{carHead||"请选择"}}</span></div>
      <div><span class="l">门牌号:</span><span class="r"><input type="text" placeholder="请输入您的门牌号" name="" v-model="houseNumber"></span></div>
    </div>
    <!--车为主-->
    <div class="time_chose fcg43 font32" v-show="userType==2">
      <div><span class="l">车位所在小区:</span><span class="r"><input type="text" name="" placeholder="请选择小区" readonly @click="show('ceng_district')" v-model="nowName.name=='请选择小区'?'':nowName.name"></span></div>
      <div><span class="l">车位号:</span><span class="r"><input type="text" name="" placeholder="请添加车位号" v-model="carParkNumber"></span></div>
      <div><span class="l">车位产权证明:</span><span class="r"><img :src="showImage?showImage:'static/img/no_ss.png'" @click="choseImagrs"></span></div>
      <div><span class="l">车位有效期:</span><span class="r" id="education_list3">{{endDay}}</span><span class="r" style="margin-right:.2rem;" id="education_list4">{{beginDay}}</span></div>
    </div>
    <div class="protocol fcb font18" :class="readProtocol?'curr':''"><i @click="changeProtocol"></i><span class="">我同意 <em @click="goRouter('/protocol')">《吼吼停车服务协议》</em></span></div>
    <div class="checkout fcw font40" @click="login">提交注册申请</div>
    <div class="word fcb font18">*仅用于小区物业审核与平台注册，不向第三方泄露公开、透露个人信息</div>
    <!--注册成功-->
    <div class="ceng ceng_result fcg95 font26" style="display:none;">
      <div class="ceng_content centered" @click="emptyClick($event)">
        <div class="title fcb font38">注册成功</div>
        <div class="word">我们会在5个工作日内审核你的申请</div>
        <div class="word">如有任何疑问请拨打客服</div>
        <div class="word">电话：400-000-000</div>
        <div class="ceng_operating fcw font34" v-show="userType==2&&canRegistered" @click="close('ceng_result')">继续注册成为车位主</div>
        <div class="ceng_operating fcw font34" v-show="userType==2" @click="goRouter('/park')">去看看空余车位</div>
        <div class="ceng_operating fcw font34" v-show="userType==1&&canRegistered" @click="close('ceng_result')">继续注册成为车主</div>
        <div class="ceng_operating fcw font34" v-show="userType==1" @click="goRouter('/publish')">去发布车位</div>
      </div>
    </div>
    <!--选择小区-->
    <div class="ceng ceng_district fcg43 font30" @click="close('ceng_district')" style="display:none;">
      <div class="ceng_content centered" @click="emptyClick($event)">
        <div class="title font38">选择小区</div>
        <div class="word"><span class="l">省:</span><span class="r" @click="getProvince">{{nowProvince}}</span></div>
        <div class="word"><span class="l">市:</span><span class="r" @click="getCity">{{nowCity}}</span></div>
        <div class="word"><span class="l">小区名称:</span><span class="r" @click="getName">{{nowName.name}}</span></div>
      </div>
    </div>
    <!--<div class="show_list" v-show="showList.length>0" @click="close('show_list')">
      <div class="content" @click="emptyClick($event)">
        <p v-for="value in showList" @click="setNowTypeName(value)">{{value.name}}</p>
      </div>
    </div>-->
    <ul id="education_list" style="display: none">
      <li v-for="value in showList" :data="value">{{value.name}}</li>
    </ul>
    <ul id="education_list2" style="display: none">
      <li v-for="car in carList">
        <div :data-value="car">{{car}}</div>
        <ul>
          <li :data-value="carLi" v-for="carLi in carList2">{{carLi}}</li>
        </ul>
      </li>
    </ul>
  </div>
</template>
<script>
module.exports={
    devtool:'cheap-module-source-map',
    name:"login",
    data:function(){
      return {
        iscanalign:true,
        time:"",
        alignTime:60,
        iscansend:false,
        iscango:false,
        phone:"",
        code:"",
        userType:1,//1是车主 2是车位主
        nowProvince:"请选择省份",
        nowCity:"请选择城市",
        nowName:{"name":"请选择小区"},
        showList:[],
        nowType:"",
        houseNumber:"",//门牌号
        carNumber:"",//车牌号
        carParkNumber:"",//车位号
        readProtocol:true,//是否阅读协议
        carEquityImg:"",//产权证明
        showImage:"",//本地图片预览
        count:0,//微信请求次数
        canRegistered:true,
        isCanAjax:true,
        carList:["京","津","冀","渝","晋","蒙","辽","吉","黑","泸","苏","浙","皖","闽","赣","鲁","豫","鄂","湘","粤","桂","琼","渝","川","贵","云","藏","陕","甘","青","宁","新"],
        carList2:["A","B","C","D","E","F","G","H","I","J","K","L","M","N","O","P","Q","R","S","T","U","V","W","X","Y","Z"],
        carHead:"",
        maxLength:5,
        userLoginInfo:{},
        beginDay:"开始日期",
        endDay:"结束日期"
      }
    },
    mounted:function(){
      var that=this;
      $("#education_list2").mobiscroll().treelist({
        theme: "",  
        lang: "zh",  
        display: 'bottom', 
        // wheels:[], 设置此属性可以只显示年月，此处演示，就用下面的onBeforeShow方法,另外也可以用treelist去实现  
        //onBeforeShow: function (inst) { inst.settings.wheels[0].length>2?inst.settings.wheels[0].pop():null; }, //弹掉“日”滚轮
        headerText: function (valueText) { //自定义弹出框头部格式
          
        },  
        onSelect: function (valueText, inst) {
          var arr=valueText.split(" ");
          that.carHead=that.carList[arr[0]]+""+that.carList2[arr[1]];
        }  
      });
      var that=this,currYear=(new Date()).getFullYear();
      $("#education_list3").mobiscroll().date({
        theme: "",  
        lang: "zh",  
        display: 'bottom',
        startYear: currYear - 5, //开始年份currYear-5不起作用的原因是加了minDate: new Date()
        endYear: currYear + 15,
        // wheels:[], 设置此属性可以只显示年月，此处演示，就用下面的onBeforeShow方法,另外也可以用treelist去实现  
        //onBeforeShow: function (inst) { inst.settings.wheels[0].length>2?inst.settings.wheels[0].pop():null; }, //弹掉“日”滚轮
        headerText: function (valueText) { //自定义弹出框头部格式
          var array = valueText.split('/');  
          return array[0] + "年" + array[1] + "月"+array[2]+"日"; 
        },  
        onSelect: function (valueText, inst) {
          var array = valueText.split('/');  
          that.endDay=array[0] + "-" + array[1] + "-"+array[2]; 
        }  
      });
      $("#education_list4").mobiscroll().date({
        theme: "",  
        lang: "zh",  
        display: 'bottom',
        startYear: currYear - 5, //开始年份currYear-5不起作用的原因是加了minDate: new Date()
        endYear: currYear + 15,
        // wheels:[], 设置此属性可以只显示年月，此处演示，就用下面的onBeforeShow方法,另外也可以用treelist去实现  
        //onBeforeShow: function (inst) { inst.settings.wheels[0].length>2?inst.settings.wheels[0].pop():null; }, //弹掉“日”滚轮
        headerText: function (valueText) { //自定义弹出框头部格式
          var array = valueText.split('/');  
          return array[0] + "年" + array[1] + "月"+array[2]+"日"; 
        },  
        onSelect: function (valueText, inst) {
          var array = valueText.split('/');  
          that.beginDay=array[0] + "-" + array[1] + "-"+array[2]; 
        }  
      });
    },
    created:function(){
      // 组件创建完
    },
    activated:function(){
      //每次进入
      this.close("ceng")
      if(location.href.indexOf("userType")!=-1)
        this.userType=getUrl().userType*1;
      this.count=0;
      this.getWx();
      this.getFansInfo();
    },
    watch:{
      //监视变化
      phone:function(){
        if(/^1[3|4|5|7|8]\d{9}$/.test(this.phone)){
          this.iscansend=true;
          if(this.code.length==4){
            this.iscango=true;
          }
        }else{
          this.iscansend=false;
          this.iscango=false;
        }
      },
      code:function(){
        if(this.code.length==6&&/^1[3|4|5|7|8]\d{9}$/.test(this.phone)){
          this.iscango=true;
        }else{
          this.iscango=false;
        }
      },
      carHead:function(){
        if(this.carHead.indexOf("泸")!=-1){
          this.maxLength=6;
        }else{
          this.maxLength=5;
        }
      },
      carNumber:function(){
        this.carNumber=this.carNumber.replace(/[^a-zA-Z0-9]/g,"");
        if(this.carNumber.length>this.maxLength){
          this.carNumber=this.carNumber.substr(0,this.maxLength)
        }
      }
    },
    methods:{
      choseCar:function(){
        var that=this;
        $("input[id^=education_list2_dummy]").focus();
      },
      getFansInfo:function(){
        //获取用户信息
        var that=this;
        $.ajax({
          url:urlDir+"/wx/fans/get",
          type:"GET",
          success:function(res){
            if(res.code==0){
              that.userLoginInfo=res.data;
              if(res.data.phoneNo){
                that.phone=res.data.phoneNo;
                that.nowName={
                  name:res.data.carOwnerHouseName||res.data.carParkrHouseName,
                  id:res.data.carOwnerCommunityId||res.data.carParkCommunityId
                },
                that.carNumber=res.data.carNumber.split("`")[0].substr(2);
                that.carHead=res.data.carNumber.split("`")[0].substr(0,2);
                that.houseNumber=res.data.houseNumber;
              }
            }
          },
          error:function(res){

          }
        })
      },
      getWx:function(){
        //微信签名
        var that=this;
        $.ajax({
          url:urlDir+"/wx/jssdk/sign",
          type:"POST",
          data:{
            url:location.href
          },
          success:function(data){
            if(typeof(data)=="string"){
              data=toParse(data)
            }
            console.log(data)
            if(data.data.appid){
              wx.config({
                  debug: false, // 开启调试模式,调用的所有api的返回值会在客户端alert出来，若要查看传入的参数，可以在pc端打开，参数信息会通过log打出，仅在pc端时才会打印。
                  appId: data.data.appid, // 必填，公众号的唯一标识
                  timestamp: parseInt(data.data.timestamp), // 必填，生成签名的时间戳
                  nonceStr: data.data.noncestr, // 必填，生成签名的随机串
                  signature:data.data.signature,// 必填，签名，见附录1
                  jsApiList: ["chooseImage","uploadImage"]
              });
            }else{
              that.count++;
              if(that.count<4)
                that.getWx();
            }
          }
        })
      },
      choseImagrs:function(){
        //选择图片
        var that=this;
        wx.chooseImage({
          count:1, // 默认9
          sizeType: ['original'], // 可以指定是原图还是压缩图，默认二者都有
          sourceType: ['album', 'camera'], // 可以指定来源是相册还是相机，默认二者都有
          success: function (res) {
            that.showImage=res.localIds[0];
            wx.uploadImage({
              localId:res.localIds[0], // 需要上传的图片的本地ID，由chooseImage接口获得
              isShowProgressTips:1, // 默认为1，显示进度提示
              success: function (res){
                that.imgUpload(res.serverId)
                   // 返回图片的服务器端ID
                  //;
              }
          });
          }
        });
      },
      imgUpload:function(serverId){
        var that=this
        $.ajax({
          url:urlDir+"/wx/media/upload",
          type:"GET",
          data:{
            serverId:serverId
          },
          success:function(res){
            if(res.code==0){
              that.carEquityImg=res.data.filepath
            }
          },
          error:function(res){

          }
        })
      },
      changeProtocol:function(){
        this.readProtocol=!this.readProtocol;
      },
      goRouter:function(str){
        this.$router.push({path:str})
      },
      show:function(str){
        $("."+str).css("display","block")
      },
      close:function(str){
        $("."+str).css("display","none")
      },
      emptyClick:function(event){
        event.preventDefault();
        event.stopPropagation();
      },
      setNowTypeName:function(str){
        switch(this.nowType){
          case "province":
            this.nowProvince=str.name;
            break;
          case "city":
            this.nowCity=str.name;
            break;
          case "name":
            this.nowName=str;
            break;
        }
        this.showList=[];
      },
      setScrollList:function(){
        var that=this;
        $("#education_list").mobiscroll().treelist({
          theme: "",  
          lang: "zh",  
          display: 'bottom', 
          // wheels:[], 设置此属性可以只显示年月，此处演示，就用下面的onBeforeShow方法,另外也可以用treelist去实现  
          //onBeforeShow: function (inst) { inst.settings.wheels[0].length>2?inst.settings.wheels[0].pop():null; }, //弹掉“日”滚轮
          headerText: function (valueText) { //自定义弹出框头部格式
            console.log(valueText)
          },  
          onSelect: function (valueText, inst) {
            console.log(valueText, inst)
            that.setNowTypeName(that.showList[valueText])
          }  
        });
        $("input[id^=education_list_dummy]").focus();
      },
      getProvince:function(){
        //获取省份列表
        var that=this;
        $.ajax({
          url:urlDir+"/wx/community/province/list",
          type:"GET",
          success:function(res){
            if(res.code==0){
              that.nowType="province"
              that.showList=[];
              res.data.provinceNameList.forEach(function(v,i){
                that.showList.push({name:v})
              })
              setTimeout(function(){
                that.setScrollList()
              },100)
            }
          },
          error:function(res){

          }
        })
      },
      getCity:function(){
        //获取城市列表
        var that=this;
        if(this.nowProvince!="请选择省份"){
          $.ajax({
            url:urlDir+"/wx/community/city/list",
            type:"GET",
            data:{
              provinceName:this.nowProvince
            },
            success:function(res){
              if(res.code==0){
                that.nowType="city"
                that.showList=[];
                res.data.cityNameList.forEach(function(v,i){
                  that.showList.push({name:v})
                })
                setTimeout(function(){
                  that.setScrollList()
                },100)
              }
            },
            error:function(res){

            }
          })
        }else{
          error_msg("请先选择省份",2000)
        }
      },
      getName:function(){
        //获取小区列表
        var that=this;
        if(this.nowCity!="请选择城市"){
          $.ajax({
            url:urlDir+"/wx/community/listByCityName",
            type:"GET",
            data:{
              cityName:this.nowCity
            },
            success:function(res){
              if(res.code==0){
                that.nowType="name"
                that.showList=res.data;
                setTimeout(function(){
                  that.setScrollList()
                },100)
              }
            },
            error:function(res){

            }
          })
        }else{
          error_msg("请先选择城市",2000)
        }
      },
      changeUserType:function(index){
        this.userType=index;
      },
      alignGo:function(){
        var that=this;
        clearInterval(this.time);
        that.alignTime=60;
        that.iscanalign=false;
        this.time=setInterval(function(){
          that.alignTime-=1;
          if(that.alignTime<0){
            that.iscanalign=true;
            that.alignTime=60;
            clearInterval(that.time);
          }
        },1000)
      },
      sendCode:function(){
        var that=this;
        if(this.iscansend){
          $.ajax({
            url:urlDir+"/wx/common/sms/send",
            type:"GET",
            data:{
              phoneNo:this.phone,
              type:this.userType
            },
            success:function(res){
              error_msg(res.msg,2000)
              if(res.code==0){
                that.alignGo();
              }
            },
            error:function(res){

            }
          })
        }else{
          error_msg("请输入正确的手机号",2000)
        }
      },
      login:function(){
        var that=this;
        if(this.isCanAjax){
          this.isCanAjax=false;
          if(!this.readProtocol){
            error_msg("请先阅读并同意《吼吼停车服务协议》",2000)
            return true;
          }
          if(!this.iscango){
            error_msg("请输入正确的手机号与验证码",2000)
            return true;
          }
          if(!this.nowName.id){
            error_msg("请先选择小区名称",2000)
            return true;
          }

          if(this.userType==1){
            //车主注册
            if(!this.carHead){
              error_msg("请先选择车牌号",2000)
              return true;
            }
            if(!this.carNumber){
              error_msg("请先输入车牌号码",2000)
              return true;
            }
            if(!this.houseNumber){
              error_msg("请先输入门牌号码",2000)
              return true;
            }
            $.ajax({
              url:urlDir+"/wx/fans/reg/carOwner",
              type:"POST",
              data:{
                phoneNo:this.phone,
                verifyCode:this.code,
                carOwnerCommunityId:this.nowName.id,
                carNumber:this.carHead+this.carNumber,
                houseNumber:this.houseNumber
              },
              success:function(res){
                that.isCanAjax=true;
                error_msg(res.msg,2000)
                if(res.code==0){
                  that.code="";
                  that.userType=2;
                  $(".phone_number").attr("readonly")
                  that.show("ceng_result");
                  that.canRegistered=res.data
                }
              },
              error:function(res){
                that.isCanAjax=true;
              }
            })
          }else{
            //车位主注册
            if(!this.carParkNumber){
              error_msg("请先输入车位号",2000)
              return true;
            }
            if(!this.carEquityImg){
              error_msg("请先上传车位产权证明",2000)
              return true;
            }
            $.ajax({
              url:urlDir+"/wx/fans/reg/carPark",
              type:"POST",
              data:{
                phoneNo:this.phone,
                verifyCode:this.code,
                carParkCommunityId:this.nowName.id,
                carParkNumber:this.carParkNumber,
                carEquityImg:this.carEquityImg,
              },
              success:function(res){
                error_msg(res.msg,2000)
                that.isCanAjax=true;
                if(res.code==0){
                  $(".phone_number").attr("readonly")
                  that.code="";
                  that.userType=1;
                  that.show("ceng_result");
                  that.canRegistered=res.data
                }
              },
              error:function(res){
                that.isCanAjax=true;
              }
            })

          }
        }
        
        
      }
    }
  }
</script>
<style scoped>
.login{width:100%;height:100%;overflow-y:auto;background:#fff;}
.input{height:.76rem;line-height:.76rem;width:5.9rem;margin:.6rem auto 0;border-bottom:1px solid #959595;background:url("../assets/images/login_phone.png") no-repeat .2rem;background-size:.34rem .44rem;padding-left:1.12rem;}
.input input{height:.7rem;}
.code{background-image:url("../assets/images/login_password.png");position:relative;}
.code span{display:block;padding:0 .24rem;border-radius:.24rem;height:.48rem;line-height:.48rem;text-align:center;right:0;background:#0098db;}
/**/
.tab{height:2.1rem;line-height:2.1rem;text-align:center;}
.tab span{display:inline-block;width:2.62rem;height:.86rem;line-height:.86rem;border:1px solid #0098db;color:#0098db;border-radius:.43rem;margin-right:.64rem;}
.tab span:last-child{margin-right:0;}
.tab span.curr{background:#0098db;color:#fff;}
/**/
.time_chose{width:7rem;border-radius:.16rem;box-shadow:0 0 .1rem .01rem #dae7ed;margin:.6rem auto .44rem;background:#fff;padding:.13rem .3rem;}
.time_chose div{height:.9rem;line-height:.9rem;overflow:hidden;}
.time_chose div span.r{background:url("../assets/images/sanjiao.png") no-repeat right;background-size:.16rem .23rem;padding-right:.44rem;}
.time_chose div span.r input{text-align:right;width:3.4rem;}
.time_chose div span.duan input{width:2rem;}
.time_chose div:nth-child(3) span.r{background:none;padding:0;}
.time_chose div:nth-child(3) img{width:.51rem;height:.51rem;vertical-align:middle;display:inline-block;}
/**/
.protocol{height:1rem;line-height:1rem;text-align:center;}
.protocol i{display:inline-block;width:.3rem;height:.3rem;background:url("../assets/images/login_chose_no.png") no-repeat center;background-size:.3rem .3rem;vertical-align:middle;margin-right:.2rem;}
.protocol span{display:inline-block;vertical-align:middle;}
.protocol.curr i{background-image:url("../assets/images/login_chose.png");}
/**/
.checkout{width:5rem;height:.88rem;line-height:.88rem;text-align:center;border-radius:.44rem;background:#0098db;margin:0 auto;}
.word{height:.84rem;line-height:.84rem;text-align:center;}
/*弹出层*/
.ceng{width:100%;height:100%;position:absolute;top:0;left:0;background:rgba(255,255,255,.5);}
.ceng .ceng_content{width:5.9rem;background:#fff;border-radius:.24rem;padding:0 .46rem;box-shadow:0 0 1rem 0 #cfebf8;}
/*发布成功*/
.ceng_result .ceng_content{background:#fff url("../assets/images/yes.png") no-repeat center .4rem;background-size:1.98rem 1.98rem;padding-top:2.38rem;}
.ceng_result .title{height:1.2rem;line-height:1.2rem;text-align:center;}
.ceng_result .word{height:.6rem;line-height:.6rem;text-align:center;}
.ceng_result .ceng_operating{width:3.7rem;height:.8rem;line-height:.8rem;background:#0098db;margin:.2rem auto .4rem;border-radius:.4rem;text-align:center;}
.ceng_result .ceng_operating:last-child{background:#fff;border:1px solid #0098db;color:#0098db;}
/*选择小区*/
.ceng_district{padding:.12rem 0 .6rem;}
.ceng_district .title{height:1.14rem;line-height:1.14rem;text-align:center;}
.ceng_district .word{height:.96rem;line-height:.96rem;}
.ceng_district .word span.r{background:url("../assets/images/sanjiao.png") no-repeat right;background-size:.16rem .23rem;padding-right:.44rem;}
/*列表显示*/
.show_list{width:100%;height:100%;overflow:hidden;position:absolute;top:0;left:0;z-index:20;background:rgba(0,0,0,.5);}
.show_list .content{max-height:5rem;overflow-y:auto;width:100%;position:absolute;bottom:0;background:#fff;}
.show_list .content p{line-height:.5rem;text-align:center;}
/**/

</style>
