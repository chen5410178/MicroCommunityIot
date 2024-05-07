(function (vc) {
    vc.extends({
        data: {
            addWorkLicenseMachineInfo: {
                machineId: '',
                machineName: '',
                machineCode: '',
                staffId: '',
                staffName:'',
                posUploadSec: '',
                implBean: '',
                version: '',
                factorys:[]
            }
        },
        _initMethod: function () {
            $that._listWorkLicenseFactorys();
        },
        _initEvent: function () {

        },
        methods: {
            addWorkLicenseMachineValidate() {
                return vc.validate.validate({
                    addWorkLicenseMachineInfo: $that.addWorkLicenseMachineInfo
                }, {
                    'addWorkLicenseMachineInfo.machineName': [
                        {
                            limit: "required",
                            param: "",
                            errInfo: "名称不能为空"
                        },
                        {
                            limit: "maxLength",
                            param: "200",
                            errInfo: "名称不能超过200"
                        },
                    ],
                    'addWorkLicenseMachineInfo.machineCode': [
                        {
                            limit: "required",
                            param: "",
                            errInfo: "工牌编号不能为空"
                        },
                        {
                            limit: "maxLength",
                            param: "64",
                            errInfo: "工牌编号不能超过64"
                        },
                    ],
                    'addWorkLicenseMachineInfo.staffId': [
                        {
                            limit: "required",
                            param: "",
                            errInfo: "员工不能为空"
                        },
                        {
                            limit: "maxLength",
                            param: "64",
                            errInfo: "员工不能超过64"
                        },
                    ],
                    'addWorkLicenseMachineInfo.posUploadSec': [
                        {
                            limit: "required",
                            param: "",
                            errInfo: "位置上报间隔不能为空"
                        },
                        {
                            limit: "maxLength",
                            param: "64",
                            errInfo: "位置上报间隔不能超过64"
                        },
                    ],
                    'addWorkLicenseMachineInfo.implBean': [
                        {
                            limit: "required",
                            param: "",
                            errInfo: "工牌厂家不能为空"
                        },
                        {
                            limit: "maxLength",
                            param: "30",
                            errInfo: "工牌厂家不能超过30"
                        },
                    ],
                    'addWorkLicenseMachineInfo.version': [
                        {
                            limit: "required",
                            param: "",
                            errInfo: "工牌版本不能为空"
                        },
                        {
                            limit: "maxLength",
                            param: "30",
                            errInfo: "工牌版本不能超过30"
                        },
                    ],
                });
            },
            _saveWorkLicenseMachine: function () {
                if (!$that.addWorkLicenseMachineValidate()) {
                    vc.toast(vc.validate.errInfo);
                    return;
                }
                vc.http.apiPost(
                    '/workLicense.saveWorkLicenseMachine',
                    JSON.stringify($that.addWorkLicenseMachineInfo), {
                    emulateJSON: true
                },
                    function (json, res) {
                        let _json = JSON.parse(json)
                        if (_json.code == 0) {
                            vc.goBack();
                            vc.toast("添加成功");
                            return;
                        } else {
                            vc.toast(_json.msg);
                        }
                    },
                    function (errInfo, error) {
                        vc.toast(errInfo);
                    });
            },
            openChooseStaff: function () {
                vc.emit('searchStaff', 'openSearchStaffModel', $that.addWorkLicenseMachineInfo);
            },
            _listWorkLicenseFactorys: function () {
                let param = {
                    params: {
                        page:1,
                        row:100
                    }
                };

                //发送get请求
                vc.http.apiGet('/workLicenseFactory.listWorkLicenseFactory',
                    param,
                    function (json, res) {
                        let _json = JSON.parse(json);
                        $that.addWorkLicenseMachineInfo.factorys = _json.data;
                    }, function (errInfo, error) {
                        console.log('请求失败处理');
                    }
                );
            },
        }
    });
})(window.vc);