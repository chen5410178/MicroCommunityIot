(function (vc) {

    vc.extends({
        propTypes: {
            callBackListener: vc.propTypes.string, //父组件名称
            callBackFunction: vc.propTypes.string //父组件监听方法
        },
        data: {
            addEventPoolStaffInfo: {
                epsId: '',
                eventId: '',
                staffId: '',
                staffName: '',
                eventReason: '',
                eventResult: '',
                communityId: '',
                state: 'W',
            },
            incompleteEventList: [],
            staffList: [],
        },
        _initMethod: function () {
            $that.listStaffList();
        },
        _initEvent: function () {
            vc.on('addEventPoolStaff', 'openAddEventPoolStaffModal', function (_eventPool) {
                $that.addEventPoolStaffInfo.eventId = _eventPool.eventId;
                $('#addEventPoolStaffModel').modal('show');
            });
        },
        methods: {
            addEventPoolStaffValidate() {
                return vc.validate.validate({
                    addEventPoolStaffInfo: $that.addEventPoolStaffInfo
                }, {
                    'addEventPoolStaffInfo.eventId': [
                        {
                            limit: "required",
                            param: "",
                            errInfo: "事件ID不能为空"
                        },
                    ],
                    'addEventPoolStaffInfo.staffId': [
                        {
                            limit: "required",
                            param: "",
                            errInfo: "处理员工不能为空"
                        },
                    ],
                    'addEventPoolStaffInfo.staffName': [
                        {
                            limit: "required",
                            param: "",
                            errInfo: "处理员工不能为空"
                        },
                    ],
                    'addEventPoolStaffInfo.eventReason': [
                        {
                            limit: "maxLength",
                            param: "512",
                            errInfo: "事故原因不能超过512"
                        },
                    ],
                    'addEventPoolStaffInfo.eventResult': [
                        {
                            limit: "maxLength",
                            param: "512",
                            errInfo: "处理方案不能超过512"
                        },
                    ],
                    'addEventPoolStaffInfo.eventHours': [
                        {
                            limit: "required",
                            param: "",
                            errInfo: "处理时长不能为空"
                        },
                        {
                            limit: "num",
                            param: "",
                            errInfo: "处理时长必须为整数"
                        },
                    ],
                    'addEventPoolStaffInfo.communityId': [
                        {
                            limit: "required",
                            param: "",
                            errInfo: "小区ID不能为空"
                        },
                    ],
                    'addEventPoolStaffInfo.state': [
                        {
                            limit: "required",
                            param: "",
                            errInfo: "状态不能为空"
                        },
                    ],
                });
            },
            saveEventPoolStaffInfo: function () {
                $that.addEventPoolStaffInfo.communityId = vc.getCurrentCommunity().communityId;
                for (let i = 0; i < $that.staffList.length; i++) {
                    if ($that.staffList[i].userId === $that.addEventPoolStaffInfo.staffId) {
                        $that.addEventPoolStaffInfo.staffName = $that.staffList[i].name;
                        break;
                    }
                }

                if (!$that.addEventPoolStaffValidate()) {
                    vc.toast(vc.validate.errInfo);
                    return;
                }

                //不提交数据将数据 回调给侦听处理
                if (vc.notNull($props.callBackListener)) {
                    vc.emit($props.callBackListener, $props.callBackFunction, $that.addEventPoolStaffInfo);
                    $('#addEventPoolStaffModel').modal('hide');
                    return;
                }

                vc.http.apiPost(
                    '/eventPoolStaff.saveEventPoolStaff',
                    JSON.stringify($that.addEventPoolStaffInfo),
                    {
                        emulateJSON: true
                    },
                    function (json, res) {
                        let _json = JSON.parse(json);
                        if (_json.code === 0) {
                            //关闭model
                            $('#addEventPoolStaffModel').modal('hide');
                            vc.emit('eventPoolManage', 'addEventPoolStaffSuccess', $that.addEventPoolStaffInfo.eventId);
                            $that.clearAddEventPoolStaffInfo();
                            return;
                        }
                        vc.toast(_json.msg);
                    },
                    function (errInfo, error) {
                        console.log('请求失败处理');
                        vc.toast(errInfo);
                    });
            },
            clearAddEventPoolStaffInfo: function () {
                $that.addEventPoolStaffInfo = {
                    epsId: '',
                    eventId: '',
                    staffId: '',
                    staffName: '',
                    eventReason: '',
                    eventResult: '',
                    communityId: '',
                    state: 'W',
                };
            },
            listStaffList: function () {
                let param = {
                    params: {
                        page: -1,
                        row: 100,
                        communityId: vc.getCurrentCommunity().communityId,
                    }
                };
                //发送get请求
                vc.http.apiGet('/user.listCommunityStaffsByCommunityId',
                    param,
                    function (json, res) {
                        let _json = JSON.parse(json);
                        $that.staffList = _json.data;
                    }, function (errInfo, error) {
                        console.log('请求失败处理');
                    }
                );
            },
        }
    });
})(window.vc);
