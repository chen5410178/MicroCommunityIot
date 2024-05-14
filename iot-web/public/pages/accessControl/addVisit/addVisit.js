(function (vc) {

    vc.extends({
        data: {
            addVisitInfo: {
                visitId: '',
                name: '',
                visitGender: '',
                phoneNumber: '',
                communityId: '',
                carNum:'',
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
            vc.initDateTime('addVisitTime', function (_data) {
                $that.addVisitInfo.visitTime = _data;
            });
        },
        _initEvent: function () {
            vc.on('addVisit', 'selectRoom', function(param) {
                $that.addVisitInfo.roomId = param.roomId;
                $that.addVisitInfo.roomName = param.roomName;
            });
            vc.on('addVisit', 'chooseOwner', function(_owner) {
                $that.addVisitInfo.ownerName = _owner.name;
                $that.addVisitInfo.ownerId = _owner.ownerId;
            });
        },
        methods: {
            addVisitValidate() {
                return vc.validate.validate({
                    addVisitInfo: $that.addVisitInfo
                }, {
                    'addVisitInfo.name': [
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
                    'addVisitInfo.visitGender': [
                        {
                            limit: "required",
                            param: "",
                            errInfo: "性别不能为空"
                        },
                    ],
                    'addVisitInfo.phoneNumber': [
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
                    'addVisitInfo.roomName': [
                        {
                            limit: "required",
                            param: "",
                            errInfo: "拜访房屋不能为空"
                        },
                    ],
                    'addVisitInfo.roomId': [
                        {
                            limit: "required",
                            param: "",
                            errInfo: "拜访房屋不能为空"
                        },
                    ],
                    'addVisitInfo.typeId': [
                        {
                            limit: "required",
                            param: "",
                            errInfo: "访客类型不能为空"
                        },
                    ],
                    'addVisitInfo.visitCase': [
                        {
                            limit: "maxLength",
                            param: "255",
                            errInfo: "拜访事由不能超过255"
                        },
                    ],
                    'addVisitInfo.state': [
                        {
                            limit: "required",
                            param: "",
                            errInfo: "审核状态不能为空"
                        },
                    ],
                    'addVisitInfo.communityId': [
                        {
                            limit: "required",
                            param: "",
                            errInfo: "小区不能为空"
                        },
                    ],
                });
            },
            saveVisitInfo: function () {
                $that.addVisitInfo.communityId = vc.getCurrentCommunity().communityId;
                if (!$that.addVisitValidate()) {
                    vc.toast(vc.validate.errInfo);
                    return;
                }

                vc.http.apiPost(
                    '/visit.saveVisit',
                    JSON.stringify($that.addVisitInfo),
                    {
                        emulateJSON: true
                    },
                    function (json, res) {
                        let _json = JSON.parse(json);
                        if (_json.code === 0) {
                            $that.clearAddVisitInfo();
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
            clearAddVisitInfo: function () {
                $that.addVisitInfo = {
                    visitId: '',
                    name: '',
                    visitGender: '',
                    phoneNumber: '',
                    communityId: '',
                    carNum:'',
                    roomName: '',
                    roomId: '',
                    typeId: '',
                    visitCase: '',
                    facePath: '',
                    qrcode: '',
                    state: '0',
                    msg: '',
                };
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
                        let _json = JSON.parse(json);
                        $that.visitTypeList = _json.data;
                    }, function (errInfo, error) {
                        console.log('请求失败处理');
                    }
                )
            },
            _selectRoom: function() {
                vc.emit('roomTree', 'openRoomTree', {
                    callName: 'addVisit'
                })
            },
            _openChooseOwner: function () {
                vc.emit('searchOwner', 'openSearchOwnerModel', {});
            },
        }
    });
})(window.vc);
