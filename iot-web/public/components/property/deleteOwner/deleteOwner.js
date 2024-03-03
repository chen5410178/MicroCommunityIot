(function (vc) {
    vc.extends({
        propTypes: {
            notifyLoadDataComponentName: vc.propTypes.string
        },
        data: {
            deleteOwnerInfo: {}
        },
        _initEvent: function () {
            vc.on('deleteOwner', 'openOwnerModel', function (_ownerInfo) {
                $that.deleteOwnerInfo = _ownerInfo;
                $('#deleteOwnerModel').modal('show');
            });
        },
        methods: {
            closeDeleteOwnerModel: function () {
                $('#deleteOwnerModel').modal('hide');
            },
            deleteOwner: function () {
                $that.deleteOwnerInfo.communityId = vc.getCurrentCommunity().communityId;
                vc.http.apiPost(
                    '/owner.deleteOwner',
                    JSON.stringify($that.deleteOwnerInfo), {
                        emulateJSON: true
                    },
                    function (json, res) {
                        let _json = JSON.parse(json);
                        if (_json.code == 0) {
                            //关闭model
                            $('#deleteOwnerModel').modal('hide');
                            vc.emit($props.notifyLoadDataComponentName, 'listOwnerData', {});
                            vc.toast("删除成功");
                            return;
                        } else {
                            vc.toast(_json.msg);
                        }
                    },
                    function (errInfo, error) {
                        vc.toast(errInfo);
                        // $that.deleteOwnernfo.errorInfo = errInfo;
                    });
            }
        }
    });
})(window.vc);