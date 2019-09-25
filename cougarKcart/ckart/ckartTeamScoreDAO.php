<?php 

    load_dao('abstract');

    class ckartTeamScoreDAO extends abstractDAO
    {
        public $week_id;
        public $team_id;
        public $score;
        public $rank;
        
        public function __construct() 
        {
            $this->user_id = -1;
            $this->team_id = -1;
        }
        
        public static function update($team_id, $week_id, $score) 
        {
            global $db;
            $sql = "update ".DB_MAIN.".ckart_team_score
                    set score = " . $score . "
                    
                    WHERE week_id = " . $week_id ." 
                     and team_id = ". $team_id;
            $result = $db->query($sql);
        }
        
        public static function find_by($week_id, $team_id) {
            $sql = "SELECT week_id, team_id, score from " . DB_MAIN . ".ckart_team_score WHERE week_id = " . $week_id ." 
                    and team_id = ". $team_id;
            $result_array = self::find_by_sql($sql);
       
            return !empty($result_array) ? array_shift($result_array) : FALSE;
        }
        
        public static function find_by_week_id($week_id){
            $sql = "SELECT week_id, team_id, score, rank from " . DB_MAIN . ".ckart_team_score WHERE week_id = " . $week_id ." 
            order by team_id ASC";
            $result_array = self::find_by_sql($sql);
            return !empty($result_array) ? $result_array : FALSE;
        }

        public static function get_team_score_by_map_week($week_id) {
            $sql = "SELECT week_id, team_id, score from " . DB_MAIN . ".ckart_team_score WHERE week_id = " . $week_id ." 
            order by score DESC";
            $result_array = self::find_by_sql($sql);
            return !empty($result_array) ? $result_array : FALSE;
        }
        public static function assign_rank($week_id){
            $sql = "SELECT week_id, team_id, score, rank from " . DB_MAIN . ".ckart_team_score WHERE week_id = " . $week_id ." AND archived = 0 
            order by score DESC";
            $result_array = self::find_by_sql($sql);
            $totalCount = count($result_array);
            $offset = 4 - $totalCount;
            global $db;
            for($i = 0; $i < $totalCount; $i++) {
                $update_c = "update ".DB_MAIN.".ckart_team_score ";
                $rank = $i + $offset;
                $update_c  =  $update_c . " rank = " . $rank . " ";
                if (floatval($result_array[$i]->score) >= 60.0) {
                    $update_c  =  $update_c . ", archived = 1 ";
                }

                $update_c = $update_c . "WHERE week_id = " . $week_id ." 
                            and team_id = ". $result_array[$i]->team_id;
                $result = $db->query($update_c);
            }
        }
    }
?>