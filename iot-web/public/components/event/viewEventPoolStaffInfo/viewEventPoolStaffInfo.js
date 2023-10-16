/**
 事件处理 组件
 **/
(function (vc) {

    vc.extends({
        data: {
            viewEventPoolStaffInfo: {
                epsId: '',
                eventId: '',
                staffId: '',
                staffName: '',
                eventReason: '',
                eventResult: '',
                communityId: '',
                state: '',
                stateName: '',
                eventHours: '',
                createTime: '',
            }
        },
        _initMethod: function () {
        },
        _initEvent: function () {
            vc.on('viewEventPoolStaffInfo', 'chooseEventPoolStaff', function (_eventPool) {
                $that.cleanViewEventPoolStaffInfo();
                vc.copyObject(_eventPool.eventPoolStaffList[0], $that.viewEventPoolStaffInfo);
                switch (_eventPool.eventPoolStaffList[0].state) {
                    case 'W':
                        $that.viewEventPoolStaffInfo.stateName = '待处理';
                        break;
                    case 'D':
                        $that.viewEventPoolStaffInfo.stateName = '处理中';
                        break;
                    case 'TC':
                        $that.viewEventPoolStaffInfo.stateName = '超时处理完成';
                        break;
                    case 'NC':
                        $that.viewEventPoolStaffInfo.stateName = '正常处理完成';
                        break;
                }
                $('#viewEventPoolStaffInfoModel').modal('show');
            });
        },
        methods: {
            cleanViewEventPoolStaffInfo: function () {
                $that.viewEventPoolStaffInfo = {
                    epsId: '',
                    eventId: '',
                    staffId: '',
                    staffName: '',
                    eventReason: '',
                    eventResult: '',
                    communityId: '',
                    state: '',
                    stateName: '',
                    eventHours: '',
                    createTime: '',
                }
            },
            cancelEventPoolStaff: function () {
                vc.http.apiPost(
                    '/eventPool.cancelEventPoolStaff',
                    JSON.stringify($that.viewEventPoolStaffInfo),
                    {
                        emulateJSON:true
                    },
                    function(json,res){
                        let _json = JSON.parse(json);
                        if (_json.code === 0) {
                            $('#viewEventPoolStaffInfoModel').modal('hide');
                            vc.toast("成功");
                            vc.emit('eventPoolManage', 'listEventPool', {});
                            return ;
                        }
                        vc.toast(_json.msg);
                    },
                    function(errInfo,error){
                        console.log('请求失败处理');
                        vc.toast(errInfo);
                    }
                );
            }
        }
    });
})(window.vc);
