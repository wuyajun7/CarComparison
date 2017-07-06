package cn.peter.carcomparison.vhtableview;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import cn.peter.carcomparison.R;
import cn.peter.carcomparison.bean.ComparisonCarItem;

/**
 * Created by peter on 2017/7/6.
 * explain:
 */
public class VHTableAdapter implements VHBaseAdapter {
    public final String PARAM_TIP_TXT = "●标配 ○选配 -无";

    // 坐标 0
    private final int INDEX_O = 0;
    // Item中字体大小
    private final int ITEM_FONT_SIZE = 12;

    // 第一列宽度
    private int COLUMN_WIDTH_ZERO;
    // 其他列宽度
    private int COLUMN_WIDTH_OTHER;
    // 第一行高度
    private int ROW_HEIGHT_ZERO;
    // 其他行高度
    private int ROW_HEIGHT_OTHER;

    private Context mContext;

    private ArrayList<ComparisonCarItem> titleData;
    private ArrayList<ArrayList<ComparisonCarItem>> dataList;

    public VHTableAdapter(Context context,
                          ArrayList<ComparisonCarItem> titleData,
                          ArrayList<ArrayList<ComparisonCarItem>> dataList) {
        this.mContext = context;
        this.titleData = titleData;
        this.dataList = dataList;

        // 第一列宽度
        COLUMN_WIDTH_ZERO = dip2px(context, 90);
        // 其他列宽度
        COLUMN_WIDTH_OTHER = dip2px(context, 100);
        // 第一行高度
        ROW_HEIGHT_ZERO = dip2px(context, 55);
        // 其他行高度
        ROW_HEIGHT_OTHER = dip2px(context, 36);
    }

    // 表格内容的行数，不包括标题行
    @Override
    public int getContentRows() {
        return dataList.size();
    }

    // 列数
    @Override
    public int getContentColumn() {
        return titleData.size();
    }

