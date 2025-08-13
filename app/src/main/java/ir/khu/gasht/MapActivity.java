package ir.khu.gasht;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.TypedValue;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetBehavior;

import org.osmdroid.config.Configuration;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

import java.util.List;

import ir.khu.gasht.adapters.LocationAdapter;
import ir.khu.gasht.models.Location;

public class MapActivity extends AppCompatActivity {

    private ImageView imageBack;
    private LinearLayout bottomSheet;
    private MapView mapView;
    private RecyclerView mapList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeUtils.applyTheme(this);
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        Configuration.getInstance().setUserAgentValue(getPackageName());
        setContentView(R.layout.activity_map);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // مقداردهی اولیه المان‌ها
        imageBack = findViewById(R.id.imageBack);
        bottomSheet = findViewById(R.id.bottomSheetContainer);
        mapView = findViewById(R.id.mapView);
        mapList = findViewById(R.id.mapList);

        // تنظیم ویژگی های لیست
        mapList.setLayoutManager(new LinearLayoutManager(this));
        DividerItemDecoration decoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        mapList.addItemDecoration(decoration);

        // تنظیم ویژگی های باتم شیت
        int peekHeight = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 300, getResources().getDisplayMetrics());
        BottomSheetBehavior<LinearLayout> behavior = BottomSheetBehavior.from(bottomSheet);
        behavior.setHideable(false);
        behavior.setPeekHeight(peekHeight);
        behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        behavior.setDraggable(true);
        behavior.setSkipCollapsed(false);

        // دکمه بازگشت
        imageBack.setOnClickListener(view -> finish());

        // تنظیم ویژگی های مپ
        mapView.setMultiTouchControls(true);
        mapView.getController().setZoom(14.0);
        mapView.getController().setCenter(new GeoPoint(35.6892, 51.3890));

        MapClickOverlay overlay = new MapClickOverlay(this, mapView, this::loadLocations);
        mapView.getOverlays().add(overlay);

        // دریافت و نمایش مکان های ذخیره شده
        loadLocations();
    }

    private void loadLocations() {
        mapView.getOverlays().removeIf(overlay -> overlay instanceof Marker);

        // دریافت لیست مکان ها از دیتابیس
        DatabaseManager db = new DatabaseManager(this);
        List<Location> locations = db.getAllLocations();

        Drawable customIcon = getResources().getDrawable(R.drawable.baseline_location_on_24);
        customIcon.setTint(Color.RED);

        // افزودن مارکر مکان ها به نشه
        for (Location loc : locations) {
            GeoPoint point = new GeoPoint(loc.getLatitude(), loc.getLongitude());

            Marker marker = new Marker(mapView);
            marker.setPosition(point);
            marker.setTitle(loc.getTitle());
            marker.setSubDescription("Lat: " + loc.getLatitude() + "\nLon: " + loc.getLongitude());
            marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
            marker.setIcon(customIcon);

            mapView.getOverlays().add(marker);
        }

        mapView.invalidate();

        // تنظیم آداپتر
        LocationAdapter adapter = new LocationAdapter(this, locations, this::loadLocations);
        mapList.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }
}