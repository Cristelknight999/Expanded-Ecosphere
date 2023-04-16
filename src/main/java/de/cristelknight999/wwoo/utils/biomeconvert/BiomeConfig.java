package de.cristelknight999.wwoo.utils.biomeconvert;

import java.util.List;
import java.util.Objects;

public class BiomeConfig {

    public Integer skyColor;

    public Integer waterColor;

    public Integer waterFogColor;

    public List<String> features;
    /*
    public BiomeConfig(int skyColor, int waterColor, int waterFogColor, List<String> list) {
        this.skyColor = skyColor;
        this.waterColor = waterColor;
        this.waterFogColor = waterFogColor;
        this.features = list;
    }

     */

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BiomeConfig config = (BiomeConfig) o;
        return Objects.equals(skyColor, config.skyColor) && Objects.equals(waterColor, config.waterColor) && Objects.equals(waterFogColor, config.waterFogColor) && Objects.equals(features, config.features);
    }

    @Override
    public int hashCode() {
        return Objects.hash(skyColor, waterColor, waterFogColor, features);
    }
}


