
$(() => {

    // trim input strings for input.trim
    $("input[type=text].trim").on('blur', function(e) {
        let val: string = String($(this).val());
        $(this).val(val.trim());
    });

    // auto add .dirty to input and forms
    $("input, select", $("form.check-dirty")).on('change', function (event) {
        var enclosingForms = $(event.target).parents("form");
        $(event.target).addClass('dirty');
        if (enclosingForms.length) {
            enclosingForms.addClass('dirty');
        }
    });

});
