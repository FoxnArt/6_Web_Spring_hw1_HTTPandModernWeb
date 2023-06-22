package ru.netology;

import org.apache.hc.core5.http.NameValuePair;
import org.apache.hc.core5.net.URLEncodedUtils;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class Request {
    private final String method;
    private final String path;
    List<NameValuePair> queryParams;

    public Request(String method, String pathWithQueryParams) {
        this.method = method;

        URI uri;

        try {
            uri = new URI(pathWithQueryParams);
            this.path = uri.getPath();
            this.queryParams = URLEncodedUtils.parse(uri, StandardCharsets.UTF_8);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    public String getMethod() {
        return method;
    }

    public String getPath() {
        return path;
    }

    public List<NameValuePair> getQueryParam(String name) {
        return queryParams
                .stream()
                .filter(x -> Objects.equals(x.getName(), name))
                .collect(Collectors.toList());
    }

    public List<NameValuePair> getQueryParams() {
        return queryParams;
    }
}
