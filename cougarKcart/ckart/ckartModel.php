<?php
    define('DAILY_CHALLENGE_STEPS_PER_POINT', 1000);
    //define('DAILY_CHALLENGE_MAX_POINTS_EXERCISE', 20);
    define('WEEKLY_CHALLENGE_POINTS_PER_DAY', 1);

    load_dao('challengePeriod');
    load_dao('challenge');
    load_dao('challengeGoal');
    load_dao('userChallenge');
    load_dao('incentive');
    load_dao('challengeActivity');
    require_once("application/module/ckart/ckartTeamDAO.php");
    require_once("application/module/ckart/ckartTeamScoreDAO.php");
    require_once("application/module/ckart/ckartAssignmentDAO.php");
    require_once("application/module/ckart/ckartUserWeekScoreDAO.php");
    require_once("application/daos/challengeAliasDAO.php");

    class ckartModel {
        
        public $challenge_period = NULL;
        public $user_challenge = NULL;
        public $tracking_data = array();
        public $total_available_points = 0;
        public $points_to_complete_challenge = 0;
        public $ckart_teams = NULL;
        public $team_assigned = NULL;
        public $user_team = NULL;
        public $challenge = NULL;
        public $total_points = 0;
        public $team_data = NULL;
        public $user_scores_per_map = NULL;
        
        public function load(){
            $this->get_user_challenge();
        }
        
        public function fetch_user_challenge()
        {
            
            $sessionUser = get_session_user();
            if ($userChallenge = userChallengeDAO::find_by_user_and_challenge_period($sessionUser->id, $this->challenge_period->id)) {
                
                $this->user_challenge = $userChallenge;
                $this->total_points   = $userChallenge->points;
                $this->tracking_data  = $userChallenge->tracking_data;
                return TRUE;
            }
            
            return FALSE;
        }
        
        public function fetch_challenge_period($challenge_period_id)
        {
            $challengePeriod = challengePeriodDAO::find_by_id($challenge_period_id);
  
            if ($challengePeriod) {
                $this->challenge_period = $challengePeriod;
                return TRUE;
            }
            
            return FALSE;
        }
        
        public function fetch_challenge()
        {
            $challenge = challengeDAO::find_by_id($this->user_challenge->challenge_id);
            
            if ($challenge) {
                $this->challenge = $challenge;
            }
        }
        
        public function fetch_goal_by_challenge_id($challenge_id)
        {
            $goal = challengeGoalDAO::find_by_challenge_id($challenge_id);
            if ($goal) {
                $this->challenge_goal = $goal;
                return TRUE;
            }
            return FALSE;
        }
        
        public function fetch_activities()
        {
            if ($activities = challengeActivityDAO::find_by_challenge($this->challenge->id)) {
                foreach ($activities as $activity) {
                    $this->activities['activity_' . $activity->id] = $activity;
                }
            }
        }
        
        public function calculate_challenge_points()
        {
            
            $totalPoints = 0;
            $challenge = $this->challenge;
    
            $trackingCalendar = $this->get_tracking_calendar();
           

            #maybe add smth here for more points on mins?
            
            foreach ($trackingCalendar as $dayId => $dayData) {
                $points = $this->get_tracking_day_points($dayData);
                $dayData['points'] = $points;
                $trackingCalendar[$dayId] = $dayData;
                $totalPoints += $points;
            }
            $this->tracking_data['calendar'] = $trackingCalendar;
            
            
            
            $this->total_points = $totalPoints;
            $this->total_available_points = $this->get_total_available_points();
            
            
            $this->points_to_complete_challenge = $this->get_point_to_complete_challenge();
            $this->points_to_complete_challenge = 350;
            $this->update_total_points(); 
        }
        
        public function get_tracking_activities()
        {
            if (array_key_exists('activities', $this->tracking_data)) {
                return $this->tracking_data['activities'];
            }
            
            return array();
        }
        
        private function get_total_available_points()
        {
            $challenge = $this->challenge;
            $points    = 0;
            $period    = $this->challenge_period;
            $dateStart = date("Y-m-d", $period->date_start);
            
            $dateEnd   = date("Y-m-d", $period->date_end);
            
            $totalDays = get_days_between_two_dates($dateStart, $dateEnd);
            
            $points = 0;
            for ($i = 0; $i <= $totalDays; $i++) {

                $date = strtotime($dateStart . " +" . $i . " days");
                $dayOfTheWeek = date("N", $date);

                if ($dayOfTheWeek < 5) {
                    $points += 20;
                }
            }
            return $points;
        }
        
        private function get_point_to_complete_challenge()
        {
            $total_available_points = $this->total_available_points;
            $point_to_complete = 0;
            $challenge = $this->challenge;
            $point_to_complete = floor($total_available_points * 0.75);
            return $point_to_complete;
        }
        
        public function update_total_points()
        {
            if ($this->user_challenge->points != $this->total_points) {
                $this->user_challenge->points = $this->total_points;
                $this->user_challenge->update_points();
            }
            
            $this->update_user_completion();  
        }
        
        private function update_user_completion()
        {
            if (($this->total_points >= $this->points_to_complete_challenge) && ($this->user_challenge->completed == 0) && $this->points_to_complete_challenge > 0) {
                $this->user_challenge->set_challenge_completed();
            }
            else if (($this->total_points < $this->points_to_complete_challenge) && ($this->user_challenge->completed == 1)) {
                $this->user_challenge->set_challenge_not_completed();
            }
        }
        
        public function save_exercise_detailed_tracker_day($data) //for the regular challenge with a goal checkbox
        {
            $trackingCalendar = $this->get_tracking_calendar();
            
            $dayData = array('unit'          => $data['unit'],
                             'unit_value'    => $data['unitValue'],
                             'points'        => 0);
            
            $dayData['points'] = $this->get_tracking_day_points($dayData);
            
            // Grab Previous Points for that Day
            if ($previousDayData = $this->get_tracking_day($data['dayId'])) {
                $this->total_points -= $previousDayData['points'];                
            }
            
            // Total Points
            $this->total_points += $dayData['points'];
        
            // Update Tracking Data
            // If the row is all empty... unset the day.
            if (($dayData['unit_value'] == 0 || $dayData['unit_value'] == '') && $dayData['unit'] == 0) {
                unset($trackingCalendar[$data['dayId']]);
            }
            else {
                $trackingCalendar[$data['dayId']] = $dayData;
            }
            
            $this->tracking_data['calendar'] = $trackingCalendar;
            $this->update_total_points(); 
            $this->user_challenge->tracking_data = $this->tracking_data;
            $this->user_challenge->update_tracking_data();
            $this->update_user_team_score($data['dayId']);
            return TRUE;
        }
        private function update_user_team_score($day_id) {
            $day_v = intval($day_id);
            $week_id = 0;
            if ($day_v < 20190713) {
                $day_start = 20190701;
                $day_end = 20190713;
            }
            else if ($day_v < 20190727) {
                $day_start = 20190715;
                $day_end = 20190727;
                $week_id = 1;
            }
            else if ($day_v < 20190810) {
                $day_start = 20190729;
                $day_end = 20190810;
                $week_id = 2;
            }
            else if ($day_v < 20190824) {
                $day_start = 20190812;
                $day_end = 20190824;
                $week_id = 3;
            }
            $calendar = $this->user_challenge->tracking_data['calendar'];
            $total_week_score = 0;
            foreach ($calendar as $dayData => $value) {
                $day = intval($dayData);
                if($day <= $day_end && $day >= $day_start) {
                    $total_week_score = $total_week_score + $value['points'];
                } 
            }

            //get rank
            if ($day_end >= intval(date('Ymd'))) {
                $teamID = $this->get_team_assignment()->team_id;
                $sessionUser = get_session_user();
                ckartUserWeekScoreDAO::update($teamID, $week_id, $sessionUser->id, $total_week_score);
            }
            // $this->update_abc();
        }

        private function update_abc(){
            $day_start = 20190729;
            $day_end = 20190810;
            $week_id = 2;
            $all_users = ckartUserWeekScoreDAO::find_all_users_by_week($week_id);
   
            foreach($all_users as $user) {
                $userChallenge = userChallengeDAO::find_by_user_and_challenge_period($user->user_id, $this->challenge_period->id);
                $calendar = $userChallenge->tracking_data['calendar'];
                $total_week_score = 0;
                foreach ($calendar as $dayData => $value) {
                    $day = intval($dayData);
                    if($day <= $day_end && $day >= $day_start) {
                        $total_week_score = $total_week_score + $value['points'];
                    } 
                }
                echo $total_week_score;
                ckartUserWeekScoreDAO::update($user->team_id, $week_id, $user->user_id, $total_week_score);
            }

        }

        public function update_team_score($day_id, $currentWeek, $score_change) {
            $day_v = intval($day_id);
            $week_v = -2;
            if ($day_v < 20190713) {
                $week_v = 0;
            }
            else if ($day_v < 20190727) {
                $week_v = 1;
            }
            else if ($day_v < 20190810) {
                $week_v = 2;
            }
            else if ($day_v < 20190824) {
                $week_v = 3;
            }

            if ($week_v != $currentWeek) {
                echo "000";
            }
            $teamID = $this->get_team_assignment()->team_id;
            $team = ckartTeamDAO::find_by_id($teamID);
            $totalMember = $team->member_count;
            

            $sessionUser = get_session_user();
            //ckartUserWeekScoreDAO::update($teamID, $week_v, $sessionUser->id, $score_change);
            
            $team_map_total = ckartUserWeekScoreDAO::sum_by_map_team($week_v, $teamID);
            $team_map_score = $team_map_total / $totalMember;
            ckartTeamScoreDAO::update($teamID, $week_v, $team_map_score);
            ckartTeamScoreDAO::assign_rank($week_v);
            return $team_map_score;
        }
        
        public function get_total_points_percentage() 
        {
            if ($this->total_available_points == 0) {
                return 100;
            }
            else return round($this->total_points / $this->points_to_complete_challenge *100, 1, PHP_ROUND_HALF_DOWN); 
        }
        

        public function get_ranking() {
            $ranking = array();
            
            for($map = 0; $map < 4; $map++) { // [map_ranking]
                
                // get ranking per team
                $teamRanking = ckartTeamScoreDAO::get_team_score_by_map_week($map);
                
                // get ranking in each team
                $map_ranking = array();
                foreach($teamRanking as $team) { // map_ranking = [team_id, users ]
                    $users = array();
                    $team_users = ckartUserWeekScoreDAO::find_by_week_team($map, $team->team_id);
                    
                    foreach ($team_users as $u) { //users = [alias, score]
                        array_push($users, array($u->alias, $u->score));
                    }
                    array_push($map_ranking, array($team->team_id, $users, number_format((float)$team->score, 2, '.', '')));
                }
                array_push($ranking, $map_ranking);
            }
            return $ranking;
        }
        

        public function get_user_score_per_map() {
            $sessionUser = get_session_user();
            $this->user_scores_per_map = ckartUserWeekScoreDAO::find_by_user($sessionUser->id);
            return $this->user_scores_per_map;
        }

        public function get_user_score_by_map($map_id){
            $result = 0;
            foreach($this->user_scores_per_map as $score){
                if ($score->week_id == $map_id) {
                    $result = $score->score;
                    break;
                }
            }
            return $result;
        }
        public function get_teams_info()
        {
            $ckartTeams = ckartTeamDAO::find_all();
            if ($ckartTeams) {
                $this->ckart_teams = $ckartTeams;
                return $ckartTeams;
            }
            return false;
        }
        
        public function get_user_team($teamId)
        {
            $userTeam = ckartTeamDAO::find_by_id($teamId);
            if ($userTeam) {
                $this->user_team = $userTeam;
                return $userTeam;
            }
            return false;
        }
        
        public function get_team_assignment()
        {
            $sessionUser = get_session_user();
            $this->team_assigned = ckartAssignmentDAO::find_by_user_id($sessionUser->id);
            return $this->team_assigned;
        }
        
        public function get_tracking_day($day_id)
        {
            $trackingCalendar = $this->get_tracking_calendar();
            
            if (array_key_exists($day_id, $trackingCalendar)) {
                return $trackingCalendar[$day_id];
            }
            
            return NULL;
        }
        
        public function get_tracking_calendar()
        {
          
            if (array_key_exists('calendar', $this->tracking_data)) {
                if ($this->tracking_data['calendar'] == null){
                    return array();
                }
                return $this->tracking_data['calendar'];
            }
            
            return array();
        }
        
        public function register_team($teamID)
        {
            $sessionUser = get_session_user();
            $userID = ckartAssignmentDAO::find_by_user_id($sessionUser->id);

            if ($userID == FALSE)
            {
                ckartTeamDAO::increament_member_count_by_team($teamID);
                return ckartAssignmentDAO::insert($sessionUser->id, $teamID);
            }

            return FALSE;
        }
        
        public function get_tracking_day_points($data)
        {
            $points = 0;
            $challenge = $this->challenge;
            if ($data['unit'] == 2) { // Steps
                if ($data['unit_value'] >= 100) {
                    $points = ($data['unit_value'] / DAILY_CHALLENGE_STEPS_PER_POINT);
                }

            }
            
            return round($points, 1, PHP_ROUND_HALF_DOWN);
        }

        public function register_alias($alias)
        {
            $sessionUser = get_session_user();

            if($alias == NULL)
            {
                $alias = $sessionUser->first_name . " " . $sessionUser->last_name;
            }

            return challengeAliasDAO::insert($sessionUser->id, $alias);
        }

        public function check_alias($alias)
        {   
            $check = challengeAliasDAO::check_dups($alias);
            
            if($check == FALSE)
            {
                return TRUE;
            }
            return FALSE;
        }

        public function get_team_map_data()
        {
            $team_data =  "[";
            for($i = 0; $i < 3; $i++) {
                $team_data = $team_data . "[";
                $team_week_score = ckartTeamScoreDAO::find_by_week_id($i);
                for($d = 0; $d < 3; $d++){
                    $team_data = $team_data . $team_week_score[$d]->score;
                    $team_data = $team_data . ",";
                }
                $team_data = $team_data . $team_week_score[3]->score;
                $team_data = $team_data . "],";
            }
            $team_data = $team_data . "[";
            $team_week_score = ckartTeamScoreDAO::find_by_week_id(3);
            for($d = 0; $d < 3; $d++){
                $team_data = $team_data . $team_week_score[$d]->score;
                $team_data = $team_data . ",";
            }
            $team_data = $team_data . $team_week_score[3]->score;
            $team_data = $team_data . "]]";
            $this->team_data = $team_data;
        }

    }

?>