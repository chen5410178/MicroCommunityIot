(function(vc,vm){

    vc.extends({
        data:{
            deleteParkingCouponChargeInfo:{

            }
        },
         _initMethod:function(){

         },
         _initEvent:function(){
             vc.on('deleteParkingCouponCharge','openDeleteParkingCouponChargeModal',function(_params){

                $that.deleteParkingCouponChargeInfo = _params;
                $('#deleteParkingCouponChargeModel').modal('show');

            });
        },
        methods:{
            deleteParkingCouponCharge:function(){
                $that.deleteParkingCouponChargeInfo.communityId=vc.getCurrentCommunity().communityId;
                vc.http.apiPost(
                    '/parkingCoupon.deleteParkingCouponCharge',
                    JSON.stringify($that.deleteParkingCouponChargeInfo),
                    {
                        emulateJSON:true
                     },
                     function(json,res){
                        //vm.menus = vm.refreshMenuActive(JSON.parse(json),0);
                        let _json = JSON.parse(json);
                        if (_json.code == 0) {
                            //关闭model
                            $('#deleteParkingCouponChargeModel').modal('hide');
                            vc.emit('machineDetailChargeParking', 'list',{});
                            return ;
                        }
                        vc.toast(_json.msg);
                     },
                     function(errInfo,error){
                        console.log('请求失败处理');
                        vc.toast(json);

                     });
            },
            closeDeleteParkingCouponChargeModel:function(){
                $('#deleteParkingCouponChargeModel').modal('hide');
            }
        }
    });

})(window.vc,window.$that);
