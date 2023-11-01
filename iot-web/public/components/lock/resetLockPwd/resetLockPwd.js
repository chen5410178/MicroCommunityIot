(function (vc) {
    vc.extends({
        data: {
            resetLockPwdInfo: {}
        },
        _initEvent: function () {
            vc.on('resetLockPwd', 'openResetLockPwd', function (_lockPerson) {
                $that.resetLockPwdInfo = _lockPerson;
                $('#resetLockPwdModel').modal('show');
            });
        },
        methods: {
            closeResetLockPwdModel: function () {
                $('#resetLockPwdModel').modal('hide');
            },
            resetLockPwd: function () {
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
                            vc.toast("修改密码成功，密码为" + _json.pwd + "请及时修改密码", 10 * 1000);
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
            }
        }
    });
})(window.vc);