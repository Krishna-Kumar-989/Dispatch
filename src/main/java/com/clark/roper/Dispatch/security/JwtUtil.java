package com.clark.roper.Dispatch.security;

import com.clark.roper.Dispatch.exception.UnauthorizedException;


 //Utility class for JWT-related operations.

public class JwtUtil {

    private JwtUtil() {
        // Utility class — no instantiation
    }

    /**
     * Extracts the username from a Bearer authorization header.
     * @param authHeader the Authorization header value
     * @param jwtService the JwtService to decode the token
     * @return the username embedded in the JWT
     * @throws UnauthorizedException if the header is missing or malformed
     */
    public static String extractUsernameFromAuthHeader(String authHeader, JwtService jwtService) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new UnauthorizedException("Invalid or missing Authorization header");
        }

        String jwt = authHeader.substring(7);
        return jwtService.extractUsername(jwt);
    }
}
