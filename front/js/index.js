document.addEventListener('DOMContentLoaded', () => {
    const loginForm = document.getElementById('loginForm');

    loginForm.addEventListener('submit', async (event) => {
        event.preventDefault();

        const usernameInput = document.getElementById('username');
        const passwordInput = document.getElementById('password');
        const username = usernameInput.value;
        const password = passwordInput.value;

        const loginData = {
            username: username,
            password: password
        };

        try {
            const response = await fetch('http://localhost:8080/api/v1/authenticate', { 
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify(loginData),
            });

            if (!response.ok) {
                const error = await response.json();
                alert(`Login error: ${error.message || 'Failure to login'}`);
                return;
            }

            const data = await response.json();
            const token = data.token;
            console.log(token);

            
            localStorage.setItem('tokenTaskVVTS', token);

            window.location.href = './tasklist.html'; 
            
        } catch (error) {
            console.error('Erro ao enviar requisição de login:', error);
            alert('Ocorreu um erro ao tentar fazer login.');
        }
        
    });
});

function redirectToRegister() {
    window.location.href = './register.html'; 
}