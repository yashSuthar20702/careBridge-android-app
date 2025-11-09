package com.example.carebridge.model;

import java.util.List;

public class NearbySearchResponse {

    public List<Result> results;

    public static class Result {
        public Geometry geometry;
        public String name;
        public String vicinity;

        public static class Geometry {
            public LocationObj location;

            public static class LocationObj {
                public double lat;
                public double lng;
            }
        }
    }
}
