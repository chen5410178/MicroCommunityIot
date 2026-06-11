(function (vc) {
    vc.extends({
        data: {
            prestoreAccountInfo: {
                tel: '',
                ownerId: '',
                amount: '',
                swipeCardNo: '',
                remark: '',
                acctTypes: [],
                acctType: '2003',
                primeRate: '',
                primeRates: [],
            }
        },
        _initMethod: function () {
            vc.getDict('pay_fee_detail', "prime_rate", function (_data) {
                $that.prestoreAccountInfo.primeRates = _data;
            });
        },
        _initEvent: function () {
            vc.on('prestoreAccount', 'openAddModal', function (_param) {
                $('#prestoreAccountModel').modal('show');
                //与字典表单位关联
                vc.getDict('account', "acct_type", function (_data) {
                    $that.prestoreAccountInfo.acctTypes = _data;
                });
                let _tel = _param.tel;
                if (_tel) {
                    $that.prestoreAccountInfo.tel = _tel;
                    $that.prestoreAccountInfo.acctType = _param.acctType;
                }
            });
            vc.on('prestoreAccount', 'openAddModalWithParams', function (_param) {
                $that.prestoreAccountInfo.amount = _param.redepositAmount;
                $('#prestoreAccountModel').modal('show');
            });
        },
        methods: {
            prestoreAccountValidate() {
                return vc.validate.validate({
                    prestoreAccountInfo: $that.prestoreAccountInfo
                }, {
                    'prestoreAccountInfo.amount': [
                        {
                            limit: "required",
                            param: "",
                            errInfo: "金额不能为空"
                        }
                    ],
                    'prestoreAccountInfo.acctType': [
                        {
                            limit: "required",
                            param: "",
                            errInfo: "请选择账户类型"
                        }
                    ],
                    'prestoreAccountInfo.remark': [
                        {
                            limit: "maxLength",
                            param: "200",
                            errInfo: "备注长度不能超过200位"
                        }
                    ]
                });
            },
            savePrestoreAccountInfo: function () {
                if (!$that.prestoreAccountValidate()) {
                    vc.toast(vc.validate.errInfo);
                    return;
                }
                $that.prestoreAccountInfo.communityId = vc.getCurrentCommunity().communityId;
                vc.http.apiPost(
                    '/account.ownerPrestoreAccount',
                    JSON.stringify($that.prestoreAccountInfo), {
                    emulateJSON: true
                },
                    function (json, res) {
                        //vm.menus = vm.refreshMenuActive(JSON.parse(json),0);
                        let _json = JSON.parse(json);
                        if (_json.code == 0) {
                            //关闭model
                            $('#prestoreAccountModel').modal('hide');
                            $that.clearPrestoreAccountInfo();
                            vc.emit('account', 'listAccount', {});
                            vc.emit('ownerDetailAccount', 'notify', {});
                            vc.toast('预存成功');
                            return;
                        } else {
                            vc.toast(_json.msg);
                        }
                        $that.prestoreAccountInfo.errorInfo = json;
                    },
                    function (errInfo, error) {
                        console.log('请求失败处理');
                        $that.prestoreAccountInfo.errorInfo = errInfo;
                    });
            },
            clearPrestoreAccountInfo: function () {
                let _primeRates = $that.prestoreAccountInfo.primeRates;
                $that.prestoreAccountInfo = {
                    tel: '',
                    amount: '',
                    swipeCardNo: '',
                    remark: '',
                    acctTypes: [],
                    acctType: '2003',
                    primeRate: '',
                    primeRates: _primeRates,
                };
            },
        }
    });
})(window.vc);