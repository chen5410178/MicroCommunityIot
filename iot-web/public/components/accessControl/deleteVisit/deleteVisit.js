(function(vc,vm){

    vc.extends({
        data:{
            deleteVisitInfo:{

            }
        },
         _initMethod:function(){

         },
         _initEvent:function(){
             vc.on('deleteVisit','openDeleteVisitModal',function(_params){
                $that.deleteVisitInfo = _params;
                $('#deleteVisitModel').modal('show');
            });
        },
        methods:{
            deleteVisit:function(){
                $that.deleteVisitInfo.communityId=vc.getCurrentCommunity().communityId;
                vc.http.apiPost(
                    '/visit.deleteVisit',
                    JSON.stringify($that.deleteVisitInfo),
                    {
                        emulateJSON:true
                     },
                     function(json,res){
                        let _json = JSON.parse(json);
                        if (_json.code === 0) {
                            //关闭model
                            $('#deleteVisitModel').modal('hide');
                            vc.emit('visitManage','listVisit',{});
                            return ;
                        }
                        vc.toast(_json.msg);
                     },
                     function(errInfo,error){
                        console.log('请求失败处理');
                        vc.toast(json);
                     });
            },
            closeDeleteVisitModel:function(){
                $('#deleteVisitModel').modal('hide');
            }
        }
    });
})(window.vc,window.$that);
