(function(vc) {

    vc.extends({
        data: {
            editCityAreaInfo: {
                id:'',
                areaCode: '',
                areaName: '',
                areaLevel: '',
                parentAreaId: '',
                parentAreaName: '',
                parentAreaCode:'',
                lon: '',
                lat: '',

            },
            flagOrgName: false
        },
        _initMethod: function() {

        },
        _initEvent: function() {
            vc.on('editCityArea', 'openEditCityAreaModal', function(_param) {
                vc.copyObject(_param,$that.editCityAreaInfo);
                $that.loadEditCityArea();
                $('#editCityAreaModel').modal('show');
            });
        },
        methods: {
            editCityAreaValidate: function() {
                return vc.validate.validate({
                    editCityAreaInfo: $that.editCityAreaInfo
                }, {
                    'editCityAreaInfo.areaCode': [{
                            limit: "required",
                            param: "",
                            errInfo: "地区编码不能为空"
                        },
                    ],
                    'editCityAreaInfo.areaName': [{
                            limit: "required",
                            param: "",
                            errInfo: "地区名称不能为空"
                        },
                    ],
                    'editCityAreaInfo.parentAreaCode': [
                        {
                            limit: "maxLength",
                            param: "200",
                            errInfo: "上级编码不能为空"
                        },
                    ],
                });
            },
            _editCityArea: function() {
                if (!$that.editCityAreaValidate()) {
                    vc.toast(vc.validate.errInfo);
                    return;
                }

                vc.http.apiPost(
                    '/area.updateCityArea',
                    JSON.stringify($that.editCityAreaInfo), {
                        emulateJSON: true
                    },
                    function(json, res) {
                        //vm.menus = vm.refreshMenuActive(JSON.parse(json),0);
                        if (res.status == 200) {
                            //关闭model
                            $('#editCityAreaModel').modal('hide');
                            $that.clearEditCityAreaInfo();
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
            clearEditCityAreaInfo: function() {
                $that.editCityAreaInfo = {
                    id:'',
                    areaCode: '',
                    areaName: '',
                    areaLevel: '',
                    parentAreaId: '',
                    parentAreaCode:'',
                    parentAreaName: '',
                    lon: '',
                    lat: '',
                };
            },
            loadEditCityArea:function(){
                let param = {
                    params: {
                        id: $that.editCityAreaInfo.id,
                        page:1,
                        row:1
                    }
                };
                //发送get请求
                vc.http.apiGet('/area.listAreas',
                    param,
                    function (json) {
                        let _cityArea = JSON.parse(json).data;
                        vc.copyObject(_cityArea[0],$that.editCityAreaInfo);
                    },
                    function () {
                        console.log('请求失败处理');
                    });
            }
           
           
        }
    });

})(window.vc);