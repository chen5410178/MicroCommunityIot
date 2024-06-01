/**
    入驻小区
**/
(function(vc){
    var DEFAULT_PAGE = 1;
    var DEFAULT_ROWS = 10;
    vc.extends({
        data:{
            meterMachineLogManageInfo:{
                meterMachineLogs:[],
                total:0,
                records:1,
                moreCondition:false,
                logId:'',
                conditions:{
                    personName:'',
                    communityId: vc.getCurrentCommunity().communityId
                }
            }
        },
        _initMethod:function(){
            vc.component._listMeterMachineLogs(DEFAULT_PAGE, DEFAULT_ROWS);
        },
        _initEvent:function(){

            vc.on('meterMachineLogManage','listMeterMachineLog',function(_param){
                  vc.component._listMeterMachineLogs(DEFAULT_PAGE, DEFAULT_ROWS);
            });
             vc.on('pagination','page_event',function(_currentPage){
                vc.component._listMeterMachineLogs(_currentPage,DEFAULT_ROWS);
            });
        },
        methods:{
            _listMeterMachineLogs:function(_page, _rows){

                vc.component.meterMachineLogManageInfo.conditions.page = _page;
                vc.component.meterMachineLogManageInfo.conditions.row = _rows;
                var param = {
                    params:vc.component.meterMachineLogManageInfo.conditions
               };

               //发送get请求
               vc.http.apiGet('meterMachineLog.listMeterMachineLog',
                             param,
                             function(json,res){
                                var _meterMachineLogManageInfo=JSON.parse(json);
                                vc.component.meterMachineLogManageInfo.total = _meterMachineLogManageInfo.total;
                                vc.component.meterMachineLogManageInfo.records = _meterMachineLogManageInfo.records;
                                vc.component.meterMachineLogManageInfo.meterMachineLogs = _meterMachineLogManageInfo.data;
                                vc.emit('pagination','init',{
                                     total:vc.component.meterMachineLogManageInfo.records,
                                     currentPage:_page
                                 });
                             },function(errInfo,error){
                                console.log('请求失败处理');
                             }
                           );
            },
            _openViewMeterMachineLogModal: function(_meterMachineLog) {
                vc.emit('showMeterMachineLog', 'openShowMeterMachineLogModal', _meterMachineLog);
            },
            _openAddMeterMachineLogModal:function(){
                vc.emit('addMeterMachineLog','openAddMeterMachineLogModal',{});
            },
            _openEditMeterMachineLogModel:function(_meterMachineLog){
                vc.emit('editMeterMachineLog','openEditMeterMachineLogModal',_meterMachineLog);
            },
            _openDeleteMeterMachineLogModel:function(_meterMachineLog){
                vc.emit('deleteMeterMachineLog','openDeleteMeterMachineLogModal',_meterMachineLog);
            },
            _queryMeterMachineLogMethod:function(){
                vc.component._listMeterMachineLogs(DEFAULT_PAGE, DEFAULT_ROWS);

            },
            _moreCondition:function(){
                if(vc.component.meterMachineLogManageInfo.moreCondition){
                    vc.component.meterMachineLogManageInfo.moreCondition = false;
                }else{
                    vc.component.meterMachineLogManageInfo.moreCondition = true;
                }
            }

             
        }
    });
})(window.vc);
