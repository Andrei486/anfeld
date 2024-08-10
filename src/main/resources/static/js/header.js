document.addEventListener("DOMContentLoaded", function () {
    $.get("/api/user", function(data) {
        $("#user").text(data.name); // populate username in navbar
    });
});