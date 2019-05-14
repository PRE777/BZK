var wyoming;
var wyomings;
var poly;
var drawLon;
var drawLat;
var ares = '';
//entities 父元素
var near_water_entity;
var duo_draws_entity;
var duo_draws_entity_two;
//Primitive 绘制集合  
var box_primitives;
var elli;
//闪烁
var fik_primitives;
//左右 滚轮 
var handler;
var maxLng = 0.0;
var minLng = 0.0;
var maxLat = 0.0;
var minLat = 0.0;
var gridLineColor = new Cesium.ColorGeometryInstanceAttribute(253/255, 228/255, 225/255, 0.5);
//网格
var global_grid_primitives = new Cesium.PrimitiveCollection();
viewer.scene.primitives.add(global_grid_primitives);
function receive(){
	//entities 父元素
	near_water_entity = viewer.entities.add(new Cesium.Entity());
	duo_draws_entity = viewer.entities.add(new Cesium.Entity());
	duo_draws_entity_two = viewer.entities.add(new Cesium.Entity());
	//Primitive 绘制集合  
	box_primitives = new Cesium.PrimitiveCollection();
	viewer.scene.primitives.add(box_primitives);
	elli = Cesium.Ellipsoid.WGS84;
	//闪烁
	fik_primitives = new Cesium.PrimitiveCollection();
	viewer.scene.primitives.add(fik_primitives);
	//左右 滚轮 
	handler = new Cesium.ScreenSpaceEventHandler(viewer.scene.canvas);
	handler.setInputAction(function(wheelment){
	    console.log('滚轮事件：',wheelment);    
	    ares = '';
	},Cesium.ScreenSpaceEventType.WHEEL);
}
/*
 *cesium画网格
 *画经纬度线
 *跨半球处理 
*/
function drawGrid() {
	var hght = Math.ceil(viewer.camera.positionCartographic.height);
	if( hght < 10000000 ){
		clearTime(2)
		fik_primitives.show = false;
	}else{
		clearTime(1)
	}
	dgBox();
	var extent = CesiumViewTool();
	if (extent.xmin == undefined) {
        extent.xmin = -179;
    }
    if (extent.xmax == undefined) {
        extent.xmax = 179;
    }
    if (extent.ymin == undefined) {
        extent.ymin = -89;
    }
    if (extent.ymax == undefined) {
        extent.ymax = 89;
    }
    // 避免重复绘制
    if (minLng == extent.xmin && maxLng == extent.xmax && minLat == extent.ymin && maxLat == extent.ymax) {
        return;
    }
    minLng = extent.xmin;
    maxLng = extent.xmax;
    minLat = extent.ymin;
    maxLat = extent.ymax;

    if (extent.xmin == -179 && extent.xmax == 179 && extent.ymin == -89 && extent.ymax == 89) {
        extent.height = 50000000;
    }
    var curl = '/gridgeneration/data/drawGridOnMap?scale='+ extent.height+'&lbLng='+extent.xmin+'&lbLat='+extent.ymin+'&rtLng='+extent.xmax+'&rtLat='+extent.ymax+'';
	ajaxGet(curl,false,function(json){
			var lons = json.lons;
			var lngs = json.lats;
			global_grid_primitives.removeAll();
			drawLngLatLines(lngs,lons)
	})
}
/*
 * 监听屏幕范围
*/
function listter(){
	viewer.scene.camera.moveEnd.addEventListener(drawGrid)
}
/*
 * 不显示网格
*/
function NoDrawGrid() {
	viewer.scene.camera.moveEnd.removeEventListener(drawGrid)
	global_grid_primitives.removeAll();
}
/*
 * cesium监听屏幕范围
 * 屏幕坐标转世界坐标
 * 世界坐标转经纬度坐标
*/
function CesiumViewTool() {
	if( $('#trackPopUp') ){
		$('#trackPopUp').remove();
	}
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
	extent.car3_rb = car3_rb;
	extent.car3_lt = car3_lt;
	return extent;
};
/*
 * 立体网格并绑定相关事件
 */
