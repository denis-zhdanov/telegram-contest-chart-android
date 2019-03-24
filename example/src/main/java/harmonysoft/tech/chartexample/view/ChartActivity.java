package harmonysoft.tech.chartexample.view;

import android.app.Activity;
import android.os.Bundle;
import android.util.SparseArray;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import harmonysoft.tech.chartexample.R;
import harmonysoft.tech.chartexample.data.JsonDataSourceParser;
import tech.harmonysoft.android.leonardo.log.LogUtil;
import tech.harmonysoft.android.leonardo.model.config.LeonardoConfigFactory;
import tech.harmonysoft.android.leonardo.model.config.axis.AxisConfig;
import tech.harmonysoft.android.leonardo.model.config.axis.impl.TimeAxisLabelTextStrategy;
import tech.harmonysoft.android.leonardo.model.config.chart.ChartConfig;
import tech.harmonysoft.android.leonardo.model.data.ChartDataSource;
import tech.harmonysoft.android.leonardo.model.runtime.ChartModel;
import tech.harmonysoft.android.leonardo.util.LeonardoUtil;
import tech.harmonysoft.android.leonardo.view.ChartView;
import tech.harmonysoft.android.leonardo.view.NavigatorChartView;
import tech.harmonysoft.android.leonardo.view.selector.ChartSelectorView;

import javax.annotation.Nullable;
import java.io.InputStream;
import java.util.*;

public class ChartActivity extends Activity {

    private final SparseArray<UiAction> mReloadableStyleMixins   = buildStyleReloadMixins();
    private final UiAction              mGlobalStyleReloadAction = buildGlobalStyleReloadAction();

    private final NavigableMap<String, Collection<ChartDataSource>> mInputs = new TreeMap<>();

    private ViewGroup                   mContent;
    private Spinner                     mInputSelectorSpinner;
    private EscapingViewArrayAdapter<?> mInputSelectorAdapter;
    private ChartView                   mChartView;
    private NavigatorChartView          mNavigatorChartView;
    private ChartSelectorView           mChartSelectorView;
    private ImageView                   mThemeSwitcherView;

    @SuppressWarnings("CodeBlock2Expr")
    private SparseArray<UiAction> buildStyleReloadMixins() {
        SparseArray<UiAction> result = new SparseArray<>();
        UiAction actionBackBackgroundAction = view -> {
            view.setBackgroundColor(LeonardoUtil.getColor(getApplicationContext(), R.attr.action_bar_background_color));
        };
        result.put(R.id.action_bar, actionBackBackgroundAction);
        result.put(R.id.theme_switcher, actionBackBackgroundAction);
        result.put(R.id.action_bar_title, view -> {
            ((TextView) view).setTextColor(LeonardoUtil.getColor(getApplicationContext(),
                                                                 R.attr.action_bar_text_color));
            actionBackBackgroundAction.apply(view);
        });
        return result;
    }

    private UiAction buildGlobalStyleReloadAction() {
        return view -> {
            int color = LeonardoUtil.getColor(getApplicationContext(), android.R.attr.windowBackground);
            view.setBackgroundColor(color);
        };
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        getApplication().setTheme(R.style.AppTheme_Light);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart);
        mContent = findViewById(R.id.content);
        mInputSelectorSpinner = findViewById(R.id.input_selector);
        mChartView = findViewById(R.id.chart);
        mNavigatorChartView = findViewById(R.id.navigator);
        mChartSelectorView = findViewById(R.id.chart_selector);
        mThemeSwitcherView = findViewById(R.id.theme_switcher);

        LogUtil.putMarker(mNavigatorChartView, "navigator");
        LogUtil.putMarker(mChartView, "main-chart");
        LogUtil.putMarker(mChartSelectorView, "chart-selector");

        prepareInputs();
//        prepareTestInputs();
        final String firstDataSetName;
        if (mInputs.containsKey("Followers")) {
            firstDataSetName = "Followers";
        } else {
            firstDataSetName = mInputs.firstKey();
        }

        initInputSelector();

