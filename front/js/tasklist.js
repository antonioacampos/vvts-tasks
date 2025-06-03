import { convertDate } from "./utils.js";

document.addEventListener('DOMContentLoaded', function() {

    addTaskButton();

    loadTasks();

});

async function addTaskButton() {

    const addTaskButton = document.getElementById('add-task-btn');

    addTaskButton.addEventListener('click', function() {

        const token = localStorage.getItem('tokenTaskVVTS');

        if (!token) {
            window.location.href = './index.html'; 
            return;
        }

        window.location.href = './createTask.html';

    });

}

async function loadTasks(){

    const token = localStorage.getItem('tokenTaskVVTS');

    if (!token) {
        window.location.href = './index.html'; 
        return;
    }

    try {
        const response = await fetch('http://localhost:8080/api/v1/task/get-all', {
            method: 'GET',
            headers:{
                'Authorization': `Bearer ${token}`,
                'Content-Type': 'application/json',
            }
        });
        if (!response.ok) {
            if (response.status === 401) {
                window.location.href = './index.html'; 
                return;
            } 
        }

        const tasks = await response.json();
        
        const taskList = document.getElementById('taskList');
        taskList.innerHTML = '';

        tasks.forEach(element => {
            const newTask = document.createElement('li');

            newTask.className = 'list-group-item';
            newTask.id = `task-${element.id}`;
            newTask.dataset.taskId = element.id;

            const title = document.createElement('h3');
            title.textContent = element.title || 'No Title';
            title.className = 'task-title';

            const description = document.createElement('p');
            description.textContent = element.description || 'No description provided.';
            description.className = 'task-description';

            const status = document.createElement('p');
            status.textContent = `Status: ${element.status}`;
            status.className = 'task-status';

            const deadline = document.createElement('p');
            deadline.textContent = `Deadline: ${convertDate(new Date(element.deadline))}`;
            deadline.className = 'task-deadline';
            
            newTask.appendChild(title);
            newTask.appendChild(description);
            newTask.appendChild(status);
            newTask.appendChild(deadline);

            newTask.addEventListener('click', function() {
                const taskId = this.dataset.taskId;
                sessionStorage.setItem('idTask', taskId);
                window.location.href = `./task.html`;
            });

            const line = document.createElement('hr');

            newTask.appendChild(line);

            taskList.appendChild(newTask);

        });

    } catch (error) {
        console.error('Error fetching tasks:', error);
        alert('Error fetching tasks. Please try again later.');
    }
}