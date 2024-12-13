(function (vc) {
    vc.extends({
        data: {
            monitorMapInfo: {
                faceLogs: [],
                communitys: [],
                machines: [],
                personFaces: [],
                dispatchLogs:[],
                communityId: '',
                queryDate: '',
                personName: '',
                personId: '',
                map: {}
            },

        },
        _initMethod: function () {
            $that.monitorMapInfo.queryDate = vc.getDateYYYYMMDD();
            $that.monitorMapInfo.communityId = vc.getCurrentCommunity().communityId;
            $that._initMap();
            $that._listMachines();
            $that._listPersonFaces();
            $that._listDispatchLogs();
            vc.initDate('queryDate', function (_value) {
                $that.monitorMapInfo.queryDate = _value;
                $that.monitorMapInfo.map.destroy();
                $that._initMap();

                //$that._listWorkLicensePos();
            })
        },
        _initEvent: function () {
            vc.on('monitorMap', 'selectVideo', function (_param) {

            });
        },
        methods: {
            _changePerson: function (_person) {
                $that.monitorMapInfo.personId = _person.personId;
                $that._loadFaceLog();
            },
            _listPersonFaces: function (_page, _rows) {
                let param = {
                    params: {
                        page: 1,
                        row: 10,
                        communityId: $that.monitorMapInfo.communityId,
                        personName: $that.monitorMapInfo.personName,
                        personId: $that.monitorMapInfo.personId,
                        personType:'normal'
                    }
                };

                //发送get请求
                vc.http.apiGet('/ai.getPersonFace',
                    param,
                    function (json, res) {
                        let _json = JSON.parse(json);
                        $that.monitorMapInfo.personFaces = _json.data;
                    }, function (errInfo, error) {
                        console.log('请求失败处理');
                    }
                );
            },
            _loadFaceLog: function () {
                let param = {
                    params: {
                        personId: $that.monitorMapInfo.personId,
                        communityId: $that.monitorMapInfo.communityId,
                        page: 1,
                        row: 100,
                        queryDate: $that.monitorMapInfo.queryDate
                    }
                };

                //发送get请求
                vc.http.apiGet('/ai.getFaceLog',
                    param,
                    function (json, res) {
                        let _json = JSON.parse(json);
                        $that.monitorMapInfo.faceLogs = _json.data;
                        $that._initMap();
                        $that._addPointMachine();
                        $that._addPointPerson();

                    }, function (errInfo, error) {
                        console.log('请求失败处理');
                    }
                );
            },
            _listMachines: function () {

                let param = {
                    params: {
                        page: 1,
                        row: 50,
                        communityId: $that.monitorMapInfo.communityId
                    }
                };

                //发送get请求
                vc.http.apiGet('/ai.queryMonitorCamera',
                    param,
                    function (json, res) {
                        let _json = JSON.parse(json);
                        $that.monitorMapInfo.machines = _json.data;

                        $that._addPointMachine();
                    }, function (errInfo, error) {
                        console.log('请求失败处理');
                    }
                );
            },
            _listDispatchLogs: function (_page, _rows) {
                let param = {
                    params: {
                        page:1,
                        row:100,
                        communityId: $that.monitorMapInfo.communityId,
                    }
                };

                //发送get请求
                vc.http.apiGet('/ai.getDispatchLog',
                    param,
                    function (json, res) {
                        let _json = JSON.parse(json);
                       $that.monitorMapInfo.dispatchLogs = _json.data;
                    }, function (errInfo, error) {
                        console.log('请求失败处理');
                    }
                );
            },
            _initMap: function () {
                let _lon = 116.307484;
                let _lat = 39.984120;
                try {
                    if ($that.monitorMapInfo.map.destroy) {
                        $that.monitorMapInfo.map.destroy();
                    }
                } catch (e) {

                }
                let _machines = $that.monitorMapInfo.machines;

                if (_machines && _machines.length > 0) {
                    _lat = _machines[0].lat;
                    _lon = _machines[0].lng;
                }
                let center = new TMap.LatLng(_lat, _lon)
                //定义map变量，调用 TMap.Map() 构造函数创建地图
                let map = new TMap.Map(document.getElementById('wlMap'), {
                    center: center,//设置地图中心点坐标
                    zoom: 18,   //设置地图缩放级别
                    baseMap: {			//底图设置（参数为：VectorBaseMap对象）
                        type: 'vector',	//类型：失量底图 , 'point'
                        features: ['base', 'building2d']
                        //仅渲染：道路及底面(base) + 2d建筑物(building2d)，以达到隐藏文字的效果
                    }
                });
                $that.monitorMapInfo.map = map;

            },
            _addPointMachine: function () {
                let _geometriesMarker = [];
                let _geometriesLabel = [];

                let _machines = $that.monitorMapInfo.machines;
                _machines.forEach(_m => {
                    let center = new TMap.LatLng(_m.lat, _m.lng);
                    _geometriesMarker.push({
                        "id": _m.machineId,   //点标记唯一标识，后续如果有删除、修改位置等操作，都需要此id
                        "styleId": 'myStyle',  //指定样式id
                        "position": center,  //点标记坐标位置
                    });
                    _geometriesLabel.push({
                        'id': 'label_' + _m.machineId, //点图形数据的标志信息
                        'styleId': 'label', //样式id
                        'position': center, //标注点位置
                        'content': _m.machineName, //标注文本
                    })
                });
                let markerLayer = new TMap.MultiMarker({
                    map: $that.monitorMapInfo.map,  //指定地图容器
                    //样式定义
                    styles: {
                        //创建一个styleId为"myStyle"的样式（styles的子属性名即为styleId）
                        "myStyle": new TMap.MarkerStyle({
                            "width": 35,  // 点标记样式宽度（像素）
                            "height": 38, // 点标记样式高度（像素）
                            "src": '/img/camera.png',  //图片路径
                            "cursor": "pointer",
                            //焦点在图片中的像素位置，一般大头针类似形式的图片以针尖位置做为焦点，圆形点以圆心位置为焦点
                            "anchor": { x: 32, y: 32 }
                        })
                    },
                    //点标记数据数组
                    geometries: _geometriesMarker
                });

                markerLayer.on('click', function (e) {
                    let _machine = {};
                    let _machineId = e.geometry.id;
                    let _machines = $that.monitorMapInfo.machines;
                    _machines.forEach(_m => {
                        if (_m.machineId == _machineId) {
                            _machine = _m;
                        }
                    })
                    vc.emit('playMonitorVideo', 'openPlayMonitorVideoModal', _machine);
                });

                let label = new TMap.MultiLabel({
                    id: 'label-layer',
                    map: $that.monitorMapInfo.map, //设置折线图层显示到哪个地图实例中
                    //文字标记样式
                    styles: {
                        'label': new TMap.LabelStyle({
                            'color': '#3777FF', //颜色属性
                            'size': 15, //文字大小属性
                            'offset': { x: 0, y: 15 }, //文字偏移属性单位为像素
                            'angle': 0, //文字旋转属性
                            'alignment': 'center', //文字水平对齐属性
                            'verticalAlignment': 'middle' //文字垂直对齐属性
                        })
                    },
                    //文字标记数据
                    geometries: _geometriesLabel
                });
                if (_machines && _machines.length > 0) {
                    $that.monitorMapInfo.map.panTo(new TMap.LatLng(_machines[0].lat, _machines[0].lng));
                }

            },
            _addPointPerson() {
                let _geometriesMarker = [];
                let _geometriesLabel = [];
                let _path = [];
                let _faceLogs = $that.monitorMapInfo.faceLogs;

                if (!_faceLogs || _faceLogs.length < 1) {
                    return;
                }
                let _map = $that.monitorMapInfo.map;

                _faceLogs.forEach(_m => {
                    try {
                        let center = new TMap.LatLng(_m.lat, _m.lon);
                        _geometriesMarker.push({
                            "id": _m.logId,   //点标记唯一标识，后续如果有删除、修改位置等操作，都需要此id
                            "styleId": 'myStyle',  //指定样式id
                            "position": center,  //点标记坐标位置
                        });

                        _geometriesLabel.push({
                            'id': 'label_' + _m.logId, //点图形数据的标志信息
                            'styleId': 'label', //样式id
                            'position': center, //标注点位置
                            // 'content': _m.staffName, //标注文本
                            'content': vc.timeMinFormat(_m.createTime)
                        });
                        _path.push(center);
                    } catch (e) {

                    }
                });
                let markerLayer = new TMap.MultiMarker({
                    map: _map,  //指定地图容器
                    //样式定义
                    styles: {
                        //创建一个styleId为"myStyle"的样式（styles的子属性名即为styleId）
                        "myStyle": new TMap.MarkerStyle({
                            "width": 25,  // 点标记样式宽度（像素）
                            "height": 35, // 点标记样式高度（像素）
                            "src": '/img/person.png',  //图片路径
                            //焦点在图片中的像素位置，一般大头针类似形式的图片以针尖位置做为焦点，圆形点以圆心位置为焦点
                            "anchor": { x: 32, y: 32 }
                        })
                    },
                    //点标记数据数组
                    geometries: _geometriesMarker
                });

                let label = new TMap.MultiLabel({
                    id: 'label-layer',
                    map: _map, //设置折线图层显示到哪个地图实例中
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

                let polylineLayer = new TMap.MultiPolyline({
                    id: 'polyline-layer', //图层唯一标识
                    map: _map,//设置折线图层显示到哪个地图实例中
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
            },
            _viewAlterImage: function (_url) {
                vc.emit('viewImage', 'showImage', {
                    url: _url
                });
            },
        }
    });

})(window.vc);