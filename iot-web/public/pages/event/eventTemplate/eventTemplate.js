(function(vc) {
    var DEFAULT_PAGE = 1;
    var DEFAULT_ROWS = 10;
    vc.extends({
        data: {
            eventTemplateInfo: {
                curEventTemplate: {},
            },
        },
        _initMethod: function() {},
        _initEvent: function() {
            vc.on('eventTemplate', 'switchEventTemplate', function(_param) {
                $that.eventTemplateInfo.curEventTemplate = _param;
            })
        },
        methods: {

        },
    });
})(window.vc);