(function (vc) {
    var DEFAULT_PAGE = 1;
    var DEFAULT_ROWS = 10;
    vc.extends({
        data: {
            productInfo: {
                products: [],
                records: '',
                prodName: '',
                productCategorys: [],
            },
            productCategorys: [],
            conditions: {
                prodName: '',
                categoryId: '',
            }
        },
        _initMethod: function () {
            vc.component._queryProduct(DEFAULT_PAGE, DEFAULT_ROWS);
            vc.component._queryGoodsClass();
            vc.getDict('product','category',function (_data) {
                vc.component.productCategorys = _data;
            })
        },
        _initEvent: function () {
            vc.on('pagination', 'page_event', function(_currentPage) {
                vc.component._queryProduct(_currentPage, DEFAULT_ROWS);
            });
        },
        methods: {
            _queryProduct: function (_page, _rows) {
                /*let param = {
                    params: {
                        page: _page,
                        row: _rows,
                        prodName: _prodName,
                    }
                };*/

                vc.component.conditions.page = _page;
                vc.component.conditions.row = _rows;
                let param = {
                    params: vc.component.conditions
                };

                vc.http.apiGet(
                    'product.queryProduct',
                    param,
                    function (json, res) {
                        let _productInfo = JSON.parse(json);
                        $that.productInfo.total = _productInfo.total;
                        $that.productInfo.records = _productInfo.records;

                        vc.emit('pagination', 'init', {
                            total: vc.component.productInfo.records,
                            currentPage: _page,
                            totalItem: _productInfo.total
                        });
                        $that.productInfo.products = _productInfo.data;
                    },
                    function (errInfo, error) {
                        console.log('请求失败处理');
                    }
                );
            },
            _doSearch:function(){
                $that._queryProduct(DEFAULT_PAGE, DEFAULT_ROWS);
            },
            _queryGoodsClass: function (_page, _rows) {
                let param = {
                    params: {
                        page: 1,
                        row: 50,
                    }
                };
                //发送get请求
                vc.http.apiGet('product.queryGoodsClass',
                    param,
                    function (json, res) {
                        let _productCategoryManageInfo = JSON.parse(json);
                        $that.productInfo.productCategorys = _productCategoryManageInfo.data;
                    }, function (errInfo, error) {
                        console.log('请求失败处理');
                    }
                );
            },
            _queryProductByCategoryId(_categoryId) {
                vc.component.conditions.categoryId = _categoryId;
                vc.component._queryProduct(DEFAULT_PAGE, DEFAULT_ROWS);
            },
        },
    });
})(window.vc);