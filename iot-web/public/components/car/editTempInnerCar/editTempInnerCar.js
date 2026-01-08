(function(vc, vm) {
    vc.extends({
        data: {
            editTempInnerCarInfo: {
                ticId: '',
                carNum: '',
                startTime: '',
                endTime: '',
                configId: '',
                feeName: '',
                remark: ''
            }
        },
        _initMethod: function() {
            $that._initEditTempInnerCarDateInfo();
        },
        _initEvent: function() {
            vc.on('editTempInnerCar', 'openEditTempInnerCarModal',
                function(_params) {
                    $that.refreshEditTempInnerCarInfo();
                    $('#editTempInnerCarModel').modal('show');
                    vc.copyObject(_params, $that.editTempInnerCarInfo);
                    $that.editTempInnerCarInfo.communityId = vc.getCurrentCommunity().communityId;
                });
        },
        methods: {
            _initEditTempInnerCarDateInfo: function() {
                $that.editTempInnerCarInfo.startTime = vc.dateTimeFormat(new Date().getTime());
                $('.editTempInnerCarStartTime').datetimepicker({
                    language: 'zh-CN',
                    fontAwesome: 'fa',
                    format: 'yyyy-mm-dd hh:ii:ss',
                    initTime: true,
                    initialDate: new Date(),
                    autoClose: 1,
                    todayBtn: true
                });
                $('.editTempInnerCarStartTime').datetimepicker()
                    .on('changeDate', function(ev) {
                        var value = $(".editTempInnerCarStartTime").val();
                        $that.editTempInnerCarInfo.startTime = value;
                    });
                $('.editTempInnerCarEndTime').datetimepicker({
                    language: 'zh-CN',
                    fontAwesome: 'fa',
                    format: 'yyyy-mm-dd hh:ii:ss',
                    initTime: true,
                    initialDate: new Date(),
                    autoClose: 1,
                    todayBtn: true
                });
                $('.editTempInnerCarEndTime').datetimepicker()
                    .on('changeDate', function(ev) {
                        var value = $(".editTempInnerCarEndTime").val();
                        var start = Date.parse(new Date($that.editTempInnerCarInfo.startTime))
                        var end = Date.parse(new Date(value))
                        if (start - end >= 0) {
                            vc.toast("结束时间必须大于开始时间")
                            $(".editTempInnerCarEndTime").val('')
                        } else {
                            $that.editTempInnerCarInfo.endTime = value;
                        }
                    });
                //防止多次点击时间插件失去焦点
                document.getElementsByClassName('form-control editTempInnerCarStartTime')[0].addEventListener('click', myfunc)

                function myfunc(e) {
                    e.currentTarget.blur();
                }

                document.getElementsByClassName("form-control editTempInnerCarEndTime")[0].addEventListener('click', myfunc)

                function myfunc(e) {
                    e.currentTarget.blur();
                }
            },
            editTempInnerCarValidate: function() {
                return vc.validate.validate({
                    editTempInnerCarInfo: $that.editTempInnerCarInfo
                }, {
                    'editTempInnerCarInfo.carNum': [{
                            limit: "required",
                            param: "",
                            errInfo: "车牌号不能为空"
                        },
                        {
                            limit: "carnumber",
                            errInfo: "车牌号格式不正确"
                        },
                    ],
                    'editTempInnerCarInfo.startTime': [{
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
                    'editTempInnerCarInfo.endTime': [{
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
                    'editTempInnerCarInfo.ticId': [{
                        limit: "required",
                        param: "",
                        errInfo: "临时内部车ID不能为空"
                    }]
                });
            },
            editTempInnerCar: function() {
                $that.editTempInnerCarInfo.communityId = vc.getCurrentCommunity().communityId;
                if (!$that.editTempInnerCarValidate()) {
                    vc.toast(vc.validate.errInfo);
                    return;
                }
                vc.http.apiPost('/car.updateTempInnerCar', JSON.stringify($that.editTempInnerCarInfo), {
                        emulateJSON: true
                    },
                    function(json, res) {
                        let _json = JSON.parse(json);
                        if (_json.code == 0) {
                            //关闭model
                            $('#editTempInnerCarModel').modal('hide');
                            vc.emit('tempInnerCar', 'listTempInnerCar', {});
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
            refreshEditTempInnerCarInfo: function() {
                $that.editTempInnerCarInfo = {
                    ticId: '',
                    carNum: '',
                    startTime: '',
                    endTime: '',
                    configId: '',
                    feeName: '',
                    remark: ''
                }
            }
        }
    });
})(window.vc, window.$that);

