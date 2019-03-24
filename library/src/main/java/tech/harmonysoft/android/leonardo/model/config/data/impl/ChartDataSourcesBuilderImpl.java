package tech.harmonysoft.android.leonardo.model.config.data.impl;

import android.graphics.Color;
import tech.harmonysoft.android.leonardo.model.config.data.ChartDataSourceBuilder;
import tech.harmonysoft.android.leonardo.model.config.data.ChartDataSourcesBuilder;
import tech.harmonysoft.android.leonardo.model.data.ChartDataSource;

import javax.annotation.Nonnull;
import java.util.*;

/**
 * @author Denis Zhdanov
 * @since 16/3/19
 */
public class ChartDataSourcesBuilderImpl implements ChartDataSourcesBuilder {

    private final Collection<ChartDataSource>        mDataSources        = new ArrayList<>();
    private final Collection<ChartDataSourceBuilder> mDataSourceBuilders = new ArrayList<>();

    @Nonnull
    @Override
    public ChartDataSourcesBuilder addDataSource(ChartDataSource dataSource) {
        mDataSources.add(dataSource);
        return this;
    }

    @Nonnull
    @Override
    public ChartDataSourcesBuilder addDataSourceBuilder(ChartDataSourceBuilder builder) {
        mDataSourceBuilders.add(builder);
        return this;
    }

    @Nonnull
    @Override
    public Collection<ChartDataSource> build() {
        Collection<ChartDataSourceBuilder> dataSourceBuilders = new ArrayList<>(mDataSourceBuilders);
        Collection<ChartDataSource> result = new ArrayList<>(mDataSources);
        if (dataSourceBuilders.isEmpty()) {
            return result;
        }

        Stack<Integer> availableColorsStack = getDefaultColors();
        Set<Integer> availableColorsSet = new HashSet<>(availableColorsStack);
        for (ChartDataSource dataSource : mDataSources) {
            availableColorsSet.remove(dataSource.getColor());
        }

        for (ChartDataSourceBuilder builder : dataSourceBuilders) {
            if (availableColorsSet.isEmpty()) {
                availableColorsStack = getDefaultColors();
                availableColorsSet = new HashSet<>(availableColorsStack);
            }

            if (!builder.hasColor()) {
                while (!availableColorsStack.isEmpty()) {
                    Integer color = availableColorsStack.pop();
                    if (availableColorsSet.remove(color)) {
                        builder.withColor(color);
                        break;
                    }
                }
            }

            result.add(builder.build());
        }

        return result;
    }

    @Nonnull
    private static Stack<Integer> getDefaultColors() {
        Stack<Integer> result = new Stack<>();
        result.push(Color.rgb(0xFC, 0x66, 0x1A));
        result.push(Color.rgb(0xFC, 0xB5, 0x1A));
        result.push(Color.rgb(0xCF, 0xE6, 0x45));
        result.push(Color.rgb(0x45, 0xC2, 0xE6));
        result.push(Color.rgb(0xC4, 0x5E, 0xD9));
        result.push(Color.rgb(0x59, 0x59, 0xBA));
        result.push(Color.rgb(0xED, 0x69, 0x5E));
        result.push(Color.rgb(0x3C, 0xC2, 0x3F));
        return result;
    }
}