function dgBox(comdata,serch){
	if( comdata == undefined || comdata == null ){
		var comdata = {}
	}
	//查看时间
	var stratTime = $('#test11').val();
	var time = getNowFormatDate();
	if( time != stratTime && stratTime != ''){
		clearTime(2);
		disconnect();
	}
	if( stratTime != '' ){
		comdata['time'] = stratTime;
    }
	console.log(ares)
	var dataEx = CesiumViewTool();
	if( ares != '' ){
		var xmax = ares.maxlng;
		var ymax = ares.maxlat;
		var xmin = ares.minlng;
		var ymin = ares.minlat;
		var demoHeight = dataEx.height;
	}else{
		var xmax = dataEx.xmax;
		var ymax = dataEx.ymax;
		var xmin = dataEx.xmin;
		var ymin = dataEx.ymin;
		var demoHeight = dataEx.height;
	}
	var type = [];
	var titname = [];
	for(var g=0;g<xtree1.GetChecked().length;g++){
		type.push(xtree1.GetChecked()[g].defaultValue);
		titname.push(xtree1.GetChecked()[g].title)
	}
	comdata['dataTypes'] = type.join(',');
	comdata['leftTopLat'] = ymax;
	comdata['leftTopLng'] = xmin;
	comdata['rightBotmLat'] = ymin;
	comdata['rightBotmLng'] = xmax;
	comdata['height'] = demoHeight;
	var index = layer.load(2, {time: 20*1000});
	ajaxPost('/gridgeneration/data/getDataDistribute',comdata,function(json){
		if(json.server_status == 200){
			layer.close(index);
			//layer.msg('查询成功');
			if( serch == 'serch' ){
//				viewer.camera.setView({
//			        destination: Cesium.Cartesian3.fromDegrees(json.data.datas[0].centrelng, json.data.datas[0].centrelat, demoHeight)
//			    });
			}
			poxJson(json)
		}else{
			layer.close(index);
			layer.msg('查询失败');
		}
		
	})
}
/*
 * 闪烁测试
*/
function flicker(geomee){
	var geome = [{geonum: "425027214833090560", color: "77,171,251,0.5",
		maxlat: 34,
		maxlng: 122,
		minlat: 32,
		minlng: 120}]
	//创建几何图形
	var instancesty = [];
	for( var i= 0;i<geome.length;i++ ){
		var material = new Cesium.Color(255/255,255/255,0,1)
		var instancer = new Cesium.GeometryInstance({
		  geometry : new Cesium.RectangleGeometry({
		    rectangle : Cesium.Rectangle.fromDegrees(geome[i].minlng, geome[i].minlat, geome[i].maxlng, geome[i].maxlat),
		    vertexFormat : Cesium.EllipsoidSurfaceAppearance.VERTEX_FORMAT,
		    height:0,
		    extrudedHeight:100000
		  }),
		  id:geome[i].geonum,
		  attributes: {
			  color: Cesium.ColorGeometryInstanceAttribute.fromColor(material)
		  }
		});
		instancesty.push(instancer)
	}
	var primitive = new Cesium.Primitive({
		geometryInstances: instancesty, 
		appearance: new Cesium.PerInstanceColorAppearance()
    })
	//scene.primitives
	fik_primitives.add(primitive);
}
/*
 *控制闪烁  启动定时器  关闭定时器
*/
var interval;
function clearTime(num){
	clearInterval(interval)
	if(num == 1){
		interval = setInterval(function () {
			fik_primitives.show = !fik_primitives.show;
		}, 2000);
	}else{
//		clearInterval(interval);
	}
}

