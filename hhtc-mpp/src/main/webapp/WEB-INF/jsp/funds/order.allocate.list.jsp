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

var chengfaIng = false;
var chengfaMoney = 0;
function inputChengfaMoney(){
    var _money = prompt("请输入惩罚金额（单位：元）", "");
    if(!isEmpty(_money)){
        chengfaMoney = _money;
        chengfaIng = true;
        return true;
    }else{
        chengfaIng = false;
        return false;
    }
}
function chengfaCarparker(orderId){
    if(!chengfaIng && confirm("确定惩罚此車位主么？") && inputChengfaMoney()){
        $.post("${ctx}/order/chengfaCarparker",
            {orderId:orderId, money:chengfaMoney},
            function(data){
                chengfaIng = false;
                if(0 == data.code){
                    alert("操作成功");
                }else{
                    $.promptBox(data.msg, "#ffb848");
                }
            }
        );
    }
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
            <input class="inpte fm2" type="text" name="phoneNo" id="phoneNo" value="${phoneNo}" maxlength="11" placeholder="请输入车主手机号"/>
            <button class="btn fm2" onclick="search();">订单搜索</button>
        </span>
    </div>
    <!--/Title-->
    <!--Table list-->
    <table class="tab_list" width="100%">
        <tr>
            <th>编号</th>
            <th>小区</th>
            <th>车位号</th>
            <th>车牌号</th>
            <th>停车起始时间</th>
            <th>订单金额（元）</th>
            <th>订单状态</th>
            <th>操作</th>
        </tr>
        <c:forEach items="${list}" var="order">
            <tr>
                <td><span>${order.id}</span></td>
                <td><span>${order.communityName}</span></td>
                <td><span>${order.carParkNumber}</span></td>
                <td><span>${order.carNumber}</span></td>
                <td><span>${order.timeStart}</span></td>
                <td><span>${order.spbillCreateIp}</span></td>
                <td><span>${order.orderStatus eq 0 ? "待支付" : order.orderStatus eq 1 ? "支付中" : order.orderStatus eq 2 ? "支付成功" : order.orderStatus eq 3 ? "支付失败" : order.orderStatus eq 4 ? "已关闭" : order.orderStatus eq 5 ? "转入退款" : order.orderStatus eq 6 ? "已撤销（刷卡支付）" : order.orderStatus eq 9 ? "已转租" : order.orderStatus eq 99 ? "已完成" : "【未知】"}</span></td>
                <td>
                    <a class="c09f mr_15" href="javascript:chengfaCarparker('${order.id}');">惩罚車位主</a>
                    <c:if test="${order.orderStatus eq 2}">
                        <a class="c09f" href="javascript:window.location.href='${ctx}/order/matchNewGoods?phoneNo=${phoneNo}&orderId=${order.id}';">匹配新車位</a>
                    </c:if>
                    <c:if test="${order.orderStatus ne 2}">
                        <a class="c09f" href="#">　　　　　</a>
                    </c:if>
                </td>
            </tr>
        </c:forEach>
    </table>
</div>
<!--/Content-->

<jsp:include page="../comm/footer.jsp"/>