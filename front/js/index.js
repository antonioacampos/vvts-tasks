document.addEventListener('DOMContentLoaded', () => {
    const loginForm = document.getElementById('loginForm');

    loginForm.addEventListener('submit', async (event) => {
        event.preventDefault();

        const usernameInput = document.getElementById('username');
        const passwordInput = document.getElementById('password');
        const username = usernameInput.value;
        const password = passwordInput.value;

        if (!username) {
            const errorDiv = document.getElementById('username-error');
            errorDiv.textContent = 'Username is required.';
            return;
        }
        if (!password) {
            const errorDiv = document.getElementById('password-error');
            errorDiv.textContent = 'Password is required.';
            return;
        }

        const loginData = {
            username: username,
            password: password
        };
        const jsonData = JSON.stringify(loginData);

        try {
            const response = await fetch('http://localhost:8080/api/v1/authenticate', { 
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: jsonData,
            });

            if (!response.ok) {
                if (response.status === 401) {
                    const errorDiv = document.getElementById('errorMessage');
                    errorDiv.textContent = 'Username or password is incorrect.';
                }
                return;
            }

            const data = await response.json();
            const token = data.token;
            
            localStorage.setItem('tokenTaskVVTS', token);

            window.location.href = './tasklist.html'; 
            
        } catch (error) {
            console.error('Error to login', error);
            alert('Error during login. Please try again later.');
        }
        
    });
});

function redirectToRegister() {
    window.location.href = './register.html'; 
}

document.addEventListener('DOMContentLoaded', function() {
  const toRegisterBtn = document.getElementById('toRegisterBtn');
  if (toRegisterBtn) {
    toRegisterBtn.addEventListener('click', redirectToRegister);
  }
});