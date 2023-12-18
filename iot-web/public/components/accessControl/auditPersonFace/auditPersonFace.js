(function (vc, vm) {

    vc.extends({
        data: {
            auditPersonFaceInfo: {
                pfId: '',
                roomName: '',
                pfqId: '',
                personName: '',
                personTel: '',
                ownerTypeCd: '',
                faceUrl: '',
                openId: '',
                state: '',
                remark: '',
            }
        },
        _initMethod: function () {

        },
        _initEvent: function () {
            vc.on('auditPersonFace', 'openAuditPersonFaceModal', function (_params) {
                $that.refreshAuditPersonFaceInfo();
                $('#auditPersonFaceModel').modal('show');
                vc.copyObject(_params, $that.auditPersonFaceInfo);
                $that.auditPersonFaceInfo.communityId = vc.getCurrentCommunity().communityId;
            });
        },
        methods: {
            auditPersonFaceValidate: function () {
                return vc.validate.validate({
                    auditPersonFaceInfo: $that.auditPersonFaceInfo
                }, {
                    'auditPersonFaceInfo.roomName': [
                        {
                            limit: "required",
                            param: "",
                            errInfo: "房屋名称不能为空"
                        },
                        {
                            limit: "maxLength",
                            param: "64",
                            errInfo: "房屋名称不能超过64"
                        },
                    ],
                    'auditPersonFaceInfo.pfqId': [
                        {
                            limit: "required",
                            param: "",
                            errInfo: "二维码不能为空"
                        },
                        {
                            limit: "maxLength",
                            param: "30",
                            errInfo: "二维码不能超过30"
                        },
                    ],
                    'auditPersonFaceInfo.personName': [
                        {
                            limit: "required",
                            param: "",
                            errInfo: "人员名称不能为空"
                        },
                        {
                            limit: "maxLength",
                            param: "64",
                            errInfo: "人员名称不能超过64"
                        },
                    ],
                    'auditPersonFaceInfo.personTel': [
                        {
                            limit: "required",
                            param: "",
                            errInfo: "人员电话不能为空"
                        },
                        {
                            limit: "maxLength",
                            param: "11",
                            errInfo: "人员电话不能超过11"
                        },
                    ],
                    'auditPersonFaceInfo.ownerTypeCd': [
                        {
                            limit: "required",
                            param: "",
                            errInfo: "1001不能为空"
                        },
                        {
                            limit: "maxLength",
                            param: "4",
                            errInfo: "1001不能超过4"
                        },
                    ],
                    'auditPersonFaceInfo.faceUrl': [
                        {
                            limit: "required",
                            param: "",
                            errInfo: "人脸不能为空"
                        },
                        {
                            limit: "maxLength",
                            param: "512",
                            errInfo: "人脸不能超过512"
                        },
                    ],
                    'auditPersonFaceInfo.openId': [
                        {
                            limit: "required",
                            param: "",
                            errInfo: "openId不能为空"
                        },
                        {
                            limit: "maxLength",
                            param: "64",
                            errInfo: "openId不能超过64"
                        },
                    ],
                    'auditPersonFaceInfo.state': [
                        {
                            limit: "required",
                            param: "",
                            errInfo: "状态不能为空"
                        },
                        {
                            limit: "maxLength",
                            param: "12",
                            errInfo: "状态不能超过12"
                        },
                    ],
                    'auditPersonFaceInfo.remark': [
                        {
                            limit: "required",
                            param: "",
                            errInfo: "审核说明不能为空"
                        },
                        {
                            limit: "maxLength",
                            param: "512",
                            errInfo: "审核说明不能超过512"
                        },
                    ],
                    'auditPersonFaceInfo.pfId': [
                        {
                            limit: "required",
                            param: "",
                            errInfo: "编号不能为空"
                        }]

                });
            },
            auditPersonFace: function () {
                if (!$that.auditPersonFaceValidate()) {
                    vc.toast(vc.validate.errInfo);
                    return;
                }

                vc.http.apiPost(
                    'personFace.updatePersonFace',
                    JSON.stringify($that.auditPersonFaceInfo),
                    {
                        emulateJSON: true
                    },
                    function (json, res) {
                        //vm.menus = vm.refreshMenuActive(JSON.parse(json),0);
                        let _json = JSON.parse(json);
                        if (_json.code == 0) {
                            //关闭model
                            $('#auditPersonFaceModel').modal('hide');
                            vc.emit('personFaceManage', 'listPersonFace', {});
                            return;
                        }
                        vc.message(_json.msg);
                    },
                    function (errInfo, error) {
                        console.log('请求失败处理');

                        vc.message(errInfo);
                    });
            },
            refreshAuditPersonFaceInfo: function () {
                $that.auditPersonFaceInfo = {
                    pfId: '',
                    roomName: '',
                    pfqId: '',
                    personName: '',
                    personTel: '',
                    ownerTypeCd: '',
                    faceUrl: '',
                    openId: '',
                    state: '',
                    remark: '',

                }
            }
        }
    });

})(window.vc, window.$that);
