package cn.peter.carcomparison.vhtableview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cn.peter.carcomparison.R;

public class VHTableView extends LinearLayout implements HListViewScrollView.ScrollChangedListener {

    private Context mContext;
    private LayoutInflater inflater;

    // 是否显示标题行
    private boolean showTitleRow;
    // 第一列是否可移动
    private boolean firstColumnIsMove;
    // 用于显示表格正文内容
    private ListView listView;
    // 存放标题行中的每一列的宽度，所有的行里的每一列都是基于标题行的每一列的宽度，都跟标题行的每一列的宽度相等
    private HashMap<String, Integer> mColumnWidthMap = new HashMap<>();
    // 存放所有的HScrollView
    protected List<HListViewScrollView> mHScrollViews = new ArrayList<HListViewScrollView>();

    private LinearLayout titleLayout;

    public LinearLayout getTitleLayout() {
        return titleLayout;
    }

    public void addTitleLayout(View view) {
        titleLayout.removeAllViews();
        titleLayout.addView(view, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
    }

    public ListView getListView() {
        return listView;
    }

    public VHTableView(Context context) {
        this(context, null);
    }

    public VHTableView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;

        initData();
    }

    private void initData() {
        this.inflater = LayoutInflater.from(mContext);
        // 默认显示标题行
        this.showTitleRow = true;
        // 默认第一列不可滑动
        this.firstColumnIsMove = false;
    }

    // 设置是否显示标题
    public void setShowTitle(boolean showTitle) {
        this.showTitleRow = showTitle;
    }

    // 设置第一列是否可以滑动
    public void setFirstColumnIsMove(boolean firstColumnIsMove) {
        this.firstColumnIsMove = firstColumnIsMove;
    }

    //设置adapter
    public void setAdapter(final VHBaseAdapter contentAdapter) {
        // 清除各原有数据
        cleanup();
        // 载入标题行
        initTitles(contentAdapter);
        // 载入表格正文
        initContentList(contentAdapter);

        // 假如设置了不显示标题行，在这里隐藏掉
        if (!showTitleRow) {
            getChildAt(0).setVisibility(View.GONE);
        }
    }

    public void cleanup() {
        removeAllViews();

        mColumnWidthMap.clear();
        mHScrollViews.clear();
    }

