package kpi.zaranik.kexitdrive.core.controller;

import kpi.zaranik.kexitdrive.core.config.security.CurrentUser;
import kpi.zaranik.kexitdrive.core.dto.UserInfo;
import kpi.zaranik.kexitdrive.core.dto.file.FileInfo;
import kpi.zaranik.kexitdrive.core.dto.file.importing.ImportFilesRequest;
import kpi.zaranik.kexitdrive.core.service.file.FileManagementService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
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

    @PostMapping("import")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void importFilesFromGoogleDrive(@CurrentUser UserInfo user, @RequestBody ImportFilesRequest request) {
        fileService.importFilesFromGoogleDrive(user, request);
    }
}
