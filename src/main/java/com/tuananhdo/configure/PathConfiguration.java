package com.tuananhdo.configure;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

@Component
@PropertySource("classpath:path.properties")
public class PathConfiguration {

    @Value("${path.scheme}")
    private String scheme;

    @Value("${path.host}")
    private String host;

    @Value("${path.port}")
    private int port;

    public String getRootPath() {
        return UriComponentsBuilder.newInstance()
                .scheme(scheme)
                .host(host)
                .port(port)
                .toUriString();
    }
}
