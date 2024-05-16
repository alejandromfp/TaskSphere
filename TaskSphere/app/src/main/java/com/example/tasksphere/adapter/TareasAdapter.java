package com.example.tasksphere.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.tasksphere.R;
import com.example.tasksphere.TaskDetaills;
import com.example.tasksphere.modelo.entidad.Task;
import java.util.List;

public class TareasAdapter extends RecyclerView.Adapter<TareasAdapter.TareaViewHolder> {
    private List<Task> tasksList;
    private Context context;

    public TareasAdapter(Context context, List<Task> tasksList) {
        this.context = context;
        this.tasksList = tasksList;
    }

    @NonNull
    @Override
    public TareaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_tasks, parent, false);
        return new TareaViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull TareaViewHolder holder, int position) {
        Task tarea = tasksList.get(position);
        holder.nombreTextView.setText(tarea.getTaskName());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, TaskDetaills.class);
                intent.putExtra("task", tarea);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return tasksList.size();
    }

    public class TareaViewHolder extends RecyclerView.ViewHolder {
        TextView nombreTextView;
        private Task tarea;
        public TareaViewHolder(@NonNull View itemView) {
            super(itemView);
            nombreTextView = itemView.findViewById(R.id.task_name);
        }
    }




}
