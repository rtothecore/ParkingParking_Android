package kr.co.ezinfotech.parkingparking.MAP;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import net.daum.mf.map.api.CalloutBalloonAdapter;
import net.daum.mf.map.api.CameraUpdateFactory;
import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapPointBounds;
import net.daum.mf.map.api.MapView;

import kr.co.ezinfotech.parkingparking.DATA.PZData;
import kr.co.ezinfotech.parkingparking.DATA.PZDataManager;
import kr.co.ezinfotech.parkingparking.R;

// public class MapManager extends Activity implements MapView.MapViewEventListener, MapView.POIItemEventListener {
public class DaumMapManager extends Activity {

    private Context ctx = null;
    private MapView mMapView;
    private MapPOIItem mCustomMarker = null;
    private PZData[] pzData = null;
    private int selectedPZIndex = 0;

    private static final MapPoint DEFAULT_MARKER_POINT = MapPoint.mapPointWithGeoCoord(33.5000217, 126.5456647);
    private static final MapPoint DEFAULT_MARKER_POINT2 = MapPoint.mapPointWithGeoCoord(33.50481997, 126.5343383);

    class CustomCalloutBalloonAdapter implements CalloutBalloonAdapter {
        private final View mCalloutBalloon = null;

        public CustomCalloutBalloonAdapter() {
        }

        @Override
        public View getCalloutBalloon(MapPOIItem poiItem) {
            return null;
        }

        @Override
        public View getPressedCalloutBalloon(MapPOIItem poiItem) {
            return null;
        }
    }

