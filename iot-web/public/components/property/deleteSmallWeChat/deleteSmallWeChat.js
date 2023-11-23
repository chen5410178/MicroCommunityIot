(function(vc,vm){

    vc.extends({
        data:{
            deleteSmallWeChatInfo:{

            }
        },
         _initMethod:function(){

         },
         _initEvent:function(){
             vc.on('deleteSmallWeChat','openDeleteSmallWeChatModal',function(_params){
                $that.deleteSmallWeChatInfo = _params;
                $('#deleteSmallWeChatModel').modal('show');
            });
        },
        methods:{
            deleteSmallWeChat:function(){
                $that.deleteSmallWeChatInfo.communityId=vc.getCurrentCommunity().communityId;
                vc.http.apiPost(
                    'smallWeChat.deleteSmallWeChat',
                    JSON.stringify($that.deleteSmallWeChatInfo),
                    {
                        emulateJSON:true
                     },
                     function(json,res){
                        //vm.menus = vm.refreshMenuActive(JSON.parse(json),0);
                        if(res.status == 200){
                            //关闭model
                            $('#deleteSmallWeChatModel').modal('hide');
                            vc.emit('smallWeChatManage','listSmallWeChat',{});
                            return ;
                        }
                        vc.toast(json);
                     },
                     function(errInfo,error){
                        console.log('请求失败处理');
                        vc.toast(json);
                     });
            },
            closeDeleteSmallWeChatModel:function(){
                $('#deleteSmallWeChatModel').modal('hide');
            }
        }
    });

})(window.vc,window.$that);
