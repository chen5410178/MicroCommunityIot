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

                let _community = vc.getCurrentCommunity();
                if (_community && _community.lat) {
                    let _acLat = $that.editAccessControlInfo.lat;
                    let _acLng = $that.editAccessControlInfo.lng;
                    if (!_acLat || _acLat == '39.907881821019075' || _acLat == '39.916527') {
                        $that.editAccessControlInfo.lat = _community.lat;
                    }
                    if (!_acLng || _acLng == '116.39803241981451' || _acLng == '116.397128') {
                        $that.editAccessControlInfo.lng = _community.lng;
                    }
                }
                if (!$that.editAccessControlInfo.lat || !$that.editAccessControlInfo.lng) {
                    let _communityId = $that.editAccessControlInfo.communityId;
                    if (_communityId) {
                        vc.http.apiGet('/community.listCommunitys',
                            { params: { communityId: _communityId, page: 1, row: 1 } },
                            function (json) {
                                let _data = JSON.parse(json).data;
                                if (_data && _data.length > 0) {
                                    let _c = _data[0];
                                    if (!_c.lat && _c.mapY) { _c.lat = _c.mapY; }
                                    if (!_c.lng && _c.mapX) { _c.lng = _c.mapX; }
                                    if (_c.lat) { $that.editAccessControlInfo.lat = _c.lat; }
                                    if (_c.lng) { $that.editAccessControlInfo.lng = _c.lng; }
                                }
                            }, function () {});
                    }
                }

                setTimeout(function () {
                    $('#addMapContainer').parent().appendTo('#editMapContainer');
                    vc.emit('addMap', 'showMap', $that.editAccessControlInfo);
                }, 500)
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