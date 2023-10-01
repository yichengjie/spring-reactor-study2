package com.yicj.study.r2dbc;

import com.yicj.study.r2dbc.repository.UserEntity;
import io.asyncer.r2dbc.mysql.MySqlConnectionConfiguration;
import io.asyncer.r2dbc.mysql.MySqlConnectionFactory;
import io.r2dbc.spi.Connection;
import io.r2dbc.spi.ConnectionFactories;
import io.r2dbc.spi.ConnectionFactory;
import io.r2dbc.spi.ConnectionFactoryOptions;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static io.r2dbc.spi.ConnectionFactoryOptions.*;

/**
 * @author yicj
 * @date 2023/10/1 11:24
 */
public class ConnectionFactoryTest {

    @Test
    public void hello(){
        // Notice: the query string must be URL encoded
        ConnectionFactory connectionFactory = ConnectionFactories.get(
                ConnectionFactoryOptions.builder()
                        .option(DRIVER, "mysql")
                        .option(HOST, "localhost")
                        .option(PORT, 3306)
                        .option(USER, "root")
                        .option(PASSWORD, "root")
                        .option(DATABASE, "imooc_security")
                        .build());
        Mono.from(connectionFactory.create())
                .flatMapMany(connection ->{
                    String sql = "SELECT * FROM audit_log";
                    return Flux.from(connection.createStatement(sql).execute())
                            .flatMap(result ->
                                result.map((row, metadata) ->{
                                    Long id = row.get("id", Long.class);
                                    String name = row.get("name", String.class);
                                    String username = row.get("username", String.class);
                                    UserEntity user = new UserEntity() ;
                                    user.setId(id);
                                    user.setName(name);
                                    user.setUsername(username);
                                    return user ;
                                })
                            );
                })
                .subscribe(testEntity ->System.out.println(testEntity.toString()));
    }


}
