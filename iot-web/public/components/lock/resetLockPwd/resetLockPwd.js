(function (vc) {
    vc.extends({
        data: {
            resetLockPwdInfo: {
                lpId: '',
                machineId: '',
                personId: '',
                name: '',
                startTime: '',
                endTime: '',
                openModel: '',
                state: '',
                communityId: '',
            }
        },
        _initEvent: function () {
            vc.on('resetLockPwd', 'openResetLockPwd', function (_lockPerson) {
                $that.clearResetLockPwdInfo();
                vc.copyObject(_lockPerson, $that.resetLockPwdInfo);
                $('#resetLockPwdModel').modal('show');
            });
        },
        methods: {
            resetLockPwdValidate: function () {
                return vc.validate.validate({
                    resetLockPwdInfo: $that.resetLockPwdInfo
                }, {
                    'resetLockPwdInfo.lpId': [
                        {
                            limit: "required",
                            param: "",
                            errInfo: "主键不能为空"
                        },
                    ],
                    'resetLockPwdInfo.machineId': [
                        {
                            limit: "required",
                            param: "",
                            errInfo: "门锁不能为空"
                        },
                    ],
                    'resetLockPwdInfo.startTime': [
                        {
                            limit: "required",
                            param: "",
                            errInfo: "开始时间不能为空"
                        },
                    ],
                    'resetLockPwdInfo.endTime': [
                        {
                            limit: "required",
                            param: "",
                            errInfo: "结束时间不能为空"
                        },
                    ],
                    'resetLockPwdInfo.openModel': [
                        {
                            limit: "required",
                            param: "",
                            errInfo: "开锁方式不能为空"
                        },
                    ],
                    'resetLockPwdInfo.state': [
                        {
                            limit: "required",
                            param: "",
                            errInfo: "授权状态不能为空"
                        },
                    ],
                    'resetLockPwdInfo.communityId': [
                        {
                            limit: "required",
                            param: "",
                            errInfo: "小区不能为空"
                        },
                    ],
                    'resetLockPwdInfo.cardNumber': [
                        {
                            limit: "required",
                            param: "",
                            errInfo: "密码不能为空"
                        },
                        {
                            limit: "maxLength",
                            param: "64",
                            errInfo: "密码长度不能超过64"
                        },
                    ],
                });
            },
            closeResetLockPwdModel: function () {
                $('#resetLockPwdModel').modal('hide');
            },
            resetLockPwd: function () {
                if (!$that.resetLockPwdValidate()) {
                    vc.toast(vc.validate.errInfo);
                    return;
                }
                vc.http.apiPost(
                    '/lockPerson.resetLockPwd',
                    JSON.stringify($that.resetLockPwdInfo), {
                        emulateJSON: true
                    },
                    function (json, res) {
                        let _json = JSON.parse(json);
                        if (_json.code === 0) {
                            //关闭model
                            $('#resetLockPwdModel').modal('hide');
                            vc.toast("密码修改成功", 5 * 1000);
                            return;
                        } else {
                            vc.toast(_json.msg);
                        }
                    },
                    function (errInfo, error) {
                        console.log('请求失败处理');
                        $that.resetLockPwdInfo.errorInfo = errInfo;
                    }
                );
            },
            clearResetLockPwdInfo: function () {
                $that.resetLockPwdInfo = {
                    lpId: '',
                    machineId: '',
                    personId: '',
                    name: '',
                    startTime: '',
                    endTime: '',
                    openModel: '',
                    state: '',
                    communityId: '',
                }
            }
        }
    });
})(window.vc);