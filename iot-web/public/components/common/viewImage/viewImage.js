(function (vc) {
    vc.extends({
        data: {
            viewImageInfo: {
                url: '',
            }
        },
        _initMethod: function () {
        },
        _initEvent: function () {
            vc.on('viewImage', 'showImage', function (_param) {
                $that.viewImageInfo.url = _param.url;
                $('#viewImageModel').modal('show');
            });
        },
        methods: {
            
        }
    });
})(window.vc);