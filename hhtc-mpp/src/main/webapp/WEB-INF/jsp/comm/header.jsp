<%@ page pageEncoding="UTF-8"%>
<%@ page import="java.util.Date"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<c:set var="ctx" value="${pageContext.request.contextPath}" scope="session"/>
<c:set var="ctxImgViewUrl" value="${pageContext.request.contextPath}/wx/common/file/get?filePath=" scope="session"/>

<!DOCTYPE HTML>
<html>
<head>
    <title>吼吼停车管理平台</title>
    <meta charset="UTF-8">
    <link rel="icon" href="${ctx}/img/logo.ico" type="image/x-icon">
    <link rel="stylesheet" href="${ctx}/css/basic.css"/>
    <link rel="stylesheet" href="${ctx}/css/main.css"/>
    <script src="${ctx}/js/jquery-1.11.3.min.js"></script>
    <script src="${ctx}/js/pubs.js"></script>
    <script src="${ctx}/js/common.js"></script>
</head>
<body>
<div class="c_main_l">
    <div class="c_logo"><img height="35" src="${ctx}/img/logo.png"/></div>
    <%-- 物管 --%>
    <c:if test="${user.type eq 2}">
        <ul class="c_menu">
            <li ${currentMenu eq 'menu_sys' ? 'class="on"' : ''}>
                <a href="#"><span>业务管理</span><i></i></a>
                <div>
                    <a href="${ctx}/community/list"><span>小区管理</span></a>
                    <a href="${ctx}/goods/list"><span>车位列表</span></a>
                    <a href="${ctx}/goods/task/list"><span>车位审核</span></a>
                </div>
            </li>
            <li ${currentMenu eq 'menu_fans' ? 'class="on"' : ''}>
                <a href="#"><span>粉丝管理</span><i></i></a>
                <div>
                    <a href="${ctx}/fans/task/owner/list"><span>车主审核</span><i></i></a>
                    <a href="${ctx}/fans/task/park/list"><span>车位主审核</span><i></i></a>
                </div>
            </li>
        </ul>
    </c:if>
    <%-- 运营 --%>
    <c:if test="${user.type eq 1}">
        <ul class="c_menu">
            <li ${currentMenu eq 'menu_sys' ? 'class="on"' : ''}>
                <a href="#"><span>业务管理</span><i></i></a>
                <div>
                    <a href="${ctx}/goods/list"><span>车位列表</span></a>
                    <%--TODO Cantabile 车牌列表--%>
                    <a href="${ctx}/goods/list"><span>车牌列表</span></a>
                    <a href="${ctx}/community/list/mgr"><span>物管管理</span></a>
                    <a href="${ctx}/community/list"><span>小区管理</span></a>
                    <a href="${ctx}/community/device/list"><span>小区设备管理</span></a>
                    <a href="${ctx}/communitydeviceflow/list"><span>小区设备记录</span></a>
                </div>
            </li>
            <li ${currentMenu eq 'menu_fans' ? 'class="on"' : ''}>
                <a href="#"><span>粉丝管理</span><i></i></a>
                <div>
                    <a href="${ctx}/fans/task/park/list"><span>车位审核</span></a>
                    <a href="${ctx}/fans/task/community/list"><span>地址审核</span><i></i></a>
                    <a href="${ctx}/fans/task/owner/list"><span>车牌审核</span><i></i></a>
                    <a href="${ctx}/fans/list"><span>粉丝列表</span><i></i></a>
                    <a href="${ctx}/fans/advice/list"><span>意见反馈</span><i></i></a>
                    <%--TODO Cantabile 资金管理--%>
                    <a href="${ctx}/fans/money/manage"><span>资金管理</span><i></i></a>
                </div>
            </li>
            <li ${currentMenu eq 'menu_funds' ? 'class="on"' : ''}>
                <a href="#"><span>资金管理</span><i></i></a>
                <div>
                    <a href="${ctx}/refund/apply/task/list"><span>退款审核</span></a>
                    <a href="${ctx}/refund/apply/list"><span>退款列表</span></a>
                    <a href="${ctx}/funds/flow/listByPlatform"><span>平台收益</span></a>
                    <a href="${ctx}/view?url=funds/order.allocate"><span>订单调配</span></a>
                </div>
            </li>
            <li ${currentMenu eq 'menu_mpp' ? 'class="on"' : ''}>
                <a href="#"><span>微信设置</span><i></i></a>
                <div>
                    <a href="${ctx}/view?url=mpp/mpmenu"><span>公众号菜单</span></a>
                    <a href="${ctx}/mpp/user/info"><span>公众号资料</span></a>
                    <a href="${ctx}/mpp/reply/common/get"><span>通用的回复</span></a>
                    <a href="${ctx}/mpp/reply/follow/get"><span>关注后回复</span></a>
                    <a href="${ctx}/mpp/reply/keyword/list"><span>关键字回复</span></a>
                </div>
            </li>
        </ul>
    </c:if>
</div>
<div class="c_main_r">
    <div class="c_topBar">
        <p class="p1"><i></i><span class="fm2">您好：${user.username}，欢迎访问吼吼停车管理平台，现在是：<fmt:formatDate value="<%=new Date()%>" pattern="yyyy年MM月dd日 E"/></span></p>
        <p class="p2 fm2">
            <a href="${ctx}/view?url=password" class="mr_20"><i class="i_man"></i><span class="va_m">修改密码</span></a>
            <a href="${ctx}/mpp/user/logout"><i class="i_sw"></i><span class="va_m">退出</span></a>
        </p>
    </div>