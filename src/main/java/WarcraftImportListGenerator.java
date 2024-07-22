import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

public class WarcraftImportListGenerator {

    private static final String WC3_ROOT_DIR = "C:\\Program Files (x86)\\Warcraft III\\_retail_";
    private static final Set<String> EXCLUDED_ROOT_FILES = Set.of(".svn", ".flavor.info", "x86_64");

    private static final String OUTPUT_PATH = "src\\main\\resources\\output\\war3campaign.imp";


    public static void main(String[] args) throws IOException {
        List<Path> pathList = scanDirectory(Paths.get(WC3_ROOT_DIR), EXCLUDED_ROOT_FILES).toList();
        ImportList importList = getImportList(pathList);
        writeImportList(importList);
    }

    private static ImportList getImportList(List<Path> pathList) {
        Path rootDirPath = Paths.get(WC3_ROOT_DIR);
        return new ImportList(1, pathList.stream().map(path -> new Import((byte) 13, rootDirPath.relativize(path).toFile().getPath())).toList());
    }

    private static void writeImportList(ImportList importList) throws IOException {
        try (FileOutputStream fileOutputStream = new FileOutputStream(OUTPUT_PATH)) {
            writeInt(fileOutputStream, importList.fileVersion());
            writeInt(fileOutputStream, importList.imports().size());
            for (Import importItem : importList.imports()) {
                writeByte(fileOutputStream, importItem.type());
                writeString(fileOutputStream, importItem.path());
            }
        }
    }

    private static void writeByte(FileOutputStream fos, byte value) throws IOException {
        fos.write(value);
    }

    private static void writeInt(FileOutputStream fos, int value) throws IOException {
        fos.write(value);
        fos.write(value >> 8);
        fos.write(value >> 16);
        fos.write(value >> 24);
    }

    private static void writeString(FileOutputStream fos, String value) throws IOException {
        fos.write(value.getBytes(StandardCharsets.UTF_8));
        fos.write('\0');
    }

    private static Stream<Path> scanDirectory(Path path, Set<String> excluded) {
        try {
            return Files.list(path)
                    .filter(p -> !excluded.contains(p.getFileName().toString()))
                    .flatMap(item -> item.toFile().isDirectory() ? scanDirectory(item, Set.of()) : Stream.of(item));
        } catch (IOException e) {
            throw new RuntimeException("Screwed path " + path, e);
        }
    }
}
