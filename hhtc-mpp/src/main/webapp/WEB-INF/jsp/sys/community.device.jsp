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
        $.get("${ctx}/community/listAll",
            function(data){
                if(0 == data.code){
                    var options = "";
                    for(var i=0; i<data.data.length; i++){
                        options += '<option value="' + data.data[i].id + '">' + data.data[i].name + '</option>';
                    }
                    $("#community_select").html(options);
                }else{
                    console.log(data.msg);
                }
            }
        );
    }
    if(""=="${o}" || "update"=="${o}"){
        $.get("${ctx}/community/device/get/${id}",
            function(data){
                if(0 == data.code){
                    if("update" == "${o}"){
                        $.get("${ctx}/community/listAll",
                            function(datalist){
                                if(0 == datalist.code){
                                    var options = "";
                                    for(var i=0; i<datalist.data.length; i++){
                                        options += '<option value="' + datalist.data[i].id + '"';
                                        if(datalist.data[i].id == data.data.communityId){
                                            options += " selected=\"selected\"";
                                        }
                                        options += ">" + datalist.data[i].name + "</option>";
                                    }
                                    $("#community_select").html(options);
                                }else{
                                    console.log(datalist.msg);
                                }
                            }
                        );
                        $("#serialno_camera").val(data.data.serialnoCamera);
                        $("#serialno_relays").val(data.data.serialnoRelays);
                        $("#type").find("option[value=" + data.data.type + "]").prop("selected", true);
                        $("#remark").val(data.data.remark);
                    }else{
                        $("#get_communit_name").html(data.data.communityName);
                        $("#get_serialno_camera").html(data.data.serialnoCamera);
                        $("#get_serialno_relays").html(data.data.serialnoRelays);
                        $("#get_relays_doorid").html(data.data.relaysDoorid);
                        $("#get_type").html(data.data.type==1 ? "入场" : "出场");
                        $("#get_remark").html(data.data.remark);
                        $("#get_create_time").html(data.data.createTime);
                    }
                }else{
                    $.promptBox(data.msg, "#ffb848");
                }
            }
        );
    }
});
function validateForm(){
    if(isEmpty($("#serialno_camera").val())){
        $.promptBox("请输入摄像头序列号", "#ffb848");
    }else if(isEmpty($("#serialno_relays").val())){
        $.promptBox("请输入继电器序列号", "#ffb848");
    }else if(isEmpty($("#remark").val())){
        $.promptBox("请输入备注", "#ffb848");
    }else if($("#remark").val().length > 500){
        $.promptBox("备注内容不能超过500字", "#ffb848");
    }else{
        return true;
    }
}
function submit(){
    if(validateForm()){
        $.post("${ctx}/community/device/upsert",
            $("#deviceForm").serialize(),
            function(data){
                if(0 == data.code){
                    location.href = "${ctx}/community/device/list";
                }else{
                    $.promptBox(data.msg, "#ffb848");
                }
            }
        );
    }
}
</script>

<div class="c_nav">
    <div class="ti">设备信息</div>
</div>
<!--Content-->
<div id="getDiv" class="c_content">
    <!--Table order list-->
    <table class="tab_head tab_in tab_list2" width="100%">
        <tr class="ti"><th colspan="2">设备详情</th></tr>
        <tr><th width="15%">小区名称：</th><td id="get_communit_name"></td></tr>
        <tr><th>摄像头序列号：</th><td id="get_serialno_camera"></td></tr>
        <tr><th>继电器序列号：</th><td id="get_serialno_relays"></td></tr>
        <tr><th>继电器闸门ID：</th><td id="get_relays_doorid"></td></tr>
        <tr><th>出入类型：</th><td id="get_type"></td></tr>
        <tr><th>备注：</th><td id="get_remark"></td></tr>
        <tr><th>创建时间：</th><td id="get_create_time"></td></tr>
    </table>
    <table class="tab_head tab_in tab_list2" width="100%">
        <tr class="ti"><th colspan="3">操作</th></tr>
        <tr>
            <td class="txt_l"><a class="btn_r" href="javascript:history.back();">返回</a></td>
        </tr>
    </table>
    <!--/Table order list-->
</div>
<div id="updateDiv" class="c_content" style="display:none;">
    <!--Table order list-->
    <form id="deviceForm">
        <input type="hidden" name="id" value="${empty id ? 0 : id}"/>
        <table class="tab_in2" width="100%">
            <tr class="ti">
                <th colspan="2">设备详情</th>
            </tr>
            <tr>
                <th width="15%">小区名称：</th>
                <td>
                    <select id="community_select" name="communityId">
                    </select>
                </td>
            </tr>
            <tr>
                <th>摄像头序列号：</th>
                <td><input class="inpte" type="text" id="serialno_camera" name="serialnoCamera" maxlength="64"/></td>
            </tr>
            <tr>
                <th>继电器序列号：</th>
                <td><input class="inpte" type="text" id="serialno_relays" name="serialnoRelays" maxlength="64"/></td>
            </tr>
            <tr>
                <th>继电器闸门ID：</th>
                <td><input class="inpte" type="text" id="relays_doorid" name="relaysDoorid" maxlength="99"/></td>
            </tr>
            <tr>
                <th>出入类型：</th>
                <td>
                    <select id="type" name="type">
                        <option value="1">入场</option>
                        <option value="2">出场</option>
                    </select>
                </td>
            </tr>
            <tr>
                <th>备注：</th>
                <td><textarea id="remark" name="remark" style="height:300px;"></textarea></td>
            </tr>
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

<jsp:include page="../comm/footer.jsp"/>