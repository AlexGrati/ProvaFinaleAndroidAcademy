package it.grati_alexandru.provafinaleandroidacademy;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import it.grati_alexandru.provafinaleandroidacademy.Model.Courier;
import it.grati_alexandru.provafinaleandroidacademy.Model.Package;
import it.grati_alexandru.provafinaleandroidacademy.Utils.DateConversion;

/**
 * Created by utente4.academy on 15/12/2017.
 */

public class CourierRecyclerAdapter extends RecyclerView.Adapter<CourierRecyclerAdapter.CardHolder> {

    public static class CardHolder extends RecyclerView.ViewHolder{
        private Context context;
        private TextView tCourierName;
        private CardView cardView;

        public CardHolder(View v, final Context context){
            super(v);
            cardView = v.findViewById(R.id.cardCourierId);
            tCourierName = v.findViewById(R.id.textViewCourierName);
            this.context = context;

            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, CourierActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra("COURIER_USERNAME",tCourierName.getText().toString());
                    context.startActivity(intent);
                }
            });
        }
    }

    private Context context;
    private List<Courier> courierList;
    private Courier c;

    public CourierRecyclerAdapter(Context context, List<Courier> courierList){
        this.context = context;
        this.courierList = courierList;
    }

    @Override
    public CardHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.courier_card_item, parent,false);
        CardHolder cardHolder = new CardHolder(v,context);
        return cardHolder;
    }

    @Override
    public void onBindViewHolder(CardHolder holder, int position) {
        c = courierList.get(position);
        holder.tCourierName.setText(c.getUsername());
    }

    @Override
    public int getItemCount() {
        return courierList.size();
    }
}