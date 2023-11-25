/**
    入驻小区
**/
(function (vc) {

    vc.extends({
        data: {
            sceneMenuInfo: {
                menus: [],
                subMenus: [],
                curMenuName: '',
            }
        },
        _initMethod: function () {
            let _menus = [
                {
                    name: '灯光',
                    active: false,
                    id: 1,
                }
            ];
            vc.getDict('dt_modal', 'modal_type', function (_data) {
                _data.forEach(_item => {
                    _menus.push({
                        name: _item.name,
                        active: false,
                        id: _item.statusCd,
                    })
                })
                $that.sceneMenuInfo.menus = _menus;
                $that.switchMenu($that.sceneMenuInfo.menus[0]);
            })
        },
        _initEvent: function () {


        },
        methods: {
            switchMenu: function (_menu) {
                let _menus = $that.sceneMenuInfo.menus;
                _menus.forEach(item => {
                    item.active = false;
                    if (_menu.id == item.id) {
                        item.active = true;
                    }
                });

                $that.sceneMenuInfo.curMenuName = _menu.name;
                let _subMenu = [];
                if (_menu.id == '1') {
                    _subMenu = [
                        {
                            subId: '1',
                            name: '环境光',
                            active: false,
                        }];
                    $that.sceneMenuInfo.subMenus = _subMenu;
                    return;
                }
                $that._load3dModal(_menu.id);

            },
            _switchSubMenu: function (subMenu) {
                let _subMenus = $that.sceneMenuInfo.subMenus;
                _subMenus.forEach(item => {
                    item.active = false;
                    if (item.subId == subMenu.subId) {
                        item.active = true;
                    }
                });
            },
            _load3dModal: function (_modalType) {
                let param = {
                    params: {
                        page: 1,
                        row: 100,
                        modalType:_modalType
                    }
                };
                let _subMenu = [];
                //发送get请求
                vc.http.apiGet('/dtModal.listDtModal',
                    param,
                    function (json, res) {
                        let _json = JSON.parse(json);

                        _json.data.forEach(_item => {
                            _subMenu.push({
                                subId: _item.modalId,
                                name: _item.modalName,
                                active: false,
                            });
                        })
                        $that.sceneMenuInfo.subMenus = _subMenu;
                    }, function (errInfo, error) {
                        console.log('请求失败处理');
                    }
                );
            }
        }
    });
})(window.vc);