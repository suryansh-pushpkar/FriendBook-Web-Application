$(document).ready(function () {
    const loginForm = $('#loginForm');
    const messageDiv = $('#loginMessage');

    loginForm.on('submit', function (e) {
        e.preventDefault();

        messageDiv.empty();
        const submitBtn = loginForm.find('button');
        const originalBtnText = submitBtn.text();

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
                submitBtn.prop('disabled', true).text('Logging in...');
            },
            success: function (response) {
                if (response.token) {
                    sessionStorage.setItem('friendbookToken', response.token);
                }

                // 2. Store the UserProfile DTO (to populate profile.html instantly)
                if (response.userProfile) {
                    localStorage.setItem('userProfile', JSON.stringify(response.userProfile));
                }

                // 3. Show success message
                messageDiv.html(`
                    <div class="alert alert-success d-flex align-items-center">
                        <i class="bi bi-check-circle-fill me-2"></i>
                        <div>${response.message || 'Login Successful! Redirecting...'}</div>
                    </div>
                `);

                // 4. Redirect after a short delay
                setTimeout(() => {
                    if (response.redirectUrl) {
                        window.location.href = response.redirectUrl;
                    } else {
                        const username = response.userProfile ? response.userProfile.username : 'friendbook';
                        window.location.href = '/profile/' + username;
                    }
                }, 800);
            },
            error: function (xhr) {
                submitBtn.prop('disabled', false).text(originalBtnText);

                let errorMsg = 'Either email or password is incorrect. Please try again.';

                // Check if backend sent a specific error (like Rate Limiting or detailed fail)
                if (xhr.responseJSON && xhr.responseJSON.message) {
                    errorMsg = xhr.responseJSON.message;
                }

                messageDiv.html(`
                    <div class="alert alert-danger d-flex align-items-center">
                        <i class="bi bi-exclamation-triangle-fill me-2"></i>
                        <div>${errorMsg}</div>
                    </div>
                `);
            }
        });
    });
});