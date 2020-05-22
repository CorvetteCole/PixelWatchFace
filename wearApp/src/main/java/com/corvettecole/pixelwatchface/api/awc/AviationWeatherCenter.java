package com.corvettecole.pixelwatchface.api.awc;

import static com.corvettecole.pixelwatchface.util.Constants.BOUNDING_BOX_OFFSET;

import android.annotation.SuppressLint;
import android.location.Location;
import android.util.Log;
import ca.braunit.weatherparser.exception.DecoderException;
import ca.braunit.weatherparser.metar.MetarDecoder;
import ca.braunit.weatherparser.metar.domain.Metar;
import com.corvettecole.pixelwatchface.R;
import com.corvettecole.pixelwatchface.api.WeatherProvider;
import com.corvettecole.pixelwatchface.api.awc.models.Feature;
import com.corvettecole.pixelwatchface.api.awc.models.FeatureCollection;
import com.corvettecole.pixelwatchface.models.Weather;
import com.corvettecole.pixelwatchface.models.WeatherProviderType;
import com.corvettecole.pixelwatchface.models.metar.CloudQuantity;
import com.corvettecole.pixelwatchface.models.metar.Descriptor;
import com.corvettecole.pixelwatchface.models.metar.Intensity;
import com.corvettecole.pixelwatchface.models.metar.Precipitation;
import com.corvettecole.pixelwatchface.models.metar.WeatherCondition;
import com.google.gson.JsonParseException;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import org.json.JSONObject;

public class AviationWeatherCenter extends WeatherProvider {

  public AviationWeatherCenter(Location location) {
    super(location);
  }

  @Override
  public WeatherProviderType getType() {
    return WeatherProviderType.AWC;
  }

  // https://www.aviationweather.gov/help/webservice?page=metarjson
  @SuppressLint("DefaultLocale")
  @Override
  public String getWeatherURL() {
    if (mRetry) {
      // roughly 120 mile radius.
      return String.format(
          "https://aviationweather.gov/cgi-bin/json/MetarJSON.php?bbox=%f,%f,%f,%f&density=all",
          mLocation.getLongitude() - BOUNDING_BOX_OFFSET * 3,
          mLocation.getLatitude() - BOUNDING_BOX_OFFSET * 3,
          mLocation.getLongitude() + BOUNDING_BOX_OFFSET * 3,
          mLocation.getLatitude() + BOUNDING_BOX_OFFSET * 3);
    } else {
      // roughly 40 mile radius as is. minlon, minlat, maxlon, maxlat
      return String.format(
          "https://aviationweather.gov/cgi-bin/json/MetarJSON.php?bbox=%f,%f,%f,%f&density=all",
          mLocation.getLongitude() - BOUNDING_BOX_OFFSET,
          mLocation.getLatitude() - BOUNDING_BOX_OFFSET,
          mLocation.getLongitude() + BOUNDING_BOX_OFFSET,
          mLocation.getLatitude() + BOUNDING_BOX_OFFSET);
    }
  }

  @Override
  public Weather parseWeatherResponse(JSONObject jsonObject)
      throws JsonParseException, IllegalArgumentException {
    Weather weather = new Weather();

    FeatureCollection parsedResult = mGson.fromJson(jsonObject.toString(), FeatureCollection.class);

    if (parsedResult.getFeatures().size() == 0) {
      mRetry = true;
      throw new IllegalArgumentException("METAR Station list empty!");
    } else {
      mRetry = false;
    }

    float minDistance = Float.MAX_VALUE;
    Feature minFeature = parsedResult.getFeatures().get(0);

    List<CloudQuantity> cloudQuantities = new ArrayList<>();
    // get closest airport
    for (Feature feature : parsedResult.getFeatures()) {
      List<Double> coordinates = feature.getGeometry().getCoordinates();
      Location airportLocation = new Location(mLocation);
      cloudQuantities.add(CloudQuantity.getEnum(feature.getProperties().getCover()));
      airportLocation.setLongitude(coordinates.get(0));
      airportLocation.setLatitude(coordinates.get(1));
      float distance = mLocation.distanceTo(airportLocation);
      if (distance < minDistance) {
        minDistance = distance;
        minFeature = feature;
      }
    }

    CloudQuantity cloudQuantityMode = cloudQuantities.stream().collect(
        Collectors.groupingBy(w -> w, Collectors.counting()))
        .entrySet()
        .stream()
        .max(Entry.comparingByValue())
        .get()
        .getKey();

    weather.setTemperature(minFeature.getProperties().getTemp());
    weather.setIconID(getWeatherIconID(minFeature, cloudQuantityMode));
    ZonedDateTime zonedDateTime = ZonedDateTime.parse(minFeature.getProperties().getObsTime());
    weather.setTime(zonedDateTime.toEpochSecond());
    return weather;
  }


