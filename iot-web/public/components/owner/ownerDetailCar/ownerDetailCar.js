/**
 入驻小区
 **/
(function (vc) {
    var DEFAULT_PAGE = 1;
    var DEFAULT_ROWS = 10;
    vc.extends({
        data: {
            ownerDetailCarInfo: {
                cars: [],
                ownerId:'',
                roomNum: '',
                allOweFeeAmount:'0'
            }
        },
        _initMethod: function () {
        },
        _initEvent: function () {
            vc.on('ownerDetailCar', 'switch', function (_data) {
                $that.ownerDetailCarInfo.ownerId = _data.ownerId;
                $that._loadOwnerDetailCarData(DEFAULT_PAGE, DEFAULT_ROWS);
            });
            vc.on('ownerDetailCar', 'paginationPlus', 'page_event',
                function (_currentPage) {
                    $that._loadOwnerDetailCarData(_currentPage, DEFAULT_ROWS);
                });
            vc.on('ownerDetailCar', 'notify', function (_data) {
                $that._loadOwnerDetailCarData(DEFAULT_PAGE,DEFAULT_ROWS);
            })
        },
        methods: {
            _loadOwnerDetailCarData: function (_page, _row) {
                let param = {
                    params: {
                        communityId: vc.getCurrentCommunity().communityId,
                        ownerId:$that.ownerDetailCarInfo.ownerId,
                        page:_page,
                        row:_row
                    }
                };
               
                //发送get请求
                vc.http.apiGet('/ownerCar.queryOwnerCars',
                    param,
                    function (json) {
                        let _roomInfo = JSON.parse(json);
                        $that.ownerDetailCarInfo.cars = _roomInfo.data;
                        vc.emit('ownerDetailCar', 'paginationPlus', 'init', {
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
            _qureyOwnerDetailCar: function () {
                $that._loadOwnerDetailCarData(DEFAULT_PAGE, DEFAULT_ROWS);
            },
        }
    });
})(window.vc);