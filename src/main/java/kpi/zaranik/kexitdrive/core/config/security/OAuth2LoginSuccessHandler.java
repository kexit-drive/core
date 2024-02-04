package kpi.zaranik.kexitdrive.core.config.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

@Component
public class OAuth2LoginSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws ServletException, IOException {
        this.setAlwaysUseDefaultTargetUrl(true);
        this.setDefaultTargetUrl("http://localhost:63342/converter-djvu/src/main/resources/frontend.html");
        super.onAuthenticationSuccess(request, response, authentication);
    }
}