    MapView.MapViewEventListener mvel = new MapView.MapViewEventListener() {
        @Override
        public void onMapViewInitialized(MapView mapView) {
            Toast.makeText(ctx, "onMapViewInitialized", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onMapViewCenterPointMoved(MapView mapView, MapPoint mapPoint) {

        }

        @Override
        public void onMapViewZoomLevelChanged(MapView mapView, int i) {

        }

        @Override
        public void onMapViewSingleTapped(MapView mapView, MapPoint mapPoint) {
            // Toast.makeText(ctx, "onMapViewSingleTapped", Toast.LENGTH_SHORT).show();
            LinearLayout ll = (LinearLayout) ((Activity)ctx).findViewById(R.id.parkingBottomLL);
            if(View.VISIBLE == ll.getVisibility()) {
                ll.setVisibility(View.INVISIBLE);
            }
        }

        @Override
        public void onMapViewDoubleTapped(MapView mapView, MapPoint mapPoint) {

        }

        @Override
        public void onMapViewLongPressed(MapView mapView, MapPoint mapPoint) {

        }

        @Override
        public void onMapViewDragStarted(MapView mapView, MapPoint mapPoint) {

        }

        @Override
        public void onMapViewDragEnded(MapView mapView, MapPoint mapPoint) {

        }

        @Override
        public void onMapViewMoveFinished(MapView mapView, MapPoint mapPoint) {

        }
    };

    MapView.POIItemEventListener piel = new MapView.POIItemEventListener() {
        @Override
        public void onPOIItemSelected(MapView mapView, MapPOIItem mapPOIItem) {
            // Toast.makeText(ctx, "Clicked " + mapPOIItem.getItemName() + " onPOIItemSelected", Toast.LENGTH_SHORT).show();
            LinearLayout ll = (LinearLayout) ((Activity)ctx).findViewById(R.id.parkingBottomLL);

            // 클릭한 마커를 다시 또 클릭했을 때
            if(selectedPZIndex == mapPOIItem.getTag()) {
                if(View.INVISIBLE == ll.getVisibility()) {
                    ll.setVisibility(View.VISIBLE);
                } else {
                    ll.setVisibility(View.INVISIBLE);
                }
            } else {
                TextView tvParkingName = (TextView) ((Activity)ctx).findViewById(R.id.textViewParkingName);
                tvParkingName.setText(pzData[mapPOIItem.getTag()].name);

                ll.setVisibility(View.VISIBLE);
                selectedPZIndex = mapPOIItem.getTag();
            }
        }

        @Override
        public void onCalloutBalloonOfPOIItemTouched(MapView mapView, MapPOIItem mapPOIItem) {

        }

        @Override
        public void onCalloutBalloonOfPOIItemTouched(MapView mapView, MapPOIItem mapPOIItem, MapPOIItem.CalloutBalloonButtonType calloutBalloonButtonType) {

        }

        @Override
        public void onDraggablePOIItemMoved(MapView mapView, MapPOIItem mapPOIItem, MapPoint mapPoint) {

        }
    };

    public DaumMapManager(Context ctxVal) {
        ctx = ctxVal;

        // http://noransmile.tistory.com/entry/android-Context%EC%97%90%EC%84%9C-findViewById-%ED%98%B8%EC%B6%9C%ED%95%98%EB%8A%94%EB%B0%A9%EB%B2%95
        mMapView = (MapView) ((Activity)ctx).findViewById(R.id.map_view);

        // http://ericstoltz.tistory.com/449
        mMapView.setMapViewEventListener(mvel);
        mMapView.setPOIItemEventListener(piel);
    }

    // http://apis.map.daum.net/android/guide/
    // http://ariarihan.tistory.com/368
    public void runMapProcess() {
        mMapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOnWithoutHeading);

        mMapView.setCalloutBalloonAdapter(new DaumMapManager.CustomCalloutBalloonAdapter());
        createCustomMarker(mMapView);
        showAll();
    }

    private void createCustomMarker(MapView mapView) {
        // 1. PZDataManager에서 SQLite에 접속하여 모든 주차장정보를 얻음.
        PZDataManager pzdm = new PZDataManager(null);
        pzData = pzdm.getAllPZData();

        // 2. 얻은 주차장 정보를 화면에 뿌림
        for(int i = 0; i < pzData.length; i++) {
            mCustomMarker = new MapPOIItem();
            mCustomMarker.setItemName(pzData[i].name);
            mCustomMarker.setTag(i);
            mCustomMarker.setMapPoint(MapPoint.mapPointWithGeoCoord(pzData[i].loc.getLatitude(), pzData[i].loc.getLongitude()));
            mCustomMarker.setMarkerType(MapPOIItem.MarkerType.CustomImage);

            switch(pzData[i].park_base.fee) {
                case "0" :
                    mCustomMarker.setCustomImageResourceId(R.drawable.mk_00);
                    break;
                case "500" :
                    mCustomMarker.setCustomImageResourceId(R.drawable.mk_05);
                    break;
                case "1000" :
                    mCustomMarker.setCustomImageResourceId(R.drawable.mk_10);
                    break;
                case "1500" :
                    mCustomMarker.setCustomImageResourceId(R.drawable.mk_15);
                    break;
                case "2000" :
                    mCustomMarker.setCustomImageResourceId(R.drawable.mk_20);
                    break;
                default :
                    break;
            }

            mCustomMarker.setCustomImageAutoscale(false);
            mCustomMarker.setCustomImageAnchor(0.5f, 1.0f);
            mCustomMarker.setShowCalloutBalloonOnTouch(false);

            mapView.addPOIItem(mCustomMarker);
        }

        mapView.selectPOIItem(mCustomMarker, true);
        // mapView.setMapCenterPoint(DEFAULT_MARKER_POINT, false);
    }

    private void showAll() {
        int padding = 20;
        float minZoomLevel = 7;
        float maxZoomLevel = 10;
        MapPointBounds bounds = new MapPointBounds(DEFAULT_MARKER_POINT, DEFAULT_MARKER_POINT2);
        mMapView.moveCamera(CameraUpdateFactory.newMapPointBounds(bounds, padding, minZoomLevel, maxZoomLevel));
    }
}