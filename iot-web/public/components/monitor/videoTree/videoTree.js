(function (vc) {
    let DEFAULT_PAGE = 1;
    let DEFAULT_ROW = 10;
    vc.extends({
        data: {
            videoTreeInfo: {
                videoPos: [],
                callName: ''
            }
        },
        _initMethod: function () {
        },
        _initEvent: function () {
            vc.on('videoTree', 'initVideoTree', function (_param) {
                $that.videoTreeInfo.callName = _param.callName;
                $that._loadVideoTreeFloorAndUnits();
            });
        },
        methods: {
            _loadVideoTreeFloorAndUnits: function () {
                let param = {
                    params: {
                        page:1,
                        row:1000,
                        communityId:vc.getCurrentCommunity().communityId
                    }
                };
                //发送get请求
                vc.http.apiGet('/monitorArea.getMonitorArea',
                    param,
                    function (json) {
                        let _json = JSON.parse(json);
                        $that.videoTreeInfo.videoPos = _json.data;
                        $that._initJsTreeVideoTreeVideoPos();
                    },
                    function () {
                        console.log('请求失败处理');
                    });
            },
            _initJsTreeVideoTreeVideoPos: function () {
                let _data = $that._doJsTreeVideoTreeData();
                // _data = _data.sort(function(a, b) {
                //     return a.communityName - b.communityName
                // })
                $.jstree.destroy()
                $("#jstree_videoTree").jstree({
                    "checkbox": {
                        "keep_selected_style": false
                    },
                    'state': { //一些初始化状态
                        "opened": true,
                    },
                    'core': {
                        "check_callback": true,
                        'data': _data
                    }
                });
                $("#jstree_videoTree").on("ready.jstree", function (e, data) {
                    //data.instance.open_all();//打开所有节点
                    let _callName = $that.videoTreeInfo.callName;
                    if (_callName == 'oweFeeCallable') {
                        return;
                    }
                    $('#jstree_videoTree').jstree('select_node', _data[0].children[0].id /* , true */);
                });
                $('#jstree_videoTree').on("changed.jstree", function (e, data) {
                    if (data.action == 'model' || data.action == 'ready') {
                        //默认合并
                        //$("#jstree_floorUnit").jstree("close_all");
                        return;
                    }
                    let _selected = data.selected[0];
                    if (_selected.startsWith('f_')) {
                        return;
                    }
                    //console.log(_selected, data.node.original.maId)
                    if (_selected.startsWith('u_')) {
                        $that._videoTreeLoadRoom(data.node.original.maId, data);
                    }
                    if (_selected.startsWith('r_')) {
                        vc.emit($that.videoTreeInfo.callName, 'selectVideo', {
                            machineName: data.node.original.machineName,
                            machineId: data.node.original.machineId
                        })
                    }
                });
                $('#jstree_videoTree')
                    .on('click', '.jstree-anchor', function (e) {
                        $(this).jstree(true).toggle_node(e.target);
                    })
            },
            _videoTreeLoadRoom: function (_maId, data) {
                //获取选中的节点
                let node = data.instance.get_node(data.selected[0]);
                //遍历选中节点的子节点
                //let childNodes = data.instance.get_children_dom(node);
                let childNodes = node.children;
                //$('#u_' + _maId)
                if (childNodes && childNodes.length > 0) {
                    $('#jstree_videoTree').jstree('open_node', '#u_' + _maId);
                    return;
                }
                let param = {
                    params: {
                        page: 1,
                        row: 1000,
                        maId: _maId,
                        communityId:vc.getCurrentCommunity().communityId
                    }
                }
                //发送get请求
                let _datas = [];
                vc.http.apiGet('/monitorMachine.listMonitorMachine',
                    param,
                    function (json, res) {
                        let _json = JSON.parse(json);
                        if (_json.total < 1) {
                            vc.toast('未找到摄像头');
                            return;
                        }
                        _json.data.forEach(_machine => {
                            let _text = _machine.machineName;
                            let _data = {
                                id: 'r_' + _machine.machineId,
                                machineId: _machine.machineId,
                                machineName: _machine.machineName,
                                text: _text,
                                icon: "/img/org.png",
                            };
                            _datas.push(_data);
                            // $('#jstree_videoTree').jstree('create_node', $('#u_' + _maId), _data, "last", false, false);
                        })
                        $('#jstree_videoTree').jstree('_append_json_data', $('#u_' + _maId), _datas, function () {
                            // 这个回调函数要加  不然会报错，即使这个函数里面什么也不做
                        });
                        setTimeout(function () {
                            $('#jstree_videoTree').jstree('open_node', '#u_' + _maId);
                        }, 1000);
                        if (_json.data && _json.data.length > 0) {
                            // vc.emit($that.videoTreeInfo.callName, 'selectVideo', {
                            //     machineName: _json.data[0].machineName,
                            //     machineId: _json.data[0].machineId
                            // })
                        }
                    },
                    function (errInfo, error) {
                        console.log('请求失败处理');
                    }
                );
            },
            _doJsTreeVideoTreeData: function () {
                let _mFloorTree = [];
                let _videoPos = $that.videoTreeInfo.videoPos;
                //构建 第一层菜单组
                _videoPos.forEach(pItem => {
                    let _includeFloor = false;
                    for (let _mgIndex = 0; _mgIndex < _mFloorTree.length; _mgIndex++) {
                        if (pItem.communityId == _mFloorTree[_mgIndex].communityId) {
                            _includeFloor = true;
                        }
                    }
                    if (!_includeFloor) {
                        let _floorItem = {
                            id: 'f_' + pItem.communityId,
                            communityId: pItem.communityId,
                            communityName: pItem.communityName,
                            icon: "/img/org.png",
                            text: pItem.communityName ,
                            state: {
                                opened: false
                            },
                            children: []
                        };
                        $that._doJsTreeVideoTreeMenuData(_floorItem);
                        _mFloorTree.push(_floorItem);
                    }
                });
                return _mFloorTree;
            },
            _doJsTreeVideoTreeMenuData: function (_floorItem) {
                let _videoPos = $that.videoTreeInfo.videoPos;
                //构建菜单
                let _children = _floorItem.children;
                for (let _pIndex = 0; _pIndex < _videoPos.length; _pIndex++) {
                    if (_floorItem.communityId == _videoPos[_pIndex].communityId) {
                        let _includeMenu = false;
                        for (let _mgIndex = 0; _mgIndex < _children.length; _mgIndex++) {
                            if (_videoPos[_pIndex].maId == _children[_mgIndex].maId) {
                                _includeMenu = true;
                            }
                        }
                        if (!_includeMenu) {
                            let _menuItem = {
                                id: 'u_' + _videoPos[_pIndex].maId,
                                maId: _videoPos[_pIndex].maId,
                                text: _videoPos[_pIndex].name ,
                                icon: "/img/org.png",
                                state: {
                                    opened: true
                                },
                                children: []
                            };
                            _children.push(_menuItem);
                        }
                    }
                }
            },
        }
    });
})(window.vc);