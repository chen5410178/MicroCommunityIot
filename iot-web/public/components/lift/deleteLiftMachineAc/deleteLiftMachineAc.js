(function(vc,vm){

    vc.extends({
        data:{
            deleteLiftMachineAcInfo:{

            }
        },
         _initMethod:function(){

         },
         _initEvent:function(){
             vc.on('deleteLiftMachineAc','openDeleteLiftMachineAcModal',function(_params){
                $that.deleteLiftMachineAcInfo = _params;
                $('#deleteLiftMachineAcModel').modal('show');

            });
        },
        methods:{
            deleteLiftMachineAc:function(){
                $that.deleteLiftMachineAcInfo.communityId=vc.getCurrentCommunity().communityId;
                vc.http.apiPost(
                    '/liftMachineAc.deleteLiftMachineAc',
                    JSON.stringify($that.deleteLiftMachineAcInfo),
                    {
                        emulateJSON:true
                     },
                     function(json,res){
                        //vm.menus = vm.refreshMenuActive(JSON.parse(json),0);
                        let _json = JSON.parse(json);
                        if (_json.code == 0) {
                            //关闭model
                            $('#deleteLiftMachineAcModel').modal('hide');
                            vc.emit('liftMachineAc','listLiftMachineAc',{});
                            return ;
                        }
                        vc.toast(_json.msg);
                     },
                     function(errInfo,error){
                        console.log('请求失败处理');
                        vc.toast(json);
                     });
            },
            closeDeleteLiftMachineAcModel:function(){
                $('#deleteLiftMachineAcModel').modal('hide');
            }
        }
    });

})(window.vc,window.$that);
