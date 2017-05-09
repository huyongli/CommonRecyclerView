package cn.ittiger.recyclerview;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.AttributeSet;
import android.view.View;

/**
 * 通用RecyclerView，支持添加ItemClick和ItemLongClick事件，同时支持自动加载更多
 * Created by laohu on 16-7-21.
 */
public class CommonRecyclerView extends RecyclerView {

    private OnScrollListener mOnScrollListener;
    private LoadMoreListener mLoadMoreListener;
    private boolean mIsAutoLoadMore = true;//是否自动加载更多
    private int mLastVisiblePosition = 0;

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

        //设置加载更多处理
        super.setOnScrollListener(new OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {

                super.onScrollStateChanged(recyclerView, newState);
                if(newState == SCROLL_STATE_IDLE && mIsAutoLoadMore && mLoadMoreListener != null) {
                    int firstPosition = getFirstVisiblePosition();
                    if(mLastVisiblePosition + 1 == getAdapter().getItemCount() && firstPosition != 0) {
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
     * 获取第一条展示的位置
     *
     * @return
     */
    public int getFirstVisiblePosition() {

        int position;
        if (getLayoutManager() instanceof LinearLayoutManager) {
            position = ((LinearLayoutManager) getLayoutManager()).findFirstVisibleItemPosition();
        } else if (getLayoutManager() instanceof GridLayoutManager) {
            position = ((GridLayoutManager) getLayoutManager()).findFirstVisibleItemPosition();
        } else if (getLayoutManager() instanceof StaggeredGridLayoutManager) {
            StaggeredGridLayoutManager layoutManager = (StaggeredGridLayoutManager) getLayoutManager();
            int[] firstPositions = layoutManager.findFirstVisibleItemPositions(new int[layoutManager.getSpanCount()]);
            position = getMinPosition(firstPositions);
        } else {
            position = 0;
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
     * 获得最大的位置
     *
     * @param positions
     * @return
     */
    private int getMinPosition(int[] positions) {

        int minPosition = Integer.MAX_VALUE;
        for (int i = 0; i < positions.length; i++) {
            minPosition = Math.min(minPosition, positions[i]);
        }
        return minPosition;
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
        if (adapter != null) {
            adapter.registerAdapterDataObserver(mDataObserver);
        }
        checkIfEmpty();
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
