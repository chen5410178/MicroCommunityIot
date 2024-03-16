/**
    放行申请 组件
**/
(function(vc){

    vc.extends({
        propTypes: {
           callBackListener:vc.propTypes.string, //父组件名称
           callBackFunction:vc.propTypes.string //父组件监听方法
        },
        data:{
            viewMeterMachineLogInfo:{
                index:0,
                flowComponent:'viewMeterMachineLogInfo',
                personName:'',

            }
        },
        _initMethod:function(){
            //根据请求参数查询 查询 业主信息
            vc.component._loadMeterMachineLogInfoData();
        },
        _initEvent:function(){
            vc.on('viewMeterMachineLogInfo','chooseMeterMachineLog',function(_app){
                vc.copyObject(_app, vc.component.viewMeterMachineLogInfo);
                vc.emit($props.callBackListener,$props.callBackFunction,vc.component.viewMeterMachineLogInfo);
            });

            vc.on('viewMeterMachineLogInfo', 'onIndex', function(_index){
                vc.component.viewMeterMachineLogInfo.index = _index;
            });

        },
        methods:{

            _openSelectMeterMachineLogInfoModel(){
                vc.emit('chooseMeterMachineLog','openChooseMeterMachineLogModel',{});
            },
            _openAddMeterMachineLogInfoModel(){
                vc.emit('addMeterMachineLog','openAddMeterMachineLogModal',{});
            },
            _loadMeterMachineLogInfoData:function(){

            }
        }
    });

})(window.vc);
