(function(vc){
    vc.extends({
        propTypes: {
           emitChooseMonitorMachine:vc.propTypes.string,
           emitLoadData:vc.propTypes.string
        },
        data:{
            chooseMonitorMachineInfo:{
                selectMonitorMachineList: [],
                machineName: '',
                locationName: '',
                maId: ''
            },
            monitorMachineList: [],
        },
        _initMethod:function(){

        },
        _initEvent:function(){
            vc.on('chooseMonitorMachine', 'openChooseMonitorMachineModel',function(){
                $that._queryMonitorMachines();
                $('#chooseMonitorMachineModel').modal('show');
            });
        },
        methods:{
            selectAll: function (e) {
                var _selectMonitorMachineList = $that.chooseMonitorMachineInfo.selectMonitorMachineList;
                var checkObj = document.querySelectorAll('.checkItem'); // 获取所有checkbox项
                if (e.target.checked) { // 判定全选checkbox的勾选状态
                    for (var i = 0; i < checkObj.length; i++) {
                        if (!checkObj[i].checked) { // 将未勾选的checkbox选项push到绑定数组中
                            $that.chooseMonitorMachineInfo.selectMonitorMachineList.push(checkObj[i]._value);
                        }
                    }
                } else { // 如果是去掉全选则清空checkbox选项绑定数组
                    $that.chooseMonitorMachineInfo.selectMonitorMachineList = [];
                }
            },
            _submitSelectMonitorMachineList: function () {
                vc.emit($props.emitLoadData,'listMonitorMachineData', $that.chooseMonitorMachineInfo.selectMonitorMachineList);
                $('#chooseMonitorMachineModel').modal('hide');
            },
            _queryMonitorMachines: function () {
                var param = {
                    params: {
                        page: -1,
                        row: 100,
                        communityId: vc.getCurrentCommunity().communityId,
                        machineName: $that.chooseMonitorMachineInfo.machineName,
                        locationName: $that.chooseMonitorMachineInfo.locationName,
                        maId: $that.chooseMonitorMachineInfo.maId
                    }
                };
                //发送get请求
                vc.http.apiGet('/monitorMachine.listMonitorMachine',
                    param,
                    function (json, res) {
                        var _monitorMachineManageInfo = JSON.parse(json);
                        $that.monitorMachineList = _monitorMachineManageInfo.data;
                    }, function (errInfo, error) {
                        console.log('请求失败处理');
                    }
                );
            },
            _resetConditions: function () {
                $that.chooseMonitorMachineInfo.machineName = '';
                $that.chooseMonitorMachineInfo.locationName = '';
                $that.chooseMonitorMachineInfo.maId = '';
                $that._queryMonitorMachines();
            }
        }
    });
})(window.vc);
