(function(vc,vm){

    vc.extends({
        data:{
            deletePersonFaceQrcodeInfo:{

            }
        },
         _initMethod:function(){

         },
         _initEvent:function(){
             vc.on('deletePersonFaceQrcode','openDeletePersonFaceQrcodeModal',function(_params){

                $that.deletePersonFaceQrcodeInfo = _params;
                $('#deletePersonFaceQrcodeModel').modal('show');

            });
        },
        methods:{
            deletePersonFaceQrcode:function(){
                $that.deletePersonFaceQrcodeInfo.communityId=vc.getCurrentCommunity().communityId;
                vc.http.apiPost(
                    '/personFace.deletePersonFaceQrcode',
                    JSON.stringify($that.deletePersonFaceQrcodeInfo),
                    {
                        emulateJSON:true
                     },
                     function(json,res){
                        //vm.menus = vm.refreshMenuActive(JSON.parse(json),0);
                        let _json = JSON.parse(json);
                        if (_json.code == 0) {
                            //关闭model
                            $('#deletePersonFaceQrcodeModel').modal('hide');
                            vc.emit('personFaceQrcode','listPersonFaceQrcode',{});
                            return ;
                        }
                        vc.toast(_json.msg);
                     },
                     function(errInfo,error){
                        console.log('请求失败处理');
                        vc.toast(json);

                     });
            },
            closeDeletePersonFaceQrcodeModel:function(){
                $('#deletePersonFaceQrcodeModel').modal('hide');
            }
        }
    });

})(window.vc,window.$that);
