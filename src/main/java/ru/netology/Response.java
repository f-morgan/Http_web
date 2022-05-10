package ru.netology;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class Response {
    private final static String NEWLINE = "\r\n";

    private final Map<String, String> headers = new HashMap<>();
    private byte[] body;
    private int statusCode = 200;
    private String status = "Ok";
    private String mimeType;

    public Response() {
        this.headers.put("Server", "ru-netology");
        this.headers.put("Connection", "Close");
    }

    public void addHeader(String key, String value) {
        headers.put(key, value);
    }

    public byte[] getMessageHeaders() {
        StringBuilder builder = new StringBuilder();

        builder.append("HTTP/1.1 ")
                .append(statusCode)
                .append(" ")
                .append(status)
                .append(NEWLINE);

        for (Map.Entry<String, String> entry : headers.entrySet()) {
            builder.append(entry.getKey())
                    .append(": ")
                    .append(entry.getValue())
                    .append(NEWLINE);
        }

        return builder.append(NEWLINE)
                .toString()
                .getBytes(StandardCharsets.UTF_8);

    }

    public byte[] getMessageBody() {
        return body;
    }



    public Map<String, String> getHeaders() {
        return headers;
    }

    public byte[] getBody() {
        return body;
    }

    public void setBody(byte[] body) {
        this.headers.put("Content-Length", String.valueOf(body.length));
        this.body = body;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
        addHeader("Content-Type", mimeType);
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
