function refreshCode() {
    $("#validCode").attr("src", "/code/getValidateCode?time=" + Math.random());
}

var flag = false;
function validateCode() {
    var val = $("#code").val();
    if (!(val == null || val.length == 0)) {
        $.ajax({
            url: "/code/validateCode?code=" + val,
            type: "get",
            async: false,
            dataType: "json",
            success: function (data) {
                if (data) {
                    $("#validCodeInfo").text("");
                    flag = true;
                    return data;
                } else {
                    $("#validCodeInfo").text("code is false");
                    return data;
                }
            }
        });
    }
}

function check() {
    var userName = $("#userName").val();
    var password = $("#password").val();
    var nickName = $("#nickName").val();
    var code = $("#code").val();

    if(userName == null || userName.length == 0) {
        $("#userNameInfo").text("userName is required!");
    } else {
        $("#userNameInfo").text("");
    }

    if(nickName == null || nickName.length == 0) {
        $("#nickNameInfo").text("nickName is required!");
    } else {
        $("#nickNameInfo").text("");
    }

    if(password == null || password == 0) {
        $("#passwordInfo").text("password is required!");
    } else {
        $("#passwordInfo").text("");
    }

    if(code == null || code.length == 0) {
        $("#validCodeInfo").text("code is required!");
    } else {
        if(flag) {

            $("#covalidCodeInfode").text("");
        }
    }
}



function enableSubmit() {
    var userName = $("#userName").val();
    var password = $("#password").val();
    var nickName = $("#nickName").val();
    var code = $("#code").val();
    if (userName != null && password != null && nickName != null && code != null && flag) {
        return true;
    }
    return false;
}
