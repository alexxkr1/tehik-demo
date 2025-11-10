package com.example.tehik.utils;

import jakarta.servlet.http.HttpServletRequest;

public class RequestUtil {
    /**
     * Extracts the client's real IP address, checking common proxy headers
     * (X-Forwarded-For) before falling back to the direct remote address.
     */
    public static String getClientIpAddress(HttpServletRequest request) {

        String xfHeader = request.getHeader("X-Forwarded-For");

        if (xfHeader != null && !xfHeader.isEmpty()) {
            return xfHeader.split(",")[0].trim();
        }

        return request.getRemoteAddr();
    }
}
