/**
 入驻小区
 **/
(function (vc) {
    var DEFAULT_PAGE = 1;
    var DEFAULT_ROWS = 10;
    vc.extends({
        data: {
            roomDetailLockPersonInfo: {
                lockPersons: [],
                roomId:'',
                machineId:'',
            }
        },
        _initMethod: function () {
        },
        _initEvent: function () {
            vc.on('roomDetailLockPerson', 'switch', function (_data) {
                $that.roomDetailLockPersonInfo.roomId = _data.roomId;
                $that._loadRoomDetailLockPersonData(DEFAULT_PAGE, DEFAULT_ROWS);
            });
            vc.on('roomDetailLockPerson', 'paginationPlus', 'page_event',
                function (_currentPage) {
                    $that._loadRoomDetailLockPersonData(_currentPage, DEFAULT_ROWS);
                });
            vc.on('roomDetailLockPerson', 'notify', function (_data) {
                $that._loadRoomDetailLockPersonData(DEFAULT_PAGE,DEFAULT_ROWS);
            });
            vc.on('machineDetailLockPerson', 'switch', function (_data) {
                $that.roomDetailLockPersonInfo.machineId = _data.machineId;
                $that._loadMachineDetailLockPersonData(DEFAULT_PAGE, DEFAULT_ROWS);
            });
            vc.on('machineDetailLockPerson', 'paginationPlus', 'page_event', function (_currentPage) {
                $that._loadMachineDetailLockPersonData(_currentPage, DEFAULT_ROWS);
            });
        },
        methods: {
            _loadRoomDetailLockPersonData: function (_page, _row) {
                let param = {
                    params: {
                        communityId: vc.getCurrentCommunity().communityId,
                        roomId:$that.roomDetailLockPersonInfo.roomId,
                        page:_page,
                        row:_row
                    }
                };
               
                //发送get请求
                vc.http.apiGet('/lockPerson.listLockPerson',
                    param,
                    function (json) {
                        let _roomInfo = JSON.parse(json);
                        $that.roomDetailLockPersonInfo.lockPersons = _roomInfo.data;
                        vc.emit('roomDetailLockPerson', 'paginationPlus', 'init', {
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
            _qureyRoomDetailLockPerson: function () {
                $that._loadRoomDetailLockPersonData(DEFAULT_PAGE, DEFAULT_ROWS);
            },
            _loadMachineDetailLockPersonData: function (_page, _row) {
                let param = {
                    params: {
                        communityId: vc.getCurrentCommunity().communityId,
                        machineId:$that.roomDetailLockPersonInfo.machineId,
                        page:_page,
                        row:_row
                    }
                };

                //发送get请求
                vc.http.apiGet('/lockPerson.listLockPerson',
                    param,
                    function (json) {
                        let _machineInfo = JSON.parse(json);
                        $that.roomDetailLockPersonInfo.lockPersons = _machineInfo.data;
                        vc.emit('machineDetailLockPerson', 'paginationPlus', 'init', {
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