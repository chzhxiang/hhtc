<%@ page pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<jsp:include page="../comm/header.jsp"/>
<script>
    function deleteCarNum(id){
        if(confirm('确定将该车牌删除吗？')){
            $.post("${ctx}/goods/delete",// TODO Cantabile 接口
                {id:id},
                function(data){
                    if(0==data.code){
                        alert('车牌已删除成功!');
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
    <div class="ti">车牌列表（已注册车牌数：${regcount}）</div>
</div>
<!--Content-->
<div class="c_content">
    <!--Table list-->
    <table class="tab_list" width="100%">
        <tr>
            <th>头像</th>
            <th>昵称</th>
            <th>小区</th>
            <th>车牌</th>
            <th>添加时间</th>
            <th>操作</th>
        </tr>
        <c:forEach items="${page.content}" var="carNum">
            <tr >
                <td><span><img alt="头像" src="${carNum.headimgurl}" height="30px" width="30px"></span></td>
                <td><span>${carNum.nickname}</span></td>

                <td><span>${carNum.communityName}</span></td>
                <td><span>${carNum.caNumber}</span></td>

                <td><span><fmt:formatDate value="${carNum.createTime}" pattern="yyyy-MM-dd HH:mm"/></span></td>
                <td>
                    <a class="c09f mr_15" href="${ctx}/view?url=sys/carnumber&id=${carNum.id}">查看</a>
                    <c:if test="${user.type eq 1}">
                        <a class="c09f" href="javascript:deleteCarNum('${carNum.id}');">删除</a>
                    </c:if>
                </td>
            </tr>
        </c:forEach>
    </table>
    <!--/Table list-->
    <%-- TODO Cantabile 接口地址--%>
    <jsp:include page="../comm/page.jsp?requestURI=${ctx}/carnumber/list"/>
</div>
<!--/Content-->

<jsp:include page="../comm/footer.jsp"/>