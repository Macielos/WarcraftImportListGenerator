import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Set;

public class MainBuildFromLocalFiles {

    private static final String WC3_ROOT_DIR =
            "C:\\Program Files (x86)\\Warcraft III";
    private static final Set<String> EXCLUDED_ROOT_FILES = Set.of(".svn", ".flavor.info", "x86_64", "x86", "Data", ".build.info", ".patch.result", ".product.db");

    private static final String OUTPUT_PATH = "src\\main\\files\\output\\war3campaign.imp";


    public static void main(String[] args) throws IOException {
        List<Path> pathList = DirectoryScanner.scanDirectory(Paths.get(WC3_ROOT_DIR), EXCLUDED_ROOT_FILES).toList();
        ImportList importList = WarcraftImportListGenerator.createImportList(WC3_ROOT_DIR, pathList);
        WarcraftImportListGenerator.writeImportList(OUTPUT_PATH, importList);
    }
}
