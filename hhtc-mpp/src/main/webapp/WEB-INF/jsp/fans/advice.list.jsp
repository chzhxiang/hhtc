<%@ page pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<jsp:include page="../comm/header.jsp"/>

<script>
function alertContent(content){
    $('#alertContent').html(content)
    $.dialogBox();
}
</script>

<div class="c_nav">
    <div class="ti">意见反馈</div>
</div>
<!--Content-->
<div class="c_content">
    <!--Table list-->
    <table class="tab_list" width="100%">
        <tr>
            <th>头像</th>
            <th>昵称</th>
            <th>反馈内容</th>
            <th>反馈时间</th>
            <th>操作</th>
        </tr>
        <c:forEach items="${page.content}" var="advice">
            <tr>
                <td><span><img alt="头像" src="${advice.headimgurl}" height="30px" width="30px"></span></td>
                <td><span>${advice.nickname}</span></td>
                <td>
                    <span>
                        <c:if test="${fn:length(advice.content) gt 32}">
                            ${fn:substring(advice.content, 0, 32)}...
                        </c:if>
                        <c:if test="${fn:length(advice.content) le 32}">
                            ${advice.content}
                        </c:if>
                    </span>
                </td>
                <td><span><fmt:formatDate value="${advice.createTime}" pattern="yyyy-MM-dd HH:mm"/></span></td>
                <td>
                    <a class="c09f" href="javascript:alertContent('${advice.content}');">查看</a>
                </td>
            </tr>
        </c:forEach>
    </table>
    <!--/Table list-->
    <jsp:include page="../comm/page.jsp?requestURI=${ctx}/fans/advice/list"/>
</div>
<!--/Content-->
<!--Dialog-->
<div class="dialog_box">
	<div class="dg_ti"><span>反馈内容</span></div>
	<div class="dg_te">
		<ul class="dg_row">
			<li style="overflow:hidden;">
			    <span id="alertContent" style="white-space:normal; width:100%; padding:0 20px; text-align:center; box-sizing:border-box;"></span>
            </li>
		</ul>
	</div>
	<div class="dg_btn">
		<button id="dg_close" class="btn_g" type="button">关闭</button>
		<div class="clear"></div>
	</div>
</div>
<div class="shade_box"></div>
<!--/Dialog-->

<jsp:include page="../comm/footer.jsp"/>