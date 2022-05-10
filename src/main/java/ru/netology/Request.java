package ru.netology;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class Request {
    private final static String DELIMITER = "\r\n\r\n";
    private final static String NEWLINE = "\r\n";
    private final static String HEADER_DELIMITER = ":";

    private final String message;
    private final Method method;
    private final String url;
    private final String path;
    private final Map<String, String> headers;
    private final String body;
    private final Map<String, Set<String>> params;

    public Request(String message) throws IOException, URISyntaxException {

        this.message = message;
        String[] messageParts = message.split(DELIMITER);
        String head = messageParts[0];
        String[] headersLines = head.split(NEWLINE);
        String[] startingLine = headersLines[0].split(" ");

        this.method = Method.valueOf(startingLine[0]);
        this.path = startingLine[1];
        this.url = this.path.split("\\?",2)[0];
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

        if (this.method==Method.GET) {

            this.params = Collections.unmodifiableMap(
                    new HashMap<>() {{
                        List<NameValuePair> qparam = URLEncodedUtils.parse(new URI(path), StandardCharsets.UTF_8);
                        qparam.forEach(nameValuePair ->{
                            String name = nameValuePair.getName();
                            String value = nameValuePair.getValue();
                            Set<String> values = get(name);
                            if (values == null) {
                                values = new HashSet<>();
                            }
                            values.add(value);
                            put(name,values);
                        });
                        //forEach((k, v) -> System.out.println(k + " = " + v));
                    }}
            );


        } else if (this.method==Method.POST) {

            this.params = new HashMap<>();

        } else {
            this.params = new HashMap<>();
        }
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

    public Set<String> getQueryParam(String name) {
        return params.get(name);
    }

    public Map<String, Set<String>> getQueryParams() {
        return params;
    }
}
