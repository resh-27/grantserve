package com.cognizant.review_service.config;
import feign.RequestInterceptor;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Configuration
public class FeignConfig {

    @Bean
    public RequestInterceptor requestInterceptor() {
        return requestTemplate -> {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

            if (attributes == null) {
                // If you see this in your console, it's a thread isolation issue
                System.out.println("FEIGN ERROR: No RequestContext available (Thread mismatch)");
                return;
            }

            HttpServletRequest request = attributes.getRequest();
            String header = request.getHeader("Authorization");

            if (header != null) {
                requestTemplate.header("Authorization", header);
                System.out.println("FEIGN SUCCESS: Token propagated to next service");
            } else {
                System.out.println("FEIGN WARN: No Authorization header found in original request");
            }
        };
    }
}