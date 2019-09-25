<?php

    require_once(INCLUDES_DIR . 'objects/meta.php');
    require_once(INCLUDES_DIR . 'objects/css.php');
    require_once(INCLUDES_DIR . 'objects/js.php');

    class abstractView
    {
        protected $page = '';
        protected $titles;
        protected $page_title;
        
        // Librairies
        private $css;
        private $js;
        
        // Model
        protected $model;
        
        // Sections
        protected $current_section;
        protected $sections;
        
        /* Public Functions */
        
        public function __construct($model = "")
        {
            if (!empty($model)) {
                $this->set_model($model);
            }
            
            $this->titles = array();
            $this->page_title = "";
            $this->meta = array();
            $this->css = array();
            $this->js = array();
            $this->sections = array();
            $this->current_section = "";
        }
        
        public function set_page($page)
        {
            $this->page = $page;
        }
    
        public function set_model($model)
        {
            $this->model = $model;
        }
        
        public function add_meta($property, $content)
        {
            $meta = new meta();

            $meta->property = $property;
            $meta->content = $content;

            $this->meta[] = $meta;
        }
        
        public function get_meta_html()
        {
            $html = "";
            foreach ($this->meta as $meta) {
                $html .= $meta->get_html() . chr(13);
            }
            
            return $html;
        }
        
        public function add_css($name, $root = FALSE, $type = "text/css", $media = "screen")
        {
            $filepath = ($root) ? ROOT_CSS_DIR : CSS_DIR;
            $filename = $filepath . $name;

            if (!array_key_exists($filename, $this->css)) {

                $css = new css();

                $css->name = $name;
                $css->path = $filepath;
                $css->type = $type;
                $css->media = $media;

                $this->css[$filename] = $css;
            }
        }
        
        public function add_css_url($url, $type = "text/css", $media = "screen")
        {
            if (!array_key_exists($url, $this->css)) {

                $css = new css();

                $css->name = $url;
                $css->path = "";
                $css->type = $type;
                $css->media = $media;
                $css->include_timestamp = FALSE;

                $this->css[$url] = $css;
            }
        }
        
        public function get_css_html()
        {
            $html = "";
            foreach ($this->css as $css) {
                $html .= $css->get_html() . chr(13);
            }
            
            return $html;
        }

        public function add_js($name, $root = FALSE, $type = "text/javascript")
        {
            $filepath = ($root) ? ROOT_JS_DIR : JS_DIR;
            $filename = $filepath . $name;
            
            if (!array_key_exists($filename, $this->js)) {

                $js = new js();

                $js->name = $name;
                $js->path = $filepath;
                $js->type = $type;

                $this->js[$filename] = $js;
            }
        }

        public function add_js_url($url, $type = "text/javascript")
        {
            if (!array_key_exists($url, $this->js)) {

                $js = new js();

                $js->name = $url;
                $js->path = "";
                $js->type = $type;
                $js->include_timestamp = FALSE;

                $this->js[$url] = $js;
            }
        }
        
        public function get_js_html()
        {
            $html = "";
            foreach ($this->js as $js) {
                $html .= $js->get_html() . chr(13);
            }
            
            return $html;
        }

        public function set_title($title)
        {
            $this->titles = array();
            $this->add_title($title);
        }
        
        public function add_title($title, $link = "")
        {
            $this->titles[] = array('title' => $title, 'link' => $link);   
            
        }
        
        public function get_title()
        {
            $htmlTitle = "";
            
            for ($position = 0; $position < count($this->titles); $position++) {
                if ($position > 0) {
                    $htmlTitle .= WEBSITE_TITLE_SEPARATOR;
                }
                
                $htmlTitle .= $this->titles[$position]['title'];
            }
            
            return $htmlTitle;
        }
        
        public function get_page_title()
        {
            $lastTitle = end($this->titles);
            return $lastTitle['title'];
        }
        
        public function get_additional_titles()
        {
            $titles = array();
            
            if (count($this->titles) > 1) {
                for ($i = 1; $i < count($this->titles); $i++) {
                    $titles[] = $this->titles[$i];
                }
            }
            
            return $titles;
        }
        
        public function set_current_section($section)
        {
            if (array_key_exists($section, $this->sections)) {
                $this->current_section = $section;   
            }
        }
    }

?>