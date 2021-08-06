package com.kartal.instaclonefirebase;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.HashMap;
import java.util.UUID;

public class UploadActivity extends AppCompatActivity {

    Bitmap selectedImage ;
    ImageView imageView;
    EditText commentText ;

    Uri resimData ;

    //Depolama
    private FirebaseStorage firebaseStorage ;
    private StorageReference storageReference;

    //VeriTabanıyla çalışabilmek için:
    private FirebaseFirestore firebaseFirestore ;
    private FirebaseAuth firebaseAuth;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);

        imageView = findViewById(R.id.imageView);
        commentText = findViewById(R.id.commentText);

        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();



    }

    public void yukle(View view) {

        if (resimData != null) {


            //Universal unique id (UUID) >> Bunu her resime random id atamak için yapıyoruz
            UUID uuid = UUID.randomUUID();
            String imageId = "images/" + uuid + ".jpg";

            storageReference.child(imageId).putFile(resimData).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    Toast.makeText(UploadActivity.this,"Resim Yükleme Başarılı",Toast.LENGTH_LONG).show();

                    //Dowload URL İŞLEMLERİ
                    //Kaydettiğimiz resmin nereye kaydedildiğini bul diyoruz.
                    //Ek olarak veritabanına neleri alacağımızı da söylüyoruz.
                    StorageReference newReference = FirebaseStorage.getInstance().getReference(imageId);
                    newReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {


                            String dowloadUrl = uri.toString();

                            FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                            String userEmail = firebaseUser.getEmail();
                            String comment = commentText.getText().toString();


                            //Object demek herhangi bir tip olabilir demek. Yani değerlerimiz (Values) int de olabilir , string de olabilir.
                            //Hangi veritabanına kaydedeceğimizi söylüyoruz.
                            HashMap<String,Object> postData = new HashMap<>();
                            postData.put("useremail",userEmail);
                            postData.put("dowloadurl",dowloadUrl);
                            postData.put("comment",comment);
                            postData.put("date", FieldValue.serverTimestamp()); //güncel tarihi aldık.
                            firebaseFirestore.collection("Posts").add(postData).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {

                                @Override
                                public void onSuccess(DocumentReference documentReference) {

                                    Toast.makeText(UploadActivity.this,"Veritabanına Başarıyla Kaydedildi",Toast.LENGTH_LONG).show();

                                    Intent intent = new Intent(UploadActivity.this,FeedActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // Diğer Bütün Aktiviteleri Kapat demek.
                                    startActivity(intent);

                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {

                                    Toast.makeText(UploadActivity.this,e.getLocalizedMessage().toString(),Toast.LENGTH_LONG).show();
                                }
                            });



                        }
                    });




                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                    Toast.makeText(UploadActivity.this,e.getLocalizedMessage().toString(),Toast.LENGTH_LONG).show();
                }
            });
        }


    }

    public void resimsec(View view) {
        //Galeriye erişim izinleri //Kodlar hazır zaten ezber


        //Eğer izin yoksa
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,new String[] {Manifest.permission.READ_EXTERNAL_STORAGE},1);

        }else {
            Intent intentResim = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intentResim,2);

        }
    }

    //İstenilen izinlerin sonucu burada
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        //Yukarıda almak istediğimiz izinleri aldıysak eğer... Erişim sağlıyoruz
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults [0] == PackageManager.PERMISSION_GRANTED) {
                Intent intentResim = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intentResim,2);

            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }


    //galeriye erişim izni
    //Başlatılan activitenin sonucunda ne yapıcaz. İzini aldıktan sonra yani
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if (requestCode == 2 && resultCode == RESULT_OK && data != null) {

          resimData = data.getData();

            try {

                if (Build.VERSION.SDK_INT >= 28 ) {

                    ImageDecoder.Source source = ImageDecoder.createSource(this.getContentResolver(),resimData);
                    selectedImage = ImageDecoder.decodeBitmap(source);
                    imageView.setImageBitmap(selectedImage);

                }else {
                    selectedImage = MediaStore.Images.Media.getBitmap(getContentResolver(),resimData);
                    imageView.setImageBitmap(selectedImage);
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }
}