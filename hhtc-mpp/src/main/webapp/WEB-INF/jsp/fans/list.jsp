<%@ page pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<jsp:include page="../comm/header.jsp"/>

<div class="c_nav">
    <div class="ti">粉丝列表</div>
</div>
<!--Content-->
<div class="c_content">
    <!--Table list-->
    <table class="tab_list" width="100%">
        <tr>
            <th>头像</th>
            <th>昵称</th>
            <th>性别</th>
            <th>手机</th>
            <th>身份</th>
            <th>车牌号</th>
            <th>注册时间</th>
        </tr>
        <c:forEach items="${page.content}" var="fans">
            <tr>
                <td><span><img alt="头像" src="${fans.headimgurl}" height="30px" width="30px"></span></td>
                <td><span>${fans.nickname}</span></td>
                <td><span>${fans.sex eq 1 ? '男' : fans.sex eq 2 ? '女' : '未知'}</span></td>
                <td><span>${fans.phoneNo}</span></td>
                <td><span>${fans.carOwnerStatus eq 2 && fans.carParkStatus eq 2 ? '车主 && 车位主' : fans.carOwnerStatus eq 2 ? '车主' : fans.carParkStatus eq 2 ? '车位主' : '未注册'}</span></td>
                <td><span>${fans.carNumber}</span></td>
                <c:choose>
                    <c:when test="${fans.carOwnerStatus eq 2 && fans.carParkStatus eq 2}">
                        <td><span><fmt:formatDate value="${fans.carOwnerRegTime}" pattern="yyyy-MM-dd HH:mm"/> && <fmt:formatDate value="${fans.carParkRegTime}" pattern="yyyy-MM-dd HH:mm"/></span></td>
                    </c:when>
                    <c:when test="${fans.carOwnerStatus eq 2 && fans.carParkStatus ne 2}">
                        <td><span><fmt:formatDate value="${fans.carOwnerRegTime}" pattern="yyyy-MM-dd HH:mm"/></span></td>
                    </c:when>
                    <c:when test="${fans.carOwnerStatus ne 2 && fans.carParkStatus eq 2}">
                        <td><span><fmt:formatDate value="${fans.carParkRegTime}" pattern="yyyy-MM-dd HH:mm"/></span></td>
                    </c:when>
                    <c:otherwise>
                        <td><span>-</span></td>
                    </c:otherwise>
                </c:choose>
            </tr>
        </c:forEach>
    </table>
    <!--/Table list-->
    <jsp:include page="../comm/page.jsp?requestURI=${ctx}/fans/list"/>
</div>
<!--/Content-->

<jsp:include page="../comm/footer.jsp"/>