package kr.co.ezinfotech.parkingparking.MAP;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import net.daum.mf.map.api.CalloutBalloonAdapter;
import net.daum.mf.map.api.CameraUpdateFactory;
import net.daum.mf.map.api.MapCircle;
import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapPointBounds;
import net.daum.mf.map.api.MapView;

import java.util.ArrayList;
import java.util.Arrays;

import kr.co.ezinfotech.parkingparking.DATA.ClusteredData;
import kr.co.ezinfotech.parkingparking.DATA.FavoritesDataManager;
import kr.co.ezinfotech.parkingparking.DATA.MapClusterDataManager;
import kr.co.ezinfotech.parkingparking.DATA.PZData;
import kr.co.ezinfotech.parkingparking.DATA.PZDataManager;
import kr.co.ezinfotech.parkingparking.DetailActivity;
import kr.co.ezinfotech.parkingparking.NAVI.TmapManager;
import kr.co.ezinfotech.parkingparking.R;
import kr.co.ezinfotech.parkingparking.TOUCH.TouchManager;
import kr.co.ezinfotech.parkingparking.UTIL.GPSManager;
import kr.co.ezinfotech.parkingparking.UTIL.LoginManager;
import kr.co.ezinfotech.parkingparking.UTIL.UtilManager;

public class DaumMapManager extends Activity {

    private Context ctx = null;
    private MapView mMapView;
    private MapPOIItem mCustomMarker = null;
    public ArrayList<PZData> pzData = new ArrayList<>();
    private int selectedPZIndex = 0;
    public Location centerPoint = new Location("centerPoint");
    public Location myLocPoint = new Location("myLocPoint");
    private boolean isFirst = true;
    private int preCurrentMode = 4;
    private int currentMode = 0;    // 0:전체, 1:유료, 2:무료, 3:즐겨찾기
    private String[] favorites = null;
    private int currentZoomLevel = 0;
    private boolean turnOnCluster = false;

    private Location searchedPoint = new Location("searchedPoint");
    private boolean showSearchedMarker = false;
    private MapPOIItem defaultMarker = new MapPOIItem();
    private String defaultMarkerName = null;

    public DaumMapManager() {
    }

    public DaumMapManager(Context ctxVal) {
        ctx = ctxVal;

        // http://noransmile.tistory.com/entry/android-Context%EC%97%90%EC%84%9C-findViewById-%ED%98%B8%EC%B6%9C%ED%95%98%EB%8A%94%EB%B0%A9%EB%B2%95
        mMapView = (MapView) ((Activity)ctx).findViewById(R.id.map_view);

        // GPS 정보를 얻어 현재 위치로 이동
        GPSManager gpsm = new GPSManager(ctx);
        if(gpsm.isGetLocation()) {
            mMapView.setMapCenterPoint(MapPoint.mapPointWithGeoCoord(gpsm.getLatitude(), gpsm.getLongitude()), true);
        } else {
            // GPS를 사용할 수 없는 경우
            gpsm.showSettingsAlert();
        }

        // http://ericstoltz.tistory.com/449
        mMapView.setMapViewEventListener(mvel);
        mMapView.setPOIItemEventListener(piel);
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
            // Toast.makeText(ctx, "onMapViewInitialized", Toast.LENGTH_SHORT).show();

            // 현재위치 트랙킹 및 줌인
            mMapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOnWithoutHeadingWithoutMapMoving);
            mMapView.setMapCenterPointAndZoomLevel(mMapView.getMapCenterPoint(), 2, false);

            // 현재 나의 GPS 좌표 저장
            myLocPoint.setLatitude(mapView.getMapCenterPoint().getMapPointGeoCoord().latitude);
            myLocPoint.setLongitude(mapView.getMapCenterPoint().getMapPointGeoCoord().longitude);

            centerPoint.setLatitude(mapView.getMapCenterPoint().getMapPointGeoCoord().latitude);
            centerPoint.setLongitude(mapView.getMapCenterPoint().getMapPointGeoCoord().longitude);

