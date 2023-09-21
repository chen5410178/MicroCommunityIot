(function(vc, vm) {
    vc.extends({
        data: {
            editCarBlackWhiteInfo: {
                bwId: '',
                blackWhite: '',
                carNum: '',
                startTime: '',
                endTime: ''
            }
        },
        _initMethod: function() {
            $that._initEditCarBlackWhiteDateInfo();
        },
        _initEvent: function() {
            vc.on('editCarBlackWhite', 'openEditCarBlackWhiteModal',
                function(_params) {
                    $that.refreshEditCarBlackWhiteInfo();
                    $('#editCarBlackWhiteModel').modal('show');
                    vc.copyObject(_params, $that.editCarBlackWhiteInfo);
                    $that.editCarBlackWhiteInfo.communityId = vc.getCurrentCommunity().communityId;
                });
        },
        methods: {
            _initEditCarBlackWhiteDateInfo: function() {
                $that.editCarBlackWhiteInfo.startTime = vc.dateTimeFormat(new Date().getTime());
                $('.editCarBlackWhiteStartTime').datetimepicker({
                    language: 'zh-CN',
                    fontAwesome: 'fa',
                    format: 'yyyy-mm-dd hh:ii:ss',
                    initTime: true,
                    initialDate: new Date(),
                    autoClose: 1,
                    todayBtn: true
                });
                $('.editCarBlackWhiteStartTime').datetimepicker()
                    .on('changeDate', function(ev) {
                        var value = $(".editCarBlackWhiteStartTime").val();
                        $that.editCarBlackWhiteInfo.startTime = value;
                    });
                $('.editCarBlackWhiteEndTime').datetimepicker({
                    language: 'zh-CN',
                    fontAwesome: 'fa',
                    format: 'yyyy-mm-dd hh:ii:ss',
                    initTime: true,
                    initialDate: new Date(),
                    autoClose: 1,
                    todayBtn: true
                });
                $('.editCarBlackWhiteEndTime').datetimepicker()
                    .on('changeDate', function(ev) {
                        var value = $(".editCarBlackWhiteEndTime").val();
                        var start = Date.parse(new Date($that.editCarBlackWhiteInfo.startTime))
                        var end = Date.parse(new Date(value))
                        if (start - end >= 0) {
                            vc.toast("结束时间必须大于开始时间")
                            $(".editCarBlackWhiteEndTime").val('')
                        } else {
                            $that.editCarBlackWhiteInfo.endTime = value;
                        }
                    });
                //防止多次点击时间插件失去焦点
                document.getElementsByClassName('form-control editCarBlackWhiteStartTime')[0].addEventListener('click', myfunc)

                function myfunc(e) {
                    e.currentTarget.blur();
                }

                document.getElementsByClassName("form-control editCarBlackWhiteEndTime")[0].addEventListener('click', myfunc)

                function myfunc(e) {
                    e.currentTarget.blur();
                }
            },
            editCarBlackWhiteValidate: function() {
                return vc.validate.validate({
                    editCarBlackWhiteInfo: $that.editCarBlackWhiteInfo
                }, {
                    'editCarBlackWhiteInfo.blackWhite': [{
                            limit: "required",
                            param: "",
                            errInfo: "名单类型不能为空"
                        },
                        {
                            limit: "num",
                            param: "",
                            errInfo: "名单类型格式错误"
                        },
                    ],
                    'editCarBlackWhiteInfo.carNum': [{
                            limit: "required",
                            param: "",
                            errInfo: "车牌号不能为空"
                        },
                        {
                            limit: "carnumber",
                            errInfo: "车牌号格式不正确"
                        },
                    ],
                    'editCarBlackWhiteInfo.startTime': [{
                            limit: "required",
                            param: "",
                            errInfo: "开始时间不能为空"
                        },
                        {
                            limit: "dateTime",
                            param: "",
                            errInfo: "不是有效的时间格式"
                        },
                    ],
                    'editCarBlackWhiteInfo.endTime': [{
                            limit: "required",
                            param: "",
                            errInfo: "结束时间不能为空"
                        },
                        {
                            limit: "dateTime",
                            param: "",
                            errInfo: "不是有效的时间格式"
                        },
                    ],
                    'editCarBlackWhiteInfo.bwId': [{
                        limit: "required",
                        param: "",
                        errInfo: "黑白名单ID不能为空"
                    }]
                });
            },
            editCarBlackWhite: function() {
                $that.editCarBlackWhiteInfo.communityId = vc.getCurrentCommunity().communityId;
                if (!$that.editCarBlackWhiteValidate()) {
                    vc.toast(vc.validate.errInfo);
                    return;
                }
                vc.http.apiPost('/carBlackWhite.updateCarBlackWhite', JSON.stringify($that.editCarBlackWhiteInfo), {
                        emulateJSON: true
                    },
                    function(json, res) {
                        let _json = JSON.parse(json);
                        if (_json.code == 0) {
                            //关闭model
                            $('#editCarBlackWhiteModel').modal('hide');
                            vc.emit('carBlackWhite', 'listCarBlackWhite', {});
                            vc.toast("修改成功");
                            return;
                        } else {
                            vc.toast(_json.msg);
                        }
                    },
                    function(errInfo, error) {
                        console.log('请求失败处理');
                        vc.toast(errInfo);
                    });
            },
            refreshEditCarBlackWhiteInfo: function() {
                $that.editCarBlackWhiteInfo = {
                    bwId: '',
                    blackWhite: '',
                    carNum: '',
                    startTime: '',
                    endTime: ''
                }
            }
        }
    });
})(window.vc, window.$that);