/**
    入驻小区
**/
import Cockpit3d from 'api/3d/Cockpit3d.js'

(function(vc) {
    var DEFAULT_PAGE = 1;
    var DEFAULT_ROWS = 10;
    vc.extends({
        data: {
            accessControlFaceInfo: {
                accessControlFaces: [],
                total: 0,
                records: 1,
                moreCondition: false,
                mfId: '',
                accessControls: [],
                conditions: {
                    machineId: '',
                    personType: '',
                    name: '',
                    state: '',
                    comeType: '',
                    queryStartTime:'',
                    queryEndTime:''
                }
            }
        },
        _initMethod: function() {
            $that._listAccessControls();
            $that._listAccessControlFaces(DEFAULT_PAGE, DEFAULT_ROWS);
            vc.initDateTime('queryStartTime',function(_value){
                $that.accessControlFaceInfo.conditions.queryStartTime = _value;
            });
            vc.initDateTime('queryEndTime',function(_value){
                $that.accessControlFaceInfo.conditions.queryEndTime = _value;
            });
        },
        _initEvent: function() {

            vc.on('accessControlFace', 'listAccessControlFace', function(_param) {
                $that._listAccessControlFaces(DEFAULT_PAGE, DEFAULT_ROWS);
            });
            vc.on('pagination', 'page_event', function(_currentPage) {
                $that._listAccessControlFaces(_currentPage, DEFAULT_ROWS);
            });
        },
        methods: {
            _listAccessControlFaces: function(_page, _rows) {

                $that.accessControlFaceInfo.conditions.page = _page;
                $that.accessControlFaceInfo.conditions.row = _rows;
                $that.accessControlFaceInfo.conditions.communityId = vc.getCurrentCommunity().communityId;
                var param = {
                    params: $that.accessControlFaceInfo.conditions
                };

                //发送get请求
                vc.http.apiGet('/accessControlFace.listAccessControlFace',
                    param,
                    function(json, res) {
                        var _json = JSON.parse(json);
                        $that.accessControlFaceInfo.total = _json.total;
                        $that.accessControlFaceInfo.records = _json.records;
                        $that.accessControlFaceInfo.accessControlFaces = _json.data;
                        vc.emit('pagination', 'init', {
                            total: $that.accessControlFaceInfo.records,
                            currentPage: _page,
                            dataCount:_json.total
                        });
                    },
                    function(errInfo, error) {
                        console.log('请求失败处理');
                    }
                );
            },
            _openDeleteAccessControlFaceModel: function(_accessControlFace) {
                vc.emit('deleteAccessControlFace', 'openDeleteAccessControlFaceModal', _accessControlFace);
            },
            _queryAccessControlFaceMethod: function() {
                $that._listAccessControlFaces(DEFAULT_PAGE, DEFAULT_ROWS);
            },
            swatchAccessControl: function(_accessControl) {
                $that.accessControlFaceInfo.conditions.machineId = _accessControl.machineId;
                $that._listAccessControlFaces(DEFAULT_PAGE, DEFAULT_ROWS);
            },
            _moreCondition: function() {
                if ($that.accessControlFaceInfo.moreCondition) {
                    $that.accessControlFaceInfo.moreCondition = false;
                } else {
                    $that.accessControlFaceInfo.moreCondition = true;
                }
            },
            _listAccessControls: function(_page, _rows) {

                let param = {
                    params: {
                        page: 1,
                        row: 100,
                        communityId: vc.getCurrentCommunity().communityId
                    }
                };
                $that.accessControlFaceInfo.accessControls = [{
                    machineId: '',
                    machineName: '全部人脸'
                }]

                //发送get请求
                vc.http.apiGet('/accessControl.listAccessControl',
                    param,
                    function(json, res) {
                        let _accessControlManageInfo = JSON.parse(json);
                        _accessControlManageInfo.data.forEach(item => {
                            $that.accessControlFaceInfo.accessControls.push(item);
                        })
                    },
                    function(errInfo, error) {
                        console.log('请求失败处理');
                    }
                );
            },
            _viewOwnerFace: function(_url) {
                vc.emit('viewImage', 'showImage', {
                    url: _url
                });
            },
            _openAccessControlPerson: function() {
                vc.jumpToPage('/#/pages/accessControl/accessControlPerson')
            }
        }
    });
})(window.vc);