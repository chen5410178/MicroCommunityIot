(function (vc) {
    vc.extends({
        data: {
            indexMachineViewInfo: {
                accessControlCount:0,
                accessControlOfflineCount:0,
                barrierCount:0,
                barrierOfflineCount:0,
                chargeCount:0,
                chargeOfflineCount:0,
                meterCount:0,
                meterOfflineCount:0,
                liftCount:0,
                liftOfflineCount:0,
                lampCount:0,
                lampOfflineCount:0,
            }
        },
        _initMethod: function () {
           
        },
        _initEvent: function () {
            vc.on('indexMachineView','initData',function(){
                $that._loadPropertyIndexMachines();
            })
        },
        methods: {
            _loadPropertyIndexMachines: function () {
                let param = {
                    params: {
                        page: 1,
                        row: 10,
                        communityId: vc.getCurrentCommunity().communityId
                    }
                };

                //发送get请求
                vc.http.apiGet('/propertyIndex.queryPropertyMachinesIndex',
                    param,
                    function (json, res) {
                        let _res = JSON.parse(json);
                        if(_res.code !== 0){
                            return;
                        }
                        vc.copyObject(_res.data, $that.indexMachineViewInfo);
                    },
                    function (errInfo, error) {
                        console.log('请求失败处理');
                    }
                );
            },
            _toLift:function(){
                vc.jumpToPage('/#/pages/lift/liftMachineManage?tab=电梯信息')
            },
            _toLamp:function(){
                vc.jumpToPage('/#/pages/lamp/lampMachineManage?tab=路灯信息')
            }
        }
    })
})(window.vc);