(function(vc) {
    vc.extends({
        data: {
            loginInfo: {
                logo: '',
                username: '',
                passwd: '',
                validateCode: '',
                errorInfo: '',
                codeImage: '',
                systemTitle:'',
            }
        },
        _initMethod: function() {
            $that.clearCacheData();
            $that._loadSysInfo();
            $that.generateCode();

        },
        _initEvent: function() {
            $that.$on('errorInfoEvent', function(_errorInfo) {
                $that.loginInfo.errorInfo = _errorInfo;
                console.log('errorInfoEvent 事件被监听', _errorInfo)
            });

            $that.$on('validate_code_component_param_change_event', function(params) {
                for (var tmpAttr in params) {
                    $that.loginInfo[tmpAttr] = params[tmpAttr];
                }
                console.log('errorInfoEvent 事件被监听', params)
            });
            vc.on('login', 'doLogin', function() {
                $that.doLogin();
            })
        },
        methods: {
            clearCacheData: function() {
                vc.clearCacheData();
            },
            _loadSysInfo: function() {
                var param = {
                    params: {
                        sys: 'HC'
                    }
                }
                vc.http.apiGet(
                    'login.getSysInfo',
                    param,
                    function(json, res) {
                        //vm.menus = vm.refreshMenuActive(JSON.parse(json),0);
                        if (res.status != 200) {
                            console.log("加载系统信息失败");
                            vc.saveData("_sysInfo", { logo: 'HC' });
                            vc.copyObject(json, $that.loginInfo);
                            return;
                        }
                        vc.copyObject(JSON.parse(json), $that.loginInfo);
                        //保存到浏览器
                        vc.saveData("_sysInfo", JSON.parse(json));
                    },
                    function(errInfo, error) {
                        console.log('请求失败处理');
                        vc.saveData("_sysInfo", { logo: 'HC' });
                        vc.copyObject(json, $that.loginInfo);
                        $that.loginInfo.errorInfo = errInfo;
                    });
            },
            doLogin: function() {
                if (!vc.notNull($that.loginInfo.username)) {
                    vc.toast('用户名不能为空');
                    return;
                }
                if (!vc.notNull($that.loginInfo.passwd)) {
                    vc.toast('密码不能为空');
                    return;
                }
                if (!vc.notNull($that.loginInfo.validateCode)) {
                    vc.toast('验证码不能为空');
                    return;
                }
                vc.http.apiPost(
                    '/login.pcUserLogin',
                    JSON.stringify($that.loginInfo), {
                        emulateJSON: true
                    },
                    function(json, res) {
                        //vm.menus = vm.refreshMenuActive(JSON.parse(json),0);
                        let _data = JSON.parse(json);
                        if (_data.hasOwnProperty('code') && _data.code != '0') {
                            vc.toast(_data.msg);
                            return;
                        }
                        if (res.status == 200) {
                            vc.emit('initData', 'loadCommunityInfo', {
                                url: '/'
                            });
                            return;
                        }
                        vc.toast(json);
                        $that.loginInfo.errorInfo = json;
                    },
                    function(errInfo, error) {
                        console.log('请求失败处理');
                        vc.toast(errInfo);
                        $that.loginInfo.errorInfo = errInfo;
                    });

            },
            _doRegister: function() {
                vc.jumpToPage('/user.html#/pages/frame/register');
            },
            generateCode: function() {
                var param = {
                    params: {
                        _uId: '123'
                    }
                };
                vc.http.apiGet('/login.generatorValidateCode',
                    param,
                    function(json, res) {
                        //vm.menus = vm.refreshMenuActive(JSON.parse(json),0);
                        if (res.status == 200) {
                            $that.loginInfo.codeImage = json;
                            return;
                        }
                    },
                    function(errInfo, error) {
                        console.log('请求失败处理', errInfo, error);
                    });

            },
        },
        _destroyedMethod: function() {
            console.log("登录页面销毁调用");
        }
    });


})(window.vc);