package cn.ittiger.recyclerview;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

public abstract class CommonRecyclerViewAdapter<T> extends RecyclerView.Adapter<CommonRecyclerViewAdapter.CommonViewHolder> {

    private static final int TYPE_HEADER = 100000;
    private static final int TYPE_FOOTER = 200000;

    protected List<T> mList;
    private SparseArray<View> mHeaderViews = new SparseArray<>(0);
    private SparseArray<View> mFooterViews = new SparseArray<>(0);

    private boolean mIsHeaderViewEnable = true;
    private boolean mIsFooterViewEnable = true;
    private int mColumnNums = 1;//列表的列数
    private boolean mIsLinearLayoutHorizontal = false;//是否为水平列表
    private OnItemClickListener mOnItemClickListener;
    private OnItemLongClickListener mItemLongClickListener;

    public CommonRecyclerViewAdapter(List<T> list) {

        mList = list;
    }

    @Override
    public final CommonViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        if(isHeaderViewEnable() && mHeaderViews.get(viewType) != null) {
            return new CommonViewHolder(mHeaderViews.get(viewType));
        } else if(isFooterViewEnable() && mFooterViews.get(viewType) != null) {
            return new CommonViewHolder(mFooterViews.get(viewType));
        }
        return onCreateItemViewHolder(parent, viewType);
    }

    public abstract CommonViewHolder onCreateItemViewHolder(ViewGroup parent, int viewType);

    public abstract void onBindItemViewHolder(CommonViewHolder holder, int position, T item);

    @Override
    public void onBindViewHolder(CommonRecyclerViewAdapter.CommonViewHolder holder, int position) {

        if(isFooterView(position) || isHeaderView(position)) {
            return;
        }
        T item = getItem(position);
        onBindItemViewHolder(holder, position, item);
    }

    public final int getItemViewType(int position) {

        if(isHeaderView(position)) {//FooterView
            return mHeaderViews.keyAt(position);
        }
        if(isFooterView(position)){//HeaderView
            return mFooterViews.keyAt(position - getHeaderViewCount() - getItemDataCount());
        }
        return getItemViewTypeForData(position);
    }

    /**
     * 展示的总数据数(包括HeaderView和FooterView)
     *
     * @return
     */
    @Override
    public final int getItemCount() {

        return getItemDataCount() + getHeaderViewCount() + getFooterViewCount();
    }

    /**
     * 要展示的有效数据数(不包括HeaderView和FooterView)
     *
     * @return
     */
    public int getItemDataCount() {

        return mList == null ? 0 : mList.size();
    }

    public List<T> getData() {

        return mList;
    }

    /**
     * 获取position位置的数据
     *
     * @param position
     * @return
     */
    public T getItem(int position) {

        position = position - getHeaderViewCount();
        return mList == null ? null : mList.get(position);
    }

    public void addAll(List<T> list) {

        int positionStart = getHeaderViewCount();
        if(mList == null) {
            mList = list;
        } else {
            positionStart += mList.size();
            mList.addAll(list);
        }
        notifyItemRangeInserted(positionStart, list.size());
        updateColumnNums();
    }

    public void add(T item) {

        if(mList == null) {
            mList = new ArrayList<>(1);
        }
        int size = getItemDataCount();
        mList.add(item);
        notifyDataSetChanged();
        updateColumnNums();
    }

    public void add(T item, int position) {

        if(mList == null) {
            mList = new ArrayList<>();
        }
        mList.add(position, item);
        notifyItemInserted(position);
        updateColumnNums();
    }

    public void update(T item) {

        if(mList == null) {
            mList = new ArrayList<>();
        }
        int idx = mList.indexOf(item);
        if (idx < 0) {
            add(item);
        } else {
            mList.set(idx, item);
            notifyItemChanged(idx);
        }
    }

    public void update(T item , int position) {

        if(mList == null) {
            mList = new ArrayList<>();
        }
        mList.set(position, item);
        notifyItemChanged(position);
    }

    public void reset(List<T> list) {

        mList = list;
        notifyDataSetChanged();
        updateColumnNums();
    }

    public void removeAll() {

        if(mList != null) {
            mList.clear();
            notifyDataSetChanged();
        }
    }

    /**
     * 获取待展示的position索引处数据的viewType
     *
     * @param position
     * @return
     */
    public int getItemViewTypeForData(int position) {

        return super.getItemViewType(position);
    }

    /**
     * 判断position位置是否为FooterView的索引
     *
     * @param position
     * @return
     */
    public boolean isFooterViewPosition(int position) {

        return position >= getItemDataCount() + getHeaderViewCount();
    }

    /**
     * 判断position位置是否为HeaderView的索引
     *
     * @param position
     * @return
     */
    public boolean isHeaderViewPosition(int position) {

        return position < getHeaderViewCount();
    }

    /**
     * 获取HeaderView的总数
     *
     * @return
     */
    public int getHeaderViewCount() {

        return isHeaderViewEnable() ? mHeaderViews.size() : 0;
    }

    /**
     * 获取FooterView的总数
     *
     * @return
     */
    public int getFooterViewCount() {

        return isFooterViewEnable() ? mFooterViews.size() : 0;
    }

    /**
     * HeaderView是否启用,默认不启用
     *
     * @return
     */
    public boolean isHeaderViewEnable() {

        return mIsHeaderViewEnable;
    }

    /**
     * 启用HeaderView
     */
    public void enableHeaderView() {

        mIsHeaderViewEnable = true;
    }

    /**
     * 禁用HeaderView
     */
    public void disableHeaderView() {

        mIsHeaderViewEnable = false;
    }

    /**
     * FooterView是否启用,默认不启用
     *
     * @return
     */
    public boolean isFooterViewEnable() {

        return mIsFooterViewEnable;
    }

    /**
     * 启用FooterView
     */
    public void enableFooterView() {

        mIsFooterViewEnable = true;
    }

    /**
     * 禁用FooterView
     */
    public void disableFooterView() {

        mIsFooterViewEnable = false;
    }

    /**
     * 判断position位置是否为FooterView
     *
     * @param position
     * @return
     */
    public boolean isFooterView(int position) {

        return isFooterViewEnable() && isFooterViewPosition(position);
    }

    /**
     * 判断position位置是否为HeaderView
     *
     * @param position
     * @return
     */
    public boolean isHeaderView(int position) {

        return isHeaderViewEnable() && isHeaderViewPosition(position);
    }

    /**
     * 添加一个HeaderView
     *
     * @param headerView
     */
    public void addHeaderView(View headerView) {

        if(headerView == null) {
            throw new NullPointerException("headerView is null");
        }
        mHeaderViews.put(TYPE_HEADER + getHeaderViewCount(), headerView);
        notifyItemInserted(getHeaderViewCount() - 1);
        updateColumnNums();
    }

    /**
     * 添加一个FooterView
     *
     * @param footerView
     */
    public void addFooterView(View footerView) {

        if(footerView == null) {
            throw new NullPointerException("footerView is null");
        }
        mFooterViews.put(TYPE_FOOTER + getFooterViewCount(), footerView);
        notifyItemInserted(getHeaderViewCount() + getItemDataCount() + getFooterViewCount() - 1);
        updateColumnNums();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {

        super.onAttachedToRecyclerView(recyclerView);
        final RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
        if(layoutManager instanceof GridLayoutManager) {
            mColumnNums = ((GridLayoutManager) layoutManager).getSpanCount();
            ((GridLayoutManager) layoutManager).setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {

                    return getNewSpanSize(mColumnNums, position);
                }
            });
        } else if(layoutManager instanceof StaggeredGridLayoutManager) {
            mColumnNums = ((StaggeredGridLayoutManager)layoutManager).getSpanCount();
        } else {
            if(((LinearLayoutManager)layoutManager).getOrientation() == LinearLayoutManager.VERTICAL) {
                mColumnNums = 1;
            } else {
                mColumnNums = getItemCount();
                mIsLinearLayoutHorizontal = true;
            }
        }
    }

    @Override
    public void onViewAttachedToWindow(CommonRecyclerViewAdapter.CommonViewHolder holder) {

        super.onViewAttachedToWindow(holder);
        int position = holder.getLayoutPosition();
        if(isHeaderView(position) || isFooterView(position)) {
            final ViewGroup.LayoutParams layoutParams = holder.itemView.getLayoutParams();
            if(layoutParams != null && layoutParams instanceof StaggeredGridLayoutManager.LayoutParams) {
                StaggeredGridLayoutManager.LayoutParams lp = (StaggeredGridLayoutManager.LayoutParams) layoutParams;
                lp.setFullSpan(true);
            }
        }
    }

    private int getNewSpanSize(int spanCount, int position) {

        if(isHeaderView(position) || isFooterView(position)) {
            return spanCount;
        }

        return getGridLayoutSpanSize(spanCount, position);
    }

    /**
     * RecyclerView作为GridView使用时，如果某项数据需要满屏展示，则需要重写此方法进行实现
     * 实现时只需要在相应position时返回参数spanCount值即可
     *
     * @param spanCount
     * @param position
     * @return
     */
    public int getGridLayoutSpanSize(int spanCount, int position) {

        return 1;
    }

    private void updateColumnNums() {

        if(mIsLinearLayoutHorizontal) {
            mColumnNums = getItemCount();
        }
    }

    /**
     * 获取当前的列数
     * @return
     */
    public int getColumnNums() {

        return mColumnNums;
    }

    public class CommonViewHolder extends RecyclerView.ViewHolder implements
            View.OnClickListener, View.OnLongClickListener {

        public CommonViewHolder(View itemView) {

            super(itemView);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View v) {

            if(mOnItemClickListener != null) {
                mOnItemClickListener.onItemClick(getLayoutPosition(), v);
            }
        }

        @Override
        public boolean onLongClick(View v) {

            if(mItemLongClickListener != null) {
                mItemLongClickListener.onItemLongClick(getLayoutPosition(), v);
            }
            return false;
        }
    }

    /**
     * Item项点击事件
     */
    public interface OnItemClickListener {

        void onItemClick(int position, View itemView);
    }

    /**
     * Item项长按点击事件
     */
    public interface OnItemLongClickListener {

        void onItemLongClick(int position, View itemView);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {

        mOnItemClickListener = onItemClickListener;
    }

    public void setOnItemLongClickListener(OnItemLongClickListener itemLongClickListener) {

        mItemLongClickListener = itemLongClickListener;
    }
}

