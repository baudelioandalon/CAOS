package com.example.caos;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.onurkaganaldemir.ktoastlib.KToast;

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
        final ModeloBluetooth uploadCurrent = mUploads.get(position);
        final String name = uploadCurrent.getNameBluetooth().toUpperCase();

        holder.text_name.setText(name);
        holder.text_address.setText(uploadCurrent.getAddressBluetooth());
        holder.linear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                msj(name + " : " + uploadCurrent.getAddressBluetooth());
            }
        });
    }

    public void msj (String mensaje
    ){
        Toast.makeText(mContext,mensaje,Toast.LENGTH_SHORT).show();
//        if(type.equals("success")){
//            KToast.successToast(mContext.get, mensaje, Gravity.BOTTOM, KToast.LENGTH_AUTO);
//        } else if(type.equals("info")){
//            KToast.infoToast(this, mensaje, Gravity.BOTTOM, KToast.LENGTH_AUTO);
//        } else if(type.equals("normal")){
//            KToast.normalToast(this, mensaje, Gravity.BOTTOM, KToast.LENGTH_AUTO, R.drawable.ic_arroba);
//        }else if(type.equals("warning")) {
//            KToast.warningToast(this, mensaje, Gravity.BOTTOM, KToast.LENGTH_AUTO);
//        }else if(type.equals("error")) {
//            KToast.errorToast(this, mensaje, Gravity.BOTTOM, KToast.LENGTH_AUTO);
//        }else if(type.equals("customColor")) {
//            KToast.customColorToast(this, mensaje, Gravity.BOTTOM, KToast.LENGTH_AUTO, R.color.colorPrimary, R.drawable.ic_arroba);
//        }else if(type.equals("customBackground")) {
//            KToast.customBackgroudToast(this, mensaje, Gravity.BOTTOM, KToast.LENGTH_AUTO, R.drawable.ic_arroba,
//                    null ,R.drawable.ic_arroba);
//        }
    }

    @Override
    public int getItemCount() {
        return mUploads.size();
    }

    public class ImageViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView text_name, text_address;
        ImageView text_image;
        LinearLayout linear;

        public ImageViewHolder(View itemView) {
            super(itemView);
            linear = itemView.findViewById(R.id.linear);
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


