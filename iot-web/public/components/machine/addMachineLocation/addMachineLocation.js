(function (vc) {

    vc.extends({
        propTypes: {
            callBackListener: vc.propTypes.string, //父组件名称
            callBackFunction: vc.propTypes.string //父组件监听方法
        },
        data: {
            addMachineLocationInfo: {
                locationId: '',
                locationName: '',
                locationType: '',
                locationObjName: '',
                locationObjId: '',
                floorId: '',
                unitId: '',
                floors: [],
                units: []
            }
        },
        _initMethod: function () {

        },
        _initEvent: function () {
            vc.on('addMachineLocation', 'openAddMachineLocationModal', function () {
                $that._loadFloors();
                $('#addMachineLocationModel').modal('show');
            });
        },
        methods: {
            addMachineLocationValidate() {
                return vc.validate.validate({
                    addMachineLocationInfo: $that.addMachineLocationInfo
                }, {
                    'addMachineLocationInfo.locationName': [
                        {
                            limit: "required",
                            param: "",
                            errInfo: "位置名称不能为空"
                        },
                        {
                            limit: "maxLength",
                            param: "100",
                            errInfo: "位置名称不能超过100"
                        },
                    ],
                    'addMachineLocationInfo.locationType': [
                        {
                            limit: "required",
                            param: "",
                            errInfo: "位置类型不能为空"
                        },
                        {
                            limit: "maxLength",
                            param: "12",
                            errInfo: "位置类型不能超过12"
                        },
                    ],
                    'addMachineLocationInfo.locationObjName': [
                        {
                            limit: "required",
                            param: "",
                            errInfo: "对象ID不能为空"
                        },
                        {
                            limit: "maxLength",
                            param: "256",
                            errInfo: "对象ID不能超过256"
                        },
                    ],
                });
            },
            saveMachineLocationInfo: function () {
                let _locationType = $that.addMachineLocationInfo.locationType;
                if(_locationType == '1000'){
                    $that.addMachineLocationInfo.locationObjId = vc.getCurrentCommunity().communityId;
                    $that.addMachineLocationInfo.locationObjName = vc.getCurrentCommunity().name;
                }
                if(_locationType == '6000'){
                    $that.addMachineLocationInfo.locationObjId = $that.addMachineLocationInfo.floorId;
                    let _floors = $that.addMachineLocationInfo.floors;
                    _floors.forEach(item => {
                        if(item.floorId == $that.addMachineLocationInfo.floorId){
                            $that.addMachineLocationInfo.locationObjName = item.floorName;
                        }
                    });
                }
                if(_locationType == '2000'){
                    $that.addMachineLocationInfo.locationObjId = $that.addMachineLocationInfo.unitId;
                    let _units = $that.addMachineLocationInfo.units;
                    _units.forEach(item => {
                        if(item.unitId == $that.addMachineLocationInfo.unitId){
                            $that.addMachineLocationInfo.locationObjName = item.unitNum;
                        }
                    });
                }
                if (!$that.addMachineLocationValidate()) {
                    vc.toast(vc.validate.errInfo);
                    return;
                }

                $that.addMachineLocationInfo.communityId = vc.getCurrentCommunity().communityId;
                //不提交数据将数据 回调给侦听处理
                vc.http.apiPost(
                    '/location.saveMachineLocation',
                    JSON.stringify($that.addMachineLocationInfo),
                    {
                        emulateJSON: true
                    },
                    function (json, res) {
                        //vm.menus = vm.refreshMenuActive(JSON.parse(json),0);
                        let _json = JSON.parse(json);
                        if (_json.code == 0) {
                            //关闭model
                            $('#addMachineLocationModel').modal('hide');
                            $that.clearAddMachineLocationInfo();
                            vc.emit('machineLocation', 'listMachineLocation', {});
                            return;
                        }
                        vc.toast(_json.msg);

                    },
                    function (errInfo, error) {
                        console.log('请求失败处理');

                        vc.toast(errInfo);

                    });
            },
            clearAddMachineLocationInfo: function () {
                $that.addMachineLocationInfo = {
                    locationName: '',
                    locationType: '',
                    locationObjName: '',
                    locationObjId: '',
                    floorId: '',
                    unitId: '',
                    floors: [],
                    units: []
                };
            },
            _loadFloors:function(){
               
                let param = {
                    params: {
                        communityId:vc.getCurrentCommunity().communityId,
                        page:1,
                        row:100
                    }
                };

                //发送get请求
                vc.http.apiGet('/floor.queryFloors',
                    param,
                    function (json, res) {
                        let _json = JSON.parse(json);
                        $that.addMachineLocationInfo.floors = _json.data;
                    }, function (errInfo, error) {
                        console.log('请求失败处理');
                    }
                );

            },
            _loadUnits:function(){
                let param = {
                    params: {
                        communityId:vc.getCurrentCommunity().communityId,
                        page:1,
                        row:100,
                        floorId:$that.addMachineLocationInfo.floorId
                    }
                };

                //发送get请求
                vc.http.apiGet('/unit.queryUnits',
                    param,
                    function (json, res) {
                        let _json = JSON.parse(json);
                        $that.addMachineLocationInfo.units = _json;
                    }, function (errInfo, error) {
                        console.log('请求失败处理');
                    }
                );
            }
        }
    });

})(window.vc);
