(function(vc,vm){

    vc.extends({
        data:{
            deleteMeterMachineLogInfo:{

            }
        },
         _initMethod:function(){

         },
         _initEvent:function(){
             vc.on('deleteMeterMachineLog','openDeleteMeterMachineLogModal',function(_params){

                vc.component.deleteMeterMachineLogInfo = _params;
                $('#deleteMeterMachineLogModel').modal('show');

            });
        },
        methods:{
            deleteMeterMachineLog:function(){
                vc.component.deleteMeterMachineLogInfo.communityId=vc.getCurrentCommunity().communityId;
                vc.http.apiPost(
                    'meterMachineLog.deleteMeterMachineLog',
                    JSON.stringify(vc.component.deleteMeterMachineLogInfo),
                    {
                        emulateJSON:true
                     },
                     function(json,res){
                        //vm.menus = vm.refreshMenuActive(JSON.parse(json),0);
                        let _json = JSON.parse(json);
                        if (_json.code == 0) {
                            //关闭model
                            $('#deleteMeterMachineLogModel').modal('hide');
                            vc.emit('meterMachineLogManage','listMeterMachineLog',{});
                            return ;
                        }
                        vc.message(_json.msg);
                     },
                     function(errInfo,error){
                        console.log('请求失败处理');
                        vc.message(json);

                     });
            },
            closeDeleteMeterMachineLogModel:function(){
                $('#deleteMeterMachineLogModel').modal('hide');
            }
        }
    });

})(window.vc,window.vc.component);
