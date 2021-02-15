package com.abort.employeetimesheet;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.abort.employeetimesheet.Common.Common;
import com.abort.employeetimesheet.Model.EmployDetail;
import com.abort.employeetimesheet.Model.TimeSheetModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import dmax.dialog.SpotsDialog;

public class HomeActivity extends AppCompatActivity {

    private TableLayout tableLayout;

    android.app.AlertDialog dialog;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuItem item2 = menu.add(R.string.menu_view_material);
        item2.setShowAsAction(MenuItem.SHOW_AS_ACTION_WITH_TEXT|MenuItem.SHOW_AS_ACTION_IF_ROOM);
        item2.setTitle(R.string.menu_view_material);
        MenuItem item = menu.add(R.string.menu_add);
        item.setShowAsAction(MenuItem.SHOW_AS_ACTION_WITH_TEXT|MenuItem.SHOW_AS_ACTION_IF_ROOM);
        item.setIcon(R.drawable.ic_baseline_add_24);
        item.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                showAddEmployee();
                return true;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }
    private void showAddEmployee() {
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(new ContextThemeWrapper(this,R.style.AlertDialogCustom));
        builder.setTitle("Add Employee");
//        builder.setIcon(R.drawable.logo_explorer);
        builder.setCancelable(false);
        View itemView = LayoutInflater.from(this).inflate(R.layout.layoutadd, null);
        EditText edt_name = (EditText) itemView.findViewById(R.id.name_emp);
        EditText edt_email = (EditText) itemView.findViewById(R.id.email_emp);
        EditText edt_id = (EditText) itemView.findViewById(R.id.id_emp);
        EditText edt_designation = (EditText) itemView.findViewById(R.id.designation_emp);
        EditText edt_mobile = (EditText) itemView.findViewById(R.id.mobile_emp);
        builder.setNegativeButton("CANCEL",(dialogInterface, i) -> {
            dialogInterface.dismiss();
        });
        builder.setPositiveButton("ADD", (dialogInterface, i) -> {
            dialog.dismiss();
            if (TextUtils.isEmpty(edt_name.getText().toString())) {
                Toast.makeText(this, "enter Name !", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
                return;
            } else if (TextUtils.isEmpty(edt_email.getText().toString())) {
                Toast.makeText(this, "enter Email", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
                return;
            }else if (TextUtils.isEmpty(edt_designation.getText().toString())) {
                Toast.makeText(this, "enter Designation", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
                return;
            }
            else if (TextUtils.isEmpty(edt_id.getText().toString())) {
                Toast.makeText(this, "enter ID", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
                return;
            }
            else if (TextUtils.isEmpty(edt_mobile.getText().toString())) {
                Toast.makeText(this, "enter Mobile ", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
                return;
            }
            EmployDetail employDetail = new EmployDetail();
            employDetail.setId(edt_id.getText().toString());
            employDetail.setName(edt_name.getText().toString());
            employDetail.setEmail(edt_email.getText().toString());
            employDetail.setMobile(edt_mobile.getText().toString());
            employDetail.setDesignation(edt_designation.getText().toString());
            FirebaseDatabase.getInstance().getReference(Common.EMPLOYEE_DETAILS).child(employDetail.getId())
                    .setValue(employDetail)
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
//                                dialog.dismiss();
                            dialog.dismiss();
                            Toast.makeText(HomeActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    dialog.dismiss();
                    HomeActivity.super.recreate();
                }
            });
        });
        builder.setView(itemView);
        androidx.appcompat.app.AlertDialog registerDialog = builder.create();
        registerDialog.setCanceledOnTouchOutside(false);
        registerDialog.setCancelable(false);
        registerDialog.show();
    }
    int i ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        tableLayout=(TableLayout)findViewById(R.id.employee_table);
        dialog=new SpotsDialog.Builder().setContext(this).setCancelable(false).build();

        List<EmployDetail> tempList = new ArrayList<>();
        DatabaseReference categoryRef = FirebaseDatabase.getInstance()
                .getReference(Common.EMPLOYEE_DETAILS);

        categoryRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot itemSnapShot:dataSnapshot.getChildren())
                {
                    EmployDetail categoryModel = itemSnapShot.getValue(EmployDetail.class);
                    tempList.add(categoryModel);
                }
                for (i=0;i<tempList.size();i++){
                    View tableRow = LayoutInflater.from(HomeActivity.this).inflate(R.layout.table_items,null,false);

                    TextView employee_id = (TextView) tableRow.findViewById(R.id.employee_id);
                    TextView employee_name  = (TextView) tableRow.findViewById(R.id.employee_name);
                    TextView employee_mobile  = (TextView) tableRow.findViewById(R.id.employee_mobile);
                    TextView employee_email  = (TextView) tableRow.findViewById(R.id.employee_email);
                    TextView employee_designation  = (TextView) tableRow.findViewById(R.id.employee_designation);
                    employee_id.setText(tempList.get(i).getId());
                    employee_name.setText(tempList.get(i).getName());
                    employee_mobile.setText(tempList.get(i).getMobile());
                    employee_email.setText(tempList.get(i).getEmail());
                    employee_designation.setText(tempList.get(i).getDesignation());
                    tableLayout.addView(tableRow);
                    tableRow.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            showEmployeeOptions(employee_name.getText().toString(),employee_id.getText().toString(),employee_email.getText().toString(),employee_mobile.getText().toString(),employee_designation.getText().toString());
                            //Toast.makeText(HomeActivity.this, ""+employee_name.getText(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void showEmployeeOptions(String name,String id,String email,String mobile,String deisgnation) {
        EmployDetail employDetail=new EmployDetail();
        employDetail.setId(id);
        employDetail.setEmail(email);
        employDetail.setMobile(mobile);
        employDetail.setName(name);
        employDetail.setDesignation(deisgnation);
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(new ContextThemeWrapper(this,R.style.AlertDialogCustom));
        builder.setTitle("Selected Employee - "+employDetail.getName());
//        builder.setIcon(R.drawable.logo_explorer);
        builder.setCancelable(true);
        View itemView = LayoutInflater.from(this).inflate(R.layout.employee_options, null);
        Button add_time = (Button) itemView.findViewById(R.id.add_time_sheet);
        Button view_time = (Button) itemView.findViewById(R.id.view_time_sheet);
        Button edit_details = (Button) itemView.findViewById(R.id.edit_details);
        Button delte_details = (Button) itemView.findViewById(R.id.delete);
        delte_details.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.show();
                FirebaseDatabase.getInstance().getReference(Common.EMPLOYEE_DETAILS)
                        .child(employDetail.getId())
                        .removeValue()
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                dialog.dismiss();
                                Toast.makeText(HomeActivity.this, "Success !", Toast.LENGTH_SHORT).show();
                                HomeActivity.super.recreate();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        dialog.dismiss();
                        Toast.makeText(HomeActivity.this, "Error !", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
        edit_details.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showEditOption(employDetail);
            }
        });
        add_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimesheet(employDetail);
            }
        });
        view_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ViewTimesheet(employDetail);
            }
        });
        builder.setView(itemView);
        androidx.appcompat.app.AlertDialog registerDialog = builder.create();
        registerDialog.show();
    }
    private void ViewTimesheet(EmployDetail employDetail) {
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(new ContextThemeWrapper(this,R.style.AlertDialogCustom));

        FirebaseDatabase.getInstance().getReference(Common.EMPLOYEE_DETAILS)
                .child(employDetail.getId())
                .child(Common.TIME_SHEET)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        TimeSheetModel timeSheetModel=snapshot.getValue(TimeSheetModel.class);
                        builder.setTitle(timeSheetModel.getTask());
                        builder.setMessage(timeSheetModel.getDiscription()+" on "+timeSheetModel.getDate()+" hours of work is "+timeSheetModel.getHours());
                        androidx.appcompat.app.AlertDialog registerDialog = builder.create();
                        registerDialog.show();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


    }

    private void showTimesheet(EmployDetail employDetail) {
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(new ContextThemeWrapper(this,R.style.AlertDialogCustom));
        builder.setTitle("Time Sheet Updation");
//        builder.setIcon(R.drawable.logo_explorer);
        builder.setCancelable(true);
        View itemView = LayoutInflater.from(this).inflate(R.layout.time_sheet_layout, null);
        EditText edt_task=(EditText)itemView.findViewById(R.id.task);
        EditText edt_discription=(EditText)itemView.findViewById(R.id.discription);
        EditText edt_hour=(EditText)itemView.findViewById(R.id.hours);
        DatePicker datePicker=(DatePicker)itemView.findViewById(R.id.datePicker);
        builder.setNegativeButton("CANCEL",(dialogInterface, i) -> {
            dialogInterface.dismiss();
        });
        builder.setPositiveButton("ADD", (dialogInterface, i) -> {
            if (TextUtils.isEmpty(edt_task.getText().toString())) {
                Toast.makeText(this, "enter Task Name !", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
                return;
            } else if (TextUtils.isEmpty(edt_discription.getText().toString())) {
                Toast.makeText(this, "enter Discription", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
                return;
            } else if (TextUtils.isEmpty(edt_hour.getText().toString())) {
                Toast.makeText(this, "enter Hours", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
                return;
            }
            int day = datePicker.getDayOfMonth();
            int month = datePicker.getMonth() + 1;
            int year = datePicker.getYear();

            TimeSheetModel timeSheetModel = new TimeSheetModel();
            timeSheetModel.setTask(edt_task.getText().toString());
            timeSheetModel.setDiscription(edt_discription.getText().toString());
            timeSheetModel.setHours(edt_hour.getText().toString());
            timeSheetModel.setDate(day + "-" + month + "-" + year);
            FirebaseDatabase.getInstance().getReference(Common.EMPLOYEE_DETAILS)
                    .child(employDetail.getId())
                    .child(Common.TIME_SHEET)
                    .setValue(timeSheetModel)
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                        }
                    })
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            //Sheet Show
                        }
                    });
        });
        builder.setView(itemView);
        androidx.appcompat.app.AlertDialog registerDialog = builder.create();
        registerDialog.show();

    }

    private void showEditOption(EmployDetail employDetail) {
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(new ContextThemeWrapper(this,R.style.AlertDialogCustom));
        builder.setTitle("Update Details");
//        builder.setIcon(R.drawable.logo_explorer);
        builder.setCancelable(false);
        View itemView = LayoutInflater.from(this).inflate(R.layout.layoutadd, null);
        EditText edt_name = (EditText) itemView.findViewById(R.id.name_emp);
        EditText edt_email = (EditText) itemView.findViewById(R.id.email_emp);
        EditText edt_id = (EditText) itemView.findViewById(R.id.id_emp);
        EditText edt_designation = (EditText) itemView.findViewById(R.id.designation_emp);
        EditText edt_mobile = (EditText) itemView.findViewById(R.id.mobile_emp);
        edt_id.setEnabled(false);
        edt_id.setText(employDetail.getId());
        edt_name.setText(employDetail.getName());
        edt_mobile.setText(employDetail.getMobile());
        edt_designation.setText(employDetail.getDesignation());
        edt_email.setText(employDetail.getEmail());
        builder.setNegativeButton("CANCEL",(dialogInterface, i) -> {
            dialogInterface.dismiss();
        });
        builder.setPositiveButton("UPDATE", (dialogInterface, i) -> {
            dialog.dismiss();
            if (TextUtils.isEmpty(edt_name.getText().toString())) {
                Toast.makeText(this, "enter Name !", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
                return;
            } else if (TextUtils.isEmpty(edt_email.getText().toString())) {
                Toast.makeText(this, "enter Email", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
                return;
            }else if (TextUtils.isEmpty(edt_designation.getText().toString())) {
                Toast.makeText(this, "enter Designation", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
                return;
            }
            else if (TextUtils.isEmpty(edt_id.getText().toString())) {
                Toast.makeText(this, "enter ID", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
                return;
            }
            else if (TextUtils.isEmpty(edt_mobile.getText().toString())) {
                Toast.makeText(this, "enter Mobile ", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
                return;
            }
            EmployDetail employDetaill = new EmployDetail();
            employDetaill.setId(edt_id.getText().toString());
            employDetaill.setName(edt_name.getText().toString());
            employDetaill.setEmail(edt_email.getText().toString());
            employDetaill.setMobile(edt_mobile.getText().toString());
            employDetaill.setDesignation(edt_designation.getText().toString());
            FirebaseDatabase.getInstance().getReference(Common.EMPLOYEE_DETAILS).child(employDetaill.getId())
                    .setValue(employDetaill)
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
//                                dialog.dismiss();
                            dialog.dismiss();
                            Toast.makeText(HomeActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    dialog.dismiss();
                    HomeActivity.super.recreate();
                }
            });
        });
        builder.setView(itemView);
        androidx.appcompat.app.AlertDialog registerDialog = builder.create();
        registerDialog.setCanceledOnTouchOutside(false);
        registerDialog.setCancelable(false);
        registerDialog.show();
    }
}