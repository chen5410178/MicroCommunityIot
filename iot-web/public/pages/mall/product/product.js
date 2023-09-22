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
            },
            domainName: '',
            prodDetailUri: '/page.html#/pages/plateform/productDetail?productId=',
        },
        _initMethod: function () {
            vc.getDict('microIndustry_platform', 'domain_name', function (_data) {
                $that.domainName = _data[0].statusCd;
            });
            $that._queryProduct(DEFAULT_PAGE, DEFAULT_ROWS);
            //$that._queryGoodsClass();
            vc.getDict('product','category',function (_data) {
                $that.productCategorys = _data;
            })
        },
        _initEvent: function () {
            vc.on('pagination', 'page_event', function(_currentPage) {
                $that._queryProduct(_currentPage, DEFAULT_ROWS);
            });
        },
        methods: {
            _queryProduct: function (_page, _rows) {
                $that.conditions.page = _page;
                $that.conditions.row = _rows;
                let param = {
                    params: $that.conditions
                };

                vc.http.apiGet(
                    'product.queryProduct',
                    param,
                    function (json, res) {
                        let _productInfo = JSON.parse(json);
                        $that.productInfo.total = _productInfo.total;
                        $that.productInfo.records = _productInfo.records;

                        vc.emit('pagination', 'init', {
                            total: $that.productInfo.records,
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
            _doReset:function () {
                $that.conditions = {
                    prodName: '',
                    categoryId: '',
                };
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
                $that.conditions.categoryId = _categoryId;
                $that._queryProduct(DEFAULT_PAGE, DEFAULT_ROWS);
            },
        },
    });
})(window.vc);