(function(vc,vm){

    vc.extends({
        data:{
            deleteMonitorManufactorInfo:{

            }
        },
         _initMethod:function(){

         },
         _initEvent:function(){
             vc.on('deleteMonitorManufactor','openDeleteMonitorManufactorModal',function(_params){

                $that.deleteMonitorManufactorInfo = _params;
                $('#deleteMonitorManufactorModel').modal('show');

            });
        },
        methods:{
            deleteMonitorManufactor:function(){
                $that.deleteMonitorManufactorInfo.communityId=vc.getCurrentCommunity().communityId;
                vc.http.apiPost(
                    '/monitorManufactor.deleteMonitorManufactor',
                    JSON.stringify($that.deleteMonitorManufactorInfo),
                    {
                        emulateJSON:true
                     },
                     function(json,res){
                        //vm.menus = vm.refreshMenuActive(JSON.parse(json),0);
                        let _json = JSON.parse(json);
                        if (_json.code == 0) {
                            //关闭model
                            $('#deleteMonitorManufactorModel').modal('hide');
                            vc.emit('monitorManufactor','listMonitorManufactor',{});
                            return ;
                        }
                        vc.toast(_json.msg);
                     },
                     function(errInfo,error){
                        console.log('请求失败处理');
                        vc.toast(json);

                     });
            },
            closeDeleteMonitorManufactorModel:function(){
                $('#deleteMonitorManufactorModel').modal('hide');
            }
        }
    });

})(window.vc,window.$that);
