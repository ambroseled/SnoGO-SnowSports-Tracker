package seng202.team5.Model;

import seng202.team5.Model.Activity;
import seng202.team5.Model.DataPoint;
import seng202.team5.Model.DataSet;

import java.util.ArrayList;

public class DataAnalyser {

    /**
     * Performs analysis on a passed activity.
     * @param activity The activity to analyse.
     */
    public void analyseActivity(Activity activity) {
        DataSet dataSet = activity.getDataSet();
        ArrayList<DataPoint> dataPoints = dataSet.getDataPoints();
        markActive(dataPoints);
        DataPoint previous = dataPoints.get(0);
        previous.setSpeed(0);
        previous.setDistance(0);
        for (int i = 1; i < dataPoints.size(); i++) {
            DataPoint current = dataPoints.get(i);
            if (current.isActive()) {
                appendDistance(current, previous);
                appendSpeed(current, previous);
            } else {
                current.setSpeed(0);
                current.setDistance(previous.getDistance());
            }
            previous = dataPoints.get(i);
        }
        dataSet.setVerticalDistance(calcVertical(dataSet));
        dataSet.setTotalDistance(previous.getDistance());
        dataSet.setAvgHeartRate(calcAvgHeart(dataSet));
        dataSet.setTopSpeed(topSpeed(dataSet));
    }


    /**
     * Looping over all DataPoints in a passed list and using checkInactive as
     * a helper function to mark each DataPoint as active or inactive.
     * @param dataPoints A list of the DataPoints to mark active or inactive.
     */
    private void markActive(ArrayList<DataPoint> dataPoints) {
        for (int i = 0; i < dataPoints.size(); i++) {
            String active = checkInactive(i, dataPoints);
            if (active.equals("Inactive")) {
                // Loop until no alt increase
                DataPoint previous = dataPoints.get(i++);
                previous.setActive(false);
                double altChange;
                for (; i < dataPoints.size(); i++) {
                    altChange = oneAlt(previous.getElevation(), dataPoints.get(i).getElevation());
                    if (altChange > 0) {
                        // User is no longer on a lift or moving up
                        break;
                    } else {
                        dataPoints.get(i).setActive(false);
                    }
                }
            } else {
                dataPoints.get(i).setActive(true);
            }
        }
    }


    /**
     * Checks if a passed DataPoint is active or not. The check is done by
     * calculating the change in vertical distance over 1 minute. If this change
     * is negative then it is assumed the user is on a lift or hiking hence "Inactive"
     * is returned. If the change is less than 1 it is also assumed that the user is
     * inactive either being in a cafe or standing still so "Inactive is also returned.
     * If neither of these conditions are meet then it is assumed the user is active and
     * hence "active" is returned.
     * @param index An index pointing to the DataPoint to check the activity of.
     * @param dataPoints An arrayList of all the DataPoints in the activity.
     * @return A string holding if the passed DataPoint is active or not.
     */
    private String checkInactive(int index, ArrayList<DataPoint> dataPoints) {
        double startLat = dataPoints.get(index).getLatitude();
        double startLong = dataPoints.get(index).getLongitude();
        double startAlt = dataPoints.get(index).getElevation();
        double[] start = {startLong, startLat, startAlt};

        double endLat = dataPoints.get(index + 60).getLatitude();
        double endLong = dataPoints.get(index + 60).getLongitude();
        double endAlt = dataPoints.get(index + 60).getElevation();
        double[] end = {endLong, endLat, endAlt};


        double vertChange = oneAlt(startAlt, endAlt);

        if (vertChange < 1) {
            return "Inactive";
        } else {
            return "Active";
        }
    }


    /**
     * Calculating the cartesian product of a passed location point.
     * @param alt The altitude
     * @param longitude The longitude value
     * @param lat The latitude value.
     * @return A double array holding the cartesian product.
     */
    private double[] cartesian(double alt, double longitude, double lat) {
        double x = alt * Math.cos(Math.toRadians(lat)) * Math.sin(Math.toRadians(longitude));
        double y = alt * Math.sin(Math.toRadians(lat));
        double z = alt * Math.cos(Math.toRadians(lat)) * Math.cos(Math.toRadians(longitude));
        double[] cart = {x, y, z};
        return cart;
    }


