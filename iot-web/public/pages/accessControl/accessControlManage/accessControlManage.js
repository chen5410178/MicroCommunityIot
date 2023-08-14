/**
    入驻小区
**/
(function(vc){
    var DEFAULT_PAGE = 1;
    var DEFAULT_ROWS = 10;
    vc.extends({
        data:{
            accessControlManageInfo:{
                accessControls:[],
                total:0,
                records:1,
                moreCondition:false,
                machineId:'',
                conditions:{
                    machineCode:'',
machineName:'',
locationName:'',

                }
            }
        },
        _initMethod:function(){
            vc.component._listAccessControls(DEFAULT_PAGE, DEFAULT_ROWS);
        },
        _initEvent:function(){
            
            vc.on('accessControlManage','listAccessControl',function(_param){
                  vc.component._listAccessControls(DEFAULT_PAGE, DEFAULT_ROWS);
            });
             vc.on('pagination','page_event',function(_currentPage){
                vc.component._listAccessControls(_currentPage,DEFAULT_ROWS);
            });
        },
        methods:{
            _listAccessControls:function(_page, _rows){

                vc.component.accessControlManageInfo.conditions.page = _page;
                vc.component.accessControlManageInfo.conditions.row = _rows;
                var param = {
                    params:vc.component.accessControlManageInfo.conditions
               };

               //发送get请求
               vc.http.apiGet('accessControl.listAccessControls',
                             param,
                             function(json,res){
                                var _accessControlManageInfo=JSON.parse(json);
                                vc.component.accessControlManageInfo.total = _accessControlManageInfo.total;
                                vc.component.accessControlManageInfo.records = _accessControlManageInfo.records;
                                vc.component.accessControlManageInfo.accessControls = _accessControlManageInfo.data;
                                vc.emit('pagination','init',{
                                     total:vc.component.accessControlManageInfo.records,
                                     currentPage:_page
                                 });
                             },function(errInfo,error){
                                console.log('请求失败处理');
                             }
                           );
            },
            _openAddAccessControlModal:function(){
                vc.emit('addAccessControl','openAddAccessControlModal',{});
            },
            _openEditAccessControlModel:function(_accessControl){
                vc.emit('editAccessControl','openEditAccessControlModal',_accessControl);
            },
            _openDeleteAccessControlModel:function(_accessControl){
                vc.emit('deleteAccessControl','openDeleteAccessControlModal',_accessControl);
            },
            _queryAccessControlMethod:function(){
                vc.component._listAccessControls(DEFAULT_PAGE, DEFAULT_ROWS);

            },
            _moreCondition:function(){
                if(vc.component.accessControlManageInfo.moreCondition){
                    vc.component.accessControlManageInfo.moreCondition = false;
                }else{
                    vc.component.accessControlManageInfo.moreCondition = true;
                }
            }

             
        }
    });
})(window.vc);
