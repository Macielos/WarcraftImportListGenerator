import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;
import java.util.stream.Stream;

public class DirectoryScanner {

    public static Stream<Path> scanDirectory(Path path, Set<String> excluded) {
        try {
            return Files.list(path)
                    .filter(p -> !excluded.contains(p.getFileName().toString()))
                    .flatMap(item -> item.toFile().isDirectory() ? scanDirectory(item, Set.of()) : Stream.of(item));
        } catch (IOException e) {
            throw new RuntimeException("Screwed path " + path, e);
        }
    }

}
