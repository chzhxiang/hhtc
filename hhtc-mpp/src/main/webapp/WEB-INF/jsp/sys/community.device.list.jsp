<%@ page pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<jsp:include page="../comm/header.jsp"/>

<script>
function deleteDevice(id){
    if(confirm('确定删除该设备吗？')){
        $.post("${ctx}/community/device/delete",
            {id:id},
            function(data){
                if(0==data.code){
                    alert('设备已删除！');
                    location.reload();
                }else{
                    $.promptBox(data.msg, "#ffb848");
                }
            }
        );
    }
}
function openDoor(deviceId, serialno, doorid){
    if(confirm('确定要开闸吗吗？')){
        $.post("${ctx}/community/device/openDoor",
            {deviceId:deviceId, serialno:serialno, doorid:doorid},
            function(data){
                if(0==data.code && data.data){
                    alert('开闸成功');
                }else{
                    alert('开闸失败');
                }
            }
        );
    }
}
</script>

<div class="c_nav">
    <div class="ti">小区设备列表</div>
</div>
<!--Content-->
<div class="c_content">
    <c:if test="${user.type eq 1}">
        <!--Title-->
        <div class="title txt_r">
            <%--
            <span class="sch2" style="margin-right:50px;">
                <form action="${ctx}/community/list" method="GET">
                    <input id="name" name="name"  value="${name}" class="inpte fm2" type="text" placeholder="按小区名称搜索"/>
                    <button type="submit"  class="btn fm2">搜索</button>
                </form>
			</span>
            --%>
            <a class="bgre va_m" href="${ctx}/view?url=sys/community.device&o=add">+新增设备</a>
        </div>
        <!--/Title-->
    </c:if>
    <!--Table list-->
    <table class="tab_list" width="100%">
        <tr>
            <th>编号</th>
            <th>小区名称</th>
            <th>摄像头系列号</th>
            <th>继电器系列号</th>
            <th>出入类型</th>
            <th>添加时间</th>
            <th>操作</th>
        </tr>
        <c:forEach items="${page.content}" var="device">
            <tr>
                <td><span>${device.id}</span></td>
                <td><span>${device.communityName}</span></td>
                <td><span>${device.serialnoCamera}</span></td>
                <td><span>${device.serialnoRelays}</span></td>
                <td><span>${device.type eq 1 ? "入场" : "出场"}</span></td>
                <td><span><fmt:formatDate value="${device.createTime}" pattern="yyyy-MM-dd HH:mm"/></span></td>
                <td>
                    <a class="c09f mr_15" href="${ctx}/view?url=sys/community.device&id=${device.id}">查看</a>
                    <a class="c09f mr_15" href="javascript:deleteDevice('${device.id}')">删除</a>
                    <a class="c09f" href="javascript:openDoor('${device.id}', '${device.serialnoRelays}', '${device.relaysDoorid}')">手动开闸</a>
                </td>
            </tr>
        </c:forEach>
    </table>
    <!--/Table list-->
    <jsp:include page="../comm/page.jsp?requestURI=${ctx}/community/device/list"/>
</div>
<!--/Content-->

<jsp:include page="../comm/footer.jsp"/>