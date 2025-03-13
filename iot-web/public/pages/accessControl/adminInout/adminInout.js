/**
    入驻小区
**/
(function(vc) {
    var DEFAULT_PAGE = 1;
    var DEFAULT_ROWS = 10;
    vc.extends({
        data: {
            adminInoutInfo: {
                adminInouts: [],
                accessControls: [],
                openTypeCds: [{
                    openTypeName: '全部',
                    openTypeCd: '',
                }, {
                    openTypeName: '人脸开门',
                    openTypeCd: '1000',
                }, {
                    openTypeName: '钥匙开门',
                    openTypeCd: '2000',
                }, {
                    openTypeName: '二维码开门',
                    openTypeCd: '3000',
                }, {
                    openTypeName: '密码开门',
                    openTypeCd: '4000',
                }],
                total: 0,
                records: 1,
                moreCondition: false,
                inoutId: '',
                conditions: {
                    machineId: '',
                    name: '',
                    openTypeCd: '',
                    tel: '',
                    state: '',
                    queryStartTime:'',
                    queryEndTime:'',
                    communityId:'',
                }
            }
        },
        _initMethod: function() {
            vc.on('selectAdminCommunity','changeCommunity',function(_community){
                $that.adminInoutInfo.conditions.communityId = _community.communityId;
                $that._listAccessControls();
                $that._listAdminInouts(DEFAULT_PAGE, DEFAULT_ROWS);
            })
            $that._listAccessControls();
            $that._listAdminInouts(DEFAULT_PAGE, DEFAULT_ROWS);
            vc.initDateTime('queryStartTime',function(_value){
                $that.adminInoutInfo.conditions.queryStartTime = _value;
            });
            vc.initDateTime('queryEndTime',function(_value){
                $that.adminInoutInfo.conditions.queryEndTime = _value;
            });
        },
        _initEvent: function() {

            vc.on('adminInout', 'listAdminInout', function(_param) {
                $that._listAdminInouts(DEFAULT_PAGE, DEFAULT_ROWS);
            });
            vc.on('pagination', 'page_event', function(_currentPage) {
                $that._listAdminInouts(_currentPage, DEFAULT_ROWS);
            });
        },
        methods: {
            _listAdminInouts: function(_page, _rows) {

                $that.adminInoutInfo.conditions.page = _page;
                $that.adminInoutInfo.conditions.row = _rows;

                let param = {
                    params: $that.adminInoutInfo.conditions
                };

                //发送get请求
                vc.http.apiGet('/accessControlInout.listAdminAccessControlInout',
                    param,
                    function(json, res) {
                        var _json = JSON.parse(json);
                        $that.adminInoutInfo.total = _json.total;
                        $that.adminInoutInfo.records = _json.records;
                        $that.adminInoutInfo.adminInouts = _json.data;
                        vc.emit('pagination', 'init', {
                            total: $that.adminInoutInfo.records,
                            currentPage: _page,
                            dataCount:_json.total
                        });
                    },
                    function(errInfo, error) {
                        console.log('请求失败处理');
                    }
                );
            },

            _queryAdminInoutMethod: function() {
                $that._listAdminInouts(DEFAULT_PAGE, DEFAULT_ROWS);

            },
            swatchOpenTypeCd: function(_openType) {
                $that.adminInoutInfo.conditions.openTypeCd = _openType.openTypeCd;
                $that._listAdminInouts(DEFAULT_PAGE, DEFAULT_ROWS);
            },
            _viewOwnerFace: function(_url) {
                vc.emit('viewImage', 'showImage', {
                    url: _url
                });
            },

            _listAccessControls: function(_page, _rows) {

                let param = {
                    params: {
                        page: 1,
                        row: 100,
                        communityId: $that.adminInoutInfo.conditions.communityId
                    }
                };
                $that.adminInoutInfo.accessControls = [];

                //发送get请求
                vc.http.apiGet('/accessControl.listAdminAccessControl',
                    param,
                    function(json, res) {
                        let _json = JSON.parse(json);
                        $that.adminInoutInfo.accessControls = _json.data;
                    },
                    function(errInfo, error) {
                        console.log('请求失败处理');
                    }
                );
            },


        }
    });
})(window.vc);