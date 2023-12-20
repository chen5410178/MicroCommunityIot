(function(vc,vm){

    vc.extends({
        data:{
            deletePassQrcodeInfo:{

            }
        },
         _initMethod:function(){

         },
         _initEvent:function(){
             vc.on('deletePassQrcode','openDeletePassQrcodeModal',function(_params){
                $that.deletePassQrcodeInfo = _params;
                $('#deletePassQrcodeModel').modal('show');
            });
        },
        methods:{
            deletePassQrcode:function(){
                $that.deletePassQrcodeInfo.communityId=vc.getCurrentCommunity().communityId;
                vc.http.apiPost(
                    '/passcode.deletePassQrcode',
                    JSON.stringify($that.deletePassQrcodeInfo),
                    {
                        emulateJSON:true
                     },
                     function(json,res){
                        let _json = JSON.parse(json);
                        if (_json.code == 0) {
                            //关闭model
                            $('#deletePassQrcodeModel').modal('hide');
                            vc.emit('passQrcode','listPassQrcode',{});
                            return ;
                        }
                        vc.toast(_json.msg);
                     },
                     function(errInfo,error){
                        console.log('请求失败处理');
                        vc.toast(json);
                     });
            },
            closeDeletePassQrcodeModel:function(){
                $('#deletePassQrcodeModel').modal('hide');
            }
        }
    });

})(window.vc,window.$that);
