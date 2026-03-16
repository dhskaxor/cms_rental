package com.nt.cms.common.config;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.yaml.snakeyaml.Yaml;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

/**
 * cms-config.yml 파일을 프로젝트 루트에서 직접 로드하는 PostProcessor
 *
 * <p>spring.config.import가 Gradle bootRun 등에서 cms-config.yml을 로드하지 못하는 경우를 대비하여,
 * user.dir 기준 cms-config.yml을 직접 읽어 Environment에 추가한다.</p>
 *
 * @author CMS Team
 */
public class CmsConfigEnvironmentPostProcessor implements EnvironmentPostProcessor {

    private static final String CMS_CONFIG_FILE = "cms-config.yml";
    private static final String PROPERTY_SOURCE_NAME = "cmsConfigFile";

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        // 테스트 프로파일에서는 로드하지 않음 (테스트는 application-test 또는 @TestPropertySource 사용)
        if (java.util.Arrays.asList(environment.getActiveProfiles()).contains("test")) {
            return;
        }
        Path configPath = Paths.get(System.getProperty("user.dir", "."), CMS_CONFIG_FILE);
        if (!Files.isRegularFile(configPath)) {
            return;
        }

        try {
            String yamlContent = Files.readString(configPath, StandardCharsets.UTF_8);
            @SuppressWarnings("unchecked")
            Map<String, Object> raw = new Yaml().load(yamlContent);
            if (raw != null && !raw.isEmpty()) {
                Map<String, Object> flat = flattenMap("", raw);
                if (!flat.isEmpty()) {
                    environment.getPropertySources().addFirst(
                            new MapPropertySource(PROPERTY_SOURCE_NAME, flat));
                }
            }
        } catch (Exception ignored) {
            // 로드 실패 시 무시 (optional)
        }
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> flattenMap(String prefix, Map<String, Object> map) {
        Map<String, Object> result = new HashMap<>();
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            String key = prefix.isEmpty() ? entry.getKey() : prefix + "." + entry.getKey();
            Object value = entry.getValue();
            if (value instanceof Map) {
                result.putAll(flattenMap(key, (Map<String, Object>) value));
            } else if (value != null) {
                result.put(key, value);
            }
        }
        return result;
    }
}
