package net.zuiron.photosynthesis.config;

public class CropConfig {
    private int[] maxAge;
    private int[] minAge;
    private float[] biomesTemperature;
    private float[] biomesHumidity;

    public CropConfig(int[] maxAge, int[] minAge, float[] biomesTemperature, float[] biomesHumidity){
        this.maxAge=maxAge;
        this.minAge=minAge;
        this.biomesTemperature = biomesTemperature;
        this.biomesHumidity = biomesHumidity;
    }

    public int[] getMinAge() {
        return minAge;
    }

    public int[] getMaxAge() {
        return maxAge;
    }

    public float[] getBiomesHumidity() {
        return biomesHumidity;
    }

    public float[] getBiomesTemperature() {
        return biomesTemperature;
    }
}
