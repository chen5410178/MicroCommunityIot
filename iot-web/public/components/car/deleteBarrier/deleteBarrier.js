(function(vc,vm){

    vc.extends({
        data:{
            deleteBarrierInfo:{

            }
        },
         _initMethod:function(){

         },
         _initEvent:function(){
             vc.on('deleteBarrier','openDeleteBarrierModal',function(_params){

                vc.component.deleteBarrierInfo = _params;
                $('#deleteBarrierModel').modal('show');

            });
        },
        methods:{
            deleteBarrier:function(){
                vc.component.deleteBarrierInfo.communityId=vc.getCurrentCommunity().communityId;
                vc.http.apiPost(
                    'barrier.deleteBarrier',
                    JSON.stringify(vc.component.deleteBarrierInfo),
                    {
                        emulateJSON:true
                     },
                     function(json,res){
                        //vm.menus = vm.refreshMenuActive(JSON.parse(json),0);
                        let _json = JSON.parse(json);
                        if (_json.code == 0) {
                            //关闭model
                            $('#deleteBarrierModel').modal('hide');
                            vc.emit('barrier','listBarrier',{});
                            return ;
                        }
                        vc.message(_json.msg);
                     },
                     function(errInfo,error){
                        console.log('请求失败处理');
                        vc.message(json);

                     });
            },
            closeDeleteBarrierModel:function(){
                $('#deleteBarrierModel').modal('hide');
            }
        }
    });

})(window.vc,window.vc.component);
