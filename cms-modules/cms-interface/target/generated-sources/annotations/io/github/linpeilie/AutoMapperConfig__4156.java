package io.github.linpeilie;

import org.mapstruct.Builder;
import org.mapstruct.MapperConfig;
import org.mapstruct.ReportingPolicy;

@MapperConfig(
    componentModel = "spring-lazy",
    uses = {ConverterMapperAdapter__4156.class},
    unmappedTargetPolicy = ReportingPolicy.IGNORE,
    builder = @Builder(buildMethod = "build", disableBuilder = true)
)
public interface AutoMapperConfig__4156 {
}
