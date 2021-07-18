$(function(){
	$("#publishBtn").click(publish);
});

function publish() {
	$("#publishModal").modal("hide");

	//获得帖子输入框的内容
	var title = $("#recipient-name").val();
	var content = $("#message-text").val();

	//向服务器发送数据
	$.post(
			CONTEXT_PATH + "/discuss/add",
			{"title":title,"content":content},
			function(data){
				//String格式转换为js对象
				data = $.parseJSON(data);
				//将返回信息输入到提示框中
				$("#hintBody").text(data.msg);
				//显示提示框
				$("#hintModal").modal("show");
				//两秒后关闭提示框，并刷新页面
				setTimeout(function(){
					$("#hintModal").modal("hide");
					//判断刷新页面是否成功
					if( data.code == 0){
						window.location.reload();
					}
				}, 2000);


			}
	)


}