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
            }
        },
        _initMethod: function () {
            //$that.dtSceneSettingInfo.scene = initScene("webgl-scene",$that.dtSceneSettingInfo);
            //addBoxGeometry("webgl-scene",$that.dtSceneSettingInfo);

            let _scene = new Scene3d('webgl-scene');
            $that.dtSceneSettingInfo.scene = _scene;

            _scene.setSelect3DObject(function(_object){
                console.log('选中objetc',_object);
                //_scene.changeMaterial(_object)
                _scene.move3dObject(_object);
            })
        },
        _initEvent: function () {
            vc.on('dtSceneSetting','addModal',function(_modal){
                //add3DObject($that.dtSceneSettingInfo,_modal)
                $that.dtSceneSettingInfo.scene.add3DObject(_modal);
                //$that.dtSceneSettingInfo.scene.add3DBox(_modal);
            })

        },
        methods: {
           
        }
    });
})(window.vc);