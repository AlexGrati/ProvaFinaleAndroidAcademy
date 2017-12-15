package it.grati_alexandru.provafinaleandroidacademy;

import it.grati_alexandru.provafinaleandroidacademy.Model.Package;
import it.grati_alexandru.provafinaleandroidacademy.Utils.DateConversion;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Date;
import java.util.List;

/**
 * Created by Alex on 14-Dec-17.
 */


public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.CardHolder> {

    public static class CardHolder extends RecyclerView.ViewHolder{
        private Context context;
        private TextView tPackSize;
        private TextView tPackStatus;
        private TextView tDeliveryDate;
        private TextView tPackId;
        private CardView cardView;
        public CardHolder(View v, final Context context){
            super(v);
            cardView = v.findViewById(R.id.cardViewId);
            tPackId = v.findViewById(R.id.textViewPackId);
            tDeliveryDate = v.findViewById(R.id.textViewDeliveryDate);
            tPackSize = v.findViewById(R.id.textViewPackSize);
            tPackStatus = v.findViewById(R.id.textViewStatus);

            this.context = context;

            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context,PackageActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra("PACKAGE_ID",tPackId.getText().toString());
                    context.startActivity(intent);
                }
            });
        }
    }

    private Context context;
    private List<Package> packageList;
    private Package p;

    public RecyclerAdapter(Context context, List<Package> packageList){
        this.context = context;
        this.packageList = packageList;
    }

    @Override
    public CardHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.package_card_item, parent,false);

        CardHolder cardHolder = new CardHolder(v,context);
        return cardHolder;
    }

    @Override
    public void onBindViewHolder(CardHolder holder, int position) {
        p = packageList.get(position);
        holder.tPackId.setText(""+p.getId());
        holder.tDeliveryDate.setText(DateConversion.formatDateToString(p.getDeliveryDate()));
        holder.tPackStatus.setText(p.getStatus());
        holder.tPackSize.setText(p.getSize());
    }

    @Override
    public int getItemCount() {
        return packageList.size();
    }
}