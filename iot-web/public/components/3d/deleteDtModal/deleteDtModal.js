(function(vc,vm){

    vc.extends({
        data:{
            deleteDtModalInfo:{

            }
        },
         _initMethod:function(){

         },
         _initEvent:function(){
             vc.on('deleteDtModal','openDeleteDtModalModal',function(_params){

                $that.deleteDtModalInfo = _params;
                $('#deleteDtModalModel').modal('show');

            });
        },
        methods:{
            deleteDtModal:function(){
                vc.http.apiPost(
                    '/dtModal.deleteDtModal',
                    JSON.stringify($that.deleteDtModalInfo),
                    {
                        emulateJSON:true
                     },
                     function(json,res){
                        //vm.menus = vm.refreshMenuActive(JSON.parse(json),0);
                        let _json = JSON.parse(json);
                        if (_json.code == 0) {
                            //关闭model
                            $('#deleteDtModalModel').modal('hide');
                            vc.emit('dtModal','listDtModal',{});
                            return ;
                        }
                        vc.toast(_json.msg);
                     },
                     function(errInfo,error){
                        console.log('请求失败处理');
                        vc.toast(json);

                     });
            },
            closeDeleteDtModalModel:function(){
                $('#deleteDtModalModel').modal('hide');
            }
        }
    });

})(window.vc,window.$that);
