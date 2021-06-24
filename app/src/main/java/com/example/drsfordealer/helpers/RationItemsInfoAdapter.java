package com.example.drsfordealer.helpers;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.drsfordealer.R;
import com.example.drsfordealer.database.RationItemModel;

import java.util.List;

public class RationItemsInfoAdapter extends RecyclerView.Adapter<RationItemsInfoAdapter.RationItemsInfoViewHolder>{

    private Context context;
    private List<RationItemModel> rationItemModelList;

    public RationItemsInfoAdapter(Context context, List<RationItemModel> rationItemModelList) {
        this.context = context;
        this.rationItemModelList = rationItemModelList;
    }

    @NonNull
    @Override
    public RationItemsInfoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.layout_bill_info_recycler_item, null);
        return new RationItemsInfoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RationItemsInfoViewHolder holder, int position) {
        RationItemModel rationItem = rationItemModelList.get(position);
        holder.textViewItemName.setText(rationItem.getItemName());
        holder.textViewItemPricePerUnit.setText(rationItem.getPrice());
        holder.textViewItemAmount.setText(rationItem.getMaxPerHead());
        holder.textViewItemSubtotal.setText(rationItem.getSubtotal());
    }

    @Override
    public int getItemCount() {
        return rationItemModelList.size();
    }

    class RationItemsInfoViewHolder extends RecyclerView.ViewHolder {

        TextView textViewItemName;
        TextView textViewItemPricePerUnit;
        TextView textViewItemAmount;
        TextView textViewItemSubtotal;

        public RationItemsInfoViewHolder(@NonNull View itemView) {
            super(itemView);

            textViewItemName = itemView.findViewById(R.id.tv_ration_item_name);
            textViewItemPricePerUnit = itemView.findViewById(R.id.tv_ration_item_price);
            textViewItemAmount = itemView.findViewById(R.id.tv_ration_item_amt);
            textViewItemSubtotal = itemView.findViewById(R.id.tv_ration_item_subtotal);
        }
    }
}
