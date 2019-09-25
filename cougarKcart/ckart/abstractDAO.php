<?php

    abstract class abstractDAO
    {
        public $table_prefix = "";
        
        protected static function find_by_sql($sql = "")
        {
            global $db;
           
            $result = $db->query($sql);
            
            $objects = array();
            while ($record = $db->fetch_array($result)) {
                $objects[] = static::instantiate($record);
            }
            
            return $objects;
        }

		protected static function instantiate($record)
		{
			$object = new static;
            
            foreach ($record as $attribute => $value) {
                $object->set_attribute($attribute, $value);
            }
        
			return $object;
		}
        
        protected function set_attribute($attribute, $value)
        {
            $attributeWithoutPrefix = str_replace($this->table_prefix . "_", "", $attribute);
        
            if ($this->has_attribute($attributeWithoutPrefix)) {
                $this->$attributeWithoutPrefix = $value;
            }
            else {
                $this->$attribute = $value;
            }
        }
        
        protected function has_attribute($attribute)
        {
            $object_vars = get_object_vars($this);
            return array_key_exists($attribute, $object_vars);
        }
        
        protected function sanitize_value($value)
        {
            global $db;
            return $db->escape_value($value);
        }
    }

?>