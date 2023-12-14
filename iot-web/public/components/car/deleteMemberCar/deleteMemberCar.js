(function(vc,vm){

    vc.extends({
        data:{
            deleteMemberCarInfo:{

            }
        },
         _initMethod:function(){

         },
         _initEvent:function(){
             vc.on('deleteMemberCar','openDeleteMemberCarModal',function(_params){

                $that.deleteMemberCarInfo = _params;
                $('#deleteMemberCarModel').modal('show');

            });
        },
        methods:{
            deleteMemberCar:function(){
                $that.deleteMemberCarInfo.communityId=vc.getCurrentCommunity().communityId;
                vc.http.apiPost(
                    '/ownerCar.deleteOwnerCar',
                    JSON.stringify($that.deleteMemberCarInfo),
                    {
                        emulateJSON:true
                     },
                     function(json,res){
                        //vm.menus = vm.refreshMenuActive(JSON.parse(json),0);
                        let _json = JSON.parse(json);
                        if (_json.code == 0) {
                            //关闭model
                            $('#deleteMemberCarModel').modal('hide');
                            vc.emit('memberCarManage','listMemberCar',{});
                            return ;
                        }
                        vc.message(_json.msg);
                     },
                     function(errInfo,error){
                        console.log('请求失败处理');
                        vc.message(json);

                     });
            },
            closeDeleteMemberCarModel:function(){
                $('#deleteMemberCarModel').modal('hide');
            }
        }
    });

})(window.vc,window.$that);