    // 初始化 标题栏
    private void initTitles(VHBaseAdapter contentAdapter) {
        View titleView = inflater.inflate(R.layout.layout_vh_table_listview, this, false);
        LinearLayout ll_first_column = (LinearLayout) titleView.findViewById(R.id.ll_first_column);

        int i = 0;
        if (firstColumnIsMove) {
            // 假如设置是可移动的，则把不可移动的部分ll_first_column隐藏掉，把所有数据都加在HListViewScrollView中
            ll_first_column.setVisibility(View.GONE);
        } else {
            View view = contentAdapter.getTitleView(0, ll_first_column);
            // 测量view的高度，都采用自适应的模式测量
            view.measure(0, 0);

            // 假如是设置了第一列不可移动的，则把第一列的数据加到ll_first_column中，其余的都加到HListViewScrollView中
            ll_first_column.removeAllViews();
            ll_first_column.addView(view, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);

            // 存起来以便设置表格正文的时候进行宽度设置
            mColumnWidthMap.put("0", view.getMeasuredWidth());

            //之后的titleView就都放在CHListViewScrollView中，因为0 title已经设置了，所以从1开始
            i = 1;
        }

        HListViewScrollView chs_data_group = (HListViewScrollView) titleView.findViewById(R.id.chs_data_group);
        //把CHListViewScrollView加入管理
        addHViews(chs_data_group);

        LinearLayout ll_data_group = (LinearLayout) titleView.findViewById(R.id.ll_data_group);
        ll_data_group.removeAllViews();

        for (; i < contentAdapter.getContentColumn(); i++) {
            View view = contentAdapter.getTitleView(i, ll_data_group);
            view.measure(0, 0);

            ll_data_group.addView(view, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            mColumnWidthMap.put(String.valueOf(i), view.getMeasuredWidth());
        }
        addView(titleView);
    }

    // 初始化 内容栏
    private void initContentList(VHBaseAdapter contentAdapter) {
        listView = new ListView(mContext);
        listView.setDividerHeight(0);
        listView.setOverScrollMode(View.OVER_SCROLL_NEVER);

        ContentAdapter adapter = new ContentAdapter(contentAdapter);

        listView.addFooterView(contentAdapter.getFooterView(listView));
        listView.setAdapter(adapter);

        FrameLayout frameLayout = new FrameLayout(mContext);
        frameLayout.addView(listView, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);

        titleLayout = new LinearLayout(mContext);
        frameLayout.addView(titleLayout, LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);

        addView(frameLayout, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
    }

    // 添加水平 view
    public void addHViews(final HListViewScrollView hScrollView) {
        if (!mHScrollViews.isEmpty()) {
            int size = mHScrollViews.size();
            HListViewScrollView scrollView = mHScrollViews.get(size - 1);
            final int scrollX = scrollView.getScrollX();
            // 这是给第一次满屏，或者快速下滑等情况时，
            // 新创建的会再创建一个convertView的时候，
            // 把这个新进入的convertView里的HListViewScrollView移到对应的位置
            if (scrollX != 0) {
                listView.post(new Runnable() {
                    @Override
                    public void run() {
                        //在主线程中去移动到对应的位置
                        hScrollView.scrollTo(scrollX, 0);
                    }
                });
            }
        }
        hScrollView.setScrollChangedListener(this);

        mHScrollViews.add(hScrollView);
    }

    private HListViewScrollView currentTouchView;

    @Override
    public void setCurrentTouchView(HListViewScrollView currentTouchView) {
        this.currentTouchView = currentTouchView;
    }

    @Override
    public HListViewScrollView getCurrentTouchView() {
        return currentTouchView;
    }

    @Override
    public void onUIScrollChanged(int l, int t, int old_l, int old_t) {
        for (HListViewScrollView scrollView : mHScrollViews) {
            //防止重复滑动
            if (currentTouchView != scrollView)
                scrollView.smoothScrollTo(l, t);
        }
    }

    public class ContentAdapter extends BaseAdapter {
        private VHBaseAdapter contentAdapter;

        public ContentAdapter(VHBaseAdapter conentAdapter) {
            this.contentAdapter = conentAdapter;
        }

        @Override
        public int getCount() {
            return contentAdapter.getContentRows();
        }

        @Override
        public Object getItem(int position) {
            return contentAdapter.getItem(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, final ViewGroup parent) {
            int maxHeight = 0;

            ViewHolder viewHolder;
            if (null == convertView) {
                convertView = inflater.inflate(R.layout.layout_vh_table_listview, parent, false);

                HListViewScrollView chs_data_group = (HListViewScrollView) convertView.findViewById(R.id.chs_data_group);
                //把CHListViewScrollView加入管理
                addHViews(chs_data_group);

                viewHolder = new ViewHolder();
                viewHolder.contentColumnViews = new View[contentAdapter.getContentColumn()];
                viewHolder.ll_first_column = (LinearLayout) convertView.findViewById(R.id.ll_first_column);
                viewHolder.ll_data_group = (LinearLayout) convertView.findViewById(R.id.ll_data_group);
                viewHolder.ll_row_title = (LinearLayout) convertView.findViewById(R.id.row_title);
                viewHolder.rowClickListener = new RowClickListener();

                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            // 更新每行的views的数据
            updateViews(contentAdapter, viewHolder, viewHolder.ll_row_title, viewHolder.ll_first_column, viewHolder.ll_data_group, position);
            // 更熟的views数据后重新测量高度，取那一行的最大高度作为整行的高度
            maxHeight = getMaxHeight(contentAdapter, viewHolder.contentColumnViews);
            // 重新更新view到表格上
            updateUI(contentAdapter, viewHolder.ll_first_column, viewHolder.ll_data_group, viewHolder.contentColumnViews, maxHeight);

            // 为了尽可能少的影响ScrollView的触摸事件，所以点击事件这里取个巧，直接设置在这两个LinearLayout上
            viewHolder.rowClickListener.setData(contentAdapter, position, convertView);
            viewHolder.ll_first_column.setOnClickListener(viewHolder.rowClickListener);
            viewHolder.ll_data_group.setOnClickListener(viewHolder.rowClickListener);
            return convertView;
        }
    }

    private ViewHolder updateViews(VHBaseAdapter contentAdapter, ViewHolder viewHolder, LinearLayout ll_row_title, LinearLayout ll_firstcolumn, LinearLayout ll_data_group, int row) {
        for (int i = 0; i < contentAdapter.getContentColumn(); i++) {
            if (!firstColumnIsMove && i == 0) {
                View titleView = contentAdapter.getTableSectionView(row, viewHolder.titleView);
                ll_row_title.removeAllViews();
                ll_row_title.addView(titleView);

                View view = contentAdapter.getTableCellView(row, 0, viewHolder.contentColumnViews[0], ll_firstcolumn);
                viewHolder.contentColumnViews[0] = view;
            } else {

                View view = contentAdapter.getTableCellView(row, i, viewHolder.contentColumnViews[i], ll_data_group);
                viewHolder.contentColumnViews[i] = view;
            }
        }
        return viewHolder;
    }

    // 获取最大高度
    private int getMaxHeight(VHBaseAdapter contentAdapter, View[] views) {
        int maxHeight = 0;
        for (int i = 0; i < contentAdapter.getContentColumn(); i++) {
            // 测量模式：宽度以标题行各列的宽度为准，高度为自适应
            int w = MeasureSpec.makeMeasureSpec(mColumnWidthMap.get(String.valueOf(i)), MeasureSpec.EXACTLY);
            int h = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
            views[i].measure(w, h);
            maxHeight = Math.max(maxHeight, views[i].getMeasuredHeight());
        }
        return maxHeight;
    }

    private void updateUI(VHBaseAdapter contentAdapter, LinearLayout ll_first_column, LinearLayout ll_data_group, View[] views, int maxHeight) {
        // 其实这里可以优化一下，不用remove掉全部又加一次，以后再优化一下。。。。
        ll_first_column.removeAllViews();
        ll_data_group.removeAllViews();
        for (int i = 0; i < contentAdapter.getContentColumn(); i++) {
            if (!firstColumnIsMove && i == 0) {
                ll_first_column.addView(views[0], mColumnWidthMap.get("0"), maxHeight);
            } else {
                ll_data_group.addView(views[i], mColumnWidthMap.get(String.valueOf(i)), maxHeight);
            }
        }
    }

    public class RowClickListener implements OnClickListener {
        private VHBaseAdapter contentAdapter;
        private int row;
        private View convertView;

        public void setData(VHBaseAdapter contentAdapter, int row, View convertView) {
            this.contentAdapter = contentAdapter;
            this.row = row;
            this.convertView = convertView;
        }

        @Override
        public void onClick(View v) {
            if (null != contentAdapter && null != convertView) {
                contentAdapter.OnClickContentRowItem(row, convertView);
            }
        }
    }

    public class ViewHolder {
        LinearLayout ll_first_column;
        LinearLayout ll_data_group;
        LinearLayout ll_row_title;

        View titleView;
        View[] contentColumnViews;

        RowClickListener rowClickListener;
    }
}