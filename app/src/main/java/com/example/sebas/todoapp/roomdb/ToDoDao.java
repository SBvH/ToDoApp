package com.example.sebas.todoapp.roomdb;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;
//Data Access Object for operations with to-do-objects
@Dao
public interface ToDoDao {

	@Insert
	long insert(ToDo toDo);

	@Delete
	void delete(ToDo toDo);

	@Query("DELETE FROM TO_DOS WHERE id = :id")
	void delete(long id);

	@Query("DELETE FROM to_dos")
	void deleteAll();

	@Update
	void update(ToDo toDo);

	@Query("SELECT * FROM to_dos WHERE user_id = :user_id")
	List<ToDo> getToDosForUser(long user_id);


	@Query("SELECT * FROM to_dos WHERE user_id = :user_id ORDER BY done DESC, due_date ASC, title ASC")
	List<ToDo> getToDosForUserOrderByStatus(long user_id);

	@Query("SELECT * FROM to_dos WHERE user_id = :user_id ORDER BY due_date ASC, title ASC")
	List<ToDo> getToDosForUserOrderByDate(long user_id);

	@Query("SELECT * FROM to_dos WHERE user_id = :user_id ORDER BY favorite DESC, due_date ASC, title ASC")
	List<ToDo> getToDosForUserOrderByFavorite(long user_id);



	@Query("SELECT * FROM to_dos WHERE id = :id")
	ToDo getTodoById(long id);


}
