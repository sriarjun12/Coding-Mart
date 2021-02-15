package com.abort.employeetimesheet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.abort.employeetimesheet.Common.Common;
import com.abort.employeetimesheet.Model.EmployeeModel;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Arrays;
import java.util.List;

import dmax.dialog.SpotsDialog;

public class MainActivity extends AppCompatActivity {
    private static int APP_REQUEST_CODE=7171;
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener listener;

    private DatabaseReference userRef;


    private StorageReference storageReference;
    private List<AuthUI.IdpConfig> providers;

    android.app.AlertDialog dialog;

    @Override
    protected void onStart() {
        super.onStart();
        firebaseAuth.addAuthStateListener(listener);
    }
    @Override
    protected void onStop() {
        if(listener!=null)
            firebaseAuth.removeAuthStateListener(listener);
        super.onStop();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
    }
    private void init() {

        dialog=new SpotsDialog.Builder().setContext(this).setCancelable(false).build();
        providers = Arrays.asList(new AuthUI.IdpConfig.EmailBuilder().build());
        userRef= FirebaseDatabase.getInstance().getReference(Common.EMPLOYEE_REF);
        firebaseAuth=FirebaseAuth.getInstance();
        listener=firebaseAuthLocal ->{
            FirebaseUser user=firebaseAuthLocal.getCurrentUser();
            if(user !=null){
                checkUserFromFirebase(user);
            }
            else{
                PhoneLogin();
            }
        };
    }
    private void checkUserFromFirebase(FirebaseUser user){
        dialog.show();
        userRef.child(user.getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()){
                            EmployeeModel employeeModel = snapshot.getValue(EmployeeModel.class);
                            goToHomeActivity(employeeModel);
                        }
                        else{
                            dialog.dismiss();
                            storetoFirebase(user);
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        dialog.dismiss();
                        Toast.makeText(MainActivity.this, ""+error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void storetoFirebase(FirebaseUser user) {
        EmployeeModel employeeModel=new EmployeeModel();
        employeeModel.setEmail(user.getEmail());
        employeeModel.setName(user.getDisplayName());
        employeeModel.setUid(user.getUid());

        FirebaseDatabase.getInstance().getReference(Common.EMPLOYEE_REF).child(user.getUid())
                .setValue(employeeModel)
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                }).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
            }
        });
    }

    private void goToHomeActivity(EmployeeModel employeeModel) {
        dialog.dismiss();
        Common.currentEmployeeModel = employeeModel;
        Intent intent = new Intent(this,HomeActivity.class);
        startActivity(intent);
        finish();
    }
    private void PhoneLogin() {
        startActivityForResult(AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(providers)
                .setLogo(R.drawable.file)
                .setTheme(R.style.LoginTheme)
                .build(),APP_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == APP_REQUEST_CODE) {
            IdpResponse response = IdpResponse.fromResultIntent(data);
            if (resultCode == RESULT_OK) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                EmployeeModel employeeModel=new EmployeeModel();
                employeeModel.setEmail(user.getEmail());
                employeeModel.setName(user.getDisplayName());
                employeeModel.setUid(user.getUid());
                goToHomeActivity(employeeModel);
            }
        }
        else
        {
            Toast.makeText(this, "Failed to sign in", Toast.LENGTH_SHORT).show();
        }
    }}