package cn.peter.carcomparison.vhtableview;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

/**
 * Created by jian on 2016/7/20.
 */
public interface VHBaseAdapter {

    // 表格内容的行数，不包括标题行
    int getContentRows();

    // 列数
    int getContentColumn();

    // 标题的view，这里从0开始，这里要注意，一定要有view返回去，不能为null，每一行
    // 各列的宽度就等于标题行的列的宽度，且边框的话，自己在这里和下文的表格单元格view里面设置
    View getTitleView(int columnPosition, ViewGroup parent);

    // 表格正文的view，行和列都从0开始，宽度的话在载入的时候，默认会是以标题行各列的宽度，高度的话自适应
    View getTableCellView(int contentRow, int contentColum, View view, ViewGroup parent);

    // 每一行的标题
    View getTableSectionView(int contentRow, View view);

    // footerview
    View getFooterView(ListView view);

    Object getItem(int contentRow);

    // 每一行被点击的时候的回调
    void OnClickContentRowItem(int row, View convertView);
}
