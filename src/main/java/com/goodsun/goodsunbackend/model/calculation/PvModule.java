package com.goodsun.goodsunbackend.model.calculation;

import com.goodsun.goodsunbackend.model.request.DataPoint;
import com.goodsun.goodsunbackend.model.request.PanelObstacleData;
import com.goodsun.goodsunbackend.model.request.SolarPanel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

/**
 * The PvModule class represents a photovoltaic module with specific characteristics and data
 * related to its positioning and shadowing effects.
 * @author Jonas Nunnenmacher
 */
public class PvModule {
    private double dcRating;
    private double azimuth;
    private double elevation;
    private TreeMap<Integer, Double> obstacleElevationPerAzimuthDataset;

    /**
     * Constructs a PvModule instance.
     *
     * @param dcRating the direct current rating of the module
     * @param azimuth the azimuth angle of the module
     * @param elevation the elevation angle of the module
     * @param obstacleElevationPerAzimuthDataset the dataset of obstacle elevations per azimuth
     */
    public PvModule(double dcRating, double azimuth, double elevation, TreeMap<Integer, Double> obstacleElevationPerAzimuthDataset) {
        this.dcRating = dcRating;
        this.azimuth = azimuth;
        this.elevation = elevation;
        this.obstacleElevationPerAzimuthDataset = obstacleElevationPerAzimuthDataset;
    }


    /**
     * Creates a PvModule instance from a SolarPanel instance out of the UserData of a request.
     *
     * @param solarPanel the solar panel data
     * @return a new PvModule instance
     */
    public static PvModule getPvModule(SolarPanel solarPanel){
        return new PvModule(solarPanel.panelWatts(), solarPanel.panelAzimuth(), solarPanel.panelElevation(), getSingleDatasetForPvModule(solarPanel.panelObstacleDatasets()));
    }

    /**
     * Combines multiple datasets into a single dataset for a PV module with corrected values.
     *
     * @param datasets an array of PanelObstacleData instances
     * @return a combined and interpolated dataset
     */
    public static TreeMap<Integer, Double> getSingleDatasetForPvModule(PanelObstacleData[] datasets){
        HashMap<Integer, ArrayList<Double>> combinedDatasets = combineDatasets(datasets);
        HashMap<Integer, Double> averagedDataset = averageDatasets(combinedDatasets);
        TreeMap<Integer, Double> interpolatedDataset = interpolateDataset(averagedDataset);
        return interpolatedDataset;
    }

    /**
     * Interpolates missing data points in the dataset.
     *
     * @param data the dataset with possible missing data points
     * @return an interpolated dataset
     */
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

    /**
     * Interpolates the elevation between two azimuth points.
     *
     * @param leftAzimuth the azimuth angle on the left
     * @param leftElevation the elevation angle on the left
     * @param rightAzimuth the azimuth angle on the right
     * @param rightElevation the elevation angle on the right
     * @param targetAzimuth the target azimuth angle
     * @return the interpolated elevation
     */
    private static double interpolateElevation(int leftAzimuth, double leftElevation, int rightAzimuth, double rightElevation, int targetAzimuth) {
        int distanceLeftRight = (rightAzimuth - leftAzimuth + 360) % 360;
        int distanceTargetLeft = (targetAzimuth - leftAzimuth + 360) % 360;
        double t = (double) distanceTargetLeft / distanceLeftRight;
        return leftElevation + t * (rightElevation - leftElevation);
    }

    /**
     * Averages the elevation values for the corresponding azimut values of datasets to create a single dataset.
     *
     * @param obstacleInfoMulti the combined datasets
     * @return the averaged dataset
     */
    private static HashMap<Integer, Double> averageDatasets(HashMap<Integer, ArrayList<Double>> obstacleInfoMulti) {
        HashMap<Integer, Double> obstacleInfo = new HashMap<>();
        for (Map.Entry<Integer, ArrayList<Double>> integerArrayListEntry : obstacleInfoMulti.entrySet()) {
            Integer key = integerArrayListEntry.getKey();
            ArrayList<Double> value = integerArrayListEntry.getValue();
            obstacleInfo.put(key, calculateAverage(value.toArray(new Double[0])));
        }
        return obstacleInfo;
    }

    /**
     * Combines multiple PanelObstacleData datasets into one.
     *
     * @param datasets an array of PanelObstacleData instances
     * @return a combined dataset
     */
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

    /**
     * Calculates the average of an array of doubles.
     *
     * @param numbers the array of doubles
     * @return the average value
     */
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

    /**
     * Gets the shadowing elevation for a given azimuth angle.
     *
     * @param azimuth the azimuth angle
     * @return the shadowing elevation at the given azimuth
     */
    public double getShadowingElevationForAzimuth(int azimuth){
        return obstacleElevationPerAzimuthDataset.get(azimuth);
    }

    /**
     * Removes all shadowing effects by resetting the obstacle elevation dataset.
     */
    public void removeShadowing() {
        PanelObstacleData[] panelObstacleDatasets = new PanelObstacleData[]{new PanelObstacleData(1,new DataPoint[]{new DataPoint(0,0)})};
        this.obstacleElevationPerAzimuthDataset = getSingleDatasetForPvModule(panelObstacleDatasets);
    }
}