            // 마커 그리기
            createCustomMarker(mMapView);
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
        public void onMapViewZoomLevelChanged(MapView mapView, int zoomLevel) {
            centerPoint.setLatitude(mapView.getMapCenterPoint().getMapPointGeoCoord().latitude);
            centerPoint.setLongitude(mapView.getMapCenterPoint().getMapPointGeoCoord().longitude);

            if(zoomLevel != currentZoomLevel) {
                if(turnOnCluster && (4 <= zoomLevel)) {
                    // Toast.makeText(ctx, "onMapViewZoomLevelChanged-" + zoomLevel, Toast.LENGTH_SHORT).show();
                    Log.i("turnOnCluster", String.valueOf(turnOnCluster));
                    Log.i("zoomLevel", String.valueOf(zoomLevel));
                    runMapCluster(zoomLevel);
                } else {
                    // mMapView.removeAllCircles();
                    if(preCurrentMode != currentMode) {
                        switch (currentMode) {
                            case 0 :
                                runMapProcess(false, false);
                                break;
                            case 1:
                                runMapProcessWithFee(1, false);
                                break;
                            case 2:
                                runMapProcessWithFee(2, false);
                                break;
                            case 3:
                                runMapProcessWithFavorites(favorites, false);
                                break;
                            default :
                                break;
                        }
                    }
                    if(4 <= zoomLevel) {
                        Log.i("zoomLevel2", String.valueOf(zoomLevel));
                        runMapCluster(zoomLevel);
                    }
                }
            }
            currentZoomLevel = zoomLevel;

            if (showSearchedMarker) {
                setDefaultMarker();    // 맵의 중심에 마커 찍기
            }
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

            // 기본 마커일 경우 리턴
            if(7777 == mapPOIItem.getTag()) {
                return;
            }

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
                if("무료".equals(pzData.get(mapPOIItem.getTag()).fee_info)) {
                    tvParkingFeeContent.setText("무료");
                } else {
                    if(pzData.get(mapPOIItem.getTag()).park_base.fee.equals("null")
                            || pzData.get(mapPOIItem.getTag()).park_base.time.equals("null")
                            || pzData.get(mapPOIItem.getTag()).park_base.fee.equals("-1")) {
                        tvParkingFeeContent.setText("미등록");
                    } else {
                        tvParkingFeeContent.setText(pzData.get(mapPOIItem.getTag()).park_base.fee + "원/" + pzData.get(mapPOIItem.getTag()).park_base.time + "분");
                    }
                }

                TextView tvOpTimeContent = (TextView)((Activity)ctx).findViewById(R.id.textViewParkingOpTimeContent);
                if("무료".equals(pzData.get(mapPOIItem.getTag()).fee_info)) {
                    tvOpTimeContent.setText(pzData.get(mapPOIItem.getTag()).w_op.end_time + " ~ 익일" + pzData.get(mapPOIItem.getTag()).w_op.start_time);
                } else {
                    if(pzData.get(mapPOIItem.getTag()).w_op.start_time.equals("null") || pzData.get(mapPOIItem.getTag()).w_op.end_time.equals("null")) {
                        tvOpTimeContent.setText("미등록");
                    } else {
                        tvOpTimeContent.setText(pzData.get(mapPOIItem.getTag()).w_op.start_time + " ~ " + pzData.get(mapPOIItem.getTag()).w_op.end_time);
                    }
                }

                ll.setVisibility(View.VISIBLE);
                selectedPZIndex = mapPOIItem.getTag();

                // 전화, 내비, 예약, 예측 버튼 클릭 리스너 - http://jizard.tistory.com/9
                ((Activity)ctx).findViewById(R.id.buttonTel).setOnClickListener(
                    new Button.OnClickListener() {
                        public void onClick(View v) {
                            if(!(pzData.get(mapPOIItem.getTag()).tel).equals("null")) {
                                String tel = "tel:" + pzData.get(mapPOIItem.getTag()).tel;
                                try {
                                    ctx.startActivity(new Intent("android.intent.action.DIAL", Uri.parse(tel)));
                                } catch (ActivityNotFoundException e) {
                                    Toast.makeText(ctx, "기기에 전화기능이 없습니다", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(ctx, "등록된 전화번호가 없습니다", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                );

                ((Activity)ctx).findViewById(R.id.buttonNavi).setOnClickListener(
                    new Button.OnClickListener() {
                        public void onClick(View v) {
                            if (TmapManager.isTmapInstalled()) {
                                TmapManager.showRoute(pzData.get(mapPOIItem.getTag()).name, (float)pzData.get(mapPOIItem.getTag()).loc.getLatitude(), (float)pzData.get(mapPOIItem.getTag()).loc.getLongitude());
                            } else {
                                Toast.makeText(ctx, "네비기능을 이용하시기 위해서는 T맵을 설치해야 합니다", Toast.LENGTH_LONG).show();
                                ArrayList tmapDownUrl = TmapManager.getTmapDownUrl();
                                Uri uri = Uri.parse(tmapDownUrl.get(0).toString());
                                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                                ctx.startActivity(intent);
                            }
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
            if(!LoginManager.isLogin()) {   // 로그인 안 한 경우
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
                    // Toast.makeText(ctx, "OnClickListener-" + pzData.get(mapPOIItem.getTag()).addr_road, Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(ctx, DetailActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // ADDED
                    intent.putExtra("no", pzData.get(mapPOIItem.getTag()).no);
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

    // http://apis.map.daum.net/android/guide/
    // http://ariarihan.tistory.com/368
    public void runMapProcess(boolean forFirstLoading, boolean fromFAB) {
        if(forFirstLoading) {
        } else {
                // 클러스터링 모드이고 줌레벨이 4이상이면 현재기기위치로 이동한다
                if(fromFAB && turnOnCluster && (4 <= currentZoomLevel ) ) {
                    // mMapView.setZoomLevel(3, true);
                    mMapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOnWithoutHeadingWithoutMapMoving);
                    mMapView.setMapCenterPointAndZoomLevel(MapPoint.mapPointWithGeoCoord(myLocPoint.getLatitude(), myLocPoint.getLongitude()), 2, false);
                }

                turnOnCluster = false;
                mMapView.removeAllCircles();    // 클러스터링 초기화
                mMapView.removeAllPOIItems();   // 맵 초기화
                if (showSearchedMarker) {
                    setDefaultMarker();    // 맵의 중심에 마커 찍기
                }
                mMapView.setPOIItemEventListener(piel);

                mMapView.setCalloutBalloonAdapter(new DaumMapManager.CustomCalloutBalloonAdapter());
                createCustomMarker(mMapView);
                if (isFirst) {
                    // showAll();
                    isFirst = false;
                    preCurrentMode = 4;
                } else {
                    preCurrentMode = 0;
                }
        }
    }

    private void runMapCluster(int currentZoomLevel2) {
        turnOnCluster = true;
        mMapView.removeAllPOIItems();
        if (showSearchedMarker) {
            setDefaultMarker();    // 맵의 중심에 마커 찍기
        }
        mMapView.removeAllCircles();
        mMapView.setPOIItemEventListener(null);

        int clusterRange = 1000;   // 1Km
        ArrayList<ClusteredData> clusteredData = new ArrayList<>();
        Boolean[] isClustered = new Boolean[pzData.size()];
        Arrays.fill(isClustered, Boolean.FALSE);

        // currentZoomLevel에 따라 clusterRange값 조절
        Log.i("ZoomLevel", String.valueOf(currentZoomLevel2));
        /*
        if(4 >= currentZoomLevel) {
            clusterRange = 2000;    // 3Km
        } else if(4 < currentZoomLevel && 5 >= currentZoomLevel) {
            clusterRange = 5000;    // 8Km
        } else if(5 < currentZoomLevel) {
            clusterRange = 15000;    // 12Km
        }
        */
        if(currentZoomLevel2 == 4) {
            clusterRange = 2000;    // 12Km
        } else if(currentZoomLevel2 == 5) {
            clusterRange = 5000;    // 12Km
        } else {
            clusterRange = 15000;
        }

        // 클러스터링된 위치정보 중 가장 멀리있는 좌표
        Location maxClusterLoc = new Location("maxClusterLoc");

        for(int i = 0; i < pzData.size(); i++) {
            ClusteredData cd = new ClusteredData();
            float maxDistance = 0;

            // 클러스터링 중심점 계산
            Location clusterSeedLoc = new Location("clusterSeedLoc");
            if (0 == i) {
                clusterSeedLoc.setLatitude(pzData.get(i).loc.getLatitude());
                clusterSeedLoc.setLongitude(pzData.get(i).loc.getLongitude());
            } else {
                float maxDistanceForSeed = 0;
                Location maxClusterLocForSeed = new Location("maxClusterLocForSeed");
                boolean isFinded = false;
                for (int m = 0; m < pzData.size(); m++) {
                    if(!isClustered[m]) {   // 이미 클러스터링된 위치정보는 제외하고 클러스터링
                        // 클러스터링할 위치정보
                        Location targetLoc = new Location("targetLoc");
                        targetLoc.setLatitude(pzData.get(m).loc.getLatitude());
                        targetLoc.setLongitude(pzData.get(m).loc.getLongitude());

                        float distance = maxClusterLoc.distanceTo(targetLoc);
                        if(clusterRange <= distance) {
                            clusterSeedLoc = targetLoc;
                            isFinded = true;
                            break;
                        } else {
                            if (maxDistanceForSeed < distance) {   // 클러스터링된 위치정보 중 가장 멀리있는 좌표를 업데이트
                                maxDistanceForSeed = distance;
                                maxClusterLocForSeed = targetLoc;
                            }
                        }

                        if(m == (pzData.size() - 1) && !isFinded) { // 좌표데이터를 모두 다 탐색했고 아직 시드 좌표를 못 찾았다면 지금까지 찾은 가장 먼 좌표를 시드 좌표로 설정
                            clusterSeedLoc = maxClusterLocForSeed;
                        }
                    }
                }
            }

            for(int j = 0; j < pzData.size(); j++) {
                if(!isClustered[j]) {   // 이미 클러스터링된 위치정보는 제외하고 클러스터링
                    // 클러스터링할 위치정보
                    Location targetLoc = new Location("targetLoc");
                    targetLoc.setLatitude(pzData.get(j).loc.getLatitude());
                    targetLoc.setLongitude(pzData.get(j).loc.getLongitude());

                    float distance = clusterSeedLoc.distanceTo(targetLoc);
                    if(clusterRange >= distance) {
                        cd.clusterData.add(targetLoc);
                        isClustered[j] = true;
                        if (maxDistance < distance) {   // 클러스터링된 위치정보 중 가장 멀리있는 좌표를 업데이트
                            maxDistance = distance;
                            maxClusterLoc = targetLoc;
                        }
                    }
                }
            }
            if(0 < cd.clusterData.size()) {
                clusteredData.add(cd);
            }
        }

        // 가중치 계산
        // int weightForCircle = ((currentZoomLevel - 4) * 1000);
        int weightForCircle = ((currentZoomLevel2 - 4) * 500);

        // 오버레이 이미지 그리기
        for(int k = 0; k < clusteredData.size(); k++) {
            int circleRadius = 0;
/*
            if(1 <= clusteredData.get(k).clusterData.size() && 10 >= clusteredData.get(k).clusterData.size()) {
                circleRadius = 100 + weightForCircle;  // 0.3Km
            } else if(10 < clusteredData.get(k).clusterData.size() && 50 >= clusteredData.get(k).clusterData.size()) {
                circleRadius = 300 + weightForCircle;  // 0.6Km
            } else if(50 < clusteredData.get(k).clusterData.size() && 100 >= clusteredData.get(k).clusterData.size()) {
                circleRadius = 500 + weightForCircle;  // 0.9Km
            } else if(100 < clusteredData.get(k).clusterData.size() && 150 >= clusteredData.get(k).clusterData.size()) {
                circleRadius = 700 + weightForCircle;  // 1.2Km
            } else if(150 < clusteredData.get(k).clusterData.size()) {
                circleRadius = 900 + weightForCircle;  // 1.5Km
            }
*/
/*
            if(4 >= currentZoomLevel) {
                circleRadius = 2000;    // 3Km
            } else if(4 < currentZoomLevel && 5 >= currentZoomLevel) {
                circleRadius = 5000;    // 8Km
            } else if(5 < currentZoomLevel) {
                circleRadius = 15000;    // 12Km
            }
*/
            if(currentZoomLevel2 == 4) {
                circleRadius = 2000 / 2;    // 12Km
            } else if(currentZoomLevel2 == 5) {
                circleRadius = 3500;    // 12Km
            } else {
                circleRadius = 10000;
            }

            MapCircle circle = new MapCircle(
                    MapPoint.mapPointWithGeoCoord(clusteredData.get(k).clusterData.get(0).getLatitude(), clusteredData.get(k).clusterData.get(0).getLongitude()), // center
                    circleRadius, // radius
                    // Color.argb(128, 255, 0, 0), // strokeColor
                    Color.argb(0, 0, 0, 0), // strokeColor
                    Color.argb(128, 237, 31, 36) // fillColor
                    // Color.argb(128, 255, 218, 87) // fillColor
            );
            mMapView.addCircle(circle);

            mCustomMarker = new MapPOIItem();
            mCustomMarker.setItemName(Integer.toString(k));
            mCustomMarker.setTag(k);
            mCustomMarker.setMapPoint(MapPoint.mapPointWithGeoCoord(clusteredData.get(k).clusterData.get(0).getLatitude(), clusteredData.get(k).clusterData.get(0).getLongitude()));
            mCustomMarker.setMarkerType(MapPOIItem.MarkerType.CustomImage);

            // https://devtalk.kakao.com/t/android-mapview-custom-view/46225/3
            LayoutInflater inflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View inflatedFrame = inflater.inflate(R.layout.view_map_bb_b, null);

            ((TextView)inflatedFrame.findViewById(R.id.view_m_b_tv)).setText(Integer.toString(clusteredData.get(k).clusterData.size()));
            ((TextView)inflatedFrame.findViewById(R.id.view_m_b_tv)).setTextSize(20);
            ((TextView)inflatedFrame.findViewById(R.id.view_m_b_tv)).setTextColor(Color.WHITE);
            // ((TextView)inflatedFrame.findViewById(R.id.view_m_b_tv)).setTextColor(Color.BLACK);

            Bitmap bitmap = UtilManager.createBitmapFromView(inflatedFrame.findViewById(R.id.view_m_b));
            mCustomMarker.setCustomImageBitmap(bitmap);
            mCustomMarker.setCustomImageAnchor(0.5f, 0.5f);
            mCustomMarker.setShowCalloutBalloonOnTouch(false);

            mMapView.addPOIItem(mCustomMarker);
        }

        preCurrentMode = 4;
    }

    public void runMapProcessWithParam(int division, int type, int op, int fee) {
        mMapView.removeAllPOIItems();   // 맵 초기화

        mMapView.setCalloutBalloonAdapter(new DaumMapManager.CustomCalloutBalloonAdapter());
        createCustomMarkerWithParam(mMapView, division, type, op, fee);
        showAll();
    }

    public void runMapProcessWithFee(int fee, boolean fromFAB) {
        // 클러스터링 모드이고 줌레벨이 4이상이면 현재기기위치로 이동한다
        if(fromFAB && turnOnCluster && (4 <= currentZoomLevel ) ) {
            // mMapView.setZoomLevel(3, true);
            mMapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOnWithoutHeadingWithoutMapMoving);
            mMapView.setMapCenterPointAndZoomLevel(MapPoint.mapPointWithGeoCoord(myLocPoint.getLatitude(), myLocPoint.getLongitude()), 2, false);
        }

        turnOnCluster = false;
        mMapView.removeAllCircles();
        mMapView.removeAllPOIItems();   // 맵 초기화
        if (showSearchedMarker) {
            setDefaultMarker();    // 맵의 중심에 마커 찍기
        }
        mMapView.setPOIItemEventListener(piel);

        mMapView.setCalloutBalloonAdapter(new DaumMapManager.CustomCalloutBalloonAdapter());
        createCustomMarkerWithFee(mMapView, fee);
        preCurrentMode = fee;
        // showAll();
    }

    public void runMapProcessWithFavorites(String[] favoritesVal, boolean fromFAB) {
        // 클러스터링 모드이고 줌레벨이 4이상이면 현재기기위치로 이동한다
        if(fromFAB && turnOnCluster && (4 <= currentZoomLevel ) ) {
            // mMapView.setZoomLevel(3, true);
            mMapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOnWithoutHeadingWithoutMapMoving);
            mMapView.setMapCenterPointAndZoomLevel(MapPoint.mapPointWithGeoCoord(myLocPoint.getLatitude(), myLocPoint.getLongitude()), 2, false);
        }

        turnOnCluster = false;
        mMapView.removeAllCircles();
        mMapView.removeAllPOIItems();   // 맵 초기화
        if (showSearchedMarker) {
            setDefaultMarker();    // 맵의 중심에 마커 찍기
        }
        mMapView.setPOIItemEventListener(piel);

        mMapView.setCalloutBalloonAdapter(new DaumMapManager.CustomCalloutBalloonAdapter());
        createCustomMarkerWithFavorites(mMapView, favoritesVal);
        preCurrentMode = 3;
        // showAll();
    }

    public void runMapProcessWithFavoritesAtFirst(String[] favoritesVal) {
        turnOnCluster = false;
        mMapView.removeAllCircles();
        mMapView.removeAllPOIItems();   // 맵 초기화
        if (showSearchedMarker) {
            setDefaultMarker();    // 맵의 중심에 마커 찍기
        }
        mMapView.setPOIItemEventListener(piel);

        mMapView.setCalloutBalloonAdapter(new DaumMapManager.CustomCalloutBalloonAdapter());
        createCustomMarkerWithFavoritesAtFirst(mMapView, favoritesVal);
        preCurrentMode = 3;
    }

    private void createCustomMarker(MapView mapView) {
        // 1. PZDataManager에서 SQLite에 접속하여 모든 주차장정보를 얻음.
        PZDataManager pzdm = new PZDataManager(null);
        pzData = pzdm.getAllPZData();

        if(0 == pzData.size()) {
            Toast.makeText(ctx, "내부DB에 데이터가 없습니다", Toast.LENGTH_SHORT).show();
            return;
        }

        // 2. 말풍선 뿌리기
        for(int i = 0; i < pzData.size(); i++) {
            mCustomMarker = new MapPOIItem();
            mCustomMarker.setItemName(pzData.get(i).name);
            mCustomMarker.setTag(i);
            mCustomMarker.setMapPoint(MapPoint.mapPointWithGeoCoord(pzData.get(i).loc.getLatitude(), pzData.get(i).loc.getLongitude()));
            mCustomMarker.setMarkerType(MapPOIItem.MarkerType.CustomImage);

            // String feeVal = pzData.get(i).add_term.fee;
            String feeVal = pzData.get(i).park_base.fee;
            if("0".equals(feeVal)) {
                // feeVal = "무료";
                mCustomMarker.setCustomImageResourceId(R.drawable.mk_free);
            } else if("-1".equals(feeVal)) {
                // feeVal = "미등록";
                mCustomMarker.setCustomImageResourceId(R.drawable.mk_null);
            } else {
                switch(feeVal) {
                    case "100" :
                        mCustomMarker.setCustomImageResourceId(R.drawable.mk_100);
                        break;
                    case "200" :
                        mCustomMarker.setCustomImageResourceId(R.drawable.mk_200);
                        break;
                    case "300" :
                        mCustomMarker.setCustomImageResourceId(R.drawable.mk_300);
                        break;
                    case "400" :
                        mCustomMarker.setCustomImageResourceId(R.drawable.mk_400);
                        break;
                    case "500" :
                        mCustomMarker.setCustomImageResourceId(R.drawable.mk_500);
                        break;
                    case "600" :
                        mCustomMarker.setCustomImageResourceId(R.drawable.mk_600);
                        break;
                    case "700" :
                        mCustomMarker.setCustomImageResourceId(R.drawable.mk_700);
                        break;
                    case "800" :
                        mCustomMarker.setCustomImageResourceId(R.drawable.mk_800);
                        break;
                    case "900" :
                        mCustomMarker.setCustomImageResourceId(R.drawable.mk_900);
                        break;
                    case "1000" :
                        mCustomMarker.setCustomImageResourceId(R.drawable.mk_1000);
                        break;
                    case "1100" :
                        mCustomMarker.setCustomImageResourceId(R.drawable.mk_1100);
                        break;
                    case "1200" :
                        mCustomMarker.setCustomImageResourceId(R.drawable.mk_1200);
                        break;
                    case "1300" :
                        mCustomMarker.setCustomImageResourceId(R.drawable.mk_1300);
                        break;
                    case "1400" :
                        mCustomMarker.setCustomImageResourceId(R.drawable.mk_1400);
                        break;
                    case "1500" :
                        mCustomMarker.setCustomImageResourceId(R.drawable.mk_1500);
                        break;
                    case "1600" :
                        mCustomMarker.setCustomImageResourceId(R.drawable.mk_1600);
                        break;
                    case "1700" :
                        mCustomMarker.setCustomImageResourceId(R.drawable.mk_1700);
                        break;
                    case "1800" :
                        mCustomMarker.setCustomImageResourceId(R.drawable.mk_1800);
                        break;
                    case "1900" :
                        mCustomMarker.setCustomImageResourceId(R.drawable.mk_1900);
                        break;
                    case "2000" :
                        mCustomMarker.setCustomImageResourceId(R.drawable.mk_2000);
                        break;
                }
            }

            mCustomMarker.setCustomImageAutoscale(false);
            mCustomMarker.setCustomImageAnchor(0.5f, 0.7f);
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

        // 2. 말풍선 뿌리기
        for(int i = 0; i < pzData.size(); i++) {
            mCustomMarker = new MapPOIItem();
            mCustomMarker.setItemName(pzData.get(i).name);
            mCustomMarker.setTag(i);
            mCustomMarker.setMapPoint(MapPoint.mapPointWithGeoCoord(pzData.get(i).loc.getLatitude(), pzData.get(i).loc.getLongitude()));
            mCustomMarker.setMarkerType(MapPOIItem.MarkerType.CustomImage);

            // String feeVal = pzData.get(i).add_term.fee;
            String feeVal = pzData.get(i).park_base.fee;
            if("0".equals(feeVal)) {
                // feeVal = "무료";
                mCustomMarker.setCustomImageResourceId(R.drawable.mk_free);
            } else if("-1".equals(feeVal)) {
                // feeVal = "미등록";
                mCustomMarker.setCustomImageResourceId(R.drawable.mk_null);
            } else {
                switch(feeVal) {
                    case "100" :
                        mCustomMarker.setCustomImageResourceId(R.drawable.mk_100);
                        break;
                    case "200" :
                        mCustomMarker.setCustomImageResourceId(R.drawable.mk_200);
                        break;
                    case "300" :
                        mCustomMarker.setCustomImageResourceId(R.drawable.mk_300);
                        break;
                    case "400" :
                        mCustomMarker.setCustomImageResourceId(R.drawable.mk_400);
                        break;
                    case "500" :
                        mCustomMarker.setCustomImageResourceId(R.drawable.mk_500);
                        break;
                    case "600" :
                        mCustomMarker.setCustomImageResourceId(R.drawable.mk_600);
                        break;
                    case "700" :
                        mCustomMarker.setCustomImageResourceId(R.drawable.mk_700);
                        break;
                    case "800" :
                        mCustomMarker.setCustomImageResourceId(R.drawable.mk_800);
                        break;
                    case "900" :
                        mCustomMarker.setCustomImageResourceId(R.drawable.mk_900);
                        break;
                    case "1000" :
                        mCustomMarker.setCustomImageResourceId(R.drawable.mk_1000);
                        break;
                    case "1100" :
                        mCustomMarker.setCustomImageResourceId(R.drawable.mk_1100);
                        break;
                    case "1200" :
                        mCustomMarker.setCustomImageResourceId(R.drawable.mk_1200);
                        break;
                    case "1300" :
                        mCustomMarker.setCustomImageResourceId(R.drawable.mk_1300);
                        break;
                    case "1400" :
                        mCustomMarker.setCustomImageResourceId(R.drawable.mk_1400);
                        break;
                    case "1500" :
                        mCustomMarker.setCustomImageResourceId(R.drawable.mk_1500);
                        break;
                    case "1600" :
                        mCustomMarker.setCustomImageResourceId(R.drawable.mk_1600);
                        break;
                    case "1700" :
                        mCustomMarker.setCustomImageResourceId(R.drawable.mk_1700);
                        break;
                    case "1800" :
                        mCustomMarker.setCustomImageResourceId(R.drawable.mk_1800);
                        break;
                    case "1900" :
                        mCustomMarker.setCustomImageResourceId(R.drawable.mk_1900);
                        break;
                    case "2000" :
                        mCustomMarker.setCustomImageResourceId(R.drawable.mk_2000);
                        break;
                }
            }
            // mCustomMarker.setCustomImageResourceId(R.drawable.mk_blank);

            mCustomMarker.setCustomImageAutoscale(false);
            mCustomMarker.setCustomImageAnchor(0.5f, 0.7f);
            mCustomMarker.setShowCalloutBalloonOnTouch(false);

            mapView.addPOIItem(mCustomMarker);
        }

        mapView.selectPOIItem(mCustomMarker, true);
    }

    private void createCustomMarkerWithFee(MapView mapView, int fee) {
        // 1. PZDataManager에서 SQLite에 접속하여 특정 요금정보를 가진 주차장정보를 얻음.
        PZDataManager pzdm = new PZDataManager(null);
        pzData = pzdm.getPZDataWithFee(fee);

        // 2. 말풍선 뿌리기
        for(int i = 0; i < pzData.size(); i++) {
            mCustomMarker = new MapPOIItem();
            mCustomMarker.setItemName(pzData.get(i).name);
            mCustomMarker.setTag(i);
            mCustomMarker.setMapPoint(MapPoint.mapPointWithGeoCoord(pzData.get(i).loc.getLatitude(), pzData.get(i).loc.getLongitude()));
            mCustomMarker.setMarkerType(MapPOIItem.MarkerType.CustomImage);

            // String feeVal = pzData.get(i).add_term.fee;
            String feeVal = pzData.get(i).park_base.fee;
            if("0".equals(feeVal)) {
                // feeVal = "무료";
                mCustomMarker.setCustomImageResourceId(R.drawable.mk_free);
            } else if("-1".equals(feeVal)) {
                // feeVal = "미등록";
                mCustomMarker.setCustomImageResourceId(R.drawable.mk_null);
            } else {
                switch(feeVal) {
                    case "100" :
                        mCustomMarker.setCustomImageResourceId(R.drawable.mk_100);
                        break;
                    case "200" :
                        mCustomMarker.setCustomImageResourceId(R.drawable.mk_200);
                        break;
                    case "300" :
                        mCustomMarker.setCustomImageResourceId(R.drawable.mk_300);
                        break;
                    case "400" :
                        mCustomMarker.setCustomImageResourceId(R.drawable.mk_400);
                        break;
                    case "500" :
                        mCustomMarker.setCustomImageResourceId(R.drawable.mk_500);
                        break;
                    case "600" :
                        mCustomMarker.setCustomImageResourceId(R.drawable.mk_600);
                        break;
                    case "700" :
                        mCustomMarker.setCustomImageResourceId(R.drawable.mk_700);
                        break;
                    case "800" :
                        mCustomMarker.setCustomImageResourceId(R.drawable.mk_800);
                        break;
                    case "900" :
                        mCustomMarker.setCustomImageResourceId(R.drawable.mk_900);
                        break;
                    case "1000" :
                        mCustomMarker.setCustomImageResourceId(R.drawable.mk_1000);
                        break;
                    case "1100" :
                        mCustomMarker.setCustomImageResourceId(R.drawable.mk_1100);
                        break;
                    case "1200" :
                        mCustomMarker.setCustomImageResourceId(R.drawable.mk_1200);
                        break;
                    case "1300" :
                        mCustomMarker.setCustomImageResourceId(R.drawable.mk_1300);
                        break;
                    case "1400" :
                        mCustomMarker.setCustomImageResourceId(R.drawable.mk_1400);
                        break;
                    case "1500" :
                        mCustomMarker.setCustomImageResourceId(R.drawable.mk_1500);
                        break;
                    case "1600" :
                        mCustomMarker.setCustomImageResourceId(R.drawable.mk_1600);
                        break;
                    case "1700" :
                        mCustomMarker.setCustomImageResourceId(R.drawable.mk_1700);
                        break;
                    case "1800" :
                        mCustomMarker.setCustomImageResourceId(R.drawable.mk_1800);
                        break;
                    case "1900" :
                        mCustomMarker.setCustomImageResourceId(R.drawable.mk_1900);
                        break;
                    case "2000" :
                        mCustomMarker.setCustomImageResourceId(R.drawable.mk_2000);
                        break;
                }
            }
            // mCustomMarker.setCustomImageResourceId(R.drawable.mk_blank);

            mCustomMarker.setCustomImageAutoscale(false);
            mCustomMarker.setCustomImageAnchor(0.5f, 0.7f);
            mCustomMarker.setShowCalloutBalloonOnTouch(false);

            mapView.addPOIItem(mCustomMarker);
        }

        mapView.selectPOIItem(mCustomMarker, true);
    }

    private void createCustomMarkerWithFavorites(MapView mapView, String[] favoritesVal) {
        // 1. PZDataManager에서 SQLite에 접속하여 모든 주차장정보를 얻음.
        PZDataManager pzdm = new PZDataManager(null);
        pzData = pzdm.getPZDataWithNos(favoritesVal);

        // 2. 말풍선 뿌리기
        for(int i = 0; i < pzData.size(); i++) {
            mCustomMarker = new MapPOIItem();
            mCustomMarker.setItemName(pzData.get(i).name);
            mCustomMarker.setTag(i);
            mCustomMarker.setMapPoint(MapPoint.mapPointWithGeoCoord(pzData.get(i).loc.getLatitude(), pzData.get(i).loc.getLongitude()));
            mCustomMarker.setMarkerType(MapPOIItem.MarkerType.CustomImage);

            // String feeVal = pzData.get(i).add_term.fee;
            String feeVal = pzData.get(i).park_base.fee;
            if("0".equals(feeVal)) {
                // feeVal = "무료";
                mCustomMarker.setCustomImageResourceId(R.drawable.mk_free);
            } else if("-1".equals(feeVal)) {
                // feeVal = "미등록";
                mCustomMarker.setCustomImageResourceId(R.drawable.mk_null);
            } else {
                switch(feeVal) {
                    case "100" :
                        mCustomMarker.setCustomImageResourceId(R.drawable.mk_100);
                        break;
                    case "200" :
                        mCustomMarker.setCustomImageResourceId(R.drawable.mk_200);
                        break;
                    case "300" :
                        mCustomMarker.setCustomImageResourceId(R.drawable.mk_300);
                        break;
                    case "400" :
                        mCustomMarker.setCustomImageResourceId(R.drawable.mk_400);
                        break;
                    case "500" :
                        mCustomMarker.setCustomImageResourceId(R.drawable.mk_500);
                        break;
                    case "600" :
                        mCustomMarker.setCustomImageResourceId(R.drawable.mk_600);
                        break;
                    case "700" :
                        mCustomMarker.setCustomImageResourceId(R.drawable.mk_700);
                        break;
                    case "800" :
                        mCustomMarker.setCustomImageResourceId(R.drawable.mk_800);
                        break;
                    case "900" :
                        mCustomMarker.setCustomImageResourceId(R.drawable.mk_900);
                        break;
                    case "1000" :
                        mCustomMarker.setCustomImageResourceId(R.drawable.mk_1000);
                        break;
                    case "1100" :
                        mCustomMarker.setCustomImageResourceId(R.drawable.mk_1100);
                        break;
                    case "1200" :
                        mCustomMarker.setCustomImageResourceId(R.drawable.mk_1200);
                        break;
                    case "1300" :
                        mCustomMarker.setCustomImageResourceId(R.drawable.mk_1300);
                        break;
                    case "1400" :
                        mCustomMarker.setCustomImageResourceId(R.drawable.mk_1400);
                        break;
                    case "1500" :
                        mCustomMarker.setCustomImageResourceId(R.drawable.mk_1500);
                        break;
                    case "1600" :
                        mCustomMarker.setCustomImageResourceId(R.drawable.mk_1600);
                        break;
                    case "1700" :
                        mCustomMarker.setCustomImageResourceId(R.drawable.mk_1700);
                        break;
                    case "1800" :
                        mCustomMarker.setCustomImageResourceId(R.drawable.mk_1800);
                        break;
                    case "1900" :
                        mCustomMarker.setCustomImageResourceId(R.drawable.mk_1900);
                        break;
                    case "2000" :
                        mCustomMarker.setCustomImageResourceId(R.drawable.mk_2000);
                        break;
                }
            }
            // mCustomMarker.setCustomImageResourceId(R.drawable.mk_blank);

            mCustomMarker.setCustomImageAutoscale(false);
            mCustomMarker.setCustomImageAnchor(0.5f, 0.7f);
            mCustomMarker.setShowCalloutBalloonOnTouch(false);

            mapView.addPOIItem(mCustomMarker);
        }

        mapView.selectPOIItem(mCustomMarker, true);
    }

    private void createCustomMarkerWithFavoritesAtFirst(MapView mapView, String[] favoritesVal) {
        // 1. PZDataManager에서 SQLite에 접속하여 모든 주차장정보를 얻음.
        PZDataManager pzdm = new PZDataManager(null);
        pzData = pzdm.getPZDataWithNos(favoritesVal);

        // 2. 말풍선 뿌리기
        for(int i = 0; i < pzData.size(); i++) {
            mCustomMarker = new MapPOIItem();
            mCustomMarker.setItemName(pzData.get(i).name);
            mCustomMarker.setTag(i);
            mCustomMarker.setMapPoint(MapPoint.mapPointWithGeoCoord(pzData.get(i).loc.getLatitude(), pzData.get(i).loc.getLongitude()));
            mCustomMarker.setMarkerType(MapPOIItem.MarkerType.CustomImage);

            // String feeVal = pzData.get(i).add_term.fee;
            String feeVal = pzData.get(i).park_base.fee;
            if("0".equals(feeVal)) {
                // feeVal = "무료";
                mCustomMarker.setCustomImageResourceId(R.drawable.mk_free);
            } else if("-1".equals(feeVal)) {
                // feeVal = "미등록";
                mCustomMarker.setCustomImageResourceId(R.drawable.mk_null);
            } else {
                switch(feeVal) {
                    case "100" :
                        mCustomMarker.setCustomImageResourceId(R.drawable.mk_100);
                        break;
                    case "200" :
                        mCustomMarker.setCustomImageResourceId(R.drawable.mk_200);
                        break;
                    case "300" :
                        mCustomMarker.setCustomImageResourceId(R.drawable.mk_300);
                        break;
                    case "400" :
                        mCustomMarker.setCustomImageResourceId(R.drawable.mk_400);
                        break;
                    case "500" :
                        mCustomMarker.setCustomImageResourceId(R.drawable.mk_500);
                        break;
                    case "600" :
                        mCustomMarker.setCustomImageResourceId(R.drawable.mk_600);
                        break;
                    case "700" :
                        mCustomMarker.setCustomImageResourceId(R.drawable.mk_700);
                        break;
                    case "800" :
                        mCustomMarker.setCustomImageResourceId(R.drawable.mk_800);
                        break;
                    case "900" :
                        mCustomMarker.setCustomImageResourceId(R.drawable.mk_900);
                        break;
                    case "1000" :
                        mCustomMarker.setCustomImageResourceId(R.drawable.mk_1000);
                        break;
                    case "1100" :
                        mCustomMarker.setCustomImageResourceId(R.drawable.mk_1100);
                        break;
                    case "1200" :
                        mCustomMarker.setCustomImageResourceId(R.drawable.mk_1200);
                        break;
                    case "1300" :
                        mCustomMarker.setCustomImageResourceId(R.drawable.mk_1300);
                        break;
                    case "1400" :
                        mCustomMarker.setCustomImageResourceId(R.drawable.mk_1400);
                        break;
                    case "1500" :
                        mCustomMarker.setCustomImageResourceId(R.drawable.mk_1500);
                        break;
                    case "1600" :
                        mCustomMarker.setCustomImageResourceId(R.drawable.mk_1600);
                        break;
                    case "1700" :
                        mCustomMarker.setCustomImageResourceId(R.drawable.mk_1700);
                        break;
                    case "1800" :
                        mCustomMarker.setCustomImageResourceId(R.drawable.mk_1800);
                        break;
                    case "1900" :
                        mCustomMarker.setCustomImageResourceId(R.drawable.mk_1900);
                        break;
                    case "2000" :
                        mCustomMarker.setCustomImageResourceId(R.drawable.mk_2000);
                        break;
                }
            }
            // mCustomMarker.setCustomImageResourceId(R.drawable.mk_blank);

            mCustomMarker.setCustomImageAutoscale(false);
            mCustomMarker.setCustomImageAnchor(0.5f, 0.7f);
            mCustomMarker.setShowCalloutBalloonOnTouch(false);

            mapView.addPOIItem(mCustomMarker);
        }

        // 3. mapPointBounds 구하기(좌하단 지점과 우상단 지점)
        Double dbLeftBottomLat = 0.0;
        Double dbLeftBottomLng = 0.0;
        Double dbRightTopLat = 0.0;
        Double dbRightTopLng = 0.0;

        for(int j = 0; j < pzData.size(); j++) {
            if (0 == j) {
                dbLeftBottomLat = pzData.get(j).loc.getLatitude();
                dbLeftBottomLng = pzData.get(j).loc.getLongitude();
            }

            if (dbLeftBottomLat > pzData.get(j).loc.getLatitude()) {
                dbLeftBottomLat = pzData.get(j).loc.getLatitude();
            }

            if (dbLeftBottomLng > pzData.get(j).loc.getLongitude()) {
                dbLeftBottomLng = pzData.get(j).loc.getLongitude();
            }
        }
        MapPoint mpLeftBottom = MapPoint.mapPointWithGeoCoord(dbLeftBottomLat, dbLeftBottomLng);

        for(int k = 0; k < pzData.size(); k++) {
            if (0 == k) {
                dbRightTopLat = pzData.get(k).loc.getLatitude();
                dbRightTopLng = pzData.get(k).loc.getLongitude();
            }

            if (dbRightTopLat < pzData.get(k).loc.getLatitude()) {
                dbRightTopLat = pzData.get(k).loc.getLatitude();
            }

            if (dbRightTopLng < pzData.get(k).loc.getLongitude()) {
                dbRightTopLng = pzData.get(k).loc.getLongitude();
            }
        }
        MapPoint mpRightTop = MapPoint.mapPointWithGeoCoord(dbRightTopLat, dbRightTopLng);

        MapPointBounds bounds = new MapPointBounds(mpLeftBottom, mpRightTop);
        int padding = 40;
        mapView.moveCamera(CameraUpdateFactory.newMapPointBounds(bounds, padding));

        // mapView.selectPOIItem(mCustomMarker, true);
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
        mMapView.setCurrentLocationTrackingMode(null);
        mMapView.setMapCenterPointAndZoomLevel(MapPoint.mapPointWithGeoCoord(lat,lng), 1, true);
    }

    // 기본 마커 GPS 설정
    public void setDefaultMarkerLoc(boolean showDefaultMarker, double lat, double lng, String markerName) {
        searchedPoint.setLatitude(lat);
        searchedPoint.setLongitude(lng);
        showSearchedMarker = showDefaultMarker;
        defaultMarkerName = markerName;
    }

    // 기본 마커 찍기
    public void setDefaultMarker() {
        mMapView.removePOIItem(defaultMarker);  // 이전에 그린 기본마커를 맵에서 제거한다.

        defaultMarker.setItemName(defaultMarkerName);
        defaultMarker.setTag(7777);
        defaultMarker.setMapPoint(MapPoint.mapPointWithGeoCoord(searchedPoint.getLatitude(),searchedPoint.getLongitude()));
        defaultMarker.setMarkerType(MapPOIItem.MarkerType.BluePin); // 기본으로 제공하는 BluePin 마커 모양.
        defaultMarker.setSelectedMarkerType(MapPOIItem.MarkerType.RedPin); // 마커를 클릭했을때, 기본으로 제공하는 RedPin 마커 모양.

        mMapView.addPOIItem(defaultMarker);
    }

    // 모든 마커 삭제
    public void destroy() {
        mMapView.removePOIItem(defaultMarker);  // 이전에 그린 기본마커를 맵에서 제거한다.
        mMapView.removeAllCircles();    // 클러스터링 초기화
        mMapView.removeAllPOIItems();   // 맵 초기화
    }

    // 맵이 기기 위치로 돌아옴
    public void setMapCenterWithMyLoc() {
        mMapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOnWithoutHeadingWithoutMapMoving);
        mMapView.setMapCenterPointAndZoomLevel(MapPoint.mapPointWithGeoCoord(myLocPoint.getLatitude(), myLocPoint.getLongitude()), 2, false);
    }

    private MapPoint getBottomLeftMapPoint() {
        double bottom = 0;
        double left = 0;

        for(int i = 0; i < pzData.size(); i++) {
            if(bottom > pzData.get(i).loc.getLongitude()) {
                bottom = pzData.get(i).loc.getLongitude();
            } else if(0 == bottom) {
                bottom = pzData.get(i).loc.getLongitude();
            }
        }

        for(int i = 0; i < pzData.size(); i++) {
            if(left > pzData.get(i).loc.getLatitude()) {
                left = pzData.get(i).loc.getLatitude();
            } else if(0 == left) {
                left = pzData.get(i).loc.getLatitude();
            }
        }

        return MapPoint.mapPointWithGeoCoord(left, bottom);
    }

    private MapPoint getTopRightMapPoint() {
        double top = 0;
        double right = 0;

        for(int i = 0; i < pzData.size(); i++) {
            if(top < pzData.get(i).loc.getLongitude()) {
                top = pzData.get(i).loc.getLongitude();
            } else if(0 == top) {
                top = pzData.get(i).loc.getLongitude();
            }
        }

        for(int i = 0; i < pzData.size(); i++) {
            if(right < pzData.get(i).loc.getLatitude()) {
                right = pzData.get(i).loc.getLatitude();
            } else if(0 == right) {
                right = pzData.get(i).loc.getLatitude();
            }
        }

        return MapPoint.mapPointWithGeoCoord(right, top);
    }

    public void setMode(int modeVal) {
        currentMode = modeVal;
    }

    public void setFavorites(String[] favoritesVal) {
        favorites = favoritesVal;
    }
}