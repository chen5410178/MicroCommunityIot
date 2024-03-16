(function(vc){
    vc.extends({
        propTypes: {
           emitChooseMeterMachineLog:vc.propTypes.string,
           emitLoadData:vc.propTypes.string
        },
        data:{
            chooseMeterMachineLogInfo:{
                meterMachineLogs:[],
                _currentMeterMachineLogName:'',
            }
        },
        _initMethod:function(){
        },
        _initEvent:function(){
            vc.on('chooseMeterMachineLog','openChooseMeterMachineLogModel',function(_param){
                $('#chooseMeterMachineLogModel').modal('show');
                vc.component._refreshChooseMeterMachineLogInfo();
                vc.component._loadAllMeterMachineLogInfo(1,10,'');
            });
        },
        methods:{
            _loadAllMeterMachineLogInfo:function(_page,_row,_name){
                var param = {
                    params:{
                        page:_page,
                        row:_row,
                        communityId:vc.getCurrentCommunity().communityId,
                        name:_name
                    }
                };

                //发送get请求
               vc.http.apiGet('meterMachineLog.listMeterMachineLogs',
                             param,
                             function(json){
                                var _meterMachineLogInfo = JSON.parse(json);
                                vc.component.chooseMeterMachineLogInfo.meterMachineLogs = _meterMachineLogInfo.meterMachineLogs;
                             },function(){
                                console.log('请求失败处理');
                             }
                           );
            },
            chooseMeterMachineLog:function(_meterMachineLog){
                if(_meterMachineLog.hasOwnProperty('name')){
                     _meterMachineLog.meterMachineLogName = _meterMachineLog.name;
                }
                vc.emit($props.emitChooseMeterMachineLog,'chooseMeterMachineLog',_meterMachineLog);
                vc.emit($props.emitLoadData,'listMeterMachineLogData',{
                    meterMachineLogId:_meterMachineLog.meterMachineLogId
                });
                $('#chooseMeterMachineLogModel').modal('hide');
            },
            queryMeterMachineLogs:function(){
                vc.component._loadAllMeterMachineLogInfo(1,10,vc.component.chooseMeterMachineLogInfo._currentMeterMachineLogName);
            },
            _refreshChooseMeterMachineLogInfo:function(){
                vc.component.chooseMeterMachineLogInfo._currentMeterMachineLogName = "";
            }
        }

    });
})(window.vc);