    // 标题的view，这里从0开始，这里要注意，一定要有view返回去，不能为null，每一行
    // 各列的宽度就等于标题行的列的宽度，且边框的话，自己在这里和下文的表格单元格view里面设置
    @Override
    public View getTitleView(final int columnPosition, ViewGroup parent) {
        FrameLayout header_item = (FrameLayout) LayoutInflater.from(mContext).inflate(R.layout.layout_param_item, null);
        header_item.setMinimumWidth(ROW_HEIGHT_ZERO);
        header_item.setBackgroundResource(R.drawable.bg_shape_gray);

        TextView param_item_tv = (TextView) header_item.findViewById(R.id.param_item_tv);
        param_item_tv.setTextSize(ITEM_FONT_SIZE);
        param_item_tv.setHeight(ROW_HEIGHT_ZERO);

        ImageView param_item_del = (ImageView) header_item.findViewById(R.id.param_item_del);

        if (INDEX_O == columnPosition) {
            param_item_del.setVisibility(View.GONE);
            param_item_tv.setWidth(COLUMN_WIDTH_ZERO);
        } else {
            param_item_del.setVisibility(View.VISIBLE);
            param_item_tv.setWidth(COLUMN_WIDTH_OTHER);

            param_item_del.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(mContext, "remove index " + columnPosition, Toast.LENGTH_SHORT).show();
                }
            });
        }

        param_item_tv.setText(titleData.get(columnPosition).getName());
        param_item_tv.setGravity(Gravity.CENTER);

        return header_item;
    }

    // 表格正文的view，行和列都从0开始，宽度的话在载入的时候，默认会是以标题行各列的宽度，高度的话自适应
    @Override
    public View getTableCellView(int contentRow, int contentColumn, View view, ViewGroup parent) {
        TableItemView tableItemView = null;

        if (null == view) {
            tableItemView = new TableItemView();
            view = LayoutInflater.from(mContext).inflate(R.layout.layout_param_item, null);
            tableItemView.param_item_content = (FrameLayout) view.findViewById(R.id.param_item_content);

            //tableItemView.img_add = ((ImageView) view.findViewById(R.id.img_add));
            //tableItemView.img_add.setVisibility(View.GONE);

            tableItemView.param_item_del = ((ImageView) view.findViewById(R.id.param_item_del));
            tableItemView.param_item_del.setVisibility(View.GONE);

            tableItemView.param_item_tv = (TextView) view.findViewById(R.id.param_item_tv);
            tableItemView.param_item_tv.setTextSize(ITEM_FONT_SIZE);
            tableItemView.param_item_tv.setGravity(Gravity.CENTER);
            tableItemView.param_item_tv.setHeight(ROW_HEIGHT_OTHER);

            view.setTag(tableItemView);
        } else {
            tableItemView = (TableItemView) view.getTag();
        }

        ArrayList<ComparisonCarItem> comparisonCarItems = dataList.get(contentRow);
        ComparisonCarItem comparisonCarItem = comparisonCarItems.get(contentColumn);
        int size = comparisonCarItems.size();
        boolean same = comparisonCarItems.get(0).isSame();

        if (!tableItemView.param_item_tv.getText().equals(comparisonCarItem.getName())) {
            tableItemView.param_item_tv.setText(comparisonCarItem.getName());
        }

        boolean isFirstColumn = contentColumn == 0;
        boolean isFirstOrLastColumn = isFirstColumn || contentColumn == size - 1;

        int itemTvColor = mContext.getResources().getColor(
                isFirstColumn ? android.R.color.black : android.R.color.black);
        if (tableItemView.param_item_tv.getCurrentTextColor() != itemTvColor) {
            tableItemView.param_item_tv.setTextColor(itemTvColor);
        }

        int tabColor = mContext.getResources().getColor(
                isFirstOrLastColumn || contentRow != 0 ? android.R.color.black : android.R.color.holo_red_light);
        if (tableItemView.param_item_tv.getCurrentTextColor() != tabColor) {
            tableItemView.param_item_tv.setTextColor(tabColor);
        }

        int itemBgId = isFirstOrLastColumn || same ? R.drawable.bg_shape_gray : R.drawable.bg_shape_green;
        if (view.getTag() == null || view.getTag() != Integer.valueOf(itemBgId)) {
            view.setBackgroundResource(itemBgId);
            view.setTag(R.layout.layout_param_item, itemBgId);
        }

//            int visibility = isComparsionSame && same ? View.GONE : View.VISIBLE;
//            if (tableItemView.flContent.getVisibility() != visibility) {
//                tableItemView.flContent.setVisibility(visibility);
//            }

        return view;
    }

    // Section 布局
    @Override
    public View getTableSectionView(int contentRow, View view) {
        TableSectionView tableRowTitleView = null;
        if (null == view) {
            view = LayoutInflater.from(mContext).inflate(R.layout.layout_param_section, null);

            tableRowTitleView = new TableSectionView();
            tableRowTitleView.section_title = (TextView) view.findViewById(R.id.section_title);
            tableRowTitleView.section_sub_title = (TextView) view.findViewById(R.id.section_sub_title);
        } else {
            tableRowTitleView = (TableSectionView) view.getTag();
        }

        ComparisonCarItem comparisonCarItem = dataList.get(contentRow).get(0);
        int visibility = comparisonCarItem.isHeader() ? View.VISIBLE : View.GONE;
        if (visibility != view.getVisibility()) {
            view.setVisibility(visibility);
        }
        if (!tableRowTitleView.section_title.getText().equals(comparisonCarItem.getRowTitle())) {
            tableRowTitleView.section_title.setText(comparisonCarItem.getRowTitle());
        }
        if (!tableRowTitleView.section_sub_title.getText().equals(PARAM_TIP_TXT)) {
            tableRowTitleView.section_sub_title.setText(PARAM_TIP_TXT);
        }

        return view;
    }

    @Override
    public View getFooterView(ListView view) {
        View footer_view = LayoutInflater.from(mContext).inflate(R.layout.layout_param_section, null);
        footer_view.setBackgroundColor(mContext.getResources().getColor(android.R.color.white));

        TextView tvTitle = (TextView) footer_view.findViewById(R.id.section_title);
        tvTitle.setText("注:以上仅供参考,请以实车为准！");
        tvTitle.setPadding(20, 20, 20, 20);
        tvTitle.setTextColor(mContext.getResources().getColor(android.R.color.holo_blue_dark));

        TextView tvSubTitle = (TextView) footer_view.findViewById(R.id.section_sub_title);
        tvSubTitle.setText(null);

        return footer_view;
    }

    @Override
    public Object getItem(int contentRow) {
        return dataList.get(contentRow);
    }

    //每一行被点击的时候的回调
    @Override
    public void OnClickContentRowItem(int row, View convertView) {

    }

    class TableItemView {
        FrameLayout param_item_content;
        TextView param_item_tv;
        ImageView param_item_del;
    }

    class TableSectionView {
        TextView section_title;
        TextView section_sub_title;
    }

    public int dip2px(Context context, float dipValue) {
        if (context == null) {
            return (int) dipValue;
        }
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }
}
