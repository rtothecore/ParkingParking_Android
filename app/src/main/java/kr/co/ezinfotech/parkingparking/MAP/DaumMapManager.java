package kr.co.ezinfotech.parkingparking.MAP;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.text.TextUtils;
import android.text.method.Touch;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import net.daum.mf.map.api.CalloutBalloonAdapter;
import net.daum.mf.map.api.CameraUpdateFactory;
import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapPointBounds;
import net.daum.mf.map.api.MapView;

import java.util.ArrayList;

import kr.co.ezinfotech.parkingparking.DATA.FavoritesDataManager;
import kr.co.ezinfotech.parkingparking.DATA.PZData;
import kr.co.ezinfotech.parkingparking.DATA.PZDataManager;
import kr.co.ezinfotech.parkingparking.DetailActivity;
import kr.co.ezinfotech.parkingparking.NAVI.TmapManager;
import kr.co.ezinfotech.parkingparking.R;
import kr.co.ezinfotech.parkingparking.TOUCH.TouchManager;
import kr.co.ezinfotech.parkingparking.UTIL.LoginManager;

public class DaumMapManager extends Activity {

    private Context ctx = null;
    private MapView mMapView;
    private MapPOIItem mCustomMarker = null;
    public ArrayList<PZData> pzData = new ArrayList<>();
    private int selectedPZIndex = 0;
    public Location centerPoint = new Location("centerPoint");
    private boolean isFavorites = false;
    private boolean isFirst = true;

    private static final MapPoint DEFAULT_MARKER_POINT = MapPoint.mapPointWithGeoCoord(33.5000217, 126.5456647);
    private static final MapPoint DEFAULT_MARKER_POINT2 = MapPoint.mapPointWithGeoCoord(33.50481997, 126.5343383);

