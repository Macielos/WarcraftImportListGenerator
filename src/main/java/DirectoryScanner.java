import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;
import java.util.stream.Stream;

public class DirectoryScanner {

    public static Stream<Path> scanDirectory(Path path, Path rootPath, Set<String> excluded) {
        try {
            return Files.list(path)
                    .filter(p ->  !isExcluded(path, rootPath, excluded))
                    .flatMap(item -> item.toFile().isDirectory()
                            ? scanDirectory(item, rootPath, excluded)
                            : isExcluded(item, rootPath, excluded)
                            ? Stream.of()
                            : Stream.of(item));
        } catch (IOException e) {
            throw new RuntimeException("Screwed path " + path, e);
        }
    }

    private static boolean isExcluded(Path path, Path rootPath, Set<String> excluded) {
        if(excluded.contains(rootPath.relativize(path).toString())) {
            System.out.println("Path excluded: " + path);
            return true;
        }
        return false;
    }

}
