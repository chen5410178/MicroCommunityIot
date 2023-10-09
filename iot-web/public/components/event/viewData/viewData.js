(function(vc) {

    vc.extends({
        data: {
            viewDataInfo: {
                title: '',
                data: ''
            },
            flagOrgName: false
        },
        _initMethod: function() {

        },
        _initEvent: function() {
            vc.on('viewData', 'openEventViewDataModal', function(_param) {
                $that.viewDataInfo.title = _param.title;
                $that.viewDataInfo.data = _param.data;
                $('#viewDataModel').modal('show');
            });
        },
        methods: {

        }
    });

})(window.vc);