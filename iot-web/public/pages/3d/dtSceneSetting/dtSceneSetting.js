/**
    入驻小区
**/

import {initScene,addBoxGeometry} from 'api/3d/sceneApi.js';
(function (vc) {
    vc.extends({
        data: {
            dtSceneSettingInfo: {
                scene:{},
                camera:{},
                renderer:{}
            }
        },
        _initMethod: function () {
            $that.dtSceneSettingInfo.scene = initScene("webgl-scene",$that.dtSceneSettingInfo);
            //addBoxGeometry("webgl-scene",$that.dtSceneSettingInfo);
        },
        _initEvent: function () {


        },
        methods: {
           
        }
    });
})(window.vc);