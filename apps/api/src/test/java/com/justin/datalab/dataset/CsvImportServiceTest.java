package com.justin.datalab.dataset;

import com.justin.datalab.dataset.CsvImportService.ColumnAnalysis;
import com.justin.datalab.dataset.CsvImportService.CsvAnalysis;
import com.justin.datalab.dataset.CsvImportService.PreviewData;
import com.justin.datalab.shared.enums.ColumnType;
import org.junit.jupiter.api.Test;

import java.io.StringReader;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;

class CsvImportServiceTest {

    private static final String CSV = """
            id,name,score,active,joined
            1,Alice,9.5,true,2020-01-01
            2,Bob,8.0,false,2020-02-15
            3,,7.25,true,2020-03-20
            """;

    private final CsvImportService service = new CsvImportService();

    @Test
    void analyzeInfersTypesRowCountAndNullability() {
        CsvAnalysis analysis = service.analyze(new StringReader(CSV));

        assertThat(analysis.rowCount()).isEqualTo(3);
        assertThat(analysis.columns()).hasSize(5);

        Map<String, ColumnAnalysis> byName = analysis.columns().stream()
                .collect(java.util.stream.Collectors.toMap(ColumnAnalysis::name, Function.identity()));

        assertThat(byName.get("id").type()).isEqualTo(ColumnType.INTEGER);
        assertThat(byName.get("id").nullable()).isFalse();

        assertThat(byName.get("name").type()).isEqualTo(ColumnType.STRING);
        assertThat(byName.get("name").nullable()).isTrue(); // row 3 has an empty name

        assertThat(byName.get("score").type()).isEqualTo(ColumnType.DECIMAL);
        assertThat(byName.get("active").type()).isEqualTo(ColumnType.BOOLEAN);
        assertThat(byName.get("joined").type()).isEqualTo(ColumnType.DATE);
    }

    @Test
    void readPreviewReturnsLimitedRows() {
        PreviewData preview = service.readPreview(new StringReader(CSV), 2);

        assertThat(preview.columns()).containsExactly("id", "name", "score", "active", "joined");
        assertThat(preview.rows()).hasSize(2);
        assertThat(preview.rows().get(0)).containsExactly("1", "Alice", "9.5", "true", "2020-01-01");
    }

    @Test
    void blankHeadersGetGeneratedNames() {
        String csv = "a,,c\n1,2,3\n";
        CsvAnalysis analysis = service.analyze(new StringReader(csv));

        List<String> names = analysis.columns().stream().map(ColumnAnalysis::name).toList();
        assertThat(names).containsExactly("a", "column_2", "c");
    }
}
