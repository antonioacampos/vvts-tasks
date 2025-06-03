import { convertDate } from './utils.js';

document.addEventListener('DOMContentLoaded', async function() {
    const task = await getTask();
    if (!task) {
        return;
    }

    displayTask(task);

    const editTaskButton = document.getElementById('edit-task-btn');
    editTaskButton.addEventListener('click', function() {
        sessionStorage.setItem('idTask', task.id);
        window.location.href = './editTask.html';
    });

    markAsCompleteButton();
    clockinButton();
    clockoutButton();
    deleteButton();

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

function displayTask(task) {
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
    deadlineElement.textContent = `Deadline: ${convertDate(new Date(task.deadline))}`;
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
}

async function markAsCompleteButton() {

    const markAsCompleteButton = document.getElementById('mark-complete-btn');
    markAsCompleteButton.addEventListener('click', async function() {
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
            const response = await fetch(`http://localhost:8080/api/v1/task/mark-completed/${id}`, {
                method: 'PUT',
                headers: {
                    'Authorization': `Bearer ${token}`,
                    'Content-Type': 'application/json',
                }
            });

            if (response.ok) {
                alert('Mark as completed successful.');
                window.location.reload();
            } else {
                if (response.status === 401) {
                    window.location.href = './index.html'; 
                } else if (response.status === 403) {
                    const error = document.getElementById('error-message');
                    error.textContent = 'Only In progress tasks can be marked as completed.';
                }
            }
        } catch (error) {
            console.error('Error marking as completed:', error);
            alert('Error marking as completed. Please try again later.');
        }
    });

}


async function clockinButton() {
    const clockinButton = document.getElementById('clock-in-btn');
    clockinButton.addEventListener('click', async function() {
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
            const response = await fetch(`http://localhost:8080/api/v1/task/clock-in/${id}`, {
                method: 'PUT',
                headers: {
                    'Authorization': `Bearer ${token}`,
                    'Content-Type': 'application/json',
                }
            });

            if (response.ok) {
                alert('Clock-in successful.');
                window.location.reload();
            } else {
                if (response.status === 401) {
                    window.location.href = './index.html'; 
                } else if (response.status === 403) {
                    const error = document.getElementById('error-message');
                    error.textContent = 'Only Pending tasks can be clocked in.';
                }
            }
        } catch (error) {
            console.error('Error clocking in:', error);
            alert('Error clocking in. Please try again later.');
        }
    });

}

async function clockoutButton() {

    const clockinButton = document.getElementById('clock-out-btn');
    clockinButton.addEventListener('click', async function() {
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
            const response = await fetch(`http://localhost:8080/api/v1/task/clock-out/${id}`, {
                method: 'PUT',
                headers: {
                    'Authorization': `Bearer ${token}`,
                    'Content-Type': 'application/json',
                }
            });

            if (response.ok) {
                alert('Clock-out successful.');
                window.location.reload();
            } else {
                if (response.status === 401) {
                    window.location.href = './index.html'; 
                } else if (response.status === 403) {
                    const error = document.getElementById('error-message');
                    error.textContent = 'Only In progress tasks can be clocked in.';
                }
            }
        } catch (error) {
            console.error('Error clocking out:', error);
            alert('Error clocking out. Please try again later.');
        }
    });


}


async function deleteButton() {
    const deleteButton = document.getElementById('delete-task-btn');
    deleteButton.addEventListener('click', async function() {
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
            const response = await fetch(`http://localhost:8080/api/v1/task/delete/${id}`, {
                method: 'DELETE',
                headers: {
                    'Authorization': `Bearer ${token}`,
                    'Content-Type': 'application/json',
                }
            });

            if (response.ok) {
                alert('Task deleted successfully.');
                window.location.href = './tasklist.html';
            } else {
                if (response.status === 401) {
                    window.location.href = './index.html'; 
                } else {
                    const error = document.getElementById('error-message');
                    error.textContent = 'Error deleting task. Please try again later.';
                }
            }
        } catch (error) {
            console.error('Error deleting task:', error);
            alert('Error deleting task. Please try again later.');
        }
    });
}