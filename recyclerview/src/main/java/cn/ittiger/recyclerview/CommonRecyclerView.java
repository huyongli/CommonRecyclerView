package cn.ittiger.recyclerview;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;

/**
 * 通用RecyclerView，支持添加ItemClick和ItemLongClick事件，同时支持自动加载更多
 * Created by laohu on 16-7-21.
 */
public class CommonRecyclerView extends RecyclerView {

    private GestureDetector mGestureDetector;
    private OnItemClickListener mOnItemClickListener;
    private OnItemLongClickListener mItemLongClickListener;
    private OnScrollListener mOnScrollListener;
    private LoadMoreListener mLoadMoreListener;
    private boolean mIsAutoLoadMore = true;//是否自动加载更多
    private CommonRecyclerViewAdapter mCommonRecyclerViewAdapter;
    private int mLastVisiblePosition = 0;
    private final ArrayList<OnItemTouchListener> mOnItemTouchListeners =
            new ArrayList<>();

    public CommonRecyclerView(Context context) {

        this(context, null);
    }

    public CommonRecyclerView(Context context, AttributeSet attrs) {

        this(context, attrs, 0);
    }

    public CommonRecyclerView(Context context, AttributeSet attrs, int defStyle) {

        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context context) {

        mGestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {

            @Override
            public void onLongPress(MotionEvent e) {

                super.onLongPress(e);
                if(mItemLongClickListener != null) {
                    View childView = findChildViewUnder(e.getX(), e.getY());
                    if(childView != null) {
                        int position = getChildLayoutPosition(childView);
                        if(!(mCommonRecyclerViewAdapter.isHeaderViewPosition(position) ||
                            mCommonRecyclerViewAdapter.isFooterViewPosition(position))) {
                            int headerViewCount = mCommonRecyclerViewAdapter.getHeaderViewCount();
                            mItemLongClickListener.onItemLongClick(position - headerViewCount, childView);
                        }
                    }
                }
            }

            @Override
            public boolean onSingleTapUp(MotionEvent e) {

                if(mOnItemClickListener != null) {
                    View childView = findChildViewUnder(e.getX(),e.getY());
                    if(childView != null){
                        boolean handled = childView.dispatchTouchEvent(e);
                        if(handled) {
                            return true;
                        }
                        int position = getChildLayoutPosition(childView);
                        if(!(mCommonRecyclerViewAdapter.isHeaderViewPosition(position) ||
                                mCommonRecyclerViewAdapter.isFooterViewPosition(position))) {
                            int headerViewCount = mCommonRecyclerViewAdapter.getHeaderViewCount();
                            mOnItemClickListener.onItemClick(position - headerViewCount, childView);
                        }
                        return true;
                    }
                }
                return super.onSingleTapUp(e);
            }

        }) {
            @Override
            public boolean onTouchEvent(MotionEvent ev) {

                dispatchItemTouchEvent(ev);
                return super.onTouchEvent(ev);
            }
        };

