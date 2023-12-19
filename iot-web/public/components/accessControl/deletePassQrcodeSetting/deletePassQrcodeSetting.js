(function(vc,vm){

    vc.extends({
        data:{
            deletePassQrcodeSettingInfo:{

            }
        },
         _initMethod:function(){

         },
         _initEvent:function(){
             vc.on('deletePassQrcodeSetting','openDeletePassQrcodeSettingModal',function(_params){
                $that.deletePassQrcodeSettingInfo = _params;
                $('#deletePassQrcodeSettingModel').modal('show');
            });
        },
        methods:{
            deletePassQrcodeSetting:function(){
                $that.deletePassQrcodeSettingInfo.communityId=vc.getCurrentCommunity().communityId;
                vc.http.apiPost(
                    '/passcode.deletePassQrcode',
                    JSON.stringify($that.deletePassQrcodeSettingInfo),
                    {
                        emulateJSON:true
                     },
                     function(json,res){
                        //vm.menus = vm.refreshMenuActive(JSON.parse(json),0);
                        let _json = JSON.parse(json);
                        if (_json.code == 0) {
                            //关闭model
                            $('#deletePassQrcodeSettingModel').modal('hide');
                            vc.emit('passQrcodeSetting','listPassQrcodeSetting',{});
                            return ;
                        }
                        vc.toast(_json.msg);
                     },
                     function(errInfo,error){
                        console.log('请求失败处理');
                        vc.toast(json);
                     });
            },
            closeDeletePassQrcodeSettingModel:function(){
                $('#deletePassQrcodeSettingModel').modal('hide');
            }
        }
    });

})(window.vc,window.$that);
