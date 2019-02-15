package com.kmgdemo;

import android.app.Activity;
import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipDescription;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.DragEvent;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.CombinedChart;
import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.CombinedData;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.data.ScatterData;
import com.github.mikephil.charting.data.ScatterDataSet;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.model.GradientColor;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.MPPointF;
import com.google.android.material.navigation.NavigationView;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.BarGraphSeries;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.kmgdemo.custom.DayAxisValueFormatter;
import com.kmgdemo.custom.MyValueFormatter;
import com.kmgdemo.custom.XYMarkerView;
import com.kmgdemo.formatter.ValueFormatter;

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

/**
 * Created by Umesh Kumar on 02/15/19.
 */
public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,
        View.OnDragListener, View.OnLongClickListener, OnChartValueSelectedListener/*, View.OnTouchListener*/ {

    private static final String GRAPH_VIEW_TAG = "DRAG GRAPH";
    private static final String GRAPH_TAG = "false";
    private RelativeLayout.LayoutParams tvParams;
    private final RectF onValueSelectedRectF = new RectF();
    private BarChart chart;
    private RelativeLayout relativeLayout;
    private View draggableView;

    // For PieChart
    protected final String[] months = new String[]{
            "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Okt", "Nov", "Dec"
    };

    protected final String[] parties = new String[]{
            "Party A", "Party B", "Party C", "Party D", "Party E", "Party F", "Party G", "Party H",
            "Party I", "Party J", "Party K", "Party L", "Party M", "Party N", "Party O", "Party P",
            "Party Q", "Party R", "Party S", "Party T", "Party U", "Party V", "Party W", "Party X",
            "Party Y", "Party Z"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findIds();
        implementEvents();
        createLayout();
        showDialog(this, "All Graphs added here are fully customizable and can be modify as required.\n\nTo remove any graph: Just drag it outside from the draggable view and drop it.\n\n\nThank you :)");
    }

    private void findIds() {
        // set LinearLayout as a root element of the screen
        relativeLayout = (RelativeLayout) findViewById(R.id.parent_layout);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    private void implementEvents() {
        //add or remove any layout view that you don't want to accept dragged view
        findViewById(R.id.layout_1).setOnDragListener(this);
        findViewById(R.id.layout_2).setOnDragListener(this);
        findViewById(R.id.layout_3).setOnDragListener(this);
        findViewById(R.id.layout_4).setOnDragListener(this);
        findViewById(R.id.layout_5).setOnDragListener(this);
        findViewById(R.id.layout_6).setOnDragListener(this);
    }

    private void createLayout() {

        LinearLayout linLayout = new LinearLayout(this);
        // specifying vertical orientation
        linLayout.setOrientation(LinearLayout.VERTICAL);
        linLayout.setGravity(Gravity.CENTER_HORIZONTAL);
        // creating LayoutParams
        tvParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        tvParams.addRule(RelativeLayout.CENTER_IN_PARENT);
        tvParams.width = 290;
        tvParams.height = 250;
//        setContentView(relativeLayout);
    }

    private void setData(int count, float range) {

        float start = 1f;

        ArrayList<BarEntry> values = new ArrayList<>();

        for (int i = (int) start; i < start + count; i++) {
            float val = (float) (Math.random() * (range + 1));

            if (Math.random() * 100 < 25) {
                values.add(new BarEntry(i, val, getResources().getDrawable(R.drawable.ic_launcher_foreground)));
            } else {
                values.add(new BarEntry(i, val));
            }
        }

        BarDataSet set1;

        if (chart.getData() != null &&
                chart.getData().getDataSetCount() > 0) {
            set1 = (BarDataSet) chart.getData().getDataSetByIndex(0);
            set1.setValues(values);
            chart.getData().notifyDataChanged();
            chart.notifyDataSetChanged();

        } else {
            set1 = new BarDataSet(values, "The year 2019");

            set1.setDrawIcons(false);

//            set1.setColors(ColorTemplate.MATERIAL_COLORS);

            /*int startColor = ContextCompat.getColor(this, android.R.color.holo_blue_dark);
            int endColor = ContextCompat.getColor(this, android.R.color.holo_blue_bright);
            set1.setGradientColor(startColor, endColor);*/

            int startColor1 = ContextCompat.getColor(this, android.R.color.holo_orange_light);
            int startColor2 = ContextCompat.getColor(this, android.R.color.holo_blue_light);
            int startColor3 = ContextCompat.getColor(this, android.R.color.holo_orange_light);
            int startColor4 = ContextCompat.getColor(this, android.R.color.holo_green_light);
            int startColor5 = ContextCompat.getColor(this, android.R.color.holo_red_light);
            int endColor1 = ContextCompat.getColor(this, android.R.color.holo_blue_dark);
            int endColor2 = ContextCompat.getColor(this, android.R.color.holo_purple);
            int endColor3 = ContextCompat.getColor(this, android.R.color.holo_green_dark);
            int endColor4 = ContextCompat.getColor(this, android.R.color.holo_red_dark);
            int endColor5 = ContextCompat.getColor(this, android.R.color.holo_orange_dark);

            List<GradientColor> gradientColors = new ArrayList<>();
            gradientColors.add(new GradientColor(startColor1, startColor1));
            gradientColors.add(new GradientColor(startColor2, endColor2));
            gradientColors.add(new GradientColor(startColor3, endColor3));
            gradientColors.add(new GradientColor(startColor4, endColor4));
            gradientColors.add(new GradientColor(startColor5, endColor5));

            set1.setGradientColors(gradientColors);

            ArrayList<IBarDataSet> dataSets = new ArrayList<>();
            dataSets.add(set1);

            BarData data = new BarData(dataSets);
            data.setValueTextSize(10f);
//            data.setValueTypeface(tfLight);
            data.setBarWidth(0.9f);

            chart.setData(data);
        }
    }

    // This is the method that the system calls when it dispatches a drag event to the
    // listener.
    @Override
    public boolean onDrag(View view, DragEvent event) {
        // Defines a variable to store the action type for the incoming event
        int action = event.getAction();
        // Handles each of the expected events
        switch (action) {
            case DragEvent.ACTION_DRAG_STARTED:
                // Determines if this View can accept the dragged data
                if (event.getClipDescription().hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN)) {
                    // if you want to apply color when drag started to your view you can uncomment below lines
                    // to give any color tint to the View to indicate that it can accept
                    // data.

                    //  view.getBackground().setColorFilter(Color.BLUE, PorterDuff.Mode.SRC_IN);//set background color to your view

                    // Invalidate the view to force a redraw in the new tint
                    //  view.invalidate();

                    // returns true to indicate that the View can accept the dragged data.
                    return true;

                }

                // Returns false. During the current drag and drop operation, this View will
                // not receive events again until ACTION_DRAG_ENDED is sent.
                return false;

            case DragEvent.ACTION_DRAG_ENTERED:
                // Applies a YELLOW or any color tint to the View, when the dragged view entered into drag acceptable view
                // Return true; the return value is ignored.

                view.getBackground().setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN);

                // Invalidate the view to force a redraw in the new tint
                view.invalidate();

                return true;
            case DragEvent.ACTION_DRAG_LOCATION:
                // Ignore the event
                return true;
            case DragEvent.ACTION_DRAG_EXITED:
                // Re-sets the color tint to blue, if you had set the BLUE color or any color in ACTION_DRAG_STARTED. Returns true; the return value is ignored.

                //  view.getBackground().setColorFilter(Color.BLUE, PorterDuff.Mode.SRC_IN);

                //If u had not provided any color in ACTION_DRAG_STARTED then clear color filter.
                view.getBackground().clearColorFilter();
                // Invalidate the view to force a redraw in the new tint
                view.invalidate();

                return true;
            case DragEvent.ACTION_DROP:

                switch (view.getId()) {
                    case R.id.layout_1:
                    case R.id.layout_2:
                    case R.id.layout_3:
                    case R.id.layout_4:
                    case R.id.layout_5:
                    case R.id.layout_6:
                        boolean tY = setDropInDragViewAndGetReport(view, event);

                        // Returns true. DragEvent.getResult() will return true.
                        return tY;
                    default:
                        return false;
                }

            case DragEvent.ACTION_DRAG_ENDED:

                // Turns off any color tinting
                view.getBackground().clearColorFilter();

                // Invalidates the view to force a redraw
                view.invalidate();

                // Does a getResult(), and displays what happened.
                if (event.getResult()) {
//                    Toast.makeText(this, "The drop was handled.", Toast.LENGTH_SHORT).show();

                    // returns true; the value is ignored.
                    return true;
                } else {
                    View v = (View) event.getLocalState();
                    ViewGroup owner = (ViewGroup) v.getParent();
                    if (owner != null) {
                        owner.clearDisappearingChildren();
                        owner.removeView(v);// remove the dragged view
                    }
                    if (draggableView != null) {
                        draggableView.setVisibility(View.VISIBLE);
                        draggableView = null;
                    }
//                    Toast.makeText(this, "The drop didn't work.", Toast.LENGTH_SHORT).show();

                    // returns true; the value is cleared.
                    return false;
                }

                // An unknown action type was received.
            default:
                Log.e("DragDrop Example", "Unknown action type received by OnDragListener.");
                break;
        }
        return false;
    }

    private boolean setDropInDragViewAndGetReport(View view, DragEvent event) {

        // Overlapping handled: i parent must has 1 child
        if (((LinearLayout) view).getChildCount() == 0) {
            // Gets the item containing the dragged data
            ClipData.Item item = event.getClipData().getItemAt(0);

            // Gets the text data from the item.
            String dragData = item.getText().toString();

            // Turns off any color tints
            view.getBackground().clearColorFilter();

            // Invalidates the view to force a redraw
            view.invalidate();
            View v = (View) event.getLocalState();
            ViewGroup owner = (ViewGroup) v.getParent();
            owner.removeView(v);//remove the dragged view
            view.setTag(R.string.condition, "true");
            LinearLayout container = (LinearLayout) view;//caste the view into LinearLayout as our drag acceptable layout is LinearLayout
            container.addView(v);//Add the dragged view
            v.setVisibility(View.VISIBLE);//finally set Visibility to VISIBLE
            return true;
        } else {
            View v = (View) event.getLocalState();
            ViewGroup owner = (ViewGroup) v.getParent();
            if (owner != null) {
                owner.clearDisappearingChildren();
                owner.removeView(v);//remove the dragged view
            }
            return false;
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_avg_cost_per_claim) {
            setAvgCostPerClaimGraph();
        } else if (id == R.id.nav_customer_satisfaction) {
            setCustomerSatisfactionGraph();
        } else if (id == R.id.nav_claims_ratio) {
            setClaimRatioGraph();
        } else if (id == R.id.nav_top_brokers) {
            setTopBrokersGraph();
//            setGraphViewDynamically();
        } else if (id == R.id.nav_average_time_to_settle) {
            setAverageTimeToSettleGraph();
        } else if (id == R.id.nav_policy_sales_growth) {
            setPolicySalesGrowthGraph();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void setAvgCostPerClaimGraph() {
        GraphView graph = new GraphView(this);
        LineGraphSeries<DataPoint> series = new LineGraphSeries<DataPoint>(new DataPoint[]{
                new DataPoint(0, 5),
                new DataPoint(1, 7),
                new DataPoint(2, 6),
                new DataPoint(3, 8),
                new DataPoint(4, 9)
        });
        series.setColor(Color.BLUE);
        series.setBackgroundColor(Color.BLUE);
        series.setDrawBackground(true);
        series.setAnimated(true);
        graph.addSeries(series);
        series = new LineGraphSeries<DataPoint>(new DataPoint[]{
                new DataPoint(0, 4),
                new DataPoint(1, 6),
                new DataPoint(2, 4),
                new DataPoint(3, 5),
                new DataPoint(4, 7)
        });
        series.setBackgroundColor(Color.GREEN);
        series.setDrawBackground(true);
        series.setColor(Color.GREEN);
//        series.setDataPointsRadius(8);
//        series.setDrawDataPoints(true);
        series.setAnimated(true);
        graph.addSeries(series);
        series = new LineGraphSeries<DataPoint>(new DataPoint[]{
                new DataPoint(0, 1),
                new DataPoint(1, 5),
                new DataPoint(2, 3),
                new DataPoint(3, 2),
                new DataPoint(4, 6)
        });
        series.setColor(Color.CYAN);
        series.setBackgroundColor(Color.CYAN);
        series.setAnimated(true);
        series.setDrawBackground(true);
        graph.addSeries(series);
        graph.setTitle("Avg Cost Per Claim");
        graph.setBackgroundColor(getResources().getColor(R.color.white));
        graph.setTag(R.string.condition, GRAPH_TAG);
        graph.setTag(GRAPH_VIEW_TAG);
        graph.setLayoutParams(tvParams);
        graph.setOnLongClickListener(this);
        relativeLayout.addView(graph);
    }

    private void setCustomerSatisfactionGraph() {
        PieChart pieChart = new PieChart(this);
        pieChart.setUsePercentValues(true);
        pieChart.getDescription().setEnabled(false);
        pieChart.setExtraOffsets(5, 10, 5, 5);

        pieChart.setDragDecelerationFrictionCoef(0.95f);

        SpannableString s = new SpannableString("Customer\nSatisfaction");
//        s.setSpan(new RelativeSizeSpan(1.5f), 0, 14, 0);
        pieChart.setCenterText(s);

        pieChart.setExtraOffsets(20.f, 0.f, 20.f, 0.f);

        pieChart.setDrawHoleEnabled(true);
        pieChart.setHoleColor(Color.WHITE);

        pieChart.setTransparentCircleColor(Color.WHITE);
        pieChart.setTransparentCircleAlpha(110);

        pieChart.setHoleRadius(58f);
        pieChart.setTransparentCircleRadius(61f);

        pieChart.setDrawCenterText(true);
        pieChart.setTouchEnabled(false);

        pieChart.setRotationAngle(0);
        // enable rotation of the pieChart by touch
        pieChart.setRotationEnabled(true);
        pieChart.setHighlightPerTapEnabled(true);

        // pieChart.setUnit(" €");
        // pieChart.setDrawUnitsInChart(true);

        // add a selection listener
        pieChart.setOnChartValueSelectedListener(this);

        pieChart.animateY(1400, Easing.EaseInOutQuad);
        // pieChart.spin(2000, 0, 360);

        Legend l = pieChart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        l.setOrientation(Legend.LegendOrientation.VERTICAL);
        l.setDrawInside(false);
        l.setEnabled(false);

        ArrayList<PieEntry> entries = new ArrayList<>();

        // NOTE: The order of the entries when being added to the entries array determines their position around the center of
        // the chart.
        for (int i = 0; i < 4; i++) {
            entries.add(new PieEntry((float) (Math.random() * 100) + 100 / 5, parties[i % parties.length]));
        }

        PieDataSet dataSet = new PieDataSet(entries, "Election Results");
        dataSet.setSliceSpace(3f);
        dataSet.setSelectionShift(5f);

        // add a lot of colors

        ArrayList<Integer> colors = new ArrayList<>();

        for (int c : ColorTemplate.VORDIPLOM_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.JOYFUL_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.COLORFUL_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.LIBERTY_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.PASTEL_COLORS)
            colors.add(c);

        colors.add(ColorTemplate.getHoloBlue());

        dataSet.setColors(colors);
        //dataSet.setSelectionShift(0f);

        dataSet.setValueLinePart1OffsetPercentage(80.f);
        dataSet.setValueLinePart1Length(0.2f);
        dataSet.setValueLinePart2Length(0.4f);

        //dataSet.setXValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);
        dataSet.setYValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);

        PieData data = new PieData(dataSet);
        data.setValueFormatter(new PercentFormatter());
        data.setValueTextSize(11f);
        data.setValueTextColor(Color.BLACK);
        pieChart.setData(data);

        // undo all highlights
        pieChart.highlightValues(null);

        pieChart.invalidate();

        pieChart.setBackgroundColor(getResources().getColor(R.color.white));
        pieChart.setTag(R.string.condition, GRAPH_TAG);
        pieChart.setTag(GRAPH_VIEW_TAG);
        pieChart.setLayoutParams(tvParams);
        pieChart.setOnLongClickListener(this);
        relativeLayout.addView(pieChart);
    }

    private void setClaimRatioGraph() {
        CombinedChart combinedChart = new CombinedChart(this);
        combinedChart.getDescription().setEnabled(false);
        combinedChart.setBackgroundColor(Color.WHITE);
        combinedChart.setDrawGridBackground(false);
        combinedChart.setDrawBarShadow(false);
        combinedChart.setHighlightFullBarEnabled(false);

        // draw bars behind lines
        combinedChart.setDrawOrder(new CombinedChart.DrawOrder[]{
                CombinedChart.DrawOrder.BAR, CombinedChart.DrawOrder.LINE
        });

        Legend l = combinedChart.getLegend();
        l.setWordWrapEnabled(true);
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        l.setDrawInside(false);

        YAxis rightAxis = combinedChart.getAxisRight();
        rightAxis.setDrawGridLines(false);
        rightAxis.setAxisMinimum(0f); // this replaces setStartAtZero(true)

        YAxis leftAxis = combinedChart.getAxisLeft();
        leftAxis.setDrawGridLines(false);
        leftAxis.setAxisMinimum(0f); // this replaces setStartAtZero(true)

        XAxis xAxis = combinedChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTH_SIDED);
        xAxis.setAxisMinimum(0f);
        xAxis.setGranularity(1f);
        xAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return months[(int) value % months.length];
            }
        });

        // Line Chart View
        CombinedData data = new CombinedData();
        LineData d = new LineData();
        ArrayList<Entry> entries = new ArrayList<>();
        for (int index = 0; index < 4; index++)
            entries.add(new Entry(index + 0.5f, getRandom(15, 5)));
        LineDataSet set = new LineDataSet(entries, "Claim Ratio");
        set.setColor(Color.rgb(0, 0, 0));
        set.setLineWidth(2.5f);
        set.setCircleColor(Color.rgb(0, 0, 0));
        set.setCircleRadius(5f);
        set.setFillColor(Color.rgb(0, 0, 0));
        set.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        set.setDrawValues(true);
        set.setValueTextSize(10f);
        set.setValueTextColor(Color.rgb(0, 0, 70));
        set.setAxisDependency(YAxis.AxisDependency.LEFT);
        d.addDataSet(set);
        data.setData(d);

        // Bar Chart View
        ArrayList<BarEntry> entries1 = new ArrayList<>();
        ArrayList<BarEntry> entries2 = new ArrayList<>();
        for (int index = 0; index < 4; index++) {
            entries1.add(new BarEntry(0, getRandom(25, 25)));

            // stacked
            entries2.add(new BarEntry(0, new float[]{getRandom(13, 12)/*, getRandom(13, 12)*/}));
        }
        BarDataSet set1 = new BarDataSet(entries1, "Bar 1");
        set1.setColor(Color.rgb(60, 220, 78));
        set1.setValueTextColor(Color.rgb(60, 220, 78));
        set1.setValueTextSize(10f);
        set1.setAxisDependency(YAxis.AxisDependency.LEFT);
        BarDataSet set2 = new BarDataSet(entries2, "");
        set2.setStackLabels(new String[]{"Stack"/*, "Stack 2"*/});
        set2.setColors(Color.rgb(61, 165, 255)/*, Color.rgb(23, 197, 255)*/);
        set2.setValueTextColor(Color.rgb(61, 165, 255));
        set2.setValueTextSize(10f);
        set2.setAxisDependency(YAxis.AxisDependency.LEFT);
        float groupSpace = 0.06f;
        float barSpace = 0.02f; // x2 dataset
        float barWidth = 0.45f; // x2 dataset
        // (0.45 + 0.02) * 2 + 0.06 = 1.00 -> interval per "group"
        BarData dd = new BarData(set1, set2);
        dd.setBarWidth(barWidth);
        // make this BarData object grouped
        dd.groupBars(0, groupSpace, barSpace); // start at x = 0
        data.setData(dd);

        xAxis.setAxisMaximum(data.getXMax() + 0.25f);

        combinedChart.setData(data);
        combinedChart.invalidate();

        combinedChart.setBackgroundColor(getResources().getColor(R.color.white));
        combinedChart.setTag(R.string.condition, GRAPH_TAG);
        combinedChart.setTag(GRAPH_VIEW_TAG);
        combinedChart.setLayoutParams(tvParams);
        combinedChart.setOnLongClickListener(this);
        relativeLayout.addView(combinedChart);
    }

    private void setTopBrokersGraph() {
        chart = new BarChart(MainActivity.this);
        chart.getDescription().setEnabled(false);

        // if more than 60 entries are displayed in the chart, no values will be
        // drawn
        chart.setMaxVisibleValueCount(60);

        // scaling can now only be done on x- and y-axis separately
        chart.setPinchZoom(false);

        chart.setDrawBarShadow(false);
        chart.setDrawGridBackground(false);

        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);

        chart.getAxisLeft().setDrawGridLines(false);

        // add a nice and smooth animation
        chart.animateY(1500);

        chart.getLegend().setEnabled(false);
        chart.setBackgroundColor(getResources().getColor(R.color.white));
        chart.setTag(R.string.condition, GRAPH_TAG);
        chart.setTag(GRAPH_VIEW_TAG);
        chart.setLayoutParams(tvParams);
        chart.setOnLongClickListener(MainActivity.this);
        setData(10, 20);
        relativeLayout.addView(chart);
    }

    private void setAverageTimeToSettleGraph() {
        HorizontalBarChart horizontalBarChart = new HorizontalBarChart(this);
        horizontalBarChart.setDrawBarShadow(false);

        horizontalBarChart.setDrawValueAboveBar(true);

        horizontalBarChart.getDescription().setEnabled(false);

        // if more than 60 entries are displayed in the horizontalBarChart, no values will be
        // drawn
        horizontalBarChart.setMaxVisibleValueCount(60);

        // scaling can now only be done on x- and y-axis separately
        horizontalBarChart.setPinchZoom(false);

        // draw shadows for each bar that show the maximum value
        // horizontalBarChart.setDrawBarShadow(true);

        horizontalBarChart.setDrawGridBackground(false);

        XAxis xl = horizontalBarChart.getXAxis();
        xl.setPosition(XAxis.XAxisPosition.BOTTOM);
        xl.setDrawAxisLine(true);
        xl.setDrawGridLines(false);
        xl.setGranularity(10f);

        YAxis yl = horizontalBarChart.getAxisLeft();
        yl.setDrawAxisLine(true);
        yl.setDrawGridLines(true);
        yl.setAxisMinimum(0f); // this replaces setStartAtZero(true)
//        yl.setInverted(true);

        YAxis yr = horizontalBarChart.getAxisRight();
        yr.setDrawAxisLine(true);
        yr.setDrawGridLines(false);
        yr.setAxisMinimum(0f); // this replaces setStartAtZero(true)
//        yr.setInverted(true);

        horizontalBarChart.setFitBars(true);
        horizontalBarChart.animateY(2500);

        Legend l = horizontalBarChart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        l.setDrawInside(false);
        l.setFormSize(8f);
        l.setXEntrySpace(4f);

        float barWidth = 9f;
        float spaceForBar = 10f;
        ArrayList<BarEntry> values = new ArrayList<>();

        for (int i = 0; i < 4; i++) {
            float val = (float) (Math.random() * 100);
            values.add(new BarEntry(i * spaceForBar, val,
                    getResources().getDrawable(R.drawable.ic_launcher_foreground)));
        }

        BarDataSet set1;

        if (horizontalBarChart.getData() != null &&
                horizontalBarChart.getData().getDataSetCount() > 0) {
            set1 = (BarDataSet) horizontalBarChart.getData().getDataSetByIndex(0);
            set1.setValues(values);
            horizontalBarChart.getData().notifyDataChanged();
            horizontalBarChart.notifyDataSetChanged();
        } else {
            set1 = new BarDataSet(values, "DataSet 1");
            set1.setDrawIcons(false);
            ArrayList<IBarDataSet> dataSets = new ArrayList<>();
            dataSets.add(set1);
            BarData data = new BarData(dataSets);
            data.setValueTextSize(10f);
            data.setBarWidth(barWidth);
            horizontalBarChart.setData(data);
        }

        horizontalBarChart.setBackgroundColor(getResources().getColor(R.color.white));
        horizontalBarChart.setTag(R.string.condition, GRAPH_TAG);
        horizontalBarChart.setTag(GRAPH_VIEW_TAG);
        horizontalBarChart.setLayoutParams(tvParams);
        horizontalBarChart.setOnLongClickListener(MainActivity.this);
        relativeLayout.addView(horizontalBarChart);
    }

    private void setPolicySalesGrowthGraph() {
        PieChart pieChart = new PieChart(this);
        pieChart.setBackgroundColor(Color.WHITE);
//            pieChart.setLayoutParams(tvParams);
        pieChart.setUsePercentValues(true);
        pieChart.getDescription().setEnabled(false);

        pieChart.setCenterText(generateCenterSpannableText());

        pieChart.setDrawHoleEnabled(true);
        pieChart.setHoleColor(Color.WHITE);

        pieChart.setTransparentCircleColor(Color.WHITE);
        pieChart.setTransparentCircleAlpha(110);

        pieChart.setHoleRadius(58f);
        pieChart.setTransparentCircleRadius(41f);

        pieChart.setDrawCenterText(true);

        pieChart.setRotationEnabled(false);
        pieChart.setHighlightPerTapEnabled(true);
        pieChart.setTouchEnabled(false);
//            pieChart.setClickable(true);
//            pieChart.setFocusable(true);

        pieChart.setMaxAngle(260f); // HALF CHART
        pieChart.setRotationAngle(140f);
        pieChart.setCenterTextOffset(0, 20);

        ArrayList<PieEntry> values = new ArrayList<>();

        for (int i = 0; i < 4; i++) {
            values.add(new PieEntry((float) ((Math.random() * 100) + 100 / 5), parties[i % parties.length]));
        }

        PieDataSet dataSet = new PieDataSet(values, "Result");
        dataSet.setSliceSpace(3f);
        dataSet.setSelectionShift(5f);

        dataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        //dataSet.setSelectionShift(0f);

        PieData data = new PieData(dataSet);
        data.setValueFormatter(new PercentFormatter());
        data.setValueTextSize(11f);
        data.setValueTextColor(Color.WHITE);
        pieChart.setData(data);

        pieChart.animateY(1400, Easing.EaseInOutQuad);

        Legend l = pieChart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        l.setDrawInside(false);
        l.setXEntrySpace(7f);
        l.setYEntrySpace(0f);
        l.setYOffset(0f);

        // entry label styling
        pieChart.setEntryLabelColor(Color.WHITE);
        pieChart.setEntryLabelTextSize(12f);

//        pieChart.setBackgroundResource(R.drawable.graph_view_background);
        pieChart.setBackgroundResource(R.color.transparent);
        pieChart.setTag(R.string.condition, GRAPH_TAG);
        pieChart.setTag(GRAPH_VIEW_TAG);
        pieChart.setLayoutParams(tvParams);
        pieChart.setOnLongClickListener(this);
        pieChart.setPadding(5, 5, 5, 5);
        relativeLayout.addView(pieChart);
    }

    private SpannableString generateCenterSpannableText() {
        SpannableString s = new SpannableString("Sales Growth\nYTD");
        s.setSpan(new RelativeSizeSpan(1.1f), 0, 14, 0);
        s.setSpan(new StyleSpan(Typeface.NORMAL), 10, s.length() - 2, 0);
        return s;
    }

    @Override
    public void onValueSelected(Entry e, Highlight h) {

        if (e == null)
            return;

        try {
            RectF bounds = onValueSelectedRectF;
            chart.getBarBounds((BarEntry) e, bounds);
            MPPointF position = chart.getPosition(e, YAxis.AxisDependency.LEFT);

            Log.i("bounds", bounds.toString());
            Log.i("position", position.toString());

            Log.i("x-index",
                    "low: " + chart.getLowestVisibleX() + ", high: "
                            + chart.getHighestVisibleX());

            MPPointF.recycleInstance(position);
        } catch (Exception ee) {
            ee.printStackTrace();
        }
    }

    private void setGraphViewDynamically() {
        chart = new BarChart(MainActivity.this);

        chart.setOnChartValueSelectedListener(MainActivity.this);

        chart.setDrawBarShadow(false);
        chart.setDrawValueAboveBar(true);

        chart.getDescription().setEnabled(false);

        // if more than 60 entries are displayed in the chart, no values will be
        // drawn
        chart.setMaxVisibleValueCount(60);

        // scaling can now only be done on x- and y-axis separately
        chart.setPinchZoom(false);

        chart.setDrawGridBackground(false);
        // chart.setDrawYLabels(false);

        ValueFormatter xAxisFormatter = new DayAxisValueFormatter(chart);

        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
//                xAxis.setTypeface(tfLight);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f); // only intervals of 1 day
        xAxis.setLabelCount(7);
        xAxis.setValueFormatter(xAxisFormatter);

        ValueFormatter custom = new MyValueFormatter("$");

        YAxis leftAxis = chart.getAxisLeft();
//                leftAxis.setTypeface(tfLight);
        leftAxis.setLabelCount(8, false);
        leftAxis.setValueFormatter(custom);
        leftAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
        leftAxis.setSpaceTop(15f);
        leftAxis.setAxisMinimum(0f); // this replaces setStartAtZero(true)

        YAxis rightAxis = chart.getAxisRight();
        rightAxis.setDrawGridLines(false);
//                rightAxis.setTypeface(tfLight);
        rightAxis.setLabelCount(8, false);
        rightAxis.setValueFormatter(custom);
        rightAxis.setSpaceTop(15f);
        rightAxis.setAxisMinimum(0f); // this replaces setStartAtZero(true)

        Legend l = chart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        l.setDrawInside(false);
        l.setForm(Legend.LegendForm.SQUARE);
        l.setFormSize(9f);
        l.setTextSize(11f);
        l.setXEntrySpace(4f);

        XYMarkerView mv = new XYMarkerView(MainActivity.this, xAxisFormatter);
        mv.setChartView(chart); // For bounds control
        chart.setMarker(mv); // Set the marker to the chart


        chart.setBackgroundColor(getResources().getColor(R.color.white));
        chart.setTag(R.string.condition, GRAPH_TAG);
        chart.setTag(GRAPH_VIEW_TAG);
        chart.setLayoutParams(tvParams);
        chart.setOnLongClickListener(MainActivity.this);
        setData(5, 5);
        relativeLayout.addView(chart);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onNothingSelected() {
    }

    @Override
    public boolean onLongClick(View view) {
        // Create a new ClipData.

        // This is done in two steps to provide clarity. The convenience method
        // ClipData.newPlainText() can create a plain text ClipData in one step.

        // Create a new ClipData.Item from the ImageView object's tag
        ClipData.Item item = new ClipData.Item((CharSequence) view.getTag());

        // Create a new ClipData using the tag as a label, the plain text MIME type, and
        // the already-created item. This will create a new ClipDescription object within the
        // ClipData, and set its MIME type entry to "text/plain"
        String[] mimeTypes = {ClipDescription.MIMETYPE_TEXT_PLAIN};

        ClipData data = new ClipData(view.getTag().toString(), mimeTypes, item);

        // Instantiates the drag shadow builder.
        View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(view);

        // Starts the drag
        view.startDrag(data//data to be dragged
                , shadowBuilder //drag shadow
                , view//local data about the drag and drop operation
                , 0//no needed flags
        );

        //Set view visibility to INVISIBLE as we are going to drag the view
        view.setVisibility(View.INVISIBLE);
        draggableView = view;
        return true;
    }

    protected float getRandom(float range, float start) {
        return (float) (Math.random() * range) + start;
    }

    public void showDialog(Activity activity, String msg) {
        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.custom_dailogue);

        TextView text = (TextView) dialog.findViewById(R.id.text_dialog);
        text.setText(msg);

        Button dialogButton = (Button) dialog.findViewById(R.id.btn_dialog);
        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.setCancelable(false);
        dialog.show();

    }
}
