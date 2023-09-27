(function(vc,vm){

    vc.extends({
        data:{
            deleteEventTemplateInfo:{

            }
        },
         _initMethod:function(){

         },
         _initEvent:function(){
             vc.on('deleteEventTemplate','openDeleteEventTemplateModal',function(_params){
                $that.deleteEventTemplateInfo = _params;
                $('#deleteEventTemplateModel').modal('show');
            });
        },
        methods:{
            deleteEventTemplate:function(){
                $that.deleteEventTemplateInfo.communityId=vc.getCurrentCommunity().communityId;
                vc.http.apiPost(
                    '/eventTemplate.deleteEventTemplate',
                    JSON.stringify($that.deleteEventTemplateInfo),
                    {
                        emulateJSON:true
                     },
                     function(json,res){
                        let _json = JSON.parse(json);
                        if (_json.code === 0) {
                            //关闭model
                            $('#deleteEventTemplateModel').modal('hide');
                            vc.emit('eventTemplateManage','listEventTemplate',{});
                            return ;
                        }
                        vc.toast(_json.msg);
                     },
                     function(errInfo,error){
                        console.log('请求失败处理');
                        vc.toast(json);
                     });
            },
            closeDeleteEventTemplateModel:function(){
                $('#deleteEventTemplateModel').modal('hide');
            }
        }
    });
})(window.vc,window.$that);
