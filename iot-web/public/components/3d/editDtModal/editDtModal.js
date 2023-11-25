(function (vc, vm) {

    vc.extends({
        data: {
            editDtModalInfo: {
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
                $that.editDtModalInfo.modalTypes = _data;
            })
        },
        _initEvent: function () {
            vc.on('editDtModal', 'openEditDtModalModal', function (_params) {
                $that.refreshEditDtModalInfo();
                $('#editDtModalModel').modal('show');
                vc.copyObject(_params, $that.editDtModalInfo);
                if ($that.editDtModalInfo.path) {
                    vc.emit('editDtModal', 'uploadFile', 'notifyVedio', $that.editDtModalInfo.path)
                }
            });
            vc.on('editDtModal', 'notifyModal', function (_param) {
                $that.editDtModalInfo.path = _param.realFileName;
            })
        },
        methods: {
            editDtModalValidate: function () {
                return vc.validate.validate({
                    editDtModalInfo: $that.editDtModalInfo
                }, {
                    'editDtModalInfo.modalName': [
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
                    'editDtModalInfo.modalType': [
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
                    'editDtModalInfo.modalBean': [
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
                    'editDtModalInfo.path': [
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
                    'editDtModalInfo.modalId': [
                        {
                            limit: "required",
                            param: "",
                            errInfo: "编号不能为空"
                        }]

                });
            },
            editDtModal: function () {
                if (!$that.editDtModalValidate()) {
                    vc.toast(vc.validate.errInfo);
                    return;
                }

                vc.http.apiPost(
                    '/dtModal.updateDtModal',
                    JSON.stringify($that.editDtModalInfo),
                    {
                        emulateJSON: true
                    },
                    function (json, res) {
                        //vm.menus = vm.refreshMenuActive(JSON.parse(json),0);
                        let _json = JSON.parse(json);
                        if (_json.code == 0) {
                            //关闭model
                            $('#editDtModalModel').modal('hide');
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
            refreshEditDtModalInfo: function () {
                let _modalTypes = $that.editDtModalInfo.modalTypes;

                $that.editDtModalInfo = {
                    modalTypes: _modalTypes,
                    modalId: '',
                    modalName: '',
                    modalType: '',
                    modalBean: '',
                    path: '',

                }
                vc.emit('editDtModal', 'uploadFile', 'clearVedio', {});
            }
        }
    });

})(window.vc, window.$that);
