(function(vc, vm) {

    vc.extends({
        data: {
            editAccessControlInfo: {
                machineId: '',
                machineCode: '',
                machineName: '',
                machineIp: '',
                machineMac: '',
                direction: '',
                implBean: '',
                locationId: '',
                locationName: '',
                promptSwitch: '',
                    normalPrompt: '',
                    owePrompt: '',
                locations: [],
                hms: [],
                monitorId:'',
                monitors:[],
                platformId: '',
                callingNum: '',
                remark:'',
                lat: '',
                lng: ''
            }
        },
        _initMethod: function() {

        },
        _initEvent: function() {
            vc.on('editAccessControl', 'openEditAccessControlModal', function(_params) {
                $that.refreshEditAccessControlInfo();
                $('#editAccessControlModel').modal('show');
                vc.copyObject(_params, $that.editAccessControlInfo);
                $that._listEditMachineLocations();
                $that._listEditHms();
                $that._listEditMonitors();
                $that.editAccessControlInfo.communityId = vc.getCurrentCommunity().communityId;
                // 将 addMap 容器移入当前 edit modal
                setTimeout(function () {
                    $('#addMapContainer').parent().appendTo('#editMapContainer');
                    vc.emit('addMap', 'showMap', $that.editAccessControlInfo);
                    // 延迟等待地图就绪后创建初始标记（通过 addMap.js 暴露的 _map 引用）
                    if ($that.editAccessControlInfo.lat && $that.editAccessControlInfo.lng) {
                        setTimeout(function () {
                            var container = document.getElementById('addMapContainer');
                            if (container && container._map) {
                                var pos = new TMap.LatLng(
                                    parseFloat($that.editAccessControlInfo.lat),
                                    parseFloat($that.editAccessControlInfo.lng)
                                );
                                new TMap.MultiMarker({
                                    styleId: "marker",
                                    position: pos,
                                    map: container._map,
                                    id: "edit_pos"
                                });
                            }
                        }, 1500);
                    }
                }, 1000)
            });
        },
        methods: {
            editAccessControlValidate: function() {
                return vc.validate.validate({
                    editAccessControlInfo: $that.editAccessControlInfo
                }, {
                    'editAccessControlInfo.machineCode': [{
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
                    'editAccessControlInfo.machineName': [{
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
                    'editAccessControlInfo.machineIp': [{
                        limit: "maxLength",
                        param: "64",
                        errInfo: "设备IP不能超过64"
                    }, ],
                    'editAccessControlInfo.machineMac': [{
                        limit: "maxLength",
                        param: "64",
                        errInfo: "设备mac不能超过64"
                    }, ],
                    'editAccessControlInfo.direction': [{
                        limit: "required",
                        param: "",
                        errInfo: "设备方向进出不能为空"
                    }, ],
                    'editAccessControlInfo.implBean': [{
                            limit: "required",
                            param: "",
                            errInfo: "门禁厂家不能为空"
                        },
                        {
                            limit: "maxLength",
                            param: "30",
                            errInfo: "门禁厂家不能超过30"
                        },
                    ],
                    'editAccessControlInfo.locationId': [{
                        limit: "required",
                        param: "",
                        errInfo: "位置ID不能为空"
                    }],
                    'editAccessControlInfo.machineId': [{
                        limit: "required",
                        param: "",
                        errInfo: "编号不能为空"
                    }]

                });
            },
            _editAccessControl: function() {
                if (!$that.editAccessControlValidate()) {
                    vc.toast(vc.validate.errInfo);
                    return;
                }

                vc.http.apiPost(
                    '/accessControl.updateAccessControl',
                    JSON.stringify($that.editAccessControlInfo), {
                        emulateJSON: true
                    },
                    function(json, res) {
                        //vm.menus = vm.refreshMenuActive(JSON.parse(json),0);
                        let _json = JSON.parse(json);
                        if (_json.code == 0) {
                            //关闭model
                            $('#editAccessControlModel').modal('hide');
                            vc.emit('accessControlManage', 'listAccessControl', {});
                            return;
                        }
                        vc.toast(_json.msg);
                    },
                    function(errInfo, error) {
                        console.log('请求失败处理');

                        vc.toast(errInfo);
                    });
            },
            refreshEditAccessControlInfo: function() {
                $that.editAccessControlInfo = {
                    machineId: '',
                    machineCode: '',
                    machineName: '',
                    machineIp: '',
                    machineMac: '',
                    direction: '',
                    implBean: '',
                    locationId: '',
                    locationName: '',
                    promptSwitch: '',
                    normalPrompt: '',
                    owePrompt: '',
                    locations: [],
                    hms: [],
                    monitorId:'',
                    monitors:[],
                    platformId: '',
                    callingNum: '',
                    remark:'',
                    lat: '',
                    lng: ''
                }
            },
            _listEditMachineLocations: function(_page, _rows) {

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
                    function(json, res) {
                        let _json = JSON.parse(json);
                        $that.editAccessControlInfo.locations = _json.data;

                    },
                    function(errInfo, error) {
                        console.log('请求失败处理');
                    }
                );
            },

            _listEditHms: function(_page, _rows) {

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
                    function(json, res) {
                        let _json = JSON.parse(json);
                        $that.editAccessControlInfo.hms = _json.data;

                    },
                    function(errInfo, error) {
                        console.log('请求失败处理');
                    }
                );
            },
            _listEditMonitors: function (_page, _rows) {
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
                        $that.editAccessControlInfo.monitors = _json.data;

                    },
                    function (errInfo, error) {
                        console.log('请求失败处理');
                    }
                );
            },
        }
    });

})(window.vc, window.$that);