/**
    入驻小区
**/
(function (vc) {
    var DEFAULT_PAGE = 1;
    var DEFAULT_ROWS = 10;
    vc.extends({
        data: {
            liftCameraInfo: {
                liftCameras: [],
                total: 0,
                records: 1,
                moreCondition: false,
                lcId: '',
                conditions: {
                    cameraName: '',
                    cameraCode: '',
                    liftName: '',

                }
            }
        },
        _initMethod: function () {
            $that._listLiftCameras(DEFAULT_PAGE, DEFAULT_ROWS);
        },
        _initEvent: function () {

            vc.on('liftCamera', 'listLiftCamera', function (_param) {
                $that._listLiftCameras(DEFAULT_PAGE, DEFAULT_ROWS);
            });
            vc.on('pagination', 'page_event', function (_currentPage) {
                $that._listLiftCameras(_currentPage, DEFAULT_ROWS);
            });
        },
        methods: {
            _listLiftCameras: function (_page, _rows) {

                $that.liftCameraInfo.conditions.page = _page;
                $that.liftCameraInfo.conditions.row = _rows;
                var param = {
                    params: $that.liftCameraInfo.conditions
                };

                //发送get请求
                vc.http.apiGet('/liftCamera.listLiftCamera',
                    param,
                    function (json, res) {
                        let _json = JSON.parse(json);
                        $that.liftCameraInfo.total = _json.total;
                        $that.liftCameraInfo.records = _json.records;
                        $that.liftCameraInfo.liftCameras = _json.data;
                        vc.emit('pagination', 'init', {
                            total: $that.liftCameraInfo.records,
                            currentPage: _page
                        });
                    }, function (errInfo, error) {
                        console.log('请求失败处理');
                    }
                );
            },
            _openAddLiftCameraModal: function () {
                vc.emit('addLiftCamera', 'openAddLiftCameraModal', {});
            },
            _openEditLiftCameraModel: function (_liftCamera) {
                vc.emit('editLiftCamera', 'openEditLiftCameraModal', _liftCamera);
            },
            _openDeleteLiftCameraModel: function (_liftCamera) {
                vc.emit('deleteLiftCamera', 'openDeleteLiftCameraModal', _liftCamera);
            },
            _queryLiftCameraMethod: function () {
                $that._listLiftCameras(DEFAULT_PAGE, DEFAULT_ROWS);

            },
            _moreCondition: function () {
                if ($that.liftCameraInfo.moreCondition) {
                    $that.liftCameraInfo.moreCondition = false;
                } else {
                    $that.liftCameraInfo.moreCondition = true;
                }
            }


        }
    });
})(window.vc);
