package kpi.zaranik.kexitdrive.core.client;

import kpi.zaranik.kexitdrive.core.dto.DriveFile;
import kpi.zaranik.kexitdrive.core.dto.FileListDriveResponse;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;

@HttpExchange
public interface GoogleApiClient {

    @GetExchange("drive/v3/files")
    FileListDriveResponse getListOfFiles();

    @GetExchange("drive/v3/files/{fileId}")
    DriveFile getDriveFileInfo(@PathVariable String fileId);

    @GetExchange("drive/v3/files/{fileId}?alt=media")
    Resource downloadFile(@PathVariable String fileId);

}
