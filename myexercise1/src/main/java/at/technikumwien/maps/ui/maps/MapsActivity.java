package at.technikumwien.maps.ui.maps;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.FrameLayout;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;

import at.technikumwien.maps.R;
import at.technikumwien.maps.data.model.DrinkingFountain;
import at.technikumwien.maps.ui.base.BaseActivity;

/**
 * Created by Bellacraine on 08.10.2017.
 */

public class MapsActivity extends BaseActivity<MapsView, MapsPresenter> implements OnMapReadyCallback, MapsView{

    private GoogleMap googleMap;
    private FrameLayout rootLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        rootLayout = findViewById(R.id.root_layout);

        // Get the SupportMapFragment and request notification
        // when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    @NonNull
    @Override
    public MapsPresenter createPresenter() {return new MapsPresenter(getAppDependencyManager());
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        LatLng viennaLatLng = new LatLng(48.239340, 16.377335);
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(viennaLatLng, 10));
        presenter.loadDrinkingFountains();
    }


    @Override
    public void showDrinkingFountains(List<DrinkingFountain> drinkingFountains) {
        googleMap.clear();

        for(DrinkingFountain df : drinkingFountains) {
            googleMap.addMarker(new MarkerOptions().position(df.position()).title(df.name()));
        }
    }


    @Override
    public void showLoadingError(Exception e) {
        Snackbar.make(rootLayout, "Trinkbrunnen konnten nicht geladen werden", Snackbar.LENGTH_INDEFINITE)
                .setAction("Bitte erneut versuchen", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        presenter.loadDrinkingFountains();
                    }
                }).show();
    }
}
