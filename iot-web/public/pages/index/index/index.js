(function(vc) {
    vc.extends({
        data: {
            indexInfo: {
                storeTypeCd: '', 
            },
          
        },
        _initMethod: function() {
            setTimeout(function(){
                $that._computeStoreTypeCd();
                
            },1000)
            

        },
        _initEvent: function() {
          
        },
        methods: {

            _computeStoreTypeCd:function(){
                let _getUserInfo = vc.getData('/nav/getUserInfo')
                if(_getUserInfo){
                    $that.indexInfo.storeTypeCd = _getUserInfo.storeTypeCd
                }

                if($that.indexInfo.storeTypeCd == '800900000003'){
                    vc.emit('indexCommunityView','initData',{});
                    vc.emit('indexEvent','initData',{});
                    vc.emit('indexMachineView','initData',{});
                    vc.emit('indexMonitorAlarm','initData',{});
                }

                if($that.indexInfo.storeTypeCd == '800900000001'){
                    vc.emit('adminIndex', 'initData',{})
                }

                if($that.indexInfo.storeTypeCd == '800900000000'){
                    vc.emit('devIndex', 'initData',{})
                }
            }
        }
    });

})(window.vc);