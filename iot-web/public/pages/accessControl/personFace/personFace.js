/**
    入驻小区
**/
(function (vc) {
    var DEFAULT_PAGE = 1;
    var DEFAULT_ROWS = 10;
    vc.extends({
        data: {
            personFaceInfo: {
                personFaces: [],
                states: [],
                total: 0,
                records: 1,
                moreCondition: false,
                pfId: '',
                conditions: {
                    roomName: '',
                    personName: '',
                    personTel: '',
                    ownerTypeCd: '',
                    state: '',
                }
            }
        },
        _initMethod: function () {
            $that._listPersonFaces(DEFAULT_PAGE, DEFAULT_ROWS);

            $that.personFaceInfo.states = [
                {
                    statusCd: '',
                    name: '全部'
                }
            ]
            vc.getDict('person_face', 'state', function (_data) {
                _data.forEach(item => {
                    $that.personFaceInfo.states.push(item);
                })
            })
        },
        _initEvent: function () {
            vc.on('personFace', 'listPersonFace', function (_param) {
                $that._listPersonFaces(DEFAULT_PAGE, DEFAULT_ROWS);
            });
            vc.on('pagination', 'page_event', function (_currentPage) {
                $that._listPersonFaces(_currentPage, DEFAULT_ROWS);
            });
        },
        methods: {
            _listPersonFaces: function (_page, _rows) {

                $that.personFaceInfo.conditions.page = _page;
                $that.personFaceInfo.conditions.row = _rows;
                $that.personFaceInfo.conditions.communityId = vc.getCurrentCommunity().communityId;

                let param = {
                    params: $that.personFaceInfo.conditions
                };

                //发送get请求
                vc.http.apiGet('/personFace.listPersonFace',
                    param,
                    function (json, res) {
                        let _json = JSON.parse(json);
                        $that.personFaceInfo.total = _json.total;
                        $that.personFaceInfo.records = _json.records;
                        $that.personFaceInfo.personFaces = _json.data;
                        vc.emit('pagination', 'init', {
                            total: $that.personFaceInfo.records,
                            currentPage: _page,
                            dataCount:_json.total
                        });
                    }, function (errInfo, error) {
                        console.log('请求失败处理');
                    }
                );
            },
            _openAuditPersonFaceModel: function (_personFace) {
                vc.emit('auditPersonFace', 'openAuditPersonFaceModal', _personFace);
            },
            _openDeletePersonFaceModel: function (_personFace) {
                vc.emit('deletePersonFace', 'openDeletePersonFaceModal', _personFace);
            },
            _queryPersonFaceMethod: function () {
                $that._listPersonFaces(DEFAULT_PAGE, DEFAULT_ROWS);
            },
            _switchState:function(_state){
                $that.personFaceInfo.conditions.state = _state.statusCd;
                $that._listPersonFaces(DEFAULT_PAGE, DEFAULT_ROWS);
            },
            _viewOwnerFace: function(_url) {
                vc.emit('viewImage', 'showImage', {
                    url: _url
                });
            },


        }
    });
})(window.vc);
