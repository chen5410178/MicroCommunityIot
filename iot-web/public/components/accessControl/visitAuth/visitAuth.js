(function (vc, vm) {

    vc.extends({
        data: {
            editVisitInfo: {
                visitId: '',
                name: '',
                visitGender: '',
                phoneNumber: '',
                communityId: '',
                roomName: '',
                roomId: '',
                ownerId: '',
                ownerName: '',
                visitTime: '',
                departureTime: '',
                typeId: '',
                visitCase: '',
                facePath: '',
                qrcode: '',
                state: '',
                msg: '',
            },
            stateList: []
        },
        _initMethod: function () {
            vc.getDict('visit', 'state', function (_data) {
                $that.stateList = _data;
            });
        },
        _initEvent: function () {
            vc.on('doVisitAuth', 'doVisitAuthModal', function (_params) {
                $that.refreshEditVisitInfo();
                $('#visitAuthModel').modal('show');
                vc.copyObject(_params, $that.editVisitInfo);
            });
        },
        methods: {
            editVisitValidate: function () {
                return vc.validate.validate({
                    editVisitInfo: $that.editVisitInfo
                }, {
                    'editVisitInfo.visitId': [
                        {
                            limit: "required",
                            param: "",
                            errInfo: "拜访ID不能为空"
                        },
                    ],
                    'editVisitInfo.name': [
                        {
                            limit: "required",
                            param: "",
                            errInfo: "姓名不能为空"
                        },
                        {
                            limit: "maxLength",
                            param: "255",
                            errInfo: "姓名不能超过255"
                        },
                    ],
                    'editVisitInfo.visitGender': [
                        {
                            limit: "required",
                            param: "",
                            errInfo: "性别不能为空"
                        },
                    ],
                    'editVisitInfo.phoneNumber': [
                        {
                            limit: "required",
                            param: "",
                            errInfo: "电话号码不能为空"
                        },
                        {
                            limit: "phone",
                            param: "",
                            errInfo: "电话号码格式错误"
                        },
                    ],
                    'editVisitInfo.roomName': [
                        {
                            limit: "required",
                            param: "",
                            errInfo: "拜访房屋不能为空"
                        },
                    ],
                    'editVisitInfo.roomId': [
                        {
                            limit: "required",
                            param: "",
                            errInfo: "拜访房屋不能为空"
                        },
                    ],
                    'editVisitInfo.typeId': [
                        {
                            limit: "required",
                            param: "",
                            errInfo: "访客类型不能为空"
                        },
                    ],
                    'editVisitInfo.visitCase': [
                        {
                            limit: "maxLength",
                            param: "255",
                            errInfo: "拜访事由不能超过255"
                        },
                    ],
                    'editVisitInfo.state': [
                        {
                            limit: "required",
                            param: "",
                            errInfo: "审核状态不能为空"
                        },
                    ],
                });
            },
            visitAuth: function () {
                if (!$that.editVisitValidate()) {
                    vc.toast(vc.validate.errInfo);
                    return;
                }

                vc.http.apiPost(
                    '/visit.updateVisit',
                    JSON.stringify($that.editVisitInfo),
                    {
                        emulateJSON: true
                    },
                    function (json, res) {
                        let _json = JSON.parse(json);
                        if (_json.code === 0) {
                            //关闭model
                            $('#visitAuthModel').modal('hide');
                            vc.emit('visitManage', 'listVisit', {});
                            return;
                        }
                        vc.toast(_json.msg);
                    },
                    function (errInfo, error) {
                        console.log('请求失败处理');
                        vc.toast(errInfo);
                    });
            },
            refreshEditVisitInfo: function () {
                $that.editVisitInfo = {
                    visitId: '',
                    name: '',
                    visitGender: '',
                    phoneNumber: '',
                    communityId: '',
                    roomName: '',
                    roomId: '',
                    ownerId: '',
                    ownerName: '',
                    visitTime: '',
                    departureTime: '',
                    typeId: '',
                    visitCase: '',
                    facePath: '',
                    qrcode: '',
                    state: '',
                    msg: '',
                }
            }
        }
    });
})(window.vc, window.$that);
