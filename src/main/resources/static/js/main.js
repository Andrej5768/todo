// Selectors
const toDoInput = document.querySelector('.todo-input');
const toDoBtn = document.querySelector('.todo-btn');
const toDoList = document.querySelector('.todo-list');

// Event Listeners
toDoBtn.addEventListener('click', addToDo);
toDoList.addEventListener('click', deleteCheck);
toDoList.addEventListener('click', editTaskText);
document.addEventListener("DOMContentLoaded", getTodos);

// Constants
const API_URL = '/api/tasks';

// Functions
async function addToDo(event) {
    event.preventDefault();
    const taskText = toDoInput.value.trim();

    if (taskText === '') {
        alert("You must write something!");
        return;
    }

    try {
        const taskData = await sendCreateTaskRequest(taskText);
        const newTask = createTaskElement(taskText, taskData.id);

        toDoList.appendChild(newTask);
        toDoInput.value = '';
    } catch (error) {
        console.error(error);
    }
}

function deleteCheck(event) {
    const item = event.target;
    if (item.classList.contains('delete-btn')) {
        const task = item.parentElement;
        task.classList.add("fall");
        removeLocal(task);
        task.addEventListener('transitionend', function () {
            task.remove();
        });
    }
    if (item.classList.contains('check-btn')) {
        item.parentElement.classList.toggle("completed");
    }
}

function editTaskText(event) {
    const item = event.target;
    if (item.classList.contains('edit-btn')) {
        const task = item.parentElement;
        const taskTextElement = task.querySelector('.todo-item');
        const newText = prompt("Edit task:", taskTextElement.innerText);
        if (newText !== null) {
            taskTextElement.innerText = newText;
            updateLocal(task, newText);
        }
    }
}

function createTaskElement(text, id) {
    const taskDiv = document.createElement("div");
    taskDiv.classList.add('todo');

    taskDiv.dataset.id = id;

    const newTask = document.createElement('li');
    newTask.innerText = text;
    newTask.classList.add('todo-item');
    taskDiv.appendChild(newTask);

    const checkButton = document.createElement('button');
    checkButton.innerHTML = '<i class="fas fa-check"></i>';
    checkButton.classList.add('check-btn');
    taskDiv.appendChild(checkButton);

    const editButton = document.createElement('button'); // Кнопка "Изменить"
    editButton.innerHTML = '<i class="fas fa-edit"></i>';
    editButton.classList.add('edit-btn');
    taskDiv.appendChild(editButton);

    const deleteButton = document.createElement('button');
    deleteButton.innerHTML = '<i class="fas fa-trash"></i>';
    deleteButton.classList.add('delete-btn');
    taskDiv.appendChild(deleteButton);

    // Adding an event handler for the "Edit" button
    editButton.addEventListener('click', function () {
        newTask.contentEditable = true; // Allowing text editing
        newTask.focus(); // Setting focus to the text field
        editButton.style.display = 'none'; // Hiding the "Edit" button
        deleteButton.style.display = 'none'; // Hiding the "Delete" button
        checkButton.style.display = 'none'; // Hiding the "Complete" button
        saveButton.style.display = 'inline'; // Showing the "Save" button
    });

    const saveButton = document.createElement('button'); // The "Save" button
    saveButton.innerHTML = '<i class="fas fa-save"></i>';
    saveButton.classList.add('save-btn');
    saveButton.style.display = 'none'; // Initially hiding the "Save" button
    taskDiv.appendChild(saveButton);

    // Adding an event handler for the "Save" button
    saveButton.addEventListener('click', function () {
        newTask.contentEditable = false; // Disabling text editing
        editButton.style.display = 'inline'; // Showing the "Edit" button
        deleteButton.style.display = 'inline'; // Showing the "Delete" button
        checkButton.style.display = 'inline'; // Showing the "Complete" button
        saveButton.style.display = 'none'; // Hiding the "Save" button
        const newText = newTask.innerText.trim();
        if (newText === "") {
            alert("Task text cannot be empty.");
            newTask.innerText = text; // Restoring the original text
        } else if (newText !== text) {
            updateLocal(taskDiv, newText);
        }
    });

    return taskDiv;
}

function getTodos() {
    fetch(API_URL)
        .then(response => {
            if (response.ok) {
                return response.json();
            } else {
                throw new Error('Failed to fetch tasks');
            }
        })
        .then(data => {
            data.forEach(task => {
                console.log(task);
                const newTask = createTaskElement(task.description, task.id);
                toDoList.appendChild(newTask);
            });
        })
        .catch(error => {
            console.error(error);
        });
}

function removeLocal(task) {
    const taskId = task.dataset.id;
    sendDeleteTaskRequest(taskId);
}

function updateLocal(task, newText) {
    const taskId = task.dataset.id;
    sendUpdateTaskTextRequest(taskId, newText);
}

async function sendDeleteTaskRequest(taskId) {
    try {
        const response = await fetch(`${API_URL}/${taskId}`, {
            method: 'DELETE',
        });

        if (!response.ok) {
            throw new Error('Failed to delete task');
        }
    } catch (error) {
        console.error(error);
    }
}

async function sendUpdateTaskTextRequest(taskId, newText) {
    try {
        const response = await fetch(`${API_URL}/update/${taskId}`, {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({text: newText}),
        });

        if (!response.ok) {
            throw new Error('Failed to update task text');
        }

    } catch (error) {
        console.error(error);
        throw error;
    }
}

async function sendCreateTaskRequest(taskText) {
    try {
        const response = await fetch(API_URL, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({text: taskText}),
        });

        if (!response.ok) {
            throw new Error('Failed to create task');
        }

        return response.json();
    } catch (error) {
        console.error(error);
        throw error;
    }
}
