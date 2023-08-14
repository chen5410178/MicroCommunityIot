(function(vc,vm){

    vc.extends({
        data:{
            deleteMachineLocationInfo:{

            }
        },
         _initMethod:function(){

         },
         _initEvent:function(){
             vc.on('deleteMachineLocation','openDeleteMachineLocationModal',function(_params){

                $that.deleteMachineLocationInfo = _params;
                $('#deleteMachineLocationModel').modal('show');

            });
        },
        methods:{
            deleteMachineLocation:function(){
                $that.deleteMachineLocationInfo.communityId=vc.getCurrentCommunity().communityId;
                vc.http.apiPost(
                    '/location.deleteMachineLocation',
                    JSON.stringify($that.deleteMachineLocationInfo),
                    {
                        emulateJSON:true
                     },
                     function(json,res){
                        //vm.menus = vm.refreshMenuActive(JSON.parse(json),0);
                        let _json = JSON.parse(json);
                        if (_json.code == 0) {
                            //关闭model
                            $('#deleteMachineLocationModel').modal('hide');
                            vc.emit('machineLocation','listMachineLocation',{});
                            return ;
                        }
                        vc.toast(_json.msg);
                     },
                     function(errInfo,error){
                        console.log('请求失败处理');
                        vc.toast(json);

                     });
            },
            closeDeleteMachineLocationModel:function(){
                $('#deleteMachineLocationModel').modal('hide');
            }
        }
    });

})(window.vc,window.$that);
