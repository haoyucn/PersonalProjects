<?php

    define('LOGIN_REQUIRED', TRUE);

    define('CURRENT_DIR', 'hm/');
    define('ROOT_DIR', '../');

    include(ROOT_DIR . 'includes/global.php');

    $page = get_get_var('page');
    $module = get_get_var('module');

    if ($page) {
         
        load_mvc($page);  
        
        $modelName = $page . "Model";
        $viewName = $page . "View";
        $controllerName = $page . "Controller";
        
        instantiate_mvc($modelName, $viewName, $controllerName);
    } 
    else if ($module) {
        $moduleDir = CURRENT_DIR . "application/module/" . $module . "/";
        
        $controllerName = $module . "Controller";
        $modelName = $module . "Model";
        $viewName = $module . "View";
        
        load_file($moduleDir . $controllerName . ".php");
  
        load_file($moduleDir . $modelName . ".php");
        load_file($moduleDir . $viewName . ".php");
        instantiate_mvc($modelName, $viewName, $controllerName);
    }

    function instantiate_mvc($modelName, $viewName, $controllerName) {
        $model = NULL;
        if (class_exists($modelName)) {
            $model = new $modelName();
        }
        
        $view = NULL;
        if (class_exists($viewName)) {
            $view = new $viewName($model);
        }
        
        if (class_exists($controllerName)) {
            $controller = new $controllerName($view, $model);
            $controller->load();
        }
        else if ($view != NULL) {
            $view->load();   
        }
    }
    
    
?>