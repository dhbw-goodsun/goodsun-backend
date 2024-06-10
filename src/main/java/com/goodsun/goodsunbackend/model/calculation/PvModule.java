package com.goodsun.goodsunbackend.model.calculation;

import com.goodsun.goodsunbackend.model.request.DataPoint;
import com.goodsun.goodsunbackend.model.request.PanelObstacleData;
import com.goodsun.goodsunbackend.model.request.SolarPanel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class PvModule {
    private double dcRating;
    private double azimuth;
    private double elevation;
    private TreeMap<Integer, Double> obstacleElevationPerAzimuthDataset;

    public PvModule(double dcRating, double azimuth, double elevation, TreeMap<Integer, Double> obstacleElevationPerAzimuthDataset) {
        this.dcRating = dcRating;
        this.azimuth = azimuth;
        this.elevation = elevation;
        this.obstacleElevationPerAzimuthDataset = obstacleElevationPerAzimuthDataset;
    }


    public static PvModule getPvModule(SolarPanel solarPanel){
        return new PvModule(solarPanel.panelWatts(), solarPanel.panelAzimuth(), solarPanel.panelElevation(), getSingleDatasetForPvModule(solarPanel.panelObstacleDatasets()));
    }

    public static TreeMap<Integer, Double> getSingleDatasetForPvModule(PanelObstacleData[] datasets){
        HashMap<Integer, ArrayList<Double>> combinedDatasets = combineDatasets(datasets);
        HashMap<Integer, Double> averagedDataset = averageDatasets(combinedDatasets);
        TreeMap<Integer, Double> interpolatedDataset = interpolateDataset(averagedDataset);
        return interpolatedDataset;
    }

    private static TreeMap<Integer, Double> interpolateDataset(HashMap<Integer, Double> data) {
        TreeMap<Integer, Double> interpolatedDataset = new TreeMap<>();

        for (int azimuth = 0; azimuth < 360; azimuth++) {
            if (data.containsKey(azimuth)) {
                interpolatedDataset.put(azimuth, data.get(azimuth));
            } else {
                Integer leftAzimuth = null;
                Integer rightAzimuth = null;

                for (int i = azimuth - 1; i >= azimuth - 360; i--) {
                    int wrappedIndex = (i + 360) % 360;
                    if (data.containsKey(wrappedIndex)) {
                        leftAzimuth = wrappedIndex;
                        break;
                    }
                }

                for (int i = azimuth + 1; i <= azimuth + 360; i++) {
                    int wrappedIndex = i % 360;
                    if (data.containsKey(wrappedIndex)) {
                        rightAzimuth = wrappedIndex;
                        break;
                    }
                }

                if (leftAzimuth == null || rightAzimuth == null) continue;

                double leftElevation = data.get(leftAzimuth);
                double rightElevation = data.get(rightAzimuth);
                double interpolatedElevation = interpolateElevation(leftAzimuth, leftElevation, rightAzimuth, rightElevation, azimuth);

                interpolatedDataset.put(azimuth, interpolatedElevation);
            }
        }

        return interpolatedDataset;
    }

    private static double interpolateElevation(int leftAzimuth, double leftElevation, int rightAzimuth, double rightElevation, int targetAzimuth) {
        int distanceLeftRight = (rightAzimuth - leftAzimuth + 360) % 360;
        int distanceTargetLeft = (targetAzimuth - leftAzimuth + 360) % 360;
        double t = (double) distanceTargetLeft / distanceLeftRight;
        return leftElevation + t * (rightElevation - leftElevation);
    }

    private static HashMap<Integer, Double> averageDatasets(HashMap<Integer, ArrayList<Double>> obstacleInfoMulti) {
        HashMap<Integer, Double> obstacleInfo = new HashMap<>();
        for (Map.Entry<Integer, ArrayList<Double>> integerArrayListEntry : obstacleInfoMulti.entrySet()) {
            Integer key = integerArrayListEntry.getKey();
            ArrayList<Double> value = integerArrayListEntry.getValue();
            obstacleInfo.put(key, calculateAverage(value.toArray(new Double[0])));
        }
        return obstacleInfo;
    }

    private static HashMap<Integer, ArrayList<Double>> combineDatasets(PanelObstacleData[] datasets){
        HashMap<Integer, ArrayList<Double>> obstacleInfoMulti = new HashMap<>();
        for (int i = 0; i < datasets.length; i++) {
            PanelObstacleData panelObstacleDataset = datasets[i];
            for (int j = 0; j < panelObstacleDataset.dataPoints().length; j++) {
                DataPoint dataPoint = panelObstacleDataset.dataPoints()[j];
                int azimuth = (int) dataPoint.azimuth();
                if (obstacleInfoMulti.get(azimuth) == null) {
                    obstacleInfoMulti.put(azimuth, new ArrayList<Double>());
                }
                obstacleInfoMulti.get(azimuth).add(dataPoint.elevation());
            }
        }
        return obstacleInfoMulti;
    }


    private static double calculateAverage(Double[] numbers) {
        double sum = 0.0;
        for (double number : numbers) {
            sum += number;
        }
        return sum / numbers.length;
    }

    @Override
    public String toString() {
        return "PvModule{" +
                "dcRating=" + dcRating +
                ", azimuth=" + azimuth +
                ", elevation=" + elevation +
                ", obstacleElevationPerAzimuth=" + obstacleElevationPerAzimuthDataset +
                '}';
    }

    public double getDcRating() {
        return dcRating;
    }

    public double getAzimuth() {
        return azimuth;
    }

    public double getElevation() {
        return elevation;
    }

    public double getShadowingElevationForAzimuth(int azimuth){
        return obstacleElevationPerAzimuthDataset.get(azimuth);
    }

    public void removeShadowing() {
        PanelObstacleData[] panelObstacleDatasets = new PanelObstacleData[]{new PanelObstacleData(1,new DataPoint[]{new DataPoint(0,0)})};
        this.obstacleElevationPerAzimuthDataset = getSingleDatasetForPvModule(panelObstacleDatasets);
    }
}
