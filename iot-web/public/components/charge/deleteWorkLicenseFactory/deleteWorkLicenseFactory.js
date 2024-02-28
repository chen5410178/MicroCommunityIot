(function(vc,vm){

    vc.extends({
        data:{
            deleteWorkLicenseFactoryInfo:{

            }
        },
         _initMethod:function(){

         },
         _initEvent:function(){
             vc.on('deleteWorkLicenseFactory','openDeleteWorkLicenseFactoryModal',function(_params){

                $that.deleteWorkLicenseFactoryInfo = _params;
                $('#deleteWorkLicenseFactoryModel').modal('show');

            });
        },
        methods:{
            deleteWorkLicenseFactory:function(){
                vc.http.apiPost(
                    '/workLicenseFactory.deleteWorkLicenseFactory',
                    JSON.stringify($that.deleteWorkLicenseFactoryInfo),
                    {
                        emulateJSON:true
                     },
                     function(json,res){
                        //vm.menus = vm.refreshMenuActive(JSON.parse(json),0);
                        let _json = JSON.parse(json);
                        if (_json.code == 0) {
                            //关闭model
                            $('#deleteWorkLicenseFactoryModel').modal('hide');
                            vc.emit('workLicenseFactory','listWorkLicenseFactory',{});
                            return ;
                        }
                        vc.toast(_json.msg);
                     },
                     function(errInfo,error){
                        console.log('请求失败处理');
                        vc.toast(json);

                     });
            },
            closeDeleteWorkLicenseFactoryModel:function(){
                $('#deleteWorkLicenseFactoryModel').modal('hide');
            }
        }
    });

})(window.vc,window.$that);
