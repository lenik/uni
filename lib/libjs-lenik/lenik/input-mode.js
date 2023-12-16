$(document).ready(function() {

    // input.trim
    $("input[type=text].trim").on('blur', function(e) {
        var val = $(this).val();
        $(this).val(val.trim());
    });

    $("input, select").on('change', function (event) {
        var enclosingForm = $(event.target).parents("form");
        $(event.target).addClass('dirty');
        if (enclosingForm.length) {
            enclosingForm.addClass('dirty');
        }
    });

});
