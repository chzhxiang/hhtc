<%@ page pageEncoding="UTF-8"%>

<script type="text/javascript" src="${ctx}/js/jquery-1.11.3.min.js"></script>
<script>
$(function(){
	$("#img").attr("src", "${ctx}/wx/common/file/get?filePath=${param.path}");
});
</script>

<img id="img" width="1500" height="800">