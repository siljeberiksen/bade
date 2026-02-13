package no.badevann.config

import io.micronaut.context.annotation.Factory
import io.micronaut.core.convert.TypeConverter
import jakarta.inject.Singleton
import no.badevann.domain.*
import java.util.Optional

@Factory
class EnumConverterFactory {

    @Singleton
    fun waterTypeReadConverter(): TypeConverter<String, WaterType> =
        TypeConverter { value, _, _ -> Optional.ofNullable(WaterType.valueOf(value)) }

    @Singleton
    fun measurementTypeReadConverter(): TypeConverter<String, MeasurementType> =
        TypeConverter { value, _, _ -> Optional.ofNullable(MeasurementType.valueOf(value)) }

    @Singleton
    fun measurementUnitReadConverter(): TypeConverter<String, MeasurementUnit> =
        TypeConverter { value, _, _ -> Optional.ofNullable(MeasurementUnit.valueOf(value)) }

    @Singleton
    fun measurementQualifierReadConverter(): TypeConverter<String, MeasurementQualifier> =
        TypeConverter { value, _, _ -> Optional.ofNullable(MeasurementQualifier.valueOf(value)) }

    @Singleton
    fun dataSourceKindReadConverter(): TypeConverter<String, DataSourceKind> =
        TypeConverter { value, _, _ -> Optional.ofNullable(DataSourceKind.valueOf(value)) }

    @Singleton
    fun scrapeRunStatusReadConverter(): TypeConverter<String, ScrapeRunStatus> =
        TypeConverter { value, _, _ -> Optional.ofNullable(ScrapeRunStatus.valueOf(value)) }

    @Singleton
    fun scrapeTriggerReadConverter(): TypeConverter<String, ScrapeTrigger> =
        TypeConverter { value, _, _ -> Optional.ofNullable(ScrapeTrigger.valueOf(value)) }

    @Singleton
    fun statusLabelReadConverter(): TypeConverter<String, StatusLabel> =
        TypeConverter { value, _, _ -> Optional.ofNullable(StatusLabel.valueOf(value)) }

    @Singleton
    fun qualityRatingReadConverter(): TypeConverter<String, QualityRating> =
        TypeConverter { value, _, _ -> Optional.ofNullable(QualityRating.valueOf(value)) }

    @Singleton
    fun ratingBasisReadConverter(): TypeConverter<String, RatingBasis> =
        TypeConverter { value, _, _ -> Optional.ofNullable(RatingBasis.valueOf(value)) }

    @Singleton
    fun trendDirectionReadConverter(): TypeConverter<String, TrendDirection> =
        TypeConverter { value, _, _ -> Optional.ofNullable(TrendDirection.valueOf(value)) }
}
