/**
    入驻小区
**/
(function (vc) {
    var DEFAULT_PAGE = 1;
    var DEFAULT_ROWS = 10;
    vc.extends({
        data: {
            passQrcodeInfo: {
                passQrcodes: [],
                states:[],
                total: 0,
                records: 1,
                moreCondition: false,
                pqId: '',
                conditions: {
                    personName: '',
                    personTel: '',
                    state: '',
                    queryStartTime:'',
                    queryEndTime:''
                }
            }
        },
        _initMethod: function () {
            $that._listPassQrcodes(DEFAULT_PAGE, DEFAULT_ROWS);
            $that.passQrcodeInfo.states = [
                {
                    statusCd: '',
                    name: '全部'
                }
            ]
            vc.getDict('person_face', 'state', function (_data) {
                _data.forEach(item => {
                    $that.passQrcodeInfo.states.push(item);
                })
            })
            vc.initDateTime('queryStartTime',function(_value){
                $that.passQrcodeInfo.conditions.queryStartTime = _value;
            });
            vc.initDateTime('queryEndTime',function(_value){
                $that.passQrcodeInfo.conditions.queryEndTime = _value;
            });
        },
        _initEvent: function () {

            vc.on('passQrcode', 'listPassQrcode', function (_param) {
                $that._listPassQrcodes(DEFAULT_PAGE, DEFAULT_ROWS);
            });
            vc.on('pagination', 'page_event', function (_currentPage) {
                $that._listPassQrcodes(_currentPage, DEFAULT_ROWS);
            });
        },
        methods: {
            _listPassQrcodes: function (_page, _rows) {

                $that.passQrcodeInfo.conditions.page = _page;
                $that.passQrcodeInfo.conditions.row = _rows;
                $that.passQrcodeInfo.conditions.communityId = vc.getCurrentCommunity().communityId;
                var param = {
                    params: $that.passQrcodeInfo.conditions
                };

                //发送get请求
                vc.http.apiGet('/passcode.listPassQrcode',
                    param,
                    function (json, res) {
                        let _json = JSON.parse(json);
                        $that.passQrcodeInfo.total = _json.total;
                        $that.passQrcodeInfo.records = _json.records;
                        $that.passQrcodeInfo.passQrcodes = _json.data;
                        vc.emit('pagination', 'init', {
                            total: $that.passQrcodeInfo.records,
                            currentPage: _page,
                            dataCount:_json.total
                        });
                    }, function (errInfo, error) {
                        console.log('请求失败处理');
                    }
                );
            },
            _openAuditPassQrcodeModel: function (_passQrcode) {
                vc.emit('auditPassQrcode', 'openAuditPassQrcodeModal', _passQrcode);
            },
            _openDeletePassQrcodeModel: function (_passQrcode) {
                vc.emit('deletePassQrcode', 'openDeletePassQrcodeModal', _passQrcode);
            },
            _queryPassQrcodeMethod: function () {
                $that._listPassQrcodes(DEFAULT_PAGE, DEFAULT_ROWS);

            },
            _viewPasscode:function(_passQrcode){
                vc.emit('viewQrCode', 'openQrCodeModal',{
                    title:'通行码',
                    url:_passQrcode.qrcode,
                    remark:_passQrcode.personName
                })
            },
            _moreCondition: function () {
                if ($that.passQrcodeInfo.moreCondition) {
                    $that.passQrcodeInfo.moreCondition = false;
                } else {
                    $that.passQrcodeInfo.moreCondition = true;
                }
            },
            _switchState:function(_state){
                $that.passQrcodeInfo.conditions.state = _state.statusCd;
                $that._listPassQrcodes(DEFAULT_PAGE, DEFAULT_ROWS);
            },
        }
    });
})(window.vc);
