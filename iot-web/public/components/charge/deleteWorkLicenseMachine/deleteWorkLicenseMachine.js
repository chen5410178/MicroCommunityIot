(function(vc,vm){

    vc.extends({
        data:{
            deleteWorkLicenseMachineInfo:{

            }
        },
         _initMethod:function(){

         },
         _initEvent:function(){
             vc.on('deleteWorkLicenseMachine','openDeleteWorkLicenseMachineModal',function(_params){

                $that.deleteWorkLicenseMachineInfo = _params;
                $('#deleteWorkLicenseMachineModel').modal('show');

            });
        },
        methods:{
            deleteWorkLicenseMachine:function(){
                $that.deleteWorkLicenseMachineInfo.communityId=vc.getCurrentCommunity().communityId;
                vc.http.apiPost(
                    'workLicenseMachine.deleteWorkLicenseMachine',
                    JSON.stringify($that.deleteWorkLicenseMachineInfo),
                    {
                        emulateJSON:true
                     },
                     function(json,res){
                        //vm.menus = vm.refreshMenuActive(JSON.parse(json),0);
                        let _json = JSON.parse(json);
                        if (_json.code == 0) {
                            //关闭model
                            $('#deleteWorkLicenseMachineModel').modal('hide');
                            vc.emit('workLicenseMachine','listWorkLicenseMachine',{});
                            return ;
                        }
                        vc.toast(_json.msg);
                     },
                     function(errInfo,error){
                        console.log('请求失败处理');
                        vc.toast(json);

                     });
            },
            closeDeleteWorkLicenseMachineModel:function(){
                $('#deleteWorkLicenseMachineModel').modal('hide');
            }
        }
    });

})(window.vc,window.$that);
