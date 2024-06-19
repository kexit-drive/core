//package kpi.zaranik.kexitdrive.core.service.file;
//
//import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.mockOidcLogin;
//import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.springSecurity;
//
//import kpi.zaranik.kexitdrive.core.BaseIntegrationTest;
//import kpi.zaranik.kexitdrive.core.repository.FileEntityRepository;
//import lombok.extern.slf4j.Slf4j;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
//import org.springframework.context.ApplicationContext;
//import org.springframework.security.core.authority.AuthorityUtils;
//import org.springframework.security.oauth2.core.oidc.OidcIdToken;
//import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
//import org.springframework.security.oauth2.core.oidc.user.OidcUser;
//import org.springframework.test.web.reactive.server.WebTestClient;
//
//@Slf4j
//@AutoConfigureWebTestClient
//public class FileServiceIntegrationTest extends BaseIntegrationTest {
//
//    @Autowired
//    FileService fileService;
//
//    @Autowired
//    FileEntityRepository fileEntityRepository;
//
//    WebTestClient webTestClient;
//
//    @Autowired
//    private ApplicationContext context;
//
//
//    @BeforeEach
//    void setup() {
//        webTestClient = WebTestClient.bindToApplicationContext(context)
//            .apply(springSecurity())
//            .configureClient()
//            .build();
//    }
//
//    @Test
//    void testUser() {
//        OidcUser oidcUser = new DefaultOidcUser(
//            AuthorityUtils.createAuthorityList("SCOPE_openid", "SCOPE_email", "SCOPE_profile", "SCOPE_https://www.googleapis.com/auth/drive.readonly"),
//            OidcIdToken.withTokenValue("id_token").subject("0000").build()
//        );
//        webTestClient.mutateWith(mockOidcLogin().oidcUser(oidcUser))
//            .get()
//            .uri("/user/current")
//            .exchange()
//            .expectStatus().isOk();
//
////        Assertions.assertThat(userInfo.externalId()).isEqualTo("0000");
//    }
//
////    @Test
////    void testCreationOfDirectory() {
////        // given
////        FileEntity main = fileEntityRepository.save(FileEntity.createDirectory("main", null));
////
////        // when
////        var request = new CreateDirectoryRequest(null, null);
////        fileService.createDirectory(request, UserInfo.builder()
////                .externalId("")
////            .build());
////
////
////        // then
////
////    }
//
//}
