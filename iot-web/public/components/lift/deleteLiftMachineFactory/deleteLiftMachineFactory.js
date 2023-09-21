(function(vc,vm){

    vc.extends({
        data:{
            deleteLiftMachineFactoryInfo:{

            }
        },
         _initMethod:function(){

         },
         _initEvent:function(){
             vc.on('deleteLiftMachineFactory','openDeleteLiftMachineFactoryModal',function(_params){

                vc.component.deleteLiftMachineFactoryInfo = _params;
                $('#deleteLiftMachineFactoryModel').modal('show');

            });
        },
        methods:{
            deleteLiftMachineFactory:function(){
                vc.component.deleteLiftMachineFactoryInfo.communityId=vc.getCurrentCommunity().communityId;
                vc.http.apiPost(
                    'liftMachineFactory.deleteLiftMachineFactory',
                    JSON.stringify(vc.component.deleteLiftMachineFactoryInfo),
                    {
                        emulateJSON:true
                     },
                     function(json,res){
                        //vm.menus = vm.refreshMenuActive(JSON.parse(json),0);
                        let _json = JSON.parse(json);
                        if (_json.code === 0) {
                            //关闭model
                            $('#deleteLiftMachineFactoryModel').modal('hide');
                            vc.emit('liftMachineFactoryManage','listLiftMachineFactory',{});
                            return ;
                        }
                        vc.toast(_json.msg);
                     },
                     function(errInfo,error){
                        console.log('请求失败处理');
                        vc.toast(json);

                     });
            },
            closeDeleteLiftMachineFactoryModel:function(){
                $('#deleteLiftMachineFactoryModel').modal('hide');
            }
        }
    });

})(window.vc,window.vc.component);
