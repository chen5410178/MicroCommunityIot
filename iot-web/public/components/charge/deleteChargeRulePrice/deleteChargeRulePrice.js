(function(vc,vm){

    vc.extends({
        data:{
            deleteChargeRulePriceInfo:{

            }
        },
         _initMethod:function(){

         },
         _initEvent:function(){
             vc.on('deleteChargeRulePrice','openDeleteChargeRulePriceModal',function(_params){
                vc.component.deleteChargeRulePriceInfo = _params;
                $('#deleteChargeRulePriceModel').modal('show');

            });
        },
        methods:{
            deleteChargeRulePrice:function(){
                vc.component.deleteChargeRulePriceInfo.communityId=vc.getCurrentCommunity().communityId;
                vc.http.apiPost(
                    '/chargeRule.deleteChargeRulePrice',
                    JSON.stringify(vc.component.deleteChargeRulePriceInfo),
                    {
                        emulateJSON:true
                     },
                     function(json,res){
                        //vm.menus = vm.refreshMenuActive(JSON.parse(json),0);
                        let _json = JSON.parse(json);
                        if (_json.code == 0) {
                            //关闭model
                            $('#deleteChargeRulePriceModel').modal('hide');
                            vc.emit('chargeRulePrice', 'listChargeRulePrice',{});
                            return ;
                        }
                        vc.toast(_json.msg);
                     },
                     function(errInfo,error){
                        console.log('请求失败处理');
                        vc.toast(json);

                     });
            },
            closeDeleteChargeRulePriceModel:function(){
                $('#deleteChargeRulePriceModel').modal('hide');
            }
        }
    });

})(window.vc,window.vc.component);
