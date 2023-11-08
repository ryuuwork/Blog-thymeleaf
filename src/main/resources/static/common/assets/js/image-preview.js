$("#file").change(function () {
    const fileSize = this.files[0].size;
    if (fileSize > 1048576) {
        this.setCustomValidity("You must choose ....")
        this.reportValidity();
    } else {
        this.setCustomValidity("")
        showImagePreview(this);
    }
});

function showImagePreview(fileInput) {
    const file = fileInput.files[0];
    const reader = new FileReader();
    reader.onload = function (event) {
        $("#image-preview").attr("src", event.target.result)
    };
    reader.readAsDataURL(file);
}

function checkPasswordMatch() {
    const newPassword = document.getElementById("newPassword")
    const confirmPassword = document.getElementById("confirmPassword")
    confirmPassword.setCustomValidity(newPassword.value !== confirmPassword.value ? 'Password not match' : '');

    const currentPassword = document.getElementById("currentPassword")
    const currentPasswordError = document.getElementById("currentPasswordError")
    if (currentPassword.length < 8 || currentPassword.length > 20) {
        currentPasswordError.innerHTML = "Password length must be between 8 and 20 characters.";
    } else {
        currentPasswordError.innerHTML = "";
    }
}

function showPasswordInForm() {
    const password = document.getElementById("password");
    const iconShowPassword = document.querySelector("#showPasswordForm i");
    password.type = password.type === "password" ? "text" : "password";
    iconShowPassword.className = password.type === "password" ? "fas fa-eye" : "fas fa-eye-slash";
}

function activeInputFile(event) {
    event.preventDefault();
    $("#file").click();
}

document.getElementById("resetButton").addEventListener("click", function (event) {
    event.preventDefault();
    window.location.href = "/admin/users";
});


