var drawLon;
var drawLat;
var wyoming;
var wyomings;
var poly;
var near_water_entity = viewer.entities.add(new Cesium.Entity());
var duo_draws_entity = viewer.entities.add(new Cesium.Entity());
var duo_draws_entity_two = viewer.entities.add(new Cesium.Entity());
//左右 滚轮 
var handler = new Cesium.ScreenSpaceEventHandler(viewer.scene.canvas);
/*
 * 2d or 3d事件
*/
function Scene(cnum) {
	console.log(viewer.scene.mode)
	switch (viewer.scene.mode) {
		case 2:
			viewer.scene.mode = 3
			break;
		case 3:
			viewer.scene.mode = 2
			break;
	}
}
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
	sessionStorage.setItem('geonum',geonum)
	ceartHtml(height,geonum,1)
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
	console.log(json)
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
	var height = Math.ceil(viewer.camera.positionCartographic.height);
	console.log(gesoum)
	sessionStorage.setItem('geonum',gesoum)
	ceartHtml(height,gesoum,1)
}
/*
 * 创建表格
*/
function ceartHtml(height,geonum,voe,comdata){
	$('.bottom_table').show();
	var type = [];
	var titname = [];
	for(var g=0;g<xtree1.GetChecked().length;g++){
		type.push(xtree1.GetChecked()[g].defaultValue);
		titname.push(xtree1.GetChecked()[g].title)
	}
	var json = titname;
	var jsono = type;
	var str = '<ul class="layui-tab-title">';
				for(var i=0;i<json.length;i++){
					str += '<li data-type='+jsono[i]+'>'+json[i]+'</li>';
				}
		str +='</ul>';
	$('.bottom_table').append(str);
	$('.layui-tab-title li').eq(0).addClass('layui-this');
	$('.layui-tab-title li').on('click',function(){
		var index = $(this).index();
		$('.layui-tab-title li').eq(index).addClass('layui-this').siblings().removeClass('layui-this');
		$('.layui-tab-content .layui-tab-item').eq(index).addClass('layui-show').siblings().removeClass('layui-show');
		var id = jsono[index];
		var pagesnums = 1;
		var type = jsono[index];
		tableDraw(height,geonum,pagesnums,id,type,voe,comdata);
	});

	var stry = '<div class="layui-tab-content" style="height: 100px;">';
	for(var i=0;i<json.length;i++){
			stry += '<div class="layui-tab-item">'+
			'<table class="layui-hide" id=demo'+jsono[i]+' ></table>'+
			'<div id=pages'+jsono[i]+' style="text-align:right;margin-right:2.4%;margin-top: 7px;"></div>'+
			'</div>';
	}
	stry +='</div>';
	$('.bottom_table').append(stry);
	$('.layui-tab-content .layui-tab-item').eq(0).addClass('layui-show');
	//调用表格
	var id = jsono[0];
	var pagesnums = 1;
	var type = '001';
	tableDraw(height,geonum,pagesnums,id,type,voe,comdata);
}
/*
 *cesium画网格
 *画经纬度线
 *跨半球处理 
*/
function drawGrid(xmax,ymax,xmin,ymin,demoHeight,car3_rb,car3_lt) {
	//去除网格	
	$.each(near_water_entity._children,function(i,obj){          
	        viewer.entities.remove(obj);
	});
	if( car3_rb != undefined && car3_lt != undefined){
		var curl = '/gridgeneration/data/drawGridOnMap?scale='+demoHeight+'&lbLng='+xmin+'&lbLat='+ymin+'&rtLng='+xmax+'&rtLat='+ymax+'';
	}else{
		var curl = '/gridgeneration/data/drawGridOnMap?scale='+demoHeight+'&lbLng=-179&lbLat='+ymin+'&rtLng=179&rtLat='+ymax+'';
	}
	ajaxGet(curl,false,function(json){
			var lons = json.lons;
			var lngs = json.lats;
			var posarr = [];
			lons.forEach(function(item, index) {
				drawLat = viewer.entities.add({
					parent:near_water_entity,
					polyline: {
						positions: Cesium.Cartesian3.fromDegreesArray([item, ymax, item, ymin]),
						material: Cesium.Color.RED.withAlpha(0.4),
						outline: true,
						outlineColor: Cesium.Color.RED.withAlpha(0.4)
					}
				})
			});
			if( xmax > xmin && car3_rb != undefined){
				var lonlist = [xmax, xmin];
				var nums = 25;
				for( var xx=0;xx<lngs.length;xx++ ){
					for (var i = 0; i < nums; i++) {
						posarr.push(Cesium.Cartesian3.fromDegreesArray([lonlist[0] * (nums - i) / nums + lonlist[lonlist.length - 1] * (i) /
						                                                nums, lngs[xx],
						                                                lonlist[0] * (nums - 1 - i) / nums + lonlist[lonlist.length - 1] * (i + 1) / nums, lngs[xx]
										            					]))
					}
				}
				for( var i=0;i<posarr.length;i++ ){
					drawLon = viewer.entities.add({
						parent:near_water_entity,
						polyline: {
							positions:posarr[i],
							material: Cesium.Color.RED.withAlpha(0.4),
							outline: true,
							outlineColor: Cesium.Color.RED.withAlpha(0.4)
						}
					})
				}
			}else{
				var nums = 25;
				var lonlist = [-179.99, xmax];
				for( var xx=0;xx<lngs.length;xx++ ){
					for (var i = 0; i < nums; i++) {
						posarr.push(Cesium.Cartesian3.fromDegreesArray([lonlist[0] * (nums - i) / nums + lonlist[lonlist.length - 1] * (i) /
						                                                nums, lngs[xx],
										            						lonlist[0] * (nums - 1 - i) / nums + lonlist[lonlist.length - 1] * (i + 1) / nums, lngs[xx]
										            					]))
					}
				}
				for( var i=0;i<posarr.length;i++ ){
					drawLon = viewer.entities.add({
						parent:near_water_entity,
						polyline: {
							positions:posarr[i],
							material: Cesium.Color.RED.withAlpha(0.4),
							outline: true,
							outlineColor: Cesium.Color.RED.withAlpha(0.4)
						}
					})
				}
				var lonlistr = [179.99, xmax];
				var posarrr =  [];
				for( var xx=0;xx<lngs.length;xx++ ){
					for (var i = 0; i < nums; i++) {
						posarrr.push(Cesium.Cartesian3.fromDegreesArray([lonlistr[0] * (nums - i) / nums + lonlistr[lonlistr.length - 1] * (i) /
						                                                nums, lngs[xx],
						                                                lonlistr[0] * (nums - 1 - i) / nums + lonlistr[lonlistr.length - 1] * (i + 1) / nums, lngs[xx]
										            					]))
					}
				}
				for( var i=0;i<posarrr.length;i++ ){
					viewer.entities.add({
						parent:near_water_entity,
						polyline: {
							positions:posarrr[i],
							material: Cesium.Color.RED.withAlpha(0.4),
							outline: true,
							outlineColor: Cesium.Color.RED.withAlpha(0.4)
						}
					})
				}
			}	
	})
}
/*
 * 监听屏幕范围
*/
function listter(){
	viewer.scene.camera.moveEnd.addEventListener(CesiumViewTool)
}
/*
 * 不显示网格
*/
function NoDrawGrid() {
	viewer.scene.camera.moveEnd.removeEventListener(CesiumViewTool)
	$.each(near_water_entity._children,function(i,obj){          
	        viewer.entities.remove(obj);
	});
}
/*
 * cesium监听屏幕范围
 * 屏幕坐标转世界坐标
 * 世界坐标转经纬度坐标
*/
function CesiumViewTool() {
	var extent = {};
	var scene = viewer.scene;
	var ellipsoid = scene.globe.ellipsoid;
	var canvas = scene.canvas;
	var demoHeight = viewer.camera.getMagnitude();
	var car3_lt = viewer.camera.pickEllipsoid(new Cesium.Cartesian2(0, 0), ellipsoid);// canvas左上角
	var car3_rb = viewer.camera.pickEllipsoid(new Cesium.Cartesian2(canvas.width, canvas.height), ellipsoid); // canvas右下角
	if (car3_lt && car3_rb) {
		var carto_lt = ellipsoid.cartesianToCartographic(car3_lt);
		var carto_rb = ellipsoid.cartesianToCartographic(car3_rb);
		extent.xmin = Cesium.Math.toDegrees(carto_lt.longitude);
		extent.ymax = Cesium.Math.toDegrees(carto_lt.latitude);
		extent.xmax = Cesium.Math.toDegrees(carto_rb.longitude);
		extent.ymin = Cesium.Math.toDegrees(carto_rb.latitude);
	} else if (!car3_lt && car3_rb) {
		var car3_lt2 = null;
		var yIndex = 0;
		var xIndex = 0;
		do {
			yIndex <= canvas.height ? yIndex += 10 : canvas.height;
			xIndex <= canvas.width ? xIndex += 10 : canvas.width;
			car3_lt2 = viewer.camera.pickEllipsoid(new Cesium.Cartesian2(xIndex, yIndex), ellipsoid);
		} while (!car3_lt2);
		var carto_lt2 = ellipsoid.cartesianToCartographic(car3_lt2);
		var carto_rb2 = ellipsoid.cartesianToCartographic(car3_rb);
		extent.xmin = Cesium.Math.toDegrees(carto_lt2.longitude);
		extent.ymax = Cesium.Math.toDegrees(carto_lt2.latitude);
		extent.xmax = Cesium.Math.toDegrees(carto_rb2.longitude);
		extent.ymin = Cesium.Math.toDegrees(carto_rb2.latitude);
	}
	else if (car3_lt && !car3_rb) {
		var car3_rb2 = null;
		var yIndex = canvas.height;
		var xIndex = canvas.width;
		do {
			yIndex >= 10 ? yIndex -= 10 : 10;
			xIndex >= 10 ? xIndex -= 10 : 10;
			car3_rb2 = viewer.camera.pickEllipsoid(new Cesium.Cartesian2(yIndex, yIndex), ellipsoid);
		} while (!car3_rb2);
		var carto_lt2 = ellipsoid.cartesianToCartographic(car3_lt);
		var carto_rb2 = ellipsoid.cartesianToCartographic(car3_rb2);
		extent.xmin = Cesium.Math.toDegrees(carto_lt2.longitude);
		extent.ymax = Cesium.Math.toDegrees(carto_lt2.latitude);
		extent.xmax = Cesium.Math.toDegrees(carto_rb2.longitude);
		extent.ymin = Cesium.Math.toDegrees(carto_rb2.latitude);
	} else if (!car3_lt && !car3_rb) {
		var car3_lt2 = null;
		var yIndex = 0;
		var xIndex = 0;
		do {
			yIndex <= canvas.height ? yIndex += 10 : canvas.height;
			xIndex <= canvas.width ? xIndex += 10 : canvas.width;
			car3_lt2 = viewer.camera.pickEllipsoid(new Cesium.Cartesian2(xIndex, yIndex), ellipsoid);
		} while (!car3_lt2);
		var car3_rb2 = null;
		var yIndex = canvas.height;
		var xIndex = canvas.width;
		do {
			yIndex >= 10 ? yIndex -= 10 : 10;
			xIndex >= 10 ? xIndex -= 10 : 10;
			car3_rb2 = viewer.camera.pickEllipsoid(new Cesium.Cartesian2(yIndex, yIndex), ellipsoid);
		} while (!car3_rb2);
		var carto_lt2 = ellipsoid.cartesianToCartographic(car3_lt2);
		var carto_rb2 = ellipsoid.cartesianToCartographic(car3_rb2);
		extent.xmin = Cesium.Math.toDegrees(carto_lt2.longitude);
		extent.ymax = Cesium.Math.toDegrees(carto_lt2.latitude);
		extent.xmax = Cesium.Math.toDegrees(carto_rb2.longitude);
		extent.ymin = Cesium.Math.toDegrees(carto_rb2.latitude);
	}
	extent.height = Math.ceil(viewer.camera.positionCartographic.height);
	drawGrid(extent.xmax,extent.ymax,extent.xmin,extent.ymin,extent.height,car3_rb,car3_lt)
};
/*
 * 动态监听单点画网格
*/
function noGetPosition() {
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
		console.log(movement.position)
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
function tableDraw(height,geonum,pagesnums,id,type,voe,comdata){
	$('.bottom_table').show();
	if( pagesnums == undefined ){
		var pageNum = 1;
	}else{
		var pageNum = pagesnums;
	}
	//区分组合查询  or 网格查询    ceart表格
	if( voe == 1 ){
		ajaxGet('/gridgeneration/data/getInformationHighlyActive?dataType='+type+'&height='+height+'&geonums='+geonum+'&pageNum='+pageNum,false,function(json){
			console.log(json.data.gaugeOutfit);
			console.log(json.data.datas);
			if( json.data.datas == undefined ){
				var datau = [];
			}else{
				var datau = json.data.datas;
			}
			var pageSize = 5;
			var title = json.data.gaugeOutfit;
			var totalnum = json.data.count;
			loadTableone(datau, totalnum, pageNum, pageSize,title,height,geonum,id,type)
		});
	}else{
		console.log('还没区分');
		comdata['height'] = height;
		comdata['pageNum'] = pageNum;
		comdata['pageSize'] = 5;
		comdata['geonum'] = geonum;
		comdata['dataType'] = type;
//		console.log(comdata);
//		var data = {
//			scale:height_cearm,
//			dataType:type,
//			geonums:geonum,
//			startProductDate:startProductDate,
//			endProductDate:endProductDate,
//			cellSize:cellSize,
//			scale:scale,
//			height:height,
//			geoResName:geoResName,
//			pageNum:pageNum,
//			pageSize:pageSize
//		}
		ajaxPost('/gridgeneration/data/getInformationHighlyActive',comdata,function(json){
			console.log(json.data.gaugeOutfit);
			console.log(json.data.datas);
			if( json.data.datas == undefined ){
				var datau = [];
			}else{
				var datau = json.data.datas;
			}
			var pageSize = 5;
			var title = json.data.gaugeOutfit;
			var totalnum = json.data.count;
			loadTableone(datau, totalnum, pageNum, pageSize,title,height,geonum,id,type,comdata)
		})
	}
}

$('.colose_img').on('click',function(){
	$('.bottom_table').hide();
})
//表格数据:data 表格总共多少条数据:totalnum 当前页:pageNum 每页显示条数:pageSize
function loadTableone(data, totalnum, pageNum, pageSize,title,height,geonum,id,type,comdata) {
    layui.use(['table', 'laypage'], function () {
        var table = layui.table;
        var laypage = layui.laypage;
        table.render({
            data: data,
            elem: '#demo'+id,
            page: false,
            height:180,
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
                    limits: [1, 2, 3],
                    groups: 5,
                    hash: true,
                    layout: ['prev', 'page', 'next', 'skip'],
                    jump: function (obj, first) {
                        //首次不执行
                        if (!first) {
                            //重新加载表格
                        	var pagenums = obj.curr;
                            tableDraw(height,geonum,pagenums,id,type,voe,comdata)
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
        alert(lonlat);
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
			console.log(json.data)
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
	var height = Math.ceil(viewer.camera.positionCartographic.height);
	ceartHtml(height,gesoum,1)
	console.log(gesoum)
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

/*
 * 组合查询 创建表格
*/
function combination(geonum,data){
	console.log(data)
	var height = Math.ceil(viewer.camera.positionCartographic.height);
	ceartHtml(height,geonum,2,data)
}





