var wyoming;
var wyomings;
var bluePolygon
var poly;
var near_water_entity = viewer.entities.add(new Cesium.Entity());
var duo_draws_entity = viewer.entities.add(new Cesium.Entity());
var duo_draws_entity_two = viewer.entities.add(new Cesium.Entity());
var entity_sky = viewer.entities.add(new Cesium.Entity());
//左右 滚轮 
var handler = new Cesium.ScreenSpaceEventHandler(viewer.scene.canvas);
/*
 * 2d or 3d事件
*/
function Scene(cnum) {
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
function clearGrid(){
	sessionStorage.removeItem('geonum')
	for(var i=0;i<duo_draws_entity._children.length;i++){
        viewer.entities.remove(duo_draws_entity._children[i]);
    }
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
	var height = Math.ceil(viewer.camera.positionCartographic.height);
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
		str +='</ul><img src="../src/img/tuichu.png" class="closetable" style="position: absolute;top: 12px;right: 14px;"/>';
	$('.bottom_table').append(str);
	$('.closetable').on('click',function(){
		$('.bottom_table').hide();
	})
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
	var type = id;
	tableDraw(height,geonum,pagesnums,id,type,voe,comdata);
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
			if( json.data.datas == undefined ){
				var datau = [];
			}else{
				var datau = json.data.datas;
            }
			var pageSize = 2;
			var title = json.data.gaugeOutfit;
			var totalnum = json.data.count;
            console.log("sdasdadad----"+totalnum);
			loadTableone(datau, totalnum, pageNum, pageSize,title,height,geonum,id,type)
		});
	}else{
		comdata['height'] = height;
		comdata['pageNum'] = pageNum;
		comdata['pageSize'] = 2;
		comdata['geonums'] = geonum;
		comdata['dataType'] = type;
		ajaxPost('/gridgeneration/data/getInformationHighlyActive',comdata,function(json){
			if( json.data.datas == undefined ){
				var datau = [];
			}else{
				var datau = json.data.datas;
			}
			var pageSize = 2;
			var title = json.data.gaugeOutfit;
			var totalnum = json.data.count;
			loadTableone(datau, totalnum, pageNum, pageSize,title,height,geonum,id,type,comdata)
		})
	}
}
//删除
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
            height:120,
            limit: pageSize,
            limits: [1, 2, 3],
            curr: pageNum,
            groups: 2,
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
                    groups: 2,
                    hash: true,
                    layout: ['prev', 'page', 'next', 'skip'],
                    jump: function (obj, first) {
                    	var voe = 1;
                        //首次不执行
                        if (!first) {
                        	console.log(voe)
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
	console.log(json)
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
	var height = Math.ceil(viewer.camera.positionCartographic.height);
	$('.bottom_table').empty();
	ceartHtml(height,geonum,2,data)
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




