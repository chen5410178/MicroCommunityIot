(function (vc) {

    vc.extends({
        data: {
            addEventStaffInfo: {
                esId: '',
                ruleId: '',
                communityId: '',
                state: '',
                remark: '',
                staffIdAndNameList: [],
            },
            staffNameOnlyView: [],
        },
        _initMethod: function () {
            $that.addEventStaffInfo.ruleId = vc.getParam('ruleId');
        },
        _initEvent: function () {
            vc.on('addEventStaff', 'openAddEventStaffModal', function () {
                $('#addEventStaffModel').modal('show');
            });
            vc.on('', 'listStaffData', function (_data) {
                debugger
                _data.map(staff =>{
                    $that.addEventStaffInfo.staffIdAndNameList.push(
                        {
                            staffId: staff.userId,
                            staffName: staff.name
                        });
                    $that.staffNameOnlyView += staff.name + '\r\n';
                })
            });
        },
        methods: {
            addEventStaffValidate() {
                return vc.validate.validate({
                    addEventStaffInfo: $that.addEventStaffInfo
                }, {
                    'addEventStaffInfo.ruleId': [
                        {
                            limit: "required",
                            param: "",
                            errInfo: "规则ID不能为空"
                        },
                    ],
                    'addEventStaffInfo.staffIdAndNameList': [
                        {
                            limit: "required",
                            param: "",
                            errInfo: "物业员工不能为空"
                        },
                    ],
                    'addEventStaffInfo.communityId': [
                        {
                            limit: "required",
                            param: "",
                            errInfo: "小区ID不能为空"
                        },
                    ],
                    'addEventStaffInfo.state': [
                        {
                            limit: "required",
                            param: "",
                            errInfo: "状态不能为空"
                        },
                    ],
                    'addEventStaffInfo.remark': [
                        {
                            limit: "maxLength",
                            param: "512",
                            errInfo: "备注不能超过512"
                        },
                    ],
                });
            },
            saveEventStaffInfo: function () {

                $that.addEventStaffInfo.communityId = vc.getCurrentCommunity().communityId;

                if (!$that.addEventStaffValidate()) {
                    vc.toast(vc.validate.errInfo);
                    return;
                }

                vc.http.apiPost(
                    '/eventStaff.saveEventStaff',
                    JSON.stringify($that.addEventStaffInfo),
                    {
                        emulateJSON: true
                    },
                    function (json, res) {
                        let _json = JSON.parse(json);
                        if (_json.code === 0) {
                            $that.clearAddEventStaffInfo();
                            vc.emit('eventStaffManage', 'listEventStaff', {});
                            vc.getBack();
                        }
                        vc.toast(_json.msg);
                    },
                    function (errInfo, error) {
                        console.log('请求失败处理');
                        vc.toast(errInfo);
                    });
            },
            clearAddEventStaffInfo: function () {
                $that.addEventStaffInfo = {
                    esId: '',
                    ruleId: '',
                    communityId: '',
                    state: '',
                    remark: '',
                    staffIdAndNameList: [],
                };
            },
            selectStaff: function () {
                $that.addEventStaffInfo.staffIdAndNameList = [];
                $that.staffNameOnlyView = '';
                vc.emit('chooseStaff', 'openChooseStaffModel', '');
            },
        }
    });

})(window.vc);
