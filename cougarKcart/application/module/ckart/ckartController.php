<?php
    
    require_once('setting.php');

    class ckartController
    {
        protected $model;
        protected $view;
        
        public function __construct($view = NULL, $model = NULL)
        {
            
            $this->model = $model;
            $this->view = $view;
        }
        
        public function load()
        {
            if (isset($_GET['action'])) {
                $this->call_action($_GET['action']);
            }
            else {
                if(!$this->load_user_challenge()) {
                    die("Missing Period id\n");
                }
 
                $this->view->load();
            }
        }
        
        private function load_user_challenge()
        {
            $periodId = get_get_var('period_id');
            
            if ($this->model->fetch_challenge_period($periodId)) {
                if ($this->model->fetch_user_challenge()) {
                    $this->get_mode();
                    $this->model->fetch_user_challenge();
                    $this->model->get_teams_info();
                    $this->model->fetch_challenge();
                    $this->model->fetch_goal_by_challenge_id($this->model->challenge->id);
                    $this->model->fetch_activities();
                    
                    $this->model->calculate_challenge_points();
                    
                    return TRUE;
                }
            }
            
            
            return FALSE;
        }
        
        private function get_mode()
        {
            $mode = CKART_MODE_MAP; // Default value
            // $getMode = get_get_var('mode');
            
            if ($this->model->get_team_assignment() == FALSE) {
                $this->model->get_team_assignment();
                $mode = CKART_MODE_TEAM_SELECT;
            } 
            
            $this->view->mode = $mode;
            $this->model->get_team_map_data();
            // need to know if user has chosen a team 
            
            // if user didnt, set mod to pop up team chosen window
            
            // else regular page
        }
        
        private function call_action($action)
        {
            
            switch ($action) {
                case "register_alias":
                    $this->register_alias();
                    break;
                case "team_register":
                    $this->team_register();
                    break;
                case 'save_exercise_detailed_tracker_day':
                    $this->save_exercise_detailed_tracker_day();
                    break;
                case "check_alias":
                    $this->check_alias();
                    break;
                case "update_team_score":
                    $this->update_team_score();
                    break;
                default:
                    break;
            }
        }
        
        private function team_register()
        {
//          echo "function get called ";
            $teamID = get_post_var("teamID");
            
            if ($this->model->register_team($teamID))
            {
                echo "SUCCESS";
            }
            else {
                echo "ERROR";
            }

        }

        private function update_team_score() 
        {
            $day_id = get_post_var("day_id");
            $currentWeek = get_post_var("week_id");
            $score_change = get_post_var("score_change");
           
            echo $this->model->update_team_score($day_id, $currentWeek, $score_change);
        }
        
        private function register_alias()
        {
            $alias = get_post_var("alias");
            
            if ($this->model->register_alias($alias))
            {
                echo "SUCCESS";
            }
            else {
                echo "ERROR";
            }
    
        }
        
        private function check_alias()
        {
            $alias = get_post_var("alias");
            if ($this->model->check_alias($alias))
            {
                echo "SUCCESS";
            }
            else {
                echo "ERROR";
            }
    
        }
        
        

        private function save_exercise_detailed_tracker_day()
        {
            $result = array('message'               => 'ERROR',
                            'totalPoints'           => 0,
                            'totalPointsPercentage' => 0,
                            'points'                => 0);
            
            if ($this->load_user_challenge()) {

                $postDataFields = array('dayId', 'unit', 'unitValue');
                $postVars = get_allowed_post_vars($postDataFields);

                // Javascript already validates these fields. This is an extra protection.
                $errorFound = FALSE;

                // Invalid Activity Data
                if (!empty($postVars['unit']) && !is_numeric($postVars['unit'])) {
                    $errorFound = TRUE;
                }
                
                if (!$errorFound) {
                    if ($this->model->save_exercise_detailed_tracker_day($postVars)) {
                        
                        $points = 0;
                        if ($dayData = $this->model->get_tracking_day($postVars['dayId'])) {
                            $points = $dayData['points'];
                        }
                        
                        $result = array('message'               => "SUCCESS",
                                        'points'                => $points,
                                        'totalPoints'           => $this->model->total_points,
                                        'totalPointsPercentage' => $this->model->get_total_points_percentage());
                    }
                }
            }

            echo json_encode($result);
        }
        
    }
?>