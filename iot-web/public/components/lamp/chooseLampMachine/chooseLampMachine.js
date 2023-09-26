(function(vc){
    vc.extends({
        propTypes: {
           emitChooseLampMachine:vc.propTypes.string,
           emitLoadData:vc.propTypes.string
        },
        data:{
            chooseLampMachineInfo:{
                lampMachines:[],
                selectLampMachineIdList: [],
                _currentLampMachineName:'',
                _currentLampMachineCode:'',
                _currentLampLocationName:'',
            }
        },
        _initMethod:function(){
        },
        _initEvent:function(){
            vc.on('chooseLampMachine','openChooseLampMachineModel',function(_param){
                $('#chooseLampMachineModel').modal('show');
                $that._refreshChooseLampMachineInfo();
                $that._loadAllLampMachineInfo(-1,10);
            });
        },
        methods:{
            _loadAllLampMachineInfo:function(_page,_row){
                var param = {
                    params:{
                        page:_page,
                        row:_row,
                        communityId: vc.getCurrentCommunity().communityId,
                        machineName: $that.chooseLampMachineInfo._currentLampMachineName,
                        machineCode: $that.chooseLampMachineInfo._currentLampMachineCode,
                        locationName: $that.chooseLampMachineInfo._currentLampLocationName,
                    }
                };

                //发送get请求
               vc.http.apiGet('/lampMachine.listLampMachine',
                   param,
                   function(json){
                      var _lampMachineInfo = JSON.parse(json);
                      $that.chooseLampMachineInfo.lampMachines = _lampMachineInfo.data;
                   },function(){
                      console.log('请求失败处理');
                   }
               );
            },
            queryLampMachines:function(){
                $that._loadAllLampMachineInfo(-1,10);
            },
            reset:function () {
                $that._refreshChooseLampMachineInfo();
                $that.queryLampMachines();
            },
            _refreshChooseLampMachineInfo:function(){
                $that.chooseLampMachineInfo._currentLampMachineName = "";
                $that.chooseLampMachineInfo._currentLampMachineCode = "";
                $that.chooseLampMachineInfo._currentLampLocationName = "";
            },
            selectAll: function (e) {
                var _selectLampMachineIdList = $that.chooseLampMachineInfo.selectLampMachineIdList;
                var checkObj = document.querySelectorAll('.checkItem'); // 获取所有checkbox项
                if (e.target.checked) { // 判定全选checkbox的勾选状态
                    for (var i = 0; i < checkObj.length; i++) {
                        if (!checkObj[i].checked) { // 将未勾选的checkbox选项push到绑定数组中
                            $that.chooseLampMachineInfo.selectLampMachineIdList.push(checkObj[i].value);
                        }
                    }
                } else { // 如果是去掉全选则清空checkbox选项绑定数组
                    $that.chooseLampMachineInfo.selectLampMachineIdList = [];
                }
            },
            _submitSelectLampMachine: function () {
                vc.emit($props.emitLoadData,'listLampMachineData', $that.chooseLampMachineInfo.selectLampMachineIdList);
                $('#chooseLampMachineModel').modal('hide');
            },
        }

    });
})(window.vc);
