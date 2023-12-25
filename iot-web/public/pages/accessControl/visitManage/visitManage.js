/**
 入驻小区
 **/
(function (vc) {
    var DEFAULT_PAGE = 1;
    var DEFAULT_ROWS = 10;
    vc.extends({
        data: {
            visitManageInfo: {
                visits: [],
                total: 0,
                records: 1,
                moreCondition: false,
                visitId: '',
                conditions: {
                    visitId: '',
                    nameLike: '',
                    visitGender: '',
                    phoneNumberLike: '',
                    roomNameLike: '',
                    ownerNameLike: '',
                    communityId: '',
                    typeId: '',
                    state: '',
                    queryStartTime:'',
                    queryEndTime:''
                }
            },
            visitTypeList: [],
            stateList: [],
        },
        _initMethod: function () {
            $that.listVisitTypes();
            vc.getDict('visit', 'state', function (_data) {
                $that.stateList = _data;
            })
            $that._listVisits(DEFAULT_PAGE, DEFAULT_ROWS);
            vc.initDateTime('queryStartTime',function(_value){
                $that.visitManageInfo.conditions.queryStartTime = _value;
            });
            vc.initDateTime('queryEndTime',function(_value){
                $that.visitManageInfo.conditions.queryEndTime = _value;
            });
        },
        _initEvent: function () {

            vc.on('visitManage', 'listVisit', function (_param) {
                $that._listVisits(DEFAULT_PAGE, DEFAULT_ROWS);
            });
            vc.on('pagination', 'page_event', function (_currentPage) {
                $that._listVisits(_currentPage, DEFAULT_ROWS);
            });
        },
        methods: {
            _listVisits: function (_page, _rows) {

                $that.visitManageInfo.conditions.page = _page;
                $that.visitManageInfo.conditions.row = _rows;
                $that.visitManageInfo.conditions.communityId = vc.getCurrentCommunity().communityId;
                var param = {
                    params: $that.visitManageInfo.conditions
                };

                //发送get请求
                vc.http.apiGet('/visit.listVisit',
                    param,
                    function (json, res) {
                        var _visitManageInfo = JSON.parse(json);
                        $that.visitManageInfo.total = _visitManageInfo.total;
                        $that.visitManageInfo.records = _visitManageInfo.records;
                        $that.visitManageInfo.visits = _visitManageInfo.data;
                        vc.emit('pagination', 'init', {
                            total: $that.visitManageInfo.records,
                            currentPage: _page,
                            dataCount:_visitManageInfo.total
                        });
                    }, function (errInfo, error) {
                        console.log('请求失败处理');
                    }
                );
            },
            _openAddVisitModal: function () {
                vc.jumpToPage('/#/pages/accessControl/addVisit');
            },
            _openEditVisitModel: function (_visit) {
                vc.jumpToPage('/#/pages/accessControl/editVisit?visitId=' + _visit.visitId);
            },
            _openDeleteVisitModel: function (_visit) {
                vc.emit('deleteVisit', 'openDeleteVisitModal', _visit);
            },
            _queryVisitMethod: function () {
                $that._listVisits(DEFAULT_PAGE, DEFAULT_ROWS);
            },
            _resetVisitMethod: function () {
                $that.visitManageInfo.conditions = {
                    visitId: '',
                    nameLike: '',
                    visitGender: '',
                    phoneNumberLike: '',
                    roomNameLike: '',
                    ownerNameLike: '',
                    communityId: '',
                    typeId: '',
                    state: '',
                    queryStartTime:'',
                    queryEndTime:''
                }
                $that._listVisits(DEFAULT_PAGE, DEFAULT_ROWS);
            },
            _moreCondition: function () {
                if ($that.visitManageInfo.moreCondition) {
                    $that.visitManageInfo.moreCondition = false;
                } else {
                    $that.visitManageInfo.moreCondition = true;
                }
            },
            _doVisitAuth: function (_visit) {
                vc.emit('doVisitAuth', 'doVisitAuthModal', _visit);
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
                        $that.visitTypeList = [{
                            name: '访客类型',
                            typeId: ''
                        }];
                        _visitTypeInfo.data.forEach(item => {
                            $that.visitTypeList.push(item);
                        });
                    }, function (errInfo, error) {
                        console.log('请求失败处理');
                    }
                )
            },
            switchVisitType: function(_visitTypt) {
                $that.visitManageInfo.conditions.typeId = _visitTypt.typeId;
                $that._listVisits(DEFAULT_PAGE, DEFAULT_ROWS);
            },
            _toVisitDetail: function (_visit) {
                vc.jumpToPage('/#/pages/accessControl/visitDetail?visitId=' + _visit.visitId + "&phoneNumber=" + _visit.phoneNumber);
            },
            _showVisitQrCode: function () {
                vc.emit('visitQrCode', 'openQrCodeModal', {});
            }
        }
    });
})(window.vc);
