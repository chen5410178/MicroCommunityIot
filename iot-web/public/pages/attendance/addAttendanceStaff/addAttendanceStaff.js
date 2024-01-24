(function (vc) {
    vc.extends({
        data: {
            addAttendanceStaffInfo: {
                csId: '',
                machineId: '',
                staffId: '',
                staffName: '',
                facePath: ''
            }
        },
        _initMethod: function () {
            $that.addAttendanceStaffInfo.machineId = vc.getParam('machineId');
        },
        _initEvent: function () {
            vc.on("addAttendanceStaff", "notifyUploadImage", function (_param) {
                if (_param.length > 0) {
                    $that.addAttendanceStaffInfo.facePath = _param[0].url;
                }
            });
        },
        methods: {
            addAttendanceStaffValidate() {
                return vc.validate.validate({
                    addAttendanceStaffInfo: $that.addAttendanceStaffInfo
                }, {
                    'addAttendanceStaffInfo.machineId': [
                        {
                            limit: "required",
                            param: "",
                            errInfo: "设备ID不能为空"
                        }
                    ],
                    'addAttendanceStaffInfo.staffId': [
                        {
                            limit: "required",
                            param: "",
                            errInfo: "员工名称不能为空"
                        }
                    ]
                });
            },
            saveAttendanceStaffInfo: function () {
                if (!$that.addAttendanceStaffValidate()) {
                    vc.toast(vc.validate.errInfo);
                    return;
                }
                $that.addAttendanceStaffInfo.communityId = vc.getCurrentCommunity().communityId;
                vc.http.apiPost(
                    '/attendance.saveAttendanceStaff',
                    JSON.stringify($that.addAttendanceStaffInfo),
                    {
                        emulateJSON: true
                    },
                    function (json, res) {
                        //vm.menus = vm.refreshMenuActive(JSON.parse(json),0);
                        let _json = JSON.parse(json);
                        if (_json.code == 0) {
                            //关闭model
                            $('#addAttendanceStaffModel').modal('hide');
                            vc.goBack();
                            vc.toast("添加成功");
                            return;
                        } else {
                            vc.toast(_json.msg);
                        }
                    },
                    function (errInfo, error) {
                        console.log('请求失败处理');
                        vc.message(errInfo);
                    });
            },
            openChooseStaff: function () {
                vc.emit('searchStaff', 'openSearchStaffModel', $that.addAttendanceStaffInfo);
            },
            _goBack: function () {
                vc.goBack();
            }
        }
    });
})(window.vc);
