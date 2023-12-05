(function(vc,vm){
    vc.extends({
        data:{
            deleteDtScriptInfo:{

            }
        },
         _initMethod:function(){

         },
         _initEvent:function(){
             vc.on('deleteDtScript','openDeleteDtScriptModal',function(_params){
                $that.deleteDtScriptInfo = _params;
                $('#deleteDtScriptModel').modal('show');
            });
        },
        methods:{
            deleteDtScript:function(){
                vc.http.apiPost(
                    '/dtScript.deleteDtScript',
                    JSON.stringify($that.deleteDtScriptInfo),
                    {
                        emulateJSON:true
                     },
                     function(json,res){
                        //vm.menus = vm.refreshMenuActive(JSON.parse(json),0);
                        let _json = JSON.parse(json);
                        if (_json.code == 0) {
                            //关闭model
                            $('#deleteDtScriptModel').modal('hide');
                            vc.emit('dtScript','listDtScript',{});
                            return ;
                        }
                        vc.message(_json.msg);
                     },
                     function(errInfo,error){
                        console.log('请求失败处理');
                        vc.message(json);

                     });
            },
            closeDeleteDtScriptModel:function(){
                $('#deleteDtScriptModel').modal('hide');
            }
        }
    });

})(window.vc,window.$that);
