/**
 入驻小区
 **/
(function (vc) {
    var DEFAULT_PAGE = 1;
    var DEFAULT_ROWS = 10;
    vc.extends({
        data: {
            callingPlatformManageInfo: {
                callingPlatforms: [],
                total: 0,
                records: 1,
                moreCondition: false,
                platformId: '',
                conditions: {
                    platformName: '',
                    ipAddr: '',

                }
            }
        },
        _initMethod: function () {
            vc.component._listCallingPlatforms(DEFAULT_PAGE, DEFAULT_ROWS);
        },
        _initEvent: function () {

            vc.on('callingPlatformManage', 'listCallingPlatform', function (_param) {
                vc.component._listCallingPlatforms(DEFAULT_PAGE, DEFAULT_ROWS);
            });
            vc.on('pagination', 'page_event', function (_currentPage) {
                vc.component._listCallingPlatforms(_currentPage, DEFAULT_ROWS);
            });
        },
        methods: {
            _listCallingPlatforms: function (_page, _rows) {

                vc.component.callingPlatformManageInfo.conditions.page = _page;
                vc.component.callingPlatformManageInfo.conditions.row = _rows;
                vc.component.callingPlatformManageInfo.conditions.communityId = vc.getCurrentCommunity().communityId;
                var param = {
                    params: vc.component.callingPlatformManageInfo.conditions
                };

                //发送get请求
                vc.http.apiGet('callingPlatform.listCallingPlatform',
                    param,
                    function (json, res) {
                        var _callingPlatformManageInfo = JSON.parse(json);
                        vc.component.callingPlatformManageInfo.total = _callingPlatformManageInfo.total;
                        vc.component.callingPlatformManageInfo.records = _callingPlatformManageInfo.records;
                        vc.component.callingPlatformManageInfo.callingPlatforms = _callingPlatformManageInfo.data;
                        vc.emit('pagination', 'init', {
                            total: vc.component.callingPlatformManageInfo.records,
                            currentPage: _page
                        });
                    }, function (errInfo, error) {
                        console.log('请求失败处理');
                    }
                );
            },
            _openAddCallingPlatformModal: function () {
                vc.emit('addCallingPlatform', 'openAddCallingPlatformModal', {});
            },
            _openEditCallingPlatformModel: function (_callingPlatform) {
                vc.emit('editCallingPlatform', 'openEditCallingPlatformModal', _callingPlatform);
            },
            _openDeleteCallingPlatformModel: function (_callingPlatform) {
                vc.emit('deleteCallingPlatform', 'openDeleteCallingPlatformModal', _callingPlatform);
            },
            _queryCallingPlatformMethod: function () {
                vc.component._listCallingPlatforms(DEFAULT_PAGE, DEFAULT_ROWS);

            },
            _moreCondition: function () {
                if (vc.component.callingPlatformManageInfo.moreCondition) {
                    vc.component.callingPlatformManageInfo.moreCondition = false;
                } else {
                    vc.component.callingPlatformManageInfo.moreCondition = true;
                }
            }


        }
    });
})(window.vc);
