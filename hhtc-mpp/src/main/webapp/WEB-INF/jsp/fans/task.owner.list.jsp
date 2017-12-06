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
    if(confirm("确定审核 [ " + (flag==1 ? "通过" : "拒绝") +" ] 此车牌号么？")){
        if(flag==1 || (flag==2 && inputRemark())){
            $.post("${ctx}/fans/Audit",
                {id:id, status:flag,  auditRemark: remark},
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
    <div class="ti">车牌审核</div>
</div>
<!--Content-->
<div class="c_content">
    <!--Table list-->
    <table class="tab_list" width="100%">
        <tr>
            <th>头像</th>
            <th>昵称</th>
            <th>手机</th>
            <th>住址</th>
            <th>车牌号</th>
            <th>提交时间</th>
            <th>审核</th>
        </tr>
        <c:forEach items="${page.content}" var="audit">
            <tr>
                <td><span><img alt="头像" src="${audit.headimgurl}" height="30px" width="30px"></span></td>
                <td><span>${audit.nickname}</span></td>
                <td><span>${audit.phoneNo}</span></td>
                <td><span>${audit.communityName}</span></td>
                <td><span>${audit.content}</span></td>
                <td><span><fmt:formatDate value="${audit.createTime}" pattern="yyyy-MM-dd HH:mm"/></span></td>
                <td>
                    <a class="c09f mr_15" href="javascript:audit('${audit.id}', 1);">通过</a>
                    <a class="c09f" href="javascript:audit('${audit.id}', 2);">拒绝</a>
                </td>
            </tr>
        </c:forEach>
    </table>
    <!--/Table list-->
    <jsp:include page="../comm/page.jsp?requestURI=${ctx}/fans/task/owner/list"/>
</div>
<!--/Content-->

<jsp:include page="../comm/footer.jsp"/>