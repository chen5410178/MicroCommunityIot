/**
业主详情页面
 **/
(function (vc) {
    vc.extends({
        data: {
            cityAreaInfo: {
                id: '',
                areaCode: '',
                areaName: '',
                areaLevel:'',
                parentAreaCode: '',
                parentAreaName: '',
            }
        },
        _initMethod: function () {

        },
        _initEvent: function () {
            vc.on('cityArea','switchCityAreaTree',function(_param){
                $that.cityAreaInfo.id = _param.id;
                $that.cityAreaInfo.areaCode = _param.areaCode;
                $that.cityAreaInfo.areaName = _param.areaName;
                $that._listCityArea();
            });
        },
        methods: {
            _listCityArea: function () {

                let param = {
                    params: {
                        page:1,
                        row:1,
                        areaCode:$that.cityAreaInfo.areaCode,
                        id:$that.cityAreaInfo.id,
                    }
                };

                //发送get请求
                vc.http.apiGet('/area.listAreas',
                    param,
                    function (json, res) {
                        let _json = JSON.parse(json);
                        vc.copyObject(_json.data[0],$that.cityAreaInfo);
                    }, function (errInfo, error) {
                        console.log('请求失败处理');
                    }
                );
            },
           
        }
    });
})(window.vc);