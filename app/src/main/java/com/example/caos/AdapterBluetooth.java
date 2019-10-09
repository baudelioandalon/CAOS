package com.example.caos;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class AdapterBluetooth extends RecyclerView.Adapter<AdapterBluetooth.ImageViewHolder>  {

    private Context mContext;
    private ArrayList<ModeloBluetooth> mUploads;
    private OnItemClickListener mListener;
    View v;

    public AdapterBluetooth(Context context, ArrayList<ModeloBluetooth> uploads){
        mContext = context;
        mUploads = uploads;

    }


    @Override
    public ImageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            v = LayoutInflater.from(mContext).inflate(R.layout.device_name, parent, false);
        return new ImageViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ImageViewHolder holder, int position) {
        //PONER DATOS EN COMPONENTES
        ModeloBluetooth uploadCurrent = mUploads.get(position);
        final String name = uploadCurrent.getNameBluetooth().toUpperCase();

        holder.text_name.setText(name);
        holder.text_address.setText(uploadCurrent.getAddressBluetooth());
    }

    @Override
    public int getItemCount() {
        return mUploads.size();
    }

    public class ImageViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView text_name, text_address;
        ImageView text_image;

        public ImageViewHolder(View itemView) {
            super(itemView);
            text_name = itemView.findViewById(R.id.text_name);
            text_image = itemView.findViewById(R.id.text_image);
            text_address = itemView.findViewById(R.id.text_address);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (mListener!=null){
                int position = getAdapterPosition();
                if ( position!= RecyclerView.NO_POSITION){
                    mListener.onItemClick(position);
                }
            }
        }

    }//IMAGEVIEWHOLDER


    public interface  OnItemClickListener{
        void onItemClick(int position);


    }

    public void setOnItemClickListener(OnItemClickListener listener){
        mListener = listener;
    }

}


