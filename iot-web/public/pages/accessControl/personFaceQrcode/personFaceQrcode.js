/**
    入驻小区
**/
(function (vc) {
    var DEFAULT_PAGE = 1;
    var DEFAULT_ROWS = 10;
    vc.extends({
        data: {
            personFaceQrcodeInfo: {
                personFaceQrcodes: [],
                total: 0,
                records: 1,
                moreCondition: false,
                pfqId: '',
                conditions: {
                    qrcodeName: '',
                }
            }
        },
        _initMethod: function () {
            $that._listPersonFaceQrcodes(DEFAULT_PAGE, DEFAULT_ROWS);
        },
        _initEvent: function () {

            vc.on('personFaceQrcode', 'listPersonFaceQrcode', function (_param) {
                $that._listPersonFaceQrcodes(DEFAULT_PAGE, DEFAULT_ROWS);
            });
            vc.on('pagination', 'page_event', function (_currentPage) {
                $that._listPersonFaceQrcodes(_currentPage, DEFAULT_ROWS);
            });
        },
        methods: {
            _listPersonFaceQrcodes: function (_page, _rows) {
                $that.personFaceQrcodeInfo.conditions.page = _page;
                $that.personFaceQrcodeInfo.conditions.row = _rows;
                $that.personFaceQrcodeInfo.conditions.communityId = vc.getCurrentCommunity().communityId;
                let param = {
                    params: $that.personFaceQrcodeInfo.conditions
                };

                //发送get请求
                vc.http.apiGet('/personFace.listPersonFaceQrcode',
                    param,
                    function (json, res) {
                        let _json = JSON.parse(json);
                        $that.personFaceQrcodeInfo.total = _personFaceQrcodeInfo.total;
                        $that.personFaceQrcodeInfo.records = _personFaceQrcodeInfo.records;
                        $that.personFaceQrcodeInfo.personFaceQrcodes = _json.data;
                        vc.emit('pagination', 'init', {
                            total: $that.personFaceQrcodeInfo.records,
                            currentPage: _page
                        });
                    }, function (errInfo, error) {
                        console.log('请求失败处理');
                    }
                );
            },
            _openAddPersonFaceQrcodeModal: function () {
                vc.emit('addPersonFaceQrcode', 'openAddPersonFaceQrcodeModal', {});
            },
            _openEditPersonFaceQrcodeModel: function (_personFaceQrcode) {
                vc.emit('editPersonFaceQrcode', 'openEditPersonFaceQrcodeModal', _personFaceQrcode);
            },
            _openDeletePersonFaceQrcodeModel: function (_personFaceQrcode) {
                vc.emit('deletePersonFaceQrcode', 'openDeletePersonFaceQrcodeModal', _personFaceQrcode);
            },
            _queryPersonFaceQrcodeMethod: function () {
                $that._listPersonFaceQrcodes(DEFAULT_PAGE, DEFAULT_ROWS);

            },
            _moreCondition: function () {
                if ($that.personFaceQrcodeInfo.moreCondition) {
                    $that.personFaceQrcodeInfo.moreCondition = false;
                } else {
                    $that.personFaceQrcodeInfo.moreCondition = true;
                }
            }


        }
    });
})(window.vc);
