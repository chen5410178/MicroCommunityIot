
import {wgs84togcj02} from 'api/map/mapApi.js';
export default class WorkLicenseLineMapApi {

    constructor(_element) {

    }

    initMap(_pos,_elementId) {
        let _lon = 116.307484;
        let _lat = 39.984120;
        _pos.forEach(_m => {
            let _latLon = wgs84togcj02(_m.lon,_m.lat);
            _m.lat = _latLon.lat;
            _m.lon = _latLon.lon;
        });

        if (_pos && _pos.length > 0) {
            _lat = _pos[0].lat;
            _lon = _pos[0].lon;
        }


        let center = new TMap.LatLng(_lat, _lon)
        //定义map变量，调用 TMap.Map() 构造函数创建地图
        let map = new TMap.Map(document.getElementById(_elementId), {
            center: center,//设置地图中心点坐标
            zoom: 18,   //设置地图缩放级别
            baseMap: {			//底图设置（参数为：VectorBaseMap对象）
                type: 'vector',	//类型：失量底图
                features: ['base', 'building2d', 'point']
                //仅渲染：道路及底面(base) + 2d建筑物(building2d)，以达到隐藏文字的效果
            }
        });
        this.map = map;
        this._addPointMachine(_pos);
    }

    _addPointMachine(_pos) {
        let _geometriesMarker = [];
        let _geometriesLabel = [];
        let _path = [];

        _pos.forEach(_m => {
            try{
                let center = new TMap.LatLng(_m.lat, _m.lon);
                _geometriesMarker.push({
                    "id": _m.wltId,   //点标记唯一标识，后续如果有删除、修改位置等操作，都需要此id
                    "styleId": 'myStyle',  //指定样式id
                    "position": center,  //点标记坐标位置
                });
                
                _geometriesLabel.push({
                    'id': 'label_' + _m.wltId, //点图形数据的标志信息
                    'styleId': 'label', //样式id
                    'position': center, //标注点位置
                   // 'content': _m.staffName, //标注文本
                   'content':vc.timeMinFormat(_m.createTime)
                });
                _path.push(center);
            }catch(e){

            }
        });
        let markerLayer = new TMap.MultiMarker({
            map: this.map,  //指定地图容器
            //样式定义
            styles: {
                //创建一个styleId为"myStyle"的样式（styles的子属性名即为styleId）
                "myStyle": new TMap.MarkerStyle({
                    "width": 25,  // 点标记样式宽度（像素）
                    "height": 35, // 点标记样式高度（像素）
                    "src": '/img/maper.png',  //图片路径
                    //焦点在图片中的像素位置，一般大头针类似形式的图片以针尖位置做为焦点，圆形点以圆心位置为焦点
                    "anchor": { x: 32, y: 32 }
                })
            },
            //点标记数据数组
            geometries: _geometriesMarker
        });

        var label = new TMap.MultiLabel({
            id: 'label-layer',
            map: this.map, //设置折线图层显示到哪个地图实例中
            //文字标记样式
            styles: {
                'label': new TMap.LabelStyle({
                    'color': '#3777FF', //颜色属性
                    'size': 20, //文字大小属性
                    'offset': { x: 0, y: 15 }, //文字偏移属性单位为像素
                    'angle': 0, //文字旋转属性
                    'alignment': 'center', //文字水平对齐属性
                    'verticalAlignment': 'middle' //文字垂直对齐属性
                })
            },
            //文字标记数据
            geometries: _geometriesLabel
        });

        var polylineLayer = new TMap.MultiPolyline({
            id: 'polyline-layer', //图层唯一标识
            map: this.map,//设置折线图层显示到哪个地图实例中
            //折线样式定义
            styles: {
                'style_blue': new TMap.PolylineStyle({
                    'color': '#3777FF', //线填充色
                    'width': 6, //折线宽度
                    'borderWidth': 5, //边线宽度
                    'borderColor': '#FFF', //边线颜色
                    'lineCap': 'butt' //线端头方式
                })
            },
            //折线数据定义
            geometries: [
                {//第1条线
                    'id': 'pl_1',//折线唯一标识，删除时使用
                    'styleId': 'style_blue',//绑定样式名
                    'paths': _path
                }
            ]
        });
    }
}