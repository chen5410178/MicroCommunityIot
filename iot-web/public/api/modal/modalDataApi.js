/**
 * 查询模型数据
 * @param {模型} _modal 
 */
export function loadModalData(_modal, _modalData) {

    let _modalType = _modal.modalType;

    //楼栋
    if (_modalType == 'floor') {
        loadCommunityData({
            communityId: _modal.communityId,
            page: 1,
            row: 100
        }, '/floor.queryFloors').then(_data => {
            _data.forEach(item => {
                _modalData.push({
                    name: item.floorName,
                    dataId: item.floorId,
                    modalType: 'floor',
                    path: $that.sceneModalInfo.modal.path
                })
            })
        })
    } else if (_modalType == 'light') { //灯光
        _modalData.push({
            name: '环境光',
            dataId: '1',
            modalType: 'light',
            path: 'AmbientLight'
        })
    }else if (_modalType == 'template') { //灯光
        _modalData.push({
            name: $that.sceneModalInfo.modal.name,
            dataId: '1',
            modalType: 'template',
            path: $that.sceneModalInfo.modal.path
        })
    } else if (_modalType == 'car') { //车辆
        _modalData.push({
            name: '车辆',
            dataId: '2',
            modalType: 'car',
            path: $that.sceneModalInfo.modal.path
        })
    } else if (_modalType == 'person') { // 人
        _modalData.push({
            name: '人',
            dataId: '2',
            modalType: 'person',
            path: $that.sceneModalInfo.modal.path
        })
    }else if (_modalType == 'access_control') { // 门禁
        loadCommunityData({
            communityId: _modal.communityId,
            page: 1,
            row: 100
        }, '/accessControl.listAccessControl').then(_data => {
            _data.forEach(item => {
                _modalData.push({
                    name: item.machineName,
                    dataId: item.machineId,
                    modalType: 'access_control',
                    path: $that.sceneModalInfo.modal.path
                })
            })
        })
    }else if(_modalType == "barrier"){
        loadCommunityData({
            communityId: _modal.communityId,
            page: 1,
            row: 100
        }, '/parkingBox.listParkingBox').then(_data => {
            _data.forEach(item => {
                _modalData.push({
                    name: item.boxName,
                    dataId: item.boxId,
                    modalType: 'barrier',
                    path: $that.sceneModalInfo.modal.path
                })
            })
        })
    }else if(_modalType == "charge_machine"){
        loadCommunityData({
            communityId: _modal.communityId,
            page: 1,
            row: 100
        }, '/chargeMachine.listChargeMachine').then(_data => {
            _data.forEach(item => {
                _modalData.push({
                    name: item.machineName,
                    dataId: item.machineId,
                    modalType: 'charge_machine',
                    path: $that.sceneModalInfo.modal.path
                })
            })
        })
    }else if(_modalType == "meter"){
        loadCommunityData({
            communityId: _modal.communityId,
            page: 1,
            row: 100
        }, '/meterMachine.listMeterMachine').then(_data => {
            _data.forEach(item => {
                _modalData.push({
                    name: item.machineName,
                    dataId: item.machineId,
                    modalType: 'meter',
                    path: $that.sceneModalInfo.modal.path
                })
            })
        })
    }else if(_modalType == "lift"){
        loadCommunityData({
            communityId: _modal.communityId,
            page: 1,
            row: 100
        }, '/liftMachine.listLiftMachine').then(_data => {
            _data.forEach(item => {
                _modalData.push({
                    name: item.machineName,
                    dataId: item.machineId,
                    modalType: 'lift',
                    path: $that.sceneModalInfo.modal.path
                })
            })
        })
    }else if(_modalType == "lamp"){
        loadCommunityData({
            communityId: _modal.communityId,
            page: 1,
            row: 100
        }, '/lampMachine.listLampMachine').then(_data => {
            _data.forEach(item => {
                _modalData.push({
                    name: item.machineName,
                    dataId: item.machineId,
                    modalType: 'lamp',
                    path: $that.sceneModalInfo.modal.path
                })
            })
        })
    }else if(_modalType == "monitor"){
        loadCommunityData({
            communityId: _modal.communityId,
            page: 1,
            row: 100
        }, '/monitorMachine.listMonitorMachine').then(_data => {
            _data.forEach(item => {
                _modalData.push({
                    name: item.machineName,
                    dataId: item.machineId,
                    modalType: 'monitor',
                    path: $that.sceneModalInfo.modal.path
                })
            })
        })
    }else if(_modalType == "lock"){
        loadCommunityData({
            communityId: _modal.communityId,
            flag:'1',
            page: 1,
            row: 100
        }, '/lockMachine.listLockMachine').then(_data => {
            _data.forEach(item => {
                _modalData.push({
                    name: item.machineName,
                    dataId: item.machineId,
                    modalType: 'lock',
                    path: $that.sceneModalInfo.modal.path
                })
            })
        })
    }else if(_modalType == "instrument"){
        loadCommunityData({
            communityId: _modal.communityId,
            page: 1,
            row: 100
        }, '/instrument.listInstrument').then(_data => {
            _data.forEach(item => {
                _modalData.push({
                    name: item.machineName,
                    dataId: item.machineId,
                    modalType: 'instrument',
                    path: $that.sceneModalInfo.modal.path
                })
            })
        })
    }


}

/**
 * 查询楼栋数据
 */
export function loadCommunityData(_dataObj, _url) {
    let param = {
        params: _dataObj
    }
    return new Promise((resove, reject) => {
        //发送get请求
        vc.http.apiGet(_url,
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