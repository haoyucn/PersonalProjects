<?php 

    load_dao('abstract');

    class ckartAssignmentDAO extends abstractDAO
    {
        public $user_id;
        public $team_id;
        
        public function __construct() 
        {
            $this->user_id = -1;
            $this->team_id = -1;
        }
        
        public static function find_by_user_id($id) 
        {
            $sql = "SELECT user_id, 
                           team_id
                    FROM " . DB_MAIN . ".ckart_assignment
                    WHERE user_id = " . $id;
            $result_array = self::find_by_sql($sql);
            return !empty($result_array) ? array_shift($result_array) : FALSE;
        }
    
        
        public static function insert($user_id, $team_id)
        {
            global $db;
            $sql = "INSERT INTO " . DB_MAIN . ".ckart_assignment(user_id, team_id) 
                    VALUES('" . $user_id . "', '" . $team_id . "')";
            
            $result = $db->query($sql);
            
            if ($result) {
                $res = new ckartAssignmentDAO();
                $res->user_id = $user_id;
                $res->team_id = $team_id;
                return $res;
            }
            else {
                return FALSE;
            }
        }
    }
?>