package appcontest.playabq;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Stephen on 3/23/2016.
 *
 * Class meant to filter the parsed json community center and map information.
 */
public class Filter {
    private List<Map> communityCenterList;
    private List<Map> parkList;
    private List<Map> currentFilteredLocations;
    public Filter(List<Map> commList, List<Map> prkList) {
        communityCenterList=commList;
        parkList=prkList;
        currentFilteredLocations=new ArrayList<Map>();
    }

    /**
     *
     * @param requiredFeatures a list of preferred features for a park or community center.
     * @return a list of community centers and parks that include all of the features
     */
    public List<Map> intersectGetLocationsWith(List<String>requiredFeatures) {
        currentFilteredLocations.clear();
            for (Map ctr : communityCenterList) {
                for (String requiredFeature:requiredFeatures) {
                    if (!resemblesTruth(ctr, requiredFeature)) {
                        currentFilteredLocations.remove(ctr);
                        break;
                    } else if (!currentFilteredLocations.contains(ctr)) {
                        currentFilteredLocations.add(ctr);
                    }
                }
            }
            for (Map prk : parkList) {
                for (String requiredFeature:requiredFeatures) {
                    if (!resemblesTruth(prk, requiredFeature)) {
                        currentFilteredLocations.remove(prk);
                        break;
                    } else if (!currentFilteredLocations.contains(prk)) {
                        currentFilteredLocations.add(prk);
                    }
                }
            }
        return currentFilteredLocations;
    }

    /**
     *
     * @param requiredFeatures a list of preferred features for a park or community center.
     * @return a list of community centers and parks that include any of the features
     */
    public List<Map> unionGetLocationsWith(List<String>requiredFeatures) {
        currentFilteredLocations.clear();
        for (String requiredFeature:requiredFeatures) {
            for (Map ctr : communityCenterList) {
                if (resemblesTruth(ctr, requiredFeature) && !currentFilteredLocations.contains(ctr)) {
                    currentFilteredLocations.add(ctr);
                }
            }
            for (Map prk : parkList) {
                if (resemblesTruth(prk, requiredFeature) && !currentFilteredLocations.contains(prk)) {
                    currentFilteredLocations.add(prk);
                }
            }
        }
        return currentFilteredLocations;
    }


    /**
     *
     * @param location a park or community center
     * @param feature a feature of a park or community center
     * @return if the park or community center contains the feature based on the json file.
     */
    private static boolean resemblesTruth(Map location,String feature) {
        Object predicate;
        if ((predicate=location.get(feature))==null)
            return false;
        String strPred = (String) predicate.toString();
        return !(strPred.equalsIgnoreCase("false") || strPred.equals("0"));
    }

    /**
     * Convenience method to print the last filtered list of parks and centers
     */
    public void printLocations()
    {
        for(Map location:currentFilteredLocations)
        {
            System.out.println(location.get("CENTERNAME"));
        }
    }

    public static void filterExample()
    {
        /*
        private List<String> filterFeatures= new ArrayList<String>();
        Filter filter = new Filter(commList,parkList);
        filterFeatures.add("GYMNASIUM");
        filterFeatures.add("OUTDOORBASKETBALL");
        filter.getLocationsWith(filterFeatures);
        filter.printLocations(); */
    }
}
