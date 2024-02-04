package kpi.zaranik.kexitdrive.core.service;

import kpi.zaranik.kexitdrive.core.dto.UserInfo;
import kpi.zaranik.kexitdrive.core.dto.file.FileInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class FileManagementService {

    public FileInfo uploadFromComputer(UserInfo user, MultipartFile file) {
        return null;
    }

}
