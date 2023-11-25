/**
 * 查询楼栋数据
 */
export function loadFloorData(_dataObj) {
    let param = {
        params:_dataObj
    }
    return new Promise((resove, reject) => {
        //发送get请求
        vc.http.apiGet('/floor.queryFloors',
        param,
            function (json, res) {
                let _json = JSON.parse(json);
                resove(_json.data);
            }, function (errInfo, error) {
                console.log('请求失败处理');
            }
        );
    })
}