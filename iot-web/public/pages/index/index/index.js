(function(vc) {
    vc.extends({
        data: {
            indexInfo: {
                storeTypeCd: '', 
            },
          
        },
        _initMethod: function() {
            let _getUserInfo = vc.getData('/nav/getUserInfo')
            if(_getUserInfo){
                $that.indexInfo.storeTypeCd = _getUserInfo.storeTypeCd
            }

        },
        _initEvent: function() {
          
        },
        methods: {
        }
    });

})(window.vc);