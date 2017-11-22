<%@ page pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<jsp:include page="../comm/header.jsp"/>

<script>
function search(){
    if(isEmpty($('#phoneNo').val())){
        $.promptBox("手机号不能为空", "#ffb848");
        return false;
    }
    window.location.href = "${ctx}/order/search?phoneNo=" + $('#phoneNo').val();
}
</script>

<div class="c_nav">
    <div class="ti">订单调配</div>
</div>
<!--Content-->
<div class="c_content">
    <!--Title-->
    <div class="title">
        <span class="sch2">
            <input class="inpte fm2" type="text" name="phoneNo" id="phoneNo" maxlength="11" placeholder="请输入车主手机号"/>
            <button class="btn fm2" onclick="search();">订单搜索</button>
        </span>
    </div>
    <!--/Title-->
</div>
<!--/Content-->

<jsp:include page="../comm/footer.jsp"/>