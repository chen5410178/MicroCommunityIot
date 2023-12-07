/**
    入驻小区
**/

(function (vc) {
    var DEFAULT_PAGE = 1;
    var DEFAULT_ROWS = 10;
    vc.extends({
        data: {
            dtSceneObjScriptInfo: {
                scripts: [],
                scenes: [],
                total: 0,
                records: 1,
                moreCondition: false,
                modalId: '',
                conditions: {
                    objName: '',
                    communityId: '',
                    scriptId: '',
                    sceneId:'',
                }
            }
        },
        _initMethod: function () {

            $that.dtSceneObjScriptInfo.conditions.sceneId = vc.getParam('sceneId');
            $that.dtSceneObjScriptInfo.conditions.communityId = vc.getParam('communityId');

            $that._listDtSceneObjScripts(DEFAULT_PAGE, DEFAULT_ROWS);
            $that._listDtScenes();
        },
        _initEvent: function () {

            vc.on('dtSceneObjScript', 'listDtSceneObjScript', function (_param) {
                $that._listDtSceneObjScripts(DEFAULT_PAGE, DEFAULT_ROWS);
            });
            vc.on('pagination', 'page_event', function (_currentPage) {
                $that._listDtSceneObjScripts(_currentPage, DEFAULT_ROWS);
            });
        },
        methods: {
            _listDtSceneObjScripts: function (_page, _rows) {
                $that.dtSceneObjScriptInfo.conditions.page = _page;
                $that.dtSceneObjScriptInfo.conditions.row = _rows;
                let param = {
                    params: $that.dtSceneObjScriptInfo.conditions
                };

                //发送get请求
                vc.http.apiGet('/dtScript.listDtSceneObjScript',
                    param,
                    function (json, res) {
                        let _json = JSON.parse(json);
                        $that.dtSceneObjScriptInfo.total = _json.total;
                        $that.dtSceneObjScriptInfo.records = _json.records;
                        $that.dtSceneObjScriptInfo.scripts = _json.data;
                        vc.emit('pagination', 'init', {
                            total: $that.dtSceneObjScriptInfo.records,
                            currentPage: _page
                        });
                    }, function (errInfo, error) {
                        console.log('请求失败处理');
                    }
                );
            },
            _openDeleteDtSceneObjScriptModel: function (_dtSceneObjScript) {
                vc.emit('deleteObjectScript','openDeleteModal', _dtSceneObjScript);
            },
            _queryDtSceneObjScriptMethod: function () {
                $that._listDtSceneObjScripts(DEFAULT_PAGE, DEFAULT_ROWS);
            },
            _listDtScenes: function () {
                let param = {
                    params: {
                        page:1,
                        row:100,
                        communityId:$that.dtSceneObjScriptInfo.conditions.communityId
                    }
                };
                //发送get请求
                vc.http.apiGet('/dtScene.listDtScene',
                    param,
                    function (json, res) {
                        let _json = JSON.parse(json);
                        $that.dtSceneObjScriptInfo.scenes = _json.data;
                    }, function (errInfo, error) {
                        console.log('请求失败处理');
                    }
                );
            },
            swatchScene:function(_dict){
                $that.dtSceneObjScriptInfo.conditions.sceneId = _dict.sceneId;
                $that._listDtSceneObjScripts(DEFAULT_PAGE, DEFAULT_ROWS);
            }


        }
    });
})(window.vc);
