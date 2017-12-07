<%@ page pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<jsp:include page="../comm/header.jsp"/>
<script>
    function deleteGoods(id){
        if(confirm('确定将该车位删除吗？')){
            $.post("${ctx}/goods/delete",
                {id:id},
                function(data){
                    if(0==data.code){
                        alert('车位已删除成功!');
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
    <div class="ti">车位列表（已注册车位数：${regcount}）</div>
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
            <th>有效期</th>
            <th>添加时间</th>
            <th>状态</th>
            <th>操作</th>
        </tr>
        <c:forEach items="${page.content}" var="goods">
            <%--<c:if test="${goods.isRepetition eq 1}">--%>
                <%--<tr class="cf30 fw">--%>
            <%--</c:if>--%>
            <%--<c:if test="${goods.isRepetition eq 0}">--%>
                <%--<tr >--%>
            <%--</c:if>--%>
            <tr >
                <td><span><img alt="头像" src="${goods.headimgurl}" height="30px" width="30px"></span></td>
                <td><span>${goods.nickname}</span></td>

                <td><span>${goods.communityName}</span></td>
                <td><span>${goods.carParkNumber}</span></td>

                <td><span>${goods.carUsefulEndDate}</span></td>

                <td><span><fmt:formatDate value="${goods.createTime}" pattern="yyyy-MM-dd HH:mm"/></span></td>
                <td><span>${goods.state eq "long" ? "私有" : "租赁"}</span></td>
                <td>
                    <a class="c09f mr_15" href="${ctx}/view?url=sys/goods&id=${goods.id}">查看</a>
                    <c:if test="${user.type eq 1}">
                        <a class="c09f" href="javascript:deleteGoods('${goods.id}');">删除</a>
                    </c:if>
                </td>
            </tr>
        </c:forEach>
    </table>
    <!--/Table list-->
    <jsp:include page="../comm/page.jsp?requestURI=${ctx}/goods/list"/>
</div>
<!--/Content-->

<jsp:include page="../comm/footer.jsp"/>