(function (vc, vm) {

    vc.extends({
        data: {
            eventPoolStaff: {}
        },
        _initMethod: function () {
        },
        _initEvent: function () {
            vc.on('eventDetail', 'openEventDetailModal', function (_eventPoolStaff) {
                $that.eventPoolStaff = _eventPoolStaff;
                $that.queryEvent(_eventPoolStaff.eventId);
                $('#eventDetailModel').modal('show');
            })
        },
        methods: {
        }
    });
})(window.vc, window.$that);
