(function(vc,vm){

    vc.extends({
        data:{
            deleteParkingRegionInfo:{

            }
        },
         _initMethod:function(){

         },
         _initEvent:function(){
             vc.on('deleteParkingRegion','openDeleteParkingRegionModal',function(_params){

                $that.deleteParkingRegionInfo = _params;
                $('#deleteParkingRegionModel').modal('show');

            });
        },
        methods:{
            deleteParkingRegion:function(){
                $that.deleteParkingRegionInfo.communityId=vc.getCurrentCommunity().communityId;
                vc.http.apiPost(
                    'parkingRegion.deleteParkingRegion',
                    JSON.stringify($that.deleteParkingRegionInfo),
                    {
                        emulateJSON:true
                     },
                     function(json,res){
                        //vm.menus = vm.refreshMenuActive(JSON.parse(json),0);
                        let _json = JSON.parse(json);
                        if (_json.code == 0) {
                            //关闭model
                            $('#deleteParkingRegionModel').modal('hide');
                            vc.emit('parkingRegion','listParkingRegion',{});
                            return ;
                        }
                        vc.message(_json.msg);
                     },
                     function(errInfo,error){
                        console.log('请求失败处理');
                        vc.message(json);

                     });
            },
            closeDeleteParkingRegionModel:function(){
                $('#deleteParkingRegionModel').modal('hide');
            }
        }
    });

})(window.vc,window.$that);
