package harmonysoft.tech.chartexample.data;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import tech.harmonysoft.android.leonardo.model.DataPoint;
import tech.harmonysoft.android.leonardo.model.config.LeonardoConfigFactory;
import tech.harmonysoft.android.leonardo.model.data.ChartDataSource;

import javax.annotation.Nonnull;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;

import static java.util.Arrays.asList;

/**
 * @author Denis Zhdanov
 * @since 20/3/19
 */
public class JsonDataSourceParser {

    private static final Collection<String> LEGENDS = asList(
            "Followers", "Notifications", "Sheldons Population", "Zergs", "Wasted Hours"
    );

    private static final String NAME_COLUMNS_DATA  = "columns";
    private static final String NAME_COLUMN_TYPES  = "types";
    private static final String NAME_COLUMN_NAMES  = "names";
    private static final String NAME_COLUMN_COLORS = "colors";

    private static final String VALUE_COLUMN_X = "x";

    @Nonnull
    public Map<String, Collection<ChartDataSource>> parse(InputStream in) throws IOException, JSONException {
        ByteArrayOutputStream bOut = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int read;
        while ((read = in.read(buffer)) >= 0) {
            bOut.write(buffer, 0, read);
        }
        return parse(new String(bOut.toByteArray(), StandardCharsets.UTF_8));
    }

    @Nonnull
    public Map<String, Collection<ChartDataSource>> parse(String s) throws JSONException {
        List<String> legends = new ArrayList<>(LEGENDS);
        JSONArray root = new JSONArray(s);
        Map<String, Collection<ChartDataSource>> result = new HashMap<>();
        int counter = 0;
        for (int i = 0; i < root.length(); i++) {
            final String legend;
            if (legends.isEmpty()) {
                legend = "SomeStats" + ++counter;
            } else {
                legend = legends.remove(0);
            }
            result.put(legend, parse(root.getJSONObject(i)));
        }
        return result;
    }

    @Nonnull
    public Collection<ChartDataSource> parse(JSONObject json) throws JSONException {
        ParseContext context = new ParseContext();
        JSONArray columnsDataJson = json.getJSONArray(NAME_COLUMNS_DATA);
        for (int i = 0; i < columnsDataJson.length(); i++) {
            RawColumnData rawColumnData = parseRawColumnData(columnsDataJson.getJSONArray(i), i);
            context.columnData.put(rawColumnData.columnName, rawColumnData.values);
        }
        fillColumnTypes(json.getJSONObject(NAME_COLUMN_TYPES), context);
        fillColumnNames(json.getJSONObject(NAME_COLUMN_NAMES), context);
        fillColumnColors(json.getJSONObject(NAME_COLUMN_COLORS), context);
        validate(context);
        return build(context);
    }

    @Nonnull
    private RawColumnData parseRawColumnData(JSONArray columnJson, int i) throws JSONException {
        if (columnJson.length() <= 0) {
            throw new IllegalArgumentException(String.format(
                    "Bad input: every column data array (defined in the '%s' element) is expected to have its "
                    + "name as the first element and values as subsequent. However, column data #%d (zero-based) "
                    + "is empty", NAME_COLUMNS_DATA, i));
        }
        String columnName = columnJson.getString(0);
        List<Long> values = new ArrayList<>();
        for (int j = 1; j < columnJson.length(); j++) {
            try {
                values.add(columnJson.getLong(j));
            } catch (JSONException e) {
                throw new RuntimeException(String.format(
                        "Failed parsing data for column '%s'. Expected to get a numeric value but got '%s' "
                        + "(index %d)", columnName, columnJson.get(j), j
                ), e);
            }
        }
        return new RawColumnData(columnName, values);
    }

    private void fillColumnTypes(JSONObject json, ParseContext context) throws JSONException {
        JSONArray columnIds = json.names();
        for (int i = 0; i < columnIds.length(); i++) {
            String columnId = columnIds.getString(i);
            context.types.put(columnId.trim(), json.getString(columnId).trim());
        }
    }

    private void fillColumnNames(JSONObject json, ParseContext context) throws JSONException {
        JSONArray columnIds = json.names();
        for (int i = 0; i < columnIds.length(); i++) {
            String columnId = columnIds.getString(i);
            context.names.put(columnId.trim(), json.getString(columnId).trim());
        }
    }

    private void fillColumnColors(JSONObject json, ParseContext context) throws JSONException {
        JSONArray columnIds = json.names();
        for (int i = 0; i < columnIds.length(); i++) {
            String columnId = columnIds.getString(i);
            String colorHex = json.getString(columnId);
            int colorInt = Integer.parseInt(colorHex.trim().substring(1), 16);
            colorInt |= 0xFF000000;
            context.colors.put(columnId.trim(), colorInt);
        }
    }

    private void validate(ParseContext context) {
        validateColumnDataSizeIsConsistent(context);
        validateAllColumnsAreNamed(context);
        validateColumnTypes(context);
        validateColors(context);
    }