  private int getWeatherIconID(Feature feature, CloudQuantity cloudQuantity)
      throws IllegalArgumentException {
    Log.d("AWC Parsing", feature.getProperties().getCover());

    List<WeatherCondition> weatherConditions = null;
    try {
      Metar metar = MetarDecoder.decodeMetar(feature.getProperties().getRawOb()).getMetar();
      weatherConditions = getWeatherConditions(metar);
    } catch (DecoderException | IllegalArgumentException e) {
      e.printStackTrace();
      // TODO log error decoding METAR or getting weather conditions
    }

    if (weatherConditions == null) {
      // problems parsing weather conditions
      throw new IllegalArgumentException();
    }

    if (weatherConditions.size() == 0) {
      return getCloudIcon(cloudQuantity);
    } else {
      if (weatherConditions.size() > 1) {
        boolean hasRain = false;
        boolean hasSnow = false;
        for (WeatherCondition weatherCondition : weatherConditions) {
          switch (weatherCondition.getPrecipitation()) {
            case RAIN:
            case DRIZZLE:
              hasRain = true;
              break;
            case SNOW:
            case SNOW_GRAINS:
              hasSnow = true;
              break;
          }
        }
        if (hasRain && hasSnow) {
          return R.drawable.wintry_mix; // wintry mix
        }
      }

      int iconID = R.drawable.sunny;
      Precipitation iconPrecipitation = Precipitation.UNKNOWN_PRECIPITATION;

      for (WeatherCondition weatherCondition : weatherConditions) {
        // if the weather condition is of a higher priority than the one that dictated the icon, run through the switch statement
        if (weatherCondition.getPrecipitation().compareTo(iconPrecipitation) > 0) {
          iconPrecipitation = weatherCondition.getPrecipitation();
          switch (weatherCondition.getPrecipitation()) {
            case RAIN:
              iconID = getRainIcon(weatherCondition, cloudQuantity);
              break;
            case DRIZZLE:
              iconID = R.drawable.drizzle;
              break;
            case SNOW:
              switch (weatherCondition.getIntensity()) {
                case LIGHT:
                  iconID = R.drawable.flurries;
                  break;
                case MODERATE:
                  iconID = R.drawable.snow_showers;
                  break;
                case HEAVY:
                  iconID = R.drawable.blizzard;
                  break;
              }
              break;
            case SNOW_GRAINS:
              iconID = R.drawable.flurries;
              break;
            case ICE_PELLETS:
            case ICE_CRYSTALS:
            case HAIL:
            case SMALL_HAIL:
              iconID = R.drawable.wintry_mix; // wintry_mix_rain
              break;

            case FOG:
            case VOLCANIC_ASH:
            case MIST:
            case HAZE:
            case WIDESPREAD_DUST:
            case SMOKE:
            case SAND:
            case SPRAY:
            case SQUALL:
            case SAND_WHIRLS:
            case DUSTSTORM:
            case SANDSTORM:
            case FUNNEL_CLOUD:
              iconID = R.drawable.haze_fog_dust_smoke; // haze_fog_dust_smoke
              break;
            case UNKNOWN_PRECIPITATION:
              iconID = getCloudIcon(cloudQuantity);
              break;
          }
        }
      }
      return iconID;
    }
  }


  private int getRainIcon(WeatherCondition weatherCondition, CloudQuantity cloudQuantity) {
    if (weatherCondition.getDescriptor() != null) {
      if (weatherCondition.getDescriptor().equals(Descriptor.THUNDERSTORM)) {
        switch (cloudQuantity) {
          case SKC:
          case NSC:
          case FEW:
          case SCT:
            return isDay() ? R.drawable.isolated_scattered_tstorms_day
                : R.drawable.isolated_scattered_tstorms_night; // isolated scatter tstorms
          case BKN:
          case OVC:
            return R.drawable.strong_tstorms; // strong tstorms
        }
      }
      if (weatherCondition.getDescriptor().equals(Descriptor.FREEZING)) {
        return R.drawable.wintry_mix; // wintry_mix_rain_snow
      }
    }
    if (weatherCondition.getIntensity().equals(Intensity.HEAVY)) {
      return R.drawable.heavy_rain; // heavy rain
    } else {
      switch (cloudQuantity) {
        case SKC:
        case NSC:
        case FEW:
        case SCT:
          return isDay() ? R.drawable.scattered_showers_day
              : R.drawable.scattered_showers_night; // scattered showers
        default:
        case BKN:
        case OVC:
          return R.drawable.showers_rain; // showers rain
      }
    }
  }

  private int getCloudIcon(CloudQuantity cloudQuantity) {
    switch (cloudQuantity) {
      default:
      case CLR:
      case SKC:
      case NSC:
        return isDay() ? R.drawable.sunny : R.drawable.clear_night;  // sunny/clear night
      case FEW:
        return isDay() ? R.drawable.mostly_sunny : R.drawable.mostly_clear_night; // mostly sunny
      case SCT:
        return isDay() ? R.drawable.partly_cloudy : R.drawable.partly_cloudy_night; // partly cloudy
      case BKN:
      case OVC:
        return isDay() ? R.drawable.mostly_cloudy_day
            : R.drawable.mostly_cloudy_night; // mostly cloudy

    }
  }

  private List<WeatherCondition> getWeatherConditions(Metar metar) throws IllegalArgumentException {
    ArrayList<WeatherCondition> weatherConditions = new ArrayList<>();
    if (metar.getPresentWeather().size() > 0) {
      for (ca.braunit.weatherparser.common.domain.Weather presentWeather : metar
          .getPresentWeather()) {
        Precipitation precipitation = null;
        Intensity intensity = null;
        Descriptor descriptor = null;

        if (presentWeather.getPrecipitationCode() != null) {
          precipitation = Precipitation.getEnum(presentWeather.getPrecipitationCode());
          intensity = Intensity.getEnum(presentWeather.getIntensityCode());
          if (presentWeather.getDescriptorCode() != null) {
            descriptor = Descriptor.getEnum(presentWeather.getDescriptorCode());
          }
        }

        if (precipitation != null) {
          weatherConditions.add(new WeatherCondition(precipitation, intensity, descriptor));
        }
      }
    }
    return weatherConditions;
  }
}
