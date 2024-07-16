(function(vc,vm){

    vc.extends({
        data:{
            deleteAccessControlAdInfo:{

            }
        },
         _initMethod:function(){

         },
         _initEvent:function(){
             vc.on('deleteAccessControlAd','openDeleteAccessControlAdModal',function(_params){

                $that.deleteAccessControlAdInfo = _params;
                $('#deleteAccessControlAdModel').modal('show');

            });
        },
        methods:{
            deleteAccessControlAd:function(){
                $that.deleteAccessControlAdInfo.communityId=vc.getCurrentCommunity().communityId;
                vc.http.apiPost(
                    '/ad.deleteAccessControlAd',
                    JSON.stringify($that.deleteAccessControlAdInfo),
                    {
                        emulateJSON:true
                     },
                     function(json,res){
                        //vm.menus = vm.refreshMenuActive(JSON.parse(json),0);
                        let _json = JSON.parse(json);
                        if (_json.code == 0) {
                            //关闭model
                            $('#deleteAccessControlAdModel').modal('hide');
                            vc.emit('accessControlAd','listAccessControlAd',{});
                            return ;
                        }
                        vc.toast(_json.msg);
                     },
                     function(errInfo,error){
                        console.log('请求失败处理');
                        vc.toast(json);

                     });
            },
            closeDeleteAccessControlAdModel:function(){
                $('#deleteAccessControlAdModel').modal('hide');
            }
        }
    });

})(window.vc,window.$that);
