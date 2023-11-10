/**
 入驻小区
 **/
(function (vc) {
    var DEFAULT_PAGE = 1;
    var DEFAULT_ROWS = 10;
    vc.extends({
        data: {
            ownerDetailChargeOrderInfo: {
                orders: [],
                ownerId:'',
                link:'',
            }
        },
        _initMethod: function () {
        },
        _initEvent: function () {
            vc.on('ownerDetailChargeOrder', 'switch', function (_data) {
                $that.ownerDetailChargeOrderInfo.ownerId = _data.ownerId;
                $that.ownerDetailChargeOrderInfo.link = _data.link;

                $that._loadOwnerDetailChargeOrderData(DEFAULT_PAGE, DEFAULT_ROWS);
            });
            vc.on('ownerDetailChargeOrder', 'paginationPlus', 'page_event',
                function (_currentPage) {
                    $that._loadOwnerDetailChargeOrderData(_currentPage, DEFAULT_ROWS);
                });
            vc.on('ownerDetailChargeOrder', 'notify', function (_data) {
                $that._loadOwnerDetailChargeOrderData(DEFAULT_PAGE,DEFAULT_ROWS);
            })
        },
        methods: {
            _loadOwnerDetailChargeOrderData: function (_page, _row) {
                let param = {
                    params: {
                        communityId: vc.getCurrentCommunity().communityId,
                        personTel:$that.ownerDetailChargeOrderInfo.link,
                        page:_page,
                        row:_row
                    }
                };
               
                //发送get请求
                vc.http.apiGet('/chargeMachine.listChargeMachineOrder',
                    param,
                    function (json) {
                        let _roomInfo = JSON.parse(json);
                        $that.ownerDetailChargeOrderInfo.orders = _roomInfo.data;
                        vc.emit('ownerDetailChargeOrder', 'paginationPlus', 'init', {
                            total: _roomInfo.records,
                            dataCount: _roomInfo.total,
                            currentPage: _page
                        });
                    },
                    function () {
                        console.log('请求失败处理');
                    }
                );
            },
            //查询
            _qureyOwnerDetailChargeOrder: function () {
                $that._loadOwnerDetailChargeOrderData(DEFAULT_PAGE, DEFAULT_ROWS);
            },
        }
    });
})(window.vc);