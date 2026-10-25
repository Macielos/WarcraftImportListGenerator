import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

public record ImportList(int fileVersion, List<Import> imports) {
    public ImportList merge(ImportList extraImportList) {
        return new ImportList(fileVersion, Stream.concat(imports.stream(), extraImportList.imports().stream()).sorted(Comparator.comparing(Import::path)).distinct().toList());
    }
}
