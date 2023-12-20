(function(vc,vm){

    vc.extends({
        data:{
            editPassQrcodeInfo:{
                pqId:'',
personName:'',
personTel:'',
openId:'',
qrcode:'',
qrcodeTime:'',
state:'',
remark:'',

            }
        },
         _initMethod:function(){

         },
         _initEvent:function(){
             vc.on('editPassQrcode','openEditPassQrcodeModal',function(_params){
                vc.component.refreshEditPassQrcodeInfo();
                $('#editPassQrcodeModel').modal('show');
                vc.copyObject(_params, vc.component.editPassQrcodeInfo );
                vc.component.editPassQrcodeInfo.communityId = vc.getCurrentCommunity().communityId;
            });
        },
        methods:{
            editPassQrcodeValidate:function(){
                        return vc.validate.validate({
                            editPassQrcodeInfo:vc.component.editPassQrcodeInfo
                        },{
                            'editPassQrcodeInfo.personName':[
{
                            limit:"required",
                            param:"",
                            errInfo:"名称不能为空"
                        },
 {
                            limit:"maxLength",
                            param:"64",
                            errInfo:"名称不能超过64"
                        },
                    ],
'editPassQrcodeInfo.personTel':[
{
                            limit:"required",
                            param:"",
                            errInfo:"手机号不能为空"
                        },
 {
                            limit:"maxLength",
                            param:"64",
                            errInfo:"手机号不能超过64"
                        },
                    ],
'editPassQrcodeInfo.openId':[
{
                            limit:"required",
                            param:"",
                            errInfo:"openId不能为空"
                        },
 {
                            limit:"maxLength",
                            param:"64",
                            errInfo:"openId不能超过64"
                        },
                    ],
'editPassQrcodeInfo.qrcode':[
{
                            limit:"required",
                            param:"",
                            errInfo:"通信码不能为空"
                        },
 {
                            limit:"maxLength",
                            param:"128",
                            errInfo:"通信码不能超过128"
                        },
                    ],
'editPassQrcodeInfo.qrcodeTime':[
{
                            limit:"required",
                            param:"",
                            errInfo:"通信码刷新时间不能为空"
                        },
 {
                            limit:"maxLength",
                            param:"64",
                            errInfo:"通信码刷新时间不能超过64"
                        },
                    ],
'editPassQrcodeInfo.state':[
{
                            limit:"required",
                            param:"",
                            errInfo:"状态不能为空"
                        },
 {
                            limit:"maxLength",
                            param:"12",
                            errInfo:"状态不能超过12"
                        },
                    ],
'editPassQrcodeInfo.remark':[
{
                            limit:"required",
                            param:"",
                            errInfo:"审核说明不能为空"
                        },
 {
                            limit:"maxLength",
                            param:"512",
                            errInfo:"审核说明不能超过512"
                        },
                    ],
'editPassQrcodeInfo.pqId':[
{
                            limit:"required",
                            param:"",
                            errInfo:"编号不能为空"
                        }]

                        });
             },
            editPassQrcode:function(){
                if(!vc.component.editPassQrcodeValidate()){
                    vc.toast(vc.validate.errInfo);
                    return ;
                }

                vc.http.apiPost(
                    'passQrcode.updatePassQrcode',
                    JSON.stringify(vc.component.editPassQrcodeInfo),
                    {
                        emulateJSON:true
                     },
                     function(json,res){
                        //vm.menus = vm.refreshMenuActive(JSON.parse(json),0);
                        let _json = JSON.parse(json);
                        if (_json.code == 0) {
                            //关闭model
                            $('#editPassQrcodeModel').modal('hide');
                             vc.emit('passQrcodeManage','listPassQrcode',{});
                            return ;
                        }
                        vc.message(_json.msg);
                     },
                     function(errInfo,error){
                        console.log('请求失败处理');

                        vc.message(errInfo);
                     });
            },
            refreshEditPassQrcodeInfo:function(){
                vc.component.editPassQrcodeInfo= {
                  pqId:'',
personName:'',
personTel:'',
openId:'',
qrcode:'',
qrcodeTime:'',
state:'',
remark:'',

                }
            }
        }
    });

})(window.vc,window.vc.component);
