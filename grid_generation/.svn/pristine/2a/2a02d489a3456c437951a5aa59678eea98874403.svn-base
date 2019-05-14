//测试环境
//var host="http://dev.iwhere.com:8010";
//正式下载
var host="http://localhost:8080";
function ajaxGet(url,cache,success,error) {
	$.ajax({
		url:url,
		type:'GET',
		cache:cache,
		dataType:"json",
		timeout: 100000,
		success:success,
		error:error,
		complete: function(XMLHttpRequest, textStatus) {
			setTimeout(function() {
				if(textStatus != 'success') {
					if(XMLHttpRequest.status == "404") {
						console.log("请检查网络");
						return;
					}
					if(textStatus == 'timeout') {
						console.log("请求超时");
						return;
					}
					if(textStatus == 'error') {
						console.log("请求出错");
						return;
					} else {
						console.log("请求异常"+textStatus);
						return;
					}
				}
			}, 500);

		}
	});
}

function ajaxPost(url,data,success,error) {
	$.ajax({
		url:url,
		type:'POST',
		data:data,
		dataType:"json",
		success:success,
		error:error,
		complete: function(XMLHttpRequest, textStatus) {
			console.log(XMLHttpRequest)
			console.log(textStatus)
			setTimeout(function() {
				if(textStatus != 'success') {
					if(XMLHttpRequest.status == "404") {
						console.log("请检查网络");
						return;
					}
					if(textStatus == 'timeout') {
						console.log("请求超时");
						return;
					}
					if(textStatus == 'error') {
						console.log("请求出错");
						return;
					} else {
						console.log("请求异常"+textStatus);
						return;
					}
				}
			}, 500);
		}
	});
}