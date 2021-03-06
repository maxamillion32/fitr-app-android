package fitr.mobile;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import fitr.mobile.models.Distance;
import fitr.mobile.presenters.DistancePresenter;
import fitr.mobile.views.DistanceView;

public class DistanceReportsFragment extends Fragment implements
        DistanceView {

    private static final String TAG = "DistanceReports";

    private static final String DATE_FORMAT_PATTERN_DEFAULT = "yyyy-MM-dd'T'HH:mm:ss";

    @Inject
    DistancePresenter distancePresenter;

    private BarChart barChart;
    private TableLayout table;
    private SwipeRefreshLayout swipeLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        ((Injector) getActivity()).inject(this);
        distancePresenter.attachView(this);

        // Views
        final View view = inflater.inflate(R.layout.fragment_report_distance, container, false);
        barChart = (BarChart) view.findViewById(R.id.bc_distance_chart);
        table = (TableLayout) view.findViewById(R.id.tl_distance_table);
        swipeLayout = (SwipeRefreshLayout) view.findViewById(R.id.srl_swipe_container);
        swipeLayout.setOnRefreshListener(swipeRefreshListener());

        // Configure chart
        barChart.animateX(3000);
        barChart.animateY(3000);

        // Refresh chart
        distancePresenter.refreshData();

        return view;
    }

    @Override
    public void onDestroyView() {
        distancePresenter.detachView();
        super.onDestroyView();
    }

    private SwipeRefreshLayout.OnRefreshListener swipeRefreshListener() {
        return new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                distancePresenter.refreshData();
            }
        };
    }

    public void setRefreshing(boolean isRefreshing) {
        swipeLayout.setRefreshing(isRefreshing);
    }

    @Override
    public void setDistanceChartData(BarData barData) {
        if (barChart == null) return;
        barChart.invalidate();
        barChart.setData(barData);
        barChart.notifyDataSetChanged();
    }

    @Override
    public void setDistanceTableData(List<Distance> data) {
        table.removeAllViews();
        for (Distance dataItem : data) {
            TableRow tr = new TableRow(getContext());
            tr.setPadding(0, 10, 0, 0);
            TextView c1 = new TextView(getContext());
            c1.setPadding(0, 0, 20, 0);
            c1.setText(formatTime(dataItem.getStartDate().getTime()));
            TextView c2 = new TextView(getContext());
            c2.setPadding(0, 0, 20, 0);
            c2.setText(formatTime(dataItem.getEndDate().getTime()));
            TextView c3 = new TextView(getContext());
            c3.setPadding(0, 0, 20, 0);
            c3.setText(String.valueOf(dataItem.getDistanceInMeters()));
            tr.addView(c1);
            tr.addView(c2);
            tr.addView(c3);
            table.addView(tr);
        }
    }

    private String formatTime(long timeMillis) {
        SimpleDateFormat df = new SimpleDateFormat(DATE_FORMAT_PATTERN_DEFAULT);
        return df.format(new Date(timeMillis));
    }

    interface Injector {
        void inject(DistanceReportsFragment frag);
    }

}
