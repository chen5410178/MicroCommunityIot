(function(vc,vm){

    vc.extends({
        data:{
            deleteSpaceMachineFactoryInfo:{

            }
        },
         _initMethod:function(){

         },
         _initEvent:function(){
             vc.on('deleteSpaceMachineFactory','openDeleteSpaceMachineFactoryModal',function(_params){
                $that.deleteSpaceMachineFactoryInfo = _params;
                $('#deleteSpaceMachineFactoryModel').modal('show');
            });
        },
        methods:{
            deleteSpaceMachineFactory:function(){
                vc.http.apiPost(
                    '/spaceMachineFactory.deleteSpaceMachineFactory',
                    JSON.stringify($that.deleteSpaceMachineFactoryInfo),
                    {
                        emulateJSON:true
                     },
                     function(json,res){
                        //vm.menus = vm.refreshMenuActive(JSON.parse(json),0);
                        let _json = JSON.parse(json);
                        if (_json.code == 0) {
                            //关闭model
                            $('#deleteSpaceMachineFactoryModel').modal('hide');
                            vc.emit('spaceMachineFactory','listSpaceMachineFactory',{});
                            return ;
                        }
                        vc.toast(_json.msg);
                     },
                     function(errInfo,error){
                        console.log('请求失败处理');
                        vc.toast(json);

                     });
            },
            closeDeleteSpaceMachineFactoryModel:function(){
                $('#deleteSpaceMachineFactoryModel').modal('hide');
            }
        }
    });

})(window.vc,window.$that);
