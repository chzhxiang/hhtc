<%@ page pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<jsp:include page="../comm/header.jsp"/>

<script>
    $(function(){
        if("update"=="${o}" || "add"=="${o}"){
            $("#getDiv").hide();
            $("#updateDiv").show();
        }
        if("add"=="${o}"){
            $.get("${ctx}/mpp/user/list",
                function(data){
                    if(0 == data.code){
                        var options = '<option value="0">自动生成</option>';
                        for(var i=0; i<data.data.length; i++){
                            if(data.data[i].type == 2){
                                options += '<option value="' + data.data[i].id + '">' + data.data[i].username + '</option>';
                            }
                        }
                        $("#select_uid").html(options);
                    }else{
                        console.log(data.msg);
                    }
                }
            );
        }
        if(""=="${o}" || "update"=="${o}"){
            $.get("${ctx}/community/get/${id}",
                function(data){
                    if(0 == data.code){
                        var uid = data.data.uid;
                        if("update" == "${o}"){
                            $.get("${ctx}/mpp/user/list",
                                function(data){
                                    if(0 == data.code){
                                        var options = '<option value="0">自动生成</option>';
                                        for(var i=0; i<data.data.length; i++){
                                            var user = data.data[i];
                                            options += '<option value="' + user.id + '"';
                                            if(user.id == uid){
                                                options += " selected=\"selected\"";
                                            }
                                            options += ">" + user.username + "</option>";
                                        }
                                        $("#select_uid").html(options);
                                    }else{
                                        console.log(data.msg);
                                    }
                                }
                            );
                            $("#provinceName").val(data.data.provinceName);
                            $("#cityName").val(data.data.cityName);
                            $("#name").val(data.data.name);
                            $("#address").val(data.data.address);
                            $("#pointLng").val(data.data.pointLng + "," + data.data.pointLat);
                            $("#linkMan").val(data.data.linkMan);
                            $("#linkTel").val(data.data.linkTel);
                            $("#moneyBase").val(data.data.moneyBase);
                            $("#moneyRentMin").val(data.data.moneyRentMin);
                            $("#moneyRentDay").val(data.data.moneyRentDay);
                            $("#moneyRentNight").val(data.data.moneyRentNight);
                            $("#moneyRentFull").val(data.data.moneyRentFull);
                            $("#rentRatioPlatform").val(data.data.rentRatioPlatform);
                            $("#rentRatioCarparker").val(data.data.rentRatioCarparker);
                            var park_qu=JSON.parse(data.data.carParkNumberPrefix);
                            var html=[];
                            for(var key in park_qu){
                                html.push("<tr class='parkoperating'><td></td><td>楼层 <input class='ceng' type='text' style='width:60px;' value='"+key+"'> 区域")
                                park_qu[key].forEach(function(v,i){
                                    html.push("<label style='margin-right:10px;'><input class='qu' type='text' style='width:60px;' value='"+v+"'>"+(i!=0?"<i class='parkclose'>x</i>":"")+"</label>")
                                })
                                html.push("<span style='margin-right:10px;' class='addqu btn_g'>添加区域</span><span class='delqu btn_g'>删除楼层</span></td></tr>")
                            }
                            $(".addparkingnumber_befor").before(html.join(""))
                            $(".addqu").off().on("click",function(){
                                $(this).before("<label style='margin-right:10px;'><input class='qu' type='text' style='width:60px;'><i class='parkclose'>x</i></label>")
                                $(".parkclose").off().on("click",function(){
                                    $(this).parent("label").remove();
                                })
                            })
                            $(".parkclose").off().on("click",function(){
                                $(this).parent("label").remove();
                            })
                            $(".delqu").off().on("click",function(){
                                $(this).parents("tr").remove();
                            })
                        }else{
                            $.get('${ctx}/mpp/user/get/'+data.data.uid,
                                function(data){
                                    if(0 == data.code){
                                        $("#get_username").html(data.data.username);
                                    }else{
                                        console.log(data.msg);
                                    }
                                }
                            );
                            $("#get_provinceName").html(data.data.provinceName);
                            $("#get_cityName").html(data.data.cityName);
                            $("#get_point").html(data.data.pointLng + "," + data.data.pointLat);
                            $("#get_address").html(data.data.address);
                            $("#get_name").html(data.data.name);
                            $("#get_linkMan").html(data.data.linkMan);
                            $("#get_linkTel").html(data.data.linkTel);
                            $("#get_moneyBase").html(data.data.moneyBase);
                            $("#get_moneyRentMin").html(data.data.moneyRentMin);
                            $("#get_moneyRentDay").html(data.data.moneyRentDay);
                            $("#get_moneyRentNight").html(data.data.moneyRentNight);
                            $("#get_moneyRentFull").html(data.data.moneyRentFull);
                            $("#get_rentRatioPlatform").html(data.data.rentRatioPlatform);
                            $("#get_rentRatioCarparker").html(data.data.rentRatioCarparker);
                            var park_qu=JSON.parse(data.data.carParkNumberPrefix);
                            var html=[];
                            for(var key in park_qu){
                                html.push("<div>楼层:<span>"+key+"</span> 区域: ")
                                park_qu[key].forEach(function(v,i){
                                    html.push("<span>"+v+"</span>")
                                })
                                html.push("</div>")
                            }
                            $("#get_park_qu").html(html.join(""))
                        }
                    }else{
                        $.promptBox(data.msg, "#ffb848");
                    }
                }
            );
        }
    });
    function isEmpty(str){
        if(str.length==0){
            return true;
        }else{
            return false;
        }
    }
    function validateForm(){
        if(isEmpty($("#provinceName").val())){
            $.promptBox("请输入省份名称", "#ffb848");
        }else if(isEmpty($("#cityName").val())){
            $.promptBox("请输入城市名称", "#ffb848");
        }else if(isEmpty($("#name").val())){
            $.promptBox("请输入小区名称", "#ffb848");
        }else if(isEmpty($("#pointLng").val())){
            $.promptBox("请输入经纬度", "#ffb848");
        }else if(isEmpty($("#address").val())){
            $.promptBox("请输入小区位置", "#ffb848");
        }else if(isEmpty($("#xiaoqu").val())){
            $.promptBox("请添加车位号", "#ffb848");
        }else{
            return true;
        }
        return false;
    }
    function submit(){
        $('#uid').val($('#select_uid').val());
        $("#xiaoqu").val(getXiaoqu())
        if(validateForm()){
            $.post("${ctx}/community/upsert",
                    $("#communityForm").serialize(),
                    function(data){
                        if(0 == data.code){
                            location.href = "${ctx}/community/list";
                        }else{
                            $.promptBox(data.msg, "#ffb848");
                        }
                    }
            );
        }
    }
    function getXiaoqu(){
        var obj={},qu="",ceng="",pd=false;
        $(".parkoperating").each(function(){
            ceng=$(this).find(".ceng").val();
            if(obj[ceng]){
                alert("包含重复的楼层");
                pd=true;
                return false;
            }else if(ceng){
                qu=[];
                $(this).find(".qu").each(function(){
                    qu.push($(this).val());
                })
                obj[ceng]=qu;
            }
        })
        if(pd){
            return false;
        }
        var ajaxStr=JSON.stringify(obj);
        return ajaxStr
    }
