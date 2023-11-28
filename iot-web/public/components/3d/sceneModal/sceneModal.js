/**
    入驻小区
**/
import {loadFloorData} from 'api/modal/modalDataApi.js'
(function (vc) {

    vc.extends({
        data: {
            sceneModalInfo: {
                _currentTab:'modalData',
                modal:{},
                modalData:[],
                curData:{},
                objects:[]
            }
        },
        _initMethod: function () {
            

        },
        _initEvent: function () {
            vc.on('sceneModal','setModal',function(_data){
                $that.sceneModalInfo.modal = _data;
                $that.sceneModalInfo._currentTab = 'modalData';
                $that._loadModalData();
            })

        },
        methods: {

            _changeCurrentTab:function(_tab){
                $that.sceneModalInfo._currentTab = _tab;
                if(_tab == 'modals'){
                    $that.sceneModalInfo.objects = [];
                    vc.emit('dtSceneSetting','getAllObject',$that.sceneModalInfo.objects);
                }
            },

            _loadModalData:function(){
                $that.sceneModalInfo.modalData =  [];
                if($that.sceneModalInfo.modal.modalType == 'floor'){
                    loadFloorData({
                        communityId:vc.getParam('communityId'),
                        page:1,
                        row:100
                    }).then(_data=>{
                        _data.forEach(item => {
                            $that.sceneModalInfo.modalData.push({
                                name:item.floorName,
                                dataId:item.floorId,
                                modalType:'floor',
                                path:$that.sceneModalInfo.modal.path
                            })
                        })
                    })
                }
                
            },
            _addModalInSence:function(_modal){
                vc.emit('dtSceneSetting','addModal',_modal);
            },
            _controlObject:function(_obj){
                vc.emit('dtSceneSetting','controlObject',_obj);
            }

            
           
        }
    });
})(window.vc);