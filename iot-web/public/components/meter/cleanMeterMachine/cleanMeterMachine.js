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
                vc.component.cleanMeterMachineInfo.communityId=vc.getCurrentCommunity().communityId;
                vc.http.apiPost(
                    '/meterMachine.cleanMeterMachine',
                    JSON.stringify(vc.component.cleanMeterMachineInfo),
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
