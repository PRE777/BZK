var drawLon;
var drawLat;
var maxLng = 0.0;
var minLng = 0.0;
var maxLat = 0.0;
var minLat = 0.0;
var gridLineColor = new Cesium.ColorGeometryInstanceAttribute(253/255, 228/255, 225/255, 0.5);
//网格
var global_grid_primitives = new Cesium.PrimitiveCollection();
viewer.scene.primitives.add(global_grid_primitives);
/*
 *cesium画网格
 *画经纬度线
 *跨半球处理 
*/
function drawGrid() {
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
	if (viewer.scene.mode == Cesium.SceneMode.SCENE2D) {
	    // 范围对象
	    var extent = {};
	    // 得到当前三维场景
	    var scene = viewer.scene;
	    // 得到当前三维场景的椭球体
	    var ellipsoid = scene.globe.ellipsoid;
	    var canvas = scene.canvas;
	    // canvas左上角
	    var car3_lt = viewer.camera.pickEllipsoid(new Cesium.Cartesian2(0, 0), ellipsoid);
	    // canvas右下角
	    var car3_rb = viewer.camera.pickEllipsoid(new Cesium.Cartesian2(canvas.width, canvas.height), ellipsoid);
	    // 当canvas左上角和右下角全部在椭球体上
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
	        do {
	            yIndex <= canvas.height ? yIndex += 10 : canvas.height;
	            car3_lt2 = viewer.camera.pickEllipsoid(new Cesium.Cartesian2(0, yIndex), ellipsoid);
	        } while (!car3_lt2);
	        var carto_lt2 = ellipsoid.cartesianToCartographic(car3_lt2);
	        var carto_rb2 = ellipsoid.cartesianToCartographic(car3_rb);
	        extent.xmin = Cesium.Math.toDegrees(carto_lt2.longitude);
	        extent.ymax = Cesium.Math.toDegrees(carto_lt2.latitude);
	        extent.xmax = Cesium.Math.toDegrees(carto_rb2.longitude);
	        extent.ymin = Cesium.Math.toDegrees(carto_rb2.latitude);
	    }
	        // 获取高度
	        extent.height = Math.ceil(viewer.camera.positionCartographic.height);
	        return extent;    
	    }
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





