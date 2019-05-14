var wyoming;
var wyomings;
var poly;
var near_water_entity = viewer.entities.add(new Cesium.Entity());
var duo_draws_entity = viewer.entities.add(new Cesium.Entity());
var duo_draws_entity_two = viewer.entities.add(new Cesium.Entity());
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
	sessionStorage.setItem('geonum',geonum)
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
 *多网格 单点画
*/
function getPos(draw) {
    DynamicDrawTool.startDrawOne(viewer, true,draw, function (cartesians) {
		drawGridOnesTwo(cartesians)
    });
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
	sessionStorage.setItem('geonum',gesoum)
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




