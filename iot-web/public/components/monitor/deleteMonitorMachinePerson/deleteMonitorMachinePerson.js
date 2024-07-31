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

                $that.deleteMonitorMachinePersonInfo = _params;
                $('#deleteMonitorMachinePersonModel').modal('show');

            });
        },
        methods:{
            deleteMonitorMachinePerson:function(){
                $that.deleteMonitorMachinePersonInfo.communityId=vc.getCurrentCommunity().communityId;
                vc.http.apiPost(
                    '/monitor.deleteMonitorMachinePerson',
                    JSON.stringify($that.deleteMonitorMachinePersonInfo),
                    {
                        emulateJSON:true
                     },
                     function(json,res){
                        //vm.menus = vm.refreshMenuActive(JSON.parse(json),0);
                        let _json = JSON.parse(json);
                        if (_json.code == 0) {
                            //关闭model
                            $('#deleteMonitorMachinePersonModel').modal('hide');
                            vc.emit('monitorMachineStaff', 'listMonitorMachinePerson',{});
                            vc.emit('monitorMachineOwner', 'listMonitorMachinePerson',{});

                            
                            return ;
                        }
                        vc.toast(_json.msg);
                     },
                     function(errInfo,error){
                        console.log('请求失败处理');
                        vc.toast(json);

                     });
            },
            closeDeleteMonitorMachinePersonModel:function(){
                $('#deleteMonitorMachinePersonModel').modal('hide');
            }
        }
    });

})(window.vc,window.$that);
