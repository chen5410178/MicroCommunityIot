(function(vc,vm){

    vc.extends({
        data:{
            deleteLiftCameraInfo:{

            }
        },
         _initMethod:function(){

         },
         _initEvent:function(){
             vc.on('deleteLiftCamera','openDeleteLiftCameraModal',function(_params){

                $that.deleteLiftCameraInfo = _params;
                $('#deleteLiftCameraModel').modal('show');

            });
        },
        methods:{
            deleteLiftCamera:function(){
                $that.deleteLiftCameraInfo.communityId=vc.getCurrentCommunity().communityId;
                vc.http.apiPost(
                    '/liftCamera.deleteLiftCamera',
                    JSON.stringify($that.deleteLiftCameraInfo),
                    {
                        emulateJSON:true
                     },
                     function(json,res){
                        //vm.menus = vm.refreshMenuActive(JSON.parse(json),0);
                        let _json = JSON.parse(json);
                        if (_json.code == 0) {
                            //关闭model
                            $('#deleteLiftCameraModel').modal('hide');
                            vc.emit('liftCamera','listLiftCamera',{});
                            return ;
                        }
                        vc.toast(_json.msg);
                     },
                     function(errInfo,error){
                        console.log('请求失败处理');
                        vc.toast(json);
                     });
            },
            closeDeleteLiftCameraModel:function(){
                $('#deleteLiftCameraModel').modal('hide');
            }
        }
    });

})(window.vc,window.$that);
