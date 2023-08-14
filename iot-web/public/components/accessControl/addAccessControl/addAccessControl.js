(function(vc){

    vc.extends({
        propTypes: {
               callBackListener:vc.propTypes.string, //父组件名称
               callBackFunction:vc.propTypes.string //父组件监听方法
        },
        data:{
            addAccessControlInfo:{
                machineId:'',
                machineCode:'',
machineName:'',
machineIp:'',
machineMac:'',
direction:'',
implBean:'',
locationId:'',
locationName:'',

            }
        },
         _initMethod:function(){

         },
         _initEvent:function(){
            vc.on('addAccessControl','openAddAccessControlModal',function(){
                $('#addAccessControlModel').modal('show');
            });
        },
        methods:{
            addAccessControlValidate(){
                return vc.validate.validate({
                    addAccessControlInfo:vc.component.addAccessControlInfo
                },{
                    'addAccessControlInfo.machineCode':[
{
                            limit:"required",
                            param:"",
                            errInfo:"设备编码不能为空"
                        },
 {
                            limit:"maxLength",
                            param:"30",
                            errInfo:"设备编码不能超过30"
                        },
                    ],
'addAccessControlInfo.machineName':[
{
                            limit:"required",
                            param:"",
                            errInfo:"设备名称不能为空"
                        },
 {
                            limit:"maxLength",
                            param:"200",
                            errInfo:"设备名称不能超过200"
                        },
                    ],
'addAccessControlInfo.machineIp':[
{
                            limit:"required",
                            param:"",
                            errInfo:"设备IP不能为空"
                        },
 {
                            limit:"maxLength",
                            param:"64",
                            errInfo:"设备IP不能超过64"
                        },
                    ],
'addAccessControlInfo.machineMac':[
{
                            limit:"required",
                            param:"",
                            errInfo:"设备mac不能为空"
                        },
 {
                            limit:"maxLength",
                            param:"64",
                            errInfo:"设备mac不能超过64"
                        },
                    ],
'addAccessControlInfo.direction':[
{
                            limit:"required",
                            param:"",
                            errInfo:"设备方向进出不能为空"
                        },
 {
                            limit:"maxLength",
                            param:"12",
                            errInfo:"设备方向进出不能超过12"
                        },
                    ],
'addAccessControlInfo.implBean':[
{
                            limit:"required",
                            param:"",
                            errInfo:"门禁厂家不能为空"
                        },
 {
                            limit:"maxLength",
                            param:"30",
                            errInfo:"门禁厂家不能超过30"
                        },
                    ],
'addAccessControlInfo.locationId':[
{
                            limit:"required",
                            param:"",
                            errInfo:"位置ID不能为空"
                        },
 {
                            limit:"maxLength",
                            param:"30",
                            errInfo:"位置ID不能超过30"
                        },
                    ],
'addAccessControlInfo.locationName':[
{
                            limit:"required",
                            param:"",
                            errInfo:"位置名称'不能为空"
                        },
 {
                            limit:"maxLength",
                            param:"30",
                            errInfo:"位置名称'不能超过30"
                        },
                    ],




                });
            },
            saveAccessControlInfo:function(){
                if(!vc.component.addAccessControlValidate()){
                    vc.toast(vc.validate.errInfo);

                    return ;
                }

                vc.component.addAccessControlInfo.communityId = vc.getCurrentCommunity().communityId;
                //不提交数据将数据 回调给侦听处理
                if(vc.notNull($props.callBackListener)){
                    vc.emit($props.callBackListener,$props.callBackFunction,vc.component.addAccessControlInfo);
                    $('#addAccessControlModel').modal('hide');
                    return ;
                }

                vc.http.apiPost(
                    'accessControl.saveAccessControl',
                    JSON.stringify(vc.component.addAccessControlInfo),
                    {
                        emulateJSON:true
                     },
                     function(json,res){
                        //vm.menus = vm.refreshMenuActive(JSON.parse(json),0);
                        let _json = JSON.parse(json);
                        if (_json.code == 0) {
                            //关闭model
                            $('#addAccessControlModel').modal('hide');
                            vc.component.clearAddAccessControlInfo();
                            vc.emit('accessControlManage','listAccessControl',{});

                            return ;
                        }
                        vc.message(_json.msg);

                     },
                     function(errInfo,error){
                        console.log('请求失败处理');

                        vc.message(errInfo);

                     });
            },
            clearAddAccessControlInfo:function(){
                vc.component.addAccessControlInfo = {
                                            machineCode:'',
machineName:'',
machineIp:'',
machineMac:'',
direction:'',
implBean:'',
locationId:'',
locationName:'',

                                        };
            }
        }
    });

})(window.vc);
