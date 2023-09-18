(function(vc,vm){

    vc.extends({
        data:{
            deleteLiftMachineFactorySpecInfo:{

            }
        },
         _initMethod:function(){

         },
         _initEvent:function(){
             vc.on('deleteLiftMachineFactorySpec','openDeleteLiftMachineFactorySpecModal',function(_params){

                vc.component.deleteLiftMachineFactorySpecInfo = _params;
                $('#deleteLiftMachineFactorySpecModel').modal('show');

            });
        },
        methods:{
            deleteLiftMachineFactorySpec:function(){
                vc.component.deleteLiftMachineFactorySpecInfo.communityId=vc.getCurrentCommunity().communityId;
                vc.http.apiPost(
                    'liftMachineFactorySpec.deleteLiftMachineFactorySpec',
                    JSON.stringify(vc.component.deleteLiftMachineFactorySpecInfo),
                    {
                        emulateJSON:true
                     },
                     function(json,res){
                        //vm.menus = vm.refreshMenuActive(JSON.parse(json),0);
                        let _json = JSON.parse(json);
                        if (_json.code == 0) {
                            //关闭model
                            $('#deleteLiftMachineFactorySpecModel').modal('hide');
                            vc.emit('liftMachineFactorySpecManage','listLiftMachineFactorySpec',{});
                            return ;
                        }
                        vc.message(_json.msg);
                     },
                     function(errInfo,error){
                        console.log('请求失败处理');
                        vc.message(json);

                     });
            },
            closeDeleteLiftMachineFactorySpecModel:function(){
                $('#deleteLiftMachineFactorySpecModel').modal('hide');
            }
        }
    });

})(window.vc,window.vc.component);
