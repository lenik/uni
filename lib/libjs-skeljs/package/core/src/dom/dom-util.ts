import $ from 'jquery';

var fn: any = $.fn;

/**
 *  Convert HTML element attributes to object.
 *
 * @return object
 */
fn.attrs = function() {
    var elm = this[0];
    var node = elm.attributes;
    var obj: any = {};
    for (var i = 0; i < node.length; i++) {
        var ent = node[i];
        obj[ent.name] = ent.value;
    }
    return obj;
};

/**
 * Convert serializeArray to object.
 *
 * @return object
 */
fn.serializeObject = function() {
    var o: any = {};
    var a = this.serializeArray();
    $.each(a, function() {
        if (o[this.name] !== undefined) {
            if (!o[this.name].push) {
                o[this.name] = [o[this.name]];
            }
            o[this.name].push(this.value || '');
        } else {
            o[this.name] = this.value || '';
        }
    });
    return o;
};

/**
 * Convert serialArray to object, take care of array.
 *
 @return object
*/
fn.serializeObject2 = function() {

    var self = this;
    var json: any = {};
    var push_counters: any = {};
    var patterns: any = {
            "validate": /^[a-zA-Z][a-zA-Z0-9_]*(?:\[(?:\d*|[a-zA-Z0-9_]+)\])*$/,
            "key":      /[a-zA-Z0-9_]+|(?=\[\])/g,
            "push":     /^$/,
            "fixed":    /^\d+$/,
            "named":    /^[a-zA-Z0-9_]+$/
        };

    this.build = function(base: any, key: string, value: any) {
        base[key] = value;
        return base;
    };

    this.push_counter = function(key: string){
        if(push_counters[key] === undefined) {
            push_counters[key] = 0;
        }
        return push_counters[key]++;
    };

    $.each($(this).serializeArray(), function() {

        // skip invalid keys
        if(!patterns.validate.test(this.name)){
            return;
        }

        var k: string | undefined;
        var keys: RegExpMatchArray | null = this.name.match(patterns.key);
        var merge = this.value,
            reverse_key = this.name;

        while (keys != null && (k = keys.pop()) !== undefined) {

            // adjust reverse_key
            reverse_key = reverse_key.replace(new RegExp("\\[" + k + "\\]$"), '');

            // push
            if(k.match(patterns.push)){
                merge = self.build([], self.push_counter(reverse_key), merge);
            }

            // fixed
            else if(k.match(patterns.fixed)){
                merge = self.build([], k, merge);
            }

            // named
            else if(k.match(patterns.named)){
                merge = self.build({}, k, merge);
            }
        }

        json = $.extend(true, json, merge);
    });

    return json;
};

/**
     * $(form).userSubmit()
     * By default form has no submit method.
     */
fn.userSubmit = function() {
    this.each(function (i: number, form: HTMLFormElement) {
        //get the form element's document to create the input control with
        //(this way will work across windows in IE8)
        var button = form.ownerDocument.createElement('input');
        //make sure it can't be seen/disrupts layout (even momentarily)
        button.style.display = 'none';
        //make it such that it will invoke submit if clicked
        button.type = 'submit';
        //append it and click it
        form.appendChild(button).click();
        //if it was prevented, make sure we don't get a build up of buttons
        form.removeChild(button);
    });
};

fn.repeatChildren = function (n: number) {
    let children = this.html();
    for (var i = 0; i < n; i++) {
        this.append(children);
    }
};
