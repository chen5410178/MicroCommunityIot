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

                $that.deleteCallingPlatformInfo = _params;
                $('#deleteCallingPlatformModel').modal('show');

            });
        },
        methods:{
            deleteCallingPlatform:function(){
                $that.deleteCallingPlatformInfo.communityId=vc.getCurrentCommunity().communityId;
                vc.http.apiPost(
                    '/callingPlatform.deleteCallingPlatform',
                    JSON.stringify($that.deleteCallingPlatformInfo),
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
                        vc.toast(_json.msg);
                     },
                     function(errInfo,error){
                        console.log('请求失败处理');
                        vc.toast(json);

                     });
            },
            closeDeleteCallingPlatformModel:function(){
                $('#deleteCallingPlatformModel').modal('hide');
            }
        }
    });

})(window.vc,window.$that);
