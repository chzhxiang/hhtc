<%@ page pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<jsp:include page="../comm/header.jsp"/>

<script>
/**
 * 审核备注
 */
var remark = "approved";
function inputRemark(){
    var _remark = prompt("请输入拒绝原因", "");
    if(!isEmpty(_remark)){
        remark = _remark;
        return true;
    }else{
        return false;
    }
}
function audit(id, flag){
    if(confirm("确定审核 [ " + (flag==2 ? "通过" : "拒绝") +" ] 此车主么？")){
        if(flag==2 || (flag==3 && inputRemark())){
            $.post("${ctx}/fans/carAudit",
                {id:id, status:flag, type:1, auditRemark: remark},
                function(data){
                    if(0 == data.code){
                        alert("操作成功");
                        location.href = "${ctx}/fans/task/owner/list";
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
    <div class="ti">车主审核</div>
</div>
<!--Content-->
<div class="c_content">
    <!--Table list-->
    <table class="tab_list" width="100%">
        <tr>
            <th>头像</th>
            <th>昵称</th>
            <th>手机</th>
            <th>小区名称</th>
            <th>门牌号</th>
            <th>车牌号</th>
            <th>注册时间</th>
            <th>审核</th>
        </tr>
        <c:forEach items="${page.content}" var="fans">
            <tr>
                <td><span><img alt="头像" src="${fans.headimgurl}" height="30px" width="30px"></span></td>
                <td><span>${fans.nickname}</span></td>
                <td><span>${fans.phoneNo}</span></td>
                <td><span>${fans.carOwnerCommunityName}</span></td>
                <td><span>${fans.houseNumber}</span></td>
                <td><span>${fans.carNumber}</span></td>
                <td><span><fmt:formatDate value="${fans.carOwnerRegTime}" pattern="yyyy-MM-dd HH:mm"/></span></td>
                <td>
                    <a class="c09f mr_15" href="javascript:audit('${fans.id}', 2);">通过</a>
                    <a class="c09f" href="javascript:audit('${fans.id}', 3);">拒绝</a>
                </td>
            </tr>
        </c:forEach>
    </table>
    <!--/Table list-->
    <jsp:include page="../comm/page.jsp?requestURI=${ctx}/fans/task/owner/list"/>
</div>
<!--/Content-->

<jsp:include page="../comm/footer.jsp"/>