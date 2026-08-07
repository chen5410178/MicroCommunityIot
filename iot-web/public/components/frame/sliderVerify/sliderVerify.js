(function(vc) {
    vc.extends({
        data: {
            bgImage: '',
            sliderImage: '',
            sliderY: 0,
            sliderWidth: 50,
            sliderHeight: 50,
            bgWidth: 300,
            showDialog: false,
            dialogSliderText: '向右拖动滑块完成验证',
            dragging: false,
            startX: 0,
            currentX: 0,
            verified: false
        },
        _initMethod: function() {
            $that.loadSliderCode();
        },
        _initEvent: function() {

        },
        methods: {
            loadSliderCode: function() {
                var param = {
                    params: {
                        _uId: '123'
                    }
                };
                vc.http.apiGet('/login.generatorSliderCode',
                    param,
                    function(json, res) {
                        if (res.status == 200) {
                            var data = JSON.parse(json);
                            vc.component.bgImage = data.bgImage;
                            vc.component.sliderImage = data.sliderImage;
                            vc.component.sliderY = data.y;
                            vc.component.sliderWidth = data.sliderWidth;
                            vc.component.sliderHeight = data.sliderHeight;
                            vc.component.sliderKey = data.sliderKey;
                            vc.component.bgWidth = 300;
                            vc.component.currentX = 0;
                            vc.component.verified = false;
                            vc.component.dialogSliderText = '向右拖动滑块完成验证';
                        }
                    },
                    function(errInfo, error) {
                        console.log('加载滑块验证码失败', errInfo);
                    });
            },
            refresh: function() {
                $that.loadSliderCode();
            },
            openDialog: function() {
                if (vc.component.verified) return;
                vc.component.showDialog = true;
                vc.component.currentX = 0;
                setTimeout(function() {
                    var btn = document.querySelector('.slider-dialog-btn');
                    if (btn) btn.style.left = '0px';
                }, 50);
            },
            closeDialog: function() {
                if (vc.component.dragging) return;
                vc.component.showDialog = false;
            },
            onDialogDragStart: function(e) {
                if (vc.component.verified) return;
                vc.component.dragging = true;
                var clientX = e.clientX || (e.touches && e.touches[0].clientX);
                vc.component.startX = clientX;

                var self = this;
                document.addEventListener('mousemove', $that._dialogDragMove);
                document.addEventListener('mouseup', $that._dialogDragEnd);
                document.addEventListener('touchmove', $that._dialogDragMove, { passive: false });
                document.addEventListener('touchend', $that._dialogDragEnd);
            },
            _dialogDragMove: function(e) {
                if (!vc.component.dragging || vc.component.verified) return;
                e.preventDefault();
                var clientX = e.clientX || (e.touches && e.touches[0].clientX);
                var dx = clientX - vc.component.startX;
                var maxX = vc.component.bgWidth - vc.component.sliderWidth;
                vc.component.currentX = Math.max(0, Math.min(dx, maxX));
                var btn = document.querySelector('.slider-dialog-btn');
                if (btn) {
                    btn.style.left = vc.component.currentX + 'px';
                }
            },
            _dialogDragEnd: function(e) {
                if (!vc.component.dragging || vc.component.verified) return;
                vc.component.dragging = false;
                document.removeEventListener('mousemove', $that._dialogDragMove);
                document.removeEventListener('mouseup', $that._dialogDragEnd);
                document.removeEventListener('touchmove', $that._dialogDragMove);
                document.removeEventListener('touchend', $that._dialogDragEnd);

                var btn = document.querySelector('.slider-dialog-btn');
                if (btn) {
                    btn.style.left = '0px';
                }
                if (vc.component.currentX > 0) {
                    var sliderX = Math.round(vc.component.currentX);
                    vc.component.verified = true;
                    vc.component.showDialog = false;
                    vc.component.$emit('sliderVerifySuccess', {sliderX,vc.component.sliderKey});
                } else {
                    vc.component.currentX = 0;
                }
            }
        },
        _destroyedMethod: function() {
            console.log('sliderVerify 组件销毁');
        }
    });
})(window.vc);
