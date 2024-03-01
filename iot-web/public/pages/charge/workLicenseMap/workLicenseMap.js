/**
    入驻小区
**/
(function (vc) {
    var DEFAULT_PAGE = 1;
    var DEFAULT_ROWS = 30;
    vc.extends({
        data: {
            workLicenseMapInfo: {
                machines: [],
                map:{},
                total: 0,
                records: 1,
                moreCondition: false,
                machineId: '',
                conditions: {
                    machineName: '',
                    machineCode: '',
                    staffName: '',

                }
            }
        },
        _initMethod: function () {
            $that._listWorkLicenseMachines(DEFAULT_PAGE, DEFAULT_ROWS);

           
        },
        _initEvent: function () {
        },
        methods: {

            _initMap:function(){
                let _lon = 39.984120;
                let _lat = 116.307484;
                let _machines = $that.workLicenseMapInfo.machines;
                if(_machines && _machines.length>0){
                    _lat = _machines[0].lat;
                    _lon = _machines[0].lon;
                }
                let center = new TMap.LatLng(_lon, _lat)
                //定义map变量，调用 TMap.Map() 构造函数创建地图
                let map = new TMap.Map(document.getElementById('wlMap'), {
                    center: center,//设置地图中心点坐标
                    zoom: 18,   //设置地图缩放级别
                    baseMap: {			//底图设置（参数为：VectorBaseMap对象）
                        type: 'vector',	//类型：失量底图
                        features: ['base', 'building2d','point']  
                        //仅渲染：道路及底面(base) + 2d建筑物(building2d)，以达到隐藏文字的效果
                      }
                });
                $that.workLicenseMapInfo.map = map;
                $that._addPointMachine();
            },
            _addPointMachine:function(){
                let _geometriesMarker = [];
                let _geometriesLabel = [];

                let _machines = $that.workLicenseMapInfo.machines;
                _machines.forEach(_m => {
                    let  center = new TMap.LatLng(_m.lon, _m.lat);
                    _geometriesMarker.push({
                        "id": _m.machineId,   //点标记唯一标识，后续如果有删除、修改位置等操作，都需要此id
                        "styleId": 'myStyle',  //指定样式id
                        "position": center,  //点标记坐标位置
                        "properties": {//自定义属性
                            "title": _m.staffName
                        }
                    });
                    _geometriesLabel.push({
                        'id': 'label_'+_m.machineId, //点图形数据的标志信息
                        'styleId': 'label', //样式id
                        'position': center, //标注点位置
                        'content': _m.staffName, //标注文本
                    })
                });
                let markerLayer = new TMap.MultiMarker({
                    map: $that.workLicenseMapInfo.map,  //指定地图容器
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
                    map: $that.workLicenseMapInfo.map, //设置折线图层显示到哪个地图实例中
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
            },
            _listWorkLicenseMachines: function (_page, _rows) {
                $that.workLicenseMapInfo.conditions.page = _page;
                $that.workLicenseMapInfo.conditions.row = _rows;
                let param = {
                    params: $that.workLicenseMapInfo.conditions
                };

                //发送get请求
                vc.http.apiGet('/workLicense.listWorkLicenseMachine',
                    param,
                    function (json, res) {
                        let _json = JSON.parse(json);
                        $that.workLicenseMapInfo.machines = _json.data;
                        $that._initMap();
                    }, function (errInfo, error) {
                        console.log('请求失败处理');
                    }
                );
            },

        }
    });
})(window.vc);
