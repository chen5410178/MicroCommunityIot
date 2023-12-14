(function(vc,vm){

    vc.extends({
        data:{
            deleteCarMonthCardInfo:{

            }
        },
         _initMethod:function(){

         },
         _initEvent:function(){
             vc.on('deleteCarMonthCard','openDeleteCarMonthCardModal',function(_params){
                $that.deleteCarMonthCardInfo = _params;
                $('#deleteCarMonthCardModel').modal('show');
            });
        },
        methods:{
            deleteCarMonthCard:function(){
                $that.deleteCarMonthCardInfo.communityId=vc.getCurrentCommunity().communityId;
                vc.http.apiPost(
                    '/carMonth.deleteCarMonthCard',
                    JSON.stringify($that.deleteCarMonthCardInfo),
                    {
                        emulateJSON:true
                     },
                     function(json,res){
                        //vm.menus = vm.refreshMenuActive(JSON.parse(json),0);
                        let _json = JSON.parse(json);
                        if (_json.code == 0) {
                            //关闭model
                            $('#deleteCarMonthCardModel').modal('hide');
                            vc.emit('carMonthCard','listCarMonthCard',{});
                            return ;
                        }
                        vc.toast(_json.msg);
                     },
                     function(errInfo,error){
                        console.log('请求失败处理');
                        vc.toast(json);

                     });
            },
            closeDeleteCarMonthCardModel:function(){
                $('#deleteCarMonthCardModel').modal('hide');
            }
        }
    });

})(window.vc,window.$that);
