(function (vc) {
    vc.extends({
        data: {
            editWorkLicenseMachineInfo: {
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
            $that.editWorkLicenseMachineInfo.machineId = vc.getParam('machineId');

            $that._listWorkLicenseMachines();
        },
        _initEvent: function () {

        },
        methods: {
            editWorkLicenseMachineValidate() {
                return vc.validate.validate({
                    editWorkLicenseMachineInfo: $that.editWorkLicenseMachineInfo
                }, {
                    'editWorkLicenseMachineInfo.machineName': [
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
                    'editWorkLicenseMachineInfo.machineCode': [
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
                    'editWorkLicenseMachineInfo.staffId': [
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
                    'editWorkLicenseMachineInfo.posUploadSec': [
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
                    'editWorkLicenseMachineInfo.implBean': [
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
                    'editWorkLicenseMachineInfo.version': [
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
                if (!$that.editWorkLicenseMachineValidate()) {
                    vc.toast(vc.validate.errInfo);
                    return;
                }
                vc.http.apiPost(
                    '/workLicense.updateWorkLicenseMachine',
                    JSON.stringify($that.editWorkLicenseMachineInfo), {
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
                vc.emit('searchStaff', 'openSearchStaffModel', $that.editWorkLicenseMachineInfo);
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
                        $that.editWorkLicenseMachineInfo.factorys = _json.data;
                    }, function (errInfo, error) {
                        console.log('请求失败处理');
                    }
                );
            },
            _listWorkLicenseMachines: function (_page, _rows) {

                let param = {
                    params: {
                        page:1,
                        row:1,
                        machineId:$that.editWorkLicenseMachineInfo.machineId
                    }
                };

                //发送get请求
                vc.http.apiGet('/workLicense.listWorkLicenseMachine',
                    param,
                    function (json, res) {
                        let _json = JSON.parse(json);
                        vc.copyObject(_json.data[0],$that.editWorkLicenseMachineInfo);
                       
                    }, function (errInfo, error) {
                        console.log('请求失败处理');
                    }
                );
            },
        }
    });
})(window.vc);