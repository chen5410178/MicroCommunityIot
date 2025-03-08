/**
 入驻小区
 **/
(function (vc) {
    vc.extends({
        propTypes: {
            callBackListener: vc.propTypes.string, //父组件名称
        },
        data: {
            cityAreaTreeInfo: {
                orgs: [],
                orgId: '',
                curCityArea: {}
            }
        },
        _initMethod: function () {
            $that._loadCityAreaTrees();
        },
        _initEvent: function () {
            vc.on('cityAreaTree', 'refreshTree', function (_param) {
                $that._loadCityAreaTrees();
            });
        },
        methods: {
            _loadCityAreaTrees: function () {
                let param = {
                    params: {
                        hc: 1.8
                    }
                };
                //发送get请求
                vc.http.apiGet('/area.queryAreaTree',
                    param,
                    function (json) {
                        let _orgs = JSON.parse(json).data;
                        $that.cityAreaTreeInfo.orgs = _orgs;
                        $that._initJsTreeFloorUnit();
                    },
                    function () {
                        console.log('请求失败处理');
                    });
            },
            _initJsTreeFloorUnit: function () {
                let _data = $that.cityAreaTreeInfo.orgs;
                $.jstree.destroy()
                $("#jstree_org").jstree({
                    "checkbox": {
                        "keep_selected_style": false
                    },
                    'state': { //一些初始化状态
                        "opened": false,
                    },
                    // 'plugins': ['contextmenu'],
                    'core': {
                        'data': _data
                    },
                });
                $("#jstree_org").on("ready.jstree", function (e, data) {
                    //data.instance.open_all(); //打开所有节点
                });
                $('#jstree_org').on("changed.jstree", function (e, data) {
                    if (data.action == 'model' || data.action == 'ready') {
                        //默认合并
                        //$("#jstree_org").jstree("close_all");
                        return;
                    }
                    $that.cityAreaTreeInfo.curCityArea = data.node.original;
                    $that.cityAreaTreeInfo.curCityArea.orgId = $that.cityAreaTreeInfo.curCityArea.id;
                    vc.emit($props.callBackListener, 'switchCityAreaTree', {
                        areaCode: data.node.original.areaCode,
                        areaLevel: data.node.original.areaLevel,
                        areaName: $that.cityAreaTreeInfo.curCityArea.text
                    })
                });
                // $('#jstree_org')
                //     .on('click', '.jstree-anchor', function(e) {
                //         $(this).jstree(true).toggle_node(e.target);
                //     })
            },
            _openAddCityAreaModal: function () {
                if (!$that.cityAreaTreeInfo.curCityArea.id) {
                    vc.toast('未选择组织');
                    return;
                }
                let _areaLevel = $that.cityAreaTreeInfo.curCityArea.areaLevel;
                if( _areaLevel== '101'){
                    _areaLevel = "202";
                }else if(_areaLevel == '202'){
                    _areaLevel = "303";
                }else if(_areaLevel == '303'){
                    vc.toast('不能添加子节点');
                    return;
                }else{
                    _areaLevel = "101";
                }
                vc.emit('addCityArea', 'openAddCityAreaModal', {
                    parentAreaCode: $that.cityAreaTreeInfo.curCityArea.areaCode,
                    areaLevel:_areaLevel,
                    parentAreaName: $that.cityAreaTreeInfo.curCityArea.text
                });
            },
            _openEditCityAreaTreeModel: function () {
                if (!$that.cityAreaTreeInfo.curCityArea.id) {
                    vc.toast('未选择组织');
                    return;
                }
                vc.emit('editCityArea', 'openEditCityAreaModal', $that.cityAreaTreeInfo.curCityArea);
            },
            _openDeleteCityAreaTreeModel: function () {
                if (!$that.cityAreaTreeInfo.curCityArea.id) {
                    vc.toast('未选择组织');
                    return;
                }
                vc.emit('deleteCityArea', 'openDeleteCityAreaModal', $that.cityAreaTreeInfo.curCityArea);
            },
        }
    });
})(window.vc);