package de.karaca.sep.client;

import io.vertx.core.Vertx;
import io.vertx.ext.shell.ShellService;
import io.vertx.ext.shell.ShellServiceOptions;
import io.vertx.ext.shell.term.SSHTermOptions;

import lombok.extern.slf4j.Slf4j;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;

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

        ShellService.create(
                        vertx,
                        new ShellServiceOptions()
                                .setWelcomeMessage("asd")
                                .setSSHOptions(
                                        new SSHTermOptions().setHost("localhost").setPort(5555)))
                .start();

        vertx.deployVerticle(new NetClientVerticle());
    }
}