        mNavigatorChartView.apply(LeonardoConfigFactory.asNavigatorShowCase(mChartView));
        initThemeSwitcher();
        initData(firstDataSetName);
        applyConfigs();
    }

    private void prepareInputs() {
        try (InputStream in = getResources().openRawResource(R.raw.input)) {
            JsonDataSourceParser parser = new JsonDataSourceParser();
            Map<String, Collection<ChartDataSource>> inputs = parser.parse(in);
            mInputs.putAll(inputs);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void initInputSelector() {
        mInputSelectorAdapter = new EscapingViewArrayAdapter<>(this,
                                                               R.layout.spinner_input_item,
                                                               new ArrayList<>(mInputs.keySet()));
        mInputSelectorAdapter.setDropDownViewResource(R.layout.spinner_input_drop_down_item);
        mInputSelectorSpinner.setAdapter(mInputSelectorAdapter);

        mInputSelectorSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String item = parent.getItemAtPosition(position).toString();
                initData(item);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void initThemeSwitcher() {
        mThemeSwitcherView.setOnClickListener(v -> {
            if (isLightThemeActive()) {
                getApplication().setTheme(R.style.AppTheme_Dark);
                mThemeSwitcherView.setImageResource(R.drawable.ic_sun);
            } else {
                getApplication().setTheme(R.style.AppTheme_Light);
                mThemeSwitcherView.setImageResource(R.drawable.ic_moon);
            }
            doForChildren(view -> {
                mGlobalStyleReloadAction.apply(view);
                UiAction mixin = mReloadableStyleMixins.get(view.getId());
                if (mixin != null) {
                    mixin.apply(view);
                }
            });
            applyConfigs();
        });
    }

    private boolean isLightThemeActive() {
        TypedValue typedValue = new TypedValue();
        getApplication().getTheme().resolveAttribute(R.attr.theme_name, typedValue, true);
        return "light".equals(typedValue.string.toString());
    }

    private void doForChildren(UiAction action) {
        for (View view : mInputSelectorAdapter.getDropDownViews()) {
            action.apply(view);
        }
        Stack<ViewGroup> toProcess = new Stack<>();
        toProcess.push(mContent);
        while (!toProcess.isEmpty()) {
            ViewGroup group = toProcess.pop();
            action.apply(group);
            for (int i = 0; i < group.getChildCount(); i++) {
                View child = group.getChildAt(i);
                if (child instanceof ViewGroup) {
                    toProcess.add((ViewGroup) child);
                } else {
                    action.apply(child);
                }
            }
        }
    }

    private void applyConfigs() {
        AxisConfig xAxisConfig = LeonardoConfigFactory.newAxisConfigBuilder()
                                                      .withLabelTextStrategy(new TimeAxisLabelTextStrategy())
                                                      .withContext(this)
                                                      .build();

        ChartConfig chartConfig = LeonardoConfigFactory.newChartConfigBuilder()
                                                       .withContext(getApplicationContext())
                                                       .withXAxisConfig(xAxisConfig)
                                                       .build();
        mChartView.apply(chartConfig);

        mNavigatorChartView.apply(
                LeonardoConfigFactory.newNavigatorConfigBuilder()
                                     .withViewMargin((int) getResources().getDimension(R.dimen.app_padding))
                                     .withContext(getApplicationContext())
                                     .build(),
                chartConfig);

        mChartSelectorView.apply(LeonardoConfigFactory.newChartSelectorConfigBuilder()
                                                      .withContext(getApplicationContext())
                                                      .build());
    }

    private void initData(String name) {
        Collection<ChartDataSource> dataSources = mInputs.get(name);
        if (dataSources == null) {
            throw new IllegalArgumentException(String.format(
                    "Can't find an input with name '%s'. Available names: %s", name, mInputs.keySet()
            ));
        }

        ChartModel chartModel = LeonardoUtil.spinUpMvc(3);
        for (ChartDataSource dataSource : dataSources) {
            chartModel.addDataSource(dataSource);
        }
        chartModel.setActiveRange(dataSources.iterator().next().getDataRange(), mNavigatorChartView);

        mChartView.apply(chartModel);
        mNavigatorChartView.apply(chartModel);
        mChartSelectorView.apply(chartModel);
        mChartSelectorView.setDataSources(dataSources);
    }

//    private void prepareTestInputs() {
//        long minX = 0;
//        long maxX = 10;
//        mInputs.put("Test", LeonardoConfigFactory
//                .newDataSourcesBuilder()
//                .addDataSourceBuilder(LeonardoConfigFactory.newDataSourceBuilder()
//                                                           .withLegend("First")
//                                                           .withMinX(minX)
//                                                           .withMaxX(maxX)
//                                                           .withLoader(new InfiniteChartDataLoader()))
//                .addDataSourceBuilder(LeonardoConfigFactory.newDataSourceBuilder()
//                                                           .withLegend("Second")
//                                                           .withMinX(minX)
//                                                           .withMaxX(maxX)
//                                                           .withLoader(new InfiniteChartDataLoader()))
//                .addDataSourceBuilder(LeonardoConfigFactory.newDataSourceBuilder()
//                                                           .withLegend("Third")
//                                                           .withMinX(minX)
//                                                           .withMaxX(maxX)
//                                                           .withLoader(new InfiniteChartDataLoader()))
//                .addDataSourceBuilder(LeonardoConfigFactory.newDataSourceBuilder()
//                                                           .withLegend("Forth")
//                                                           .withMinX(minX)
//                                                           .withMaxX(maxX)
//                                                           .withLoader(new InfiniteChartDataLoader()))
//                .build());
//    }

    private interface UiAction {
        void apply(View view);
    }
}
