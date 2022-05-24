$(document).ready(function () {
    $('#btn_download').click(function (event) {
        event.preventDefault();
        $('#myOverlay').fadeIn(297, function () {
            $('#myPopup').css('display', 'block').animate({opacity: 1}, 198);
        });
    });

    $('#myPopup__close, #myOverlay').click(function () {
        $('#myPopup').animate({opacity: 1}, 198, function () {
            $(this).css('display', 'none');
            $('#myOverlay').fadeOut(297);
        });
    });
});