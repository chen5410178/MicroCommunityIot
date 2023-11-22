(function(vc,vm){

    vc.extends({
        data:{
            deleteInstrumentFactorySpecInfo:{

            }
        },
         _initMethod:function(){

         },
         _initEvent:function(){
             vc.on('deleteInstrumentFactorySpec','openDeleteInstrumentFactorySpecModal',function(_params){
                $that.deleteInstrumentFactorySpecInfo = _params;
                $('#deleteInstrumentFactorySpecModel').modal('show');
            });
        },
        methods:{
            deleteInstrumentFactorySpec:function(){
                vc.http.apiPost(
                    '/instrumentFactorySpec.deleteInstrumentFactorySpec',
                    JSON.stringify($that.deleteInstrumentFactorySpecInfo),
                    {
                        emulateJSON:true
                     },
                     function(json,res){
                        let _json = JSON.parse(json);
                        if (_json.code === 0) {
                            //关闭model
                            $('#deleteInstrumentFactorySpecModel').modal('hide');
                            vc.emit('instrumentFactorySpecManage','listInstrumentFactorySpec',{});
                            return ;
                        }
                        vc.toast(_json.msg);
                     },
                     function(errInfo,error){
                        console.log('请求失败处理');
                        vc.toast(json);
                     });
            },
            closeDeleteInstrumentFactorySpecModel:function(){
                $('#deleteInstrumentFactorySpecModel').modal('hide');
            }
        }
    });
})(window.vc,window.$that);
