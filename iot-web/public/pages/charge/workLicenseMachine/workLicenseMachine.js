/**
    入驻小区
**/
(function (vc) {
    var DEFAULT_PAGE = 1;
    var DEFAULT_ROWS = 10;
    vc.extends({
        data: {
            workLicenseMachineInfo: {
                machines: [],
                total: 0,
                records: 1,
                moreCondition: false,
                machineId: '',
                conditions: {
                    machineName: '',
                    machineCode: '',
                    staffName: '',

                }
            }
        },
        _initMethod: function () {
            $that._listWorkLicenseMachines(DEFAULT_PAGE, DEFAULT_ROWS);
        },
        _initEvent: function () {

            vc.on('workLicenseMachine', 'listWorkLicenseMachine', function (_param) {
                $that._listWorkLicenseMachines(DEFAULT_PAGE, DEFAULT_ROWS);
            });
            vc.on('pagination', 'page_event', function (_currentPage) {
                $that._listWorkLicenseMachines(_currentPage, DEFAULT_ROWS);
            });
        },
        methods: {
            _listWorkLicenseMachines: function (_page, _rows) {

                $that.workLicenseMachineInfo.conditions.page = _page;
                $that.workLicenseMachineInfo.conditions.row = _rows;
                let param = {
                    params: $that.workLicenseMachineInfo.conditions
                };

                //发送get请求
                vc.http.apiGet('/workLicense.listWorkLicenseMachine',
                    param,
                    function (json, res) {
                        let _json = JSON.parse(json);
                        $that.workLicenseMachineInfo.total = _json.total;
                        $that.workLicenseMachineInfo.records = _json.records;
                        $that.workLicenseMachineInfo.machines = _json.data;
                        vc.emit('pagination', 'init', {
                            total: $that.workLicenseMachineInfo.records,
                            currentPage: _page
                        });
                    }, function (errInfo, error) {
                        console.log('请求失败处理');
                    }
                );
            },
            _openAddWorkLicenseMachineModal: function () {
               vc.jumpToPage('/#/pages/charge/addWorkLicenseMachine');
            },
            _openEditWorkLicenseMachineModel: function (_workLicenseMachine) {
                vc.jumpToPage('/#/pages/charge/editWorkLicenseMachine?machineId='+_workLicenseMachine.machineId);
            },
            _openDeleteWorkLicenseMachineModel: function (_workLicenseMachine) {
                vc.emit('deleteWorkLicenseMachine', 'openDeleteWorkLicenseMachineModal', _workLicenseMachine);
            },
            _queryWorkLicenseMachineMethod: function () {
                $that._listWorkLicenseMachines(DEFAULT_PAGE, DEFAULT_ROWS);
            },
            _openPps:function(_workLicenseMachine){
                vc.emit('worklicensePps', 'openWorklicensePpsModal', _workLicenseMachine);
            },
            _moreCondition: function () {
                if ($that.workLicenseMachineInfo.moreCondition) {
                    $that.workLicenseMachineInfo.moreCondition = false;
                } else {
                    $that.workLicenseMachineInfo.moreCondition = true;
                }
            }


        }
    });
})(window.vc);
