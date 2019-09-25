<?php
    load_dao('abstract');

    class ckartTeamDAO extends abstractDAO
    {
        public $id;
        public $name;
        public $member_count;
        public $team_kart;
        
        public function __construct()
        {
            $id = -1;
            $name = NULL;
            $member_count = -1;
            $team_kart = NULL;
        }
        
        public static function find_all($challenge_id = 0)
        {
            $sql = "SELECT id, 
                           name,
                           member_count,
                           team_score,
                           team_kart_img,
                           team_token_img
                    FROM " . DB_MAIN . ".ckart_team";

            $result_array = self::find_by_sql($sql);
            return !empty($result_array) ? $result_array : FALSE;
        }
        
        public static function find_by_id($id)
        {
            $sql = "SELECT id, 
                           name,
                           member_count,
                           team_score,
                           team_kart_img,
                           team_token_img
                    FROM " . DB_MAIN . ".ckart_team
                    WHERE id = " . $id;

            $result_array = self::find_by_sql($sql);
            return !empty($result_array) ? array_shift($result_array) : FALSE;
        }
        public static function increament_member_count_by_team($team_id) {
            global $db;
            
            $team = ckartTeamDAO::find_by_id($team_id);
            $count = $team->member_count;

            $count++;

            $sql = "UPDATE " . DB_MAIN . ".ckart_team SET member_count = " . $count . " WHERE id = " . $team_id;

            $result = $db->query($sql);
        }
    }
?>