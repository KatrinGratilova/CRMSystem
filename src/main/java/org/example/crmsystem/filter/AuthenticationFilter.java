package org.example.crmsystem.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.example.crmsystem.messages.ExceptionMessages;
import org.example.crmsystem.service.AuthenticationService;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
@RequiredArgsConstructor
public class AuthenticationFilter extends OncePerRequestFilter {
    private final AuthenticationService authenticationService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        CachedBodyHttpServletRequest wrappedRequest = new CachedBodyHttpServletRequest(request);

        String path = wrappedRequest.getRequestURI();
        if (path.equals("/login") || request.getMethod().equalsIgnoreCase("POST") || path.equals("/swagger-ui.html")) {
            filterChain.doFilter(wrappedRequest, response);
            return;
        }

        String username = extractUsername(wrappedRequest);

        if (username == null || !authenticationService.isAuthenticated(username)) {
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

            String errorMessage = ExceptionMessages.USER_IS_NOT_AUTHENTICATED_WITH_USERNAME.format(username);

            response.getWriter().write(errorMessage);
            response.getWriter().flush();
            return;
        }

        filterChain.doFilter(wrappedRequest, response);
    }

    private String extractUsername(HttpServletRequest request) {
        String path = request.getRequestURI();
        String username = null;

        Pattern pattern = Pattern.compile("^/(trainees|trainers)/([^/]+)");
        Matcher matcher = pattern.matcher(path);

        if (matcher.find()) {
            username = matcher.group(2);
            return username;
        }

        try {
            byte[] requestBody = request.getInputStream().readAllBytes();
            ObjectMapper objectMapper = new ObjectMapper();
            Map<String, Object> body = objectMapper.readValue(requestBody, Map.class);

            System.out.println(body);
            if (body.containsKey("username")) {
                username = body.get("username").toString();
                return username;
            }
        } catch (IOException e) {
            System.out.println("Failed to parse request body: " + e.getMessage());
        }

        return null;
    }
}
