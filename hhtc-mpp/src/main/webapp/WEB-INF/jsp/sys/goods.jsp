<%@ page pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<jsp:include page="../comm/header.jsp"/>

<script src="${ctx}/js/ajaxfileupload.js"></script>
<script src="${ctx}/js/My97DatePicker/WdatePicker.js"></script>

<script>
$(function(){
    if("update"=="${o}" || "add"=="${o}"){
        $("#getDiv").hide();
        $("#updateDiv").show();
    }
    if(""=="${o}" || "update"=="${o}"){
        $.get("${ctx}/goods/get/${id}",
            function(data){
                if(0 == data.code){
                    if("update" == "${o}"){
                        $("#community_id").val(data.data.communityId);
                        $("#community_name").val(data.data.communityName);
                        $("#community_name_hidden").val(data.data.communityName);
                        $("#appid").val(data.data.appid);
                        $("#openid").val(data.data.openid);
                        $("#car_park_number").val(data.data.carParkNumber);
                        $("#car_park_number_hidden").val(data.data.carParkNumber);
                        $("#car_audit_status_hidden").val(data.data.carAuditStatus);
                        $("#car_audit_status").val(data.data.carAuditStatus==1 ? "审核中" : data.data.carAuditStatus==2 ? "审核通过" : "审核通过");
                        $("#car_useful_from_date").val(data.data.carUsefulFromDate);
                        $("#car_useful_end_date").val(data.data.carUsefulEndDate);
                        //$("#id_car_equity_img").val(data.data.carEquityImg);
                        //$("#id_img_car_equity_img").attr("src", '${ctxImgViewUrl}' + data.data.carEquityImg);
                        $("#id_car_park_img").val(data.data.carParkImg);
                        $("#id_img_car_park_img").attr("src", '${ctxImgViewUrl}' + data.data.carParkImg);
                    }else{
                        $("#get_communit_name").html(data.data.communityName);
                        $("#get_car_park_number").html(data.data.carParkNumber);
                        $("#get_car_park_img").html("<a href='${ctx}/view?url=comm/img&path=" + data.data.carParkImg + "' target='_blank'><img height='200px' width='300px' src='${ctxImgViewUrl}" + data.data.carParkImg + "'></a>");
                        //$("#get_car_equity_img").html("<a href='${ctx}/view?url=comm/img&path=" + data.data.carEquityImg + "' target='_blank'><img height='200px' width='300px' src='${ctxImgViewUrl}" + data.data.carEquityImg + "'></a>");
                        $("#get_car_useful_from_date").html(data.data.carUsefulFromDate);
                        $("#get_car_useful_end_date").html(data.data.carUsefulEndDate);
                        $("#get_car_audit_status").html(data.data.carAuditStatus==1 ? "审核中" : data.data.carAuditStatus==2 ? "审核通过" : "审核拒绝");
                        $("#get_car_audit_time").html(data.data.carAuditTime);
                    }
                }else{
                    $.promptBox(data.msg, "#ffb848");
                }
            }
        );
    }
});
function uploadIcons(){
    $.ajaxFileUpload({
        url:'${ctx}/common/uploadImg',
        secureuri:false,
        fileElementId:'id_imgData_car_park_img',
        dataType:'text',
        error:function(data, status, e){
            $.promptBox('图片上传失败', "#ffb848");n
        },
        success:function(data, status){
            data = data.replace(/<pre.*?>/g, '');
            data = data.replace(/<PRE.*?>/g, '');
            data = data.replace("<PRE>", '');
            data = data.replace("</PRE>", '');
            data = data.replace("<pre>", '');
            data = data.replace("</pre>", '');
            if(data.substring(0, 1) == 0){
                $('#id_car_park_img').val(data.substring(2));
                $('#id_img_car_park_img').attr("src", '${ctxImgViewUrl}' + data.substring(2));
            }else{
                $.promptBox(data.substring(2), "#ffb848");
            }
        }
    });
}
function validateForm(flag){
    if(flag == 2){
        if(isEmpty($("#car_useful_from_date").val()) || $("#car_useful_from_date").val()==0){
            $.promptBox("请输入车位产权起始有效期", "#ffb848");
        }else if(isEmpty($("#car_useful_end_date").val()) || $("#car_useful_end_date").val()==0){
            $.promptBox("请输入车位产权截止有效期", "#ffb848");
        }else if(parseInt($("#car_useful_end_date")) <= parseInt($("#car_useful_from_date"))){
            $.promptBox("车位产权截止有效期应大于起始有效期", "#ffb848");
        }else if(isEmpty($("#id_car_park_img").val()) || $("#id_car_park_img").val()=="${ctx}/img/qrcode.jpg"){
            $.promptBox("请上传车位平面图", "#ffb848");
        }else{
            return true;
        }
    }else{
        return true;
    }
}
function inputRemark(){
    var _remark = prompt("请输入拒绝原因", "");
    if(!isEmpty(_remark)){
        $("#car_audit_remark_hidden").val(_remark);
        return true;
    }else{
        return false;
    }
}
function audit(flag){
    $("#car_audit_status_hidden").val(flag);
    if(validateForm(flag) && confirm("确定审核 [ " + (flag==1 ? "通过" : "拒绝") +" ] 此车位么？")){
        if(flag==1 || (flag==2 && inputRemark())){
            $.post("${ctx}/fans/Audit",
                {id:${id}, status:flag,  auditRemark: ""},
                function(data){
                    if(0 == data.code){
                        alert("操作成功");
                        location.href = "${ctx}/fans/task/park/list";
                    }else{
                        $.promptBox(data.msg, "#ffb848");
                    }
                }
            );
        }
    }
}
</script>