</script>
<script>
    $(function(){
        $("input.inp_stop_type").on("click", function(){
            var o = $(this),
                    p = $("tr.tr_stop_type"),
                    n = o.index("input.inp_stop_type");

            if(n>0){
                if(o.is(":checked")){
                    p.hide().eq(n-1).show();
                }
            }else{
                p.hide();
            }
        });
        $("#d_type").on("click, focusin", "input.inpte", function(){
            $("td.d_type").find("input[type='radio']").prop("checked", false);
            $(this).prev("label").find("input[type='radio']").prop("checked", true);
        });
        /*添加车位号*/
        $(".addparkingnumber").on("click",function(){
            $(this).parents("tr").after("<tr class='parkoperating'><td></td><td>楼层 <input class='ceng' type='text' style='width:60px;'> 区域 <label style='margin-right:10px;'><input class='qu' type='text' style='width:60px;'></label><span style='margin-right:10px;' class='addqu btn_g'>添加区域</span><span class='delqu btn_g'>删除楼层</span></td></tr>")
            $(".addqu").off().on("click",function(){
                $(this).before("<label style='margin-right:10px;'><input class='qu' type='text' style='width:60px;'><i class='parkclose'>x</i></label>")
                $(".parkclose").off().on("click",function(){
                    $(this).parent("label").remove();
                })
            })
            $(".delqu").off().on("click",function(){
                $(this).parents("tr").remove();
            })

        })

        /*添加车位号*/
    });
