package cgg.informatique.jfl.webSocket.configurations;

import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import org.springframework.web.util.ContentCachingRequestWrapper;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public final class RestAuthenticationEntryPoint
        implements AuthenticationEntryPoint {

    @Override
    public void commence(
            final HttpServletRequest request,
            final HttpServletResponse response,
            final AuthenticationException authException) throws IOException {

        String typeDeRequete = new String();


        typeDeRequete = request.getHeader("rest");
        String username = request.getHeader("username");
        if ((typeDeRequete != null) && (typeDeRequete.equals("oui")))
        {
            System.out.println("commence():" + username + "REST");
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED,
                    "Unauthorized par JFL");
        }
        else
        {
            System.out.println("commence():" + "WEB");
            response.sendRedirect("/login");
        }


    }


}