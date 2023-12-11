(function(vc) {

    vc.extends({
        data: {
            viewEventTemplateParamInfo: {
                paramList: [],
            },
        },
        _initMethod: function() {

        },
        _initEvent: function() {
            vc.on('viewEventTemplateParam', 'openViewEventTemplateParamModal', function(_param) {
                $that.viewEventTemplateParamInfo.paramList = _param;
                $('#viewEventTemplateParam').modal('show');
            });
        },
        methods: {

        }
    });

})(window.vc);