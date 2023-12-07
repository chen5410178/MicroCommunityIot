(function(vc,vm){
    vc.extends({
        data:{
            deleteObjectScriptInfo:{

            }
        },
         _initMethod:function(){

         },
         _initEvent:function(){
             vc.on('deleteObjectScript','openDeleteModal',function(_params){
                $that.deleteObjectScriptInfo = _params;
                $('#deleteObjectScriptModel').modal('show');
            });
        },
        methods:{
            deleteObjectScript:function(){
                vc.http.apiPost(
                    '/dtScript.deleteDtSceneObjScript',
                    JSON.stringify($that.deleteObjectScriptInfo),
                    {
                        emulateJSON:true
                     },
                     function(json,res){
                        //vm.menus = vm.refreshMenuActive(JSON.parse(json),0);
                        let _json = JSON.parse(json);
                        if (_json.code == 0) {
                            //关闭model
                            $('#deleteObjectScriptModel').modal('hide');
                            vc.emit('curModal', 'loadObjScript', {});
                            vc.emit('dtSceneObjScript', 'listDtSceneObjScript', {});
                            
                            return ;
                        }
                        vc.toast(_json.msg);
                     },
                     function(errInfo,error){
                        console.log('请求失败处理');
                        vc.toast(json);

                     });
            },
            closeDeleteObjectScriptModel:function(){
                $('#deleteObjectScriptModel').modal('hide');
            }
        }
    });

})(window.vc,window.$that);
