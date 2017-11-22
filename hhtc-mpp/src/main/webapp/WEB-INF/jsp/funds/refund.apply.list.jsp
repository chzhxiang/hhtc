<%@ page pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<jsp:include page="../comm/header.jsp"/>

<div class="c_nav">
    <div class="ti">退款列表</div>
</div>
<!--Content-->
<div class="c_content">
    <!--Table list-->
    <table class="tab_list" width="100%">
        <tr>
            <th>编号</th>
            <th>类型</th>
            <th>金额（分）</th>
            <th>支付状态</th>
            <th>审核状态</th>
            <th>审核时间</th>
            <th>申请时间</th>
        </tr>
        <c:forEach items="${page.content}" var="apply">
            <tr>
                <td><span>${apply.id}</span></td>
                <td><span>${apply.applyType eq 1 ? "退款（押金）" : "提现（余额）"}</span></td>
                <td><span>${apply.refundFee}</span></td>
                <td><span>${apply.payStatus eq 0 ? "未支付" : apply.payStatus eq 1 ? "支付中" : apply.payStatus eq 2 ? "支付成功" : apply.payStatus eq 3 ? "支付部分失败" : "支付全部失败"}</span></td>
                <td><span>${apply.auditStatus eq 1 ? "待审核" : apply.auditStatus eq 2 ? "审核通过" : "审核拒绝"}</span></td>
                <td><span>${apply.auditTime}</span></td>
                <td><span><fmt:formatDate value="${apply.createTime}" pattern="yyyy-MM-dd HH:mm"/></span></td>
            </tr>
        </c:forEach>
    </table>
    <!--/Table list-->
    <jsp:include page="../comm/page.jsp?requestURI=${ctx}/refund/apply/list"/>
</div>
<!--/Content-->

<jsp:include page="../comm/footer.jsp"/>