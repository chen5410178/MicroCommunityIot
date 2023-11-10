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
                //$that.listOwnerInfo.conditions.ownerId = _ownerId;
            }
            $that._getColumns(function() {
                $that._listOwnerData($that.listOwnerInfo.currentPage, DEFAULT_ROWS);
            });
        },
        _initEvent: function() {
            vc.on('listOwner', 'listOwnerData', function() {
                $that._listOwnerData($that.listOwnerInfo.currentPage, DEFAULT_ROWS);
            });
            vc.on('pagination', 'page_event', function(_currentPage) {
                $that.listOwnerInfo.currentPage = _currentPage;
                $that._listOwnerData(_currentPage, DEFAULT_ROWS);
            });
            vc.on('listOwner', 'chooseRoom', function(_room) {
                if ($that.listOwnerInfo._eventName == 'PayPropertyFee') {
                    vc.jumpToPage("/#/pages/property/listRoomFee?" + vc.objToGetParam(_room));
                } else {
                    vc.jumpToPage("/#/pages/property/ownerRepairManage?ownerId=" + $that.listOwnerInfo._currentOwnerId + "&roomId=" + _room.roomId);
                }
            });
            vc.on('listOwner', 'chooseParkingSpace', function(_parkingSpace) {
                vc.jumpToPage("/#/pages/property/listParkingSpaceFee?" + vc.objToGetParam(_parkingSpace));
            });
            vc.on("listOwner", "notify", function(_param) {
                if (_param.hasOwnProperty("floorId")) {
                    $that.listOwnerInfo.conditions.floorId = _param.floorId;
                }
                if (_param.hasOwnProperty("unitId")) {
                    $that.listOwnerInfo.conditions.unitId = _param.unitId;
                }
                if (_param.hasOwnProperty("roomId")) {
                    $that.listOwnerInfo.conditions.roomId = _param.roomId;
                    $that._listOwnerData(DEFAULT_PAGE, DEFAULT_ROWS);
                }
            });
        },
        methods: {
            _listOwnerData: function(_page, _row) {
                $that.listOwnerInfo.conditions.page = _page;
                $that.listOwnerInfo.conditions.row = _row;
                $that.listOwnerInfo.conditions.communityId = vc.getCurrentCommunity().communityId;
                var param = {
                    params: $that.listOwnerInfo.conditions
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
                        $that.listOwnerInfo.total = listOwnerData.total;
                        $that.listOwnerInfo.records = listOwnerData.records;
                        $that.listOwnerInfo.owners = listOwnerData.data;
                        $that.dealOwnerAttr(listOwnerData.owners);
                        vc.emit('pagination', 'init', {
                            total: $that.listOwnerInfo.records,
                            dataCount: $that.listOwnerInfo.total,
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
                $that.listOwnerInfo.moreCondition = false;
            },
            _openDelOwnerModel: function(_owner) { // 打开删除对话框
                vc.emit('deleteOwner', 'openOwnerModel', _owner);
                $that.listOwnerInfo.moreCondition = false;
            },
            _openEditOwnerModel: function(_owner) {
                vc.emit('editOwner', 'openEditOwnerModal', _owner);
                $that.listOwnerInfo.moreCondition = false;
            },
            //查询
            _queryOwnerMethod: function() {
                $that._listOwnerData(DEFAULT_PAGE, DEFAULT_ROWS);
            },
            //重置
            _resetOwnerMethod: function() {
                $that.listOwnerInfo.conditions.name = "";
                $that.listOwnerInfo.conditions.roomName = "";
                $that.listOwnerInfo.conditions.link = "";
                $that.listOwnerInfo.conditions.ownerId = "";
                $that.listOwnerInfo.conditions.idCard = "";
                $that._listOwnerData(DEFAULT_PAGE, DEFAULT_ROWS);
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
                vc.jumpToPage("/#/pages/owner/ownerDetail?memberId=" + _owner.memberId +"&ownerId="+_owner.ownerId+ "&ownerName=" + _owner.name + "&needBack=true");
            },
            _openDeleteOwnerRoom: function(_owner) {
                vc.jumpToPage("/#/pages/property/deleteOwnerRoom?ownerId=" + _owner.ownerId);
            },
            _moreCondition: function() {
                if ($that.listOwnerInfo.moreCondition) {
                    $that.listOwnerInfo.moreCondition = false;
                } else {
                    $that.listOwnerInfo.moreCondition = true;
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