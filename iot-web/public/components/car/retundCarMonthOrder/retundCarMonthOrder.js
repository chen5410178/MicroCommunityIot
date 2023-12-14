(function(vc,vm){
    vc.extends({
        data:{
            retundCarMonthOrderInfo:{

            }
        },
         _initMethod:function(){

         },
         _initEvent:function(){
             vc.on('retundCarMonthOrder','openRetundOrderModal',function(_params){
                $that.retundCarMonthOrderInfo = _params;
                $('#retundCarMonthOrderModel').modal('show');
            });
        },
        methods:{
            retundCarMonthOrder:function(){
                $that.retundCarMonthOrderInfo.communityId=vc.getCurrentCommunity().communityId;
                vc.http.apiPost(
                    '/carMonth.refundCarMonthOrder',
                    JSON.stringify($that.retundCarMonthOrderInfo),
                    {
                        emulateJSON:true
                     },
                     function(json,res){
                        //vm.menus = vm.refreshMenuActive(JSON.parse(json),0);
                        let _json = JSON.parse(json);
                        if (_json.code == 0) {
                            //关闭model
                            $('#retundCarMonthOrderModel').modal('hide');
                            vc.emit('carMonthOrder','listCarMonthOrder',{});
                            return ;
                        }
                        vc.message(_json.msg);
                     },
                     function(errInfo,error){
                        console.log('请求失败处理');
                        vc.message(json);

                     });
            },
            closeDeleteCarMonthOrderModel:function(){
                $('#retundCarMonthOrderModel').modal('hide');
            }
        }
    });

})(window.vc,window.$that);
