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
                    '/personFace.authPersonFace',
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
                        vc.toast(_json.msg);
                    },
                    function (errInfo, error) {
                        console.log('请求失败处理');

                        vc.toast(errInfo);
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
