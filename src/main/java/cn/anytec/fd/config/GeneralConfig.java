package cn.anytec.fd.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class GeneralConfig {

    @Value("${config.test}")
    private String test;

    public String getTest() {
        return test;
    }
}
