(function(vc) {
    vc.extends({
        data: {
            bgImage: '',
            sliderImage: '',
            sliderY: 0,
            sliderWidth: 50,
            sliderHeight: 50,
            bgWidth: 300,
            sliderText: '按住滑块，拖动到最右边',
            dragging: false,
            startX: 0,
            currentX: 0,
            verified: false
        },
        _initMethod: function() {
            $that.loadSliderCode();
        },
        _initEvent: function() {
            var self = this;
            document.addEventListener('mousemove', function(e) {
                $that.onDragMove(e);
            });
            document.addEventListener('mouseup', function(e) {
                $that.onDragEnd(e);
            });
            document.addEventListener('touchmove', function(e) {
                $that.onDragMove(e);
            }, { passive: false });
            document.addEventListener('touchend', function(e) {
                $that.onDragEnd(e);
            });
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
                            vc.component.bgWidth = 300;
                            vc.component.currentX = 0;
                            vc.component.verified = false;
                            vc.component.sliderText = '按住滑块，拖动到最右边';
                        }
                    },
                    function(errInfo, error) {
                        console.log('加载滑块验证码失败', errInfo);
                    });
            },
            refresh: function() {
                $that.loadSliderCode();
            },
            onDragStart: function(e) {
                if (vc.component.verified) return;
                vc.component.dragging = true;
                var clientX = e.clientX || (e.touches && e.touches[0].clientX);
                vc.component.startX = clientX;
            },
            onDragMove: function(e) {
                if (!vc.component.dragging || vc.component.verified) return;
                var clientX = e.clientX || (e.touches && e.touches[0].clientX);
                var dx = clientX - vc.component.startX;
                var maxX = vc.component.bgWidth - vc.component.sliderWidth;
                vc.component.currentX = Math.max(0, Math.min(dx, maxX));
                var btn = document.querySelector('.slider-verify .slider-btn');
                if (btn) {
                    btn.style.left = vc.component.currentX + 'px';
                }
            },
            onDragEnd: function(e) {
                if (!vc.component.dragging || vc.component.verified) return;
                vc.component.dragging = false;
                var btn = document.querySelector('.slider-verify .slider-btn');
                if (btn) {
                    btn.style.left = '0px';
                }
                if (vc.component.currentX > 0) {
                    var sliderX = Math.round(vc.component.currentX);
                    vc.component.$emit('sliderVerifySuccess', sliderX);
                }
            }
        },
        _destroyedMethod: function() {
            console.log('sliderVerify 组件销毁');
        }
    });
})(window.vc);
