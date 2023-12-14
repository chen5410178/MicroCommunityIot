/**
    入驻小区
**/
import { loadData } from '/api/3d/CockpitCore.js';
(function (vc) {

    vc.extends({
        data: {
            cockpitRightRoomInfo: {
                roomId: '',
                roomName: '',
                layer: "",
                roomSubTypeName: "",
                builtUpArea: "",
                roomArea: "",
                stateName: "",
                startTime: "",
                roomAttrDtos: [],
                url: '',
                ownerName: '',
                memberCount: 0,
                remark: '',
                ownerId: "",
                name: '',
                link: "",
                sex: "",
                idCard: "",
                address: "",
                ownerTypeName: "",
                cars: [],
                floorId: '',
                allLayer: 1,
                curLayer: 1
            }
        },
        _initMethod: function () {

        },
        _initEvent: function () {
            vc.on('cockpitRightRoom', 'notify', function (_room) {
                $that.clearCockpitRightRoom();
                $that.cockpitRightRoomInfo.roomId = _room.roomId;
                $that.cockpitRightRoomInfo.floorId = _room.floorId;
                $that.cockpitRightRoomInfo.curLayer = _room.layer
                $that._loadRoom();
                $that._loadRoomFloor();
            })
        },
        methods: {

            // _changeLayer: function (_layer) {
            //     $that.cockpitRightRoomInfo.curLayer = _layer;
            //     vc.emit('cockpitCore', 'loadRoomModal', { floorId: $that.cockpitRightRoomInfo.floorId, layer: _layer });
            // },
            _loadRoom: function () {
                loadData({
                    page: 1,
                    row: 1,
                    communityId: vc.getCurrentCommunity().communityId,
                    roomId: $that.cockpitRightRoomInfo.roomId
                }, '/room.queryRooms').then((data) => {
                    vc.copyObject(data[0], $that.cockpitRightRoomInfo);
                    if (data[0].ownerId) {
                        $that._loadOwner();
                        $that._loadCars();
                    }
                });
            },
            _loadOwner: function () {
                loadData({
                    page: 1,
                    row: 1,
                    communityId: vc.getCurrentCommunity().communityId,
                    ownerId: $that.cockpitRightRoomInfo.ownerId
                }, '/owner.queryOwners').then((data) => {
                    vc.copyObject(data[0], $that.cockpitRightRoomInfo)
                });
            },
            _loadCars: function () {
                loadData({
                    page: 1,
                    row: 10,
                    communityId: vc.getCurrentCommunity().communityId,
                    ownerId: $that.cockpitRightRoomInfo.ownerId
                }, '/ownerCar.queryOwnerCars').then((data) => {
                    $that.cockpitRightRoomInfo.cars = data;
                });
            },
            _loadRoomFloor:function(){
                loadData({
                    page:1,
                    row:1,
                    communityId:vc.getCurrentCommunity().communityId,
                    floorId:$that.cockpitRightRoomInfo.floorId
                },'floor.queryFloors').then((_floors)=>{
                    $that.cockpitRightRoomInfo.allLayer = _floors[0].layer;
                })
            },
            clearCockpitRightRoom: function () {
                $that.cockpitRightRoomInfo = {
                    roomId: '',
                    roomName: '',
                    layer: "",
                    roomSubTypeName: "",
                    builtUpArea: "",
                    roomArea: "",
                    stateName: "",
                    startTime: "",
                    roomAttrDtos: [],
                    url: '',
                    ownerName: '',
                    memberCount: 0,
                    remark: '',
                    ownerId: "",
                    name: '',
                    link: "",
                    sex: "",
                    idCard: "",
                    address: "",
                    ownerTypeName: "",
                    cars: [],
                    floorId: '',
                    allLayer: 1,
                    curLayer: 1
                }
            },
            _changeRoomLayer: function (_curLayer) {
                $that.cockpitRightRoomInfo.curLayer = _curLayer;
                vc.emit('cockpitCore', 'loadRoomModal', { floorId: $that.cockpitRightRoomInfo.floorId, layer: _curLayer });
            }
        }
    });
})(window.vc);