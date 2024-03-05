(function(vc,vm){

    vc.extends({
        data:{
            deleteParkingSpaceMachineInfo:{

            }
        },
         _initMethod:function(){

         },
         _initEvent:function(){
             vc.on('deleteParkingSpaceMachine','openDeleteParkingSpaceMachineModal',function(_params){

                $that.deleteParkingSpaceMachineInfo = _params;
                $('#deleteParkingSpaceMachineModel').modal('show');

            });
        },
        methods:{
            deleteParkingSpaceMachine:function(){
                $that.deleteParkingSpaceMachineInfo.communityId=vc.getCurrentCommunity().communityId;
                vc.http.apiPost(
                    '/parkingSpaceMachine.deleteParkingSpaceMachine',
                    JSON.stringify($that.deleteParkingSpaceMachineInfo),
                    {
                        emulateJSON:true
                     },
                     function(json,res){
                        //vm.menus = vm.refreshMenuActive(JSON.parse(json),0);
                        let _json = JSON.parse(json);
                        if (_json.code == 0) {
                            //关闭model
                            $('#deleteParkingSpaceMachineModel').modal('hide');
                            vc.emit('parkingSpaceMachine','listParkingSpaceMachine',{});
                            return ;
                        }
                        vc.toast(_json.msg);
                     },
                     function(errInfo,error){
                        console.log('请求失败处理');
                        vc.toast(json);

                     });
            },
            closeDeleteParkingSpaceMachineModel:function(){
                $('#deleteParkingSpaceMachineModel').modal('hide');
            }
        }
    });

})(window.vc,window.$that);
