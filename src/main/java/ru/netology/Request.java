package ru.netology;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class Request {
    private final static String DELIMITER = "\r\n\r\n";
    private final static String NEWLINE = "\r\n";
    private final static String HEADER_DELIMITER = ":";

    private final String message;
    private final Method method;
    private final String url;
    private final Map<String, String> headers;
    private final String body;

    public Request(String message) throws IOException {
        Method requestMethod;

        this.message = message;
        String[] messageParts = message.split(DELIMITER);
        String head = messageParts[0];
        String[] headersLines = head.split(NEWLINE);
        String[] startingLine = headersLines[0].split(" ");

        try {
            requestMethod = Method.valueOf(startingLine[0].toUpperCase());
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            requestMethod = null;
        }
        this.method = requestMethod;
        this.url = startingLine[1];
        this.headers = Collections.unmodifiableMap(
                new HashMap<>() {{
                    for (int i = 1; i < headersLines.length; i++) {
                        String[] header = headersLines[i].split(HEADER_DELIMITER, 2);
                        put(header[0].trim(), header[1].trim());
                    }  
                }}
        );

        String bodyLength = headers.get("Content-Length");
        int length = bodyLength != null ? Integer.parseInt(bodyLength) : 0;
        this.body = messageParts.length > 1 ? messageParts[1].trim().substring(0, length) : "";

    }

    public String getMessage() {
        return message;
    }

    public Method getMethod() {
        return method;
    }

    public String getUrl() {
        return url;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public String getBody() {
        return body;
    }
}
