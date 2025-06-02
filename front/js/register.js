import { isEmail } from './utils.js';

document.addEventListener('DOMContentLoaded', function() {
    const registerForm = document.getElementById('registerForm');
    const backToLoginBtn = document.getElementById('backToLoginBtn');
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
            let nameError = document.getElementById('name-error');
            nameError.textContent = 'Name is required.';
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
        if (!isEmail(email)) {
            const errorDiv = document.getElementById('email-error');
            errorDiv.textContent = 'Invalid email format.';
            return;
        }
        if (!password) {
            const errorDiv = document.getElementById('password-error');
            errorDiv.textContent = 'Password is required.';
            return;
        }
        
        const registerData = {
            name: name,
            lastname: lastName,
            email: email,
            password: password
        };
        const jsonData = JSON.stringify(registerData);

        try {
            const response = await fetch('http://localhost:8080/api/v1/register', { 
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                }, 
                body: jsonData,
            });

            
            if (!response.ok) {
                if (response.status === 409) {
                    const errorDiv = document.getElementById('errorMessage');
                    errorDiv.textContent = 'Username already exists.';
                }
                console.log(response);
                return;
            }

            redirectToLogin();
            
        } catch (error) {
            console.error('Error during registration:', error);
            alert('Error during register. Please try again later.');
        }
    });

    if (backToLoginBtn) {
        backToLoginBtn.addEventListener('click', redirectToLogin);
    }

    function redirectToLogin() {
        window.location.href = './index.html'; 
    }
});

