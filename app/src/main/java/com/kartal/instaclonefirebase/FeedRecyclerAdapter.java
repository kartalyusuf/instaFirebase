package com.kartal.instaclonefirebase;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;


//PostHolder isim verdik sadece herhangi bir kod değil.
public class FeedRecyclerAdapter extends RecyclerView.Adapter<FeedRecyclerAdapter.PostHolder> {

    //Bunlar kullanacağımız özelliklerimiz. Sonrasında constructor oluşturduk bunlarla.
    private ArrayList<String> userEmailList;
    private ArrayList<String> commentList;
    private ArrayList<String> imageList;

    //constructor
    public FeedRecyclerAdapter(ArrayList<String> userEmailList, ArrayList<String> commentList, ArrayList<String> imageList) {
        this.userEmailList = userEmailList;
        this.commentList = commentList;
        this.imageList = imageList;
    }

    //Burada bağlama (oluşturma) işlemi yapıyoruz.
    @NonNull
    @Override
    public PostHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.recycler_row,parent,false); //bizden bi root istiyor. yani başka yere bağlanacak mı hayır o yüzden false yaptık.

        return new PostHolder(view); //Bizden postholder istiyo o yğzden bunu döndürüyoruz.
    }


    //Burada bağlama işlemi yapıldıktan sonra ne yapacağını söylüyoruz.
    @Override
    public void onBindViewHolder(@NonNull PostHolder holder, int position) {

        holder.useremail.setText(userEmailList.get(position));
        holder.comment.setText(commentList.get(position));
        Picasso.get().load(imageList.get(position)).into(holder.postAt); //picasso kütüphanesini buildapp e yükledik önce. sonra burda kodu yazdık. Kolay bi kod.


    }

    //Listede kaç tane row (satır) olcak onu yazıyoruz.
    @Override
    public int getItemCount() {
        return userEmailList.size(); //Buraya resim de yapabilirdik comment de farketmiyor.Biz email kullandık. SİZE kadar atabilirsin anlamında. Binevi sınırsız.
    }


    //görünümler bunun içinde tanımlanıyo aslında. recycler_row 'da yaptığımız görünümler.

    class PostHolder extends RecyclerView.ViewHolder {

        ImageView postAt;
        TextView useremail;
        TextView comment;


        //yukarıda tanımladıklarımızı burada çağırıyoruz.
        public PostHolder(@NonNull View itemView) {
            super(itemView);

            postAt = itemView.findViewById(R.id.postAt);
            useremail =itemView.findViewById(R.id.useremail);
            comment = itemView.findViewById(R.id.comment);



        }
    }
}
