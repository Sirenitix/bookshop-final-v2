$(document).ready(function () {
    $('#users-upload').click(function () {
        $.ajax({
            url: '/users/list',
            type: 'GET',
            dataType: 'json',
            data: {
                offset: function () {
                    var oldOffset = $('#users-upload').data('refreshoffset');
                    $('#users-upload').data('refreshoffset', ++oldOffset);
                    return $('#users-upload').data('refreshoffset');
                },
                limit: function () {
                    return $('#users-upload').data('refreshlimit');
                }
            },
            success: function (data) {
                if (data.length < 20) {
                    $('#users-upload').hide();
                }
                var rows = '';
                data.forEach(function (user) {
                        rows = rows + '<tr>' +
                            '<td>' + user.name + '</td>' +
                            '<td>' + user.roleString + '</td>' +
                            '<td>' + '₽ ' + user.balance + '</td>' +
                            '<td>' + user.formatTime + '</td>' +
                            '<td>' + '<a href="users/' + user.hash + '">' + 'Подробнее' + '</a>' + '</td>' +
                            '</tr>'
                });
                $('#list-users').append(rows);
            }
        });
    });
});