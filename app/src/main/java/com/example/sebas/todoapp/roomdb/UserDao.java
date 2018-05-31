package com.example.sebas.todoapp.roomdb;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;
//Data Access Object for operations with user-objects
@Dao
public interface UserDao {

	@Insert
	long insert(User user);

	@Delete
	void delete(User user);

	@Update
	void update(User user);

	@Query("SELECT * FROM users WHERE id = :id")
	User getUserById(long id);

	@Query("SELECT * FROM users WHERE username = :username")
	User getUserByEmail(String username);


}
