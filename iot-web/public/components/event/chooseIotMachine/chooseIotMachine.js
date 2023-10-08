(function(vc){
    vc.extends({
        propTypes: {
           emitChooseIotMachine:vc.propTypes.string,
           emitLoadData:vc.propTypes.string
        },
        data:{
            chooseIotMachineInfo:{
                selectIotMachineList: [],
                _currentIotMachineName:'',
            },
            objTypeList: [],
            iotMachineList: [],
            serviceCode: '',
            eventObjectList: [],
        },
        _initMethod:function(){
            vc.getDict('event_object', 'obj_type', function (_data) {
                $that.objTypeList = _data;
            });
        },
        _initEvent:function(){
            vc.on('chooseIotMachine','openChooseIotMachineModel',function(_ruleId){
                $('#chooseIotMachineModel').modal('show');
            });
            vc.on('eventObject', 'listMachine', function () {
                $that._listEventObjects($that.addEventObjectInfo.ruleId);
            });
        },
        methods:{
            /*_listIotMachineListByMachineName:function(_page,_row){
                let param = {
                    params: {
                        page: -1,
                        row: 1000,
                        communityId: vc.getCurrentCommunity().communityId,
                        machineName: $that.chooseIotMachineInfo._currentIotMachineName,
                    }
                };
                //发送get请求
                vc.http.apiGet($that.serviceCode,
                    param,
                    function (json, res) {
                        let _json = JSON.parse(json);
                        $that.iotMachineList = [];
                        if ($that.eventObjectList.length === 0) {
                            $that.iotMachineList = _json.data;
                        } else {
                            lo:for (let i = 0; i < _json.data.length; i++) {
                                for (let j = 0; j < $that.eventObjectList.length; j++) {
                                    if ($that.eventObjectList[j].objectId === _json.data[i].machineId) {
                                        continue lo;
                                    }
                                }
                                $that.iotMachineList.push(_json.data[i]);
                            }
                        }
                    }, function (errInfo, error) {
                        console.log('请求失败处理');
                    }
                );
            },
            queryIotMachines:function(){
                $that._listIotMachineListByMachineName();
            },
            reset:function () {
                $that.chooseIotMachineInfo._currentIotMachineName = "";
                $that._listIotMachineListByMachineName();
            },*/
            selectAll: function (e) {
                var _selectIotMachineList = $that.chooseIotMachineInfo.selectIotMachineList;
                var checkObj = document.querySelectorAll('.checkItem'); // 获取所有checkbox项
                if (e.target.checked) { // 判定全选checkbox的勾选状态
                    for (var i = 0; i < checkObj.length; i++) {
                        if (!checkObj[i].checked) { // 将未勾选的checkbox选项push到绑定数组中
                            $that.chooseIotMachineInfo.selectIotMachineList.push(checkObj[i]._value);
                        }
                    }
                } else { // 如果是去掉全选则清空checkbox选项绑定数组
                    $that.chooseIotMachineInfo.selectIotMachineList = [];
                }
            },
            _submitSelectIotMachine: function () {
                vc.emit($props.emitLoadData,'listIotMachineData', $that.chooseIotMachineInfo.selectIotMachineList);
                $('#chooseIotMachineModel').modal('hide');
            },
            _listIotMachineList: function () {

                let param = {
                    params: {
                        page: -1,
                        row: 1000,
                        communityId: vc.getCurrentCommunity().communityId,
                    }
                };
                //发送get请求
                vc.http.apiGet($that.serviceCode,
                    param,
                    function (json, res) {
                        let _json = JSON.parse(json);
                        $that.iotMachineList = [];
                        if ($that.eventObjectList.length === 0) {
                            $that.iotMachineList = _json.data;
                        } else {
                            lo:for (let i = 0; i < _json.data.length; i++) {
                                for (let j = 0; j < $that.eventObjectList.length; j++) {
                                    if ($that.eventObjectList[j].objectId === _json.data[i].machineId) {
                                        continue lo;
                                    }
                                }
                                $that.iotMachineList.push(_json.data[i]);
                            }
                        }
                        if ($that.iotMachineList.length === 0) {
                            vc.toast('该对象类型暂无对应设备或设备已全部绑定该规则');
                        }
                    }, function (errInfo, error) {
                        console.log('请求失败处理');
                    }
                );
            },
            _listEventObjects: function (_ruleId) {
                var param = {
                    params: {
                        page: -1,
                        row: 1000,
                        ruleId: _ruleId,
                        communityId: vc.getCurrentCommunity().communityId,
                        objType: $that.addEventObjectInfo.objType
                    }
                };

                //发送get请求
                vc.http.apiGet('/eventObject.listEventObject',
                    param,
                    function (json, res) {
                        var _eventObjectInfo = JSON.parse(json);
                        $that.eventObjectList = _eventObjectInfo.data;

                        for (var index = 0; index < $that.objTypeList.length; index++) {
                            if ($that.addEventObjectInfo.objType === $that.objTypeList[index].statusCd) {
                                $that.serviceCode = $that.objTypeList[index].description;
                                $that._listIotMachineList();
                                break;
                            }
                        }

                    }, function (errInfo, error) {
                        console.log('请求失败处理');
                    }
                );
            },
        }

    });
})(window.vc);
