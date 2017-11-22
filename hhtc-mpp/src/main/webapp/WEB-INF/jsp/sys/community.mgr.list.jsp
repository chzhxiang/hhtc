<%@ page pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<jsp:include page="../comm/header.jsp"/>

<script src="${ctx}/js/webtoolkit.md5.js"></script>
<script>
var newPwd = '123456';
function inputPwd(){
    var _newPwd = prompt("请输入新密码", "");
    if(!isEmpty(_newPwd)){
        newPwd = _newPwd;
        return true;
    }else{
        return false;
    }
}
function resetPassword(id){
    if(inputPwd() && confirm('确定将该物管的登录密码重置为 [ ' + newPwd + ' ] 么？')){
        $.post("${ctx}/community/resetPassword",
            {uid:id, newPassword:MD5(newPwd)},
            function(data){
                if(0==data.code && data.data){
                    alert('密码已成功重置为 [ ' + newPwd + ' ]');
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
    <div class="ti">物管列表</div>
</div>
<!--Content-->
<div class="c_content">
    <!--Table list-->
    <table class="tab_list" width="100%">
        <tr>
            <th>编号</th>
            <th>登录帐号</th>
            <th>所辖小区</th>
            <th>创建时间</th>
            <th>操作</th>
        </tr>
        <c:forEach items="${page.content}" var="mgrUser">
            <tr>
                <td><span>${mgrUser.id}</span></td>
                <td><span>${mgrUser.username}</span></td>
                <c:forEach items="${communityNameMap}" var="entry">
                    <c:if test="${entry.key eq mgrUser.id}">
                        <td><span>${entry.value}</span></td>
                    </c:if>
                </c:forEach>
                <td><span>${mgrUser.createTime}</span></td>
                <td><a class="c09f" href="javascript:resetPassword('${mgrUser.id}');">密码重置</a></td>
            </tr>
        </c:forEach>
    </table>
    <!--/Table list-->
    <jsp:include page="../comm/page.jsp?requestURI=${ctx}/community/list/mgr"/>
</div>
<!--/Content-->

<jsp:include page="../comm/footer.jsp"/>