package com.example.sebas.todoapp.roomdb;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import java.util.Date;

@Entity(tableName = "to_dos")
public class ToDo {

	@PrimaryKey(autoGenerate = true)
	@ColumnInfo(name = "id")
	private long id;

	@ColumnInfo(name = "title")
	private String title;

	@ColumnInfo(name = "description")
	private String description;

	@ColumnInfo(name = "location")
	private String location;

	@ColumnInfo(name = "due_date")
	private Date dueDate;

	@ColumnInfo(name = "done")
	private boolean done;

	@ColumnInfo(name = "favorite")
	private boolean favorite;

	@ColumnInfo(name = "user_id")
	private long userId;

	public ToDo(long id, String title, String description, Date dueDate, String location, boolean done, boolean favorite, long userId) {
		this.id = id;
		this.title = title;
		this.description = description;
		this.dueDate = dueDate;
		this.location = location;
		this.done = done;
		this.favorite = favorite;
		this.userId = userId;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Date getDueDate() {
		return dueDate;
	}

	public void setDueDate(Date dueDate) {
		this.dueDate = dueDate;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public boolean isDone() {
		return done;
	}

	public void setDone(boolean done) {
		this.done = done;
	}

	public boolean isFavorite() {
		return favorite;
	}

	public void setFavorite(boolean favorite) {
		this.favorite = favorite;
	}

	public long getUserId() {
		return userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}

	@Override
	public String toString() {
		return "ToDo[id=" + id + ",title=" + title + ",date="+dueDate.toString()+"]";
	}
}
