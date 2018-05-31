package com.example.sebas.todoapp.catalogui;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.dropbox.client2.exception.DropboxException;
import com.example.sebas.todoapp.EditorActivity;
import com.example.sebas.todoapp.R;
import com.example.sebas.todoapp.roomdb.AppDatabase;
import com.example.sebas.todoapp.roomdb.ToDo;

import java.io.IOException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;


public class ToDoAdapter extends RecyclerView.Adapter<ToDoAdapter.ViewHolder> {
	//Interface for the callback methods.
	public interface ToDoAdapterCallback {
		void onToggleStatus(ToDo toDo);
		void onToggleFavorite(ToDo toDo);
		void onEditClick(ToDo toDo);
		void onOpenLocation(ToDo toDo);
		void onMailClick(ToDo toDo);
		void onDeleteClick(ToDo toDo);
	}

	private final Context context;
	private final List<ToDo> toDoList;

	private final ToDoAdapterCallback callback;


	public ToDoAdapter(List<ToDo> toDoList, Context context, ToDoAdapterCallback callback) {
		this.toDoList = toDoList;
		this.context = context;
		this.callback = callback;
		setHasStableIds(false);
	}


	//Adapter for rendering the UI with the cardView.
	@NonNull
	@Override
	public ToDoAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		CardView v = (CardView) LayoutInflater.from(parent.getContext())
				.inflate(R.layout.list_item, parent, false);

		return new ViewHolder(v);
	}
	//onBindViewHolder gets values form the to-do-Object and set UI, onClickListeners for UI Events
	@Override
	public void onBindViewHolder(@NonNull ToDoAdapter.ViewHolder holder, int position) {
		ToDo toDo = toDoList.get(position);
		holder.toDo = toDo;

		holder.name.setText(toDo.getTitle());
		holder.description.setText(toDo.getDescription());
		holder.favorite.setImageResource(toDo.isFavorite() ? R.drawable.ic_star_orange_24dp : R.drawable.baseline_star_border_black_24);
		holder.status.setChecked(toDo.isDone());
		holder.location.setText(toDo.getLocation());

		SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy", Locale.GERMANY);
		String day = sdf.format(toDo.getDueDate());
		sdf.applyPattern("HH:mm");
		String time = sdf.format(toDo.getDueDate());
		holder.dueDate.setText(context.getString(R.string.due_date, day, time));

		// on clicks
		holder.editPencil.setOnClickListener(view -> callback.onEditClick(toDo));
		holder.status.setOnClickListener(view -> callback.onToggleStatus(toDo));
		holder.favorite.setOnClickListener(view -> callback.onToggleFavorite(toDo));
		holder.maps.setOnClickListener(view -> callback.onOpenLocation(toDo));
		holder.location.setOnClickListener(view -> callback.onOpenLocation(toDo));
		holder.mail.setOnClickListener(view -> callback.onMailClick(toDo));
		holder.delete.setOnClickListener(view -> callback.onDeleteClick(toDo));
	}

	@Override
	public int getItemCount() {
		return toDoList.size();
	}

	//Elements of the RecyclerView
	public class ViewHolder extends RecyclerView.ViewHolder {
		TextView name, dueDate, description, location;
		CheckBox status;
		ImageView favorite, maps, share, mail, editPencil, delete;
		ToDo toDo;

		public ToDo getToDo() {
			return toDo;
		}

		public ViewHolder(View view) {
			super(view);
			name = view.findViewById(R.id.name);
			status = view.findViewById(R.id.status_checkbox);
			dueDate = view.findViewById(R.id.due_date);
			description = view.findViewById(R.id.description);
			favorite = view.findViewById(R.id.favorite);
			maps = view.findViewById(R.id.maps);
			location = view.findViewById(R.id.location);
			editPencil = view.findViewById(R.id.edit_pencil);
			mail = view.findViewById(R.id.mail_button);
			delete = view.findViewById(R.id.delete_button);

		}
	}

}
