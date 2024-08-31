(function(vc,vm){

    vc.extends({
        data:{
            deleteCallingPlatformInfo:{

            }
        },
         _initMethod:function(){

         },
         _initEvent:function(){
             vc.on('deleteCallingPlatform','openDeleteCallingPlatformModal',function(_params){

                vc.component.deleteCallingPlatformInfo = _params;
                $('#deleteCallingPlatformModel').modal('show');

            });
        },
        methods:{
            deleteCallingPlatform:function(){
                vc.component.deleteCallingPlatformInfo.communityId=vc.getCurrentCommunity().communityId;
                vc.http.apiPost(
                    'callingPlatform.deleteCallingPlatform',
                    JSON.stringify(vc.component.deleteCallingPlatformInfo),
                    {
                        emulateJSON:true
                     },
                     function(json,res){
                        //vm.menus = vm.refreshMenuActive(JSON.parse(json),0);
                        let _json = JSON.parse(json);
                        if (_json.code == 0) {
                            //关闭model
                            $('#deleteCallingPlatformModel').modal('hide');
                            vc.emit('callingPlatformManage','listCallingPlatform',{});
                            return ;
                        }
                        vc.message(_json.msg);
                     },
                     function(errInfo,error){
                        console.log('请求失败处理');
                        vc.message(json);

                     });
            },
            closeDeleteCallingPlatformModel:function(){
                $('#deleteCallingPlatformModel').modal('hide');
            }
        }
    });

})(window.vc,window.vc.component);
