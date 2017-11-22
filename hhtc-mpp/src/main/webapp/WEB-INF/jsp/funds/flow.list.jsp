<%@ page pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<jsp:include page="../comm/header.jsp"/>

<div class="c_nav">
    <div class="ti">平台收益</div>
</div>
<!--Content-->
<div class="c_content">
    <!--Table list-->
    <table class="tab_list" width="100%">
        <tr>
            <th>编号</th>
            <th>金额（元）</th>
            <th>收支类型</th>
            <th>收支描述</th>
            <th>时间</th>
        </tr>
        <c:forEach items="${page.content}" var="flow">
            <tr>
                <td><span>${flow.id}</span></td>
                <td><span ${flow.inOut eq 'in' ? 'class="cf30 fw"' : 'cgre fw'}>${flow.money}</span></td>
                <td><span>${flow.inOut eq "in" ? "收入" : "支出"}</span></td>
                <td><span>${flow.inOutDesc}</span></td>
                <td><span><fmt:formatDate value="${flow.bizDateTime}" pattern="yyyy-MM-dd HH:mm"/></span></td>
            </tr>
        </c:forEach>
    </table>
    <!--/Table list-->
    <jsp:include page="../comm/page.jsp?requestURI=${ctx}/funds/flow/listByPlatform"/>
</div>
<!--/Content-->

<jsp:include page="../comm/footer.jsp"/>