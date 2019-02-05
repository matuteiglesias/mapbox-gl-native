package com.mapbox.mapboxsdk.module.telemetry;

import android.os.Bundle;
import android.os.Parcel;
import android.support.test.runner.AndroidJUnit4;

import com.google.gson.Gson;
import com.mapbox.android.telemetry.Event;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class PerformanceEventTest {


  @Test
  public void checksPerformanceEvent() throws Exception {

    Event event = obtainPerformanceEvent();
    assertTrue(event instanceof PerformanceEvent);

    PerformanceEvent performanceEvent = (PerformanceEvent)event;
    Parcel parcel = Parcel.obtain();

    performanceEvent.writeToParcel(parcel, 0);
    parcel.setDataPosition(0);

    PerformanceEvent newPerfEvent = PerformanceEvent.CREATOR.createFromParcel(parcel);
    assertTrue(newPerfEvent instanceof PerformanceEvent);

  }

  private Event obtainPerformanceEvent() {
    String styleStr = "mapbox://styles/mapbox/streets-v11";
    boolean testPerfEvent = true;
    Double doubleValue = 40.5;
    Long longValue = 40L;
    Integer intValue = 40;

    List<Attribute<String>> attributes = new ArrayList<>();
    attributes.add(
            new Attribute<>("style_id", styleStr));
    attributes.add(
            new Attribute<>("test_perf_event", String.valueOf(testPerfEvent)));

    List<Attribute<? extends Number>> counters = new ArrayList();
    counters.add(new Attribute<>("long_value", longValue));
    counters.add(new Attribute<>("double_value", doubleValue));
    counters.add(new Attribute<>("int_value", intValue));

    Gson gson = new Gson();

    Bundle bundle = new Bundle();
    String jsonStr = gson.toJson(attributes);
    bundle.putString("attributes", jsonStr);
    jsonStr = gson.toJson(counters);
    bundle.putString("counters", jsonStr);

    return new PerformanceEvent(UUID.randomUUID().toString(), bundle);
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
