package beyond.crud_sql.common.config

import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.PropertySource
import org.springframework.context.annotation.PropertySources

@Configuration
@PropertySources(PropertySource("classpath:/env.properties"))
class PropertyConfig {
}