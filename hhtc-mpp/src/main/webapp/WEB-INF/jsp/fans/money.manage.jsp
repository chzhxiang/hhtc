<%@ page pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<jsp:include page="../comm/header.jsp"/>

<script>

//    var orderId = document.getElementById("orderId");
//    var postPhoneNO = document.getElementById("postPhoneNO");
//    var ownersPhoneNO = document.getElementById("ownersPhoneNO");
//    var totalPrice = document.getElementById("totalPrice");
//    var orderStatus = document.getElementById("orderStatus");
//    var timeStart = document.getElementById("timeStart");
//    var timeEnd = document.getElementById("timeEnd");

function checkExe() {
    if(isEmpty($('#phoneNo').val()))
        $.promptBox("手机号不能为空", "#ffb848");
        return false;
    if ($('#moneyBaseOperation').val().indexOf("-")>0 ||
        $('#moneyBaseOperation').val().indexOf("+")>0 ||
        $('#moneyBalanceOperation').val().indexOf("-")>0 ||
        $('#moneyBalanceOperation').val().indexOf("+")>0) {
        $.promptBox("请严格输入参数", "#ffb848");
        return false;
    }
    if($('#moneyBalanceOperation').val().indexOf("-")>=0){
        $.promptBox("余额不能减少", "#ffb848");
        return false;
    }

    return true;
}

function execution(){
    if(checkExe()){
        return false;
    }else {
        if(confirm("确定执行此用户么？")){
            $.get("${ctx}/funds/flow/change",
                {phoneNO:$('#phoneNo').val(),
                    moneybase: isEmpty($('#moneyBaseOperation').val()) ? 0 : $('#moneyBaseOperation').val(),
                    moneybalance: isEmpty($('#moneyBalanceOperation').val()) ? 0 : $('#moneyBalanceOperation').val(),  },
                function(data){
                    if(0 == data.code){
                        $.promptBox("操作成功！", "#ffb848");
                        <%--location.href = "${ctx}/fans/task/owner/list";--%>
                    }else{
                        $.promptBox(data.msg, "#ffb848");
                    }
                });
        }
    }
    <%--window.location.href = "${ctx}/order/search?phoneNo=" + $('#phoneNo').val();--%>
}

function searchUser(){

    if(isEmpty($('#targetUser').val())){
        $.promptBox("目标用户不能为空", "#ffb848");
        return false;
    }else{
        $.get("${ctx}/fans/funds",
            {phoneNO:$('#targetUser').val()},
            function(data){
                if(0 == data.code){
                    $("#userNo").html(data.data.phoneNO);
                    $("#userNickName").html(data.data.nickname);
                    $("#userMoneyBase").html(data.data.moneybase);
                    $("#userMoneyBalance").html(data.data.moneybalance);
                }else{
                    $.promptBox(data.msg, "#ffb848");
                }
            });
    }
    <%--window.location.href = "${ctx}/order/search?phoneNo=" + $('#phoneNo').val();--%>
}

function searchOrder(){
    if(isEmpty($('#targetOrder').val())){
        $.promptBox("目标订单不能为空", "#ffb848");
        return false;
    }else{
        $.get("${ctx}/order/search/orderid",
            {orderid:$('#targetOrder').val()},
            function(data){
                if(0 == data.code){
                    $("#orderId").html(data.data.orderId);
                    $("#postPhoneNO").html(data.data.postPhoneNO);
                    $("#ownersPhoneNO").html(data.data.ownersPhoneNO);
                    $("#totalPrice").html(data.data.totalPrice);
                    $("#orderStatus").html(data.data.orderStatus == 0 ? "预约中" : data.data.orderStatus == 1 ? "进行中" : "已完成");
                    $("#timeStart").html(data.data.timeStart);
                    $("#timeEnd").html(data.data.timeEnd);
                }else{
                    $.promptBox(data.msg, "#ffb848");
                }
            });
    }
    <%--window.location.href = "${ctx}/order/search?phoneNo=" + $('#phoneNo').val();--%>
}
</script>

<div class="c_nav">
    <div class="ti">资金调配</div>
</div>
<!--Content-->
<div class="c_content">
    <!--Title-->
    <div class="title">
        <%--<form action="${ctx}/goods/test" method="post" name="exe">--%>
        <span class="sch2">
            <span class="sch">目标用户ID：</span>
            <input class="inpte fm2" type="number" name="phoneNo" id="phoneNo" maxlength="11" placeholder="请输入车主手机号"/>
            &nbsp
            <span class="sch">押金操作：</span>
            <input class="inpte fm2" type="number" name="moneyBase" id="moneyBaseOperation" maxlength="11" placeholder="押金操作"/>
            &nbsp
            <span class="sch">余额操作：</span>
            <input class="inpte fm2" type="number" name="moneyBalance" id="moneyBalanceOperation" maxlength="11" placeholder="余额操作"/>
            <button class="btn fm2" onclick="execution();">执行</button>
        </span>
        <%--</form>--%>
    </div>
    <!--/Title-->

    <!--Title-->
    <div class="title">
        <span class="sch2">
            <input class="inpte fm2" type="number" name="phoneNo" id="targetUser" maxlength="11" placeholder="目标用户手机号"/>
            <button class="btn fm2" onclick="searchUser();">用户搜索</button>
        </span>
    </div>
    <!--/Title-->

    <!--Table list-->
    <table class="tab_list" width="100%">
        <tr>
            <th>用户手机</th>
            <th>用户昵称</th>
            <th>用户当前押金</th>
            <th>用户当前余额</th>
        </tr>
        <%--<c:forEach items="${userInfo}" var="user">--%>
        <tr>
            <td><span id="userNo">&nbsp</span></td>
            <td><span id="userNickName">&nbsp</span></td>
            <td><span id="userMoneyBase">&nbsp</span></td>
            <td><span id="userMoneyBalance">&nbsp</span></td>
        </tr>
        <%--</c:forEach>--%>
    </table>

    <!--/Table list-->
    <br/>
    <!--Title-->
    <div class="title">
        <span class="sch2">
            <input class="inpte fm2" type="number" name="orderNo" id="targetOrder" maxlength="11" placeholder="目标订单"/>
            <button class="btn fm2" onclick="searchOrder();">订单搜索</button>
        </span>
    </div>
    <!--/Title-->
    <!--Table list-->
    <table class="tab_list" width="100%">
        <tr>
            <th>订单号</th>
            <th>用户类型</th>
            <th>用户手机</th>
            <th>订单金额</th>
            <th>订单状态</th>
            <th>订单开始时间</th>
            <th>订单结束时间</th>
        </tr>
        <%--<c:forEach items="${page.content}" var="community">--%>
        <tr>
            <td rowspan="2"><span id="orderId">1</span></td>
            <td><span >车位主</span></td>
            <td><span id="postPhoneNO">13509458511</span></td>
            <td rowspan="2"><span>Money</span></td>
            <td rowspan="2"><span id="orderStatus">进行中</span></td>
            <td rowspan="2"><span id="timeStart">2017-11-12 16：30</span></td>
            <td rowspan="2"><span id="timeEnd">2018-11-12 16：30</span></td>
        </tr>
        <tr>
            <td><span>车主</span></td>
            <td><span id="ownersPhoneNO">13509458511</span></td>
        </tr>
    </table>
    <!--/Table list-->
</div>
<!--/Content-->

<jsp:include page="../comm/footer.jsp"/>