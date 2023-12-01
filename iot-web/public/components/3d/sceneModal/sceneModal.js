/**
    入驻小区
**/
import {loadModalData} from 'api/modal/modalDataApi.js'
(function (vc) {

    vc.extends({
        data: {
            sceneModalInfo: {
                _currentTab:'modalData',
                modal:{},
                modalData:[],
                curData:{},
                objects:[],
            }
        },
        _initMethod: function () {
            

        },
        _initEvent: function () {
            vc.on('sceneModal','setModal',function(_data){
                _data.communityId = vc.getParam('communityId');
                $that.sceneModalInfo.modal = _data;
                $that.sceneModalInfo._currentTab = 'modalData';
                $that._loadModalData();
            });
            vc.on('sceneModal','notifyObjects',function(_objs){
                $that.sceneModalInfo.objects = [];
                $that._copyTreeData(_objs,$that.sceneModalInfo.objects);
                $that._showSceneTree();
            })
            vc.on('sceneModal','_reloadTab',function(_tab){
                $that._changeCurrentTab( $that.sceneModalInfo._currentTab);
            })

        },
        methods: {
            _showSceneTree:function(){
                $.jstree.destroy()
                $("#sceneTree").jstree({
                    "checkbox": {
                        "keep_selected_style": false
                    },
                    'state': { //一些初始化状态
                        "opened": false,
                    },
                    // 'plugins': ['contextmenu'],
                    'core': {
                        'data': $that.sceneModalInfo.objects
                    },
                });
                $("#sceneTree").on("ready.jstree", function (e, data) {
                    //data.instance.open_all(); //打开所有节点
                });
                $('#sceneTree').on("changed.jstree", function (e, data) {
                    if (data.action == 'model' || data.action == 'ready') {
                        //默认合并
                        //$("#jstree_org").jstree("close_all");
                        return;
                    }
                    
                    // $that.orgTreeInfo.curOrg = data.node.original;
                    // $that.orgTreeInfo.curOrg.orgId = $that.orgTreeInfo.curOrg.id;
                    $that._controlObject(data.node.original)
                });
            },
            _changeCurrentTab:function(_tab){
                $that.sceneModalInfo._currentTab = _tab;
                if(_tab == 'modals'){
                    vc.emit('dtSceneSetting','getAllObject',[]);
                }
            },

            _loadModalData:function(){
                $that.sceneModalInfo.modalData =  [];
                
                loadModalData($that.sceneModalInfo.modal,$that.sceneModalInfo.modalData);
            },
            _addModalInSence:function(_modal){
                vc.emit('dtSceneSetting','addModal',_modal);
            },
            _deleteModalInSence:function(_modal){
                vc.emit('dtSceneSetting','deleteModal',_modal);
                $that._changeCurrentTab('modals');
            },
            _controlObject:function(_obj){
                vc.emit('dtSceneSetting','controlObject',_obj);
            },
            _copyTreeData:function(_sceneChildren,_nodeItem){
                if(!_sceneChildren){
                    return;
                }
              
                if(_sceneChildren.length < 1){
                    return;
                }
                let _node = {};
                _sceneChildren.forEach(_item => {
                   
                    _node = {
                        id: _item.id,
                        text: _item.name,
                        icon:'/img/org.png',
                        tags: [0],
                        children: []
                    }
                    
                    if(_item.children && _item.children.length >0){
                        $that._copyTreeData(_item.children,_node.children);
                    }
                    _nodeItem.push(_node);
                });
            }

            
           
        }
    });
})(window.vc);