<%@ page pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<jsp:include page="../comm/header.jsp"/>

<script src="${ctx}/js/My97DatePicker/WdatePicker.js"></script>

<script>
$(function(){
    $.get("${ctx}/community/listAll",
        function(data){
            if(0 == data.code){
                var options = "";
                for(var i=0; i<data.data.length; i++){
                    options += '<option value="' + data.data[i].id + '"';
                    if(data.data[i].id == '${communityId}'){
                        options += " selected=\"selected\"";
                    }
                    options += ">" + data.data[i].name + "</option>";
                }
                $("#community_select").html(options);
            }else{
                console.log(data.msg);
            }
        }
    );
});
function search(){
    if(isEmpty($('#from_date').val())){
        $.promptBox("请选择搜索的起始时间", "#ffb848");
        return false;
    }
    if(isEmpty($('#end_date').val())){
        $.promptBox("请选择搜索的截止时间", "#ffb848");
        return false;
    }
    if(parseInt($('#end_date').val()) < parseInt($('#from_date').val())){
        $.promptBox("选择的搜索截止时间不应早于起始时间", "#ffb848");
        return false;
    }
    var param = 'communityId=' + $('#community_select').val() + '&fromDate=' + $('#from_date').val() + '&endDate=' + $('#end_date').val()
    if(!isEmpty($('#car_number').val())){
        param = param + '&carNumber=' + $('#car_number').val();
    }
    window.location.href = "${ctx}/communitydeviceflow/list/search?" + param;
}
</script>

<div class="c_nav">
    <div class="ti">小区设备扫描流水</div>
</div>
<!--Content-->
<div class="c_content">
    <c:if test="${user.type eq 1}">
        <!--Title-->
        <div class="title txt_r">
            <span class="sch2" style="margin-right:50px;">
                <select class="sct" id="community_select"></select>
                &nbsp;
                <input class="inpte Wdate" style="width:85px" type="text" id="from_date" value="${fromDate}" maxlength="8" onFocus="WdatePicker({readOnly:true, dateFmt:'yyyyMMdd'})"/>
                &nbsp;&nbsp;-&nbsp;&nbsp;
                <input class="inpte Wdate" style="width:85px" type="text" id="end_date" value="${endDate}" maxlength="8" onFocus="WdatePicker({readOnly:true, dateFmt:'yyyyMMdd'})"/>
                &nbsp;
                <input class="inpte" type="text" id="car_number" value="${carNumber}" maxlength="8" placeholder="请输入车牌号"/>
                &nbsp;
                <button class="btn fm2" onclick="search();">搜索</button>
            </span>
        </div>
        <!--/Title-->
    </c:if>
    <!--Table list-->
    <table class="tab_list" width="100%">
        <tr>
            <th>小区</th>
            <th>车牌号</th>
            <th>识别时间</th>
            <th>识别结果</th>
            <th>开闸结果</th>
            <th>开闸时间</th>
            <th>开闸备注</th>
        </tr>
        <c:forEach items="${flowList}" var="flow">
            <tr>
                <td><span>${flow.communityName}</span></td>
                <td><span>${flow.scanCarNumber}</span></td>
                <td><span><fmt:formatDate value="${flow.scanTime}" pattern="yyyy-MM-dd HH:mm"/></span></td>
                <td><span>${flow.scanAllowOpen eq 1 ? "开闸" : "不开闸"}</span></td>
                <td><span>${flow.openResult eq 1 ? "开闸成功" : "开闸失败"}</span></td>
                <td><span><fmt:formatDate value="${flow.openTime}" pattern="yyyy-MM-dd HH:mm"/></span></td>
                <td><span>${flow.openRemark}</span></td>
            </tr>
        </c:forEach>
    </table>
    <!--/Table list-->
</div>
<!--/Content-->

<jsp:include page="../comm/footer.jsp"/>