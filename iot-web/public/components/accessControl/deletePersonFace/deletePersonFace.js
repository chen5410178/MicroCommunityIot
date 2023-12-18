(function(vc,vm){

    vc.extends({
        data:{
            deletePersonFaceInfo:{

            }
        },
         _initMethod:function(){

         },
         _initEvent:function(){
             vc.on('deletePersonFace','openDeletePersonFaceModal',function(_params){
                $that.deletePersonFaceInfo = _params;
                $('#deletePersonFaceModel').modal('show');
            });
        },
        methods:{
            deletePersonFace:function(){
                $that.deletePersonFaceInfo.communityId=vc.getCurrentCommunity().communityId;
                vc.http.apiPost(
                    '/personFace.deletePersonFace',
                    JSON.stringify($that.deletePersonFaceInfo),
                    {
                        emulateJSON:true
                     },
                     function(json,res){
                        //vm.menus = vm.refreshMenuActive(JSON.parse(json),0);
                        let _json = JSON.parse(json);
                        if (_json.code == 0) {
                            //关闭model
                            $('#deletePersonFaceModel').modal('hide');
                            vc.emit('personFace','listPersonFace',{});
                            return ;
                        }
                        vc.toast(_json.msg);
                     },
                     function(errInfo,error){
                        console.log('请求失败处理');
                        vc.toast(json);
                     });
            },
            closeDeletePersonFaceModel:function(){
                $('#deletePersonFaceModel').modal('hide');
            }
        }
    });

})(window.vc,window.$that);
