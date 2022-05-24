$(document).ready(function () {
    $('#btn_edit').click(function (event) {
        event.preventDefault();
        $('#editOverlay').fadeIn(297, function () {
            $('#editInput').css('display', 'block').animate({opacity: 1}, 198);
        });
    });

    $('#editInput__close, #editOverlay').click(function () {
        $('#editInput').animate({opacity: 1}, 198, function () {
            $(this).css('display', 'none');
            $('#editOverlay').fadeOut(297);
        });
    });
});