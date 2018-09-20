package seng202.team5.DataManipulation;


import org.junit.BeforeClass;
import org.junit.Test;
import seng202.team5.Model.Activity;
import seng202.team5.Model.DataPoint;
import seng202.team5.Model.DataSet;
import seng202.team5.Model.User;

import java.util.ArrayList;
import java.util.Date;

import static org.junit.Assert.*;

public class DataAnalyserTest {

    private static ArrayList<Activity> activities;
    private static DataAnalyser dataAnalyser = new DataAnalyser();


    /**
     * Setup before all tests. Reads the test activities from the
     * dataAnalysisTests file into the arrayList of activities, activities.
     */
    @BeforeClass
    public static void beforeAll() {
        Date date = new Date();
        User user = new User("Test", 25, 1.8, 75.8, date);
        dataAnalyser.setCurrentUser(user);
        InputDataParser parser = new InputDataParser();
        activities = parser.parseCSVToActivities("TestFiles/dataAnalysisTests.csv");
    }


    /**
     * Testing the markActive() and checkInactive() functions. This test cases
     * passes an activity where all DataPoints are inactive.
     */
    @Test
    public void testAllInactive() {
        Activity activity = activities.get(0);
        // Getting the dataSet out of the activity
        DataSet dataSet = activity.getDataSet();
        // Getting the data points out of the dataSet
        ArrayList<DataPoint> dataPoints = dataSet.getDataPoints();
        int count = 0;
        for (DataPoint point : dataPoints) {
            if (!point.isActive()) {
                count++;
            }
        }
        assertEquals(dataPoints.size(), count);
    }


    /**
     * Testing the markActive() and checkInactive() functions. This test cases
     * passes an activity where all DataPoints are active.
     */
    @Test
    public void testAllActive() {
        Activity activity = activities.get(1);
        // Getting the dataSet out of the activity
        DataSet dataSet = activity.getDataSet();
        // Getting the data points out of the dataSet
        ArrayList<DataPoint> dataPoints = dataSet.getDataPoints();
        int count = 0;
        for (DataPoint point : dataPoints) {
            if (point.isActive()) {
                count++;
            }
        }
        assertEquals(dataPoints.size(), count);
    }


    /**
     * Testing the calcAvgHeart() function. This test cases passes
     * an activity where the heart rate remains constant.
     */
    @Test
    public void testAvgHeartAllSame() {
        Activity activity = activities.get(2);
        // Getting the dataSet out of the activity
        DataSet dataSet = activity.getDataSet();
        assertEquals(132, dataSet.getAvgHeartRate(), 0.0);
    }


    /**
     * Testing the calcAvgHeart() function. This test cases passes
     * an activity where the heart rate varies.
     */
    @Test
    public void testAvgHeartDiff() {
        Activity activity = activities.get(1);
        // Getting the dataSet out of the activity
        double[] rates = {132, 156, 154, 151, 146, 139, 141, 149, 154, 149, 146, 142, 138};
        int average = 0;
        for (int i = 0; i < rates.length; i++){
            average += rates[i];
        }
        average = average / rates.length;
        DataSet dataSet = activity.getDataSet();
        assertEquals(average, dataSet.getAvgHeartRate());
    }


    /**
     * Testing the calcVertical() function. This test case passes
     * an activity where the user is moving upwards the whole time
     * and is therefore inactive and the vertical distance is not recorded.
     */
    @Test
    public void testVerticalUp() {
        Activity activity = activities.get(0);
        // Getting the dataSet out of the activity
        DataSet dataSet = activity.getDataSet();
        assertEquals(0, dataSet.getVerticalDistance(), 0);
    }


    /**
     * Testing the calcVertical() function. This test case passes
     * an activity where the user is moving downwards the whole time
     * and is therefore active and the vertical distance calculated.
     */
    @Test
    public void testVerticalDown() {
        Activity activity = activities.get(1);
        // Getting the dataSet out of the activity
        DataSet dataSet = activity.getDataSet();
        double vertical = DataAnalyser.roundNum(1802.69 - 1792.66);
        assertEquals(vertical, dataSet.getVerticalDistance(), 0);
    }


    /**
     * Testing the topSpeed function. This also tests the appendSpeed and
     * oneSpeed functions as they are used prior to the topSpeed function.
     */
    @Test
    //TODO: look over
    public void testTopSpeed() {
        Activity activity = activities.get(1);
        // Getting the dataSet out of the activity
        DataSet dataSet = activity.getDataSet();
        assertEquals(12.15, dataSet.getTopSpeed(), 0.0);
    }

  @Test
  /**
   * Testing the calcAvgSpeed function. This also tests the appendSpeed and
   * oneSpeed functions as they are used prior to the calcAvgSpeed function.
   */
     public void testAvgSpeed() {
         Activity activity = activities.get(1);
        // Getting the dataSet out of the activity
         DataSet dataSet = activity.getDataSet();
         assertEquals(7.17, dataSet.getAvgSpeed(), 0.0);
     }


    /**
     * Testing the calcBMI function.
     */
    @Test
    public void testBMI() {
        double height = 1.75;
        double weight = 70;
        double bmi = dataAnalyser.calcBMI(height, weight);
        assertEquals(22.86, bmi, 0.0);
    }



    @Test
    /**
     * Testing the calcCalBurned function.
     */
    public void testCalories() {
        assertEquals(3.25, activities.get(3).getDataSet().getCaloriesBurned(), 0.0);
    }

}