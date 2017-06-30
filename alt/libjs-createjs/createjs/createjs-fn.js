(function($) {
    window.tween = createjs.Tween.get;
    
    var events = [
        'click', 'dblclick', 'mouseover', 'mouseout', 'mousemove', 'mousedown', 'mouseMoveOutside', 'enableMouseOver', 'rollover', 'rollout', 'pressmove', 'pressup' ];
        for (var i = 0; i < events.length; i++) {
            (function(name) {
                createjs.Shape.prototype[name] = function(fn) {
                    return this.addEventListener(name, fn);
                };
            })(events[i]);
        }

    var drawables = [createjs.Container, 
                     createjs.Bitmap,
                     createjs.Shape,
                     createjs.Text];
    for (var i = 0; i < drawables.length; i++)
        $.extend(drawables[i].prototype, {
            attachTo: function(root) {
                root.addChild(this);
                return this;
            },
            at: function(x, y) {
                this.x = x;
                this.y = y;
                return this;
            }
        });

    $.extend(createjs.Bitmap.prototype, {
        width: function() {
            return this.image.width * this.scaleX;
        },
        height: function() {
            return this.image.height * this.scaleY;
        },
        resize: function(w, h) {
            this.scaleX = w / this.image.width;
            this.scaleY = h / this.image.height;
            return this;
        },
        atCenter: function(x, y) {
            this.x = x;
            this.y = y;
            var dx = this.width() / 2;
            var dy = this.height() / 2;
            //this.regX = this.width() / 2;
            //this.regY = this.height() / 2;
            return this;
        }
    });
})($);
