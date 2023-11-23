(function(vc,vm){

    vc.extends({
        data:{
            deleteInstrumentTypeInfo:{

            }
        },
         _initMethod:function(){

         },
         _initEvent:function(){
             vc.on('deleteInstrumentType','openDeleteInstrumentTypeModal',function(_params){
                $that.deleteInstrumentTypeInfo = _params;
                $('#deleteInstrumentTypeModel').modal('show');
            });
        },
        methods:{
            deleteInstrumentType:function(){
                vc.http.apiPost(
                    '/instrumentType.deleteInstrumentType',
                    JSON.stringify($that.deleteInstrumentTypeInfo),
                    {
                        emulateJSON:true
                     },
                     function(json,res){
                        let _json = JSON.parse(json);
                        if (_json.code === 0) {
                            //关闭model
                            $('#deleteInstrumentTypeModel').modal('hide');
                            vc.emit('instrumentTypeManage','listInstrumentType',{});
                            return ;
                        }
                        vc.toast(_json.msg);
                     },
                     function(errInfo,error){
                        console.log('请求失败处理');
                        vc.toast(json);
                     });
            },
            closeDeleteInstrumentTypeModel:function(){
                $('#deleteInstrumentTypeModel').modal('hide');
            }
        }
    });
})(window.vc,window.$that);
