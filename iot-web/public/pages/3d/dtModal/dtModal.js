/**
    入驻小区
**/

(function (vc) {
    var DEFAULT_PAGE = 1;
    var DEFAULT_ROWS = 10;
    vc.extends({
        data: {
            dtModalInfo: {
                dtModals: [],
                modalTypes: [],
                total: 0,
                records: 1,
                moreCondition: false,
                modalId: '',
                conditions: {
                    modalName: '',
                    modalType: '',
                    modalBean: '',

                }
            }
        },
        _initMethod: function () {
            $that._listDtModals(DEFAULT_PAGE, DEFAULT_ROWS);
            $that.dtModalInfo.modalTypes = [{
                name:'全部类型',
                statusCd:''
            }]
            vc.getDict('dt_modal', 'modal_type', function (_data) {
                _data.forEach(_item=>{
                    $that.dtModalInfo.modalTypes.push(_item);
                })
            })
        },
        _initEvent: function () {

            vc.on('dtModal', 'listDtModal', function (_param) {
                $that._listDtModals(DEFAULT_PAGE, DEFAULT_ROWS);
            });
            vc.on('pagination', 'page_event', function (_currentPage) {
                $that._listDtModals(_currentPage, DEFAULT_ROWS);
            });
        },
        methods: {
            _listDtModals: function (_page, _rows) {

                $that.dtModalInfo.conditions.page = _page;
                $that.dtModalInfo.conditions.row = _rows;
                let param = {
                    params: $that.dtModalInfo.conditions
                };

                //发送get请求
                vc.http.apiGet('/dtModal.listDtModal',
                    param,
                    function (json, res) {
                        let _json = JSON.parse(json);
                        $that.dtModalInfo.total = _json.total;
                        $that.dtModalInfo.records = _json.records;
                        $that.dtModalInfo.dtModals = _json.data;
                        vc.emit('pagination', 'init', {
                            total: $that.dtModalInfo.records,
                            currentPage: _page
                        });
                    }, function (errInfo, error) {
                        console.log('请求失败处理');
                    }
                );
            },
            _openAddDtModalModal: function () {
                vc.emit('addDtModal', 'openAddDtModalModal', {
                    modalType:$that.dtModalInfo.conditions.modalType
                });
            },
            _openEditDtModalModel: function (_dtModal) {
                vc.emit('editDtModal', 'openEditDtModalModal', _dtModal);
            },
            _openDeleteDtModalModel: function (_dtModal) {
                vc.emit('deleteDtModal', 'openDeleteDtModalModal', _dtModal);
            },
            _queryDtModalMethod: function () {
                $that._listDtModals(DEFAULT_PAGE, DEFAULT_ROWS);

            },
            _moreCondition: function () {
                if ($that.dtModalInfo.moreCondition) {
                    $that.dtModalInfo.moreCondition = false;
                } else {
                    $that.dtModalInfo.moreCondition = true;
                }
            },
            swatchModalType:function(_dict){
                $that.dtModalInfo.conditions.modalType = _dict.statusCd;
                $that._listDtModals(DEFAULT_PAGE, DEFAULT_ROWS);
            }


        }
    });
})(window.vc);
