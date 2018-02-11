package payconiq.com.jakewhartongitgub.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import payconiq.com.jakewhartongitgub.R;
import payconiq.com.jakewhartongitgub.model.JakeWhartonPage;
import payconiq.com.jakewhartongitgub.util.Constants;


public class PaginationAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final List<JakeWhartonPage> repoData;
    private boolean isLoadingAdded = false;
    private Context context;

    public PaginationAdapter(Context context) {
        repoData = new ArrayList<>();
        this.context = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        switch (viewType) {
            case Constants.ITEM:
                viewHolder = getViewHolder(parent, inflater);
                break;
            case Constants.LOADING:
                View v2 = inflater.inflate(R.layout.item_progress, parent, false);
                viewHolder = new LoadingVH(v2);
                break;
        }
        return viewHolder;
    }

    @NonNull
    private RecyclerView.ViewHolder getViewHolder(ViewGroup parent, LayoutInflater inflater) {
        RecyclerView.ViewHolder viewHolder;
        View v1 = inflater.inflate(R.layout.repolist_row_item, parent, false);
        viewHolder = new RepoVH(v1);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        JakeWhartonPage result = repoData.get(position);

        switch (getItemViewType(position)) {
            case Constants.ITEM:
                final RepoVH repoVH = (RepoVH) holder;
                repoVH.fullNameTv.setText(result.getFullName());
                repoVH.forksTv.setText(String.valueOf(result.getForks()));
                repoVH.urlTv.setText(result.getUrl());
                break;
        }

    }

    @Override
    public int getItemCount() {
        return repoData.size();

    }

    @Override
    public int getItemViewType(int position) {
        return (position == repoData.size() - 1 && isLoadingAdded) ? Constants.LOADING : Constants.ITEM;
    }


    private void add(JakeWhartonPage r) {
        repoData.add(r);
        notifyItemInserted(repoData.size() - 1);
    }

    public void addAll(List<JakeWhartonPage> moveResults) {
        for (JakeWhartonPage result : moveResults) {
            add(result);
        }
    }

    public void stopLoading() {
        isLoadingAdded = false;
        notifyDataSetChanged();
    }

    public void addLoadingFooter() {
        isLoadingAdded = true;
    }

    public void removeLoadingFooter() {
        isLoadingAdded = false;
        int position = repoData.size() - 1;
        JakeWhartonPage result = getItem(position);
        if (result != null) {
            repoData.remove(position);
            notifyItemRemoved(position);
        }
    }

    private JakeWhartonPage getItem(int position) {
        return repoData.get(position);
    }


    class RepoVH extends RecyclerView.ViewHolder {
        private final TextView forksTv;
        private final TextView fullNameTv;
        private final TextView urlTv;

        public RepoVH(View itemView) {
            super(itemView);
            forksTv = (TextView) itemView.findViewById(R.id.forks);
            fullNameTv = (TextView) itemView.findViewById(R.id.full_name);
            urlTv = (TextView) itemView.findViewById(R.id.url);
        }
    }

    class LoadingVH extends RecyclerView.ViewHolder {

        public LoadingVH(View itemView) {
            super(itemView);
        }
    }


}
