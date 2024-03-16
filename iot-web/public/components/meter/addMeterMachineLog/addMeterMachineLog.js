(function(vc){

    vc.extends({
        propTypes: {
               callBackListener:vc.propTypes.string, //父组件名称
               callBackFunction:vc.propTypes.string //父组件监听方法
        },
        data:{
            addMeterMachineLogInfo:{
                applyId:'',
                personName:'',

            }
        },
         _initMethod:function(){

         },
         _initEvent:function(){
            vc.on('addMeterMachineLog','openAddMeterMachineLogModal',function(){
                $('#addMeterMachineLogModel').modal('show');
            });
        },
        methods:{
            addMeterMachineLogValidate(){
                return vc.validate.validate({
                    addMeterMachineLogInfo:vc.component.addMeterMachineLogInfo
                },{
                    'addMeterMachineLogInfo.personName':[
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




                });
            },
            saveMeterMachineLogInfo:function(){
                if(!vc.component.addMeterMachineLogValidate()){
                    vc.toast(vc.validate.errInfo);

                    return ;
                }

                vc.component.addMeterMachineLogInfo.communityId = vc.getCurrentCommunity().communityId;
                //不提交数据将数据 回调给侦听处理
                if(vc.notNull($props.callBackListener)){
                    vc.emit($props.callBackListener,$props.callBackFunction,vc.component.addMeterMachineLogInfo);
                    $('#addMeterMachineLogModel').modal('hide');
                    return ;
                }

                vc.http.apiPost(
                    'meterMachineLog.saveMeterMachineLog',
                    JSON.stringify(vc.component.addMeterMachineLogInfo),
                    {
                        emulateJSON:true
                     },
                     function(json,res){
                        //vm.menus = vm.refreshMenuActive(JSON.parse(json),0);
                        let _json = JSON.parse(json);
                        if (_json.code == 0) {
                            //关闭model
                            $('#addMeterMachineLogModel').modal('hide');
                            vc.component.clearAddMeterMachineLogInfo();
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
            clearAddMeterMachineLogInfo:function(){
                vc.component.addMeterMachineLogInfo = {
                                            personName:'',

                                        };
            }
        }
    });

})(window.vc);
