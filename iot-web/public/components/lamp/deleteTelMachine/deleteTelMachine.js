(function(vc,vm){

    vc.extends({
        data:{
            deleteTelMachineInfo:{

            }
        },
         _initMethod:function(){

         },
         _initEvent:function(){
             vc.on('deleteTelMachine','openDeleteTelMachineModal',function(_params){

                $that.deleteTelMachineInfo = _params;
                $('#deleteTelMachineModel').modal('show');

            });
        },
        methods:{
            deleteTelMachine:function(){
                $that.deleteTelMachineInfo.communityId=vc.getCurrentCommunity().communityId;
                vc.http.apiPost(
                    '/telMachine.deleteTelMachine',
                    JSON.stringify($that.deleteTelMachineInfo),
                    {
                        emulateJSON:true
                     },
                     function(json,res){
                        //vm.menus = vm.refreshMenuActive(JSON.parse(json),0);
                        let _json = JSON.parse(json);
                        if (_json.code == 0) {
                            //关闭model
                            $('#deleteTelMachineModel').modal('hide');
                            vc.emit('telMachine','listTelMachine',{});
                            return ;
                        }
                        vc.toast(_json.msg);
                     },
                     function(errInfo,error){
                        console.log('请求失败处理');
                        vc.toast(json);

                     });
            },
            closeDeleteTelMachineModel:function(){
                $('#deleteTelMachineModel').modal('hide');
            }
        }
    });

})(window.vc,window.$that);
