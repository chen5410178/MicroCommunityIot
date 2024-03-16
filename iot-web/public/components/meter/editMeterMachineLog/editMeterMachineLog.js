(function(vc,vm){

    vc.extends({
        data:{
            editMeterMachineLogInfo:{
                applyId:'',
personName:'',

            }
        },
         _initMethod:function(){

         },
         _initEvent:function(){
             vc.on('editMeterMachineLog','openEditMeterMachineLogModal',function(_params){
                vc.component.refreshEditMeterMachineLogInfo();
                $('#editMeterMachineLogModel').modal('show');
                vc.copyObject(_params, vc.component.editMeterMachineLogInfo );
                vc.component.editMeterMachineLogInfo.communityId = vc.getCurrentCommunity().communityId;
            });
        },
        methods:{
            editMeterMachineLogValidate:function(){
                        return vc.validate.validate({
                            editMeterMachineLogInfo:vc.component.editMeterMachineLogInfo
                        },{
                            'editMeterMachineLogInfo.personName':[
{
                            limit:"required",
                            param:"",
                            errInfo:"用户名称不能为空"
                        },
 {
                            limit:"maxLength",
                            param:"32",
                            errInfo:"用户名称不能超过64"
                        },
                    ],
'editMeterMachineLogInfo.applyId':[
{
                            limit:"required",
                            param:"",
                            errInfo:"日志ID不能为空"
                        }]

                        });
             },
            editMeterMachineLog:function(){
                if(!vc.component.editMeterMachineLogValidate()){
                    vc.toast(vc.validate.errInfo);
                    return ;
                }

                vc.http.apiPost(
                    'meterMachineLog.updateMeterMachineLog',
                    JSON.stringify(vc.component.editMeterMachineLogInfo),
                    {
                        emulateJSON:true
                     },
                     function(json,res){
                        //vm.menus = vm.refreshMenuActive(JSON.parse(json),0);
                        let _json = JSON.parse(json);
                        if (_json.code == 0) {
                            //关闭model
                            $('#editMeterMachineLogModel').modal('hide');
                             vc.emit('meterMachineLogManage','listMeterMachineLog',{});
                            return ;
                        }
                        vc.message(_json.msg);
                     },
                     function(errInfo,error){
                        console.log('请求失败处理');

                        vc.message(errInfo);
                     });
            },
            refreshEditMeterMachineLogInfo:function(){
                vc.component.editMeterMachineLogInfo= {
                  applyId:'',
personName:'',

                }
            }
        }
    });

})(window.vc,window.vc.component);
