package hop.com.myrecyclerview;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by Hop on 02/08/2015.
 */
public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {
    private final String TAG = RecyclerViewAdapter.class.getSimpleName();
    private List<Item> mItems;
    private Context mContext;

    private SparseBooleanArray selectedItems;
    private RVOnItemClickListener mListener;


    private final int TYPE_INACTIVE = 0;
    private final int TYPE_ACTIVE = 1;

    public RecyclerViewAdapter(List<Item> mItems, Context mContext) {
        this.mItems = mItems;
        this.mContext = mContext;
        selectedItems = new SparseBooleanArray();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //Choose a suitable layout
        int layout = (viewType == TYPE_ACTIVE) ? R.layout.item_active_recycler_view : R.layout.item_recycler_view;
        View rootView = LayoutInflater.from(mContext).inflate(layout, parent, false);
        ViewHolder holder = new ViewHolder(rootView);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Item item = mItems.get(position);
        holder.mTvTitle.setText(item.getTitle());
        holder.mTvSubTitle.setText(item.getSubtitle() + ", which is " + (item.isActive() ? "active" : "inactive"));

        // Span the item if active
        /*final ViewGroup.LayoutParams lp = holder.itemView.getLayoutParams();
        if (lp instanceof StaggeredGridLayoutManager.LayoutParams) {
            StaggeredGridLayoutManager.LayoutParams sglp = (StaggeredGridLayoutManager.LayoutParams) lp;
            sglp.setFullSpan(item.isActive());
            holder.itemView.setLayoutParams(sglp);
        }*/
        final ViewGroup.LayoutParams lp = holder.itemView.getLayoutParams();
        if (lp instanceof StaggeredGridLayoutManager.LayoutParams) {
            StaggeredGridLayoutManager.LayoutParams sglp = (StaggeredGridLayoutManager.LayoutParams) lp;
            sglp.setFullSpan(item.isActive());
            holder.itemView.setLayoutParams(sglp);
        }

        holder.selectedOverlay.setVisibility(isSelected(position) ? View.VISIBLE : View.INVISIBLE);
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    @Override
    public int getItemViewType(int position) {
        Item item = mItems.get(position);
        return item.isActive() ? TYPE_ACTIVE : TYPE_INACTIVE;
    }

    public void setOnRVItemClickListener(RVOnItemClickListener listener) {
        this.mListener = listener;
    }

    /**
     * Count the number of selected items
     *
     * @return the number of selected items
     */
    public int getSelectedItemCount() {
        return selectedItems.size();
    }

    /**
     * Get the list of positions selected
     *
     * @return the list int of positions selected
     */
    public List<Integer> getSelectedItems() {
        List<Integer> items = new ArrayList<>(selectedItems.size());
        for (int i = 0; i < selectedItems.size(); i++) {
            int key = selectedItems.keyAt(i);
            items.add(new Integer(selectedItems.keyAt(i)));
        }
        return items;
    }

    /**
     * Check whether the item at positon was selected
     *
     * @param position The position of the item
     * @return true if the items was selected
     */
    public boolean isSelected(int position) {
        return getSelectedItems().contains(position);
    }

    /**
     * Enable selected mode of the item at the position if it was not selected or
     * deselect it if it was selected.
     *
     * @param position The position of the item
     */
    public void toggleSelection(int position) {
        if (isSelected(position)) {
            //The item was selected
            selectedItems.delete(position);
        } else {
            //The item was not selected
            selectedItems.put(position, true);
        }
        notifyItemChanged(position);
    }

    /**
     * Clear the selection status for all items
     */
    public void clearSelection() {
        List<Integer> items = getSelectedItems();
        selectedItems.clear();
        for (Integer position : items)
            notifyItemChanged(position);
    }

    /**
     * Remove the item at the position
     *
     * @param position The position of the item
     */
    public void removeItem(int position) {
        mItems.remove(position);
        notifyItemRemoved(position);
    }

    /**
     * Remove a list of items
     *
     * @param positions List of positions need to be removed
     */
    public void removeItems(List<Integer> positions) {
        //Reverse-sort the list
        Collections.sort(positions, new Comparator<Integer>() {
            @Override
            public int compare(Integer lhs, Integer rhs) {
                return rhs - lhs;
            }
        });

        //Split the list in ranges
        while (!positions.isEmpty()) {
            /*if (positions.size() == 1) {
                removeItem(positions.get(0));
                positions.remove(0);
            } else {
                //int count = 1;
                //Ignore equal items
                *//*while (positions.size() > count && positions.get(count).equals(positions.get(count - 1) - 1)) {
                    //Two equal elements are continuous
                    ++count;
                }*//*
                *//*if (count == 1) {
                    removeItem(positions.get(0));
                } else {
                    removeRange(positions.get(count - 1), count);
                }*//*

                *//*for (int i = 0; i < count; ++i) {
                    //Remove position from the list of positions
                    positions.remove(0);
                }*//*


            }*/

            removeItem(positions.get(0));
            positions.remove(0);
        }
    }

    /**
     * Remove multiple items are the same continuously
     *
     * @param positionStart The first position in the range
     * @param itemCount     The number of equal items in range
     */
    private void removeRange(int positionStart, int itemCount) {
        for (int i = 0; i < itemCount; ++i) {
            mItems.remove(positionStart);
        }
        notifyItemRangeRemoved(positionStart, itemCount);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,
            View.OnLongClickListener {
        TextView mTvTitle;
        TextView mTvSubTitle;
        CardView mCvContainer;
        View selectedOverlay;

        public ViewHolder(View itemView) {
            super(itemView);
            mTvTitle = (TextView) itemView.findViewById(R.id.tvTitle);
            mTvSubTitle = (TextView) itemView.findViewById(R.id.tvSubTitle);
            mCvContainer = (CardView) itemView.findViewById(R.id.cvContainer);
            selectedOverlay = itemView.findViewById(R.id.selected_overlay);

            mCvContainer.setOnClickListener(this);
            mCvContainer.setOnLongClickListener(this);
            mTvTitle.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (mListener == null) {
                return;
            }
            int id = v.getId();
            if (id == mTvTitle.getId()) {
                mListener.onTitleClick(v, getLayoutPosition());
            } else if (id == mCvContainer.getId()) {
                mListener.onItemClick(v, getLayoutPosition());
            }
        }


        @Override
        public boolean onLongClick(View v) {
            if (mListener == null) {
                return false;
            }

            mListener.onItemLongClick(v, getLayoutPosition());
            return true;
        }
    }

    /**
     * Interface definition for a callback to be invoked when an item on RecyclerView
     * was clicked
     */
    public interface RVOnItemClickListener {
        /**
         * Called when an item was clicked
         *
         * @param v        The view was clicked
         * @param position The position of the item in list items
         */
        void onItemClick(View v, int position);

        /**
         * Called when a title of  an item was clicked
         *
         * @param v        The view was clicked
         * @param position The position of the item in list items
         */
        void onTitleClick(View v, int position);

        /**
         * Called when an item was long-clicked
         *
         * @param v        The view was long-clicked
         * @param position The position of the item in list items
         */
        void onItemLongClick(View v, int position);
    }
}
