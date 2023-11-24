(function(vc,vm){

    vc.extends({
        data:{
            deleteDtSceneInfo:{

            }
        },
         _initMethod:function(){

         },
         _initEvent:function(){
             vc.on('deleteDtScene','openDeleteDtSceneModal',function(_params){

                $that.deleteDtSceneInfo = _params;
                $('#deleteDtSceneModel').modal('show');

            });
        },
        methods:{
            deleteDtScene:function(){
                $that.deleteDtSceneInfo.communityId=vc.getCurrentCommunity().communityId;
                vc.http.apiPost(
                    '/dtScene.deleteDtScene',
                    JSON.stringify($that.deleteDtSceneInfo),
                    {
                        emulateJSON:true
                     },
                     function(json,res){
                        //vm.menus = vm.refreshMenuActive(JSON.parse(json),0);
                        let _json = JSON.parse(json);
                        if (_json.code == 0) {
                            //关闭model
                            $('#deleteDtSceneModel').modal('hide');
                            vc.emit('dtScene','listDtScene',{});
                            return ;
                        }
                        vc.message(_json.msg);
                     },
                     function(errInfo,error){
                        console.log('请求失败处理');
                        vc.message(json);

                     });
            },
            closeDeleteDtSceneModel:function(){
                $('#deleteDtSceneModel').modal('hide');
            }
        }
    });

})(window.vc,window.$that);
