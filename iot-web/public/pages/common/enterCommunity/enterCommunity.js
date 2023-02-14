/**
    入驻小区
**/
(function(vc) {
    vc.extends({
        data: {
            communityInfo: {
                total: 0,
                records: 1,
                enterCommunityInfo: [],
            }
        },
        _initMethod: function() {
            vc.component.listMyCommunity(1, 10);
        },
        _initEvent: function() {
            vc.on('enterCommunity', 'listMyCommunity', function(_param) {
                vc.component.listMyCommunity(1, 10);
            });

            vc.on('pagination', 'page_event', function(_currentPage) {
                vc.component.listMyCommunity(_currentPage, 10);
            });
        },
        methods: {
            listMyCommunity: function(_page, _row) {
                var param = {
                        params: {
                            msg: this.message,
                            _sb: '123',
                            page: _page,
                            row: _row
                        }

                    }
                    //发送get请求
                vc.http.apiGet('/community.listMyEnteredCommunitys',
                    param,
                    function(json, res) {
                        let _json = JSON.parse(json);
                        $that.communityInfo.total = _json.total;
                        $that.communityInfo.records = _json.records;
                        $that.communityInfo.enterCommunityInfo = _json.data;

                        vc.emit('pagination', 'init', {
                            total: _json.records,
                            dataCount: _json.total,
                            currentPage: _page
                        });
                    },
                    function(errInfo, error) {
                        console.log('请求失败处理');
                    }
                );
            },
            _openEnterCommunityModal: function() {
                vc.emit('storeEnterCommunity', 'openStoreEnterCommunity', {});
            },
            _openExitCommunityModel: function(_community) {
                vc.emit('storeExitCommunity', 'openStoreExitCommunityModal', _community);
            }
        }
    });
})(window.vc);