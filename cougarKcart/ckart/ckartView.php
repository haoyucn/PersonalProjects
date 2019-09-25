<?php

    load_view('main');
    define('CKART_MODE_TEAM_SELECT','ckart_team'); 
    define('CKART_MODE_MAP','ckart_map'); 
    // Because mainView is loaded by functions in include/function.php(functions outside of this module),
    // the correct file path is needed.
    // Thus, change Dirpath outside of this module will result in a failer to load the view
    class ckartView extends mainView
    {
        public function load()
        {   
            $this->print_header();
            $this->load_javascript();
            $this->load_css();
            $this->print_status_bar();
            
            if($this->mode == CKART_MODE_MAP) 
            {
                $this->print_subsections();
            }
            else if ($this->mode == CKART_MODE_TEAM_SELECT) 
            {
                $this->print_teams_selection();
            }
                
            $this->print_footer();
        }
        
        /* Private Functions */
        
        private function load_css()
        {
            ?>
            <link rel="stylesheet" href="<?php echo CURRENT_DIR_PATH?>ckart.css">
            <?php
        }
        
        private function print_background_img()
        {
            
        }
        
        private function load_javascript()
        {
            $scriptFile = "team_select.js";
           
            if($this->mode == CKART_MODE_MAP) {
                $scriptFile = "section_button.js";
            }
            ?>
            <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.3.1/jquery.min.js"></script>
            <script src="<?php echo CURRENT_DIR_PATH . $scriptFile; ?>"></script>   
            <script src="<?php echo CURRENT_DIR_PATH; ?>map_checkpoints.js"></script>  
            <script src="https://code.jquery.com/jquery-1.12.4.min.js"></script>
            <script type="text/javascript" src="https://unpkg.com/sweetalert/dist/sweetalert.min.js"></script>
            <script>
                $(document).ready(function(){
                    $("#ranking").click(function(){
                        $("#scoreboard").animate({
                            width: "toggle"
                        });
                    });
                });
            </script>
            <?php
        }
        
        private function print_status_bar()
        {
            $period         = $this->model->challenge_period;
            $userChallenge  = $this->model->user_challenge;
            $team = $this->model->team_assigned;
            if ($team) {
                $userTeam = $this->model->get_user_team($team->team_id);
            }
            //echo json_encode($userChallenge);
//            $challenge      = $this->model->challenge;
            
            $dateStart      = date("Y-m-d", $period->date_start);
            $dateEnd        = date("Y-m-d", $period->date_end);
            $start          = new DateTime($dateStart);
            $end            = new DateTime($dateEnd);
            $totalDays      = get_days_between_two_dates($dateStart,$dateEnd); // didn't count the daystart, so ++ afterward
            $sundays        = intval($totalDays / 7) + ($start->format('N') + $totalDays % 7 >= 7); //count how many sunday it is
            $totalDays++;
            $totalDays      = $totalDays - $sundays;
            $currentTime    = get_current_time();
            $currentDate    = date("Y-m-d", $currentTime);
            
            // Days Remaining
            $daysRemaining = 0;
            if ($period->date_end > $currentTime) {
                
                $start = new DateTime($currentDate);
                $end = new DateTime($dateEnd);
                $daysRemaining = get_days_between_two_dates($currentDate, $dateEnd);
                $sundays = intval($daysRemaining / 7) + ($start->format('N') + $daysRemaining % 7 >= 7);
                $daysRemaining++;
                $daysRemaining = $daysRemaining - $sundays;
                
                if ($daysRemaining > $totalDays) {
                    $daysRemaining = $totalDays;
                }
            }
            
            // Status
            $status = "Incomplete";
            $statusClass = "text-incomplete";
            if ($userChallenge->completed) {
                $status = "Completed";
                $statusClass = "text-success";
            }
            
            $points = $userChallenge->points;
            $pointsAvailable = $this->model->total_available_points;
            
            $pointsToCompleteChallenge = $this->model->points_to_complete_challenge;
            $percentageCompleted = $this->model->get_total_points_percentage();
            ?>
            <script>
                var userteam_id = <?php echo $team->team_id?>;
                var team_data = <?php echo $this->model->team_data;?>;
            </script>
            <div class="container">
            <?php if ($period->has_not_started()) { ?>
                <div class="alert alert-info text-center mb-0">
                    Please note that the tracking period will begin when the challenge starts on <b><?php echo date("l, F jS", $period->date_start); ?></b>.
                </div>
                <br />
            <?php } else if ($period->is_during_grace_period()) { ?>
                <div class="alert alert-danger text-center mb-0">
                    The challenge has now ended. Please finish entering your data before <b><?php echo date("l, F jS", $period->date_report); ?></b>.
                </div>
                <br />
            <?php } else if ($period->is_closed()) { ?>
                <div class="alert alert-info text-center mb-0">
                    The challenge is now closed. Thank you for your participation.
                </div>
                <br />
            <?php } ?>
            </div>

            <div class="container">
                <div class="card">
                    <div class="card-body p-40">
                        <div class="row" id="challenge-stats">
                            <div class="col-md-4 text-center logo">
                                <?php if ($team) { ?>
                                <b><?php echo $userTeam->name ?></b><br />
                                
                                <img src="<?php echo IMAGES_DIR; ?>/<?php echo $userTeam->team_kart_img ?>" style="width: 180px; height: 90px;" />
                                <?php } else {
                                    ?>
                                    <img src="<?php echo IMAGES_DIR; ?>/2019_p4_ckart.png" style="width: 360px;" />
                                    <?php
                                } ?>
                            </div>
                            
                            <div class="col-md-4 text-center">
                                <p class="stats-label"><b>Points</b></p>
                                <p class="stats-value-number">
                                    <span id="total_points"><?php echo $points; ?></span> / <span id="points-to-complete-challenge"><?php echo $pointsToCompleteChallenge; ?></span>
                                </p>
                                <p class="stats-label">
                                    <span id="total_points_percentage"><?php echo $percentageCompleted; ?></span>% Completed
                                </p>
                            </div>
                            
                            <div class="col-md-4 text-center">
                                <p class="stats-label">
                                    <b>Status</b>
                                    <span id="challenge-status" class="<?php echo $statusClass; ?> stats-value-text ml-4"><?php echo $status; ?></span>
                                </p>
                                
                                <p class="stats-label">
                                    <b>Days Remaining</b>    
                                    <span class="stats-value-number ml-4"> <?php echo $daysRemaining; ?></span>
                                </p>
                                    
                                <br />
                            </div>
                        </div>
                    </div>
                </div>
            </div>
            <br/>
            <?php
        }
        
        private function print_subsections()
        {
            $teams = $this->model->ckart_teams;
            $team = $this->model->team_assigned;
            if ($team) {
                $userTeam = $this->model->get_user_team($team->team_id);
            }
            ?>
            
                
            <div class="container">
                <div class="row">
<!--                    <div class="col"></div>-->
                    <div class="col text-right">                        
                        <button id="btn-instruction" type="button" class="btn btn-success btn-md section_btn" onclick="show_section('instructions','btn-instruction');">Challenge Instructions</button>
                        
                        <?php if(!empty($this->model->team_assigned)) { ?>
                            <button id="btn-map" type="button" class="btn btn-success btn-md section_btn" onclick="show_section('map','btn-activity');">Maps</button>
                            <button id="btn-ranking" type="button" class="btn btn-success btn-md section_btn" onclick="show_section('ranking','btn-ranking');" >Rankings</button>
                            <button id="btn-tracker" type="button" class="btn btn-success btn-md section_btn btn_selected" onclick="show_section('tracker','btn-tracker');" >Daily Tracker</button>
                        <?php } ?>
                    </div>
                </div>
            </div>

            <br />

            <div class="container">
                <div class="card">
                    <div class="card-body p-50 text-center">
                        
                        <section id="instructions-section" style="display:none;">
                           <h2 class="text-center">Challenge Instructions</h2>
                            
                            <?php $this->print_challenge_instructions(); ?>
                            <br />
                        </section>
                        
                        <section id="tracker-section" style="display:none;">
                            <h2 class="text-center">Tracker</h2>
                            
                            <?php $this->print_detail_tracker();?>
                        </section>

                        <section id="ranking-section" style="display:none;">
                            <h2 class="text-center">Ranking<span style="font-size: 20px;"> - Top 10 racers for each team<span></h2>
                            <?php $this->print_ranking(); ?>
                        </section>
                        
                        <section id="map-section" style="display:block; margin-left: 25px;">
                            <div class="container">
                                <div class="row">
                                    <a id="ranking"><img src="<?php echo IMAGES_DIR; ?>/banner-ranking.png" style="height:80px;" /></a>
                                    <script>var img_dir = '<?php echo IMAGES_DIR; ?>'; </script>
                                    <div class = "row" id="scoreboard">       
                                        <img src="<?php echo IMAGES_DIR; ?>/<?php echo $userTeam->team_kart_img ?>" style="width: 180px; height: 90px;" />
                                    </div>
                                           
                                        
                                                 

                                </div>
                                <div class="row">
                                    <div>
                                        <a class="map-byu"><img src="<?php echo IMAGES_DIR; ?>/banner-byu.png" style="width: 250px;" /></a>
                                        <a class="map-parkcity"><img src="<?php echo IMAGES_DIR; ?>/banner-parkcity.png" style="width: 250px;" /></a>
                                        <a class="map-moab"><img src="<?php echo IMAGES_DIR; ?>/banner-moab.png" style="width: 250px;" /></a>
                                        <a class="map-provobeach"><img src="<?php echo IMAGES_DIR; ?>/banner-provobeach.png" style="width: 250px;" /></a>
                                    </div>
                                    <img id="change-image" onclick="showCoords(event)" src="<?php echo IMAGES_DIR; ?>/map-byu.png" style="width: 1000px; height: 1000px;" />
                                    <?php foreach ($teams as $team) {?>
                                        <img src="<?php echo IMAGES_DIR; ?>/<?php echo $team->team_token_img ?>" class="tean_icon" id = "team_icon_<?php echo $team->id;?>" style="height: 160px; " />
                                    <?php }?>
                                    <span id = "dot">&#183;</span> 
                                </div>

                            </div>
                        </section>
                    </div>
                </div>
            </div>
            <?php
        }
        
        private function print_teams_selection()
        {
            $ckartTeams = $this->model->ckart_teams;
            ?>
            <div class="container">
                <div class="card">
                    <div class="card-body p-50 text-center">
                        <div class="text-center" style="font-family: 'Press Start 2P', cursive; font-size: 24px; color: #F3BB41;">
                            Select Your Team
                            <br /><br />
                            <div class="col-md-8" style="margin: 0 auto;">
                                <div style="font-size: 18px;" class="row">
                                    <?php foreach ($ckartTeams as $team) { ?>
                                        <div class="col-md-6">
                                            <input type="radio" id="team<?php echo $team->id + 1 ?>" name="ckart_team" value="<?php echo $team->id; ?>">
                                            <label for="team<?php echo $team->id + 1 ?>">Team<?php echo $team->id + 1; ?><br /><img id="change-image" src="<?php echo IMAGES_DIR; ?>/<?php echo $team->team_kart_img ?>" style="width: 200px;" /></label><br />
                                        </div>
                                    <?php } ?>   
                                </div>
                            </div>
                            <br />
                            <br />
                            <div>
                                <label>Alias:</label><input type="text" id="aliasbox" value="">
                                <!-- <span id="aliasResult"></span> -->
                                <p style="color: black; font-size: 10px;" id="aliasResult">If you want to use your name, just leave the box as blank.</p>
                            </div>
                            <br />
                            <div>
                                <button id="SubmitBtn" class="btn btn-success" onclick="teamSubmit()">Submit</button>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
            <?php
        }
        
        private function print_ranking()
        {
            $teams = $this->model->ckart_teams;
            $rankings = $this->model->get_ranking();
            $this->model->get_user_score_per_map();
            $post_fix = array();
            $post_fix[0] = 'st';
            $post_fix[1] = 'nd';
            $post_fix[2] = 'rd';
            $post_fix[3] = 'th';
            ?>
            <nav>
                <div class="nav nav-tabs" id="nav-tab" role="tablist">
                    <a class="nav-item nav-link" id="nav-0-tab" data-toggle="tab" href="#nav-map-0" role="tab" aria-controls="nav-map-0" aria-selected="false">Campus Dr</a>
                    <a class="nav-item nav-link" id="nav-1-tab" data-toggle="tab" href="#nav-map-1" role="tab" aria-controls="nav-map-1" aria-selected="true">Park City</a>
                    <a class="nav-item nav-link" id="nav-2-tab" data-toggle="tab" href="#nav-map-2" role="tab" aria-controls="nav-map-2" aria-selected="false">Arches</a>
                    <a class="nav-item nav-link active" id="nav-3-tab" data-toggle="tab" href="#nav-map-3" role="tab" aria-controls="nav-map-3" aria-selected="false">Provo Beach</a>
                </div>
            </nav>
            <div class="tab-content" id="nav-tabContent">
                <?php
                for($map_index = 0; $map_index < count($rankings); $map_index++) {
                    $map_ranking = $rankings[$map_index];
                    $user_score = $this->model->get_user_score_by_map($map_index);
                    ?>
                    <div class="tab-pane fade <?php if ($map_index == 3) {echo "show active";} ?>" id="nav-map-<?php echo $map_index;?>" role="tabpanel" aria-labelledby="nav-<?php echo $map_index;?>-tab">
                        <br/>
                        <h3 class="scoreboard text-left">Your score: <?php echo $user_score;?> </h3> 

                        <?php
                        for($teamIndex  = 0; $teamIndex < count($map_ranking); $teamIndex++) {
                            $teamId = $map_ranking[$teamIndex][0];
                            $userRanking = $map_ranking[$teamIndex][1];
                            $avgScore = $map_ranking[$teamIndex][2];
                            ?>
                            <div class="row border" style="margin-left: 0px; margin-right: 0px;">
                            
                                <div class = "col-3">
                                    <img src="<?php echo IMAGES_DIR; ?>/<?php echo $teams[$teamId]->team_kart_img ?>" style="width: 180px; height: 90px;"/>
                                    <h3> <?php echo $teamIndex + 1 . $post_fix[$teamIndex] . " place";?> </h3>
                                    <h6>Racers: <?php echo $teams[$teamId]->member_count;?> | Average: <?php echo $avgScore;?> pts</h6>
                                </div>
                                <div class = "col-9">
                                    <div class ="row">
                                    <div class="col-6 border-left" style="margin-left: 0px; margin-right: 0px; min-height: 100%;">
                                        <div class="row border-bottom">
                                            <div class="col-2"><b>#</b></div>
                                            <div class="col-7"><b>Alias</b></div>
                                            <div class="col-3"><b>Points</b></div>
                                        </div>
                                    
                                        <?php
                                        for ($i = 0; $i < count($userRanking); $i++) {
                                            // $rank = 1;
                                            $userData = $userRanking[$i];
                                            $alias = $userData[0];
                                            $score = $userData[1];
                                            if ($i == 5) {
                                                ?>
                                                </div>
                                                <div class="col-6 border-left" style="margin-left: 0px; margin-right: 0px; min-height: 100%;">
                                                    <div class="row border-bottom">
                                                        <div class="col-2"><b>#</b></div>
                                                        <div class="col-7"><b>Alias</b></div>
                                                        <div class="col-3"><b>Points</b></div>
                                                    </div>
                                                <?php
                                                if ($i == 9 ) {
                                                    break;
                                                }
                                            }
                                            ?>
                                            <div class="row">
                                                <div class="col-2"><?php echo $i +1;?></div>
                                                <div class="col-7"><?php echo $alias;?></div>
                                                <div class="col-3"><?php echo $score;?></div>
                                            </div>
                                            <?php
                                        }
                                        ?>
                                    </div></div>
                                </div>
                            </div>
                            <?php
                        }
                        ?>
                    </div>
                    <?php
                }
                ?>
            </div>
            <?php
        }
        
        private function print_tracker()
        {
            ?>


            <?php
        }
        
        private function print_challenge_instructions()
        {
            ?>
            <div class="text-left">
            <br />
            <br />            
            <br /><h6><b>GOAL: </b> <span >The goal of this challenge is to be more active throughout the day and to have fun doing it through team competition.  </span> </h6>
                
            <h6><b>MORE DETAILS: </b> </h6>
            <ul style="margin-top: 10px; margin-bottom: 10px;">
                <li><h6 class="mb-0">Before beginning the race you will need to select a team and create an alias name for yourself.</h6></li>
                <li><h6 class="mb-0">Beginning on July 1st, strive to get at least 10,000 steps a day, and record them in the tracker, to help your team to victory!  If you fall short, no worries, just keep finding new ways to add more activity to your day and try to improve throughout the challenge.</h6></li>
                <li><h6 class="mb-0">THIS IS IMPORTANT – Be sure to record your steps EACH DAY to help move your team’s car along the track. The winner for each two week segment is not the team with the most steps at the end of the challenge, but rather the team that crosses the finish line first during each two week segment (or is in the lead at the end of a two week segment)!  If you wait until the end of the challenge to record your steps you will have missed out on helping your team.  </h6></li>
                <li><h6 class="mb-0">You earn 1 point for every 1000 steps you take.  To receive credit for successfully completing this challenge you will need to personally earn a minimum of 405 points by August 31. </h6></li>
                <li><h6 class="mb-0">There is a leader board to show the top 10 participants for each team.</h6></li>
                <li><h6 class="mb-0">Racers…. On your mark. Get set. Go!</h6></li>
            </ul>
            </div>
            <?php
        }
        
        private function print_detail_tracker()
        {
            $userChallenge = $this->model->user_challenge;
            $period = $this->model->challenge_period;
            
            $dateStart = date("Y-m-d", $period->date_start);
            $dateEnd = date("Y-m-d", $period->date_end);
            $totalDays = get_days_between_two_dates($dateStart, $dateEnd);
            
            ?>
            <br />
            <h4 class="text-center">Please record your steps everyday, to help your team cross the finish line first.</h4>
            <br />
            <table class="table tracking-table" style="font-size: 0.9em;">
                <theader>
                    <tr>
                        <th class="text-right" >Day</th>
                        <th >Date</th>
                       
                        <th class= "text-center">Exercise Type</th>
                        <th class= "text-center">Amount</th>
                        <th>Points</th>
                        <th style = "width:120px;"></th>
                    </tr>
                </theader>
                <tbody>
                <?php

                    $currentWeek = 1;
                    $currentTime = get_current_time();
            
                ?>
                <?php for ($i = 0; $i <= $totalDays; $i++) { ?>
                <?php
                    // if ($currentWeek == 9) {
                    //     continue;
                    // }
                    $newDate = strtotime($dateStart . " + " . $i . " days");
                    $dayOfTheWeek = date("N", $newDate);

                    if ($dayOfTheWeek == 1) {
                        ?>
                        <?php if ($currentWeek > 1) { ?>
                    <tr>
                        <td colspan="6"><br /></td>
                    </tr>
                    <?php } ?>
                    <?php if ($currentWeek == 9) { break;} ?>
                    <tr class="week-row">
                        <td colspan="6" style="">
                            <b>Week <?php echo $currentWeek; ?></b>
                        </td>
                    </tr>
                        <?php
                        $currentWeek++;    
                    }

                    if ($dayOfTheWeek != 7) { 
                        
                        $day = date("l", $newDate);
                        $date = date("F jS", $newDate);
                        $challengeGoalChecked = "";
                        $unitValue = "";
                        $unit = 0;
                        $points = 0;
                        
                        $disabled = "";
                        if ($period->is_closed() || $currentTime < $newDate) {
                           $disabled = "disabled";
                        }
                        
                        $dayId = date("Ymd", $newDate);
                        
                        if ($dayData = $this->model->get_tracking_day($dayId)) {
                                            
                            $unitValue = $dayData['unit_value'];
                            if (!is_numeric($unitValue)) {
                                $unitValue = 0;
                            }
                                
                            if ($dayData['unit'] == "1") {
                                $unit = 1;
                            }
                            else if ($dayData['unit'] == "2") {
                                $unit = 2;
                            }
                            
                            $points = $this->model->get_tracking_day_points($dayData);
                        }
                        
                    ?>
                    <tr>
                        <td class="text-right"><?php echo $day; ?>,</td>
                        <td><?php echo $date; ?></td>
                               
                        <td>
                                <div class="col">
                                    <?php # exercise type
                        
                                        $options = array(2 => "Steps");
                        
                                    ?>
                                    <select class="form-control custom-select exercise-unit" <?php echo $disabled; ?> id="unit_<?php echo $dayId; ?>">
                                        <?php foreach ($options as $key => $value) { ?>
                                        <?php $checked = ($key == $unit) ? "selected" : ""; ?>
                                        <option <?php echo $checked; ?> value="<?php echo $key; ?>"><?php echo $value; ?></option>
                                        <?php } ?>
                                    </select>
                                </div>
                            </div>
                        </td>
                        <td>
                            <div class="form-row">
                                <div class="col">
                                    <input type="text" class="form-control text-center custom-input exercise-goal" <?php echo $disabled; ?> placeholder="" maxlength="5" min="1" max="99999" id="unitValue_<?php echo $dayId; ?>" value="<?php echo $unitValue; ?>">
                                </div>
                        </td>
                        <td class="text-center text-success">+<span id="points_<?php echo $dayId; ?>"><?php echo $points; ?></span></td>
                        <td class="text-center">
                            <span style="font-size: 0.9em" class="text-success" id="savingLabel_<?php echo $dayId; ?>"></span>
                        </td>
                    </tr>
                    <?php } ?>
                <?php } ?>
                </tbody>
            </table>

            <input type="hidden" id="tracker-challenge-period-id" value="<?php echo $period->id; ?>">

            <?php
        }
       
    }
?>