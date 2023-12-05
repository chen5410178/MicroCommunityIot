/**
    入驻小区
**/
(function (vc) {
    var DEFAULT_PAGE = 1;
    var DEFAULT_ROWS = 10;
    vc.extends({
        data: {
            dtScriptInfo: {
                dtScripts: [],
                total: 0,
                records: 1,
                moreCondition: false,
                scriptId: '',
                conditions: {
                    scriptId: '',
                    scriptName: '',
                    scriptType: '',

                }
            }
        },
        _initMethod: function () {
            $that._listDtScripts(DEFAULT_PAGE, DEFAULT_ROWS);
        },
        _initEvent: function () {

            vc.on('dtScript', 'listDtScript', function (_param) {
                $that._listDtScripts(DEFAULT_PAGE, DEFAULT_ROWS);
            });
            vc.on('pagination', 'page_event', function (_currentPage) {
                $that._listDtScripts(_currentPage, DEFAULT_ROWS);
            });
        },
        methods: {
            _listDtScripts: function (_page, _rows) {
                $that.dtScriptInfo.conditions.page = _page;
                $that.dtScriptInfo.conditions.row = _rows;
                var param = {
                    params: $that.dtScriptInfo.conditions
                };

                //发送get请求
                vc.http.apiGet('/dtScript.listDtScript',
                    param,
                    function (json, res) {
                        let _json = JSON.parse(json);
                        $that.dtScriptInfo.total = _json.total;
                        $that.dtScriptInfo.records = _json.records;
                        $that.dtScriptInfo.dtScripts = _json.data;
                        vc.emit('pagination', 'init', {
                            total: $that.dtScriptInfo.records,
                            currentPage: _page
                        });
                    }, function (errInfo, error) {
                        console.log('请求失败处理');
                    }
                );
            },
            _openAddDtScriptModal: function () {
                vc.emit('addDtScript', 'openAddDtScriptModal', {});
            },
            _openEditDtScriptModel: function (_dtScript) {
                vc.emit('editDtScript', 'openEditDtScriptModal', _dtScript);
            },
            _openDeleteDtScriptModel: function (_dtScript) {
                vc.emit('deleteDtScript', 'openDeleteDtScriptModal', _dtScript);
            },
            _queryDtScriptMethod: function () {
                $that._listDtScripts(DEFAULT_PAGE, DEFAULT_ROWS);

            },
            _moreCondition: function () {
                if ($that.dtScriptInfo.moreCondition) {
                    $that.dtScriptInfo.moreCondition = false;
                } else {
                    $that.dtScriptInfo.moreCondition = true;
                }
            }


        }
    });
})(window.vc);
