(function (vc) {

    vc.extends({
        propTypes: {
            callBackListener: vc.propTypes.string, //父组件名称
            callBackFunction: vc.propTypes.string //父组件监听方法
        },
        data: {
            addDtModalInfo: {
                modalTypes: [],
                modalId: '',
                modalName: '',
                modalType: '',
                modalBean: '',
                path: '',
            }
        },
        _initMethod: function () {
            vc.getDict('dt_modal', 'modal_type', function (_data) {
                $that.addDtModalInfo.modalTypes = _data;
            })
        },
        _initEvent: function () {
            vc.on('addDtModal', 'openAddDtModalModal', function (_data) {
                $that.addDtModalInfo.modalType = _data.modalType;
                $('#addDtModalModel').modal('show');
            });
            vc.on('addDtModal', 'notifyModal', function (_param) {
                $that.addDtModalInfo.path = _param.realFileName;
            })
        },
        methods: {
            addDtModalValidate() {
                return vc.validate.validate({
                    addDtModalInfo: $that.addDtModalInfo
                }, {
                    'addDtModalInfo.modalName': [
                        {
                            limit: "required",
                            param: "",
                            errInfo: "模型名称不能为空"
                        },
                        {
                            limit: "maxLength",
                            param: "200",
                            errInfo: "模型名称不能超过200"
                        },
                    ],
                    'addDtModalInfo.modalType': [
                        {
                            limit: "required",
                            param: "",
                            errInfo: "模型类型不能为空"
                        },
                        {
                            limit: "maxLength",
                            param: "30",
                            errInfo: "模型类型不能超过30"
                        },
                    ],
                    'addDtModalInfo.modalBean': [
                        {
                            limit: "required",
                            param: "",
                            errInfo: "模型实现不能为空"
                        },
                        {
                            limit: "maxLength",
                            param: "64",
                            errInfo: "模型实现不能超过64"
                        },
                    ],
                    'addDtModalInfo.path': [
                        {
                            limit: "required",
                            param: "",
                            errInfo: "模型存放地址不能为空"
                        },
                        {
                            limit: "maxLength",
                            param: "512",
                            errInfo: "模型存放地址不能超过512"
                        },
                    ],
                });
            },
            saveDtModalInfo: function () {
                if (!$that.addDtModalValidate()) {
                    vc.toast(vc.validate.errInfo);

                    return;
                }

                vc.http.apiPost(
                    '/dtModal.saveDtModal',
                    JSON.stringify($that.addDtModalInfo),
                    {
                        emulateJSON: true
                    },
                    function (json, res) {
                        //vm.menus = vm.refreshMenuActive(JSON.parse(json),0);
                        let _json = JSON.parse(json);
                        if (_json.code == 0) {
                            //关闭model
                            $('#addDtModalModel').modal('hide');
                            $that.clearAddDtModalInfo();
                            vc.emit('dtModal', 'listDtModal', {});

                            return;
                        }
                        vc.toast(_json.msg);
                    },
                    function (errInfo, error) {
                        console.log('请求失败处理');
                        vc.toast(errInfo);
                    });
            },
            clearAddDtModalInfo: function () {
                let _modalTypes = $that.addDtModalInfo.modalTypes;
                $that.addDtModalInfo = {
                    modalTypes: _modalTypes,
                    modalName: '',
                    modalType: '',
                    modalBean: '',
                    path: '',
                };
                vc.emit('addDtModal', 'uploadFile', 'clearVedio', {})
            }
        }
    });

})(window.vc);
