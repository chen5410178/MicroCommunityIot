(function(vc,vm){

    vc.extends({
        data:{
            deleteLampMachineInfo:{

            }
        },
         _initMethod:function(){

         },
         _initEvent:function(){
             vc.on('deleteLampMachine','openDeleteLampMachineModal',function(_params){
                $that.deleteLampMachineInfo = _params;
                $('#deleteLampMachineModel').modal('show');
            });
        },
        methods:{
            deleteLampMachine:function(){
                $that.deleteLampMachineInfo.communityId=vc.getCurrentCommunity().communityId;
                vc.http.apiPost(
                    '/lampMachine.deleteLampMachine',
                    JSON.stringify($that.deleteLampMachineInfo),
                    {
                        emulateJSON:true
                     },
                     function(json,res){
                        let _json = JSON.parse(json);
                        if (_json.code === 0) {
                            //关闭model
                            $('#deleteLampMachineModel').modal('hide');
                            vc.emit('lampMachineManage','listLampMachine',{});
                            return ;
                        }
                        vc.toast(_json.msg);
                     },
                     function(errInfo,error){
                        console.log('请求失败处理');
                        vc.toast(json);
                     });
            },
            closeDeleteLampMachineModel:function(){
                $('#deleteLampMachineModel').modal('hide');
            }
        }
    });

})(window.vc,window.$that);
