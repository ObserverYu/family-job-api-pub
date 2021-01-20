package org.chen.listener;

import org.jboss.logging.MDC;
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
import org.springframework.stereotype.Component;

/**
 * 
 *  
 * @author YuChen
 * @date 2020/9/17 11:06
 **/

@Component
public class ApplicationStartedEventListener implements GenericApplicationListener {

    public static final int DEFAULT_ORDER = Ordered.HIGHEST_PRECEDENCE + 10;

    private static Class<?>[] EVENT_TYPES = { ApplicationStartingEvent.class,
            ApplicationEnvironmentPreparedEvent.class, ApplicationPreparedEvent.class,
            ContextClosedEvent.class, ApplicationFailedEvent.class };

    private static Class<?>[] SOURCE_TYPES = { SpringApplication.class,
            ApplicationContext.class };

    @Override
    public void onApplicationEvent(ApplicationEvent event) {
        if (event instanceof ApplicationEnvironmentPreparedEvent) {

            ConfigurableEnvironment envi = ((ApplicationEnvironmentPreparedEvent) event).getEnvironment();
            MutablePropertySources mps = envi.getPropertySources();

            PropertySource<?> ps = mps.get("configurationProperties");

            if (ps != null && ps.containsProperty("logging.path")) {
                String logPath = (String) ps.getProperty("logging.path");
                MDC.put("logging.path", logPath);
            }

            if (ps != null && ps.containsProperty("logging.pattern.level")) {
                String logLevel = (String) ps.getProperty("logging.pattern.level");
                MDC.put("logging.pattern.level", logLevel);
            }

            if (ps != null && ps.containsProperty("spring.application.name")) {
                String name = (String) ps.getProperty("spring.application.name");
                MDC.put("spring.application.name", name);
            }

            if (ps != null && ps.containsProperty("logging.package")) {
                String logPackage = (String) ps.getProperty("logging.package");
                MDC.put("logging.package", logPackage);
            }

        }

    }

    @Override
    public int getOrder() {
        // TODO Auto-generated method stub
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
