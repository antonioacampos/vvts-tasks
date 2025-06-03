document.addEventListener('DOMContentLoaded', function() {

    const token = localStorage.getItem('tokenTaskVVTS');
    if (!token) {
        window.location.href = './index.html'; 
        return;
    }

    createTask();
    
});

async function createTask() {

    const taskForm = document.getElementById('task-form');
    taskForm.addEventListener('submit', async function(event) {
        event.preventDefault();

        const titleInput = document.getElementById('task-title');
        const descriptionInput = document.getElementById('task-description');
        const deadlineInput = document.getElementById('task-deadline');

        const title = titleInput.value;
        const description = descriptionInput.value;
        const deadline = deadlineInput.value;

        if( !title || !description || !deadline) {
            const errorDiv = document.getElementById('error-message');
            errorDiv.textContent = 'All fields are required.';
            return;
        }

        const deadlineFormated = deadline + ":00";
        
        const taskData = {
            title: title,
            description: description,
            deadline: deadlineFormated,
        };
        const jsonData = JSON.stringify(taskData);

        const token = localStorage.getItem('tokenTaskVVTS');

        try {
            const response = await fetch('http://localhost:8080/api/v1/task/create', {
                method: 'POST',
                headers: {
                    'Authorization': `Bearer ${token}`,
                    'Content-Type': 'application/json',
                },
                body: jsonData,
            
            });

            if (!response.ok) {
                if (response.status === 401) {
                    window.location.href = './index.html'; 
                    return;
                } else {
                    console.error('Error creating task:', response.statusText);
                    alert('Error creating task. Please try again later.');
                    return;
                }
            }
            if (response.ok){
                window.location.href = './tasklist.html';
            }

        } catch (error) {
            console.error('Error creating task:', error);
            alert('Error creating task. Please try again later.');
            return;
        }
    });
}