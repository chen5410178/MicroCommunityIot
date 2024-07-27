(function(vc,vm){

    vc.extends({
        data:{
            deleteMonitorMachinePersonInfo:{

            }
        },
         _initMethod:function(){

         },
         _initEvent:function(){
             vc.on('deleteMonitorMachinePerson','openDeleteMonitorMachinePersonModal',function(_params){

                vc.component.deleteMonitorMachinePersonInfo = _params;
                $('#deleteMonitorMachinePersonModel').modal('show');

            });
        },
        methods:{
            deleteMonitorMachinePerson:function(){
                vc.component.deleteMonitorMachinePersonInfo.communityId=vc.getCurrentCommunity().communityId;
                vc.http.apiPost(
                    'monitorMachinePerson.deleteMonitorMachinePerson',
                    JSON.stringify(vc.component.deleteMonitorMachinePersonInfo),
                    {
                        emulateJSON:true
                     },
                     function(json,res){
                        //vm.menus = vm.refreshMenuActive(JSON.parse(json),0);
                        let _json = JSON.parse(json);
                        if (_json.code == 0) {
                            //关闭model
                            $('#deleteMonitorMachinePersonModel').modal('hide');
                            vc.emit('monitorMachinePersonManage','listMonitorMachinePerson',{});
                            return ;
                        }
                        vc.message(_json.msg);
                     },
                     function(errInfo,error){
                        console.log('请求失败处理');
                        vc.message(json);

                     });
            },
            closeDeleteMonitorMachinePersonModel:function(){
                $('#deleteMonitorMachinePersonModel').modal('hide');
            }
        }
    });

})(window.vc,window.vc.component);
