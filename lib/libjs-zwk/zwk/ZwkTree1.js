(function($) {

    $.fn.ZwkTree1 = function(opts) {
        var toggles = this.find('.toggle');
        toggles.addClass('fa');
        toggles.click(function(event) {
            var li = $(event.target).parents("li")[0];
            $(li).toggleClass("expand");
        });
    };

})(jQuery);

$(document).ready(function() {

    var IE = $('html').is('.lt-ie7, .lt-ie8, .lt-ie9');
    if (IE) {
        window.addClass = function(elm, c) {
            $(elm).addClass(c);
        };
        window.removeClass = function(elm, c) {
            $(elm).removeClass(c);
        };
        window.hasClass = function(elm, c) {
            $(elm).hasClass(c);
        };
    } else {
        window.addClass = function(elm, c) {
            elm.classList.add(c);
        };
        window.removeClass = function(elm, c) {
            elm.classList.remove(c);
        };
        window.hasClass = function(elm, c) {
            elm.classList.contains(c);
        };
    }
    // });

    function build(parent, node, depth, index, opts) {
        if (node == null) {
            alert("null node: depth=" + depth + ", index=" + index);
            return;
        }

        var children;
        if (depth == 0) {
            children = opts.buildNode(parent, node);
        } else {
            var li = document.createElement("li");
            children = opts.buildNode(li, node);

            span = document.createElement("span");
            span.className = 'toggle';
            li.appendChild(span);

            parent.appendChild(li);
            parent = li;
        }

        parent.setAttribute('depth', depth);
        // parent.setAttribute('index', index);

        var leaf = children == null || children.length == 0;
        if (leaf) {
            addClass(parent, 'leaf');
        } else {
            var ul = document.createElement('ul');
            parent.appendChild(ul);

            if (depth == 0)
                addClass(ul, "roots");
            if (opts.minDepth != null)
                if (depth < opts.minDepth)
                    addClass(parent, "expand");

            for ( var i = 0; i < children.length; i++)
                build(ul, children[i], depth + 1, i, opts);
        }
    }

    // json -> build()
    $.fn.ZwkTreeX = function(opts) {
        var datavar = this.attr("data-var");
        this.html("");
        if (opts == null)
            opts = {};
        build(this[0], eval(datavar), 0, 0, opts);

        this.ZwkTree1();

        $('.label', this).click(function(event) {
            var li = $(event.target).closest("li");

            var tree = li.closest(".ZwkTree");
            tree.find('li.selected').removeClass('selected');
            li.addClass('selected');

            var rid = li.attr('rid');
            var result = $('.bottombar .result');
            result.attr('rid', rid);
            result.attr('label', $(event.target).text());

            var parents = $(event.target).parents("li");
            var labels = [];
            for ( var i = 0; i < parents.length; i++) {
                var p = parents[i];
                if ($(p).hasClass('ZwkTree'))
                    break;
                labels.push($(p).children('.label').text());
            }
            labels.reverse();

            var sb = '';
            for ( var i = 0; i < labels.length; i++) {
                if (i > 0)
                    sb += ' - ';
                sb += labels[i];
            }
            result.text(sb);

            $('.bottombar .btn.ok').fadeIn();
        });
    };

});
