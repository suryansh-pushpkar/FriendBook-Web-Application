$(document).ready(function () {
    const signupForm = $('#signupForm');
    const messageDiv = $('#message');
    const siteKey = '6LfxnY0sAAAAAAHD5n738xWvgiNERN--S1dFgdaR';

    signupForm.on('submit', function (e) {
        e.preventDefault();
        messageDiv.empty();

        grecaptcha.ready(function () {
            grecaptcha.execute(siteKey, { action: 'signup' }).then(function (token) {
                const formData = {
                    fullName: $('#fullName').val(),
                    email: $('#email').val(),
                    password: $('#password').val(),
                    captchaToken: token
                };

                $.ajax({
                    url: '/auth/signup',
                    type: 'POST',
                    contentType: 'application/json',
                    data: JSON.stringify(formData),
                    beforeSend: function() {
                        signupForm.find('button').prop('disabled', true).text('Processing...');
                    },
                    success: function (response) {
                        messageDiv.html('<div class="alert alert-success">Account created successfully. Redirecting to the login page...</div>');
                        setTimeout(() => {
                            window.location.href = '/login';
                        }, 1800);
                    },
                    error: function (xhr) {
                        signupForm.find('button').prop('disabled', false).text('Sign Up');
                        let errorMsg = xhr.responseJSON ? xhr.responseJSON.message : 'Signup failed.';
                        messageDiv.html(`<div class="alert alert-danger">${errorMsg}</div>`);
                    }
                });
            });
        });
    });
});