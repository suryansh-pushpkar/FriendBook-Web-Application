$(document).ready(function () {
    const loginForm = $('#loginForm');
    const messageDiv = $('#loginMessage');

    loginForm.on('submit', function (e) {
        e.preventDefault();
        messageDiv.empty();

        const payload = {
            email: $('#email').val(),
            password: $('#password').val()
        };

        $.ajax({
            url: '/auth/login',
            type: 'POST',
            contentType: 'application/json',
            data: JSON.stringify(payload),
            beforeSend: function () {
                loginForm.find('button').prop('disabled', true).text('Logging in...');
            },
            success: function (response) {
                if (response.token) {
                    sessionStorage.setItem('friendbookToken', response.token);
                }
                messageDiv.html(`<div class="alert alert-success">${response.message}</div>`);
                setTimeout(() => {
                    window.location.href = response.redirectUrl;
                }, 700);
            },
            error: function (xhr) {
                loginForm.find('button').prop('disabled', false).text('Log In');
                const errorMsg = xhr.responseJSON
                    ? xhr.responseJSON.message
                    : 'Either email or password is incorrect try again';
                messageDiv.html(`<div class="alert alert-danger">${errorMsg}</div>`);
            }
        });
    });
});