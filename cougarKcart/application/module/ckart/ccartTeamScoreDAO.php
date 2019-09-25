<?php 

    load_dao('abstract');

    class ckartTeamScoreDAO extends abstractDAO
    {
        public $week_id;
        public $team_id;
        public $score;
        
        public function __construct() 
        {
            $this->user_id = -1;
            $this->team_id = -1;
        }
        
        public static function update($team_id, $week_id, $score) 
        {
            $sql = "update ".DB_MAIN."ckart_team_score
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
            $sql = "SELECT week_id, team_id, score from " . DB_MAIN . ".ckart_team_score WHERE week_id = " . $week_id ." 
            order by team_id ASC";
            $result_array = self::find_by_sql($sql);
            return !empty($result_array) ? $result_array : FALSE;
        }
        
    }
?>