(function(vc,vm){

    vc.extends({
        data:{
            editMachineLocationInfo:{
                locationId:'',
locationName:'',
locationType:'',
locationObjName:'',

            }
        },
         _initMethod:function(){

         },
         _initEvent:function(){
             vc.on('editMachineLocation','openEditMachineLocationModal',function(_params){
                vc.component.refreshEditMachineLocationInfo();
                $('#editMachineLocationModel').modal('show');
                vc.copyObject(_params, vc.component.editMachineLocationInfo );
                vc.component.editMachineLocationInfo.communityId = vc.getCurrentCommunity().communityId;
            });
        },
        methods:{
            editMachineLocationValidate:function(){
                        return vc.validate.validate({
                            editMachineLocationInfo:vc.component.editMachineLocationInfo
                        },{
                            'editMachineLocationInfo.locationName':[
{
                            limit:"required",
                            param:"",
                            errInfo:"位置名称不能为空"
                        },
 {
                            limit:"maxLength",
                            param:"100",
                            errInfo:"位置名称不能超过100"
                        },
                    ],
'editMachineLocationInfo.locationType':[
{
                            limit:"required",
                            param:"",
                            errInfo:"位置类型不能为空"
                        },
 {
                            limit:"maxLength",
                            param:"12",
                            errInfo:"位置类型不能超过12"
                        },
                    ],
'editMachineLocationInfo.locationObjName':[
{
                            limit:"required",
                            param:"",
                            errInfo:"对象ID不能为空"
                        },
 {
                            limit:"maxLength",
                            param:"256",
                            errInfo:"对象ID不能超过256"
                        },
                    ],
'editMachineLocationInfo.locationId':[
{
                            limit:"required",
                            param:"",
                            errInfo:"编号不能为空"
                        }]

                        });
             },
            editMachineLocation:function(){
                if(!vc.component.editMachineLocationValidate()){
                    vc.toast(vc.validate.errInfo);
                    return ;
                }

                vc.http.apiPost(
                    'machineLocation.updateMachineLocation',
                    JSON.stringify(vc.component.editMachineLocationInfo),
                    {
                        emulateJSON:true
                     },
                     function(json,res){
                        //vm.menus = vm.refreshMenuActive(JSON.parse(json),0);
                        let _json = JSON.parse(json);
                        if (_json.code == 0) {
                            //关闭model
                            $('#editMachineLocationModel').modal('hide');
                             vc.emit('machineLocationManage','listMachineLocation',{});
                            return ;
                        }
                        vc.message(_json.msg);
                     },
                     function(errInfo,error){
                        console.log('请求失败处理');

                        vc.message(errInfo);
                     });
            },
            refreshEditMachineLocationInfo:function(){
                vc.component.editMachineLocationInfo= {
                  locationId:'',
locationName:'',
locationType:'',
locationObjName:'',

                }
            }
        }
    });

})(window.vc,window.vc.component);
