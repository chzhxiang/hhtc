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
            <th>小区</th>
            <th>门牌号</th>
            <th>手机</th>
            <th>身份</th>
            <th>注册时间</th>
        </tr>
        <c:forEach items="${page.content}" var="fans">
            <tr>
                <td><span><img alt="头像" src="${fans.headimgurl}" height="30px" width="30px"></span></td>
                <td><span>${fans.nickname}</span></td>
                <td><span>${fans.sex eq 1 ? '男' : fans.sex eq 2 ? '女' : '未知'}</span></td>
                <td><span>${fans.communityName}</span></td>
                <td><span>${fans.houseNumber}</span></td>
                <td><span>${fans.phoneNo}</span></td>
                <td><span>${fans.infor_state.charAt(4) eq 49?
                                    (fans.infor_state.charAt(3) eq 49 ?"车主 && 车位主 ":" 车主 ")
                                    :fans.infor_state.charAt(3) eq 49 ?"车位主":""}</span></td>
                <td><span><fmt:formatDate value="${fans.createTime}" pattern="yyyy-MM-dd HH:mm"/></span></td>
            </tr>
        </c:forEach>
    </table>
    <!--/Table list-->
    <jsp:include page="../comm/page.jsp?requestURI=${ctx}/fans/list"/>
</div>
<!--/Content-->

<jsp:include page="../comm/footer.jsp"/>