package com.kartal.instaclonefirebase;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Users extends AppCompatActivity {

    // Kullanıcı kayıt işlemleri için "authentication" işlemlerini yapabilmek için. Private yaptık çünkü sadece bu sayfada kullanıyoruz.
    private FirebaseAuth firebaseAuth;
    EditText emailText , passwordText ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users);
        //Artık bunu kullanarak istediğimiz işlemi yapabiliriz. Bu hazır bir koddur. Sitede zaten nasıl kullanılacağı gösteriliyor.
        firebaseAuth = FirebaseAuth.getInstance();
        emailText = findViewById(R.id.emailText);
        passwordText = findViewById(R.id.passwordText);

        //Sürekli giriş ekranı çıkmaması için yani kullanıcıdan her zaman mail,şifre almamak için bu işlemleri yapıyoruz.

        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

        if (firebaseUser != null) {
            Intent intent = new Intent(Users.this,FeedActivity.class);
            startActivity(intent);
            finish(); //geri tuşuna bastığımızda giriş ekranına dönmesin diye
        }


    }

    public void giris(View view) {

        String email = emailText.getText().toString();
        String password = passwordText.getText().toString();


        firebaseAuth.signInWithEmailAndPassword(email,password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {

                Toast.makeText(Users.this,"GİRİŞ BAŞARILI",Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(Users.this,FeedActivity.class);
                startActivity(intent);
                finish();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                Toast.makeText(Users.this,e.getLocalizedMessage().toString(),Toast.LENGTH_LONG).show();
            }
        });

    }

    public void kayit(View view) {

        String email = emailText.getText().toString();
        String password = passwordText.getText().toString();


        //Kullanıcı kaydı başarılı ve başarısız oldu demek için bu işlemleri yapıyoruz.Yapmak istemeyebilirsin.Daha kullanışlı olması için yapıyoruz.
        //Ayrıca hata mesajlarını "Firebase" kendisi veriyor.
        firebaseAuth.createUserWithEmailAndPassword(email,password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {

                Toast.makeText(Users.this,"Kullanıcı Başarıyla Oluşturuldu",Toast.LENGTH_LONG).show();

                Intent intent = new Intent(Users.this,FeedActivity.class);
                startActivity(intent);
                finish();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                //Firebase kendisi hata mesajını vericek. O yüzden "e.getLocalizedMessage" yaptık.
                Toast.makeText(Users.this,e.getLocalizedMessage().toString(),Toast.LENGTH_LONG).show();
            }
        });

    }
}