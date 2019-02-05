package com.mapbox.mapboxsdk.testapp.activity.telemetry;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.google.gson.Gson;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.module.http.HttpRequestUtil;
import com.mapbox.mapboxsdk.testapp.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.EventListener;
import okhttp3.OkHttpClient;
import timber.log.Timber;

/**
 * Test activity showcasing gathering performance measurement data.
 */
public class PerformanceMeasurementActivity extends AppCompatActivity {

  private MapView mapView;

  private Map<String, Long> startTimes = new HashMap<>();

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_map_simple);
    mapView = findViewById(R.id.mapView);
    mapView.onCreate(savedInstanceState);


    OkHttpClient.Builder builder = new OkHttpClient.Builder();
    builder.eventListener(new EventListener() {

      @Override
      public void callStart(Call call) {
        String url = call.request().url().toString();
        startTimes.put(url, System.nanoTime());
        super.callStart(call);
        Timber.e("callStart: %s", url);
      }

      @Override
      public void callEnd(Call call) {
        String url = call.request().url().toString();
        Timber.e("callEnd: %s", url);
        Long start = startTimes.get(url);
        if (start != null) {
          long elapsed = System.nanoTime() - start;
          triggerPerformanceEvent(url.substring(0, url.indexOf('?')), elapsed);
          startTimes.remove(start);
          Timber.e("callEnd: %s took %d", url, elapsed);
        }
        super.callEnd(call);
      }
    });
    HttpRequestUtil.setOkHttpClient(builder.build());

    mapView.getMapAsync(mapboxMap -> mapboxMap.setStyle(
      new Style.Builder().fromUrl(Style.MAPBOX_STREETS)));
  }


  @Override
  protected void onStart() {
    super.onStart();
    mapView.onStart();
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

  @Override
  protected void onStop() {
    super.onStop();
    mapView.onStop();
  }

  @Override
  public void onLowMemory() {
    super.onLowMemory();
    mapView.onLowMemory();
  }

  @Override
  protected void onDestroy() {

    startTimes.clear();

    super.onDestroy();
    mapView.onDestroy();
  }

  @Override
  protected void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
    mapView.onSaveInstanceState(outState);
  }

  private void triggerPerformanceEvent(String style, long elapsed) {

    List<Attribute<String>> attributes = new ArrayList<>();
    attributes.add(
            new Attribute<>("style_id", style));
    attributes.add(
            new Attribute<>("test_perf_event", "true"));

    List<Attribute<Long>> counters = new ArrayList();
    counters.add(new Attribute<>("elapsed", elapsed));


    Bundle bundle = new Bundle();
    bundle.putString("attributes", new Gson().toJson(attributes));
    bundle.putString("counters", new Gson().toJson(counters));

    Mapbox.getTelemetry().onPerformanceEvent(bundle);

  }

  private class Attribute<T> {
    private String name;
    private T value;

    Attribute(String name, T value) {
      this.name = name;
      this.value = value;
    }
  }
}
