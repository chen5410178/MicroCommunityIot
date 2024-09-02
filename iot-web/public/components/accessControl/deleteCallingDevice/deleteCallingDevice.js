(function(vc,vm){

    vc.extends({
        data:{
            deleteCallingDeviceInfo:{

            }
        },
         _initMethod:function(){

         },
         _initEvent:function(){
             vc.on('deleteCallingDevice','openDeleteCallingDeviceModal',function(_params){

                vc.component.deleteCallingDeviceInfo = _params;
                $('#deleteCallingDeviceModel').modal('show');

            });
        },
        methods:{
            deleteCallingDevice:function(){
                vc.component.deleteCallingDeviceInfo.communityId=vc.getCurrentCommunity().communityId;
                vc.http.apiPost(
                    'callingDevice.deleteCallingDevice',
                    JSON.stringify(vc.component.deleteCallingDeviceInfo),
                    {
                        emulateJSON:true
                     },
                     function(json,res){
                        //vm.menus = vm.refreshMenuActive(JSON.parse(json),0);
                        let _json = JSON.parse(json);
                        if (_json.code == 0) {
                            //关闭model
                            $('#deleteCallingDeviceModel').modal('hide');
                            vc.emit('callingDeviceManage','listCallingDevice',{});
                            return ;
                        }
                        vc.message(_json.msg);
                     },
                     function(errInfo,error){
                        console.log('请求失败处理');
                        vc.message(json);

                     });
            },
            closeDeleteCallingDeviceModel:function(){
                $('#deleteCallingDeviceModel').modal('hide');
            }
        }
    });

})(window.vc,window.vc.component);
