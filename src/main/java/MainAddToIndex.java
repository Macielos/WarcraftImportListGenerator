import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Set;

public class MainAddToIndex {

    private static final String INPUT_FILE = "src\\main\\files\\input\\war3campaign.imp";
    private static final String OUTPUT_PATH = "src\\main\\files\\output\\war3campaign.imp";
    private static final String EXTRA_FILES_ROOT_DIR = "C:\\path\\to\\stuff\\to\\import";

    public static void main(String[] args) throws IOException {
        ImportList existingImportList = WarcraftImportListGenerator.readImportList(INPUT_FILE);
//        System.out.println(importList);
        List<Path> pathList = DirectoryScanner.scanDirectory(Paths.get(EXTRA_FILES_ROOT_DIR), Set.of()).toList();
        ImportList extraImportList = WarcraftImportListGenerator.createImportList(EXTRA_FILES_ROOT_DIR, pathList);
        ImportList mergedImportList = existingImportList.merge(extraImportList);
        WarcraftImportListGenerator.writeImportList(OUTPUT_PATH, mergedImportList);
    }
}
