(function(vc,vm){

    vc.extends({
        data:{
            restartChargeMachineInfo:{

            }
        },
         _initMethod:function(){

         },
         _initEvent:function(){
             vc.on('restartChargeMachine','openRestartChargeMachineModal',function(_params){

                $that.restartChargeMachineInfo = _params;
                $('#restartChargeMachineModel').modal('show');

            });
        },
        methods:{
            restartChargeMachine:function(){
                $that.restartChargeMachineInfo.communityId=vc.getCurrentCommunity().communityId;
                vc.http.apiPost(
                    '/chargeMachine.restartChargeMachine',
                    JSON.stringify($that.restartChargeMachineInfo),
                    {
                        emulateJSON:true
                     },
                     function(json,res){
                        //vm.menus = vm.refreshMenuActive(JSON.parse(json),0);
                        let _json = JSON.parse(json);
                        if (_json.code == 0) {
                            //关闭model
                            $('#restartChargeMachineModel').modal('hide');
                            vc.emit('chargeMachineManage','listChargeMachine',{});
                            return ;
                        }
                        vc.toast(_json.msg);
                     },
                     function(errInfo,error){
                        console.log('请求失败处理');
                        vc.toast(json);

                     });
            },
            closeDeleteChargeMachineModel:function(){
                $('#restartChargeMachineModel').modal('hide');
            }
        }
    });

})(window.vc,window.$that);
