(function(vc,vm){

    vc.extends({
        data:{
            deleteAccessControlFaceInfo:{

            }
        },
         _initMethod:function(){

         },
         _initEvent:function(){
             vc.on('deleteAccessControlFace','openDeleteAccessControlFaceModal',function(_params){

                vc.component.deleteAccessControlFaceInfo = _params;
                $('#deleteAccessControlFaceModel').modal('show');

            });
        },
        methods:{
            deleteAccessControlFace:function(){
                vc.component.deleteAccessControlFaceInfo.communityId=vc.getCurrentCommunity().communityId;
                vc.http.apiPost(
                    'accessControlFace.deleteAccessControlFace',
                    JSON.stringify(vc.component.deleteAccessControlFaceInfo),
                    {
                        emulateJSON:true
                     },
                     function(json,res){
                        //vm.menus = vm.refreshMenuActive(JSON.parse(json),0);
                        let _json = JSON.parse(json);
                        if (_json.code == 0) {
                            //关闭model
                            $('#deleteAccessControlFaceModel').modal('hide');
                            vc.emit('accessControlFaceManage','listAccessControlFace',{});
                            return ;
                        }
                        vc.message(_json.msg);
                     },
                     function(errInfo,error){
                        console.log('请求失败处理');
                        vc.message(json);

                     });
            },
            closeDeleteAccessControlFaceModel:function(){
                $('#deleteAccessControlFaceModel').modal('hide');
            }
        }
    });

})(window.vc,window.vc.component);
