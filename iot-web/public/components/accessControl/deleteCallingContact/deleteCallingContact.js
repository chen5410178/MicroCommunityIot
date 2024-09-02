(function(vc,vm){

    vc.extends({
        data:{
            deleteCallingContactInfo:{

            }
        },
         _initMethod:function(){

         },
         _initEvent:function(){
             vc.on('deleteCallingContact','openDeleteCallingContactModal',function(_params){

                vc.component.deleteCallingContactInfo = _params;
                $('#deleteCallingContactModel').modal('show');

            });
        },
        methods:{
            deleteCallingContact:function(){
                vc.component.deleteCallingContactInfo.communityId=vc.getCurrentCommunity().communityId;
                vc.http.apiPost(
                    'callingContact.deleteCallingContact',
                    JSON.stringify(vc.component.deleteCallingContactInfo),
                    {
                        emulateJSON:true
                     },
                     function(json,res){
                        //vm.menus = vm.refreshMenuActive(JSON.parse(json),0);
                        let _json = JSON.parse(json);
                        if (_json.code == 0) {
                            //关闭model
                            $('#deleteCallingContactModel').modal('hide');
                            vc.emit('callingContactManage','listCallingContact',{});
                            return ;
                        }
                        vc.message(_json.msg);
                     },
                     function(errInfo,error){
                        console.log('请求失败处理');
                        vc.message(json);

                     });
            },
            closeDeleteCallingContactModel:function(){
                $('#deleteCallingContactModel').modal('hide');
            }
        }
    });

})(window.vc,window.vc.component);
