(function (vc, vm) {

    vc.extends({
        data: {
            reservationLiftMachineInfo: {
                lmrId: '',
                machineId: '',
                resWay: '',
                roomId: '',
                roomName: '',
                personId: '',
                personName: '',
                personWay: '',
                orgLayer: '',
                targetLayer: '',
            },
            resWayList: [],
            personWayList: [],
        },
        _initMethod: function () {
            vc.getDict('lift_machine_reservation', 'res_way', function (_data) {
                $that.resWayList = _data;
            });
            vc.getDict('lift_machine_reservation', 'person_way', function (_data) {
                $that.personWayList = _data;
            });
        },
        _initEvent: function () {
            vc.on('reservationLiftMachine', 'openReservationLiftMachineModal', function (_params) {
                vc.component.refreshReservationLiftMachineInfo();
                $('#reservationLiftMachineModel').modal('show');
                $that.reservationLiftMachineInfo.machineId = _params.machineId;
                $that.reservationLiftMachineInfo.orgLayer = _params.curLayer;
            });
            vc.on('reservationLiftMachine', 'selectRoom', function(param) {
                $that.reservationLiftMachineInfo.roomId = param.roomId;
                $that.reservationLiftMachineInfo.roomName = param.roomName;
            })
        },
        methods: {
            reservationLiftMachineValidate: function () {
                return vc.validate.validate({
                    reservationLiftMachineInfo: vc.component.reservationLiftMachineInfo
                }, {
                    'reservationLiftMachineInfo.machineId': [
                        {
                            limit: "required",
                            param: "",
                            errInfo: "设备编号不能为空"
                        }
                    ],
                    'reservationLiftMachineInfo.resWay': [
                        {
                            limit: "required",
                            param: "",
                            errInfo: "预约方式不能为空"
                        }
                    ],
                    'reservationLiftMachineInfo.personWay': [
                        {
                            limit: "required",
                            param: "",
                            errInfo: "人员类型不能为空"
                        }
                    ],
                    'reservationLiftMachineInfo.orgLayer': [
                        {
                            limit: "required",
                            param: "",
                            errInfo: "原始楼层不能为空"
                        }
                    ],
                });
            },
            reservationLiftMachine: function () {
                if (!vc.component.reservationLiftMachineValidate()) {
                    vc.toast(vc.validate.errInfo);
                    return;
                }

                let curUserInfo = JSON.parse(window.localStorage.getItem('/nav/getUserInfo'));
                vc.component.reservationLiftMachineInfo.personId = curUserInfo.userId;
                vc.component.reservationLiftMachineInfo.personName = curUserInfo.name;

                vc.http.apiPost(
                    'liftMachine.reservationLiftMachine',
                    JSON.stringify(vc.component.reservationLiftMachineInfo),
                    {
                        emulateJSON: true
                    },
                    function (json, res) {
                        let _json = JSON.parse(json);
                        vc.toast(_json.msg);
                    },
                    function (errInfo, error) {
                        console.log('请求失败处理');
                        vc.toast(errInfo);
                    });
            },
            refreshReservationLiftMachineInfo: function () {
                vc.component.reservationLiftMachineInfo = {
                    lmrId: '',
                    machineId: '',
                    resWay: '',
                    roomId: '',
                    roomName: '',
                    personId: '',
                    personName: '',
                    personWay: '',
                    orgLayer: '',
                    targetLayer: '',
                }
            },
            _selectRoom: function() {
                vc.emit('roomTree', 'openRoomTree', {
                    callName: 'reservationLiftMachine'
                })
            },
        }
    });

})(window.vc, window.vc.component);
