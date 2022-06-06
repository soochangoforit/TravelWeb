$(function(){

    $('.spinner .btn:first-of-type').on('click', function() {
        var btn = $(this);
        var input = btn.closest('.spinner').find('input');
        if (input.attr('max') == undefined || parseFloat(input.val()) < parseFloat(input.attr('max'))) {
            input.val(parseFloat(input.val(), 10) + 0.5);
        } else {
            btn.next("disabled", true);
        }
    });
    $('.spinner .btn:last-of-type').on('click', function() {
        var btn = $(this);
        var input = btn.closest('.spinner').find('input');
        if (input.attr('min') == undefined || parseFloat(input.val()) > parseFloat(input.attr('min'))) {
            input.val(parseFloat(input.val(), 10) - 0.5);
        } else {
            btn.prev("disabled", true);
        }
    });

})