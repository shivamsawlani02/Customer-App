package com.dsdairysytem.clientappfinal;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class Cardadapter extends ArrayAdapter<Carddetails> {

    ArrayAdapter<String> arrayAdapter;
    Carddetails model;







    public Cardadapter(@NonNull Context context) {
        super(context, R.layout.card_deatails);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ViewHolder holder;
        if (convertView!=null){
            model.arrayList().clear();
            return  convertView;
        }

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            convertView = inflater.inflate(R.layout.card_deatails, parent, false);
            holder = new ViewHolder(convertView);


            //    holder.imageView.setImageResource(model.getImageId());

        } else {
            holder = (ViewHolder) convertView.getTag();
            convertView.setTag(holder);

        }
        model = getItem(position);



        arrayAdapter=new ArrayAdapter<String>(getContext(),R.layout.simple_string_list,R.id.miltext,model.arrayList());

        holder.tvTitle.setText(model.getTitle());
        holder.tvSubtitle.setText(model.getSubtitle());
        if(!model.arrayList().isEmpty()) {
            holder.listView.setAdapter(arrayAdapter);
        }
        convertView.setTag(holder);
        arrayAdapter.notifyDataSetChanged();


        return convertView;

    }

    static class ViewHolder {
        TextView tvTitle;
        TextView tvSubtitle;

        ListView listView;

        ViewHolder(View view) {
            tvTitle = (TextView) view.findViewById(R.id.text_title);
            tvSubtitle = (TextView) view.findViewById(R.id.text_number);
            listView=view.findViewById(R.id.milklist);
        }
    }

}
