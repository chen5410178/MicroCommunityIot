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
                objects:[]
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
            }

            
           
        }
    });
})(window.vc);