/*
 * 绘制网格
*/
function poxJson(json){
	console.log(json)
	box_primitives.removeAll();
	handler.removeInputAction(Cesium.ScreenSpaceEventType.LEFT_DOUBLE_CLICK);
	handler.removeInputAction(Cesium.ScreenSpaceEventType.LEFT_CLICK);
	var geome = json.data.datas;
	var scene = viewer.scene;
	var instances = [];
	//创建几何图形
	for( var i= 0;i<geome.length;i++ ){
		var colorRgb = geome[i].color.split(',')
		var material = new Cesium.Color(colorRgb[0]/255,colorRgb[1]/255,colorRgb[2]/255,colorRgb[3])
		var instance = new Cesium.GeometryInstance({
		  geometry : new Cesium.RectangleGeometry({
		    rectangle : Cesium.Rectangle.fromDegrees(geome[i].minlng, geome[i].minlat, geome[i].maxlng, geome[i].maxlat),
		    vertexFormat : Cesium.EllipsoidSurfaceAppearance.VERTEX_FORMAT
		  }),
		  id:geome[i].geonum,
		  attributes : {
		    //(红，绿，蓝，透明度)
			  color: Cesium.ColorGeometryInstanceAttribute.fromColor(material)
		  }
		});
		instances.push(instance)
	}
	//scene.primitives
	box_primitives.add(new Cesium.Primitive({
		geometryInstances: instances, 
		appearance: new Cesium.PerInstanceColorAppearance()
    }));
	//设置单击事件的处理句柄
    handler.setInputAction(function (movement) {
        var pick = viewer.scene.pick(movement.position);
        for( var i= 0;i<geome.length;i++ ){
        	if( geome[i].geonum == pick.id ){
        		$('#trackPopUp').remove();
        		div(geome[i].count)
                var x = movement.position.x;
        		var y = movement.position.y;
                $('#trackPopUp').css({"top":y+50,"left":x-30});
                $('#trackPopUp').show();
        	}
    	}
    }, Cesium.ScreenSpaceEventType.LEFT_CLICK);
    handler.setInputAction(function(click){
    	var pick = viewer.scene.pick(click.position);
    	console.log(pick)
    	for( var i= 0;i<geome.length;i++ ){
        	if( geome[i].geonum == pick.id ){
        	 console.log(geome[i])
        	 ares = geome[i]
    		 var rectangle = new Cesium.Rectangle.fromDegrees(geome[i].minlng, geome[i].minlat, geome[i].maxlng, geome[i].maxlat);
           	 viewer.camera.flyTo({
           		 destination: rectangle
           	 });
        	}
    	}
    },Cesium.ScreenSpaceEventType.LEFT_DOUBLE_CLICK);
}
/*
 * 移除网格
*/
function noBox() {
	box_primitives.removeAll();
	handler.removeInputAction(Cesium.ScreenSpaceEventType.LEFT_CLICK);
	handler.removeInputAction(Cesium.ScreenSpaceEventType.LEFT_DOUBLE_CLICK);
}
/*
 *创建 气泡 
*/
function div(num){
	//动态添加气泡窗口DIV
	var infoDiv = '<div id="trackPopUp" style="display:none;position:absolute;z-index:9999;">总数量'+num+'</div>';
	$("#cesiumContainer").append(infoDiv);
}
/*
* WebSocket
*/
var stompClient = null;
function connect() {

    /*var wsEndPoint='gridgeneration/endpointSang';*/
    /*if (location.host.indexOf("dev.iwhere.com:9008") != -1) {
        wsEndPoint = "http://dev.iwhere.com:9008/gridgeneration/endpointSang";
    }else if (location.host.indexOf("qa.iwhere.com:8110") != -1) {
        wsEndPoint = "http://192.168.50.150:9507/gridgeneration/endpointSang";
    }else if (location.host.indexOf("qa.iwhere.com:8112") != -1) {
        wsEndPoint = "http://10.1.2.119:8010/gridgeneration/endpointSang";
    }else if(location.host.indexOf("localhost:8080") != -1){
        wsEndPoint = "http://localhost:8080/gridgeneration/endpointSang";
	}*/

    var socket = new SockJS('/gridgeneration/endpointSang');

    stompClient = Stomp.over(socket);
    stompClient.connect({}, function(frame) {
    	send()
        stompClient.subscribe('/topic/getResponse', function(greeting){
        	var data = JSON.parse(greeting.body)
        	flicker(data.data)
        	clearTime(1)
            //showGreeting(greeting.body);  
        });
    });
}

//关闭双通道
function disconnect(){
	 $("[name=switch]:checkbox").prop("checked", false);
	fik_primitives.removeAll();
	clearTime(2)
    if(stompClient != null) {
        stompClient.disconnect();
    }
}
//发送个假消息
function send(){
	 stompClient.send("/keepwatch", {}, JSON.stringify({'name': name}));
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

function getNowFormatDate() {
    var date = new Date();
    var seperator1 = "-";
    var year = date.getFullYear();
    var month = date.getMonth() + 1;
    var strDate = date.getDate();
    if (month >= 1 && month <= 9) {
        month = "0" + month;
    }
    if (strDate >= 0 && strDate <= 9) {
        strDate = "0" + strDate;
    }
    var currentdate = year + seperator1 + month + seperator1 + strDate;
    return currentdate;
}

function drawLngLatLines(lats,lngs) {
    if (lngs == undefined || lats == undefined) {
        return;
    }
    var instanceOutLines = [];//画网格线
    // 经度线
    for (var i = 0; i < lngs.length; i = i+1) {
        var pArray = [];
        for (var j = 0; j < lats.length; j = j+2) {
            pArray.push(lngs[i]);
            pArray.push(lats[j]);
        }
        var polyline = new Cesium.PolylineGeometry({
            positions : Cesium.Cartesian3.fromDegreesArray(pArray),
            width : 0.5,
        });
        var instanceOutLine = new Cesium.GeometryInstance({
            geometry: polyline,
            attributes: {
                color: gridLineColor
            }
        });

        instanceOutLines.push(instanceOutLine);
    }
    // 纬度线
    for (var i = 0; i < lats.length; i = i+1) {
    	var pArray = [];
        for (var j = 0; j < lngs.length; j = j+2) {
            pArray.push(lngs[j]);
            pArray.push(lats[i]);
        }
        var polyline = new Cesium.PolylineGeometry({
            positions : Cesium.Cartesian3.fromDegreesArray(pArray),
            width : 0.5,
        });

        var instanceOutLine = new Cesium.GeometryInstance({
            geometry: polyline,
            attributes: {
                color: gridLineColor
            }
        });

        instanceOutLines.push(instanceOutLine);
    }
    global_grid_primitives.add(new Cesium.Primitive({
        geometryInstances: instanceOutLines,
        appearance: new Cesium.PolylineColorAppearance({
            flat : true
        })
    }));
}





