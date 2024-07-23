(function (vc) {

    vc.extends({
        data: {
            addAccessControlInfo: {
                machineId: '',
                machineCode: '',
                machineName: '',
                machineIp: '',
                machineMac: '',
                direction: '',
                implBean: '',
                locationId: '',
                locationName: '',
                promptSwitch: 'OFF',
                normalPrompt: '',
                owePrompt: '',
                locations: [],
                hms: [],
                monitors:[],
                monitorId:'',
            }
        },
        _initMethod: function () {

        },
        _initEvent: function () {
            vc.on('addAccessControl', 'openAddAccessControlModal', function () {
                $that._listAddMachineLocations();
                $that._listAddHms();
                $that._listAddMonitors();
                $('#addAccessControlModel').modal('show');
            });
        },
        methods: {
            addAccessControlValidate() {
                return vc.validate.validate({
                    addAccessControlInfo: $that.addAccessControlInfo
                }, {
                    'addAccessControlInfo.machineCode': [{
                        limit: "required",
                        param: "",
                        errInfo: "设备编码不能为空"
                    },
                    {
                        limit: "maxLength",
                        param: "64",
                        errInfo: "设备编码不能超过30"
                    },
                    ],
                    'addAccessControlInfo.machineName': [{
                        limit: "required",
                        param: "",
                        errInfo: "设备名称不能为空"
                    },
                    {
                        limit: "maxLength",
                        param: "200",
                        errInfo: "设备名称不能超过200"
                    },
                    ],
                    'addAccessControlInfo.machineIp': [{
                        limit: "maxLength",
                        param: "64",
                        errInfo: "设备IP不能超过64"
                    },],
                    'addAccessControlInfo.machineMac': [{
                        limit: "maxLength",
                        param: "64",
                        errInfo: "设备mac不能超过64"
                    },],
                    'addAccessControlInfo.direction': [{
                        limit: "required",
                        param: "",
                        errInfo: "设备方向不能为空"
                    },],
                    'addAccessControlInfo.implBean': [{
                        limit: "required",
                        param: "",
                        errInfo: "门禁厂家不能为空"
                    },],
                    'addAccessControlInfo.locationId': [{
                        limit: "required",
                        param: "",
                        errInfo: "位置不能为空"
                    },],
                });
            },
            saveAccessControlInfo: function () {
                if (!$that.addAccessControlValidate()) {
                    vc.toast(vc.validate.errInfo);
                    return;
                }
                $that.addAccessControlInfo.communityId = vc.getCurrentCommunity().communityId;
                vc.http.apiPost(
                    '/accessControl.saveAccessControl',
                    JSON.stringify($that.addAccessControlInfo), {
                    emulateJSON: true
                },
                    function (json, res) {
                        //vm.menus = vm.refreshMenuActive(JSON.parse(json),0);
                        let _json = JSON.parse(json);
                        if (_json.code == 0) {
                            //关闭model
                            $('#addAccessControlModel').modal('hide');
                            $that.clearAddAccessControlInfo();
                            vc.emit('accessControlManage', 'listAccessControl', {});

                            return;
                        }
                        vc.toast(_json.msg);

                    },
                    function (errInfo, error) {
                        console.log('请求失败处理');

                        vc.toast(errInfo);

                    });
            },
            clearAddAccessControlInfo: function () {
                $that.addAccessControlInfo = {
                    machineCode: '',
                    machineName: '',
                    machineIp: '',
                    machineMac: '',
                    direction: '',
                    implBean: '',
                    locationId: '',
                    locationName: '',
                    promptSwitch: 'OFF',
                    normalPrompt: '',
                    owePrompt: '',
                    locations: [],
                    hms: [],
                    monitors:[],
                    monitorId:'',
                };
            },
            _listAddMachineLocations: function (_page, _rows) {

                let param = {
                    params: {
                        page: 1,
                        row: 50,
                        communityId: vc.getCurrentCommunity().communityId
                    }
                };

                //发送get请求
                vc.http.apiGet('/location.listMachineLocation',
                    param,
                    function (json, res) {
                        let _json = JSON.parse(json);
                        $that.addAccessControlInfo.locations = _json.data;

                    },
                    function (errInfo, error) {
                        console.log('请求失败处理');
                    }
                );
            },

            _listAddHms: function (_page, _rows) {

                let param = {
                    params: {
                        page: 1,
                        row: 50,
                        hmType: '1001'
                    }
                };

                //发送get请求
                vc.http.apiGet('/hm.listHardwareManufacturer',
                    param,
                    function (json, res) {
                        let _json = JSON.parse(json);
                        $that.addAccessControlInfo.hms = _json.data;

                    },
                    function (errInfo, error) {
                        console.log('请求失败处理');
                    }
                );
            },
            _listAddMonitors: function (_page, _rows) {
                let param = {
                    params: {
                        page: 1,
                        row: 50,
                        communityId:vc.getCurrentCommunity().communityId
                    }
                };

                //发送get请求
                vc.http.apiGet('/monitorMachine.listMonitorMachine',
                    param,
                    function (json, res) {
                        let _json = JSON.parse(json);
                        $that.addAccessControlInfo.monitors = _json.data;

                    },
                    function (errInfo, error) {
                        console.log('请求失败处理');
                    }
                );
            },
        }
    });

})(window.vc);