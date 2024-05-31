(function(vc,vm){

    vc.extends({
        data:{
            deleteTelMachineUserInfo:{

            }
        },
         _initMethod:function(){

         },
         _initEvent:function(){
             vc.on('deleteTelMachineUser','openDeleteTelMachineUserModal',function(_params){

                $that.deleteTelMachineUserInfo = _params;
                $('#deleteTelMachineUserModel').modal('show');

            });
        },
        methods:{
            deleteTelMachineUser:function(){
                $that.deleteTelMachineUserInfo.communityId=vc.getCurrentCommunity().communityId;
                vc.http.apiPost(
                    '/telMachineUser.deleteTelMachineUser',
                    JSON.stringify($that.deleteTelMachineUserInfo),
                    {
                        emulateJSON:true
                     },
                     function(json,res){
                        //vm.menus = vm.refreshMenuActive(JSON.parse(json),0);
                        let _json = JSON.parse(json);
                        if (_json.code == 0) {
                            //关闭model
                            $('#deleteTelMachineUserModel').modal('hide');
                            vc.emit('telMachineUser','listTelMachineUser',{});
                            return ;
                        }
                        vc.toast(_json.msg);
                     },
                     function(errInfo,error){
                        console.log('请求失败处理');
                        vc.toast(json);

                     });
            },
            closeDeleteTelMachineUserModel:function(){
                $('#deleteTelMachineUserModel').modal('hide');
            }
        }
    });

})(window.vc,window.$that);