    /**
     * Calculates the distance traveled between two data points,
     * in the format (longitude, latitude, altitude).
     * @param point1 First location point.
     * @param point2 Second location point.
     * @return A double holding the distance bewtwwen teh two points.
     */
    private double oneDist(double[] point1, double[] point2) {
        double[] cart1 = cartesian(point1[2], point1[0], point1[1]);
        double[] cart2 = cartesian(point2[2], point2[0], point2[1]);
        double arg1 = Math.pow((cart2[0] - cart1[0]), 2);
        double arg2 = Math.pow((cart2[1] - cart1[1]), 2);
        double arg3 = Math.pow((cart2[2] - cart1[2]), 2);
        double distance = Math.sqrt(arg1 + arg2 + arg3);
        return distance;
    }


    /**
     * Calculates the total distance traveled at each data point in the passed activity * and appends
     * this distance value to the data point.
     * @param current The data point to append a distance to.
     * @param previous The previous data point.
     */
    private void appendDistance(DataPoint current, DataPoint previous) {
        double point1[] = {current.getLongitude(), current.getLatitude(), current.getElevation()};
        double point2[] = {previous.getLongitude(), previous.getLatitude(), previous.getElevation()};
        double distance = oneDist(point1, point2);
        current.setDistance(distance);
    }


    /**
     * Calculates the difference in altitude between two altitude values.
     * @param alt1 The first altitude value in meters.
     * @param alt2 The second altitude value in meters.
     * @return A double the difference between the two passed values.
     */
    private double oneAlt(double alt1, double alt2) {
        double diff = alt2 - alt1;
        return diff;
    }


    /**
     * Takes the distance and time values of two data points and calculates the current
     * speed.
     * @param dist1 The current distance of the first data point in meters.
     * @param dist2 The current distance of the second data point in meters.
     * @param time1 The current time of the first data point in seconds.
     * @param time2 The current time of the second data point in seconds.
     * @return A double holding the current speed in m/s.
     */
    private double oneSpeed(double dist1, double dist2, long time1, long time2) {
        // Calculating the change in distance
        double distance = dist2 - dist1;
        // Calculating the change in time
        double time = time2 - time1;
        if (time == 0) {
            // The time change is zero so the speed is zero
            return 0;
        } else if (distance == 0) {
            // The distance change is zero so the speed is zero
            return 0;
        } else {
            // The speed is above zero and will be calculated
            double speed = distance / time;
            return speed;
        }
    }


    /**
     * Calculates the current speed at a passed DataPoint and appends the speed
     * to the data point.
     * @param current The DataPoint to calculate the speed of.
     * @param previous The previous DataPoint.
     */
    private void appendSpeed(DataPoint current, DataPoint previous) {
        double distance1 = current.getDistance();
        long time1 = current.getDateTime().getTime();
        double distance2 = previous.getDistance();
        long time2 = previous.getDateTime().getTime();

        double speed = oneSpeed(distance1, distance2, time1, time2);
        current.setSpeed(speed);
    }


    /**
     * Calculates the average heart rate from a given array of heart rates.
     * @param dataSet The DataSet of the activity being analysed.
     * @return A double holding the average heart rate found.
     */
    private double calcAvgHeart(DataSet dataSet) {
        ArrayList<DataPoint> dataPoints = dataSet.getDataPoints();
        double avg = 0;
        for (DataPoint point : dataPoints) {
            avg += point.getHeartRate();
        }
        avg = avg/(dataPoints.size());
        return avg;
    }


    /**
     * Calculates the total vertical distance traveled over a given list
     * of altitude values.
     * @param dataSet The DataSet of the activity being analysed.
     * @return The vertical distance traveled.
     */
    private double calcVertical(DataSet dataSet) {
        ArrayList<DataPoint> dataPoints = dataSet.getDataPoints();
        double vertical = 0;
        double previous = dataPoints.get(0).getElevation();
        for(int i = 1; i < dataPoints.size(); i++) {
            if (dataPoints.get(i).isActive()) {
                vertical += oneAlt(previous, dataPoints.get(i).getElevation());
            }
            previous = dataPoints.get(i).getElevation();
        }
        return vertical;
    }


    /**
     * Finds the top speed of a given DataSet.
     * @param dataSet The DataSet to find the top speed for.
     * @return A double holding the top speed.
     */
    private double topSpeed(DataSet dataSet) {
        ArrayList<DataPoint> dataPoints = dataSet.getDataPoints();
        double top = 0;
        for(DataPoint point : dataPoints) {
            double speed = point.getSpeed();
            if (speed > top) {
                top = speed;
            }
        }
        return top;
    }
}