    public DaumMapManager() {
    }

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
            centerPoint.setLatitude(mapView.getMapCenterPoint().getMapPointGeoCoord().latitude);
            centerPoint.setLongitude(mapView.getMapCenterPoint().getMapPointGeoCoord().longitude);
        }

        @Override
        public void onMapViewCenterPointMoved(MapView mapView, MapPoint mapPoint) {
            // Toast.makeText(ctx, "onMapViewCenterPointMoved(" + mapView.getMapCenterPoint().getMapPointGeoCoord().latitude + "," + mapView.getMapCenterPoint().getMapPointGeoCoord().longitude + ")", Toast.LENGTH_SHORT).show();
            centerPoint.setLatitude(mapView.getMapCenterPoint().getMapPointGeoCoord().latitude);
            centerPoint.setLongitude(mapView.getMapCenterPoint().getMapPointGeoCoord().longitude);

            LinearLayout ll = (LinearLayout) ((Activity)ctx).findViewById(R.id.parkingBottomLL);
            if(View.VISIBLE == ll.getVisibility()) {
                ll.setVisibility(View.INVISIBLE);
            }
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
        public void onPOIItemSelected(MapView mapView, final MapPOIItem mapPOIItem) {
            // Toast.makeText(ctx, "Clicked " + mapPOIItem.getItemName() + " onPOIItemSelected", Toast.LENGTH_SHORT).show();
            LinearLayout ll = (LinearLayout) ((Activity)ctx).findViewById(R.id.parkingBottomLL);

            // 해당 주차장을 터치할때마다 서버에 터치정보를 보냄
            TouchManager tm = new TouchManager();
            tm.touchParking(pzData.get(mapPOIItem.getTag()).no);
            
            // 클릭한 마커를 다시 또 클릭했을 때
            if(selectedPZIndex == mapPOIItem.getTag()) {
                if(View.INVISIBLE == ll.getVisibility()) {
                    ll.setVisibility(View.VISIBLE);
                } else {
                    ll.setVisibility(View.INVISIBLE);
                }
            } else {
                TextView tvParkingName = (TextView)((Activity)ctx).findViewById(R.id.textViewParkingName);
                tvParkingName.setText(pzData.get(mapPOIItem.getTag()).name);

                TextView tvParkingFeeContent = (TextView)((Activity)ctx).findViewById(R.id.textViewParkingFeeContent);
                tvParkingFeeContent.setText(pzData.get(mapPOIItem.getTag()).add_term.fee + "원/" + pzData.get(mapPOIItem.getTag()).add_term.time + "분");

                TextView tvOpTimeContent = (TextView)((Activity)ctx).findViewById(R.id.textViewParkingOpTimeContent);
                tvOpTimeContent.setText(pzData.get(mapPOIItem.getTag()).w_op.start_time + " ~ " + pzData.get(mapPOIItem.getTag()).w_op.end_time);

                ll.setVisibility(View.VISIBLE);
                selectedPZIndex = mapPOIItem.getTag();

                // 전화, 내비, 예약, 예측 버튼 클릭 리스너 - http://jizard.tistory.com/9
                ((Activity)ctx).findViewById(R.id.buttonTel).setOnClickListener(
                    new Button.OnClickListener() {
                        public void onClick(View v) {
                            if(!(pzData.get(mapPOIItem.getTag()).tel).equals("null")) {
                                String tel = "tel:" + pzData.get(mapPOIItem.getTag()).tel;
                                ctx.startActivity(new Intent("android.intent.action.DIAL", Uri.parse(tel)));
                            } else {
                                Toast.makeText(ctx, "등록된 전화번호가 없습니다", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                );

                ((Activity)ctx).findViewById(R.id.buttonNavi).setOnClickListener(
                    new Button.OnClickListener() {
                        public void onClick(View v) {
                            TmapManager.showRoute(pzData.get(mapPOIItem.getTag()).name, (float)pzData.get(mapPOIItem.getTag()).loc.getLatitude(), (float)pzData.get(mapPOIItem.getTag()).loc.getLongitude());
                        }
                    }
                );
/*
                ((Activity)ctx).findViewById(R.id.buttonReservation).setOnClickListener(
                    new Button.OnClickListener() {
                        public void onClick(View v) {
                            Toast.makeText(ctx, "mBtnReservationClickListener", Toast.LENGTH_SHORT).show();
                        }
                    }
                );

                ((Activity)ctx).findViewById(R.id.buttonPredict).setOnClickListener(
                    new Button.OnClickListener() {
                        public void onClick(View v) {
                            Toast.makeText(ctx, "mBtnPredictClickListener", Toast.LENGTH_SHORT).show();
                        }
                    }
                );
*/
            }

            // 즐겨찾기(별모양) 아이콘 셋팅
            if(null == LoginManager.getEmail()) {   // 로그인 안 한 경우
            } else {
                final ImageView ivFavoriteStar = ((Activity)ctx).findViewById(R.id.ivFavoriteStar);
                final FavoritesDataManager fdm = new FavoritesDataManager();
                fdm.CheckExistFavoriteInDBAndSet(ctx, ivFavoriteStar, pzData.get(mapPOIItem.getTag()).no);
            }

            // 주차장 선택하여 나타난 하단정보의 주차장명을 터치하면 발생하는 이벤트 - https://stackoverflow.com/questions/15596507/how-to-set-onclick-method-with-linearlayout
            LinearLayout parkingNameLL = (LinearLayout) ((Activity)ctx).findViewById(R.id.parkingNameLL);
            parkingNameLL.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(ctx, "OnClickListener-" + pzData.get(mapPOIItem.getTag()).addr_road, Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(ctx, DetailActivity.class);
                    intent.putExtra("name", pzData.get(mapPOIItem.getTag()).name);
                    intent.putExtra("addr_road", pzData.get(mapPOIItem.getTag()).addr_road);
                    intent.putExtra("tel", pzData.get(mapPOIItem.getTag()).tel);
                    intent.putExtra("myLat", centerPoint.getLatitude());
                    intent.putExtra("myLng", centerPoint.getLongitude());
                    intent.putExtra("lat", pzData.get(mapPOIItem.getTag()).loc.getLatitude());
                    intent.putExtra("lng", pzData.get(mapPOIItem.getTag()).loc.getLongitude());
                    intent.putExtra("park_base_time", pzData.get(mapPOIItem.getTag()).park_base.time);
                    intent.putExtra("park_base_fee", pzData.get(mapPOIItem.getTag()).park_base.fee);
                    intent.putExtra("add_term_time", pzData.get(mapPOIItem.getTag()).add_term.time);
                    intent.putExtra("add_term_fee", pzData.get(mapPOIItem.getTag()).add_term.fee);
                    intent.putExtra("w_op_start_time", pzData.get(mapPOIItem.getTag()).w_op.start_time);
                    intent.putExtra("w_op_end_time", pzData.get(mapPOIItem.getTag()).w_op.end_time);
                    intent.putExtra("s_op_start_time", pzData.get(mapPOIItem.getTag()).s_op.start_time);
                    intent.putExtra("s_op_end_time", pzData.get(mapPOIItem.getTag()).s_op.end_time);
                    intent.putExtra("h_op_start_time", pzData.get(mapPOIItem.getTag()).h_op.start_time);
                    intent.putExtra("h_op_end_time", pzData.get(mapPOIItem.getTag()).h_op.end_time);
                    intent.putExtra("fee_info", pzData.get(mapPOIItem.getTag()).fee_info);
                    intent.putExtra("one_day_park_fee", pzData.get(mapPOIItem.getTag()).one_day_park.fee);
                    intent.putExtra("month_fee", pzData.get(mapPOIItem.getTag()).month_fee);
                    intent.putExtra("homepage", pzData.get(mapPOIItem.getTag()).homepage);
                    intent.putExtra("park_space_small", pzData.get(mapPOIItem.getTag()).park_space_count.small);
                    intent.putExtra("park_space_mid", pzData.get(mapPOIItem.getTag()).park_space_count.mid);
                    intent.putExtra("park_space_big", pzData.get(mapPOIItem.getTag()).park_space_count.big);
                    intent.putExtra("park_space_elec", pzData.get(mapPOIItem.getTag()).park_space_count.elec);
                    intent.putExtra("park_space_hand", pzData.get(mapPOIItem.getTag()).park_space_count.hand);
                    intent.putExtra("sale_info", pzData.get(mapPOIItem.getTag()).sale_info);
                    ctx.startActivity(intent);
                }
            });
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
        // mMapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOnWithoutHeading);

        mMapView.setCalloutBalloonAdapter(new DaumMapManager.CustomCalloutBalloonAdapter());
        createCustomMarker(mMapView);
        if (isFirst) {
            showAll();
            isFirst = false;
        }
    }

    public void runMapProcessWithParam(int division, int type, int op, int fee) {
        mMapView.removeAllPOIItems();   // 맵 초기화

        mMapView.setCalloutBalloonAdapter(new DaumMapManager.CustomCalloutBalloonAdapter());
        createCustomMarkerWithParam(mMapView, division, type, op, fee);
        showAll();
    }

    public void runMapProcessWithFee(int fee) {
        mMapView.removeAllPOIItems();   // 맵 초기화

        mMapView.setCalloutBalloonAdapter(new DaumMapManager.CustomCalloutBalloonAdapter());
        createCustomMarkerWithFee(mMapView, fee);
        // showAll();
    }

    public void runMapProcessWithFavorites(String[] favoritesVal) {
        mMapView.removeAllPOIItems();   // 맵 초기화

        mMapView.setCalloutBalloonAdapter(new DaumMapManager.CustomCalloutBalloonAdapter());
        createCustomMarkerWithFavorites(mMapView, favoritesVal);
        showAll();
    }

    private void createCustomMarker(MapView mapView) {
        // 1. PZDataManager에서 SQLite에 접속하여 모든 주차장정보를 얻음.
        PZDataManager pzdm = new PZDataManager(null);
        pzData = pzdm.getAllPZData();

        // 2. 얻은 주차장 정보를 화면에 뿌림
        for(int i = 0; i < pzData.size(); i++) {
            mCustomMarker = new MapPOIItem();
            mCustomMarker.setItemName(pzData.get(i).name);
            mCustomMarker.setTag(i);
            mCustomMarker.setMapPoint(MapPoint.mapPointWithGeoCoord(pzData.get(i).loc.getLatitude(), pzData.get(i).loc.getLongitude()));
            mCustomMarker.setMarkerType(MapPOIItem.MarkerType.CustomImage);

            switch(pzData.get(i).add_term.fee) {
                case "0" :
                    mCustomMarker.setCustomImageResourceId(R.drawable.mk_00);
                    break;
                case "250" :
                    mCustomMarker.setCustomImageResourceId(R.drawable.mk_025);
                    break;
                case "300" :
                    mCustomMarker.setCustomImageResourceId(R.drawable.mk_03);
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
                    mCustomMarker.setCustomImageResourceId(R.drawable.mk_x);
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

    private void createCustomMarkerWithParam(MapView mapView, int division, int type, int op, int fee) {
        // 1. PZDataManager에서 SQLite에 접속하여 모든 주차장정보를 얻음.
        PZDataManager pzdm = new PZDataManager(null);
        pzData = pzdm.getPZDataWithParam(division, type, op, fee);

        // 2. 얻은 주차장 정보를 화면에 뿌림
        for(int i = 0; i < pzData.size(); i++) {
            mCustomMarker = new MapPOIItem();
            mCustomMarker.setItemName(pzData.get(i).name);
            mCustomMarker.setTag(i);
            mCustomMarker.setMapPoint(MapPoint.mapPointWithGeoCoord(pzData.get(i).loc.getLatitude(), pzData.get(i).loc.getLongitude()));
            mCustomMarker.setMarkerType(MapPOIItem.MarkerType.CustomImage);

            switch(pzData.get(i).add_term.fee) {
                case "0" :
                    mCustomMarker.setCustomImageResourceId(R.drawable.mk_00);
                    break;
                case "250" :
                    mCustomMarker.setCustomImageResourceId(R.drawable.mk_025);
                    break;
                case "300" :
                    mCustomMarker.setCustomImageResourceId(R.drawable.mk_03);
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
                    mCustomMarker.setCustomImageResourceId(R.drawable.mk_x);
                    break;
            }

            mCustomMarker.setCustomImageAutoscale(false);
            mCustomMarker.setCustomImageAnchor(0.5f, 1.0f);
            mCustomMarker.setShowCalloutBalloonOnTouch(false);

            mapView.addPOIItem(mCustomMarker);
        }

        mapView.selectPOIItem(mCustomMarker, true);
    }

    private void createCustomMarkerWithFee(MapView mapView, int fee) {
        // 1. PZDataManager에서 SQLite에 접속하여 특정 요금정보를 가진 주차장정보를 얻음.
        PZDataManager pzdm = new PZDataManager(null);
        pzData = pzdm.getPZDataWithFee(fee);

        // 2. 얻은 주차장 정보를 화면에 뿌림
        for(int i = 0; i < pzData.size(); i++) {
            mCustomMarker = new MapPOIItem();
            mCustomMarker.setItemName(pzData.get(i).name);
            mCustomMarker.setTag(i);
            mCustomMarker.setMapPoint(MapPoint.mapPointWithGeoCoord(pzData.get(i).loc.getLatitude(), pzData.get(i).loc.getLongitude()));
            mCustomMarker.setMarkerType(MapPOIItem.MarkerType.CustomImage);

            switch(pzData.get(i).add_term.fee) {
                case "0" :
                    mCustomMarker.setCustomImageResourceId(R.drawable.mk_00);
                    break;
                case "250" :
                    mCustomMarker.setCustomImageResourceId(R.drawable.mk_025);
                    break;
                case "300" :
                    mCustomMarker.setCustomImageResourceId(R.drawable.mk_03);
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
                    mCustomMarker.setCustomImageResourceId(R.drawable.mk_x);
                    break;
            }

            mCustomMarker.setCustomImageAutoscale(false);
            mCustomMarker.setCustomImageAnchor(0.5f, 1.0f);
            mCustomMarker.setShowCalloutBalloonOnTouch(false);

            mapView.addPOIItem(mCustomMarker);
        }

        mapView.selectPOIItem(mCustomMarker, true);
    }

    private void createCustomMarkerWithFavorites(MapView mapView, String[] favoritesVal) {
        // 1. PZDataManager에서 SQLite에 접속하여 모든 주차장정보를 얻음.
        PZDataManager pzdm = new PZDataManager(null);
        pzData = pzdm.getPZDataWithNos(favoritesVal);

        // 2. 얻은 주차장 정보를 화면에 뿌림
        for(int i = 0; i < pzData.size(); i++) {
            mCustomMarker = new MapPOIItem();
            mCustomMarker.setItemName(pzData.get(i).name);
            mCustomMarker.setTag(i);
            mCustomMarker.setMapPoint(MapPoint.mapPointWithGeoCoord(pzData.get(i).loc.getLatitude(), pzData.get(i).loc.getLongitude()));
            mCustomMarker.setMarkerType(MapPOIItem.MarkerType.CustomImage);

            switch(pzData.get(i).add_term.fee) {
                case "0" :
                    mCustomMarker.setCustomImageResourceId(R.drawable.mk_00);
                    break;
                case "250" :
                    mCustomMarker.setCustomImageResourceId(R.drawable.mk_025);
                    break;
                case "300" :
                    mCustomMarker.setCustomImageResourceId(R.drawable.mk_03);
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
                    mCustomMarker.setCustomImageResourceId(R.drawable.mk_x);
                    break;
            }

            mCustomMarker.setCustomImageAutoscale(false);
            mCustomMarker.setCustomImageAnchor(0.5f, 1.0f);
            mCustomMarker.setShowCalloutBalloonOnTouch(false);

            mapView.addPOIItem(mCustomMarker);
        }

        mapView.selectPOIItem(mCustomMarker, true);
    }

    private void showAll() {
        int padding = 20;
        float minZoomLevel = 7;
        float maxZoomLevel = 10;
        // MapPointBounds bounds = new MapPointBounds(DEFAULT_MARKER_POINT, DEFAULT_MARKER_POINT2);
        MapPointBounds bounds = new MapPointBounds(getBottomLeftMapPoint(), getTopRightMapPoint());
        mMapView.moveCamera(CameraUpdateFactory.newMapPointBounds(bounds, padding, minZoomLevel, maxZoomLevel));
    }

    // 중심점 변경 - http://apis.map.daum.net/android/guide/
    public void setMapCenter(double lat, double lng) {
        // mMapView.setMapCenterPoint(MapPoint.mapPointWithGeoCoord(lat, lng), true);
        mMapView.setMapCenterPointAndZoomLevel(MapPoint.mapPointWithGeoCoord(lat,lng), 1, true);
    }

    private MapPoint getBottomLeftMapPoint() {
        double bottom = pzData.get(0).loc.getLongitude();
        double left = pzData.get(0).loc.getLatitude();

        for(int i = 0; i < pzData.size(); i++) {
            if(bottom > pzData.get(i).loc.getLongitude()) {
                bottom = pzData.get(i).loc.getLongitude();
            }
        }

        for(int i = 0; i < pzData.size(); i++) {
            if(left > pzData.get(i).loc.getLatitude()) {
                left = pzData.get(i).loc.getLatitude();
            }
        }

        return MapPoint.mapPointWithGeoCoord(left, bottom);
    }

    private MapPoint getTopRightMapPoint() {
        double top = pzData.get(0).loc.getLongitude();
        double right = pzData.get(0).loc.getLatitude();

        for(int i = 0; i < pzData.size(); i++) {
            if(top < pzData.get(i).loc.getLongitude()) {
                top = pzData.get(i).loc.getLongitude();
            }
        }

        for(int i = 0; i < pzData.size(); i++) {
            if(right < pzData.get(i).loc.getLatitude()) {
                right = pzData.get(i).loc.getLatitude();
            }
        }

        return MapPoint.mapPointWithGeoCoord(right, top);
    }
}