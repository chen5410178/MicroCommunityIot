(function (vc) {
    var default_row = 50;
    vc.extends({
        data: {
            selectVideoMachineInfo: {
                areas: [],
                machines: [],
                maId: '',
                machineId: '',
                curMachine: {}
            }
        },
        _initMethod: function () {
        },
        _initEvent: function () {
            vc.on('selectVideoMachine', 'openSelectVideo', function (_machine) {
                //查询公司信息
                $that._clearSelectVideoMachine();
                $('#selectVideoMachineModel').modal('show');
                $that.selectVideoMachineInfo.curMachine = _machine;

                $that._loadSelectAreas();

            });
            vc.on('selectVideoMachine', 'switchOrg', function (_param) {
                $that.loadStaff(_param);
            })
        },
        methods: {
            _loadSelectAreas: function () {
                let param = {
                    params: {
                        page: 1,
                        row: 100,
                        communityId: vc.getCurrentCommunity().communityId
                    }
                };
                //发送get请求
                vc.http.apiGet('/monitorArea.listMonitorArea',
                    param,
                    function (json) {
                        let _data = JSON.parse(json);
                        $that.selectVideoMachineInfo.areas = _data.data;
                        if (_data.data.length < 1) {
                            return;
                        }
                        $that._changeArea(_data.data[0])
                    },
                    function () {
                        console.log('请求失败处理');
                    }
                );
            },
            _loadSelectVideoMachines: function () {
                let param = {
                    params: {
                        page: 1,
                        row: 100,
                        communityId: vc.getCurrentCommunity().communityId,
                        maId: $that.selectVideoMachineInfo.maId,
                    }
                };
                //发送get请求
                vc.http.apiGet('/monitorMachine.listMonitorMachine',
                    param,
                    function (json) {
                        let _data = JSON.parse(json);
                        $that.selectVideoMachineInfo.machines = _data.data;
                    },
                    function () {
                        console.log('请求失败处理');
                    }
                );
            },
            _changeArea: function (_area) {
                $that.selectVideoMachineInfo.maId = _area.maId;
                $that._loadSelectVideoMachines();
            },
            _changeMachine: function (_machine) {
                $that.selectVideoMachineInfo.machineId = _machine.machineId;
                $that.selectVideoMachineInfo.curMachine.callback(_machine);
                $that._clearSelectVideoMachine();
                $('#selectVideoMachineModel').modal('hide');
            },
            _clearSelectVideoMachine: function () {
                $that.selectVideoMachineInfo = {
                    areas: [],
                    machines: [],
                    maId: '',
                    machineId: '',
                    curMachine: {}
                }
            }


        }
    });
})(window.vc);