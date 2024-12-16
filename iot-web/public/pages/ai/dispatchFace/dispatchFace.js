/**
    入驻小区
**/
(function (vc) {
    var DEFAULT_PAGE = 1;
    var DEFAULT_ROWS = 10;
    vc.extends({
        data: {
            dispatchFaceInfo: {
                dispatchFaces: [],
                total: 0,
                records: 1,
                moreCondition: false,
                personId: '',
                conditions: {
                    personId: '',
                    personName: '',
                    communityId: '',
                    personLink:'',
                    personType:'dispatch'
                }
            }
        },
        _initMethod: function () {
            $that._listDispatchFaces(DEFAULT_PAGE, DEFAULT_ROWS);

        },
        _initEvent: function () {

            vc.on('dispatchFace', 'listDispatchFace', function (_param) {
                $that._listDispatchFaces(DEFAULT_PAGE, DEFAULT_ROWS);
            });
            vc.on('pagination', 'page_event', function (_currentPage) {
                $that._listDispatchFaces(_currentPage, DEFAULT_ROWS);
            });
        },
        methods: {
            _listDispatchFaces: function (_page, _rows) {

                $that.dispatchFaceInfo.conditions.page = _page;
                $that.dispatchFaceInfo.conditions.row = _rows;
                $that.dispatchFaceInfo.conditions.communityId = vc.getCurrentCommunity().communityId;
                let param = {
                    params: $that.dispatchFaceInfo.conditions
                };

                //发送get请求
                vc.http.apiGet('/ai.getPersonFace',
                    param,
                    function (json, res) {
                        let _json = JSON.parse(json);
                        $that.dispatchFaceInfo.total = _json.total;
                        $that.dispatchFaceInfo.records = _json.records;
                        $that.dispatchFaceInfo.dispatchFaces = _json.data;
                        vc.emit('pagination', 'init', {
                            total: $that.dispatchFaceInfo.records,
                            currentPage: _page
                        });
                    }, function (errInfo, error) {
                        console.log('请求失败处理');
                    }
                );
            },
            _openAddDispatchFaceModal: function () {
                vc.emit('addDispatchFace', 'openAddDispatchFaceModal', {});
            },
            _openEditDispatchFaceModel: function (_dispatchFace) {
                vc.emit('editDispatchFace', 'openEditDispatchFaceModal', _dispatchFace);
            },
            _openDeleteDispatchFaceModel: function (_dispatchFace) {
                vc.emit('deleteDispatchFace', 'openDeleteDispatchFaceModal', _dispatchFace);
            },
            _queryDispatchFaceMethod: function () {
                $that._listDispatchFaces(DEFAULT_PAGE, DEFAULT_ROWS);

            },
            _viewDispatchFace: function (_url) {
                vc.emit('viewImage', 'showImage', {
                    url: _url
                });
            },
            _moreCondition: function () {
                if ($that.dispatchFaceInfo.moreCondition) {
                    $that.dispatchFaceInfo.moreCondition = false;
                } else {
                    $that.dispatchFaceInfo.moreCondition = true;
                }
            }


        }
    });
})(window.vc);
