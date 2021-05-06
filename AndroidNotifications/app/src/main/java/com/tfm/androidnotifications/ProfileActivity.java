package com.tfm.androidnotifications;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class ProfileActivity extends AppCompatActivity {
    public static final String NODE_USERS = "users";
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mAuth = FirebaseAuth.getInstance();

        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (task.isSuccessful()) {
                            String token = task.getResult().getToken();
                            saveToken(token);
                        } else {

                        }
                    }
                });
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (mAuth.getCurrentUser() == null) {

            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
    }

    private void saveToken(String token) {
        String email = mAuth.getCurrentUser().getEmail();
        User user = new User(email, token);
        final boolean[] existeToken = {false};

        DatabaseReference dbUsers = FirebaseDatabase.getInstance().getReference(NODE_USERS);

        DatabaseReference reff = FirebaseDatabase.getInstance().getReference().child("users").child("quantity");

        reff.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task1) {

                DatabaseReference dbUsers = FirebaseDatabase.getInstance().getReference(NODE_USERS);

                dbUsers.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DataSnapshot> task2) {
                        
                        HashMap<String, HashMap<String, String>> respuestaFirebase = (HashMap<String, HashMap<String, String>>) task2.getResult().getValue();

                        Collection<HashMap<String, String>> respuestaCollection = respuestaFirebase.values();

                        ArrayList<HashMap<String, String>> respuestaLista = new ArrayList<>(respuestaCollection);

                        for (int i = 0; i < respuestaLista.size()-1; i++) {
                            if(respuestaLista.get(i).get("token").equals(user.token)){
                                existeToken[0] = true;
                                break;
                            }
                        }

                        if (!existeToken[0]) {
                            long quantity = (long) task1.getResult().getValue();
                            long quantityFinal = quantity + 1;
                            reff.setValue(quantityFinal);
                            dbUsers.child(String.valueOf(quantityFinal))
                                    .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task3) {
                                    if (task3.isSuccessful()) {
                                        Toast.makeText(ProfileActivity.this, "Token Saved", Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                        }
                    }
                });


            }

        });


    }

    private long getQuantity() {
        final long[] res = {0};
        DatabaseReference reff = FirebaseDatabase.getInstance().getReference().child("users").child("quantity");

        int asd = 0;
        final boolean[] completado = {false};


        reff.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {

                res[0] = (long) task.getResult().getValue();
                completado[0] = task.isComplete();
            }

        });
        while (!completado[0]) {

        }
        return res[0];
    }

}