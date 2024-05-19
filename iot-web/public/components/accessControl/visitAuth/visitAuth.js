(function (vc, vm) {

    vc.extends({
        data: {
            visitAuthInfo: {
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
                $that.refreshvisitAuthInfo();
                $('#visitAuthModel').modal('show');
                vc.copyObject(_params, $that.visitAuthInfo);
            });
        },
        methods: {
            visitAuthValidate: function () {
                return vc.validate.validate({
                    visitAuthInfo: $that.visitAuthInfo
                }, {
                    'visitAuthInfo.visitId': [
                        {
                            limit: "required",
                            param: "",
                            errInfo: "拜访ID不能为空"
                        },
                    ],
                    'visitAuthInfo.state': [
                        {
                            limit: "required",
                            param: "",
                            errInfo: "审核状态不能为空"
                        },
                    ],
                });
            },
            visitAuth: function () {
                if (!$that.visitAuthValidate()) {
                    vc.toast(vc.validate.errInfo);
                    return;
                }

                vc.http.apiPost(
                    '/visit.propertyAuthVisit',
                    JSON.stringify($that.visitAuthInfo),
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
            refreshvisitAuthInfo: function () {
                $that.visitAuthInfo = {
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
