(function(vc,vm){

    vc.extends({
        data:{
            deleteDispatchFaceInfo:{

            }
        },
         _initMethod:function(){

         },
         _initEvent:function(){
             vc.on('deleteDispatchFace','openDeleteDispatchFaceModal',function(_params){

                $that.deleteDispatchFaceInfo = _params;
                $('#deleteDispatchFaceModel').modal('show');

            });
        },
        methods:{
            deleteDispatchFace:function(){
                vc.http.apiPost(
                    '/app/face/deletePersonFace',
                    JSON.stringify($that.deleteDispatchFaceInfo),
                    {
                        emulateJSON:true
                     },
                     function(json,res){
                        //vm.menus = vm.refreshMenuActive(JSON.parse(json),0);
                        let _json = JSON.parse(json);
                        if (_json.code == 0) {
                            //关闭model
                            $('#deleteDispatchFaceModel').modal('hide');
                            vc.emit('dispatchFace', 'listDispatchFace',{});
                            return ;
                        }
                        vc.toast(_json.msg);
                     },
                     function(errInfo,error){
                        console.log('请求失败处理');
                        vc.toast(json);

                     });
            },
            closeDeleteDispatchFaceModel:function(){
                $('#deleteDispatchFaceModel').modal('hide');
            }
        }
    });

})(window.vc,window.$that);
