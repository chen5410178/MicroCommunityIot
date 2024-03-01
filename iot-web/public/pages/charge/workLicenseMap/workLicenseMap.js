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

            $that._initMap();
        },
        _initEvent: function () {
        },
        methods: {

            _initMap:function(){
                let center = new TMap.LatLng(39.984120, 116.307484)
                //定义map变量，调用 TMap.Map() 构造函数创建地图
                let map = new TMap.Map(document.getElementById('wlMap'), {
                    center: center,//设置地图中心点坐标
                    zoom: 17.2,   //设置地图缩放级别
                    pitch: 43.5,  //设置俯仰角
                    rotation: 45    //设置地图旋转角度
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
                    }, function (errInfo, error) {
                        console.log('请求失败处理');
                    }
                );
            },

        }
    });
})(window.vc);
