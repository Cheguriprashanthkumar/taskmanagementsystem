document.getElementById("loginBtn").addEventListener("click", async () => {
    const email = document.getElementById("loginEmail").value;
    const password = document.getElementById("loginPassword").value;

    const response = await fetch("api/v1/user/signin", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ email, password })
    });

    if (response.ok) {
        const data = await response.json();
        localStorage.setItem("token", data.token);
        localStorage.setItem("role", data.roleType);

        if (data.roleType === "ADMIN") {
            window.location.href = "admin.html";
        } else {
            window.location.href = "user.html";
        }
    } else {
        alert("Login failed. Check your credentials.");
    }
});

document.getElementById("signupBtn").addEventListener("click", async () => {
    const username = document.getElementById("signupUsername").value;
    const email = document.getElementById("signupEmail").value;
    const password = document.getElementById("signupPassword").value;
    const roleType = document.getElementById("signupRole").value;

    const response = await fetch("api/v1/user/signup", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ username, email, password, roleType })
    });

    if (response.ok) {
        alert("Signup successful. Please log in.");
    } else {
        alert("Signup failed. Try again.");
    }
});
