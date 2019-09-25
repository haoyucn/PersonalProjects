function show_section(section_name, btn_id)
{
    hide_all_sections();
    $('#' + section_name + '-section').show();
    $('.section_btn').removeClass("btn_selected"); 
    $('#' + btn_id).addClass("btn_selected"); 
    
}

function hide_all_sections()
{
    $('#map-section').hide();
    $('#tracker-section').hide();
    $('#instructions-section').hide();
    $('#ranking-section').hide();
}
var viewing_map = 0;

$(document).ready(function($)
{
    $('.map-byu').on({
        'click': function(){
            viewing_map = 0;
            $('#change-image').attr('src','/hm/content/images/map-byu.png');
            update_current_map(viewing_map);
        }
    });

    
    $('.map-parkcity').on({
        'click': function(){
            viewing_map = 1;
            $('#change-image').attr('src','/hm/content/images/map-parkcity.png');
            update_current_map(viewing_map);
        }
    });
    
    $('.map-moab').on({
        'click': function(){
            viewing_map = 2;
            $('#change-image').attr('src','/hm/content/images/map-moab.png');
            update_current_map(viewing_map);
        }
    });

    $('.map-provobeach').on({
        'click': function(){
            viewing_map = 3;
            $('#change-image').attr('src','/hm/content/images/map-provobeach.png');
            update_current_map(viewing_map);
        }
    });
    $('.map-provobeach').click();

    var o = $('#change-image').position();
    imageX = o.left;
    imageY = o.top;
    update_current_map(viewing_map);
});
var imageX = 0;
var imageY = 0;


var points = [];
function showCoords(event) {

    var o = $('#change-image').offset();
    var ox = o.left;
    var oy = o.top;
   
    var x = event.pageX;
    var y = event.pageY;
    var dx = x - ox;
    var dy = y - oy;
    var coords = "X coords: " + dx + ", Y coords: " + dy;
    points[points.length] = [dx,dy];
    console.log(dx,dy);

}

function update_banner(viewing_map){
    var indexes = sort_team_score_by_viewing_map(viewing_map);
    
    $('#scoreboard').empty();
    for(var i = 0; i < 4; i++) {
        $('#scoreboard').append('<img src="' + img_dir +'/kart-' + indexes[i] + '.png" style="width: 180px; height: 81px;" />');
        
    }
}

function sort_team_score_by_viewing_map(map_num) {
    
    var scores = team_data[map_num];
    
    var scores_with_index = [];
    for (var i in scores) {
        scores_with_index.push([scores[i], i]);
    }
    scores_with_index.sort(function(left, right) {
        return left[0] > right[0] ? -1 : 1;
    });
    var indexes = [];
    for (var j in scores_with_index) {
        indexes.push(scores_with_index[j][1]);
    }
    return indexes;
}


$(function() {
    $( ".exercise-goal" ).keyup(function() {
        
        update_exercise(this.id);
        
    }),
   
    $( ".exercise-unit" ).change(function() {
        update_exercise(this.id);
    })
});

function get_day_id(container_id)
{
    var containerSplit = container_id.split('_');
    if (containerSplit.length == 2) {
        return containerSplit[1];
    }
    return -1;
}



function update_exercise(container_id)
{
    let dayId = get_day_id(container_id);
    save_tracking_day(dayId);
}



function save_tracking_day(day_id)
{
    var periodId        = $('#tracker-challenge-period-id').val();
    var unitValue       = $('#unitValue_' + day_id).val().trim();
    var unit            = $('#unit_' + day_id).val();

    
    if (unitValue != "" && isNaN(unitValue)) {
        if (unit == 2) {
            alert("Please enter a valid number of steps.");
        }
    }
    else {
        $('#savingLabel_' + day_id).finish().show();
        $('#savingLabel_' + day_id).text('Saving...'); 
        $.post("index.php?module=ckart&period_id=" + periodId + "&action=save_exercise_detailed_tracker_day", {
            dayId: day_id,
            unit: unit,
            unitValue: unitValue
        },
        function (result) {

            resultContent = JSON.parse(result);
   
            if (resultContent && resultContent.message == "SUCCESS") {
                
                let points = resultContent.points;
                let totalPoints = parseInt(resultContent.totalPoints);
                let totalPointsPercentage = resultContent.totalPointsPercentage;
                
                $('#points_' + day_id).text(points);
                update_user_team_score(periodId,day_id,totalPoints,totalPointsPercentage);
                
                
            }
            else {
               alert("An error occured. Please try again or refresh this page.");
            }
            
            $('#savingLabel_' + day_id).fadeOut(3000);
        });
   }
}


