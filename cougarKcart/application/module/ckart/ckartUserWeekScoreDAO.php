<?php 

    load_dao('abstract');

    class ckartUserWeekScoreDAO extends abstractDAO
    {
        public $week_id;
        public $team_id;
        public $user_id;
        public $score;
        
        public function __construct() 
        {
            $this->week_id = -2;
            $this->score = -1;
            $this->user_id = -1;
            $this->team_id = -1;
        }
        
        public static function update($team_id, $week_id, $user_id, $score) 
        {
            $result = ckartUserWeekScoreDAO::find_by($week_id, $team_id, $user_id);
            if ($result) {
                
                $sql = "UPDATE ". DB_MAIN . ".ckart_user_score
                    set score = " . $score . "
                    WHERE week_id = " . $week_id ." 
                     and team_id = ". $team_id . " and user_id =" . $user_id;
            }
            else {
                
                $sql = "INSERT INTO " . DB_MAIN . ".ckart_user_score (week_id, team_id, user_id, score) 
                        VALUES(" . $week_id . ", " . $team_id . ", ". $user_id . ", " . $score . ")";
                
            }
            global $db;
            $result = $db->query($sql);
        }
        
        public static function find_by($week_id, $team_id, $user_id) {
            $sql = "SELECT week_id, team_id, user_id,  score from " . DB_MAIN . ".ckart_user_score WHERE week_id = " . $week_id ." 
                    and team_id = ". $team_id ." and user_id =" . $user_id;
            $result_array = self::find_by_sql($sql);
            
            return !empty($result_array) ? array_shift($result_array) : FALSE;
        }
        
        public static function find_by_week_team($week_id, $team_id){
            $sql = "SELECT cus.week_id, cus.team_id,  cus.score, ca.alias from " . DB_MAIN . ".ckart_user_score cus
                    Join " . DB_MAIN . ".challenge_alias ca on ca.user_id = cus.user_id 
                    WHERE week_id = " . $week_id ." and cus.team_id = " . $team_id . "   
                    order by cus.score DESC LIMIT 10";
            
            $result_array = self::find_by_sql($sql);
            return !empty($result_array) ? $result_array : FALSE;
        }

        public static function find_by_user($user_id) {
            $sql = "SELECT week_id, team_id, user_id,  score from " . DB_MAIN . ".ckart_user_score WHERE user_id =" . $user_id;
            $result_array = self::find_by_sql($sql);
            
            return !empty($result_array) ? $result_array : FALSE;
        }
        
        public static function sum_by_map_team($map, $team){
            $sql = "SELECT sum(score) as s from " . DB_MAIN . ".ckart_user_score
                     WHERE team_id = " . $team . " and week_id = " . $map;
            global $db;
            $result = $db->query($sql);
            $arr = $db->fetch_array($result);
           
            return $arr['s'];
        }
        public static function find_all_users_by_week($week_id) {
            $sql  = "SELECT user_id, team_id from " . DB_MAIN . ".ckart_user_score WHERE week_id =" . $week_id;
            global $db;
            $result_array = self::find_by_sql($sql);
            return !empty($result_array) ? $result_array : FALSE;
        }
    }
?>