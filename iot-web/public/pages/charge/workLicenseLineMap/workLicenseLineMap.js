/**
    入驻小区
**/
import WorkLicenseLineMapApi from 'api/map/workLicenseLineMapApi.js';
(function (vc) {
    var DEFAULT_PAGE = 1;
    var DEFAULT_ROWS = 30;
    vc.extends({
        data: {
            workLicenseLineMapInfo: {
                pos: [],
                machineId:'',
                uploadDate:'',
                staffName:'',
                map: {},
            }
        },
        _initMethod: function () {
            $that.workLicenseLineMapInfo.machineId = vc.getParam('machineId');
            $that.workLicenseLineMapInfo.uploadDate = vc.getParam('uploadDate');
            $that._listWorkLicenseMachines(DEFAULT_PAGE, DEFAULT_ROWS);
            $that._listWorkLicensePos();

            vc.initDate('uploadDate',function(_value){
                $that.workLicenseLineMapInfo.uploadDate = _value;
                $that.workLicenseLineMapInfo.map.map.destroy();
                $that._listWorkLicensePos();
            })

        },
        _initEvent: function () {
        },
        methods: {

            _initMap: function () {
                let _pos = $that.workLicenseLineMapInfo.pos;
                let _tmpLon = 0;
                _pos.forEach(_m => {
                    _tmpLon = _m.lon;
                    _m.lon = _m.lat;
                    _m.lat = _tmpLon;
                });
               
                let map = new WorkLicenseLineMapApi();
                map.initMap(_pos,'wlLineMap');
            
                $that.workLicenseLineMapInfo.map = map;
            },
            _listWorkLicensePos: function (_page, _rows) {
                let param = {
                    params: {
                        page:1,
                        row:1000,
                        machineId:$that.workLicenseLineMapInfo.machineId,
                        uploadDate:$that.workLicenseLineMapInfo.uploadDate
                    }
                };

                //发送get请求
                vc.http.apiGet('/workLicense.listWorkLicensePos',
                    param,
                    function (json, res) {
                        let _json = JSON.parse(json);
                        $that.workLicenseLineMapInfo.pos = _json.data;
                        $that._initMap();
                    }, function (errInfo, error) {
                        console.log('请求失败处理');
                    }
                );
            },
            _listWorkLicenseMachines: function (_page, _rows) {
                let param = {
                    params: {
                        page:1,
                        row:1,
                        machineId:$that.workLicenseLineMapInfo.machineId
                    }
                };

                //发送get请求
                vc.http.apiGet('/workLicense.listWorkLicenseMachine',
                    param,
                    function (json, res) {
                        let _json = JSON.parse(json);
                        $that.workLicenseLineMapInfo.staffName = _json.data[0].staffName;
                       
                    }, function (errInfo, error) {
                        console.log('请求失败处理');
                    }
                );
            },

            

        }
    });
})(window.vc);
