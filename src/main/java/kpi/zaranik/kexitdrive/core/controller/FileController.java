package kpi.zaranik.kexitdrive.core.controller;

import kpi.zaranik.kexitdrive.core.config.security.CurrentUser;
import kpi.zaranik.kexitdrive.core.dto.UserInfo;
import kpi.zaranik.kexitdrive.core.dto.file.FileInfo;
import kpi.zaranik.kexitdrive.core.service.FileManagementService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("file")
@RequiredArgsConstructor
public class FileController {

    private final FileManagementService fileService;

    @GetMapping("upload")
    public FileInfo uploadFromComputer(@CurrentUser UserInfo user, @RequestBody MultipartFile file) {
        return fileService.uploadFromComputer(user, file);
    }

}
