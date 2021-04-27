package com.kang.test.k8s.config;

import org.slf4j.MDC;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.context.event.ApplicationEnvironmentPreparedEvent;
import org.springframework.boot.context.event.ApplicationFailedEvent;
import org.springframework.boot.context.event.ApplicationPreparedEvent;
import org.springframework.boot.context.event.ApplicationStartingEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.GenericApplicationListener;
import org.springframework.core.Ordered;
import org.springframework.core.ResolvableType;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.PropertySource;


/**
 * @Author HuangKang
 * @Date 2021/4/22 下午5:44
 * @Summarize Log4j2启动配置文件事件监听器
 * SpringApplication application = new SpringApplication(VospApiApplication.class);
 * Set<ApplicationListener<?>> ls = application.getListeners();
 * Log4j2PropertiesEventListener eventListener = new Log4j2PropertiesEventListener();
 * application.addListeners(eventListener);
 * application.run(args);
 * 启动类启动注册监听器
 */
public class Log4j2PropertiesEventListener implements GenericApplicationListener {

    public static final int DEFAULT_ORDER = Ordered.HIGHEST_PRECEDENCE + 10;

    private static Class<?>[] EVENT_TYPES = {ApplicationStartingEvent.class, ApplicationEnvironmentPreparedEvent.class,
            ApplicationPreparedEvent.class, ContextClosedEvent.class, ApplicationFailedEvent.class};

    private static Class<?>[] SOURCE_TYPES = {SpringApplication.class, ApplicationContext.class};

    @Override
    public void onApplicationEvent(ApplicationEvent event) {
        if (event instanceof ApplicationEnvironmentPreparedEvent) {
            ConfigurableEnvironment envi = ((ApplicationEnvironmentPreparedEvent) event).getEnvironment();
            MutablePropertySources mps = envi.getPropertySources();
            // 获取配置文件配置源,注意版本不同SpringBoot可能配置源名称不一样，可以DEBUG查看名称
            PropertySource<?> ps = mps.get("configurationProperties");
            // 设置log4j2日志收集logstash地址
            if (ps != null && ps.containsProperty("log4j2.logstash.address")) {
                Object address = ps.getProperty("log4j2.logstash.address");
                if (address != null && !address.toString().trim().isEmpty()) {
                    MDC.put("log4j2.logstash.address", address.toString());
                }
            }
            // 设置log4j2日志收集logstash端口号
            if (ps != null && ps.containsProperty("log4j2.logstash.port")) {
                Object port = ps.getProperty("log4j2.logstash.port");
                if (port != null && !port.toString().trim().isEmpty()) {
                    MDC.put("log4j2.logstash.port", port.toString());
                }
            }
            // 设置log4j2日志收集logstash日志格式化表达式
            if (ps != null && ps.containsProperty("log4j2.logstash.pattern")) {
                Object pattern = ps.getProperty("log4j2.logstash.pattern");
                if (pattern != null && !pattern.toString().trim().isEmpty()) {
                    // 获取项目名
                    String appName = ps.getProperty("spring.application.name").toString();
                    // 获取项目名
                    String profile = ps.getProperty("spring.profiles.active").toString();
                    MDC.put("log4j2.logstash.pattern", pattern.toString().replace("${spring.application.name}", appName).replace("${spring.profiles.active}",profile));
                }
            }

        }
    }

    @Override
    public int getOrder() {
        return DEFAULT_ORDER;
    }

    @Override
    public boolean supportsEventType(ResolvableType resolvableType) {
        return isAssignableFrom(resolvableType.getRawClass(), EVENT_TYPES);
    }

    @Override
    public boolean supportsSourceType(Class<?> sourceType) {
        return isAssignableFrom(sourceType, SOURCE_TYPES);
    }

    private boolean isAssignableFrom(Class<?> type, Class<?>... supportedTypes) {
        if (type != null) {
            for (Class<?> supportedType : supportedTypes) {
                if (supportedType.isAssignableFrom(type)) {
                    return true;
                }
            }
        }
        return false;
    }
}
