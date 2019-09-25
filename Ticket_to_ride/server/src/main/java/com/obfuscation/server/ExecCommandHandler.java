package com.obfuscation.server;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;

import communication.ICommand;
import communication.Result;
import communication.Serializer;

/**
 * Created by jalton on 10/1/18.
 */

public class ExecCommandHandler implements HttpHandler {

    /**
     * Overrides HttpHandler's handle method
     *
     * @param httpExchange
     * @throws IOException
     */
    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        try {

//            System.out.println("command come in");
            InputStream reqBody = httpExchange.getRequestBody();
            String requestString = readString(reqBody);

            ICommand commandData = new Serializer().deserializeCommand(requestString);

            //execute the command
            Result result = commandData.execute();

            httpExchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, 0);
            OutputStream respBody = httpExchange.getResponseBody();
            writeString(new Serializer().serializeResult(result), respBody);
            respBody.close();
        }
        catch (Exception e) {
            Result result = new Result(false, null, e.getMessage());
            httpExchange.sendResponseHeaders(HttpURLConnection.HTTP_BAD_REQUEST, 0);
            OutputStream respBody = httpExchange.getResponseBody();
            writeString(new Serializer().serializeResult(result), respBody);
            respBody.close();
            e.printStackTrace();
        }
    }

    /**
     * A function that writes to the output stream.
     * @param str serialized result object
     * @param os outputstream
     * @throws IOException
     */
    private void writeString(String str, OutputStream os) throws IOException {
        OutputStreamWriter sw = new OutputStreamWriter(os);
        sw.write(str);
        sw.flush();
    }

    /**
     * A function that reads from the inputstream
     * @param is inputstream
     * @return serialized command object
     * @throws IOException
     */
    private String readString(InputStream is) throws IOException {
        StringBuilder sb = new StringBuilder();
        InputStreamReader sr = new InputStreamReader(is);
        char[] buf = new char[1024];
        int len;
        while ((len = sr.read(buf)) > 0) {
            sb.append(buf, 0, len);
        }
        return sb.toString();
    }
}
