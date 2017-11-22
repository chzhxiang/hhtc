<template>
  <div class="district">
    <div class="district_info font32">
      <div class="word"><span class="l fcg43">小区名称:</span><span class="r fcgb1 chose_district"><input type="text" name="" placeholder="请选择小区" readonly @click="show('ceng_district')" v-model="nowName.name=='请选择小区'?'':nowName.name"></span></div>
      <div class="word"><span class="l fcg43">车位号:</span><span class="r fcg43"><input class="number" type="text" name="" placeholder="请输入您车位号" v-model="carParkNumber"></span></div>
      <div class="word"><span class="l fcg43">业主电话:</span><span class="r"><input type="number" name="" placeholder="请输入您手机号" v-model="phone" readonly></span></div>
      <div class="word"><span class="l fcg43">车位产权证明:</span><span class="r"><img :src="showImage?showImage:'static/img/no_ss.png'" @click="choseImagrs"></span></div>
    </div>
    <div class="checkout font34 fcw" @click="gogo">提交</div>
    <!--发布成功-->
    <div class="ceng ceng_district fcg43 font30" @click="close('ceng_district')" style="display:none;">
      <div class="ceng_content centered" @click="emptyClick($event)">
        <div class="title font38">选择小区</div>
        <div class="word"><span class="l">省:</span><span class="r" @click="getProvince">{{nowProvince}}</span></div>
        <div class="word"><span class="l">市:</span><span class="r" @click="getCity">{{nowCity}}</span></div>
        <div class="word"><span class="l">小区名称:</span><span class="r" @click="getName">{{nowName.name}}</span></div>
      </div>
    </div>
    <ul id="education_list" style="display: none">
      <li v-for="value in showList" :data="value">{{value.name}}</li>
    </ul>
  </div>
</template>

<script>
module.exports={
	devtool:'cheap-module-source-map',
    data:function(){
      return {
        carEquityImg:"",//产权证明
        showImage:"",//本地图片预览
        carParkNumber:"",//车位号
        nowProvince:"请选择省份",
        nowCity:"请选择城市",
        nowName:{"name":"请选择小区"},
        phone:"",
        count:0,
        showList:[],
        nowType:"",
      }
    },
    mounted:function(){
      
    },
    activated:function(){
      //每次进入
      this.close("ceng")
      this.count=0;
      this.getWx();
      this.getFansInfo();
    },
    created:function(){
      // 组件创建完
    },
    watch:{
      //监视变化
    },
    methods:{
      getFansInfo:function(){
        //获取用户信息
        var that=this;
        $.ajax({
          url:urlDir+"/wx/fans/get",
          type:"GET",
          success:function(res){
            if(res.code==0){
              that.phone=res.data.phoneNo
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
      gogo:function(){
        var that=this;
        if(!this.nowName.id){
          error_msg("请先选择小区名称",2000)
          return true;
        }
        if(!this.carParkNumber){
          error_msg("请先输入车位号",2000)
          return true;
        }
        if(!this.carEquityImg){
          error_msg("请先上传车位产权证明",2000)
          return true;
        }
        $.ajax({
            url:urlDir+"/wx/goods/add",
            type:"POST",
            data:{
              communityId:this.nowName.id,
              carParkNumber:this.carParkNumber,
              carEquityImg:this.carEquityImg
            },
            success:function(res){
              error_msg(res.msg,2000)
              if(res.code==0){
                setTimeout(function(){
                  history.back()
                },1000)
              }
            },
            error:function(res){

            }
          })
      }
    }
  }
</script>
<style scoped>
.district{width:100%;height:100%;position:relative;padding-bottom:1rem;overflow:hidden;background:#f7f3f1;}
.district_info{width:7rem;height:4.16rem;margin:.42rem auto;box-shadow:0 0 .1rem .01rem #dae7ed;background:#fff;padding:.15rem .3rem 0;border-radius:.16rem;}
.district_info .word .number{text-align:right;}
.district_info .word{height:.9rem;line-height:.9rem;}
.district_info .word img{width:.51rem;height:.51rem;display:inline-block;vertical-align:middle;}
.district_info .word b{display:inline-block;width:1.68rem;height:.48rem;line-height:.48rem;text-align:center;border-radius:.24rem;background:#0098db;}
.district_info .word b.cant{background:#f0f0f0;}
.district_info .word input{display:inline-block;width:2.7rem;text-align:right;}
.district_info .chose_district{background:url("../assets/images/icon10.png") no-repeat right;background-size:.24rem .24rem;padding-right:.4rem;}
.checkout{background:#0098db;width:3.7rem;height:.88rem;line-height:.88rem;text-align:center;border-radius:.44rem;margin:3rem auto 0;}
/*弹出层*/
.ceng{width:100%;height:100%;position:absolute;top:0;left:0;background:rgba(255,255,255,.5);}
.ceng .ceng_content{width:5.9rem;background:#fff;border-radius:.24rem;padding:0 .46rem;box-shadow:0 0 1rem 0 #cfebf8;}
/*选择小区*/
.ceng_district{padding:.12rem 0 .6rem;}
.ceng_district .title{height:1.14rem;line-height:1.14rem;text-align:center;}
.ceng_district .word{height:.96rem;line-height:.96rem;}
.ceng_district .word span.r{background:url("../assets/images/sanjiao.png") no-repeat right;background-size:.16rem .23rem;padding-right:.44rem;}
/*列表显示*/
.show_list{width:100%;height:100%;overflow:hidden;position:absolute;top:0;left:0;z-index:20;background:rgba(0,0,0,.5);}
.show_list .content{max-height:5rem;overflow-y:auto;width:100%;position:absolute;bottom:0;background:#fff;}
.show_list .content p{line-height:.5rem;text-align:center;}
</style>
