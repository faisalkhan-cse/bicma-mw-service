package com.bicmamwservice.configuration;

import com.bicmamwservice.exception.CustomErrorResponse;
import com.bicmamwservice.exception.UnauthorizedException;
import com.bicmamwservice.service.JwtService;
import com.bicmamwservice.service.UserInfoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;

import static com.bicmamwservice.configuration.SecurityConfig.AUTH_WHITE_LIST;


@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserInfoService userDetailsService;




    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        boolean isWhiteListed = Arrays.stream(AUTH_WHITE_LIST)
                .anyMatch(uri -> uri.equalsIgnoreCase(request.getRequestURI()));
        if(isWhiteListed) {
            filterChain.doFilter(request, response);
            return; // Return after bypassing the filter chain for the specific URIs
        }

        try {
            String authHeader = request.getHeader("Authorization");
            String token = null;
            String username = null;

            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                // If Authorization header is missing or doesn't start with "Bearer", throw an exception
                throw new UnauthorizedException("Missing or invalid Authorization header");
            }

            token = authHeader.substring(7).trim();

            if (token.isEmpty()) {
                // If token is empty after trimming, throw an exception
                throw new UnauthorizedException("Token is empty");
            }

            // Extract username from token
            username = jwtService.extractUsername(token);

            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                if (jwtService.validateToken(token, userDetails)) {
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                } else {
                    // If token validation fails, throw an exception
                    throw new UnauthorizedException("Invalid token");
                }
            }

            filterChain.doFilter(request, response);
        }
        catch (UnauthorizedException ex) {
            handleUnauthorizedException((HttpServletResponse)response, ex.getMessage());
        } catch (MalformedJwtException ex) {
            handleUnauthorizedException((HttpServletResponse)response, ex.getMessage());
        } catch (SignatureException ex) {
            handleUnauthorizedException((HttpServletResponse)response, ex.getMessage());
        } catch (ExpiredJwtException ex) {
            handleUnauthorizedException((HttpServletResponse)response, ex.getMessage());
        }
    }

    private static void handleUnauthorizedException(HttpServletResponse response,String message) throws IOException {
        HttpServletResponse httpResponse = response;
        httpResponse.setStatus(HttpServletResponse.SC_FORBIDDEN);

        // Create a custom error response object
        CustomErrorResponse errorResponse = new CustomErrorResponse("Unauthorized", message);

        // Serialize the custom error response object to JSON
        ObjectMapper mapper = new ObjectMapper();
        String jsonErrorResponse = mapper.writeValueAsString(errorResponse);

        // Set the content type and write the JSON response
        httpResponse.setContentType(MediaType.APPLICATION_JSON_VALUE);
        httpResponse.getWriter().write(jsonErrorResponse);
    }
}