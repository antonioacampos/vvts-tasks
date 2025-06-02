document.addEventListener('DOMContentLoaded', function() {
    const registerForm = document.getElementById('registerForm');
    registerForm.addEventListener('submit', async function(event) {
        event.preventDefault();

        const nameInput = document.getElementById('name');
        const lastNameInput = document.getElementById('lastname');
        const emailInput = document.getElementById('email');
        const passwordInput = document.getElementById('password');
        
        const name = nameInput.value;
        const lastName = lastNameInput.value;
        const email = emailInput.value;
        const password = passwordInput.value;

        if (!name) {
            const errorDiv = document.getElementById('name-error');
            errorDiv.textContent = 'Name is required.';
            return;
        }
        if (!lastName) {
            const errorDiv = document.getElementById('lastname-error');
            errorDiv.textContent = 'Last name is required.';
            return;
        }
        if (!email) {
            const errorDiv = document.getElementById('email-error');
            errorDiv.textContent = 'Email is required.';
            return;
        }
        if (!password) {
            const errorDiv = document.getElementById('password-error');
            errorDiv.textContent = 'Password is required.';
            return;
        }
        
        const registerData = {
            name: name,
            lastName: lastName,
            email: email,
            password: password
        };

        try {
            const response = await fetch('http://localhost:8080/api/v1/register', { 
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify(registerData),
            });

            if (!response.ok) {
                if (response.status === 400) {
                    const errorDiv = document.getElementById('errorMessage');
                    errorDiv.textContent = 'Username already exists.';
                }
                return;
            }

            redirectToLogin();
            
        } catch (error) {
            console.error('Error during registration:', error);
            alert('An error occurred while trying to register.');
        }
    });
});

function redirectToLogin() {
    window.location.href = './index.html'; 
}