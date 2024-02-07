(function (vc) {
    vc.extends({
        data: {
            deletePrivilegeGroupInfo: {}
        },
        _initEvent: function () {
            $that.$on('deletePrivilegeGroup_openDeletePrivilegeGroupModel', function (_pGroup) {
                $that.deletePrivilegeGroupInfo = _pGroup;
                $('#deletePrivilegeGroupModel').modal('show');
            });
        },
        methods: {
            closeDeletePrivilegeGroupModel: function () {
                $('#deletePrivilegeGroupModel').modal('hide');
            },
            deletePrivilegeGroup: function () {
                console.log("开始删除权限组：", $that.deletePrivilegeGroupInfo);
                vc.http.apiPost(
                    '/privilegeGroup.deletePrivilegeGroup',
                    JSON.stringify($that.deletePrivilegeGroupInfo),
                    {
                        emulateJSON: true
                    },
                    function (json, res) {
                        let _json = JSON.parse(json);
                        if (_json.code == 0) {
                            //关闭model
                            $('#deletePrivilegeGroupModel').modal('hide');
                            $that.$emit('privilegeGroup_loadPrivilegeGroup', {});
                            vc.emit('roleDiv', '_loadRole', {})
                            vc.toast("删除成功");
                            return;
                        } else {
                            vc.toast(_json.msg);
                        }
                        $that.deletePrivilegeGroupInfo.errorInfo = json;
                    },
                    function (errInfo, error) {
                        console.log('请求失败处理');
                        $that.deletePrivilegeGroupInfo.errorInfo = errInfo;
                    }
                );
            }
        }
    });
})(window.vc);