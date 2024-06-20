package kpi.zaranik.kexitdrive.core.service.file;

import static kpi.zaranik.kexitdrive.core.misc.Constants.CONVERT_FROM_PROPERTY_NAME;
import static kpi.zaranik.kexitdrive.core.misc.Constants.CONVERT_TO_PROPERTY_NAME;

import com.mongodb.client.gridfs.model.GridFSFile;
import jakarta.validation.Valid;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;
import kpi.zaranik.kexitdrive.core.dto.UserInfo;
import kpi.zaranik.kexitdrive.core.dto.file.CreateDirectoryRequest;
import kpi.zaranik.kexitdrive.core.dto.file.FileResponse;
import kpi.zaranik.kexitdrive.core.dto.file.MessageResponse;
import kpi.zaranik.kexitdrive.core.dto.file.PlayableResourceResponse;
import kpi.zaranik.kexitdrive.core.dto.file.PlayerDataTypeResponse;
import kpi.zaranik.kexitdrive.core.dto.file.UploadedFileResponse;
import kpi.zaranik.kexitdrive.core.dto.file.importing.ImportFilesRequest;
import kpi.zaranik.kexitdrive.core.dto.file.importing.ImportingMessage;
import kpi.zaranik.kexitdrive.core.dto.file.move.MoveRequest;
import kpi.zaranik.kexitdrive.core.entity.FileEntity;
import kpi.zaranik.kexitdrive.core.entity.PlayerSupportedContentType;
import kpi.zaranik.kexitdrive.core.exception.AccessToResourceDeniedException;
import kpi.zaranik.kexitdrive.core.exception.NoSuitableConverterServiceFoundException;
import kpi.zaranik.kexitdrive.core.exception.ResourceNotFoundException;
import kpi.zaranik.kexitdrive.core.mapper.FileEntityMapper;
import kpi.zaranik.kexitdrive.core.misc.Constants;
import kpi.zaranik.kexitdrive.core.repository.FileEntityRepository;
import kpi.zaranik.kexitdrive.core.service.user.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.core.io.Resource;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.client.WebClient;

@Slf4j
@Service
@Validated
@RequiredArgsConstructor
public class FileService {

    private final RabbitTemplate rabbitTemplate;
    private final GridFsTemplate gridFsTemplate;
    private final TikaService tikaService;
    private final FileEntityRepository fileRepository;
    private final UserService userService;
    private final FileEntityMapper fileEntityMapper;
    private final DiscoveryClient discoveryClient;
    @Qualifier("loadBalancedWebClientBuilder")
    private final WebClient.Builder webClientBuilder;

    public UploadedFileResponse uploadFile(MultipartFile file, String containingDirectoryId) {
        try (InputStream fileInputStream = file.getInputStream()) {
            String originalFilename = file.getOriginalFilename();
            String contentType = tikaService.getDataType(file);
            String gridFsFileId = gridFsTemplate.store(fileInputStream, originalFilename, contentType).toString();
            FileEntity uploadedFile = FileEntity.createUploadedFile(originalFilename, gridFsFileId, contentType, containingDirectoryId);
            uploadedFile = fileRepository.save(uploadedFile);
            return new UploadedFileResponse(uploadedFile.id());
        } catch (IOException exception) {
            log.error("Error while uploading file", exception);
            throw new RuntimeException(exception); // TODO
        }
    }

    // 111401278168521168568
    public void importFilesFromGoogleDrive(UserInfo user, ImportFilesRequest request) {
        List<String> fileIds = request.fileIds();
        String directoryId = request.containingDirectoryId();
        fileIds.stream()
            .map(id -> new ImportingMessage(user.externalId(), id, directoryId))
            .forEach(message -> rabbitTemplate.convertAndSend(Constants.IMPORT_FILES_QUEUE, message));
    }

    public FileResponse getFileById(String fileId) {
        FileEntity fileEntity = getFileEntity(fileId);
        return fileEntityMapper.mapToResponse(fileEntity);
    }

    public Resource getFileResourceById(String fileId) {
        FileEntity fileEntity = getFileEntity(fileId);
        String gridFsFileId = fileEntity.metadata().gridFsFileId();
        GridFSFile gridFsFile = Optional.ofNullable(gridFsTemplate.findOne(new Query(Criteria.where("_id").is(gridFsFileId))))
            .orElseThrow(() -> new ResourceNotFoundException("Unable to find file with fileId " + fileEntity.id() + " and gridFsFileId = " + gridFsFileId));
        return gridFsTemplate.getResource(gridFsFile);
    }

    public void deleteFileById(String fileId) {
        FileEntity fileEntity = getFileEntity(fileId);
        if (fileEntity.isDirectory()) {
            List<FileEntity> innerFiles = fileRepository.findByContainingDirectoryIdIs(fileId);
            innerFiles.stream().map(FileEntity::id).forEach(this::deleteFileById);
        } else {
            String gridFsFileId = fileEntity.metadata().gridFsFileId();
            gridFsTemplate.delete(new Query(Criteria.where("_id").is(gridFsFileId)));
        }
        fileRepository.delete(fileEntity);
        log.info("Deleted file {}", fileId);
    }

    private FileEntity getFileEntity(String fileId) {
        FileEntity fileEntity = fileRepository.findById(fileId).orElseThrow(ResourceNotFoundException::new);
        String currentUserExternalId = userService.getCurrentUserExternalId().orElseThrow();
        if (!fileEntity.ownerUserExternalId().equals(currentUserExternalId)) {
            throw new AccessToResourceDeniedException("user with id " + currentUserExternalId + " is not allowed to access file " + fileEntity.id());
        }
        return fileEntity;
    }

