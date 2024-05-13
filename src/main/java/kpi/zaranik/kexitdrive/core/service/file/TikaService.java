package kpi.zaranik.kexitdrive.core.service.file;

import java.io.InputStream;
import lombok.SneakyThrows;
import org.apache.tika.Tika;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class TikaService {

    private final Tika tika;

    public TikaService() {
        this.tika = new Tika();
    }

    @SneakyThrows
    public String getDataType(InputStream inputStream)  {
        return tika.detect(inputStream);
    }

    @SneakyThrows
    public String getDataType(MultipartFile multipartFile)  {
        return tika.detect(multipartFile.getInputStream());
    }

    @SneakyThrows
    public String getDataType(GridFsResource resource)  {
        return tika.detect(resource.getInputStream());
    }

}
