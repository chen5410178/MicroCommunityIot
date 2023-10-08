(function(vc){
    vc.extends({
        propTypes: {
           emitChooseStaff:vc.propTypes.string,
           emitLoadData:vc.propTypes.string
        },
        data:{
            chooseStaffInfo:{
                selectStaffList: [],
                _currentStaffName:'',
            },
            eventStaffList: [],
            staffList: [],
        },
        _initMethod:function(){

        },
        _initEvent:function(){
            vc.on('chooseStaff','openChooseStaffModel',function(){
                $that._listEventStaffs();
                $('#chooseStaffModel').modal('show');
            });
        },
        methods:{
            selectAll: function (e) {
                var _selectStaffList = $that.chooseStaffInfo.selectStaffList;
                var checkObj = document.querySelectorAll('.checkItem'); // 获取所有checkbox项
                if (e.target.checked) { // 判定全选checkbox的勾选状态
                    for (var i = 0; i < checkObj.length; i++) {
                        if (!checkObj[i].checked) { // 将未勾选的checkbox选项push到绑定数组中
                            $that.chooseStaffInfo.selectStaffList.push(checkObj[i]._value);
                        }
                    }
                } else { // 如果是去掉全选则清空checkbox选项绑定数组
                    $that.chooseStaffInfo.selectStaffList = [];
                }
            },
            _submitSelectStaff: function () {
                vc.emit($props.emitLoadData,'listStaffData', $that.chooseStaffInfo.selectStaffList);
                $('#chooseStaffModel').modal('hide');
            },
            _listStaffList: function () {

                let param = {
                    params: {
                        page: -1,
                        row: 100,
                        communityId: vc.getCurrentCommunity().communityId,
                    }
                };
                //发送get请求
                vc.http.apiGet('user.listCommunityStaffsByCommunityId',
                    param,
                    function (json, res) {
                        let _json = JSON.parse(json);
                        $that.staffList = [];
                        if ($that.eventStaffList.length === 0) {
                            $that.staffList = _json.data;
                        } else {
                            lo:for (let i = 0; i < _json.data.length; i++) {
                                for (let j = 0; j < $that.eventStaffList.length; j++) {
                                    if ($that.eventStaffList[j].staffId === _json.data[i].userId) {
                                        continue lo;
                                    }
                                }
                                $that.staffList.push(_json.data[i]);
                            }
                        }
                        if ($that.staffList.length === 0) {
                            vc.toast('该小区无物业员工或员工已全部绑定该规则');
                        }
                    }, function (errInfo, error) {
                        console.log('请求失败处理');
                    }
                );
            },
            _listEventStaffs: function () {
                var param = {
                    params: {
                        page: -1,
                        row: 100,
                        ruleId: $that.addEventStaffInfo.ruleId,
                        communityId: vc.getCurrentCommunity().communityId,
                    }
                };

                //发送get请求
                vc.http.apiGet('/eventStaff.listEventStaff',
                    param,
                    function (json, res) {
                        var _eventStaffInfo = JSON.parse(json);
                        $that.eventStaffList = _eventStaffInfo.data;
                        $that._listStaffList();

                    }, function (errInfo, error) {
                        console.log('请求失败处理');
                    }
                );
            },
        }

    });
})(window.vc);
