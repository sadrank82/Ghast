package ir.khu.gasht.adapters;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;

import java.util.List;

import ir.khu.gasht.DatabaseManager;
import ir.khu.gasht.R;
import ir.khu.gasht.models.Location;

public class LocationAdapter extends RecyclerView.Adapter<LocationAdapter.MyViewHolder> {

    private final Context context;
    private final Runnable onItemDeleted;
    private List<Location> itemList;

    public LocationAdapter(Context context, List<Location> itemList, Runnable onItemDeleted) {
        this.context = context;
        this.itemList = itemList;
        this.onItemDeleted = onItemDeleted;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // اتصال به فایل XML آیتم
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.location_layout, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        // دریافت آیتم فعلی
        Location item = itemList.get(position);

        // تنظیم داده‌های نمایش داده شده
        holder.title.setText(item.getTitle());
        holder.longAndLat.setText("Lat: " + item.getLatitude() + " | Lon: " + item.getLongitude());

        // کلیک روی دکمه حذف
        holder.deleteButton.setOnClickListener(view -> {

            // ساخت دیالوگ حذف
            Dialog dialog = new Dialog(context);
            dialog.setContentView(R.layout.dialog_layout_2);

            // اتصال ویوهای داخل دیالوگ
            MaterialButton dismissButton = dialog.findViewById(R.id.dismissButton);
            MaterialButton deleteButton = dialog.findViewById(R.id.deleteButton);

            // کلیک روی دکمه "حذف"
            deleteButton.setOnClickListener(v -> {
                // موقعیت فعلی بررسی شود که معتبر باشد
                int currentPosition = holder.getAdapterPosition();
                if (currentPosition != RecyclerView.NO_POSITION) {
                    // حذف از دیتابیس
                    new DatabaseManager(context).deleteLocation(itemList.get(currentPosition));

                    // حذف از لیست
                    itemList.remove(currentPosition);
                    notifyItemRemoved(currentPosition);

                    // اجرای کد بعد از حذف
                    if (onItemDeleted != null)
                        onItemDeleted.run();

                    // بستن دیالوگ
                    dialog.dismiss();
                }
            });

            // کلیک روی دکمه "لغو"
            dismissButton.setOnClickListener(v -> dialog.dismiss());

            // تنظیم ظاهر و ابعاد دیالوگ
            Window window = dialog.getWindow();
            if (window != null) {
                window.setBackgroundDrawable(new ColorDrawable(
                        ContextCompat.getColor(context, android.R.color.transparent)));
                window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            }

            // نمایش دیالوگ
            dialog.show();
        });
    }

    @Override
    public int getItemCount() {
        // تعداد آیتم‌ها
        return itemList != null ? itemList.size() : 0;
    }

    // ViewHolder کلاس داخلی برای نگهداری ویوهای آیتم
    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView title, longAndLat;
        ImageView deleteButton;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            // اتصال ویوها
            title = itemView.findViewById(R.id.title);
            longAndLat = itemView.findViewById(R.id.longAndLat);
            deleteButton = itemView.findViewById(R.id.deleteButton);
        }
    }
}