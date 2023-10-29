(function (vc) {
    vc.extends({
        data: {
            addCommunityShopInfo: {
                shopName: '',
                link: '',
                password: '',
                communityId: vc.getCurrentCommunity().communityId,
            }
        },
        _initMethod: function () {
        },
        _initEvent: function () {
            vc.on('addCommunityShop', 'openAddCommunityShopModal', function () {
                $('#addCommunityShopModel').modal('show');
            });
        },
        methods: {
            addCommunityShopValidate() {
                return vc.validate.validate({
                    addCommunityShopInfo: $that.addCommunityShopInfo
                }, {
                    'addCommunityShopInfo.shopName': [
                        {
                            limit: "required",
                            param: "",
                            errInfo: "名称不能为空"
                        },
                        {
                            limit: "maxLength",
                            param: "64",
                            errInfo: "名称不能超过64"
                        }
                    ],
                    'addCommunityShopInfo.link': [
                        {
                            limit: "required",
                            param: "",
                            errInfo: "手机号不能为空"
                        },
                        {
                            limit: "maxLength",
                            param: "11",
                            errInfo: "手机号不能超过12"
                        }
                    ],
                    'addCommunityShopInfo.password': [
                        {
                            limit: "required",
                            param: "",
                            errInfo: "密码不能为空"
                        }
                    ],
                });
            },
            saveCommunityShopInfo: function () {
                if (!$that.addCommunityShopValidate()) {
                    vc.toast(vc.validate.errInfo);
                    return;
                }
                vc.http.apiPost(
                    '/store.propertySaveStoreAndShop',
                    JSON.stringify($that.addCommunityShopInfo), {
                        emulateJSON: true
                    },
                    function (json, res) {
                        //vm.menus = vm.refreshMenuActive(JSON.parse(json),0);
                        let _json = JSON.parse(json);
                        if (_json.code == 0) {
                            //关闭model
                            $('#addCommunityShopModel').modal('hide');
                            $that.clearAddCommunityShopInfo();
                            vc.emit('communityShop', 'listCommunityShop', {});
                            vc.toast("添加成功");
                            return;
                        } else {
                            vc.toast(_json.msg);
                        }
                    },
                    function (errInfo, error) {
                        console.log('请求失败处理');
                        vc.toast(errInfo);
                    });
            },
            clearAddCommunityShopInfo: function () {
                $that.addCommunityShopInfo = {
                    shopName: '',
                    link: '',
                    password: '',
                    communityId: vc.getCurrentCommunity().communityId,
                };
            }
        }
    });
})(window.vc);