document.addEventListener('DOMContentLoaded', function () {
    // Password visibility toggle
    const togglePassword = document.getElementById('togglePassword');
    const passwordInput = document.getElementById('password');

    if (togglePassword && passwordInput) {
        togglePassword.addEventListener('click', function () {
            const type = passwordInput.getAttribute('type') === 'password' ? 'text' : 'password';
            passwordInput.setAttribute('type', type);

            // Change the eye icon
            const eyeIcon = this.querySelector('.icon-eye');
            if (type === 'text') {
                eyeIcon.innerHTML = `
                    <path d="M2 12s3-7 10-7 10 7 10 7-3 7-10 7-10-7-10-7z"></path>
                    <line x1="1" y1="1" x2="23" y2="23"></line>
                `;
            } else {
                eyeIcon.innerHTML = `
                    <path d="M2 12s3-7 10-7 10 7 10 7-3 7-10 7-10-7-10-7z"></path>
                    <circle cx="12" cy="12" r="3"></circle>
                `;
            }
        });
    }

    // Form validation
    const loginForm = document.getElementById('loginForm');

    if (loginForm) {
        loginForm.addEventListener('submit', function (event) {
            const username = document.getElementById('username').value.trim();
            const password = document.getElementById('password').value;
            let isValid = true;

            // Simple validation
            if (username === '') {
                isValid = false;
                showError('username', 'Username is required');
            } else {
                clearError('username');
            }

            if (password === '') {
                isValid = false;
                showError('password', 'Password is required');
            } else {
                clearError('password');
            }

            if (!isValid) {
                event.preventDefault();
            }
        });
    }

    // Helper functions for form validation
    function showError(inputId, message) {
        const input = document.getElementById(inputId);
        const errorElement = document.createElement('div');
        errorElement.className = 'error-message';
        errorElement.textContent = message;
        errorElement.style.color = 'var(--error-color)';
        errorElement.style.fontSize = '0.75rem';
        errorElement.style.marginTop = '0.25rem';

        // Remove any existing error message
        clearError(inputId);

        // Add error styling to input
        input.style.borderColor = 'var(--error-color)';

        // Add error message after input wrapper
        input.closest('.input-wrapper').insertAdjacentElement('afterend', errorElement);
    }

    function clearError(inputId) {
        const input = document.getElementById(inputId);
        const wrapper = input.closest('.form-group');
        const errorMessage = wrapper.querySelector('.error-message');

        if (errorMessage) {
            errorMessage.remove();
        }

        // Reset input styling
        input.style.borderColor = '';
    }

    // Add input focus effects
    const inputs = document.querySelectorAll('input[type="text"], input[type="password"]');

    inputs.forEach(input => {
        input.addEventListener('focus', function () {
            this.closest('.input-wrapper').style.boxShadow = '0 0 0 3px rgba(79, 70, 229, 0.1)';
            this.closest('.input-wrapper').style.borderColor = 'var(--primary-color)';
        });

        input.addEventListener('blur', function () {
            this.closest('.input-wrapper').style.boxShadow = '';
            this.closest('.input-wrapper').style.borderColor = '';
        });
    });
});