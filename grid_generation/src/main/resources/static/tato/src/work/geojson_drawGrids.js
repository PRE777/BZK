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
	removeImageryLayersG()
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
	removeImageryLayersG()
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
	removeImageryLayersG()
	sessionStorage.removeItem('geonum')
	$.each(duo_draws_entity._children,function(i,obj){          
	      viewer.entities.remove(obj);
	});
}
/*
 * 画多边形网格
*/
function drawGridOnes(json) {
	removeImageryLayersG()
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
	var type = [];
	var titname = [];
	var objSname = {};
	var objSlist = [];
	var dataTypes= '';
	console.log(xtree1._dataJson)
	console.log(xtree1.GetChecked())
	for(var g=0;g<xtree1.GetChecked().length;g++){
		dataTypes += xtree1.GetChecked()[g].defaultValue+',';
		objSname = {
			'number':xtree1.GetChecked()[g].defaultValue,
			'name':xtree1.GetChecked()[g].title
		}
		objSlist.push(objSname)
	}
	console.log(objSlist)
	var str = '<img src="../src/img/tuichu.png" class="closetable" style="position: absolute;top: 12px;right: 14px;"/></div>';
	$('.gf_bottom_table').append(str);
	$('.closetable').on('click',function(){
	$('.gf_bottom_table').hide();
	})
	var stry = '<div class="layui-tab-content gf_content" style="height: 100px;margin-top:28px;">'+
			'<div class="layui-tab-item gf_table">'+
			'<table class="layui-hide" id="demo"  lay-filter="text"></table>'+
			'<div id="pages" style="text-align:right;margin-right:1.3%;"></div>'+
			'</div>';
	'</div>';
	$('.gf_bottom_table').append(stry);
	//$('.gf_bottom_table').show();
	//调用表格
	var id = 'gf';
	var pagesnums = 1;
	console.log(dataTypes)
	tableDraw(height,geonum,pagesnums,objSlist,dataTypes);
}
/*
 * 网格表格
*/
function tableDraw(height,geonum,pagesnums,objSlist,type){
	var indexd = layer.load(2, {time: 20*1000});
	
	
	if( pagesnums == undefined ){
		var pageNum = 1;
	}else{
		var pageNum = pagesnums;
	}
	var pageSize = 2;
	var postdata = {
			"height":height,
			"geoNums":geonum,
			"pageNum":pageNum,
			"pageSize":pageSize,
			"mapTypeLayerName":type
	}
	ajaxPost('/gridgeneration/discretize/getFeatures',postdata,function(json){
		layer.close(indexd);
		if(json.server_status == 200){
		$('.gf_bottom_table').show();
		var lists = [];
		var list = {};
		var poi = json.data.records;
		for(var i=0;i<poi.length;i++){
			var poiName = JSON.parse(poi[i].featureGeoJson) 
			console.log(poiName.properties.name)
			console.log(typeof(poiName.properties))
			list = {
				'sourceId':poi[i].geo_num[0],
				'indexTime':getdate(poi[i].indexTime),
				'name':poiName.properties.name,
				'id':poi[i].sourceId,
				'layerName':nameS(poi[i].layerName,objSlist),
				"geoJson":poiName
			}
			lists.push(list)
		}
		console.log(lists)
		var textTitle = [
             {"field":"sourceId","edit":"text","title":"剖分编码","align":"center"},
             {"field":"name","edit":"text","title":"名称","align":"center"},
             {"field":"layerName","edit":"text","title":"图层名称","align":"center"},
             {"field":"indexTime","edit":"text","title":"时间","align":"center"},
             {"field":"id","edit":"text","title":"来源标识","align":"center"},
             {"toolbar": "#barDemo", "field": "", "title": "操作", "align": "center"}
        ];
		var datau = textTitle;
		var title = lists;
		var totalnum = json.data.recordCount;
		loadTableones(datau, totalnum, pageNum, pageSize,title,height,geonum,objSlist,type)
		}else{
			layer.msg('数据查询失败');
		}
	},function(){
		layer.close(index);
	   	layer.msg('数据查询失败');
	});
}
//表格数据:data 表格总共多少条数据:totalnum 当前页:pageNum 每页显示条数:pageSize
function loadTableones(data, totalnum, pageNum, pageSize,title,height,geonum,objSlist,type) {
	layui.use(['table', 'laypage'], function () {
        var table = layui.table;
        var laypage = layui.laypage;
        table.render({
        	title: '用户数据表',
            data: title,
            elem: '#demo',
            page: false,
            cellMinWidth: 180,
            limit: pageSize,
            limits: [1, 2, 3],
            curr: pageNum,
            groups: 5,
            layout: ['prev', 'page', 'next', 'skip'],
            cols: [
                 data
            ],
            done: function (res, page, count) {
                laypage.render({
                    elem: 'pages',
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
                            tableDraw(height,geonum,pagenums,objSlist,type)
                        }
                    }
                });
            }
        });
        //监听行工具事件
        table.on('tool(text)', function(obj){ //注：tool 是工具条事件名，test 是 table 原始容器的属性 lay-filter="对应的值"
          var datas = obj.data //获得当前行数据  
          ,layEvent = obj.event; //获得 lay-event 对应的值
          if(layEvent === 'edit'){
        	  layer.msg('上图');
        	  console.log(datas)
        	  managementGrid(datas.geoJson)
        	  //每一次上图都删除上一次的图 调用
        	  //sImg(datas.BottomLeft,datas.BottomRight,datas.TopLeft,datas.TopRigh,datas.GFUrl)
          }
        });
    })
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
//删除
$('.colose_img').on('click',function(){
	$('.bottom_table').hide();
})
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
	removeImageryLayersG()
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
	removeImageryLayersG()
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
function removeImageryLayersG(){
    viewer.dataSources.removeAll();
}
/**
 * 加载管理网格数据
 * 本地json格式
 */
