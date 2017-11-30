<%@ page pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<jsp:include page="../comm/header.jsp"/>

<script>
/**
 * id     : 退款id
 * status : 审核状态：2--审核通过，3--审核拒绝
 */
function audit(id, status){
    if(confirm('确定要 [ ' + (status==1?'审核通过':'审核拒绝') + ' ] 此笔退款申请么？' + (status==1?'\r\n审核通过后系统会自动退款或发红包给粉丝':''))){
        $.post("${ctx}/refund/apply/audit",
            {id:id, auditStatus:status},
            function(data){
                if(0 == data.code){
                    if(status == 1){
                        alert('提交成功，处理结果请于 [ 退款列表 ] 查询');
                    }
                    location.reload();
                }else{
                    $.promptBox(data.msg, "#ffb848");
                }
            }
        );
    }
}
</script>

<div class="c_nav">
    <div class="ti">退款审核</div>
</div>
<!--Content-->
<div class="c_content">
    <!--Table list-->
    <table class="tab_list" width="100%">
        <tr>
            <th>头像</th>
            <th>昵称</th>
            <th>手机</th>
            <th>地址</th>
            <th>类型</th>
            <th>金额</th>
            <th>申请时间</th>
            <th>操作</th>
        </tr>
        <c:forEach items="${page.content}" var="audit">
            <tr>
                <td><span><img alt="头像" src="${audit.headimgurl}" height="30px" width="30px"></span></td>
                <td><span>${audit.nickname}</span></td>
                <td><span>${audit.phone}</span></td>
                <td><span>${audit.communityName}</span></td>
                <td><span>${audit.type eq 5 ? "退款（押金）" : "提现（余额）"}</span></td>
                <td><span>${audit.content}</span></td>
                <td><span><fmt:formatDate value="${apply.createTime}" pattern="yyyy-MM-dd HH:mm"/></span></td>
                <td>
                    <a class="c09f mr_15" href="javascript:audit('${apply.id}', '1');">审核通过</a>
                    <a class="c09f" href="javascript:audit('${apply.id}', '2');">审核拒绝</a>
                </td>
            </tr>
        </c:forEach>
    </table>
    <!--/Table list-->
    <jsp:include page="../comm/page.jsp?requestURI=${ctx}/refund/apply/task/list"/>
</div>
<!--/Content-->

<jsp:include page="../comm/footer.jsp"/>