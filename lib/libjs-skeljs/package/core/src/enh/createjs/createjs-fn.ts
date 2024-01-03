import $ from 'jquery';
// import * as createjs from 'createjs';

var win: any = window;
win.tween = createjs.Tween.get;

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
        attachTo: function(root: any) {
            root.addChild(this);
            return this;
        },
        at: function(x: number, y: number) {
            this.x = x;
            this.y = y;
            return this;
        }
    });

$.extend(createjs.Bitmap.prototype, {
    width: function() {
        if (this.image == null) return 10;
        return this.image.width * this.scaleX;
    },
    height: function() {
        if (this.image == null) return 10;
        return this.image.height * this.scaleY;
    },
    resize: function(w, h) {
        if (this.image == null) return this;
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