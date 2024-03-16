(function(vc,vm){

    vc.extends({
        data:{
            cleanMeterMachineInfo:{

            }
        },
         _initMethod:function(){

         },
         _initEvent:function(){
             vc.on('cleanMeterMachine','openCleanMeterMachineModal',function(_params){

                vc.component.cleanMeterMachineInfo = _params;
                $('#cleanMeterMachineModel').modal('show');

            });
        },
        methods:{
            cleanMeterMachine:function(){
                let _meterMachine = vc.component.cleanMeterMachineInfo;
                let tmpMeterMachine = {};
                tmpMeterMachine.address=_meterMachine.address;
                tmpMeterMachine.communityId=_meterMachine.communityId;
                tmpMeterMachine.roomId=_meterMachine.roomId;
                tmpMeterMachine.machineId=_meterMachine.machineId;
                tmpMeterMachine.controlType=_meterMachine.controlType;
                tmpMeterMachine.meterType=_meterMachine.meterType;
                tmpMeterMachine.implBean=_meterMachine.implBean;
                tmpMeterMachine.implBeanName=_meterMachine.implBeanName;
                tmpMeterMachine.curDegrees=_meterMachine.curDegrees;
                tmpMeterMachine.prestoreDegrees=_meterMachine.prestoreDegrees;
                vc.component.cleanMeterMachineInfo.communityId=vc.getCurrentCommunity().communityId;
                vc.http.apiPost(
                    '/meterMachine.cleanMeterMachine',
                    JSON.stringify(tmpMeterMachine),
                    {
                        emulateJSON:true
                     },
                     function(json,res){
                        //vm.menus = vm.refreshMenuActive(JSON.parse(json),0);
                        let _json = JSON.parse(json);
                        if (_json.code == 0) {
                            //关闭model
                            $('#cleanMeterMachineModel').modal('hide');
                            vc.emit('meterMachineManage','listMeterMachine',{});
                            return ;
                        }
                        vc.message(_json.msg);
                     },
                     function(errInfo,error){
                        console.log('请求失败处理');
                        vc.message(json);

                     });
            },
            closeCleanMeterMachineModel:function(){
                $('#cleanMeterMachineModel').modal('hide');
            }
        }
    });

})(window.vc,window.vc.component);
