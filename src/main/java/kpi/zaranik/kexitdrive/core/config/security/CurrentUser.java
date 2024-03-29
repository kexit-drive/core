package kpi.zaranik.kexitdrive.core.config.security;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.PARAMETER, ElementType.ANNOTATION_TYPE})
@AuthenticationPrincipal(expression = "@userMapper.mapFromOidcUser(#this)")
public @interface CurrentUser {

}