function update_user_team_score(periodId, day_id, totalPoints,totalPointsPercentage) {
    var total = $('#total_points').text();
    var score_change = totalPoints - total;

    var year = Math.floor(day_id / 10000);
    var month = Math.floor(day_id / 100) - year * 100;
    var day  = day_id - year * 10000 - month * 100;
    var day_str = year + "-" + month + "-" + day;
    var dt = new Date(day_str);
    var week_id = get_map_by_time(dt.getTime());
    
    $.post("index.php?module=ckart&period_id=" + periodId + "&action=update_team_score",
    {
        day_id: day_id,
        week_id : week_id,
        score_change : score_change
    },
    function (result) {
        console.log(result);
        
        var userTeamScore = parseInt(result);
        team_data[week_id][userteam_id] = userTeamScore;
        update_team_position(userteam_id, userTeamScore, week_id);
    });

    update_dashboard(totalPoints, totalPointsPercentage);
}

function is_currentMap(day_id){
    var year = Math.floor(day_id / 10000);
    var month = Math.floor(day_id / 100) - year * 100;
    var day  = day_id - year * 10000 - month * 100;
    var dt = new Date(year + "-" + month + "-" + day);
    var day_id_time  = dt.getTime();

    var today = new Date();
    var today_time = dt.getTime();
    
    var day_id_map = get_map_by_time(day_id_time);
    if (day_id_time != get_map_by_time(today_time) || day_id_map == -1) {
        return false;
    }
    return true;
}

function update_current_map(map_id) {
    
    var data = team_data[map_id];
    console.log("data: " + data);
    for (var i = 0; i < 4; i++) {
        var score = data[i];
        update_team_position(i, score, map_id);
    }
    update_banner(map_id); 
}
var userTeam = "team_1";
var currentMap = "map_1";
function update_team_position(team_id, teamScore, map_id){      
    //cal team progress
    var stepLength = 3;
    if (map_id != 0 ) {
        stepLength = 1.5;
    }
    var checkpointMade = Math.floor(teamScore / stepLength);
    console.log("team: " + team_id);
    console.log("check point made: " + checkpointMade); //check point says 20, map only has 19!?
    
    if (map_id != 0 && checkpointMade >= 39) {
        checkpointMade = 0;
    }
    else if (map_id == 0 && checkpointMade >= 18) { //messing w/ this... ==  to >= ?\yup that works!!
        checkpointMade = 0;
    }
    
    move_team_icon_to_check_point(team_id, checkpointMade, map_id);
}

function update_dashboard(total_points, total_points_percentage)
{
    let pointsToCompleteChallenge = $('#points-to-complete-challenge').text();
    
    $('#total_points').text(total_points);
    
    if ($('#total_points_percentage').text() != 0 && total_points_percentage == 100 && $('#total_points_percentage').text() != 100) {
        swal("Good job!", "You have now complete the challenge!", "success");
    }
    
    $('#total_points_percentage').text(total_points_percentage);

    if (total_points >= pointsToCompleteChallenge) {
        $('#challenge-status').text("Completed");
        $('#challenge-status').removeClass('text-incomplete').addClass('text-success');
    }
    else {
        $('#challenge-status').text("Incomplete");
        $('#challenge-status').addClass('text-incomplete').removeClass('text-success');
    }
}


var map_dates = [
    ["2019-7-1" , "2019-7-14"],
    ["2019-7-15", "2019-7-28"],
    ["2019-7-29", "2019-8-11"],
    ["2019-8-12", "2019-8-25"]
];

function get_map_by_time(t_time) {
    for (var i = 0; i < map_dates.length; i++) {
        var dt_1  = new Date(map_dates[i][0]);
        var dt_2 = new Date(map_dates[i][1]);
        if (t_time <= dt_2.getTime() && t_time >= dt_1.getTime()) {
            return i;
        }
    }
    return -1;
}


function move_team_icon_to_check_point(team_id, checkPointNum, map_id) {
    console.log("coords: " + imageY + " " + imageX + "checkpoint: " + checkPointNum);
    //console.log("yo: " + checkPointNum); //20 is too big for cp (except for the 2nd map), should be < 19
    var offx = map_points[map_id][checkPointNum][0]; // this is undefined bc of the `0` somehow?
    //console.log(offx);
    var offy = map_points[map_id][checkPointNum][1];
    var shift = get_shift(team_id);
    $('#team_icon_' + team_id).css({position: "absolute", top: imageY + offy + shift[0] - 155 + 'px', left: imageX + offx + shift[1] - 40 + 'px'});
    $('#dot').css({position: "absolute", top: imageY + offy + 'px', left: imageX + offx + 'px'});
}


function get_shift(team_id) {
    switch(team_id) {
        case 0:
            return [-6,-6];
        case 1: 
            return [-3, -3];
        case 2:
            return [3,3];
        case 3:
            return [6,6];
        default:
            [0,0];
    }
}