    private void validateColumnDataSizeIsConsistent(ParseContext context) {
        String previousColumnName = null;
        int size = -1;
        for (Map.Entry<String, List<Long>> entry : context.columnData.entrySet()) {
            if (previousColumnName == null) {
                previousColumnName = entry.getKey();
                size = entry.getValue().size();
            } else {
                if (size != entry.getValue().size()) {
                    throw new IllegalArgumentException(String.format(
                            "Expected that all data columns have the same number of values but detected that "
                            + "column '%s' has %d values and column '%s' has %d values",
                            previousColumnName, size, entry.getKey(), entry.getValue().size()
                    ));
                }
            }
        }
    }

    private void validateAllColumnsAreNamed(ParseContext context) {
        if (context.columnData.size() - 1 != context.names.size()) {
            throw new IllegalArgumentException(String.format(
                    "Found %d data columns (%s) but there are %d names (%s)",
                    context.columnData.size(), context.columnData.keySet(), context.names.size(), context.names
            ));
        }
        for (String columnId : context.columnData.keySet()) {
            if (VALUE_COLUMN_X.equals(columnId)) {
                continue;
            }
            String name = context.names.get(columnId);
            if (name == null || name.isEmpty()) {
                throw new IllegalArgumentException(String.format("No name is given for column '%s'", columnId));
            }
        }
    }

    private void validateColumnTypes(ParseContext context) {
        if (context.columnData.size() != context.types.size()) {
            throw new IllegalArgumentException(String.format(
                    "Found %d data columns (%s) but there are %d types (%s)",
                    context.columnData.size(), context.columnData.keySet(), context.types.size(), context.types
            ));
        }
        String xColumnId = null;
        for (String columnId : context.columnData.keySet()) {
            if (VALUE_COLUMN_X.equals(context.types.get(columnId))) {
                if (xColumnId != null) {
                    throw new IllegalArgumentException(String.format(
                            "Found more than one X column - '%s' and ''%s", xColumnId, columnId
                    ));
                }
                xColumnId = columnId;
            }
        }

        if (xColumnId == null) {
            throw new IllegalArgumentException(String.format(
                    "No X column is found. Available mappings: %s", context.types
            ));
        }
    }

    private void validateColors(ParseContext context) {
        if (context.columnData.size() - 1 != context.colors.size()) {
            throw new IllegalArgumentException(String.format(
                    "Found %d data columns (%s) but there are %d colors (%s)",
                    context.columnData.size(), context.columnData.keySet(), context.colors.size(), context.colors.size()
            ));
        }
        for (String columnId : context.columnData.keySet()) {
            if (VALUE_COLUMN_X.equals(columnId)) {
                continue;
            }
            if (!context.colors.containsKey(columnId)) {
                throw new IllegalArgumentException(String.format("No color is provided for column '%s'", columnId));
            }
        }
    }

    @Nonnull
    private Collection<ChartDataSource> build(ParseContext context) {
        Collection<ChartDataSource> result = new ArrayList<>();
        List<Long> xValues = getXValues(context);
        for (Map.Entry<String, List<Long>> entry : context.columnData.entrySet()) {
            if (VALUE_COLUMN_X.equals(entry.getKey())) {
                continue;
            }

            List<DataPoint> dataPoints = new ArrayList<>();
            for (int i = 0; i < entry.getValue().size(); i++) {
                Long y = entry.getValue().get(i);
                dataPoints.add(new DataPoint(xValues.get(i), y));
            }

            String legend = context.names.get(entry.getKey());
            if (legend == null) {
                throw new IllegalArgumentException(String.format(
                        "No legend is defined for data source '%s'", entry.getKey()
                ));
            }

            Integer color = context.colors.get(entry.getKey());
            if (color == null) {
                throw new IllegalArgumentException(String.format(
                        "No color is defined for column '%s'", entry.getKey()
                ));
            }

            result.add(LeonardoConfigFactory.newDataSourceBuilder()
                                            .withLoader(new PreDefinedChartDataLoader(dataPoints))
                                            .withLegend(legend)
                                            .withColor(color)
                                            .withMinX(xValues.get(0))
                                            .withMaxX(xValues.get(xValues.size() - 1))
                                            .build());
        }
        return result;
    }

    private List<Long> getXValues(ParseContext context) {
        for (Map.Entry<String, String> entry : context.types.entrySet()) {
            if (VALUE_COLUMN_X.equals(entry.getValue())) {
                return context.columnData.get(entry.getKey());
            }
        }
        throw new IllegalArgumentException(String.format("No X column is found in '%s'", context));
    }

    private static class RawColumnData {

        public final List<Long> values;
        public final String     columnName;

        public RawColumnData(String columnName, List<Long> values) {
            this.values = values;
            this.columnName = columnName;
        }
    }

    private static class ParseContext {
        public final Map<String, List<Long>> columnData = new HashMap<>();
        public final Map<String, String>     types      = new HashMap<>();
        public final Map<String, String>     names      = new HashMap<>();
        public final Map<String, Integer>    colors     = new HashMap<>();
    }
}
