package com.boatsafari.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.PrintWriter;
import java.util.List;

@Component
public class RoleAuthorizationInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String path = request.getRequestURI();
        String method = request.getMethod();

        if (path.startsWith("/api/auth/") || method.equalsIgnoreCase("OPTIONS") || !path.startsWith("/api/")) {
            return true;
        }

        String userRole = request.getHeader("X-User-Role");
        if (userRole == null || userRole.trim().isEmpty()) {
            userRole = "CUSTOMER";
        }
        userRole = userRole.toUpperCase();

        if ("ADMIN".equals(userRole)) {
            return true;
        }

        boolean authorized = true;

        if (path.startsWith("/api/users") || path.startsWith("/api/reports")) {
            authorized = false;
        } else if (path.startsWith("/api/boats")) {
            if (!"GET".equalsIgnoreCase(method) && !"FLEET_MANAGER".equals(userRole)) {
                authorized = false;
            }
        } else if (path.startsWith("/api/safety")) {
            if (!List.of("SAFETY_OFFICER", "DESK_OFFICER").contains(userRole)) {
                authorized = false;
            }
        } else if (path.startsWith("/api/promotions")) {
            if (!"GET".equalsIgnoreCase(method) && !"MARKETING_OFFICER".equals(userRole)) {
                authorized = false;
            }
        } else if (path.startsWith("/api/reviews")) {
            if (("PUT".equalsIgnoreCase(method) || "DELETE".equalsIgnoreCase(method)) && !"MARKETING_OFFICER".equals(userRole)) {
                authorized = false;
            }
        } else if (path.startsWith("/api/trips")) {
            if (!"GET".equalsIgnoreCase(method) && !"MARKETING_OFFICER".equals(userRole)) {
                authorized = false;
            }
        }

        if (!authorized) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.setContentType("application/json");
            PrintWriter writer = response.getWriter();
            writer.write("{\"status\": 403, \"error\": \"Access Denied\", \"message\": \"Role '" + userRole + "' is not authorized to access endpoint: " + path + "\"}");
            writer.flush();
            return false;
        }

        return true;
    }
}
