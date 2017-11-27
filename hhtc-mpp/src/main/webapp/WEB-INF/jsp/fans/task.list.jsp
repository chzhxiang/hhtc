<%@ page pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<jsp:include page="../comm/header.jsp"/>

<div class="c_nav">
    <div class="ti">车位审核</div>
</div>
<!--Content-->
<div class="c_content">
    <!--Table list-->
    <table class="tab_list" width="100%">
        <tr>
            <th>头像</th>
            <th>昵称</th>
            <th>小区</th>
            <th>车位</th>
            <th>添加时间</th>
            <th>状态</th>
            <th>操作</th>
        </tr>
        <c:forEach items="${page.content}" var="goods">
            <tr>
                <td><span><img alt="头像" src="${goods.headimgurl}" height="30px" width="30px"></span></td>
                <td><span>${goods.nickname}</span></td>
                <td><span>${goods.communityName}</span></td>
                <td><span>${goods.carParkNumber}</span></td>
                <td><span><fmt:formatDate value="${goods.createTime}" pattern="yyyy-MM-dd HH:mm"/></span></td>
                <td><span>${goods.carAuditStatus eq 1 ? "审核中" : goods.carAuditStatus eq 2 ? "审核通过" : "审核拒绝"}</span></td>
                <td>
                    <a class="c09f mr_15" href="${ctx}/view?url=sys/goods&id=${goods.id}">查看</a>
                    <c:if test="${goods.carAuditStatus eq 1}">
                        <a class="c09f" href="${ctx}/view?url=sys/goods&o=update&id=${goods.id}">编辑</a>
                    </c:if>
                </td>
            </tr>
        </c:forEach>
    </table>
    <!--/Table list-->
    <jsp:include page="../comm/page.jsp?requestURI=${ctx}/goods/task/list"/>
</div>
<!--/Content-->

<jsp:include page="../comm/footer.jsp"/>