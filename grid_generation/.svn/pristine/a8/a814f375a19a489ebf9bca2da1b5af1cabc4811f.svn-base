var wyoming;
var wyomings;
var poly;
var near_water_entity = viewer.entities.add(new Cesium.Entity());
var duo_draws_entity = viewer.entities.add(new Cesium.Entity());
var duo_draws_entity_two = viewer.entities.add(new Cesium.Entity());
var entity_sky = viewer.entities.add(new Cesium.Entity());
var draws_img = viewer.entities.add(new Cesium.Entity());
var bluePolygon;
//左右 滚轮 
var handler = new Cesium.ScreenSpaceEventHandler(viewer.scene.canvas);
/*
 * 清除单点选网格并清除事件
*/
function oneDraw(){
	sessionStorage.removeItem('geonum')
	handler.removeInputAction(Cesium.ScreenSpaceEventType.LEFT_CLICK);
	viewer.entities.remove(wyoming);
}
/*
 * 单点选网格
*/
function drawGridOne(lon_min, lat_min, lon_max, lat_max,height,geonum) {
	viewer.entities.remove(wyoming);
	wyoming = viewer.entities.add({
		rectangle: {
			coordinates: Cesium.Rectangle.fromDegrees(lon_min, lat_min, lon_max, lat_max),
			material: Cesium.Color.RED.withAlpha(0.5),
			outline: true,
		}
	});
	$('.bottom_table').empty();
	$('.gf_bottom_table').empty();
	sessionStorage.setItem('geonum',geonum)
	ceartHtml(height,geonum)
}
/*
 * 清除画多边形网格所有事件和网格
*/
function manyDraw(){
	sessionStorage.removeItem('geonum')
	DynamicDrawTool.startDrawingClear();
	$.each(duo_draws_entity._children,function(i,obj){          
	      viewer.entities.remove(obj);
	});
}
/*
 * 清除画多边形网格
*/
function clearGrid(draw){
	sessionStorage.removeItem('geonum')
	$.each(duo_draws_entity._children,function(i,obj){          
	      viewer.entities.remove(obj);
	});
}
/*
 * 画多边形网格
*/
function drawGridOnes(json) {
	var gesoum = '';
	for( var i=0;i<json.data.length;i++ ){
		viewer.entities.add({
			parent:duo_draws_entity,
			rectangle: {
				coordinates: Cesium.Rectangle.fromDegrees(json.data[i].minlng, json.data[i].minlat, json.data[i].maxlng, json.data[i].maxlat),
				material: Cesium.Color.RED.withAlpha(0.5),
				outline: true,
			}
		});
		gesoum += json.data[i].geonum+','
	}
	$('.bottom_table').empty();
	$('.gf_bottom_table').empty();
	var height = Math.ceil(viewer.camera.positionCartographic.height);
	sessionStorage.setItem('geonum',gesoum)
	ceartHtml(height,gesoum)
}
/*
 * 创建表格
*/
function ceartHtml(height,geonum,gl){
	$('.bzk li').unbind('click');
	if( gl != 'gl' ){
		$('.gf li').unbind('click')
	}
	
	var type = [];
	var titname = [];
	var gfType = [];
	var gfTitname = [];
		for(var g=0;g<xtree1.GetChecked().length;g++){
			for(var f=0;f<xtree1._dataJson[0].data.length;f++){
					if( xtree1._dataJson[0].data[f].value == xtree1.GetChecked()[g].title ){
						type.push(xtree1.GetChecked()[g].defaultValue);
						titname.push(xtree1.GetChecked()[g].title)
					}
			}
			for(var f=0;f<xtree1._dataJson[1].data.length;f++){
				if( xtree1._dataJson[1].data[f].value == xtree1.GetChecked()[g].title ){
					gfType.push(xtree1.GetChecked()[g].defaultValue);
					gfTitname.push(xtree1.GetChecked()[g].title)
				}
			}
		}
		
		if( type.length == 0){
			$('.bottom_table').hide();
			if(gl == 'gl'){
				layer.msg('没有勾选bzk数据哦！');
			}
		}else{
			$('.bottom_table').show();
			var json = titname;
			var jsono = type;
			var str = '<div class="data_list">'+
						'<span>bzk数据:</span>'+
						'<ul class="layui-tab-title bzk">';
						for(var i=0;i<json.length;i++){
							str += '<li data-type='+jsono[i]+'>'+json[i]+'</li>';
						}
				str +='</ul><img src="../src/img/tuichu.png" class="closetables" style="position: absolute;top: 12px;right: 14px;"/></div>';
			$('.bottom_table').append(str);
			$('.closetables').on('click',function(){
				$('.bottom_table').hide();
			})
			$('.bzk li').eq(0).addClass('color_blue_bg');
			$('.bzk li').on('click',function(){
				var index = $(this).index();
				$('.bzk li').eq(index).addClass('color_blue_bg').siblings().removeClass('color_blue_bg');
				$('.bzk-content .bzk-tab').eq(index).addClass('layui-show').siblings().removeClass('layui-show');
				var id = jsono[index];
				var pagesnums = 1;
				var type = jsono[index];
				tableDraw(height,geonum,pagesnums,id,type,6);
			});
			var stry = '<div class="layui-tab-content bzk-content" style="height: 100px;">';
			for(var i=0;i<json.length;i++){
					stry += '<div class="layui-tab-item bzk-tab">'+
					'<table class="layui-hide" id=demo'+jsono[i]+' ></table>'+
					'<div id=pages'+jsono[i]+' style="text-align:right;margin-right:1.3%;"></div>'+
					'</div>';
			}
			stry +='</div>';
			$('.bottom_table').append(stry);
			$('.bzk-content .bzk-tab').eq(0).addClass('layui-show');
			//调用表格
			var id = jsono[0];
			var pagesnums = 1;
			var type = id;
			tableDraw(height,geonum,pagesnums,id,type,6);
		};
		if( gl != 'gl' ){
			if( gfType.length == 0 ){
				$('.gf_bottom_table').hide();
				if( type.length == 0  ){
					layer.msg('没有勾选GF数据哦！');
				}
			}else{
				$('.gf_bottom_table').show();
				var gfjson = gfTitname;
				var gfjsono = gfType;
				var str = '<div class="data_list">'+
							'<span>gf数据:</span>'+
							'<ul class="layui-tab-title gf">';
							for(var i=0;i<gfjson.length;i++){
								str += '<li data-type='+gfjson[i]+'>'+gfjson[i]+'</li>';
							}
					str +='</ul><img src="../src/img/tuichu.png" class="closetable" style="position: absolute;top: 12px;right: 14px;"/></div>';
				$('.gf_bottom_table').append(str);
				$('.closetable').on('click',function(){
					$('.gf_bottom_table').hide();
				})
				$('.gf li').eq(0).addClass('color_blue_bg');
				$('.gf li').on('click',function(){
					var index = $(this).index();
					$('.gf li').eq(index).addClass('color_blue_bg').siblings().removeClass('color_blue_bg');
					$('.gf_content .gf_table').eq(index).addClass('layui-show').siblings().removeClass('layui-show');
					var id = gfjsono[index]+'gf';
					var pagesnums = 1;
					var type = gfjsono[index];
					tableDraw(height,geonum,pagesnums,id,type,7);
				});
				var stry = '<div class="layui-tab-content gf_content" style="height: 100px;">';
				for(var i=0;i<gfjson.length;i++){
						stry += '<div class="layui-tab-item gf_table">'+
						'<table class="layui-hide" id=demo'+gfjsono[i]+'gf  lay-filter="text"></table>'+
						'<div id=pages'+gfjsono[i]+'gf style="text-align:right;margin-right:1.3%;"></div>'+
						'</div>';
				}
				stry +='</div>';
				$('.gf_bottom_table').append(stry);
				$('.gf_content .gf_table').eq(0).addClass('layui-show');
				//调用表格
				var id = gfjsono[0]+'gf';
				var pagesnums = 1;
				var type = gfjsono[0];
				tableDraw(height,geonum,pagesnums,id,type,7);
			}
		}
		
		
	
}
/*
 * 动态监听单点画网格
*/
function getPosition() {
	//得到当前三维场景
	var scene = viewer.scene;
	//得到当前三维场景的椭球体
	var ellipsoid = scene.globe.ellipsoid;
	var entity = viewer.entities.add({
			label : {
				show : false
			}
	});
	var longitudeString = null;
	var latitudeString = null;
	var height = null;
	var cartesian = null;
	// 定义当前场景的画布元素的事件处理
	//设置鼠标点击事件的处理函数，这里负责监听x,y坐标值变化
	handler.setInputAction(function(movement) {
		if( xtree1.GetChecked().length == 0 ){
			layer.msg('请勾选要查找的资源');
			return;
		}
			//通过指定的椭球或者地图对应的坐标系，将鼠标的二维坐标转换为对应椭球体三维坐标
			cartesian = viewer.camera.pickEllipsoid(movement.position, ellipsoid);
			if (cartesian) {
					//将笛卡尔坐标转换为地理坐标
					var cartographic = ellipsoid.cartesianToCartographic(cartesian);
					//将弧度转为度的十进制度表示
					longitudeString = Cesium.Math.toDegrees(cartographic.longitude);
					latitudeString = Cesium.Math.toDegrees(cartographic.latitude);
					//获取相机高度
					height = Math.ceil(viewer.camera.positionCartographic.height);
					ajaxGet('/gridgeneration/data/getGeoNumsOfPoint?lat='+latitudeString+'&lng='+longitudeString+'&scale='+height+'',false,function(json){
						drawGridOne(json.data.minlng, json.data.minlat, json.data.maxlng, json.data.maxlat,height,json.data.geonum)
					});
			}else {
					entity.label.show = false;
			}
	}, Cesium.ScreenSpaceEventType.LEFT_CLICK);
}
/*
 * 网格表格
*/
function tableDraw(height,geonum,pagesnums,id,type,typenum){
	if( pagesnums == undefined ){
		var pageNum = 1;
	}else{
		var pageNum = pagesnums;
	}
	if( typenum == 6 ){
		var pageSize = 1;
		var postdatas = {
				"dataType":type,
				"height":height,
				"geonums":geonum,
				"pageNum":pageNum,
				"pageSize":pageSize
		}
		ajaxPost('/gridgeneration/data/getInformationHighlyActive',postdatas,function(json){
			$('.bottom_table').show();
			if( json.data.datas == undefined ){
				var datau = [];
			}else{
				var datau = json.data.datas;
			}
			var title = json.data.gaugeOutfit;
			var totalnum = json.data.count;
			loadTableone(datau, totalnum, pageNum, pageSize,title,height,geonum,id,type)
		});
	}else{
		var pageSize = 1;
		var postdata = {
				"dataType":type,
				"height":height,
				"geonums":geonum,
				"pageNum":pageNum,
				"pageSize":pageSize
		}
		ajaxPost('/gridgeneration/data/getInformationYggf',postdata,function(json){
			$('.gf_bottom_table').show();
			if( json.data.datas == undefined ){
				var datau = [];
			}else{
				var datau = json.data.datas;
			}
			var title = json.data.gaugeOutfit;
			var totalnum = json.data.count;
			loadTableones(datau, totalnum, pageNum, pageSize,title,height,geonum,id,type)
		});
	}
	
}
//删除
$('.colose_img').on('click',function(){
	$('.bottom_table').hide();
})
//表格数据:data 表格总共多少条数据:totalnum 当前页:pageNum 每页显示条数:pageSize
function loadTableones(data, totalnum, pageNum, pageSize,title,height,geonum,id,type,comdata) {
    layui.use(['table', 'laypage'], function () {
        var table = layui.table;
        var laypage = layui.laypage;
        table.render({
        	title: '用户数据表',
            data: data,
            elem: '#demo'+id,
            page: false,
            cellMinWidth: 180,
            limit: pageSize,
            limits: [1, 2, 3],
            curr: pageNum,
            groups: 5,
            layout: ['prev', 'page', 'next', 'skip'],
            cols: [
                 title
            ],
            done: function (res, page, count) {
                laypage.render({
                    elem: 'pages'+id,
                    count: totalnum,
                    limit: pageSize,
                    curr: pageNum,
                    cellMinWidth: 80,
                    limits: [1, 2, 3],
                    groups: 3,
                    hash: true,
                    layout: ['prev', 'page', 'next', 'skip'],
                    jump: function (obj, first) {
                        //首次不执行
                        if (!first) {
                            //重新加载表格
                        	var pagenums = obj.curr;
                            tableDraw(height,geonum,pagenums,id,type,7)
                        }
                    }
                });
            }
        });
        //监听行工具事件
        table.on('tool(text)', function(obj){ //注：tool 是工具条事件名，test 是 table 原始容器的属性 lay-filter="对应的值"
          var datas = obj.data //获得当前行数据  
          ,layEvent = obj.event; //获得 lay-event 对应的值
          if(layEvent === 'detail'){
            $('.bottom_table').empty();
            ceartHtml(height,data[0].geoNums.join(","),'gl')
          } else if(layEvent === 'edit'){
        	  layer.msg('上图');
        	  //每一次上图都删除上一次的图 调用
        	  sImg(datas.BottomLeft,datas.BottomRight,datas.TopLeft,datas.TopRigh,datas.GFUrl)
          }
        });
    })
}
/*加载矩形图片*/
function sImg(BottomLeft,BottomRight,TopLeft,TopRigh,url){
	if( url == undefined || url == null || BottomLeft == undefined){
		return;
	}
	$.each(draws_img._children,function(i,obj){          
	      viewer.entities.remove(obj);
	});
	var bluePolygon =viewer.entities.add({
		parent:draws_img,
		polygon : {
		    hierarchy : Cesium.Cartesian3.fromDegreesArray([
		                                                	parseFloat(BottomRight.split(',')[0]),
		                                                	parseFloat(BottomRight.split(',')[1]),
		                                                	parseFloat(TopRigh.split(',')[0]),
		                                                	parseFloat(TopRigh.split(',')[1]),
		                                                	parseFloat(TopLeft.split(',')[0]),
		                                                	parseFloat(TopLeft.split(',')[1]),
		                                                	parseFloat(BottomLeft.split(',')[0]),
		                                                	parseFloat(BottomLeft.split(',')[1])
		                                                	]),
		    material : '../src/'+url, //材质
    }});
	viewer.zoomTo(bluePolygon);
}
//表格数据:data 表格总共多少条数据:totalnum 当前页:pageNum 每页显示条数:pageSize
function loadTableone(data, totalnum, pageNum, pageSize,title,height,geonum,id,type,comdata) {
	layui.use(['table', 'laypage'], function () {
        var table = layui.table;
        var laypage = layui.laypage;
        table.render({
        	title: '用户数据表',
            data: data,
            elem: '#demo'+id,
            page: false,
            cellMinWidth: 180,
            limit: pageSize,
            limits: [1, 2, 3],
            curr: pageNum,
            groups: 5,
            layout: ['prev', 'page', 'next', 'skip'],
            cols: [
                 title
            ],
            done: function (res, page, count) {
                laypage.render({
                    elem: 'pages'+id,
                    count: totalnum,
                    limit: pageSize,
                    curr: pageNum,
                    cellMinWidth: 80,
                    limits: [1, 2, 3],
                    groups: 3,
                    hash: true,
                    layout: ['prev', 'page', 'next', 'skip'],
                    jump: function (obj, first) {
                        //首次不执行
                        if (!first) {
                            //重新加载表格
                        	var pagenums = obj.curr;
                            tableDraw(height,geonum,pagenums,id,type,6)
                        }
                    }
                });
            }
        });
    })
}
//dian
function drawPoint() {
    DynamicDrawTool.startDrawingMarker(viewer, "点击添加项目中心点", function (cartesian) {
        //下面对处理代码
        //....
        var cartographic = Cesium.Cartographic.fromCartesian(cartesian);
        var lonlat = [Cesium.Math.toDegrees(cartographic.longitude), Cesium.Math.toDegrees(cartographic.latitude)];
    });
}
//xian
function drawLine() {
    var lineOption = {
        width: 5,
        geodesic: true
    };
    DynamicDrawTool.startDrawingPolyshape(viewer, false, lineOption, function (cartesians) {
        //下面对处理代码
        //....
    });
}
//mian
function drawPolygon(draw) {
    var gonOption = {
        width: 5,
        geodesic: true
    };
    DynamicDrawTool.startDrawingPolyshape(viewer, true, gonOption,draw, function (cartesians) {
		var wgs84_positions = [];
		for(var i = 0; i<cartesians.length; i++){
            var wgs84_point = Cartesian3_to_WGS84(cartesians[i]);
            wgs84_positions.push(wgs84_point);
        }
		var height_cearm = Math.ceil(viewer.camera.positionCartographic.height);
		var data = {
			points:JSON.stringify(wgs84_positions),
			scale:height_cearm
		}
		ajaxPost('/gridgeneration/data/getGeoNumsByPolygon',data,function(json){
			drawGridOnes(json)
		})
    });
}
//笛卡尔坐标系转WGS84坐标系
function Cartesian3_to_WGS84(point) {
    var cartesian33 = new Cesium.Cartesian3(point.x, point.y, point.z);
    var cartographic = Cesium.Cartographic.fromCartesian(cartesian33);
    var lat = Cesium.Math.toDegrees(cartographic.latitude);
    var lng = Cesium.Math.toDegrees(cartographic.longitude);
    var alt = cartographic.height;
    return {lat: lat, lng: lng, alt: alt};
};
//WGS84坐标系转笛卡尔坐标系
function WGS84_to_Cartesian3(point) {
    var car33 = Cesium.Cartesian3.fromDegrees(point.lng, point.lat, point.alt);
    var x = car33.x;
    var y = car33.y;
    var z = car33.z;
    return {x: x, y: y, z: z};
}
/*
 * 多 单点网格 
 * 绘制表格
*/
function drawGridOnesTwo(json) {
	var gesoum = '';
	for( var i=0;i<json.length;i++ ){
		gesoum += json[i]+','
	}
	$('.bottom_table').empty();
	$('.gf_bottom_table').empty();
	var height = Math.ceil(viewer.camera.positionCartographic.height);
	ceartHtml(height,gesoum)
	sessionStorage.setItem('geonum',gesoum)
}
/*
 *多网格 单点画
*/
function getPos(draw) {
    DynamicDrawTool.startDrawOne(viewer, true,draw, function (cartesians) {
		drawGridOnesTwo(cartesians)
    });
}
/*
 *多网格 单点选网格
*/
function drawGridOneTwo(lon_min, lat_min, lon_max, lat_max,height,geonum) {
	viewer.entities.add({
		parent:duo_draws_entity_two,
		rectangle: {
			coordinates: Cesium.Rectangle.fromDegrees(lon_min, lat_min, lon_max, lat_max),
			material: Cesium.Color.RED.withAlpha(0.5),
			outline: true,
		}
	});
}
/*
 *多 --- 单 选  清除网格
*/
function clearGridTwo(){
	sessionStorage.removeItem('geonum')
	$.each(duo_draws_entity_two._children,function(i,obj){          
	      viewer.entities.remove(obj);
	});
}
function clearGridTwoEven(){
	sessionStorage.removeItem('geonum')
	DynamicDrawTool.startDrawingClearTwo();
	$.each(duo_draws_entity_two._children,function(i,obj){          
	      viewer.entities.remove(obj);
	});
}


