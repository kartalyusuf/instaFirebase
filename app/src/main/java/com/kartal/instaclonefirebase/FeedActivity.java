package com.kartal.instaclonefirebase;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.auth.User;

import java.util.ArrayList;
import java.util.Map;

public class FeedActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth; //Kullanıcılar (Users)
    private FirebaseFirestore firebaseFirestore; //Veritabanı

    // "recyclerview" kullanabilmek için ve heryerden ulaşabilmek için arraylist oluşturduk. recylerview aslında bir listview dir. Daha gelişmişi diyebiliriz.
    ArrayList<String> userEmailFirebase;
    ArrayList<String> commentFirebase;
    ArrayList<String> imageFirebase;

    FeedRecyclerAdapter feedRecyclerAdapter ;


    // Options Menu kullanabilmek için önce yaratıyoruz.
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.insta_options_menu, menu);


        return super.onCreateOptionsMenu(menu);
    }

    // Seçilen item ile ne yapılacağı
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == R.id.upload) {

            Intent intentUpload = new Intent(FeedActivity.this, UploadActivity.class);
            startActivity(intentUpload);

        } else if (item.getItemId() == R.id.cikis) {

            firebaseAuth.signOut();

            Intent intentCikis = new Intent(FeedActivity.this, Users.class);
            startActivity(intentCikis);
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed);

        userEmailFirebase = new ArrayList<>();
        commentFirebase = new ArrayList<>();
        imageFirebase = new ArrayList<>();

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        getDataFromFireStore();

        //RecyclerView tanıtalım
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this)); //Tek tek sırayla dizileceğini belirtiyoruz.

        //FeedRecylerAdapterda oluşturduğumuz constructorla beraber burayı kullandık.Yani listede neler olacağının atamalarını yaptık.
        feedRecyclerAdapter = new FeedRecyclerAdapter(userEmailFirebase,commentFirebase,imageFirebase);

        //Recyclerview birbirine bağlama işlemi
        recyclerView.setAdapter(feedRecyclerAdapter);


    }


    //Verileri okumak için method açtık ;
    public void getDataFromFireStore() {

        // Veritabanında koleksiyon(collection) oluşturmuştuk. Ordan verileri çekmek istiyoruz sayfaya.
        //Kodların nasıl kullanılacağı firebase sayfasında açıklanıyor zaten.

        CollectionReference collectionReference = firebaseFirestore.collection("Posts");
        //Tarihe göre sıralama filtrelemesi yaptık. Yani arayüzde en son atılan postlar ilk başta gözükücek.
        collectionReference.orderBy("date", Query.Direction.ASCENDING).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {

                if (e != null){
                    Toast.makeText(FeedActivity.this,e.getLocalizedMessage().toString(),Toast.LENGTH_LONG).show();
                }

                if (queryDocumentSnapshots != null) {

                    for (DocumentSnapshot snapshot : queryDocumentSnapshots.getDocuments()) {

                        Map<String,Object> data = snapshot.getData();
                        String comment = (String) data.get("comment"); // parantez (string) yapmamızın sebebi görmüyo kodu. Biz de casting yapıyoruz.
                        String useremail = (String) data.get("useremail");
                        String dowloadurl = (String) data.get("dowloadurl");

                        //Arraylist e ekledik.
                        userEmailFirebase.add(useremail);
                        commentFirebase.add(comment);
                        imageFirebase.add(dowloadurl);

                        feedRecyclerAdapter.notifyDataSetChanged();  //Adapter uyarma işlemi. İçeriye yeni bir veri geldi demek için bunu yapıyoruz.



                    }
                }

            }
        });



    }
}