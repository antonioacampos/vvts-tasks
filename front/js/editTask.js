document.addEventListener('DOMContentLoaded', async function() {
    const task = await getTask();
    if (!task) {
        return;
    }

    displayTask(task);

    editTask();
    
});

async function getTask() {
    const token = localStorage.getItem('tokenTaskVVTS');
    if (!token) {
        window.location.href = './index.html'; 
        return;
    }
    const id = sessionStorage.getItem('idTask');

    if (!id) {
        window.location.href = './tasklist.html'; 
        return;
    }
    
    try {
        const response = await fetch(`http://localhost:8080/api/v1/task/get/${id}`, {
            method: 'GET',
            headers: {
                'Authorization': `Bearer ${token}`,
                'Content-Type': 'application/json',
            }
        });

        if (!response.ok) {
            if (response.status === 401) {
                window.location.href = './index.html'; 
                return;
            } else {
                console.error('Error fetching task:', response.statusText);
                alert('Error fetching task. Please try again later.');
                return;
            }
        }

        const taskData = await response.json();

        return taskData;

    } catch (error) {
        console.error('Error fetching task:', error);
        alert('Error fetching task. Please try again later.');
        return;
    }
}

async function displayTask(task) {

    const titleInput = document.getElementById('task-title');
    const descriptionInput = document.getElementById('task-description');
    const deadlineInput = document.getElementById('task-deadline');

    titleInput.value = task.title;
    descriptionInput.value = task.description;
    deadlineInput.value = task.deadline.slice(0,-3);
}

async function editTask() {

    const taskForm = document.getElementById('task-form');
    taskForm.addEventListener('submit', async function(event) {
        event.preventDefault();

        const titleInput = document.getElementById('task-title');
        const descriptionInput = document.getElementById('task-description');
        const deadlineInput = document.getElementById('task-deadline');

        const title = titleInput.value;
        const description = descriptionInput.value;
        const deadline = deadlineInput.value;

        if (!title || !description || !deadline) {
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

        const id = sessionStorage.getItem('idTask');

        try {
            const response = await fetch(`http://localhost:8080/api/v1/task/edit/${id}`, {
                method: 'PUT',
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
                } else if (response.status === 404) {
                    const errorDiv = document.getElementById('error-message');
                    errorDiv.textContent = 'Task not found.';
                } else {
                    console.error('Error editing task:', response.statusText);
                    alert('Error editing task. Please try again later.');
                    return;
                }
            }
            if (response.ok){
                window.location.href = './tasklist.html';
            }

        } catch (error) {
            console.error('Error editing task:', error);
            alert('Error editing task. Please try again later.');
            return;
        }


    });

}