/**
 * 删除管理网格的矢量数据
 */
function removeImageryLayers(){
	clearInterval(interval);
    viewer.dataSources.removeAll();
    handler.removeInputAction(Cesium.ScreenSpaceEventType.LEFT_CLICK);
    $.each(duo_draws_entity._children,function(i,obj){          
	      viewer.entities.remove(obj);
	});
	$.each(entity_sky._children,function(i,obj){   
	      viewer.entities.remove(obj);
	});
}
/**
 * 加载管理网格数据
 * 本地json格式
 */
function managementGrid(){
    var promise = Cesium.GeoJsonDataSource.load('../src/work/china.geojson'); 
    //显示管线数据  直接加载json数据 比把json转化成czml在加载 快很多
    promise.then(function (dataSource) {
        viewer.dataSources.add(dataSource);
        var entities = dataSource.entities.values;
        var colorHash = {};
        var color = new Cesium.Color(1,1,0,99/255.0);
        var outlineColor = new Cesium.Color(220/255.0,20/255.0,60/255.0,255/255.0);
        for (var o = 0; o < entities.length; o++) {
            var entity = entities[o];
            // entity.cid = entities[o]._properties._XZQDM._value
            entity.polygon.material = color;
            entity.polygon.outline = true;
            entity.polygon.outlineColor = outlineColor;
            //entity.polygon.outlineWidth = 10;
            //entity.polygon.height = 900000;
            entity.polygon.extrudedHeight = 0;
        }
    });
    viewer.flyTo(promise);
    var colors = new Cesium.Color(123/255.0,104/255.0,139/255.0,238/255.0);
    var colorvs = new Cesium.Color(1,1,0,99/255.0);
    handler.setInputAction(function(movement) {
    	$.each(duo_draws_entity._children,function(i,obj){          
	  	      viewer.entities.remove(obj);
	  	});
    	$.each(entity_sky._children,function(i,obj){
	  	      viewer.entities.remove(obj);
	  	});
        //var pickedFeature = viewer.scene.pick(movement.position);
        var pickedObjects = viewer.scene.drillPick(movement.position,2);
        if( pickedObjects.length == 0){
            return
        }else{
        	var tvalue = pickedObjects[0].id._polygon.hierarchy._value;
            //调用数据列表接口
            /**
             * 声明：修改为双击事件
             */
            var lngt = pickedObjects[0].id._polygon.hierarchy._value.positions;
            var posarrs = [];
            for(var i=0;i<lngt.length;i++){
                posarrs.push(Cartesian3_to_WGS84(lngt[i]))
            }
            var arr = []
            for (var i in posarrs) {
                arr.push(posarrs[i]); //属性
            }
            var height_cearm = Math.ceil(viewer.camera.positionCartographic.height);
			var data = {
				points:JSON.stringify(arr),
				scale:height_cearm
			}
			ajaxPost('/gridgeneration/data/getGeoNumsByPolygon',data,function(json){
				console.log(json.data)
				drawGridOnes(json)
			})
            poly(arr)
        }
    }, Cesium.ScreenSpaceEventType.LEFT_CLICK);
}

var interval;
function clearTime(num){
	clearInterval(interval)
	if(num == 1){
		interval = setInterval(function () {
			bluePolygon.show = !bluePolygon.show;
		}, 2000);
	}else{
//		clearInterval(interval);
	}
}
function poly(arrs){
	var arr = [];
	for( var i=0;i<arrs.length;i++ ){
		arr.push(arrs[i].lng)
		arr.push(arrs[i].lat)
	}
	bluePolygon = viewer.entities.add({
		parent:entity_sky,
		polygon : {
		    hierarchy : Cesium.Cartesian3.fromDegreesArray(arr),
		    extrudedHeight: 0,
	        perPositionHeight : true,
	        material : Cesium.Color.ORANGE,
	        outline : true,
	        fill : false,  //不显示填充
	        outlineColor : Cesium.Color.BLACK
		}
	});
	//加载定时器
	clearTime(1)
}






