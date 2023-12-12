/**
 入驻小区
 **/
(function (vc) {
    var DEFAULT_PAGE = 1;
    var DEFAULT_ROWS = 10;
    vc.extends({
        data: {
            visitTypeManageInfo: {
                visitTypes: [],
                total: 0,
                records: 1,
                moreCondition: false,
                typeId: '',
                conditions: {
                    typeId: '',
                    nameLike: '',
                    visitWay: '',
                    auditWay: '',
                    communityId: '',
                }
            },
            visitWayList: [],
            auditWayList: [],
        },
        _initMethod: function () {
            vc.getDict('visit_type', 'visit_way', function (_data) {
                $that.visitWayList = _data;
            });
            vc.getDict('visit_type', 'audit_way', function (_data) {
                $that.auditWayList = _data;
            });
            $that._listVisitTypes(DEFAULT_PAGE, DEFAULT_ROWS);
        },
        _initEvent: function () {
            vc.on('visitTypeManage', 'listVisitType', function (_param) {
                $that._listVisitTypes(DEFAULT_PAGE, DEFAULT_ROWS);
            });
            vc.on('pagination', 'page_event', function (_currentPage) {
                $that._listVisitTypes(_currentPage, DEFAULT_ROWS);
            });
        },
        methods: {
            _listVisitTypes: function (_page, _rows) {

                $that.visitTypeManageInfo.conditions.page = _page;
                $that.visitTypeManageInfo.conditions.row = _rows;
                $that.visitTypeManageInfo.conditions.communityId = vc.getCurrentCommunity().communityId;
                var param = {
                    params: $that.visitTypeManageInfo.conditions
                };

                //发送get请求
                vc.http.apiGet('/visitType.listVisitType',
                    param,
                    function (json, res) {
                        var _visitTypeManageInfo = JSON.parse(json);
                        $that.visitTypeManageInfo.total = _visitTypeManageInfo.total;
                        $that.visitTypeManageInfo.records = _visitTypeManageInfo.records;
                        $that.visitTypeManageInfo.visitTypes = _visitTypeManageInfo.data;
                        vc.emit('pagination', 'init', {
                            total: $that.visitTypeManageInfo.records,
                            currentPage: _page
                        });
                    }, function (errInfo, error) {
                        console.log('请求失败处理');
                    }
                );
            },
            _openAddVisitTypeModal: function () {
                vc.emit('addVisitType', 'openAddVisitTypeModal', {});
            },
            _openEditVisitTypeModel: function (_visitType) {
                vc.emit('editVisitType', 'openEditVisitTypeModal', _visitType);
            },
            _openDeleteVisitTypeModel: function (_visitType) {
                vc.emit('deleteVisitType', 'openDeleteVisitTypeModal', _visitType);
            },
            _queryVisitTypeMethod: function () {
                $that._listVisitTypes(DEFAULT_PAGE, DEFAULT_ROWS);
            },
            _resetVisitTypeMethod: function () {
                $that.visitTypeManageInfo.conditions = {
                    typeId: '',
                    name: '',
                    visitWay: '',
                    auditWay: '',
                    communityId: '',
                }
                $that._listVisitTypes(DEFAULT_PAGE, DEFAULT_ROWS);
            },
        }
    });
})(window.vc);
