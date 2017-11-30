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
            <th>电话</th>
            <th>住址</th>
            <th>车位</th>
            <th>提交时间</th>
            <th>操作</th>
        </tr>
        <c:forEach items="${page.content}" var="audit">
            <tr>
                <td><span><img alt="头像" src="${audit.headimgurl}" height="30px" width="30px"></span></td>
                <td><span>${audit.nickname}</span></td>
                <td><span>${audit.phone}</span></td>
                <td><span>${audit.communityName}</span></td>
                <td><span>${audit.content.split("@")[0]}</span></td>
                <td><span><fmt:formatDate value="${audit.createTime}" pattern="yyyy-MM-dd HH:mm"/></span></td>
                <td>
                    <a class="c09f mr_15" href="${ctx}/view?url=sys/goods&id=${audit.id}">查看</a>
                    <a class="c09f" href="${ctx}/view?url=sys/goods&o=update&id=${audit.id}">编辑</a>
                </td>
            </tr>
        </c:forEach>
    </table>
    <!--/Table list-->
    <jsp:include page="../comm/page.jsp?requestURI=${ctx}/fans/task/park/list"/>
</div>
<!--/Content-->

<jsp:include page="../comm/footer.jsp"/>