        super.addOnItemTouchListener(new SimpleOnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {

                if (mGestureDetector.onTouchEvent(e)) {//交由手势处理
                    return true;
                }
                return false;
            }
        });

        //设置加载更多处理
        super.setOnScrollListener(new OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {

                super.onScrollStateChanged(recyclerView, newState);
                if(newState == SCROLL_STATE_IDLE && mIsAutoLoadMore && mLoadMoreListener != null) {
                    if(mLastVisiblePosition + 1 == getAdapter().getItemCount()) {
                        mLoadMoreListener.onLoadMore();
                    }
                }
                if(mOnScrollListener != null) {
                    mOnScrollListener.onScrollStateChanged(recyclerView, newState);
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {

                super.onScrolled(recyclerView, dx, dy);
                if(mIsAutoLoadMore && mLoadMoreListener != null) {
                    mLastVisiblePosition = getLastVisiblePosition();
                }
                if(mOnScrollListener != null) {
                    mOnScrollListener.onScrolled(recyclerView, dx, dy);
                }
            }
        });
    }

    @Override
    public void addOnItemTouchListener(OnItemTouchListener listener) {

        super.addOnItemTouchListener(listener);
        mOnItemTouchListeners.add(listener);
    }

    private void dispatchItemTouchEvent(MotionEvent event) {

        for(OnItemTouchListener listener : mOnItemTouchListeners) {
            listener.onTouchEvent(this, event);
        }
    }

    /**
     * 获取最后一条展示的位置
     *
     * @return
     */
    public int getLastVisiblePosition() {

        int position;
        if (getLayoutManager() instanceof LinearLayoutManager) {
            position = ((LinearLayoutManager) getLayoutManager()).findLastVisibleItemPosition();
        } else if (getLayoutManager() instanceof GridLayoutManager) {
            position = ((GridLayoutManager) getLayoutManager()).findLastVisibleItemPosition();
        } else if (getLayoutManager() instanceof StaggeredGridLayoutManager) {
            StaggeredGridLayoutManager layoutManager = (StaggeredGridLayoutManager) getLayoutManager();
            int[] lastPositions = layoutManager.findLastVisibleItemPositions(new int[layoutManager.getSpanCount()]);
            position = getMaxPosition(lastPositions);
        } else {
            position = getLayoutManager().getItemCount() - 1;
        }
        return position;
    }

    /**
     * 获得最大的位置
     *
     * @param positions
     * @return
     */
    private int getMaxPosition(int[] positions) {

        int maxPosition = Integer.MIN_VALUE;
        for (int i = 0; i < positions.length; i++) {
            maxPosition = Math.max(maxPosition, positions[i]);
        }
        return maxPosition;
    }

    /**
     * 设置是否允许自动加载更多，默认为true
     * 设置之后，还需要设置加载更多的监听
     *
     * @param autoLoadMore
     */
    public void setEnableAutoLoadMore(boolean autoLoadMore) {

        mIsAutoLoadMore = autoLoadMore;
    }

    @Override
    public void setOnScrollListener(OnScrollListener listener) {

        this.mOnScrollListener = listener;
    }

    public void setAdapter(CommonRecyclerViewAdapter adapter) {

        final Adapter oldAdapter = getAdapter();
        if (oldAdapter != null) {
            oldAdapter.unregisterAdapterDataObserver(mDataObserver);
        }
        super.setAdapter(adapter);
        mCommonRecyclerViewAdapter = adapter;
        if (adapter != null) {
            adapter.registerAdapterDataObserver(mDataObserver);
        }
        checkIfEmpty();
    }

    /**
     * 集成其他三方RecyclerView的相关库，如：recyclerview-animators时，
     * 所设置的Adapter不是CommonRecyclerViewAdapter类型时，可以调用此方法重新设置CommonRecyclerViewAdapter
     *
     * @param adapter
     */
    public void setCommonRecyclerViewAdapter(CommonRecyclerViewAdapter adapter) {

        mCommonRecyclerViewAdapter = adapter;
    }

    /**
     * 设置Item单击监听
     *
     * @param itemClickListener
     */
    public void setOnItemClickListener(OnItemClickListener itemClickListener) {

        this.mOnItemClickListener = itemClickListener;
    }

    /**
     * 设置Item长按监听
     *
     * @param itemLongClickListener
     */
    public void setOnItemLongClickListener(OnItemLongClickListener itemLongClickListener) {

        this.mItemLongClickListener = itemLongClickListener;
    }

    /**
     * 设置加载更多监听
     *
     * @param loadMoreListener
     */
    public void setOnLoadMoreListener(LoadMoreListener loadMoreListener) {

        mLoadMoreListener = loadMoreListener;
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

    /**
     * 加载更多监听
     */
    public interface LoadMoreListener {

        /**
         * UI线程
         */
        void onLoadMore();
    }

    private View emptyView;

    final private AdapterDataObserver mDataObserver = new AdapterDataObserver() {
        @Override
        public void onChanged() {

            checkIfEmpty();
        }

        @Override
        public void onItemRangeInserted(int positionStart, int itemCount) {

            checkIfEmpty();
        }

        @Override
        public void onItemRangeRemoved(int positionStart, int itemCount) {

            checkIfEmpty();
        }
    };

    private void checkIfEmpty() {

        if (emptyView != null && getAdapter() != null) {
            final boolean emptyViewVisible =
                    getAdapter().getItemCount() == 0;
            emptyView.setVisibility(emptyViewVisible ? VISIBLE : GONE);
            setVisibility(emptyViewVisible ? GONE : VISIBLE);
        }
    }

    /**
     * 实现与ListView中相同的效果，设置EmptyView
     * @param emptyView
     */
    public void setEmptyView(View emptyView) {

        this.emptyView = emptyView;
        checkIfEmpty();
    }
}
