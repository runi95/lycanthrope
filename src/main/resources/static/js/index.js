function setNicknameFromInputField() {
    setNickname($("#nickname").val());

    $.ajax({
        url: '/boards',
        type: "GET",
        success: function(result) {
            changeView(result);
            getBoards();
        },
        error: function(error) {
            console.log("Error: " + error);
        }
    });
}