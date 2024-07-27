(function(vc){

    vc.extends({
        propTypes: {
               callBackListener:vc.propTypes.string, //父组件名称
               callBackFunction:vc.propTypes.string //父组件监听方法
        },
        data:{
            addMonitorMachinePersonInfo:{
                mmuId:'',
                mmuId:'',
machineName:'',
personType:'',
personName:'',

            }
        },
         _initMethod:function(){

         },
         _initEvent:function(){
            vc.on('addMonitorMachinePerson','openAddMonitorMachinePersonModal',function(){
                $('#addMonitorMachinePersonModel').modal('show');
            });
        },
        methods:{
            addMonitorMachinePersonValidate(){
                return vc.validate.validate({
                    addMonitorMachinePersonInfo:vc.component.addMonitorMachinePersonInfo
                },{
                    'addMonitorMachinePersonInfo.mmuId':[
{
                            limit:"required",
                            param:"",
                            errInfo:"监控用户不能为空"
                        },
 {
                            limit:"maxLength",
                            param:"30",
                            errInfo:"监控用户不能超过30"
                        },
                    ],
'addMonitorMachinePersonInfo.machineName':[
{
                            limit:"required",
                            param:"",
                            errInfo:"名称不能为空"
                        },
 {
                            limit:"maxLength",
                            param:"200",
                            errInfo:"名称不能超过200"
                        },
                    ],
'addMonitorMachinePersonInfo.personType':[
{
                            limit:"required",
                            param:"",
                            errInfo:"人员类型不能为空"
                        },
 {
                            limit:"maxLength",
                            param:"12",
                            errInfo:"人员类型不能超过12"
                        },
                    ],
'addMonitorMachinePersonInfo.personName':[
{
                            limit:"required",
                            param:"",
                            errInfo:"人员不能为空"
                        },
 {
                            limit:"maxLength",
                            param:"30",
                            errInfo:"人员不能超过30"
                        },
                    ],




                });
            },
            saveMonitorMachinePersonInfo:function(){
                if(!vc.component.addMonitorMachinePersonValidate()){
                    vc.toast(vc.validate.errInfo);

                    return ;
                }

                vc.component.addMonitorMachinePersonInfo.communityId = vc.getCurrentCommunity().communityId;
                //不提交数据将数据 回调给侦听处理
                if(vc.notNull($props.callBackListener)){
                    vc.emit($props.callBackListener,$props.callBackFunction,vc.component.addMonitorMachinePersonInfo);
                    $('#addMonitorMachinePersonModel').modal('hide');
                    return ;
                }

                vc.http.apiPost(
                    'monitorMachinePerson.saveMonitorMachinePerson',
                    JSON.stringify(vc.component.addMonitorMachinePersonInfo),
                    {
                        emulateJSON:true
                     },
                     function(json,res){
                        //vm.menus = vm.refreshMenuActive(JSON.parse(json),0);
                        let _json = JSON.parse(json);
                        if (_json.code == 0) {
                            //关闭model
                            $('#addMonitorMachinePersonModel').modal('hide');
                            vc.component.clearAddMonitorMachinePersonInfo();
                            vc.emit('monitorMachinePersonManage','listMonitorMachinePerson',{});

                            return ;
                        }
                        vc.message(_json.msg);

                     },
                     function(errInfo,error){
                        console.log('请求失败处理');

                        vc.message(errInfo);

                     });
            },
            clearAddMonitorMachinePersonInfo:function(){
                vc.component.addMonitorMachinePersonInfo = {
                                            mmuId:'',
machineName:'',
personType:'',
personName:'',

                                        };
            }
        }
    });

})(window.vc);