</script>
<style type="text/css">
    /*添加车位号*/
    .parkoperating label{position:relative;}
    .parkoperating label i{display:block;width:12px;height:12px;text-align:center;line-height:12px;font-size:14px;overflow:hidden;position:absolute;right:0;top:-6px;background:#090;color:#fff;border-radius:50%;}
    #get_park_qu span{padding:0 10px;display:inline-block;vertical-align:middle;background:#f5f5f5;border-radius:4px;}
    /*添加车位号*/
</style>
<div class="c_nav">
    <div class="ti">小区详情</div>
</div>
<!--Content-->
<div id="getDiv" class="c_content">
    <!--Table order list-->
    <table class="tab_head tab_in tab_list2" width="100%">
        <tr class="ti"><th colspan="2">小区信息</th></tr>
        <tr><th width="15%">省份：</th><td id="get_provinceName"></td></tr>
        <tr><th>城市：</th><td id="get_cityName"></td></tr>
        <tr><th>地址：</th><td id="get_address"></td></tr>
        <tr><th>经纬度：</th><td id="get_point"></td></tr>
        <tr><th>小区名称：</th><td id="get_name"></td></tr>
        <tr><th>物管帐号：</th><td id="get_username"></td></tr>
        <tr><th>联系人：</th><td id="get_linkMan"></td></tr>
        <tr><th>联系人手机号：</th><td id="get_linkTel"></td></tr>
        <tr><th>押金：</th><td id="get_moneyBase"></td></tr>
        <tr><th>租金（起步价）：</th><td id="get_moneyRentMin"></td></tr>
        <tr><th>租金（日间）：</th><td id="get_moneyRentDay"></td></tr>
        <tr><th>租金（夜间）：</th><td id="get_moneyRentNight"></td></tr>
        <tr><th>租金（全天）：</th><td id="get_moneyRentFull"></td></tr>
        <tr><th>平台分润比例：</th><td id="get_rentRatioPlatform"></td></tr>
        <tr><th>车位主分润比例：</th><td id="get_rentRatioCarparker"></td></tr>
        <tr><th>车位号：</th><td id="get_park_qu"></td></tr>
    </table>
    <table class="tab_head tab_in tab_list2" width="100%">
        <tr class="ti"><th colspan="3">操作</th></tr>
        <tr>
            <td class="txt_l"><a class="btn_r" href="javascript:history.back();">返回</a></td>
        </tr>
    </table>
    <!--/Table order list-->
</div>
<c:if test="${user.type eq 1}">
    <div id="updateDiv" class="c_content" style="display:none;">
        <!--Table order list-->
        <form id="communityForm">
            <input type="hidden" name="id" value="${empty id ? 0 : id}"/>
            <input type="hidden" name="uid" id="uid"/>
            <input type="hidden" name="carParkNumberPrefix" id="xiaoqu"/>
            <table class="tab_in2" width="100%">
                <tr class="ti">
                    <th colspan="2">小区信息</th>
                </tr>
                <tr>
                    <th width="15%">省份：</th>
                    <td><input class="inpte" type="text" id="provinceName" name="provinceName" maxlength="16"/></td>
                </tr>
                <tr>
                    <th width="15%">城市：</th>
                    <td><input class="inpte" type="text" id="cityName" name="cityName" maxlength="16"/></td>
                </tr>
                <tr>
                    <th width="15%">地址：</th>
                    <td><input class="inpte" type="text" id="address" name="address" maxlength="32"/></td>
                </tr>
                <tr>
                    <th width="15%">经纬度：</th>
                    <td>
                        <input class="inpte" type="text" id="pointLng" name="pointLng" maxlength="32" style="width:295px"/>
                        <a target="_blank" href="http://lbs.qq.com/tool/getpoint/">点击获取经纬度</a>
                    </td>
                </tr>
                <tr>
                    <th width="15%">小区名称：</th>
                    <td><input class="inpte" type="text" id="name" name="name" maxlength="16"/></td>
                </tr>
                <tr>
                    <th>物管帐号：</th>
                    <td>
                        <select id="select_uid" ${empty param.id ? "style=\"width:150px\"" : "disabled=\"disabled\" style=\"background-color:#eee; width:150px\""}>
                            <option value="0">自动生成</option>
                            <option value="1">主动选择</option>
                        </select>
                    </td>
                </tr>
                <tr>
                    <th width="15%">联系人：</th>
                    <td><input class="inpte" type="text" id="linkMan" name="linkMan" maxlength="16"/></td>
                </tr>
                <tr>
                    <th width="15%">联系人手机号：</th>
                    <td><input class="inpte" type="text" id="linkTel" name="linkTel" maxlength="11"/></td>
                </tr>
                <tr>
                    <th width="15%">押金：</th>
                    <td><input class="inpte" type="text" id="moneyBase" name="moneyBase" maxlength="8"/></td>
                </tr>
                <tr>
                    <th width="15%">租金（起步价）：</th>
                    <td><input class="inpte" type="text" id="moneyRentMin" name="moneyRentMin" maxlength="8"/></td>
                </tr>
                <tr>
                    <th width="15%">租金（日间）：</th>
                    <td><input class="inpte" type="text" id="moneyRentDay" name="moneyRentDay" maxlength="8"/></td>
                </tr>
                <tr>
                    <th width="15%">租金（夜间）：</th>
                    <td><input class="inpte" type="text" id="moneyRentNight" name="moneyRentNight" maxlength="8"/></td>
                </tr>
                <tr>
                    <th width="15%">租金（全天）：</th>
                    <td><input class="inpte" type="text" id="moneyRentFull" name="moneyRentFull" maxlength="8"/></td>
                </tr>
                <tr>
                    <th width="15%">平台分润比例（比如20%则填写20）：</th>
                    <td><input class="inpte" type="text" id="rentRatioPlatform" name="rentRatioPlatform" maxlength="8"/></td>
                </tr>
                <tr>
                    <th width="15%">车位主分润比例（比如20%则填写20）：</th>
                    <td><input class="inpte" type="text" id="rentRatioCarparker" name="rentRatioCarparker" maxlength="8"/></td>
                </tr>
                <!--添加车位号-->
                <tr>
                    <th>车位号</th>
                    <td><span class="addparkingnumber btn_g" style="margin-right:10px;">添加楼层</span></td>
                </tr>
                <tr class="addparkingnumber_befor" style="display:none;"></tr>
                <!--添加车位号-->
            </table>
            <table class="tab_head tab_in tab_list2" width="100%">
                <tr class="ti"><th colspan="3">操作</th></tr>
                <tr>
                    <td class="txt_l"><a class="btn_g" href="javascript:submit();">保存</a></td>
                </tr>
            </table>
        </form>
        <!--/Table order list-->
    </div>
    <!--/Content-->
</c:if>

<jsp:include page="../comm/footer.jsp"/>