/**
    入驻小区
**/

import {initScene,add3DObject} from 'api/3d/sceneApi.js';
(function (vc) {
    vc.extends({
        data: {
            dtSceneSettingInfo: {
                scene:{},
                camera:{},
                renderer:{},
                controls:{}
            }
        },
        _initMethod: function () {
            $that.dtSceneSettingInfo.scene = initScene("webgl-scene",$that.dtSceneSettingInfo);
            //addBoxGeometry("webgl-scene",$that.dtSceneSettingInfo);
        },
        _initEvent: function () {
            vc.on('dtSceneSetting','addModal',function(_modal){
                add3DObject($that.dtSceneSettingInfo,_modal)
            })

        },
        methods: {
           
        }
    });
})(window.vc);