# CommonRecyclerView

# Gradle
`compile 'cn.ittiger:recyclerview:1.5'`

# 封装了RecyclerView为RecyclerView增加了如下4个常用功能：

## 1.设置RecyclerView中每项的点击事件
mAdapter.setOnItemClickListener(new CommonRecyclerViewAdapter.OnItemClickListener());

## 2.设置RecyclerView中每项的长按点击事件
mAdapter.setOnItemLongClickListener(new CommonRecyclerViewAdapter.OnItemLongClickListener());

## 3.列表滑动到底部自动加载更多
mRecyclerView.setOnLoadMoreListener(new CommonRecyclerView.LoadMoreListener());
如果不想开启滑动到底部自动加载更多这个功能，则可以不设置加载更多监听，
也可以通过mRecyclerView.setEnableAutoLoadMore(false)关闭此功能

## 4.与ListView一样支持设置EmptyView
mRecyclerView.setEmptyView(View view);

# 封装RecyclerView.Adapter使其更通用

## 1.可以添加任意视图作为HeaderView
通过CommonRecyclerViewAdapter实例调用addHeaderView方法可以添加多个HeaderView，但是HeaderView的显示默认是关闭的，
添加之前需要先通过mAdapter.enableHeaderView()方法时RecyclerView中的HeaderView功能可用

## 2.可以添加任意视图作为FooterView
通过CommonRecyclerViewAdapter实例调用addFooterView方法可以添加多个FooterView，但是FooterView的显示默认是关闭的，
添加之前需要先通过mAdapter.enableFooterView()方法时RecyclerView中的FooterView功能可用

## 3.更新mAdapter时，RecyclerView刷新采用局部刷新
