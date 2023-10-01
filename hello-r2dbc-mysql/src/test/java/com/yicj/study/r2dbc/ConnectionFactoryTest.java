package com.yicj.study.r2dbc;

import com.yicj.study.r2dbc.repository.UserEntity;
import io.r2dbc.spi.*;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.time.Duration;
import static io.r2dbc.spi.ConnectionFactoryOptions.*;

/**
 * @author yicj
 * @date 2023/10/1 11:24
 */
@Slf4j
public class ConnectionFactoryTest {

    @Test
    public void hello() throws InterruptedException {
        ConnectionFactoryOptions options = ConnectionFactoryOptions.builder()
                .option(DRIVER, "mysql")
                //.option(HOST, "192.168.99.51")
                .option(HOST, "127.0.0.1")
                .option(PORT, 3306)  // optional, default 3306
                .option(USER, "root")
                .option(PASSWORD, "root") // optional, default null, null means has no password
                .option(DATABASE, "imooc_security") // optional, default null, null means not specifying the database
                .option(CONNECT_TIMEOUT, Duration.ofSeconds(3)) // optional, default null, null means no timeout
                .option(Option.valueOf("socketTimeout"), Duration.ofSeconds(4)) // deprecated since 1.0.1, because it has no effect and serves no purpose.
                .build();
        ConnectionFactory connectionFactory = ConnectionFactories.get(options);
        // Creating a Mono using Project Reactor
        Mono<Connection> connectionMono = Mono.from(connectionFactory.create());
        connectionMono.flatMapMany(connection -> {
            String sql = "SELECT * FROM user" ;
            return Flux.from(connection.createStatement(sql).execute())
                .flatMap(result ->
                    result.map((row, metadata) -> {
                        Long id = row.get("id", Long.class);
                        String name = row.get("name", String.class);
                        String username = row.get("username", String.class);
                        UserEntity user = new UserEntity();
                        user.setId(id);
                        user.setName(name);
                        user.setUsername(username);
                        return user;
                    })
                );
        })
        .subscribe(entity -> log.info("entity : {}", entity));
        Thread.sleep(2000);
    }

}
