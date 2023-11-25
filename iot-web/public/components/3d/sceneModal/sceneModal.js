/**
    入驻小区
**/
import {loadFloorData} from 'api/modal/modalDataApi.js'
(function (vc) {

    vc.extends({
        data: {
            sceneModalInfo: {
                modal:{},
                modalData:[],
                curData:{}
            }
        },
        _initMethod: function () {
            

        },
        _initEvent: function () {
            vc.on('sceneModal','setModal',function(_data){
                $that.sceneModalInfo.modal = _data;
                $that._loadModalData();
            })

        },
        methods: {

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
            }

            
           
        }
    });
})(window.vc);