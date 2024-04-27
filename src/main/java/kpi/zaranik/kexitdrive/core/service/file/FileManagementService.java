package kpi.zaranik.kexitdrive.core.service.file;

import java.util.List;
import kpi.zaranik.kexitdrive.core.dto.UserInfo;
import kpi.zaranik.kexitdrive.core.dto.file.FileInfo;
import kpi.zaranik.kexitdrive.core.dto.file.importing.ImportFilesRequest;
import kpi.zaranik.kexitdrive.core.dto.file.importing.ImportingMessage;
import kpi.zaranik.kexitdrive.core.misc.Constants;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class FileManagementService {

    private final RabbitTemplate rabbitTemplate;

    public FileInfo uploadFromComputer(UserInfo user, MultipartFile file) {
        return null;
    }


    // 111401278168521168568
    public void importFilesFromGoogleDrive(UserInfo user, ImportFilesRequest request) {
        List<String> fileIds = request.fileIds();
        fileIds.stream()
            .map(id -> new ImportingMessage(user.externalId(), id))
            .forEach(message -> rabbitTemplate.convertAndSend(Constants.IMPORT_FILES_QUEUE, message));
    }

}
