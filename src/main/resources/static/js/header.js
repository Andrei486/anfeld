document.addEventListener("DOMContentLoaded", function () {
    $.get("/user", function(data) {
        $("#user").text(data.name); // populate username in navbar
    });
});