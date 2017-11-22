<template>
  <div class="license_plate">
    <div class="top font24 fcg48"><i></i>{{fansInfo.carOwnerCommunityName||fansInfo.carParkCommunityName}}</div>
    <div class="content">
      <div class="title font38 fcg43">选择车牌</div>
      <div class="choseplate"><span class="" @click="choseCar">{{carHead||"请选择"}}</span><span><input class="font28" type="text" name="" placeholder="请输入车牌号" v-model="carNumberRear"></span></div>
      <div class="add fcw font34" @click="checkout">确认添加</div>
      <div class="title2 font38 fcg43" v-show="carNmberList.length>0">使用过的车牌</div>
      <div class="time_chose fcg43 font32" v-show="carNmberList.length>0">
        <div v-for="carNmber in carNmberList" @click="goCheckout(carNmber)"><span class="l">车牌</span><span class="r">{{carNmber|carShow}}</span></div>
      </div>
    </div>
    <ul id="education_list" style="display: none">
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
    data:function(){
      return {
        fansInfo:{},
        carNmberList:[],
        carList:["京","津","冀","渝","晋","蒙","辽","吉","黑","沪","苏","浙","皖","闽","赣","鲁","豫","鄂","湘","粤","桂","琼","渝","川","贵","云","藏","陕","甘","青","宁","新"],
        carList2:["A","B","C","D","E","F","G","H","I","J","K","L","M","N","O","P","Q","R","S","T","U","V","W","X","Y","Z"],
        carHead:"",
        carNumberRear:"",
        maxLength:5
      }
    },
    mounted:function(){
      var that=this;
      
    },
    activated:function(){
      //每次进入
      this.carHead="";
      this.carNumberRear="";
      this.getFansInfo();
      this.srcollInit()
    },
    created:function(){
      // 组件创建完
    },
    watch:{
      //监视变化
      carHead:function(){
        if(this.carHead.indexOf("沪")!=-1){
          this.maxLength=6;
        }else{
          this.maxLength=5;
        }
      },
      carNumberRear:function(){
        this.carNumberRear=this.carNumberRear.replace(/[^a-zA-Z0-9]/g,"");
        if(this.carNumberRear.length>this.maxLength){
          this.carNumberRear=this.carNumberRear.substr(0,this.maxLength)
        }
      }
    },
    methods:{
      srcollInit:function(){
        var that=this;
        setTimeout(function(){
          $("#education_list").mobiscroll().treelist({
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
        },100)
      },
      choseCar:function(){
        var that=this;
        $("input[id^=education_list_dummy]").focus();
      },
      goCheckout:function(carNumber){
        localStorage.setItem("carNumber",carNumber);
        setTimeout(function(){
          history.back();
        },100)
      },
      checkout:function(){
        var that=this;
        if(!this.carHead||this.carNumberRear.length<5){
          error_msg("请填写完整的车牌号",2000)
          return false
        }
        $.ajax({
          url:urlDir+"/wx/fans/carNumber/add",
          type:"POST",
          data:{
            carNumber:this.carHead+this.carNumberRear
          },
          success:function(res){
            if(res.code==0){
              localStorage.setItem("carNumber",that.carHead+that.carNumberRear);
              setTimeout(function(){
                history.back();
              },100)
            }
          },
          error:function(res){

          }
        })
      },
      getFansInfo:function(){
        //获取用户信息
        var that=this;
        $.ajax({
          url:urlDir+"/wx/fans/get",
          type:"GET",
          success:function(res){
            if(res.code==0){
              that.fansInfo=res.data;
              that.carNmberList=res.data.carNumber.split("`");
              localStorage.setItem("fansInfo",toJson(res.data));
            }
          },
          error:function(res){

          }
        })
      },
    }
  }
</script>
<style scoped>
.license_plate{width:100%;height:100%;position:relative;padding-bottom:1rem;overflow:hidden;}
.license_plate .top{width:100%;height:.54rem;line-height:.54rem;text-align:center;background:#fff;position:absolute;top:0;z-index:2;box-shadow:0 0 .1rem .01rem #dae7ed;}
.license_plate .top i{display:inline-block;width:.24rem;height:.28rem;background:url("../assets/images/icon.png") no-repeat center;background-size:.24rem .28rem;vertical-align:middle;margin-right:.12rem;}
.license_plate .content{background:#e8e4e2;width:100%;height:100%;padding-top:.54rem;position:absolute;top:0;}
.title{height:1.32rem;line-height:1.32rem;text-align:center;margin-top:.14rem;}
.choseplate{height:.84rem;line-height:.84rem;padding:0 .24rem;}
.choseplate span{display:inline-block;height:.84rem; border-radius:.16rem;box-shadow:0 0 .1rem .01rem #dae7ed;background:#fff;overflow:hidden;}
.choseplate span:nth-child(1){width:1.69rem;background:#fff url("../assets/images/sanjiao.png") no-repeat 1.22rem;background-size:.16rem .23rem;padding-left:.46rem;margin-right:.36rem;}
.choseplate span:nth-child(2){width:4.96rem;}
.choseplate span input{display:inline-block;width:100%;height:.5rem;line-height:.5rem;padding-left:.44rem;background:#fff;}
.add{width:3.7rem;height:.88rem;line-height:.88rem;text-align:center;background:#0098db;border-radius:.44rem;margin:1.16rem auto .54rem;}
.title2{height:.9rem;line-height:.9rem;text-align:center;}
.time_chose{width:7rem;border-radius:.16rem;box-shadow:0 0 .1rem .01rem #dae7ed;margin:.12rem auto .44rem;background:#fff;padding:.13rem .3rem;}
.time_chose div{height:.9rem;line-height:.9rem;overflow:hidden;}
</style>
