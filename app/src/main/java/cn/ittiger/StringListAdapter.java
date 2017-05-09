package cn.ittiger;

import cn.ittiger.recyclerview.CommonRecyclerViewAdapter;
import cn.ittiger.recyclerview.demo.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/**
 * laohu
 */
public class StringListAdapter extends CommonRecyclerViewAdapter<String> {

    private Context mContext;

    public StringListAdapter(Context context, List<String> list) {

        super(list);
        mContext = context;
    }

    @Override
    public CommonViewHolder onCreateItemViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(mContext).inflate(R.layout.item_layout, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindItemViewHolder(CommonRecyclerViewAdapter.CommonViewHolder holder, int position, String item) {

        ItemViewHolder viewHolder = (ItemViewHolder) holder;
        viewHolder.mTextView.setText(item);
    }

    public class ItemViewHolder extends CommonRecyclerViewAdapter.CommonViewHolder {

        public TextView mTextView;

        public ItemViewHolder(View itemView) {

            super(itemView);
            mTextView = (TextView) itemView.findViewById(R.id.textView);
        }
    }
}