    public PlayerDataTypeResponse getPlayerContentType(String fileId) {
        FileEntity fileEntity = getFileEntity(fileId);
        if (fileEntity.isDirectory()) { // TODO move to AOP validation
            throw new UnsupportedOperationException("This operation is not supported for directory with id " + fileId);
        }
        Optional<PlayerSupportedContentType> supportedContentType = PlayerSupportedContentType.getForMimeType(fileEntity.metadata().contentType());
        if (supportedContentType.isPresent()) {
            return new PlayerDataTypeResponse(supportedContentType.get());
        }

        PlayerSupportedContentType playerSupportedContentType = getConverterServiceInstance(fileEntity.metadata().contentType())
            .flatMap(instance -> PlayerSupportedContentType.getForMimeType(instance.getMetadata().get(CONVERT_TO_PROPERTY_NAME)))
            .orElse(PlayerSupportedContentType.UNSUPPORTED);
        return new PlayerDataTypeResponse(playerSupportedContentType);
    }

    public PlayableResourceResponse getPlayableResource(String fileId) {
        FileEntity fileEntity = getFileEntity(fileId);
        if (fileEntity.isDirectory()) {
            throw new UnsupportedOperationException("This operation is not supported for directory with id " + fileId);
        }

        Resource resource = getFileResourceById(fileId);

        String originalContentType = fileEntity.metadata().contentType();
        Optional<PlayerSupportedContentType> supportedContentType = PlayerSupportedContentType.getForMimeType(originalContentType);
        if (supportedContentType.isPresent()) {
            return new PlayableResourceResponse(resource, originalContentType);
        }

        ServiceInstance converterServiceInstance = getConverterServiceInstance(originalContentType)
            .orElseThrow(() -> new NoSuitableConverterServiceFoundException("No suitable converter service found for " + fileEntity.id()));
        Resource convertedResource = requestResource(converterServiceInstance, resource);
        String convertToContentType = converterServiceInstance.getMetadata().get(CONVERT_TO_PROPERTY_NAME);
        return new PlayableResourceResponse(convertedResource, convertToContentType);
    }

    private Resource requestResource(ServiceInstance converterServiceInstance, Resource originalResource) {
        String serviceId = converterServiceInstance.getServiceId();
        WebClient webClient = webClientBuilder.baseUrl("lb://" + serviceId).build();

        MultipartBodyBuilder builder = new MultipartBodyBuilder();
        builder
            .part("file", originalResource);
        MultiValueMap<String, HttpEntity<?>> multipartBody = builder.build();

        return webClient.post()
            .uri("convert")
            .bodyValue(multipartBody)
            .retrieve()
            .bodyToMono(Resource.class)
            .block();
    }

    private Optional<ServiceInstance> getConverterServiceInstance(String contentType) {
        List<ServiceInstance> convertFrom = discoveryClient.getServices().stream()
            .flatMap(serviceId -> discoveryClient.getInstances(serviceId).stream())
            .filter(instance -> {
                Map<String, String> instanceMetadata = instance.getMetadata();
                List<String> convertFromContentTypes = instanceMetadata.keySet().stream()
                    .filter(key -> key.startsWith(CONVERT_FROM_PROPERTY_NAME))
                    .map(instanceMetadata::get)
                    .toList();
                return convertFromContentTypes.contains(contentType);
            })
            .toList();
        return convertFrom.stream().findFirst();
    }

    public List<FileResponse> getAllFiles(String directoryId) {
        String currentUserExternalId = userService.getCurrentUserExternalId().orElseThrow();
        Stream<FileEntity> stream;
        if (directoryId == null) {
            stream = fileRepository.findByOwnerUserExternalIdAndContainingDirectoryIdIsNullOrderByCreatedDesc(currentUserExternalId);
        } else {
            stream = fileRepository.findByOwnerUserExternalIdAndContainingDirectoryIdOrderByCreatedDesc(currentUserExternalId, directoryId);
        }
        return stream.map(fileEntityMapper::mapToResponse).toList();
    }

    public FileResponse createDirectory(@Valid CreateDirectoryRequest request, String userExternalId) {
        if (request.containingDirectoryId() != null) {
            FileEntity containingDirectory = fileRepository.findById(request.containingDirectoryId())
                .orElseThrow(() -> new ResourceNotFoundException("directory with id " + request.containingDirectoryId() + " not found"));
            boolean isOwner = containingDirectory.ownerUserExternalId().equals(userExternalId);
            if (!isOwner) {
                log.error("user with id {} is not allowed to create a new directory in directory {}", userExternalId, containingDirectory.id());
                throw new AccessToResourceDeniedException(
                    "user with id " + userExternalId + " is not allowed to create a new directory in directory " + containingDirectory.id());
            }
        }
        FileEntity newDirectory = FileEntity.createDirectory(request.name(), request.containingDirectoryId());
        newDirectory = fileRepository.save(newDirectory);
        return fileEntityMapper.mapToResponse(newDirectory);
    }

    public MessageResponse move(MoveRequest request) {
        if (request.fileId().equals(request.destinationDirectoryId())) {
            throw new IllegalArgumentException("destination directory id cannot be the same as the file id");
        }
        FileEntity fileToMove = fileRepository.findById(request.fileId())
            .orElseThrow(() -> new ResourceNotFoundException("file with id " + request.fileId() + " not found"));
        FileEntity destinationDirectory = fileRepository.findById(request.destinationDirectoryId())
            .orElseThrow(() -> new ResourceNotFoundException("file with id " + request.fileId() + " not found"));
        fileRepository.setNewDirectoryId(fileToMove.id(), destinationDirectory.id());
        return new MessageResponse("file with id " + request.fileId() + " moved to directory " + request.destinationDirectoryId());
    }
}
