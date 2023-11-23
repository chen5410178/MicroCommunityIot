/**
 微信公众号
 **/
(function (vc) {
    var DEFAULT_PAGE = 1;
    var DEFAULT_ROWS = 1;
    vc.extends({
        data: {
            smallWeChatManageInfo: {
                smallWeChats: [],
                total: 0,
                records: 1,
                moreCondition: false,
                name: '',
                wetConfig: false,
                conditions: {
                    name: '',
                    appId: '',
                    weChatType: '1100'
                }
            }
        },
        _initMethod: function () {
            $that._listSmallWeChats(DEFAULT_PAGE, DEFAULT_ROWS);
        },
        _initEvent: function () {
            vc.on('smallWeChatManage', 'listSmallWeChat', function (_param) {
                $that._listSmallWeChats(DEFAULT_PAGE, DEFAULT_ROWS);
            });
        },
        methods: {
            _listSmallWeChats: function (_page, _rows) {
                $that.smallWeChatManageInfo.conditions.page = _page;
                $that.smallWeChatManageInfo.conditions.row = _rows;
                $that.smallWeChatManageInfo.conditions.communityId = vc.getCurrentCommunity().communityId;
                let param = {
                    params: $that.smallWeChatManageInfo.conditions
                };
                //发送get请求
                vc.http.apiGet('/smallWeChat.listSmallWeChats',
                    param,
                    function (json, res) {
                        let _json = JSON.parse(json);
                        $that.smallWeChatManageInfo.smallWeChats = _json.data;
                    },
                    function (errInfo, error) {
                        console.log('请求失败处理');
                    }
                );
            },
            _openAddSmallWeChatModal: function (type) {
                vc.emit('addSmallWeChat', 'openAddSmallWeChatModal', type);
            },
            _openEditSmallWeChatModel: function (_smallWeChat) {
                vc.emit('editSmallWeChat', 'openEditSmallWeChatModal', _smallWeChat);
            },
            _querySmallWeChatMethod: function () {
                $that._listSmallWeChats(DEFAULT_PAGE, DEFAULT_ROWS);
            },
            _switchWeChatType: function (type) {
                $that.smallWeChatManageInfo.conditions.weChatType = type;
                $that._listSmallWeChats(DEFAULT_PAGE, DEFAULT_ROWS);
            },
            _openWeChatMenu: function (_smallWeChat) {
                vc.jumpToPage('/#/pages/property/wechatMenuManage');
            },
            _openWeChatSmsTemplate: function (_smallWeChat) {
                vc.jumpToPage('/#/pages/property/wechatSmsTemplateManage?wechatId=' + _smallWeChat.wechatId);
            }
        }
    });
})(window.vc);