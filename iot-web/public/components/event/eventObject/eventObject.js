/**
 入驻小区
 **/
(function (vc) {
    var DEFAULT_PAGE = 1;
    var DEFAULT_ROWS = 10;
    vc.extends({
        data: {
            eventObjectInfo: {
                eventObjects: [],
                total: 0,
                records: 1,
                conditions: {
                    ruleId: '',
                    communityId: vc.getCurrentCommunity().communityId,
                    objType: '',
                    objectName: '',
                    state: ''
                },
                objTypeList: [],
            },
        },
        _initMethod: function () {
            vc.getDict('event_object', 'obj_type', function (_data) {
                $that.eventObjectInfo.objTypeList = _data;
            })
        },
        _initEvent: function () {
            vc.on('eventObject', 'switch', function (_data) {
                $that.eventObjectInfo.conditions.ruleId = _data.ruleId;
                $that._listEventObjects(DEFAULT_PAGE, DEFAULT_ROWS);
            });
            vc.on('eventObjectManage', 'listEventObject', function (_param) {
                $that._listEventObjects(DEFAULT_PAGE, DEFAULT_ROWS);
            });
            vc.on('eventObject', 'paginationPlus', 'page_event', function (_currentPage) {
                $that._listEventObjects(_currentPage, DEFAULT_ROWS);
            });
        },
        methods: {
            _listEventObjects: function (_page, _rows) {

                $that.eventObjectInfo.conditions.page = _page;
                $that.eventObjectInfo.conditions.row = _rows;
                var param = {
                    params: $that.eventObjectInfo.conditions
                };

                //发送get请求
                vc.http.apiGet('/eventObject.listEventObject',
                    param,
                    function (json, res) {
                        var _eventObjectInfo = JSON.parse(json);
                        $that.eventObjectInfo.total = _eventObjectInfo.total;
                        $that.eventObjectInfo.records = _eventObjectInfo.records;
                        $that.eventObjectInfo.eventObjects = _eventObjectInfo.data;
                        vc.emit('eventObject', 'paginationPlus', 'init', {
                            total: $that.eventObjectInfo.records,
                            currentPage: _page
                        });
                    }, function (errInfo, error) {
                        console.log('请求失败处理');
                    }
                );
            },
            _openAddEventObjectModal: function () {
                vc.jumpToPage('/#/pages/event/addEventObject?ruleId=' + $that.eventObjectInfo.conditions.ruleId);
            },
            _openDeleteEventObjectModel: function (_eventObject) {
                vc.emit('deleteEventObject', 'openDeleteEventObjectModal', _eventObject);
            },
            _queryEventObjectMethod: function () {
                $that._listEventObjects(DEFAULT_PAGE, DEFAULT_ROWS);
            },
            _resetEventObjectMethod: function () {
                $that.eventObjectInfo.conditions.objType = '';
                $that.eventObjectInfo.conditions.objectName = '';
                $that.eventObjectInfo.conditions.state = '';
                $that._listEventObjects(DEFAULT_PAGE, DEFAULT_ROWS);
            },
            _changeEventObjectState: function (_eventObject) {
                if (_eventObject.state === 'ON') {
                    _eventObject.state = 'OFF';
                } else if (_eventObject.state === 'OFF') {
                    _eventObject.state = 'ON';
                }
                vc.http.apiPost(
                    '/eventObject.updateEventObject',
                    JSON.stringify(_eventObject),
                    {
                        emulateJSON:true
                    },
                    function(json,res){
                        let _json = JSON.parse(json);
                        if (_json.code === 0) {
                            vc.emit('eventObjectManage','listEventObject',{});
                            return ;
                        }
                        vc.toast(_json.msg);
                    },
                    function(errInfo,error){
                        console.log('请求失败处理');
                        vc.toast(errInfo);
                    });
            },
        }
    });
})(window.vc);