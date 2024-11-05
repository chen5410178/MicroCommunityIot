/**
    入驻小区
**/
(function (vc) {
    var DEFAULT_PAGE = 1;
    var DEFAULT_ROWS = 10;
    vc.extends({
        data: {
            machineCollectInfo: {
                collects: [],
                total: 0,
                records: 1,
                moreCondition: false,
                collectId: '',
                conditions: {
                    collectId: '',
                    name: '',
                    address: '',
                }
            }
        },
        _initMethod: function () {
            $that._listMachineCollects(DEFAULT_PAGE, DEFAULT_ROWS);
        },
        _initEvent: function () {

            vc.on('machineCollect', 'listMachineCollect', function (_param) {
                $that._listMachineCollects(DEFAULT_PAGE, DEFAULT_ROWS);
            });
            vc.on('pagination', 'page_event', function (_currentPage) {
                $that._listMachineCollects(_currentPage, DEFAULT_ROWS);
            });
        },
        methods: {
            _listMachineCollects: function (_page, _rows) {

                $that.machineCollectInfo.conditions.page = _page;
                $that.machineCollectInfo.conditions.row = _rows;
                $that.machineCollectInfo.conditions.communityId = vc.getCurrentCommunity().communityId;
                let param = {
                    params: $that.machineCollectInfo.conditions
                };

                //发送get请求
                vc.http.apiGet('/monitor.listMonitorCollect',
                    param,
                    function (json, res) {
                        let _json = JSON.parse(json);
                        $that.machineCollectInfo.total = _json.total;
                        $that.machineCollectInfo.records = _json.records;
                        $that.machineCollectInfo.collects = _json.data;
                        vc.emit('pagination', 'init', {
                            total: $that.machineCollectInfo.records,
                            currentPage: _page
                        });
                    }, function (errInfo, error) {
                        console.log('请求失败处理');
                    }
                );
            },
            _openAddMachineCollectModal: function () {
                let _item = {};
                _item.callback = function (_machine) {
                    $that._saveMachineCollect(_machine);
                }
                vc.emit('selectVideoMachine', 'openSelectVideo', _item);
            },
            _openDeleteMachineCollectModel: function (_machineCollect) {
                vc.emit('deleteMachineCollect', 'openDeleteMachineCollectModal', _machineCollect);
            },
            _saveMachineCollect: function (_machine) {
                let _data={
                    machineId:_machine.machineId,
                    machineName:_machine.machineName,
                    machineCode:_machine.machineCode,
                    communityId:_machine.communityId
                }
                vc.http.apiPost(
                    '/monitor.saveMonitorCollect',
                    JSON.stringify(_data),
                    {
                        emulateJSON: true
                    },
                    function (json, res) {
                        //vm.menus = vm.refreshMenuActive(JSON.parse(json),0);
                        let _json = JSON.parse(json);
                        vc.toast(_json.msg);
                        if (_json.code == 0) {
                            vc.emit('machineCollect', 'listMachineCollect', {});
                            return;
                        }
                        vc.toast(_json.msg);
                    },
                    function (errInfo, error) {
                        console.log('请求失败处理');
                        vc.toast(json);

                    });
            },
            _queryMachineCollectMethod: function () {
                $that._listMachineCollects(DEFAULT_PAGE, DEFAULT_ROWS);

            }


        }
    });
})(window.vc);
