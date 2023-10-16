(function (vc, vm) {

    vc.extends({
        data: {
            editEventPoolStaffInfo: {
                epsId: '',
                eventId: '',
                staffId: '',
                staffName: '',
                eventReason: '',
                eventResult: '',
                communityId: '',
                state: '',
            }
        },
        _initMethod: function () {

        },
        _initEvent: function () {
            vc.on('editEventPoolStaff', 'openEditEventPoolStaffModal', function (_params) {
                $that.queryEvent(_params.eventId);
                $that.refreshEditEventPoolStaffInfo();
                vc.copyObject(_params, $that.editEventPoolStaffInfo);
                $('#editEventPoolStaffModel').modal('show');
            });
        },
        methods: {
            editEventPoolStaffValidate: function () {
                return vc.validate.validate({
                    editEventPoolStaffInfo: $that.editEventPoolStaffInfo
                }, {
                    'editEventPoolStaffInfo.epsId': [
                        {
                            limit: "required",
                            param: "",
                            errInfo: "主键ID不能为空"
                        },
                    ],
                    'editEventPoolStaffInfo.eventId': [
                        {
                            limit: "required",
                            param: "",
                            errInfo: "事件ID不能为空"
                        },
                    ],
                    'editEventPoolStaffInfo.staffId': [
                        {
                            limit: "required",
                            param: "",
                            errInfo: "处理员工不能为空"
                        },
                    ],
                    'editEventPoolStaffInfo.staffName': [
                        {
                            limit: "required",
                            param: "",
                            errInfo: "处理员工不能为空"
                        },
                    ],
                    'editEventPoolStaffInfo.eventReason': [
                        {
                            limit: "required",
                            param: "",
                            errInfo: "事故原因不能为空"
                        },
                        {
                            limit: "maxLength",
                            param: "512",
                            errInfo: "事故原因不能超过512"
                        },
                    ],
                    'editEventPoolStaffInfo.eventResult': [
                        {
                            limit: "required",
                            param: "",
                            errInfo: "处理方案不能为空"
                        },
                        {
                            limit: "maxLength",
                            param: "512",
                            errInfo: "处理方案不能超过512"
                        },
                    ],
                    'editEventPoolStaffInfo.communityId': [
                        {
                            limit: "required",
                            param: "",
                            errInfo: "小区ID不能为空"
                        },
                    ],
                    'editEventPoolStaffInfo.state': [
                        {
                            limit: "required",
                            param: "",
                            errInfo: "状态不能为空"
                        },
                    ],
                });
            },
            editEventPoolStaff: function () {
                
                if (!$that.editEventPoolStaffValidate()) {
                    vc.toast(vc.validate.errInfo);
                    return;
                }

                vc.http.apiPost(
                    '/eventPoolStaff.updateEventPoolStaff',
                    JSON.stringify($that.editEventPoolStaffInfo),
                    {
                        emulateJSON: true
                    },
                    function (json, res) {
                        let _json = JSON.parse(json);
                        if (_json.code === 0) {
                            //关闭model
                            $('#editEventPoolStaffModel').modal('hide');
                            vc.emit('eventPoolStaffManage', 'listEventPoolStaff', {});
                            vc.toast('已接受分配事件,请尽快处理');
                            return;
                        }
                        vc.toast(_json.msg);
                    },
                    function (errInfo, error) {
                        console.log('请求失败处理');
                        vc.toast(errInfo);
                    });
            },
            refreshEditEventPoolStaffInfo: function () {
                $that.editEventPoolStaffInfo = {
                    epsId: '',
                    eventId: '',
                    staffId: '',
                    staffName: '',
                    eventReason: '',
                    eventResult: '',
                    communityId: '',
                    state: '',
                }
            },
            queryEvent: function (_eventId) {
                var param = {
                    params: {
                        page: -1,
                        row: 1,
                        eventId: _eventId
                    }
                }

                vc.http.apiGet('/eventPool.listEventPool',
                    param,
                    function (json, res) {
                        var _eventPoolManageInfo = JSON.parse(json);
                        $that.event = _eventPoolManageInfo.data[0];
                    }, function (errInfo, error) {
                        console.log('请求失败处理');
                    }
                );
            }
        }
    });
})(window.vc, window.$that);
