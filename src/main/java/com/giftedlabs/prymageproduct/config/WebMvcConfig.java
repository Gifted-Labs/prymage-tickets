package com.giftedlabs.prymageproduct.config;

import com.giftedlabs.prymageproduct.interceptor.PublicTicketRateLimitInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    private final PublicTicketRateLimitInterceptor publicTicketRateLimitInterceptor;

    public WebMvcConfig(PublicTicketRateLimitInterceptor publicTicketRateLimitInterceptor) {
        this.publicTicketRateLimitInterceptor = publicTicketRateLimitInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(publicTicketRateLimitInterceptor)
                .addPathPatterns("/api/v1/tickets/public")
                .excludePathPatterns("/api/v1/tickets/public/track/**");
    }
}
