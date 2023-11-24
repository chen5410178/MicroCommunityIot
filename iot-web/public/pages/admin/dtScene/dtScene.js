/**
    入驻小区
**/
(function (vc) {
    let DEFAULT_PAGE = 1;
    let DEFAULT_ROWS = 10;
    vc.extends({
        data: {
            dtSceneInfo: {
                dtScenes: [],
                total: 0,
                records: 1,
                moreCondition: false,
                sceneId: '',
                communitys: [],
                conditions: {
                    sceneName: '',
                    state: '',
                    seq: '',
                    communityId: '',
                }
            }
        },
        _initMethod: function () {
            $that._listCommunitys();
        },
        _initEvent: function () {
            vc.on('dtScene', 'listDtScene', function (_param) {
                $that._listDtScenes(DEFAULT_PAGE, DEFAULT_ROWS);
            });
            vc.on('pagination', 'page_event', function (_currentPage) {
                $that._listDtScenes(_currentPage, DEFAULT_ROWS);
            });
        },
        methods: {
            _listDtScenes: function (_page, _rows) {

                $that.dtSceneInfo.conditions.page = _page;
                $that.dtSceneInfo.conditions.row = _rows;
                let param = {
                    params: $that.dtSceneInfo.conditions
                };

                //发送get请求
                vc.http.apiGet('/dtScene.listDtScene',
                    param,
                    function (json, res) {
                        let _json = JSON.parse(json);
                        $that.dtSceneInfo.total = _json.total;
                        $that.dtSceneInfo.records = _json.records;
                        $that.dtSceneInfo.dtScenes = _json.data;
                        vc.emit('pagination', 'init', {
                            total: $that.dtSceneInfo.records,
                            currentPage: _page
                        });
                    }, function (errInfo, error) {
                        console.log('请求失败处理');
                    }
                );
            },
            _openAddDtSceneModal: function () {
                vc.emit('addDtScene', 'openAddDtSceneModal', {});
            },
            _openEditDtSceneModel: function (_dtScene) {
                vc.emit('editDtScene', 'openEditDtSceneModal', _dtScene);
            },
            _openDeleteDtSceneModel: function (_dtScene) {
                vc.emit('deleteDtScene', 'openDeleteDtSceneModal', _dtScene);
            },
            _queryDtSceneMethod: function () {
                $that._listDtScenes(DEFAULT_PAGE, DEFAULT_ROWS);
            },
            _openSettingScene:function(_dtScene){
                window.open('/3d.html#/pages/3d/dtSceneSetting?sceneId='+_dtScene.sceneId+"&communityId="+_dtScene.communityId);
            },
            _moreCondition: function () {
                if ($that.dtSceneInfo.moreCondition) {
                    $that.dtSceneInfo.moreCondition = false;
                } else {
                    $that.dtSceneInfo.moreCondition = true;
                }
            },
            _listCommunitys: function (_page, _rows) {
                let _param = {
                    params: {
                        page: 1,
                        row: 100
                    }
                }
                //发送get请求
                vc.http.apiGet('/community.listCommunitys',
                    _param,
                    function (json, res) {
                        let _json = JSON.parse(json);
                        $that.dtSceneInfo.communitys = _json.data;
                        if (_json.data && _json.data.length > 0) {
                            $that.swatchCommunity(_json.data[0])
                        }
                    },
                    function (errInfo, error) {
                        console.log('请求失败处理');
                    }
                );
            },
            swatchCommunity: function (_community) {
                $that.dtSceneInfo.conditions.communityId = _community.communityId;
                $that._listDtScenes(DEFAULT_PAGE, DEFAULT_ROWS);
            }


        }
    });
})(window.vc);
