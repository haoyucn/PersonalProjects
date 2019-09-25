package com.obfuscation.server;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;

import static java.net.HttpURLConnection.HTTP_OK;

/**
 * Created by urim on 3/9/2018.
 */

public class DefaultHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        exchange.sendResponseHeaders(HTTP_OK, 0);

        //link to server test webpage
        String urlPath = exchange.getRequestURI().toString();
        if (urlPath.length() == 0 || urlPath.equals("/")) {
            String filePathStr = "web" + File.separator + "index.html";
            Path filePath = FileSystems.getDefault().getPath(filePathStr);
            Files.copy(filePath, exchange.getResponseBody());
//            System.out.println(exchange.getRequestURI().toString());
        }
        else {
            String filePathStr = "web" + urlPath;
            File file = new File(filePathStr);
            if (file.exists() && file.canRead()) {
                OutputStream respBody = exchange.getResponseBody();
                Files.copy(file.toPath(), respBody);
            }
        }
        exchange.getResponseBody().close();
    }
}