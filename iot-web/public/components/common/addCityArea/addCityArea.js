(function(vc) {

    vc.extends({
        data: {
            addCityAreaInfo: {
                areaCode: '',
                areaName: '',
                areaLevel: '',
                parentAreaId: '',
                parentAreaName: '',
                lon: '',
                lat: '',

            },
            flagOrgName: false
        },
        _initMethod: function() {

        },
        _initEvent: function() {
            vc.on('addCityArea', 'openAddCityAreaModal', function(_param) {
                if (_param.hasOwnProperty('parentAreaCode')) {
                    $that.addCityAreaInfo.parentAreaCode = _param.parentAreaCode;
                    $that.addCityAreaInfo.areaLevel = _param.areaLevel;
                    $that.addCityAreaInfo.parentAreaName = _param.parentAreaName;
                }
                $('#addCityAreaModel').modal('show');
            });
        },
        methods: {
            addCityAreaValidate: function() {
                return vc.validate.validate({
                    addCityAreaInfo: $that.addCityAreaInfo
                }, {
                    'addCityAreaInfo.areaCode': [{
                            limit: "required",
                            param: "",
                            errInfo: "地区编码不能为空"
                        },
                    ],
                    'addCityAreaInfo.areaName': [{
                            limit: "required",
                            param: "",
                            errInfo: "地区名称不能为空"
                        },
                    ],
                    'addCityAreaInfo.parentAreaCode': [

                        {
                            limit: "maxLength",
                            param: "200",
                            errInfo: "上级编码不能为空"
                        },
                    ],
                });
            },
            saveOrgInfo: function() {
                if (!$that.addCityAreaValidate()) {
                    vc.toast(vc.validate.errInfo);
                    return;
                }

                vc.http.apiPost(
                    '/area.saveCityArea',
                    JSON.stringify($that.addCityAreaInfo), {
                        emulateJSON: true
                    },
                    function(json, res) {
                        //vm.menus = vm.refreshMenuActive(JSON.parse(json),0);
                        if (res.status == 200) {
                            //关闭model
                            $('#addCityAreaModel').modal('hide');
                            $that.clearAddCityAreaInfo();
                            vc.emit('cityAreaTree', 'refreshTree', {});

                            return;
                        }
                        vc.toast(json);

                    },
                    function(errInfo, error) {
                        console.log('请求失败处理');

                        vc.toast(errInfo);

                    });
            },
            clearAddCityAreaInfo: function() {
                $that.addCityAreaInfo = {
                    areaCode: '',
                    areaName: '',
                    areaLevel: '',
                    parentAreaId: '',
                    parentAreaName: '',
                    lon: '',
                    lat: '',
                };
            },
           
           
        }
    });

})(window.vc);