<div class="c_nav">
    <div class="ti">车位信息</div>
</div>
<!--Content-->
<div id="getDiv" class="c_content">
    <!--Table order list-->
    <table class="tab_head tab_in tab_list2" width="100%">
        <tr class="ti"><th colspan="2">车位详情</th></tr>
        <tr><th width="15%">小区名称：</th><td id="get_communit_name"></td></tr>
        <tr><th>车位号：</th><td id="get_car_park_number"></td></tr>
        <tr><th>车位平面图：</th><td id="get_car_park_img"></td></tr>
        <%--
        <tr><th>产权证明图：</th><td id="get_car_equity_img"></td></tr>
        --%>
        <tr><th>起始有效期：</th><td id="get_car_useful_from_date"></td></tr>
        <tr><th>截止有效期：</th><td id="get_car_useful_end_date"></td></tr>
        <tr><th>车位审核状态：</th><td id="get_car_audit_status"></td></tr>
        <tr><th>车位审核时间：</th><td id="get_car_audit_time"></td></tr>
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
    <form id="goodsForm">
        <input type="hidden" id="community_id" name="communityId"/>
        <input type="hidden" id="appid" name="appid"/>
        <input type="hidden" id="openid" name="openid"/>
        <input type="hidden" id="community_name_hidden" name="communityName"/>
        <input type="hidden" id="car_park_number_hidden" name="carParkNumber"/>
        <input type="hidden" id="car_audit_status_hidden" name="carAuditStatus"/>
        <input type="hidden" id="car_audit_remark_hidden" name="carAuditRemark"/>
        <input type="hidden" name="id" value="${empty id ? 0 : id}"/>
        <table class="tab_in2" width="100%">
            <tr class="ti">
                <th colspan="2">车位详情</th>
            </tr>
            <tr>
                <th width="15%">小区名称：</th>
                <td><input class="inpte" type="text" id="community_name" maxlength="64" disabled="disabled"/></td>
            </tr>
            <tr>
                <th>车位号：</th>
                <td><input class="inpte" type="text" id="car_park_number" maxlength="64" disabled="disabled"/></td>
            </tr>
            <tr>
                <th>审核状态：</th>
                <td><input class="inpte" type="text" id="car_audit_status" maxlength="8" disabled="disabled"/></td>
            </tr>
            <tr>
                <th>起始有效期：</th>
                <td><input class="inpte Wdate" type="text" id="car_useful_from_date" name="carUsefulFromDate" maxlength="8" onFocus="WdatePicker({readOnly:true, dateFmt:'yyyyMMdd'})"/></td>
            </tr>
            <tr>
                <th>截止有效期：</th>
                <td><input class="inpte Wdate" type="text" id="car_useful_end_date" name="carUsefulEndDate" maxlength="8" onFocus="WdatePicker({readOnly:true, dateFmt:'yyyyMMdd'})"/></td>
            </tr>
            <%--
            <tr>
                <th>产权证明图：</th>
                <td>
                    <img id="id_img_car_equity_img" src="${ctx}/img/qrcode.jpg" style="width:200px; height:100px;">
                    <input type="hidden" id="id_car_equity_img" name="carEquityImg" value="${ctx}/img/qrcode.jpg"/>
                </td>
            </tr>
            --%>
            <tr>
                <th>
                    <a class="btn_g" href="javascript:void(0);" style="position:relative;display:block;">
                        点击上传车位平面图
                        <input type="file" id="id_imgData_car_park_img" onchange="javascript:uploadIcons();" name="imgData" style="position:absolute;left:0;top:0;width:100%;height:100%;z-index:999;opacity:0;"/>
                    </a>
                </th>
                <td>
                    <img id="id_img_car_park_img" src="${ctx}/img/qrcode.jpg" style="width:200px; height:100px;">
                    <input type="hidden" id="id_car_park_img" name="carParkImg" value="${ctx}/img/qrcode.jpg"/>
                </td>
            </tr>
        </table>
        <table class="tab_head tab_in tab_list2" width="100%">
            <tr class="ti"><th colspan="3">操作</th></tr>
            <tr>
                <td class="txt_l">
                    <a class="btn_r mr_15" href="javascript:history.back();">返回</a>
                    <a class="btn_g mr_15" href="javascript:audit(1);">审核通过</a>
                    <a class="btn_g" href="javascript:audit(2);">审核拒绝</a>
                </td>
            </tr>
        </table>
    </form>
    <!--/Table order list-->
</div>
<!--/Content-->

<jsp:include page="../comm/footer.jsp"/>