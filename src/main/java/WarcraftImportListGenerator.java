import java.io.ByteArrayInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class WarcraftImportListGenerator {

    public static ImportList createImportList(String rootDirectory, List<Path> pathList) {
        Path rootDirPath = Paths.get(rootDirectory);
        return new ImportList(1, pathList.stream().map(path -> new Import((byte) 13, rootDirPath.relativize(path).toFile().getPath())).toList());
    }

    public static void writeImportList(String outputPath, ImportList importList) throws IOException {
        try (FileOutputStream fileOutputStream = new FileOutputStream(outputPath)) {
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

    public static ImportList readImportList(String impFile) throws IOException {
        var bytes = Files.readAllBytes(Paths.get(impFile));
        try (ByteArrayInputStream stream = new ByteArrayInputStream(bytes)) {
            int version = stream.read();
            int numberOfImports = stream.read();
            var remainingBytes = stream.readAllBytes();
            var string = new String(remainingBytes, StandardCharsets.UTF_8);
            String[] files = string.split("\r");
//            System.out.println("Files: " + String.join("\n", files));
            var map = Stream.of(files).skip(1)
                    .map(String::trim)
                    .map(path -> new Import((byte) 13, path))
                    .collect(Collectors.toMap(Import::path,
                            Function.identity(),
                            (anImport, anImport2) -> anImport));
            return new ImportList(version, map.values().stream().sorted(Comparator.comparing(Import::path)).toList());
        }
    }
}
