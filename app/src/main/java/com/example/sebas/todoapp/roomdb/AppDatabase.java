package com.example.sebas.todoapp.roomdb;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;
import android.content.Context;

@Database(entities = {ToDo.class, User.class}, version = 1)
@TypeConverters({Converters.class})
public abstract class AppDatabase extends RoomDatabase {

	public abstract ToDoDao getToDoDao();
	public abstract UserDao getUserDao();

	private static AppDatabase INSTANCE;

	public static AppDatabase getAppDatabase(Context context) {
		if (INSTANCE == null) {
			INSTANCE =
					Room.databaseBuilder(context.getApplicationContext(), AppDatabase.class, "user-database")
							.allowMainThreadQueries()
							.build();
		}
		return INSTANCE;
	}

	public static void destroyInstance() {
		INSTANCE = null;
	}

}
