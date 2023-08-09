(function(vc) {
    var DEFAULT_PAGE = 1;
    var DEFAULT_ROWS = 10;
    vc.extends({
        data: {
            listOwnerInfo: {
                owners: [],
                total: 0,
                records: 1,
                moreCondition: false,
                _currentOwnerId: '',
                _eventName: '',
                conditions: {
                    ownerTypeCd: '',
                    ownerId: '',
                    name: '',
                    link: '',
                    idCard: '',
                    address: '',
                    floorId: '',
                    floorName: '',
                    unitId: '',
                    roomNum: '',
                    roomId: '',
                    roomName: ''
                },
                currentPage: DEFAULT_PAGE,
                listColumns: []
            }
        },
        _initMethod: function() {
            //加载 业主信息
            var _ownerId = vc.getParam('ownerId')
            if (vc.notNull(_ownerId)) {
                //vc.component.listOwnerInfo.conditions.ownerId = _ownerId;
            }
            $that._getColumns(function() {
                vc.component._listOwnerData($that.listOwnerInfo.currentPage, DEFAULT_ROWS);
            });
        },
        _initEvent: function() {
            vc.on('listOwner', 'listOwnerData', function() {
                vc.component._listOwnerData($that.listOwnerInfo.currentPage, DEFAULT_ROWS);
            });
            vc.on('pagination', 'page_event', function(_currentPage) {
                $that.listOwnerInfo.currentPage = _currentPage;
                vc.component._listOwnerData(_currentPage, DEFAULT_ROWS);
            });
            vc.on('listOwner', 'chooseRoom', function(_room) {
                if (vc.component.listOwnerInfo._eventName == 'PayPropertyFee') {
                    vc.jumpToPage("/#/pages/property/listRoomFee?" + vc.objToGetParam(_room));
                } else {
                    vc.jumpToPage("/#/pages/property/ownerRepairManage?ownerId=" + vc.component.listOwnerInfo._currentOwnerId + "&roomId=" + _room.roomId);
                }
            });
            vc.on('listOwner', 'chooseParkingSpace', function(_parkingSpace) {
                vc.jumpToPage("/#/pages/property/listParkingSpaceFee?" + vc.objToGetParam(_parkingSpace));
            });
            vc.on("listOwner", "notify", function(_param) {
                if (_param.hasOwnProperty("floorId")) {
                    vc.component.listOwnerInfo.conditions.floorId = _param.floorId;
                }
                if (_param.hasOwnProperty("unitId")) {
                    vc.component.listOwnerInfo.conditions.unitId = _param.unitId;
                }
                if (_param.hasOwnProperty("roomId")) {
                    vc.component.listOwnerInfo.conditions.roomId = _param.roomId;
                    vc.component._listOwnerData(DEFAULT_PAGE, DEFAULT_ROWS);
                }
            });
        },
        methods: {
            _listOwnerData: function(_page, _row) {
                vc.component.listOwnerInfo.conditions.page = _page;
                vc.component.listOwnerInfo.conditions.row = _row;
                vc.component.listOwnerInfo.conditions.communityId = vc.getCurrentCommunity().communityId;
                var param = {
                    params: vc.component.listOwnerInfo.conditions
                }
                param.params.name = param.params.name.trim();
                param.params.roomName = param.params.roomName.trim();
                param.params.link = param.params.link.trim();
                param.params.ownerId = param.params.ownerId.trim();
                param.params.idCard = param.params.idCard.trim();
                //发送get请求
                vc.http.apiGet('/owner.queryOwners',
                    param,
                    function(json, res) {
                        var listOwnerData = JSON.parse(json);
                        vc.component.listOwnerInfo.total = listOwnerData.total;
                        vc.component.listOwnerInfo.records = listOwnerData.records;
                        vc.component.listOwnerInfo.owners = listOwnerData.data;
                        $that.dealOwnerAttr(listOwnerData.owners);
                        vc.emit('pagination', 'init', {
                            total: vc.component.listOwnerInfo.records,
                            dataCount: vc.component.listOwnerInfo.total,
                            currentPage: _page
                        });
                    },
                    function(errInfo, error) {
                        console.log('请求失败处理');
                    }
                );
            },
            _openAddOwnerModal: function() { //打开添加框
                vc.emit('addOwner', 'openAddOwnerModal', -1);
                vc.component.listOwnerInfo.moreCondition = false;
            },
            _openDelOwnerModel: function(_owner) { // 打开删除对话框
                vc.emit('deleteOwner', 'openOwnerModel', _owner);
                vc.component.listOwnerInfo.moreCondition = false;
            },
            _openEditOwnerModel: function(_owner) {
                vc.emit('editOwner', 'openEditOwnerModal', _owner);
                vc.component.listOwnerInfo.moreCondition = false;
            },
            //查询
            _queryOwnerMethod: function() {
                vc.component._listOwnerData(DEFAULT_PAGE, DEFAULT_ROWS);
            },
            //重置
            _resetOwnerMethod: function() {
                vc.component.listOwnerInfo.conditions.name = "";
                vc.component.listOwnerInfo.conditions.roomName = "";
                vc.component.listOwnerInfo.conditions.link = "";
                vc.component.listOwnerInfo.conditions.ownerId = "";
                vc.component.listOwnerInfo.conditions.idCard = "";
                vc.component._listOwnerData(DEFAULT_PAGE, DEFAULT_ROWS);
            },
            _openAddOwnerRoom: function(_owner) {
                vc.jumpToPage("/#/pages/property/addOwnerRoomBinding?ownerId=" + _owner.ownerId);
            },
            _openHireParkingSpace: function(_owner) {
                vc.jumpToPage("/#/pages/property/hireParkingSpace?ownerId=" + _owner.ownerId);
            },
            _openSellParkingSpace: function(_owner) {
                vc.jumpToPage("/#/pages/property/sellParkingSpace?ownerId=" + _owner.ownerId);
            },
            _openOwnerDetailModel: function(_owner) {
                vc.jumpToPage("/#/pages/owner/ownerDetail?ownerId=" + _owner.ownerId + "&ownerName=" + _owner.name + "&needBack=true");
            },
            _openDeleteOwnerRoom: function(_owner) {
                vc.jumpToPage("/#/pages/property/deleteOwnerRoom?ownerId=" + _owner.ownerId);
            },
            _moreCondition: function() {
                if (vc.component.listOwnerInfo.moreCondition) {
                    vc.component.listOwnerInfo.moreCondition = false;
                } else {
                    vc.component.listOwnerInfo.moreCondition = true;
                }
            },
            dealOwnerAttr: function(owners) {
                if (!owners) {
                    return;
                }
                owners.forEach(item => {
                    $that._getColumnsValue(item);
                });
            },
            _getColumnsValue: function(_owner) {
                _owner.listValues = [];
                if (!_owner.hasOwnProperty('ownerAttrDtos') || _owner.ownerAttrDtos.length < 1) {
                    $that.listOwnerInfo.listColumns.forEach(_value => {
                        _owner.listValues.push('');
                    })
                    return;
                }
                let _ownerAttrDtos = _owner.ownerAttrDtos;
                $that.listOwnerInfo.listColumns.forEach(_value => {
                    let _tmpValue = '';
                    _ownerAttrDtos.forEach(_attrItem => {
                        if (_value == _attrItem.specName) {
                            _tmpValue = _attrItem.valueName;
                        }
                    })
                    _owner.listValues.push(_tmpValue);
                })
            },
            _getColumns: function(_call) {
                $that.listOwnerInfo.listColumns = [];
                vc.getAttrSpec('building_owner_attr', function(data) {
                    $that.listOwnerInfo.listColumns = [];
                    data.forEach(item => {
                        if (item.listShow == 'Y') {
                            $that.listOwnerInfo.listColumns.push(item.specName);
                        }
                    });
                    _call();
                });
            },
            _viewOwnerRooms: function(_owner) {
                vc.emit('ownerRooms', 'openOwnerRoomModel', _owner);
            },
            _viewOwnerMembers: function(_owner) {
                vc.emit('ownerMembers', 'openOwnerMemberModel', _owner);
            },

            _viewOwnerCars: function(_owner) {
                vc.emit('ownerCars', 'openOwnerCarModel', _owner);
            },
            _viewComplaints: function(_owner) {
                vc.emit('ownerComplaints', 'openOwnerComplaintModel', _owner);
            },
            _viewRepairs: function(_owner) {
                vc.emit('ownerRepairs', 'openOwnerRepairModel', _owner);
            },
            _viewOweFees: function(_owner) {
                vc.emit('ownerOweFees', 'openOwnerOweFeeModel', _owner);
            },
            _viewRoomContracts: function(_owner) {
                vc.emit('roomContracts', 'openRoomContractModel', _owner);
            },
            _viewOwnerFace: function(_url) {
                vc.emit('viewImage', 'showImage', {
                    url: _url
                });
            },
        }
    })
})(window.vc);