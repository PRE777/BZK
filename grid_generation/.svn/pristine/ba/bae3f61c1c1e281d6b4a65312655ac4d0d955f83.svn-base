$(function(){
	selectMenu(0);
	function selectMenu(index){
	   $(".select-menu-input").eq(index).val($(".select-this").eq(index).html());//在输入框中自动填充第一个选项的值
	   $(".select-menu-div").eq(index).on("click",function(e){
		   	  e.stopPropagation();
             if($(".select-menu-ul").eq(index).css("display")==="block"){
				 $(".select-menu-ul").eq(index).hide();
				 $(".select-menu-div").eq(index).find("i").removeClass("select-menu-i");
				 $(".select-menu-ul").eq(index).animate({marginTop:"50px",opacity:"0"},"fast");
			 }else{
				 $(".select-menu-ul").eq(index).show();
				 $(".select-menu-div").eq(index).find("i").addClass("select-menu-i");
				 $(".select-menu-ul").eq(index).animate({marginTop:"5px",opacity:"1"},"fast");
			 }
			 for(var i=0;i<$(".select-menu-ul").length;i++){
			      if(i!==index&& $(".select-menu-ul").eq(i).css("display")==="block"){
				        $(".select-menu-ul").eq(i).hide();
						$(".select-menu-div").eq(i).find("i").removeClass("select-menu-i");
						$(".select-menu-ul").eq(i).animate({marginTop:"50px",opacity:"0"},"fast");
				  }
			 }
	   });
	   $(".select-menu-ul").eq(index).on("click","li",function(){//给下拉选项绑定点击事件
		   $(".select-menu-input").eq(index).val($(this).html());//把被点击的选项的值填入输入框中
		   $(".select-menu-input").eq(index).attr('data-datat',$(this).attr("data-dataType"))
           $(".select-menu-div").eq(index).click();
		   $(this).siblings(".select-this").removeClass("select-this");
		   
		   $(this).addClass("select-this");
			 var numtype = $(this).attr("data-dataType");
			 //表格类型
			 var pagesnums = 1;
			 $('.enc_coding').unbind('click');
			 tableDraw(numtype,pagesnums)
	   });
     $("body").on("click",function(event){
          event.stopPropagation();
          if($(".select-menu-ul").eq(index).css("display")==="block"){
			  $(".select-menu-ul").eq(index).hide();
				 $(".select-menu-div").eq(index).find("i").removeClass("select-menu-i");
				 $(".select-menu-ul").eq(index).animate({marginTop:"50px",opacity:"0"},"fast");
		 }
	 });
	}
	//table 表格
	function tableDraw(type,pagesnums){
		$('.layui-form').empty();
		if( pagesnums == undefined ){
			var pageNum = 1;
		}else{
			var pageNum = pagesnums;
		}
		var pageSize = 10;
		ajaxGet('/gridgeneration/data/getSourceData?dataType='+type+'&pageNum='+pageNum+'&pageSize='+pageSize,false,function(json){
			
			if( json.data.datas == undefined ){
				var datau = [];
			}else{
				var datau = json.data.datas;
			}
			var pageSize = 10;
			var title = json.data.gaugeOutfit;
			var totalnum = json.data.count;
			loadTableone(datau, totalnum, pageNum, pageSize,title,type)
		},function(error){
			if(error.status != 200){
				layer.msg('服务器异常,请稍后再试')
			}
		});
	}
	//表格数据:data 表格总共多少条数据:totalnum 当前页:pageNum 每页显示条数:pageSize
    function loadTableone(data, totalnum, pageNum, pageSize,titles,type) {
    	var dataRes;
        layui.use(['table', 'laypage'], function () {
            var table = layui.table;
            var laypage = layui.laypage;
            var arrs = [];
            table.render({
                data: data,
                elem: '#test',
                page: false,
                limit: pageSize,
                limits: [1, 2, 3],
                curr: pageNum,
                groups: 5,
                layout: ['prev', 'page', 'next', 'skip'],
                cols: [
                     titles
                ],
                done: function (res, page, count) {
                	//生成编码
                	dataRes = res.data;
					$('.enc_coding').on('click',function(){
						for( var x=0;x<dataRes.length;x++ ){
							if( dataRes[x].LAY_CHECKED == true  ){
								arrs.push(dataRes[x])
							}
						}
						if( arrs.length == 0 ){
							layer.msg('请选择编码信息')
							return ;
						}
						var datas = {
								datas:JSON.stringify(arrs)
						};
						ajaxPost('/gridgeneration/data/creatEncoded',datas,function(dataty){
							$.each(dataty.datas,function(y,items){
								$.each(dataRes,function(i,item){
									if( dataRes[i].geoResID == dataty.datas[y].geoResID ){
										dataRes[i]['internalNum'] = dataty.datas[y].internalNum;
									}
								})
							})
							var udatas = [{
								field: 'internalNum',
								title: '剖分编码',
								edit: 'text',
								align:'center'
							}];
							//删除第二个
							if( titles[2].title == "剖分编码" ){
								titles.splice(2,1)
								for(var u = 0;u<udatas.length;u++){
									titles.splice(2,0,udatas[u])
								}
								var c = titles.concat(udatas);
								table.reload('test',{
										data : dataRes,
										cols: [
										       titles
										],
								});
								layer.msg('生成编码成功')
							}else{
								for(var u = 0;u<udatas.length;u++){
									titles.splice(2,0,udatas[u])
								}
								var c = titles.concat(udatas);
								table.reload('test',{
										data : dataRes,
										cols: [
										       titles
										],
								});
								layer.msg('生成编码成功')
							}
						})
					})
                    laypage.render({
                        elem: 'pages',
                        count: totalnum, //数据总数，从服务端得到
                        limit: pageSize,
                        curr: pageNum,
                        limits: [1, 2, 3],
                        groups: 5,
                        hash: true,
                        layout: ['prev', 'page', 'next', 'skip'],
                        jump: function (obj, first) {
                            //首次不执行
                            if (!first) {
                                //重新加载表格
                            	var pagenums = obj.curr;
//                                var d = getTableData((obj.curr - 1), obj.limit,height,geonum);
                                tableDraw(type,pagenums)
                            }
                        }
                    });
                }
            });
        })
    }
})