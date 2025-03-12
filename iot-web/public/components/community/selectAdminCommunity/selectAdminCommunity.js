/**
 入驻小区
 **/
(function (vc) {
    vc.extends({
        data: {
            selectAdminCommunityInfo: {
                communitys: [],
                communityId: '',
            }
        },
        _initMethod: function () {
            $that._loadAdminCommunitys();
        },
        _initEvent: function () {

        },
        methods: {
            _loadAdminCommunitys: function () {
                let param = {
                    params: {
                        _uid: '123mlkdinkldldijdhuudjdjkkd',
                        page: 1,
                        row: 100,
                    }
                };
                let _communistys = [{
                    name: '全部小区',
                    communityId: ''
                }];
                $that.selectAdminCommunityInfo.communitys = _communistys;
                vc.http.apiGet('/community.listCommunitys',
                    param,
                    function (json, res) {
                        if (res.status == 200) {
                            let _json = JSON.parse(json);
                            _json.data.forEach(c => {
                                _communistys.push(c);
                            });
                            $that.selectAdminCommunityInfo.communitys = _communistys;
                            $that._swatchAdminCommunity(_communistys[0]);
                        }
                    }, function () {
                        console.log('请求失败处理');
                    }
                );
            },
            _swatchAdminCommunity: function (_community) {
                $that.selectAdminCommunityInfo.communityId = _community.communityId;
                //todo 切换小区
                vc.emit('selectAdminCommunity', 'changeCommunity', _community);
            }
        }
    });
})(window.vc);