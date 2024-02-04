package kpi.zaranik.kexitdrive.core.mapper;

import kpi.zaranik.kexitdrive.core.dto.UserInfo;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;

@Service
public class UserMapper {

    public UserInfo mapFromOidcUser(OidcUser user) {
        return UserInfo.builder()
            .externalId(user.getSubject())
            .name(user.getFullName())
            .firstName(user.getGivenName())
            .lastName(user.getFamilyName())
            .pictureLink(user.getPicture())
            .build();
    }

}
