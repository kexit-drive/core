package kpi.zaranik.kexitdrive.core.controller;

import java.nio.charset.StandardCharsets;
import java.util.List;
import kpi.zaranik.kexitdrive.core.config.security.CurrentUser;
import kpi.zaranik.kexitdrive.core.dto.UserInfo;
import kpi.zaranik.kexitdrive.core.dto.file.CreateDirectoryRequest;
import kpi.zaranik.kexitdrive.core.dto.file.FileResponse;
import kpi.zaranik.kexitdrive.core.dto.file.PlayableResourceResponse;
import kpi.zaranik.kexitdrive.core.dto.file.PlayerDataTypeResponse;
import kpi.zaranik.kexitdrive.core.dto.file.UploadedFileResponse;
import kpi.zaranik.kexitdrive.core.dto.file.importing.ImportFilesRequest;
import kpi.zaranik.kexitdrive.core.service.file.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("file")
@RequiredArgsConstructor
public class FileController {

    private final FileService fileService;

    @GetMapping
    public List<FileResponse> getAllFiles(@RequestParam(required = false) String directoryId) { // add directory id
        return fileService.getAllFiles(directoryId);
    }

    @GetMapping("{id}")
    public FileResponse getFileById(@PathVariable String id) {
        return fileService.getFileById(id);
    }

    @GetMapping("{id}/download")
    public ResponseEntity<Resource> downloadFileById(@PathVariable String id) {
        FileResponse file = fileService.getFileById(id);
        Resource resource = fileService.getFileResourceById(id);
        return ResponseEntity.ok()
            .contentType(MediaType.parseMediaType(file.metadata().contentType()))
            .header(HttpHeaders.CONTENT_DISPOSITION, ContentDisposition.attachment().filename(resource.getFilename(), StandardCharsets.UTF_8).build().toString())
            .body(resource);
    }

    @GetMapping("{id}/player-content-type")
    public PlayerDataTypeResponse getPlayerContentType(@PathVariable String id) {
        return fileService.getPlayerContentType(id);
    }

    @GetMapping("{id}/play")
    public ResponseEntity<Resource> getPlayableResource(@PathVariable String id) {
        PlayableResourceResponse response = fileService.getPlayableResource(id);
        Resource resource = response.resource();
        String contentType = response.contentType();
        return ResponseEntity.ok()
            .contentType(MediaType.parseMediaType(contentType))
            .body(resource);
    }

    @PostMapping("upload")
    public UploadedFileResponse uploadFile(@RequestPart MultipartFile file, @RequestParam(required = false) String directoryId) {
        return fileService.uploadFile(file, directoryId);
    }

    @PostMapping("import")
    public void importFilesFromGoogleDrive(@CurrentUser UserInfo user, @RequestBody ImportFilesRequest request) {
        fileService.importFilesFromGoogleDrive(user, request);
    }

    @DeleteMapping("{id}")
    public void deleteFileById(@PathVariable String id) {
        fileService.deleteFileById(id);
    }

    @PostMapping("create-directory")
    public FileResponse createDirectory(@RequestBody CreateDirectoryRequest request, @CurrentUser UserInfo user) {
        return fileService.createDirectory(request, user.externalId());
    }

}
