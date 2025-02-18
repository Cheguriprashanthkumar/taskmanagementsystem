document.addEventListener("DOMContentLoaded", () => {
    const taskTableBody = document.querySelector("#taskTable tbody");
    const createTaskButton = document.getElementById("createTask");
    const logoutButton = document.getElementById("logout");
    const userSearchInput = document.getElementById("userSearch");
    const userList = document.getElementById("userList");

    const API_URL = "http://localhost:9065/api/v1/task"; // Update with your API URL
    const USER_API_URL = "http://localhost:9065/api/v1/user"; // API to search users
    const token = localStorage.getItem("token");

    // Logout functionality
    logoutButton.addEventListener("click", () => {
        localStorage.removeItem("token");
        localStorage.removeItem("role");
        window.location.href = "index.html";
    });

    // Fetch all tasks
    async function fetchTasks() {
        try {
            const response = await fetch(`${API_URL}/all`, {
                headers: {
                    "Authorization": `Bearer ${token}`
                }
            });

            if (response.ok) {
                const tasks = await response.json();
                renderTasks(tasks);
            } else {
                console.error("Error fetching tasks:", response.statusText);
            }
        } catch (error) {
            console.error("Error fetching tasks:", error);
        }
    }

    // Render tasks in the table
    function renderTasks(tasks) {
        taskTableBody.innerHTML = "";
        tasks.forEach(task => {
            const row = document.createElement("tr");

            row.innerHTML = `
                <td>${task.title}</td>
                <td>${task.description}</td>
                <td>${task.status}</td>
                <td>${task.priority}</td>
                <td>${task.assigneeId || "Unassigned"}</td>
                <td>
                    <button onclick="assignTask(${task.id})">Assign</button>
                    <button onclick="deleteTask(${task.id})">Delete</button>
                </td>
            `;

            taskTableBody.appendChild(row);
        });
    }

    // Create Task
    createTaskButton.addEventListener("click", async () => {
        const title = document.getElementById("taskTitle").value;
        const description = document.getElementById("taskDescription").value;
        const priority = document.getElementById("taskPriority").value;
        const assigneeId = document.querySelector("#userList li.selected")?.dataset.userId;

        if (!title || !description || !assigneeId) {
            alert("Please enter task title, description, and select a user.");
            return;
        }

        const newTask = { title, description, priority, assigneeId };

        try {
            const response = await fetch(`${API_URL}/create`, {
                method: "POST",
                headers: {
                    "Content-Type": "application/json",
                    "Authorization": `Bearer ${token}`
                },
                body: JSON.stringify(newTask)
            });

            if (response.ok) {
                alert("Task created successfully!");
                fetchTasks();
            } else {
                alert("Error creating task");
            }
        } catch (error) {
            console.error("Error creating task:", error);
        }
    });

    // Dynamic user search
    async function searchUsers() {
        const query = userSearchInput.value.trim();

        if (query.length < 2) {
            userList.innerHTML = '';
            return;
        }

        try {
            const response = await fetch(`${USER_API_URL}/search?query=${query}`, {
                headers: {
                    "Authorization": `Bearer ${token}`
                }
            });

            if (response.ok) {
                const users = await response.json();
                renderUserList(users);
            } else {
                console.error("Error searching users:", response.statusText);
            }
        } catch (error) {
            console.error("Error searching users:", error);
        }
    }

    // Render users in search results
    function renderUserList(users) {
        userList.innerHTML = '';
        users.forEach(user => {
            const listItem = document.createElement("li");
            listItem.textContent = `${user.firstName} ${user.lastName}`;
            listItem.dataset.userId = user.id;

            listItem.addEventListener("click", () => {
                document.querySelectorAll("#userList li").forEach(li => li.classList.remove("selected"));
                listItem.classList.add("selected");
            });

            userList.appendChild(listItem);
        });
    }

    // Assign Task to User
    window.assignTask = async (taskId) => {
        const userId = document.querySelector("#userList li.selected")?.dataset.userId;

        if (!userId) {
            alert("Please select a user first.");
            return;
        }

        try {
            const response = await fetch(`${API_URL}/assign/${taskId}?userId=${userId}`, {
                method: "PATCH",
                headers: {
                    "Authorization": `Bearer ${token}`
                }
            });

            if (response.ok) {
                alert("Task assigned successfully!");
                fetchTasks();
            } else {
                alert("Error assigning task");
            }
        } catch (error) {
            console.error("Error assigning task:", error);
        }
    };

    // Delete Task
    window.deleteTask = async (taskId) => {
        if (!confirm("Are you sure you want to delete this task?")) return;

        try {
            const response = await fetch(`${API_URL}/delete/${taskId}`, {
                method: "DELETE",
                headers: {
                    "Authorization": `Bearer ${token}`
                }
            });

            if (response.ok) {
                alert("Task deleted successfully!");
                fetchTasks();
            } else {
                alert("Error deleting task");
            }
        } catch (error) {
            console.error("Error deleting task:", error);
        }
    };

    // Fetch assigned tasks for logged-in user
    async function fetchAssignedTasks() {
        const userId = localStorage.getItem("userId");
        if (!userId) return;

        try {
            const response = await fetch(`${API_URL}/assigned/${userId}`, {
                headers: {
                    "Authorization": `Bearer ${token}`
                }
            });

            if (response.ok) {
                const tasks = await response.json();
                renderTasks(tasks);
            } else {
                console.error("Error fetching assigned tasks:", response.statusText);
            }
        } catch (error) {
            console.error("Error fetching assigned tasks:", error);
        }
    }

    // Initial fetch tasks on page load
    fetchTasks();
    fetchAssignedTasks();
});
