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
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;

import com.example.sebas.todoapp.roomdb.AppDatabase;
import com.example.sebas.todoapp.roomdb.ToDo;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Allows user to create a new pet or edit an existing one.
 */
public class EditorActivity extends AppCompatActivity {

    private EditText mNameEditText;
    private EditText mDescriptionEditText;
    private EditText mLocationEditText;
    private EditText mDateEditText;
    private EditText mTimeEditText;
	private CheckBox mFavorite;


    private AppDatabase db;
    private long toDoId = 0;

    private final Calendar dueDate = Calendar.getInstance();

    private boolean isNewTask() {
    	return toDoId == 0;
	}

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        db = AppDatabase.getAppDatabase(this);

		mNameEditText = findViewById(R.id.edit_pet_name);
		mDescriptionEditText = findViewById(R.id.edit_pet_description);
		mLocationEditText = findViewById(R.id.edit_location);
		mFavorite = findViewById(R.id.favbutton);
		mDateEditText = findViewById(R.id.edit_date);
		mTimeEditText = findViewById(R.id.edit_time);

        toDoId = getIntent().getLongExtra("toDoId", 0);

        if (isNewTask()) {
            setTitle(getString(R.string.editor_activity_title_new_task));
            invalidateOptionsMenu();
        } else {
            setTitle(getString(R.string.editor_activity_title_edit_task));
            populateViews(db.getToDoDao().getTodoById(toDoId));
        }


        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                dueDate.set(year, monthOfYear, dayOfMonth);
                updateLabel();
            }

            private void updateLabel() {
                String myFormat = "dd.MM.yyyy"; //In which you need put here
                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
                mDateEditText.setText(sdf.format(dueDate.getTime()));
            }
        };

        mDateEditText.setOnClickListener(v -> new DatePickerDialog(EditorActivity.this,
				date,
				dueDate.get(Calendar.YEAR),
				dueDate.get(Calendar.MONTH),
				dueDate.get(Calendar.DAY_OF_MONTH)).show());


        mTimeEditText.setOnClickListener(v -> {
			Calendar mCurrentTime = Calendar.getInstance();
			int hour = mCurrentTime.get(Calendar.HOUR_OF_DAY);
			int minute = mCurrentTime.get(Calendar.MINUTE);

			TimePickerDialog mTimePicker;
			mTimePicker = new TimePickerDialog(EditorActivity.this,
					(timePicker, selectedHour, selectedMinute) -> {
						dueDate.set(Calendar.HOUR_OF_DAY, selectedHour);
						dueDate.set(Calendar.MINUTE, selectedMinute);
						mTimeEditText.setText(String.format("%02d:%02d", selectedHour, selectedMinute));
					},
					hour,
					minute,
					true);
			mTimePicker.setTitle("Select Time");
			mTimePicker.show();
		});
        
    }

    private void populateViews(ToDo toDo) {
    	this.dueDate.setTime(toDo.getDueDate());

		mNameEditText.setText(toDo.getTitle());
		mDescriptionEditText.setText(toDo.getDescription());
		mLocationEditText.setText(toDo.getLocation());
		mFavorite.setChecked(toDo.isFavorite());

		SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy", Locale.US);
		mDateEditText.setText(sdf.format(toDo.getDueDate()));
		sdf.applyPattern("HH:mm");
		mTimeEditText.setText(sdf.format(toDo.getDueDate()));
	}


    private void saveTodo() {
        long userId = ApplicationState.getInstance().getUser().getId();
        String title = mNameEditText.getText().toString().trim();
        String description = mDescriptionEditText.getText().toString().trim();
        String location = mLocationEditText.getText().toString().trim();
        boolean favorite = mFavorite.isChecked();
        Date due = this.dueDate.getTime();

        ToDo toDo = new ToDo(toDoId,
        		title,
                description,
                due,
                location,
                false,
                favorite,
                userId);

        if(isNewTask()) {
			db.getToDoDao().insert(toDo);
		} else {
        	db.getToDoDao().update(toDo);
		}


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_editor.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    /**
     * This method is called after invalidateOptionsMenu(), so that the
     * menu can be updated (some menu items can be hidden or made visible).
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        // If this is a new pet, hide the "Delete" menu item.
        if (isNewTask()) {
            MenuItem menuItem = menu.findItem(R.id.action_delete);
            menuItem.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Save" menu option
            case R.id.action_save:
                //Save the task
				saveTodo();
                // Exit activity
                finish();
                return true;
            // Respond to a click on the "Delete" menu option
            case R.id.action_delete:
                // Pop up confirmation dialog for deletion
                showDeleteConfirmationDialog();
                return true;

        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Prompt the user to confirm that they want to delete this task.
     */
    private void showDeleteConfirmationDialog() {
		new AlertDialog.Builder(this)
				.setMessage(R.string.delete_dialog_msg)
				.setPositiveButton(R.string.delete, (dialog, id) -> {
					// User clicked the "Delete" button, so delete the pet.
					db.getToDoDao().delete(toDoId);
				})
				.setNegativeButton(R.string.cancel, (dialog, id) -> {
					if (dialog != null) {
						dialog.dismiss();
					}
				})
				.show();

    }

}