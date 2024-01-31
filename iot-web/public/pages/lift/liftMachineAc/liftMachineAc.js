/**
    入驻小区
**/
(function (vc) {
    var DEFAULT_PAGE = 1;
    var DEFAULT_ROWS = 10;
    vc.extends({
        data: {
            liftMachineAcInfo: {
                liftMachineAcs: [],
                machines: [],
                total: 0,
                records: 1,
                moreCondition: false,
                lmaId: '',
                conditions: {
                    machineId: '',
                    acMachineId: '',
                    acMachineName: '',
                    acMachineCode: '',

                }
            }
        },
        _initMethod: function () {
            $that._listLift();
        },
        _initEvent: function () {

            vc.on('liftMachineAc', 'listLiftMachineAc', function (_param) {
                $that._listLiftMachineAcs(DEFAULT_PAGE, DEFAULT_ROWS);
            });
            vc.on('pagination', 'page_event', function (_currentPage) {
                $that._listLiftMachineAcs(_currentPage, DEFAULT_ROWS);
            });
        },
        methods: {
            _listLift: function () {
                let param = {
                    params: {
                        page: 1,
                        row: 100,
                        communityId: vc.getCurrentCommunity().communityId
                    }
                };
                //发送get请求
                vc.http.apiGet('/liftMachine.listLiftMachine',
                    param,
                    function (json, res) {
                        let _json = JSON.parse(json);
                        $that.liftMachineAcInfo.machines = _json.data;
                        if (_json.data && _json.data.length > 0) {
                            $that._swatchLiftMachine(_json.data[0]);
                        }
                    }, function (errInfo, error) {
                        console.log('请求失败处理');
                    }
                );
            },
            _swatchLiftMachine: function (_lift) {
                $that.liftMachineAcInfo.conditions.machineId = _lift.machineId;
                $that._listLiftMachineAcs(DEFAULT_PAGE, DEFAULT_ROWS);
            },
            _listLiftMachineAcs: function (_page, _rows) {

                $that.liftMachineAcInfo.conditions.page = _page;
                $that.liftMachineAcInfo.conditions.row = _rows;
                $that.liftMachineAcInfo.conditions.communityId = vc.getCurrentCommunity().communityId;
                let param = {
                    params: $that.liftMachineAcInfo.conditions
                };

                //发送get请求
                vc.http.apiGet('/liftMachineAc.listLiftMachineAc',
                    param,
                    function (json, res) {
                        let _json = JSON.parse(json);
                        $that.liftMachineAcInfo.total = _json.total;
                        $that.liftMachineAcInfo.records = _json.records;
                        $that.liftMachineAcInfo.liftMachineAcs = _json.data;
                        vc.emit('pagination', 'init', {
                            total: $that.liftMachineAcInfo.records,
                            currentPage: _page
                        });
                    }, function (errInfo, error) {
                        console.log('请求失败处理');
                    }
                );
            },
            _openAddLiftMachineAcModal: function () {
                let _machineName = "";
                let _machineId = $that.liftMachineAcInfo.conditions.machineId;
                $that.liftMachineAcInfo.machines.forEach(item => {
                    if (_machineId == item.machineId) {
                        _machineName = item.machineName
                    }
                });
                vc.emit('addLiftMachineAc', 'openAddLiftMachineAcModal', {
                    machineId: _machineId,
                    machineName: _machineName,
                });
            },
            _openDeleteLiftMachineAcModel: function (_liftMachineAc) {
                vc.emit('deleteLiftMachineAc', 'openDeleteLiftMachineAcModal', _liftMachineAc);
            },
            _queryLiftMachineAcMethod: function () {
                $that._listLiftMachineAcs(DEFAULT_PAGE, DEFAULT_ROWS);
            },
            _toLiftDetail: function (_liftMachine) {
                vc.jumpToPage('/#/pages/lift/liftMachineDetail?machineId=' + _liftMachine.machineId);
            }


        }
    });
})(window.vc);
