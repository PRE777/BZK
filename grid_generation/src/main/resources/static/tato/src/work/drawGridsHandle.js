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
	viewer.dataSources.removeAll();
}
/*
 * 单点选网格
*/
function drawGridOne(lon_min, lat_min, lon_max, lat_max,height,geonum) {
	scode = geonum
	console.log(scode)
	/*viewer.entities.remove(wyoming);
	wyoming = viewer.entities.add({
		rectangle: {
			coordinates: Cesium.Rectangle.fromDegrees(lon_min, lat_min, lon_max, lat_max),
			material: Cesium.Color.RED.withAlpha(0.5),
			outline: true,
		}
	});*/
	ajaxGet('/gridgeneration/discretize/getDiscretizationVectors?sourceId='+sourceId+'&geonums='+geonum+'',false,function(json){
		//console.log(json.data[geonum])
		managem(json.data[geonum])
	});
}

function managem(jsonp){
	console.log(scode)
	$('#handle').show();
	$('#handle').attr('href','/gridgeneration/discretize/doDiscretization?sourceId='+sourceId+'&geonums='+scode+'');
    var promise = Cesium.GeoJsonDataSource.load(jsonp); 
    //显示管线数据  直接加载json数据 比把json转化成czml在加载 快很多
    promise.then(function (dataSource) {
    	//viewer.dataSources.removeAll();
    	var len = viewer.dataSources.length;
    	if( len > 0 ){
    		for(var i=0;i<len;i++){
    			var datasour = viewer.dataSources.get(i)
    			console.log(datasour)
    			if( datasour._name == 'addline' ){
    				viewer.dataSources.remove(datasour)
    			}
    		}
    	}
    	dataSource.name = 'addline'
        viewer.dataSources.add(dataSource);
        //console.log(viewer.dataSources)
        var entities = dataSource.entities.values;
        var colorHash = {};
        var color = new Cesium.Color(116/255.0,243/255.0,217/255.0,255/255.0);
        var outlineColor = new Cesium.Color(116/255.0,243/255.0,217/255.0,255/255.0);
        for (var o = 0; o < entities.length; o++) {
            var entity = entities[o];
            //console.log(entity)
            if(entity.polyline == undefined){
            	entity.billboard = undefined;
		        entity.point = new Cesium.PointGraphics({
		            color: color,
		            pixelSize: 6,
		            distanceDisplayCondition:999
		        });
            }else{
            	entity.polyline.material = color;
                entity.polyline.outline = true;
                entity.polyline.outlineColor = outlineColor;
                entity.polyline.outlineWidth = 10;
                entity.polyline.height = 5000.0;
                entity.polyline.extrudedHeight =0;
                entity.polyline._zIndex = 999;
            }
        }
    });
    //viewer.flyTo(promise);
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
		//console.log(movement.position)
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
function clearGridTwoEven(){
	sessionStorage.removeItem('geonum')
	DynamicDrawTool.startDrawingClearTwo();
	$.each(duo_draws_entity_two._children,function(i,obj){          
	      viewer.entities.remove(obj);
	});
}






