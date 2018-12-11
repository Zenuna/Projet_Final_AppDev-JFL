package cgg.informatique.jfl.webSocket.configurations;

import cgg.informatique.jfl.webSocket.controleurs.ReponseControleur;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;
import org.springframework.util.StringUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class MySavedRequestAwareAuthenticationSuccessHandler
        extends SimpleUrlAuthenticationSuccessHandler {

    private RequestCache requestCache = new HttpSessionRequestCache();



    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication)
            throws ServletException, IOException {

        String typeDeRequete = new String();
        typeDeRequete = request.getHeader("rest");

        if ((typeDeRequete != null) && (typeDeRequete.equals("oui")))
        {
            SavedRequest savedRequest
                    = requestCache.getRequest(request, response);

            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String courriel = auth.getName();
            Boolean sessionRestPasUnique = true;
            String sessionRest = new String();
            while(sessionRestPasUnique) {
                sessionRestPasUnique = false;
                sessionRest = String.valueOf(((long) (Math.random() * (10000000))));
                for (String cle:  ReponseControleur.listeDesConnexions.keySet())
                    if(ReponseControleur.listeDesConnexions.get(cle).equals(sessionRest))
                        sessionRestPasUnique = true;
            }



            ReponseControleur.listeDesConnexions.put(courriel, sessionRest);
            response.addHeader("SESSIONREST",sessionRest);


            System.out.println("onAuthenticationSuccess():0"+ courriel);
            if (savedRequest == null) {
                System.out.println("onAuthenticationSuccess():1" );
                clearAuthenticationAttributes(request);
                System.out.println("onAuthenticationSuccess():2" );
                return;
            }
            String targetUrlParam = getTargetUrlParameter();
            System.out.println("onAuthenticationSuccess():3" + targetUrlParam);
            if (isAlwaysUseDefaultTargetUrl()
                    || (targetUrlParam != null
                    && StringUtils.hasText(request.getParameter(targetUrlParam)))) {
                requestCache.removeRequest(request, response);
                clearAuthenticationAttributes(request);
                System.out.println("onAuthenticationSuccess():4" + targetUrlParam);
                return;
            }
            System.out.println("onAuthenticationSuccess():5" + targetUrlParam);
            clearAuthenticationAttributes(request);
        }
        else
        {
            System.out.println("onAuthenticationSuccess():6" );
            SimpleUrlAuthenticationSuccessHandler s = new SimpleUrlAuthenticationSuccessHandler();
            s.onAuthenticationSuccess(request,
                     response,
                    authentication);
        }
    }


    public void setRequestCache(RequestCache requestCache) {
        this.requestCache = requestCache;
    }
}