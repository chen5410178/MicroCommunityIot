/**
 入驻小区
 **/
(function (vc) {
    var DEFAULT_PAGE = 1;
    var DEFAULT_ROWS = 10;
    vc.extends({
        data: {
            callingContactManageInfo: {
                accessControls: [],
                callingContacts: [],
                total: 0,
                records: 1,
                moreCondition: false,
                contactId: '',
                conditions: {
                    deviceId: '',
                    machineId: '',
                    accessControlId: '',
                    deviceName: '',
                    callingNum: '',
                }
            }
        },
        _initMethod: function () {
            vc.component._listCallingContacts(DEFAULT_PAGE, DEFAULT_ROWS);
            vc.component._listAccessControls();
        },
        _initEvent: function () {
            vc.on('callingContactManage', 'chooseCallingDevice', function (_param) {
                if ($that.callingContactManageInfo.conditions.machineId){
                    vc.http.apiPost(
                        'callingContact.saveCallingContact',
                        JSON.stringify({deviceId:_param.deviceId,communityId:_param.communityId,platformId:_param.platformId,machineId:$that.callingContactManageInfo.conditions.machineId}),
                        {
                            emulateJSON: true
                        },
                        function (json, res) {
                            let _json = JSON.parse(json);
                            if (_json.code == 0) {
                                vc.component._listCallingContacts(DEFAULT_PAGE, DEFAULT_ROWS);
                                return;
                            }
                            vc.message(_json.msg);
                        },
                        function (errInfo, error) {
                            console.log('请求失败处理');
                            vc.message(errInfo);

                        });
                }
            });
            vc.on('callingContactManage', 'listCallingContact', function (_param) {
                vc.component._listCallingContacts(DEFAULT_PAGE, DEFAULT_ROWS);
            });
            vc.on('pagination', 'page_event', function (_currentPage) {
                vc.component._listCallingContacts(_currentPage, DEFAULT_ROWS);
            });
        },
        methods: {
            _listCallingContacts: function (_page, _rows) {

                vc.component.callingContactManageInfo.conditions.page = _page;
                vc.component.callingContactManageInfo.conditions.row = _rows;
                var param = {
                    params: vc.component.callingContactManageInfo.conditions
                };

                //发送get请求
                vc.http.apiGet('callingContact.listCallingContact',
                    param,
                    function (json, res) {
                        var _callingContactManageInfo = JSON.parse(json);
                        vc.component.callingContactManageInfo.total = _callingContactManageInfo.total;
                        vc.component.callingContactManageInfo.records = _callingContactManageInfo.records;
                        vc.component.callingContactManageInfo.callingContacts = _callingContactManageInfo.data;
                        vc.emit('pagination', 'init', {
                            total: vc.component.callingContactManageInfo.records,
                            currentPage: _page
                        });
                    }, function (errInfo, error) {
                        console.log('请求失败处理');
                    }
                );
            },
            _listAccessControls: function () {
                let param = {
                    params: {page:1,row:50,communityId:vc.getCurrentCommunity().communityId}
                };
                //发送get请求
                vc.http.apiGet('/accessControl.listAccessControl',
                    param,
                    function (json, res) {
                        let _json = JSON.parse(json);
                        vc.component.callingContactManageInfo.accessControls = _json.data;
                    },
                    function (errInfo, error) {
                        console.log('请求失败处理');
                    }
                );
            },
            swatchAccessControl:function(_item){
                $that.callingContactManageInfo.conditions.machineId= _item.machineId;
                $that._listCallingContacts(DEFAULT_PAGE, DEFAULT_ROWS);
            },
            _openChooseCallingContactModal: function () {
                vc.emit('chooseCallingDevice', 'openChooseCallingDeviceModel', {});
            },
            _openEditCallingContactModel: function (_callingContact) {
                vc.emit('editCallingContact', 'openEditCallingContactModal', _callingContact);
            },
            _openDeleteCallingContactModel: function (_callingContact) {
                vc.emit('deleteCallingContact', 'openDeleteCallingContactModal', _callingContact);
            },
            _queryCallingContactMethod: function () {
                vc.component._listCallingContacts(DEFAULT_PAGE, DEFAULT_ROWS);

            },
            _moreCondition: function () {
                if (vc.component.callingContactManageInfo.moreCondition) {
                    vc.component.callingContactManageInfo.moreCondition = false;
                } else {
                    vc.component.callingContactManageInfo.moreCondition = true;
                }
            }


        }
    });
})(window.vc);
