document.addEventListener('DOMContentLoaded', async function() {
    const task = await getTask();
    if (!task) {
        return;
    }

    const taskContainer = document.getElementById('task-container');
    taskContainer.innerHTML = ''; 

    const titleElement = document.createElement('h3');
    titleElement.textContent = task.title;
    taskContainer.appendChild(titleElement);

    const descriptionElement = document.createElement('p');
    descriptionElement.textContent = task.description ;
    taskContainer.appendChild(descriptionElement);

    const statusElement = document.createElement('p');
    statusElement.textContent = `Status: ${task.status}`;
    taskContainer.appendChild(statusElement);

    const deadlineElement = document.createElement('p');
    deadlineElement.textContent = `Deadline: ${new Date(task.deadline).toLocaleDateString()}`;
    taskContainer.appendChild(deadlineElement);

    if(task.estimatedTime) {
        const estimatedTimeElement = document.createElement('p');
        estimatedTimeElement.textContent = `Estimated Time: ${task.estimatedTime}`;
        taskContainer.appendChild(estimatedTimeElement);
    }

    if(task.timeSpent) {
        const timeSpentElement = document.createElement('p');
        timeSpentElement.textContent = `Time Spent: ${task.timeSpent}`;
        taskContainer.appendChild(timeSpentElement);
    }

    if(task.startTime) {
        const startTimeElement = document.createElement('p');
        startTimeElement.textContent = `Start Time: ${new Date(task.startTime).toLocaleString()}`;
        taskContainer.appendChild(startTimeElement);
    }

    if(task.finishTime) {
        const finishTimeElement = document.createElement('p');
        finishTimeElement.textContent = `Finish Time: ${task.finishTime ? new Date(task.finishTime).toLocaleString() : 'No finish time set'}`;
        taskContainer.appendChild(finishTimeElement);
    }

    if(task.sugestion) {
        const sugestionElement = document.createElement('p');
        sugestionElement.textContent = `Suggestion: ${task.sugestion || 'No suggestion provided'}`;
        taskContainer.appendChild(sugestionElement);
    }
    
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