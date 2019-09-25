$(function() {
    $( "#aliasbox" ).keyup(function() {
        if (this.value != "") {
            aliasCheck(this.value);
        }
    })
});

function teamSubmit()
{
    let teamID;
    let alias;

    teamID = $("input[name='ckart_team']:checked").val();
    alias = $("#aliasbox").val();

    if(teamID) {
        $.post("index.php?module=ckart&action=register_alias", {
            alias: alias
        },
        function(result){
            console.log("Alias: " + result);
            if (result == 'SUCCESS') {
                
                $.post("index.php?module=ckart&action=team_register", {
                        teamID: teamID,
                    },
                    function (result) {
                        console.log("team: " + result);
                        if (result == 'SUCCESS') {
                            
                            alert("You are successfully registered for Team" + (parseInt(teamID) + 1));
                            refresh_page();
                        }
                        else {
                            alert('An error has occured.');
                        }
                    console.log(result);
                    }
                );
            }
        })
    }
    else {
        alert("You have to select a team.");
    }
}

function aliasCheck(alias)
{   
    console.log(alias);
    $.post("index.php?module=ckart&action=check_alias", {
        alias: alias   
    },
    function(result) {
        console.log(result);
        if(result == 'SUCCESS') {
            $("#aliasResult").text("You can use this name!");
            $("#aliasResult").css("color", "green");
            $("#SubmitBtn").attr("disabled", false);
        }
        else {
            $("#aliasResult").text("The name is already taken!");
            $("#aliasResult").css("color", "red");
            $("#SubmitBtn").attr("disabled", true);
        }
    })
}