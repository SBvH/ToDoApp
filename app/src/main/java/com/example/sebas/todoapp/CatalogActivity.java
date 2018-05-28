/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.sebas.todoapp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.Toast;
import com.example.sebas.todoapp.catalogui.SortOrder;
import com.example.sebas.todoapp.catalogui.ToDoAdapter;
import com.example.sebas.todoapp.roomdb.AppDatabase;
import com.example.sebas.todoapp.roomdb.ToDo;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;



/**
 * Displays list of tasks that were entered and stored in the app.
 */
public class CatalogActivity extends AppCompatActivity {

    private Switch todaySwitch;
    private RecyclerView.Adapter adapter;

    private RelativeLayout emptyView;
    private SortOrder currentOrder = SortOrder.STATUS;
    long id = ApplicationState.getInstance().getUser().getId();
    private AppDatabase db;
    private List<ToDo> toDoList = new ArrayList<>();
    private ToDo toDo;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalog);
        Toast.makeText(this, "Hallo " + String.valueOf(ApplicationState.getInstance().getUser().getUsername()), Toast.LENGTH_SHORT).show();

        db = AppDatabase.getAppDatabase(this);

        this.toDoList = db.getToDoDao().getToDosForUser(id);

        emptyView = findViewById(R.id.empty_view);

        todaySwitch = findViewById(R.id.switch_day);
        todaySwitch.setOnClickListener(view -> updateRecyclerList(currentOrder)); // onClickListener statt changed, weil kürzer und wir brauchen den boolean nicht

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(view -> {
            Intent intent = new Intent(CatalogActivity.this, EditorActivity.class);
            startActivity(intent);
        });

        RecyclerView recycler = findViewById(R.id.recycler);
        recycler.setLayoutManager(new LinearLayoutManager(this));


        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                ToDoAdapter.ViewHolder test = (ToDoAdapter.ViewHolder) viewHolder;
                test.getToDo();
                long toDoId = test.getToDo().getId();
                db.getToDoDao().delete(toDoId);
                updateRecyclerList(currentOrder);
            }
        }).attachToRecyclerView(recycler);

        adapter = new ToDoAdapter(toDoList, this, new ToDoAdapter.ToDoAdapterCallback() {


            @Override
            public void onToggleStatus(ToDo toDo) {
                toDo.setDone(!toDo.isDone());
                db.getToDoDao().update(toDo);
                updateRecyclerList(currentOrder);
            }

            @Override
            public void onToggleFavorite(ToDo toDo) {
                toDo.setFavorite(!toDo.isFavorite());
                db.getToDoDao().update(toDo);
                updateRecyclerList(currentOrder);
            }

            @Override
            public void onEditClick(ToDo toDo) {
                Intent i = new Intent(getApplicationContext(), EditorActivity.class);
                i.putExtra("toDoId", toDo.getId());
                startActivity(i);
            }

            @Override
            public void onOpenLocation(ToDo toDo) {
                Intent geoIntent = new Intent(Intent.ACTION_VIEW,
                        Uri.parse(String.format("geo:0,0?q=%s",
                                URLEncoder.encode(toDo.getLocation()))));
                startActivity(geoIntent);
            }

            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onMailClick(ToDo toDo) {
                SimpleDateFormat simpleDate = new SimpleDateFormat("dd.MM.yyyy");
                SimpleDateFormat simpleTime = new SimpleDateFormat("HH:mm");


                Intent mailIntent = new Intent(Intent.ACTION_SEND);
                mailIntent.setType("text/plain");

                mailIntent.putExtra(Intent.EXTRA_SUBJECT, "Task");
                mailIntent.putExtra(Intent.EXTRA_TEXT   , "This is a task I want to share with You" + "\n" +"\n" +
                        "Name of the task: " + toDo.getTitle()+ "\n" +"\n" +
                        "Description: " + toDo.getDescription() + "\n" +"\n" +
                        "Date: " + simpleDate.format(toDo.getDueDate()) + "\n" + "\n" +
                        "Time: " + simpleTime.format(toDo.getDueDate()) + "\n" + "\n" +
                        "Location: " + toDo.getLocation()
                );

                try {
                    startActivity(Intent.createChooser(mailIntent, "Send mail..."));
                } catch (android.content.ActivityNotFoundException ex) {
                    Toast.makeText(CatalogActivity.this, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
                }
            }



            @Override
            public void onDeleteClick(ToDo toDo) {
                new AlertDialog.Builder(CatalogActivity.this)
                        .setMessage(R.string.delete_dialog_msg)
                        .setPositiveButton(R.string.delete, (dialog, id) -> {
                            // User clicked the "Delete" button, so delete the pet.
                            long toDoId = toDo.getId();
                            db.getToDoDao().delete(toDoId);
                            updateRecyclerList(currentOrder);
                        })
                        .setNegativeButton(R.string.cancel, (dialog, id) -> {
                            if (dialog != null) {
                                dialog.dismiss();
                            }
                        })
                        .show();
            }
        });
        recycler.setAdapter(adapter);
    }

        @Override
    public void onResume() {
        super.onResume();
        updateRecyclerList(currentOrder);
    }

    private void updateRecyclerList(SortOrder order) {
        this.currentOrder = order;
        long userId = ApplicationState.getInstance().getUser().getId();

        List<ToDo> list;

        switch (this.currentOrder) {
            case DATE:
                list = db.getToDoDao().getToDosForUserOrderByDate(userId);
                break;
            case STATUS:
                list = db.getToDoDao().getToDosForUserOrderByStatus(userId);
                break;
            case FAVORITE:
                list = db.getToDoDao().getToDosForUserOrderByFavorite(userId);
                break;
            default:
                list = new ArrayList<>();
                break;
        }

        toDoList.clear();

        for (ToDo toDo : list) {
            if (!todaySwitch.isChecked() || DateUtils.isToday(toDo.getDueDate().getTime())) {
                toDoList.add(toDo);
            }
        }

        adapter.notifyDataSetChanged();

        emptyView.setVisibility(toDoList.size() == 0 ? View.VISIBLE : View.GONE);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_catalog.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_catalog, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            case R.id.export_all_tasks:
                this.export();
                return true;
            case R.id.action_sort_status:
                updateRecyclerList(SortOrder.STATUS);
                return true;
            case R.id.action_sort_date:
                updateRecyclerList(SortOrder.DATE);
                return true;
            case R.id.action_sort_name:
                updateRecyclerList(SortOrder.FAVORITE);
                return true;
            case R.id.action_delete_all_entries:
                db.getToDoDao().deleteAll();
                updateRecyclerList(currentOrder);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void export() {

        try {
            String OutputString = "";
            SimpleDateFormat simpleDate = new SimpleDateFormat("dd.MM.yyyy");
            this.toDoList = db.getToDoDao().getToDosForUser(id);
            //Code in case the data should be saved at Documents-folder
            /*
            File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);
            if (!path.exists()) {
                path.mkdirs(); TEst
            }
            File file = new File (path,"Taskexport.csv");
            file.getAbsolutePath();
            file.createNewFile();
            FileOutputStream stream = new FileOutputStream(file);
            OutputStreamWriter outputstream = new OutputStreamWriter(stream); */

            FileOutputStream fOut = openFileOutput("Taskexport.csv", Context.MODE_PRIVATE);
            OutputStreamWriter outputStream = new OutputStreamWriter(fOut);

            OutputString += "Title;Description;Status;Favorite;Location;Date\n";

            for (ToDo toDo : toDoList) {

                if (toDo.getTitle() != null)
                    OutputString += toDo.getTitle() + ";";
                else
                    OutputString += ";";

                if (toDo.getDescription() != null)
                    OutputString += toDo.getDescription() + ";";
                else
                    OutputString += ";";

                OutputString += toDo.isDone() + ";" + toDo.isFavorite() + ";";

                if (toDo.getLocation() != null)
                    OutputString += toDo.getLocation() + ";";
                else
                    OutputString += ";";


                if (toDo.getDueDate() == null) {
                    OutputString += "\n";
                } else {
                    OutputString += simpleDate.format(toDo.getDueDate().getTime()) + "\n";
                }
            }
            outputStream.write(OutputString);
            outputStream.flush();
            outputStream.close();
            Toast.makeText(getApplicationContext(), "Tasks have been exported!", Toast.LENGTH_LONG).show();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        // Refresh main activity
        Intent refresh = new Intent(this, CatalogActivity.class);
        startActivity(refresh);
        this.finish();
    }
}
