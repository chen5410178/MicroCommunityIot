/**
    入驻小区
**/
(function (vc) {

    vc.extends({
        data: {
            cockpitRightFLoorInfo: {
                floorId:'',
                layer:33,
                curLayer:'',
            }
        },
        _initMethod: function () {
            

        },
        _initEvent: function () {
           
            vc.on('cockpitRightFLoor','notify',function(_floor){
               $that.cockpitRightFLoorInfo.floorId = _floor.objId
            })
          

        },
        methods: {
           
            _changeLayer:function(_layer){
                $that.cockpitRightFLoorInfo.curLayer = _layer;
            }
        }
    });
})(window.vc);