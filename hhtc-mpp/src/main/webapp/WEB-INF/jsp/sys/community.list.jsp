<%@ page pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<jsp:include page="../comm/header.jsp"/>

<div class="c_nav">
    <div class="ti">小区列表</div>
</div>
<!--Content-->
<div class="c_content">
    <c:if test="${user.type eq 1}">
        <!--Title-->
        <div class="title txt_r">
            <span class="sch2" style="margin-right:50px;">
                <form action="${ctx}/community/list" method="GET">
				<input id="name" name="name"  value="${name}" class="inpte fm2" type="text" placeholder="按小区名称搜索"/>
				<button type="submit"  class="btn fm2">搜索</button>
                </form>

			</span>
            <a class="bgre va_m" href="${ctx}/view?url=sys/community&o=add">+新增小区</a>
        </div>
        <!--/Title-->
    </c:if>
    <!--Table list-->
    <table class="tab_list" width="100%">
        <tr>
            <th>编号</th>
            <th>省份</th>
            <th>城市</th>
            <th>小区</th>
            <th>位置</th>
            <th>添加时间</th>
            <th>操作</th>
        </tr>
        <c:forEach items="${page.content}" var="community">
            <tr>
                <td><span>${community.id}</span></td>
                <td><span>${community.provinceName}</span></td>
                <td><span>${community.cityName}</span></td>
                <td><span>${community.name}</span></td>
                <td>
                    <span>
                        <c:if test="${fn:length(community.address) gt 16}">
                            ${fn:substring(community.address,0,32)}...
                        </c:if>
                        <c:if test="${fn:length(community.address) le 16}">
                            ${community.address}
                        </c:if>
                    </span>
                </td>
                <td><span><fmt:formatDate value="${community.createTime}" pattern="yyyy-MM-dd HH:mm"/></span></td>
                <td>
                    <a class="c09f mr_15" href="${ctx}/view?url=sys/community&id=${community.id}">查看</a>
                    <c:if test="${user.type eq 1}">
                        <a class="c09f" href="${ctx}/view?url=sys/community&o=update&id=${community.id}">编辑</a>
                    </c:if>
                </td>
            </tr>
        </c:forEach>
    </table>
    <!--/Table list-->
    <jsp:include page="../comm/page.jsp?requestURI=${ctx}/community/list"/>
</div>
<!--/Content-->

<jsp:include page="../comm/footer.jsp"/>