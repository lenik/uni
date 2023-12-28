import $ from 'jquery';

/**
 * ajaxCall(form)
 * 
 * form attributes:
 * 
 *      method: get/post
 *      action: AJAX url
 * 
 *      oncomplete
 *      onsuccess
 *      onerror
 *      ondone
 *      onfail
 * 
 *      afterset: code evaled before ajax-call.
 * 
 * @returns a promise.
 */
export function ajaxCall() {
    var $form = this;
    var params = $form.attrs();
    var data = $form.serialize();
    
    params = $.extend(params, {
        type: params.method,
        url: params.action,
        data: data,

        complete: function(xhr, status) {
            if (params.oncomplete != null) eval(params.oncomplete);
        },
        success: function (data, status, xhr) {
            if (params.onsuccess != null) eval(params.onsuccess);
        },
        error: function (xhr, status, error) {
            if (params.onerror != null) eval(params.onerror);
        }
    });

    if (params.afterset)
        eval(params.afterset);

    if (params.type == null) params.type = 'POST';

    var future = $.ajax(params)
        .done(function (data, status, xhr)
              { if (params.ondone != null) eval(params.ondone); })
        .fail(function (xhr, status, error)
              { if (params.onfail != null) eval(params.onfail); })
    ;

    $form.on('submit', (event) => {
        // Instead of return false:
        // avoid to execute the actual submit of the form.
        event.preventDefault();
        });
        
    return future;
}


(function ($) {

    $(document).ready(function() {
        $("form.ajax").attr("afterset", "");
        $("form.ajax").each(function (i, form) {
            var id = $(form).attr('id');
            $(form).append('<input type="hidden" name="form" value="' + id + '">');
        });
    });

    $.fn.ajaxCall = ajaxCall;

    $("form.ajax").on('submit', function (event) {
        var form = $(event.target);
        form.ajaxCall();
    });
})(jQuery);
