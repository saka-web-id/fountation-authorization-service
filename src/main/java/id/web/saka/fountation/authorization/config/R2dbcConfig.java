package id.web.saka.fountation.authorization.config;

import id.web.saka.fountation.authorization.util.converter.JsonNodeReadConverter;
import id.web.saka.fountation.authorization.util.converter.JsonNodeWriteConverter;
import id.web.saka.fountation.authorization.util.converter.RoleNameReadConverter;
import id.web.saka.fountation.authorization.util.converter.RoleNameWriteConverter;
import io.r2dbc.spi.ConnectionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.r2dbc.config.AbstractR2dbcConfiguration;
import org.springframework.data.r2dbc.config.EnableR2dbcAuditing;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;
import org.springframework.transaction.ReactiveTransactionManager;
import org.springframework.transaction.reactive.TransactionalOperator;

import java.util.List;

@Configuration
@EnableR2dbcAuditing
@EnableR2dbcRepositories(basePackages = {
        "id.web.saka.fountation.authorization.company",
        "id.web.saka.fountation.authorization.permission",
        "id.web.saka.fountation.authorization.role",
        "id.web.saka.fountation.authorization.user.role"
})
public class R2dbcConfig extends AbstractR2dbcConfiguration {

    private final ConnectionFactory connectionFactory;

    public R2dbcConfig(ConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
    }

    @Override
    public ConnectionFactory connectionFactory() {
        return connectionFactory;
    }

    @Override
    protected List<Object> getCustomConverters() {
        return List.of(
                new RoleNameReadConverter(),
                new RoleNameWriteConverter(),
                new JsonNodeReadConverter(),
                new JsonNodeWriteConverter()
        );
    }

    @Bean
    public TransactionalOperator transactionalOperator(ReactiveTransactionManager txManager) {
        return TransactionalOperator.create(txManager);
    }

}
