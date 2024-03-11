(function (vc) {
    vc.extends({
        data: {
            addCommunityInfo: {
                name: '',
                address: '',
                tmpAddress: '',
                areaAddress: '',
                nearbyLandmarks: '无',
                tel: '',
                cityCode: '',
                mapX: '101.33',
                mapY: '101.33',
                attrs: [],
                payFeeMonth: 12,
                feePrice: 0,
                extCommunityId: '',
                lat: '',
                lng: ''
            },
            areas: [],
            provs: [],
            citys: [],
            selectProv: '',
            selectCity: '',
            selectArea: '',
            allCity: []
        },
        _initMethod: function () {
            $that._initArea('101', '0');
        },
        _initEvent: function () {
            vc.on('addCommunity', 'openAddCommunityModal', function () {
                $that._loadCommunityAttrSpec();
                $('#addCommunityModel').modal('show');
                setTimeout(function () {
                    vc.emit('addMap', 'showMap', $that.addCommunityInfo);
                }, 1000)
            });
        },
        methods: {
            addCommunityValidate() {
                return vc.validate.validate({
                    addCommunityInfo: $that.addCommunityInfo
                }, {
                    'addCommunityInfo.name': [{
                        limit: "required",
                        param: "",
                        errInfo: "小区名称不能为空"
                    },
                    {
                        limit: "maxin",
                        param: "1,20",
                        errInfo: "小区名称必须在1至20字符之间"
                    },
                    ],
                    'addCommunityInfo.address': [{
                        limit: "required",
                        param: "",
                        errInfo: "小区地址不能为空"
                    },
                    {
                        limit: "maxLength",
                        param: "200",
                        errInfo: "小区地址不能大于200个字符"
                    },
                    ],
                    'addCommunityInfo.nearbyLandmarks': [{
                        limit: "required",
                        param: "",
                        errInfo: "附近地标不能为空"
                    },
                    {
                        limit: "maxLength",
                        param: "50",
                        errInfo: "小区附近地标不能大于50个字符"
                    },
                    ],
                    'addCommunityInfo.cityCode': [{
                        limit: "required",
                        param: "",
                        errInfo: "小区城市编码不能为空"
                    },
                    {
                        limit: "maxLength",
                        param: "12",
                        errInfo: "小区城市编码不能大于4个字符"
                    },
                    ],
                    'addCommunityInfo.mapX': [{
                        limit: "maxLength",
                        param: "20",
                        errInfo: "小区城市编码不能大于4个字符"
                    },],
                    'addCommunityInfo.mapY': [{
                        limit: "maxLength",
                        param: "20",
                        errInfo: "小区城市编码不能大于4个字符"
                    },],
                    'addCommunityInfo.tel': [{
                        limit: "required",
                        param: "",
                        errInfo: "联系方式不能为空"
                    }],
                });
            },
            saveCommunityInfo: function () {
                //$that.addCommunityInfo.communityId = vc.getCurrentCommunity().communityId;
                $that.addCommunityInfo.address = $that.addCommunityInfo.areaAddress + $that.addCommunityInfo.tmpAddress;
                if (!$that.addCommunityValidate()) {
                    vc.toast(vc.validate.errInfo);
                    return;
                }
                vc.http.apiPost(
                    '/community.saveCommunity',
                    JSON.stringify($that.addCommunityInfo), {
                    emulateJSON: true
                },
                    function (json, res) {
                        //vm.menus = vm.refreshMenuActive(JSON.parse(json),0);
                        let _json = JSON.parse(json);
                        if (_json.code == 0) {
                            //关闭model
                            $('#addCommunityModel').modal('hide');
                            $that.clearAddCommunityInfo();
                            vc.emit('communityManage', 'listCommunity', {});
                            return;
                        }
                        vc.toast(_json.msg);
                    },
                    function (errInfo, error) {
                        console.log('请求失败处理');
                        vc.toast(errInfo);
                    });
            },
            clearAddCommunityInfo: function () {
                //let _attrs = $that.addCommunityInfo.attrs;
                $that.addCommunityInfo = {
                    name: '',
                    address: '',
                    tmpAddress: '',
                    areaAddress: '',
                    nearbyLandmarks: '无',
                    cityCode: '',
                    mapX: '101.33',
                    mapY: '101.33',
                    attrs: [],
                    payFeeMonth: 12,
                    feePrice: 0,
                    extCommunityId: '',
                    lat: '',
                    lng: ''
                };
                $that.selectProv = '';
                $that.selectCity = '';
                $that.selectArea = '';
                $that.allCity = [];
            },
            getProv: function (_prov) {
                $that._initArea('202', _prov);
            },
            getCity: function (_city) {
                $that._initArea('303', _city);
            },
            getArea: function (_area) {
                $that.addCommunityInfo.cityCode = _area;
                $that.addCommunityInfo.areaAddress = '';
                if ($that.provs == null || $that.provs == undefined) {
                    return;
                }
                $that.provs.forEach(function (_param) {
                    if (_param.areaCode == $that.selectProv) {
                        $that.addCommunityInfo.areaAddress = _param.areaName;
                    }
                });
                $that.citys.forEach(function (_param) {
                    if (_param.areaCode == $that.selectCity) {
                        $that.addCommunityInfo.areaAddress += _param.areaName;
                    }
                });
                $that.areas.forEach(function (_param) {
                    if (_param.areaCode == $that.selectArea) {
                        $that.addCommunityInfo.areaAddress += _param.areaName;
                    }
                });
            },
            _initArea: function (_areaLevel, _parentAreaCode) { //加载区域
                var _param = {
                    params: {
                        areaLevel: _areaLevel,
                        parentAreaCode: _parentAreaCode
                    }
                };
                vc.http.apiGet('/area.listAreas',
                    _param,
                    function (json, res) {
                        if (res.status == 200) {
                            var _tmpAreas = JSON.parse(json).data;
                            if (_areaLevel == '101') {
                                $that.provs = _tmpAreas;
                            } else if (_areaLevel == '202') {
                                $that.citys = _tmpAreas;
                            } else {
                                $that.areas = _tmpAreas;
                            }
                            return;
                        }
                        //$that.$emit('errorInfoEvent',json);
                    },
                    function (errInfo, error) {
                        console.log('请求失败处理', errInfo, error);
                        vc.toast("查询地区失败");
                    });
            },
            _loadCommunityAttrSpec: function () {
                $that.addCommunityInfo.attrs = [];
                vc.getAttrSpec('building_community_attr', function (data) {
                    data.forEach(item => {
                        item.value = '';
                        if (item.specShow == 'Y') {
                            item.values = [];
                            $that._loadAttrValue(item.specCd, item.values);
                            $that.addCommunityInfo.attrs.push(item);
                        }
                    });
                });
            },
            _loadAttrValue: function (_specCd, _values) {
                vc.getAttrValue(_specCd, function (data) {
                    data.forEach(item => {
                        if (item.valueShow == 'Y') {
                            _values.push(item);
                        }
                    });
                });
            },
        }
    });
})(window.vc);