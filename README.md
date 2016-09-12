# CommonRecyclerView

此项目封装了RecyclerView和RecyclerView.Adapter，为RecyclerView增加了如下五个常用功能：

## 1.添加HeaderView
通过HeaderAndFooterAdapter实例调用addHeaderView方法可以添加多个HeaderView，但是HeaderView的显示默认是关闭的，
添加之前需要先通过mAdapter.enableHeaderView()方法时RecyclerView中的HeaderView功能可用

## 2.添加FooterView
通过HeaderAndFooterAdapter实例调用addFooterView方法可以添加多个FooterView，但是FooterView的显示默认是关闭的，
添加之前需要先通过mAdapter.enableFooterView()方法时RecyclerView中的FooterView功能可用

## 3.设置RecyclerView中每项的点击事件
mRecyclerView.setOnItemClickListener(new CommonRecyclerView.OnItemClickListener());

## 4.设置RecyclerView中每项的长按点击事件
mRecyclerView.setOnItemLongClickListener(new CommonRecyclerView.OnItemLongClickListener());

## 5.列表滑动到底部自动加载更多
mRecyclerView.setOnLoadMoreListener(new CommonRecyclerView.LoadMoreListener());
如果不想开启滑动到底部自动加载更多这个功能，则可以不设置加载更多监听，
也可以通过mRecyclerView.setEnableAutoLoadMore(false)关闭此功能

