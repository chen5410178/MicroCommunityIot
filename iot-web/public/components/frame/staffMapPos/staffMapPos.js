/**
 入驻小区
 **/
 import WorkLicenseLineMapApi from 'api/map/workLicenseLineMapApi.js';

 (function (vc) {
    vc.extends({
        data: {
            staffMapPosInfo: {
                staffId:'',
            }
        },
        _initMethod: function () {
            $that._loadDataPrivileges();
        },
        _initEvent: function () {
            vc.on('staffMapPos', '_showStaffMap', function (_param) {
                vc.copyObject(_param,$that.staffDetailInfo)
                $that._listWorkLicensePos();
            });
        },
        methods: {
            _listWorkLicensePos: function (_page, _rows) {
                let _uploadDate = vc.dateFormat(new Date());
                let param = {
                    params: {
                        page: 1,
                        row: 1000,
                        staffId: $that.staffMapPosInfo.staffId,
                        uploadDate: _uploadDate
                    }
                };

                //发送get请求
                vc.http.apiGet('/workLicense.listWorkLicensePos',
                    param,
                    function (json, res) {
                        let _json = JSON.parse(json);
                        let _pos = _json.data;
                        let _tmpLon = 0;
                        _pos.forEach(_m => {
                            _tmpLon = _m.lon;
                            _m.lon = _m.lat;
                            _m.lat = _tmpLon;
                        });

                        let map = new WorkLicenseLineMapApi();
                        map.initMap(_pos, 'wlLineMap');
                    }, function (errInfo, error) {
                        console.log('请求失败处理');
                    }
                );
            },

        }
    });
})(window.vc);
