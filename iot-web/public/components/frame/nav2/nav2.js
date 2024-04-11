/**
 导航栏
 **/
import { parkingWebSocket } from "/api/websocket/parkingWebSocket.js";

(function (vc) {
    let DEFAULT_PAGE = 1;
    let DEFAULT_ROW = 10;
    var vm = new Vue({
        el: '#nav',
        data: {
            nav: {
                moreNoticeUrl: '/#/pages/common/noticeManage',
                langs: [{
                    name: '中文',
                    lang: 'zh-cn'
                }, {
                    name: '繁体',
                    lang: 'cn'
                }, {
                    name: 'english',
                    lang: 'en'
                }, {
                    name: 'བོད་སྐད།',
                    lang: 'tibetan'
                }],
                langName: '中文',
                _currentCommunity: '',
                communityInfos: [],
                storeTypeCd: '',
                catalogs: [],
            },
            logo: '',
            userName: "",
        },
        mounted: function () {
            this._initSysInfo();
            this.getNavCommunity(1, 3);
            this.initLang();
            this._getMenuCatalog();
            // 定义事件名为'build'.
          
        },
        methods: {
            _initSysInfo: function () {
                let sysInfo = vc.getData("java110SystemInfo");
                if (!sysInfo) {
                    this.logo = "HCIOT";
                    return;
                }
                this.logo = sysInfo.systemSimpleTitle;
            },
            initLang: function () {
                let _lang = vc.getData('JAVA110-LANG')
                if (!_lang) {
                    vc.saveData('JAVA110-LANG', {
                        name: '中文',
                        lang: 'zh-cn'
                    })
                } else {
                    this.nav.langName = _lang.name;
                }
            },
            _changeLang: function (_lang) {
                vc.saveData('JAVA110-LANG', _lang);
                location.reload();
            },
            logout: function () {
                let param = {
                    msg: 123
                };
                //发送get请求
                vc.http.apiPost('/login.userLogout',
                    JSON.stringify(param), {
                    emulateJSON: true
                },
                    function (json, res) {
                        if (res.status == 200) {
                            // 清除本地sessionStorage存储的tab
                            vc.clearTabToLocal();
                            vc.jumpToPage("/user.html#/pages/frame/login");
                            return;
                        }
                    },
                    function () {
                        console.log('请求失败处理');
                    }
                );
            },
            getUserInfo: function () {
                //获取用户名
                let param = {
                    msg: '123',
                };
                //发送get请求
                vc.http.apiGet('/user.getUserInfo',
                    param,
                    function (json, res) {
                        if (res.status == 200) {
                            var tmpUserInfo = JSON.parse(json);
                            vm.userName = tmpUserInfo.name;
                            vm.nav.storeTypeCd = tmpUserInfo.storeTypeCd;
                            //加个水印
                            if (tmpUserInfo.watermark == 'true') {
                                vc.watermark({ watermark_txt: vc.i18n('systemName') + ":" + tmpUserInfo.name });
                            }
                        }
                    },
                    function () {
                        console.log('请求失败处理');
                    }
                );
            },
            getNavCommunity: function (_page, _row) {
                var _tmpCurrentCommunity = vc.getCurrentCommunity();
                //浏览器缓存中能获取到
                if (_tmpCurrentCommunity != null && _tmpCurrentCommunity != undefined) {
                    this.nav._currentCommunity = _tmpCurrentCommunity;
                    this.nav.communityInfos = vc.getCommunitys();
                    return;
                }
                //说明缓存中没有数据
                //发送get请求
                /**
                 [{community:"123123",name:"测试1小区"},{community:"223123",name:"测试2小区"}]
                 **/
                let param = {
                    params: {
                        _uid: '123mlkdinkldldijdhuudjdjkkd',
                        page: _page,
                        row: _row
                    }
                };
                vc.http.get('nav',
                    'getCommunitys',
                    param,
                    function (json, res) {
                        if (res.status == 200) {
                            vm.nav.communityInfos = JSON.parse(json).communitys;
                            if (vm.nav.communityInfos == null || vm.nav.communityInfos.length == 0) {
                                vm.nav._currentCommunity = {
                                    name: "还没有入驻小区"
                                };
                                return;
                            }
                            vm.nav._currentCommunity = vm.nav.communityInfos[0];
                            vc.setCurrentCommunity(vm.nav._currentCommunity);
                            vc.setCommunitys(vm.nav.communityInfos);
                            //对首页做特殊处理，因为首页在加载数据时还没有小区信息 会报错
                            if (vm.nav.communityInfos != null && vm.nav.communityInfos.length > 0) {
                                vc.emit("indexContext", "_queryIndexContextData", {});
                                vc.emit("indexArrears", "_listArrearsData", {});
                            }
                        }
                    },
                    function () {
                        console.log('请求失败处理');
                    }
                );
            },
            changeCommunity: function (_community) {
                vc.setCurrentCommunity(_community);
                vm.nav._currentCommunity = _community;
                //中心加载当前页
                //location.reload();
                vc.jumpToPage("/");
            },
            _noticeDetail: function (_msg) {
                //标记为消息已读
                vc.http.post('nav',
                    'readMsg',
                    JSON.stringify(_msg),
                    function (json, res) {
                        if (res.status == 200) {
                            vc.jumpToPage(_msg.url);
                        }
                    },
                    function () {
                        console.log('请求失败处理');
                    }
                );
            },
            _getMenuCatalog: function () {
                let _param = {
                    params: {
                        page: 1,
                        row: 10,
                        isShow: 'Y',
                    }
                }
                vc.http.apiGet('/menu.listCatalog',
                    _param,
                    function (json, res) {
                        let _listCatalogs = JSON.parse(json);
                        if (_listCatalogs.code != 0) {
                            return;
                        }
                        vm.nav.catalogs = _listCatalogs.data;
                        vm._emitMsg(_listCatalogs.data[0])
                    },
                    function (e) {
                        console.log('请求失败处理', e);
                    }
                );
            },
            _emitMsg: function (_param) {
                let event = new CustomEvent('loadMenu', {
                    "detail": _param
                });
                document.body.dispatchEvent(event);
            },
            _doMenu: function () {
                let body = document.getElementsByTagName("body")[0];
                let className = body.className;
                if (className.indexOf("mini-navbar") != -1) {
                    body.className = className.replace(/mini-navbar/g, "");
                    return;
                }
                body.className = className + " mini-navbar";
            },
            _chooseMoreCommunity: function () {
                vc.emit('chooseEnterCommunity', 'openChooseEnterCommunityModel', {});
            },
            _viewDocument: function () {
                vc.emit('document', 'openDocument', {});
            },
            _settingActiveCatalog: function (_catalog) {
                this.mallCatalog.active = '0';
                let _catalogs = this.nav.catalogs;
                _catalogs.forEach(item => {
                    item.active = '0'
                    if (item.caId == _catalog.caId) {
                        item.active = '1';
                    }
                });
                if (_catalog.caId === '1')
                    _catalog.active = '1';
                //this.nav.catalogs = _catalogs;
                this.$forceUpdate();
            },
            _changeMenuCatalog: function (_catalog) {
                //_showModelDiv(item)
                if (_catalog.caId === '1') {
                    vc.jumpToPage('/#/pages/mall/product');
                }
                if (vm._showModelDiv(_catalog)) {
                    return;
                }
                if (_catalog.url != '#') {
                    
                    vc.jumpToPage(_catalog.url);
                    //return;
                }
                vm._emitMsg(_catalog);
            },
            _showModelDiv: function (_catalog) {
                if (_catalog.url.startsWith('?')) {
                    let _modelName = _catalog.url.substring(1, _catalog.url.length);
                    $('#' + _modelName).modal('show');
                    return true;
                }
                return false;
            },
            _toChainMall:function(){
                window.open('http://shop.songbingcloud.com/')
            }
        }
    });
    vm.getUserInfo();

    function newWebSocket() {
        //建立websocket 消息连接
        let user = vc.getData('/nav/getUserInfo');
        if (!user) {
            return;
        }
        let _userId = user.userId;
        let _url = "/ws/message/" + _userId;
        new parkingWebSocket(_url, function (_data) {
            console.log(_data);
        });
    }
    newWebSocket();
})(window.vc);