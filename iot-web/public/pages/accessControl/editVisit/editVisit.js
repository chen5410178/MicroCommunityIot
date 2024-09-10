(function (vc, vm) {

    vc.extends({
        data: {
            editVisitInfo: {
                visitId: '',
                name: '',
                visitGender: '',
                phoneNumber: '',
                communityId: '',
                carNum: '',
                roomName: '',
                roomId: '',
                visitTime: '',
                typeId: '',
                visitCase: '',
                facePath: '',
                qrcode: '',
                state: '0',
                msg: '',
            },
            visitTypeList: []
        },
        _initMethod: function () {
            $that.listVisitTypes();
            vc.initDateTime('editVisitTime', function (_data) {
                $that.editVisitInfo.visitTime = _data;
            });
            $that.listVisits();
        },
        _initEvent: function () {
            vc.on('editVisit', 'selectRoom', function(param) {
                $that.editVisitInfo.roomId = param.roomId;
                $that.editVisitInfo.roomName = param.roomName;
            });
            vc.on('editVisit', 'chooseOwner', function(_owner) {
                $that.editVisitInfo.ownerName = _owner.name;
                $that.editVisitInfo.ownerId = _owner.ownerId;
            });
        },
        methods: {
            editVisitValidate: function () {
                return vc.validate.validate({
                    editVisitInfo: $that.editVisitInfo
                }, {
                    'editVisitInfo.visitId': [
                        {
                            limit: "required",
                            param: "",
                            errInfo: "拜访ID不能为空"
                        },
                    ],
                    'editVisitInfo.name': [
                        {
                            limit: "required",
                            param: "",
                            errInfo: "姓名不能为空"
                        },
                        {
                            limit: "maxLength",
                            param: "255",
                            errInfo: "姓名不能超过255"
                        },
                    ],
                    'editVisitInfo.visitGender': [
                        {
                            limit: "required",
                            param: "",
                            errInfo: "性别不能为空"
                        },
                    ],
                    'editVisitInfo.phoneNumber': [
                        {
                            limit: "required",
                            param: "",
                            errInfo: "电话号码不能为空"
                        },
                        {
                            limit: "phone",
                            param: "",
                            errInfo: "电话号码格式错误"
                        },
                    ],
                    'editVisitInfo.roomName': [
                        {
                            limit: "required",
                            param: "",
                            errInfo: "拜访房屋不能为空"
                        },
                    ],
                    'editVisitInfo.roomId': [
                        {
                            limit: "required",
                            param: "",
                            errInfo: "拜访房屋不能为空"
                        },
                    ],
                    'editVisitInfo.typeId': [
                        {
                            limit: "required",
                            param: "",
                            errInfo: "访客类型不能为空"
                        },
                    ],
                    'editVisitInfo.visitCase': [
                        {
                            limit: "maxLength",
                            param: "255",
                            errInfo: "拜访事由不能超过255"
                        },
                    ],
                    'editVisitInfo.state': [
                        {
                            limit: "required",
                            param: "",
                            errInfo: "审核状态不能为空"
                        },
                    ],
                });
            },
            editVisit: function () {
                if (!$that.editVisitValidate()) {
                    vc.toast(vc.validate.errInfo);
                    return;
                }

                vc.http.apiPost(
                    '/visit.updateVisit',
                    JSON.stringify($that.editVisitInfo),
                    {
                        emulateJSON: true
                    },
                    function (json, res) {
                        let _json = JSON.parse(json);
                        if (_json.code === 0) {
                            vc.goBack();
                            return;
                        }
                        vc.toast(_json.msg);
                    },
                    function (errInfo, error) {
                        console.log('请求失败处理');
                        vc.toast(errInfo);
                    });
            },
            listVisitTypes: function () {
                let param = {
                    params: {
                        page: -1,
                        row: 100,
                        communityId: vc.getCurrentCommunity().communityId
                    }
                }
                vc.http.apiGet('/visitType.listVisitType',
                    param,
                    function (json, res) {
                        var _visitTypeInfo = JSON.parse(json);
                        $that.visitTypeList = _visitTypeInfo.data;
                    }, function (errInfo, error) {
                        console.log('请求失败处理');
                    }
                )
            },
            _selectRoom: function() {
                vc.emit('roomTree', 'openRoomTree', {
                    callName: 'editVisit'
                })
            },
            _openChooseOwner: function () {
                vc.emit('searchOwner', 'openSearchOwnerModel', {
                    callBack:function(_owner){
                        $that.editVisitInfo.ownerId = _owner.memberId;
                        $that.editVisitInfo.ownerName = _owner.name;
                    }
                });
            },
            listVisits: function () {
                let param = {
                    params: {
                        page: 1,
                        row: 1,
                        visitId: vc.getParam("visitId"),
                        communityId: vc.getCurrentCommunity().communityId

                    }
                }
                vc.http.apiGet('/visit.listVisit',
                    param,
                    function (json, res) {
                        var _visitInfo = JSON.parse(json);
                        vc.copyObject(_visitInfo.data[0], $that.editVisitInfo);
                    }, function (errInfo, error) {
                        console.log('请求失败处理');
                    }
                )
            }
        }
    });
})(window.vc, window.$that);
