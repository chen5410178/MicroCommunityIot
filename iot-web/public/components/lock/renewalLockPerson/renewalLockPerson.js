(function (vc, vm) {

    vc.extends({
        data: {
            renewalLockPersonInfo: {
                lpId: '',
                machineId: '',
                personId: '',
                name: '',
                openModel: '',
                state: '',
                communityId: '',
                cardNumber: ''
            }
        },
        _initMethod: function () {
            vc.initDate('renewalStartTime', function(_value) {
                $that.renewalLockPersonInfo.startTime = _value;
            });
            vc.initDate('renewalEndTime', function(_value) {
                $that.renewalLockPersonInfo.endTime = _value;
            });
        },
        _initEvent: function () {
            vc.on('renewalLockPerson', 'openRenewalLockPersonModal', function (_params) {
                $that.refreshRenewalLockPersonInfo();
                $('#renewalLockPersonModel').modal('show');
                vc.copyObject(_params, $that.renewalLockPersonInfo);
            });

        },
        methods: {
            renewalLockPersonValidate: function () {
                return vc.validate.validate({
                    renewalLockPersonInfo: $that.renewalLockPersonInfo
                }, {
                    'renewalLockPersonInfo.lpId': [
                        {
                            limit: "required",
                            param: "",
                            errInfo: "授权编号不能为空"
                        },
                    ],
                    'renewalLockPersonInfo.machineId': [
                        {
                            limit: "required",
                            param: "",
                            errInfo: "门锁不能为空"
                        },
                    ],
                    'renewalLockPersonInfo.personId': [
                        {
                            limit: "required",
                            param: "",
                            errInfo: "业主不能为空"
                        },
                    ],
                    'renewalLockPersonInfo.name': [
                        {
                            limit: "required",
                            param: "",
                            errInfo: "业主不能为空"
                        },
                    ],
                    'renewalLockPersonInfo.openModel': [
                        {
                            limit: "required",
                            param: "",
                            errInfo: "开锁方式不能为空"
                        },
                    ],
                    'renewalLockPersonInfo.state': [
                        {
                            limit: "required",
                            param: "",
                            errInfo: "授权状态不能为空"
                        },
                    ],
                    'renewalLockPersonInfo.communityId': [
                        {
                            limit: "required",
                            param: "",
                            errInfo: "小区不能为空"
                        },
                    ],
                    'renewalLockPersonInfo.startTime': [
                        {
                            limit: "required",
                            param: "",
                            errInfo: "续期开始时间不能为空"
                        },
                    ],
                    'renewalLockPersonInfo.endTime': [
                        {
                            limit: "required",
                            param: "",
                            errInfo: "续期结束时间不能为空"
                        },
                    ],
                });
            },
            renewalLockPerson: function () {
                if (!$that.renewalLockPersonValidate()) {
                    vc.toast(vc.validate.errInfo);
                    return;
                }

                vc.http.apiPost(
                    '/lockPerson.renewalLockPerson',
                    JSON.stringify($that.renewalLockPersonInfo),
                    {
                        emulateJSON: true
                    },
                    function (json, res) {
                        let _json = JSON.parse(json);
                        if (_json.code === 0) {
                            //关闭model
                            $('#renewalLockPersonModel').modal('hide');
                            vc.emit('lockPersonManage', 'listLockPerson', {});
                            vc.toast("操作成功");
                            return;
                        }
                        vc.toast(_json.msg);
                    },
                    function (errInfo, error) {
                        console.log('请求失败处理');
                        vc.toast(errInfo);
                    });
            },
            refreshRenewalLockPersonInfo: function () {
                $that.renewalLockPersonInfo = {
                    lpId: '',
                    machineId: '',
                    personId: '',
                    name: '',
                    openModel: '',
                    state: '',
                    communityId: '',
                    cardNumber: ''
                }
            },
        }
    });
})(window.vc, window.$that);
