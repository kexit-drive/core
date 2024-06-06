package kpi.zaranik.kexitdrive.core.service.file;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class TikaServiceTest {

    TikaService tikaService = new TikaService();

    @Test
    void xlsxFile() throws FileNotFoundException {
        FileInputStream fis = new FileInputStream("src/test/resources/test.xlsx");
        String dataType = tikaService.getDataType(fis);
        Assertions.assertThat(dataType).isEqualTo("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
    }

    @Test
    void docxFile() throws FileNotFoundException {
        FileInputStream fis = new FileInputStream("src/test/resources/test.docx");
        String dataType = tikaService.getDataType(fis);
        Assertions.assertThat(dataType).isEqualTo("application/vnd.openxmlformats-officedocument.wordprocessingml.document");
    }

    @Test
    void pptxFile() throws FileNotFoundException {
        FileInputStream fis = new FileInputStream("src/test/resources/test.pptx");
        String dataType = tikaService.getDataType(fis);
        Assertions.assertThat(dataType).isEqualTo("application/x-tika-ooxml");
    }

    @Test
    void fb2File() throws FileNotFoundException {
        FileInputStream fis = new FileInputStream("src/test/resources/test.fb2");
        String dataType = tikaService.getDataType(fis);
        Assertions.assertThat(dataType).isEqualTo("application/x-fictionbook+xml");
    }

}
