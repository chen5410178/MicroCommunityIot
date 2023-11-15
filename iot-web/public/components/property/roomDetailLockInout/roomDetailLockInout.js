/**
 入驻小区
 **/
(function (vc) {
    var DEFAULT_PAGE = 1;
    var DEFAULT_ROWS = 10;
    vc.extends({
        data: {
            roomDetailLockInoutInfo: {
                inouts: [],
                roomId:'',
                machineId:'',
            }
        },
        _initMethod: function () {
        },
        _initEvent: function () {
            vc.on('roomDetailLockInout', 'switch', function (_data) {
                $that.roomDetailLockInoutInfo.roomId = _data.roomId;
                $that._loadRoomDetailLockInoutData(DEFAULT_PAGE, DEFAULT_ROWS);
            });
            vc.on('roomDetailLockInout', 'paginationPlus', 'page_event',
                function (_currentPage) {
                    $that._loadRoomDetailLockInoutData(_currentPage, DEFAULT_ROWS);
                });
            vc.on('roomDetailLockInout', 'notify', function (_data) {
                $that._loadRoomDetailLockInoutData(DEFAULT_PAGE,DEFAULT_ROWS);
            });
            vc.on('machineDetailLockInout', 'switch', function (_data) {
                $that.roomDetailLockInoutInfo.machineId = _data.machineId;
                $that._loadMachineDetailLockInoutData(DEFAULT_PAGE, DEFAULT_ROWS);
            });
            vc.on('machineDetailLockInout', 'paginationPlus', 'page_event', function (_currentPage) {
                $that._loadMachineDetailLockInoutData(_currentPage, DEFAULT_ROWS);
            });
        },
        methods: {
            _loadRoomDetailLockInoutData: function (_page, _row) {
                let param = {
                    params: {
                        communityId: vc.getCurrentCommunity().communityId,
                        roomId:$that.roomDetailLockInoutInfo.roomId,
                        page:_page,
                        row:_row
                    }
                };
               
                //发送get请求
                vc.http.apiGet('/lockInout.listLockInout',
                    param,
                    function (json) {
                        let _roomInfo = JSON.parse(json);
                        $that.roomDetailLockInoutInfo.inouts = _roomInfo.data;
                        vc.emit('roomDetailLockInout', 'paginationPlus', 'init', {
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
            _qureyRoomDetailLockInout: function () {
                $that._loadRoomDetailLockInoutData(DEFAULT_PAGE, DEFAULT_ROWS);
            },
            _loadMachineDetailLockInoutData: function (_page, _row) {
                let param = {
                    params: {
                        communityId: vc.getCurrentCommunity().communityId,
                        machineId:$that.roomDetailLockInoutInfo.machineId,
                        page:_page,
                        row:_row
                    }
                };

                //发送get请求
                vc.http.apiGet('/lockInout.listLockInout',
                    param,
                    function (json) {
                        let _machineInfo = JSON.parse(json);
                        $that.roomDetailLockInoutInfo.inouts = _machineInfo.data;
                        vc.emit('machineDetailLockInout', 'paginationPlus', 'init', {
                            total: _machineInfo.records,
                            dataCount: _machineInfo.total,
                            currentPage: _page
                        });
                    },
                    function () {
                        console.log('请求失败处理');
                    }
                );
            },
        }
    });
})(window.vc);