function managementGrid(geojson){
	console.log($('.right_infor .infor_but_content').eq(2).is('.infor_but_content_bg'))
	DynamicDrawTool.startDrawingClear();
	$.each(duo_draws_entity._children,function(i,obj){          
	      viewer.entities.remove(obj);
	});
	$.each(duo_draws_entity_two._children,function(i,obj){          
	      viewer.entities.remove(obj);
	});
	viewer.entities.remove(wyoming);
	if( $('.right_infor .infor_but_content').eq(2).is('.infor_but_content_bg') ){
		drawPolygon();
	}
    var promise = Cesium.GeoJsonDataSource.load(geojson); 
    //显示管线数据  直接加载json数据 比把json转化成czml在加载 快很多
    promise.then(function (dataSource) {
        viewer.dataSources.add(dataSource);
        var entities = dataSource.entities.values;
        var colorHash = {};
        var color = new Cesium.Color(235/255.0,18/255.0,0,255/255.0);
        var outlineColor = new Cesium.Color(235/255.0,18/255.0,0,255/255.0);
        for (var o = 0; o < entities.length; o++) {
            var entity = entities[o];
            if(entity.polyline == undefined){
            	entity.billboard = undefined;
		        entity.point = new Cesium.PointGraphics({
		            color: Cesium.Color.RED,
		            pixelSize: 6
		        });
            }else{
            	entity.polyline.material = color;
	            entity.polyline.outline = true;
	            entity.polyline.outlineColor = outlineColor;
	            entity.polyline.outlineWidth = 10;
	            entity.polyline.height = 0;
	            entity.polyline.extrudedHeight = 0;
            }
        }
    });
    viewer.flyTo(promise);
}
//时间戳转时间
function getdate(timestamp) {
    var date = new Date(timestamp);
    var Y = date.getFullYear() + '-';
    var M = (date.getMonth()+1 < 10 ? '0'+(date.getMonth()+1) : date.getMonth()+1) + '-';
    var D = date.getDate() + ' ';
    var h = date.getHours() + ':';
    var m = date.getMinutes() + ':';
    var s = date.getSeconds();
    return Y+M+D+h+m+s;
}
//名称
function nameS(name,objSlist){
	for(var y=0;y<objSlist.length;y++ ){
		if( name == objSlist[y].number  ){
			return objSlist[y].name
		}
	}
}






