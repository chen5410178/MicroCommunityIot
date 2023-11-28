/**
    入驻小区
**/

//import {initScene,add3DObject} from 'api/3d/sceneApi.js';
import Scene3d from 'api/3d/Scene3d.js'
(function (vc) {
    vc.extends({
        data: {
            dtSceneSettingInfo: {
                scene:{},
                modal:'translate',
                object:{}
            }
        },
        _initMethod: function () {
            //$that.dtSceneSettingInfo.scene = initScene("webgl-scene",$that.dtSceneSettingInfo);
            //addBoxGeometry("webgl-scene",$that.dtSceneSettingInfo);

            let _scene = new Scene3d('webgl-scene');
            $that.dtSceneSettingInfo.scene = _scene;

            _scene.setSelect3DObject(function(_object){
                console.log('选中objetc',_object);
                vc.emit('dtSceneSetting','controlObject',_object);
            })
        },
        _initEvent: function () {
            vc.on('dtSceneSetting','addModal',function(_modal){
                //add3DObject($that.dtSceneSettingInfo,_modal)
                $that.dtSceneSettingInfo.scene.add3DObject(_modal);
                //$that.dtSceneSettingInfo.scene.add3DBox(_modal);
            });
            vc.on('dtSceneSetting','setModal',function(_modal){
                $that.dtSceneSettingInfo.modal = _modal;
                $that.dtSceneSettingInfo.scene.setTransformControlModal(_modal);
            });
            vc.on('dtSceneSetting','getAllObject',function(_objects){
                let _objs = $that.dtSceneSettingInfo.scene.getAllObject();
                _objs.forEach(_o => {
                    _objects.push(_o);
                });
            });

            vc.on('dtSceneSetting','controlObject',function(_object){
                $that.dtSceneSettingInfo.object = _object;
                vc.emit('curModal','setModalData',{
                    name:_object.name,
                    position:_object.position,
                    object:_object
                });
                $that.dtSceneSettingInfo.scene.setTransformControlModal($that.dtSceneSettingInfo.modal);
                $that.dtSceneSettingInfo.scene.controlObject(_object);
            })
        },
        methods: {
           
        }
    });
})(window.vc);