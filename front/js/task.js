document.addEventListener('DOMContentLoaded', async function() {
    const task = await getTask();

    
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