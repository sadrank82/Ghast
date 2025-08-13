package ir.khu.gasht;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;

import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Overlay;

import ir.khu.gasht.models.Location;

public class MapClickOverlay extends Overlay {

    private Context context;
    private MapView map;
    private Runnable onLocationAdded;

    public MapClickOverlay(Context context, MapView map, Runnable onLocationAdded) {
        super(context);
        this.context = context;
        this.map = map;
        this.onLocationAdded = onLocationAdded;
    }


    @Override
    public boolean onSingleTapConfirmed(MotionEvent e, MapView mapView) {

        // دریافت طول و عرض جغرافیایی از نقشه
        final GeoPoint tappedPoint = (GeoPoint) mapView.getProjection().fromPixels((int) e.getX(), (int) e.getY());
        final double lat = tappedPoint.getLatitude();
        final double lon = tappedPoint.getLongitude();

        // ساخت دیالوگ
        Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialog_layout);

        //مقدار دهی اجزای دیالوگ
        EditText titleInput = dialog.findViewById(R.id.titleInput);
        MaterialButton dismissButton = dialog.findViewById(R.id.dismissButton);
        MaterialButton createButton = dialog.findViewById(R.id.createButton);

        // تنظیم کلیک روی دکمه ذخیره مکان
        createButton.setOnClickListener(view -> {
            String locationName = titleInput.getText().toString().trim();
            if (!locationName.isEmpty()) {

                // نمایش مارکر روی نقشه
                Marker marker = new Marker(map);
                marker.setPosition(tappedPoint);
                marker.setTitle(locationName);
                marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);

                // تنظیم آیکون سفازشی مارکر
                Drawable customIcon = context.getResources().getDrawable(R.drawable.baseline_location_on_24);
                customIcon.setTint(Color.RED);
                marker.setIcon(customIcon);

                map.getOverlays().add(marker);
                map.invalidate();

                // ذخیره مکان
                Location location = new Location();
                location.setTitle(locationName);
                location.setLongitude(lon);
                location.setLatitude(lat);

                new DatabaseManager(context)
                        .insertLocation(location);

                // اعلام ساخته شدن مکان
                if (onLocationAdded != null) {
                    onLocationAdded.run();
                }

                dialog.dismiss();
            } else {
                Toast.makeText(context, "نام مکان نباید خالی باشد", Toast.LENGTH_SHORT).show();
            }
        });

        // تنظیم کلیک روی دکمه لغو
        dismissButton.setOnClickListener(view -> dialog.dismiss());

        // تنظیم پس زمینه شفاف و نمایش دیالوگ
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();

        // تنظیم عرض لایه دیالوگ
        Window window = dialog.getWindow();
        if (window != null) {
            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
        return true;
    }
}
