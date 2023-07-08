package de.karaca.sep.client;

import io.vertx.core.Vertx;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

@Slf4j
public class ClientApplication {
    public static void main(String[] args) throws IOException {
        System.setProperty(
                "vertx.logger-delegate-factory-class-name",
                "io.vertx.core.logging.SLF4JLogDelegateFactory");

        log.info("Starting client...");

//        Terminal terminal = TerminalBuilder.builder().system(true).build();
//        LineReader lineReader = LineReaderBuilder.builder().terminal(terminal).build();
//
//        var line = lineReader.readLine("> ");
//
//        log.info("Hello, {}!", line);

        var vertx = Vertx.vertx();

        vertx.deployVerticle(new NetClientVerticle());
    }
}
