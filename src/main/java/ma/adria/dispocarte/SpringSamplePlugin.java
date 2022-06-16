package ma.adria.dispocarte;


import ma.adria.plugincontract.Contract;
import org.pf4j.Extension;
import org.pf4j.PluginWrapper;
import org.pf4j.spring.SpringPlugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

public class SpringSamplePlugin extends SpringPlugin {

    private static final Logger log = LoggerFactory.getLogger(SpringSamplePlugin.class);

    public SpringSamplePlugin(PluginWrapper wrapper) {
        super(wrapper);
    }

    @Override
    public void start() {
        log.info("Spring first plugin.start()");
    }

    @Override
    public void stop() {
        log.info("Spring first plugin.stop()");
        super.stop(); // to close applicationContext
    }

    @Override
    protected ApplicationContext createApplicationContext() {
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext();
        applicationContext.setClassLoader(getWrapper().getPluginClassLoader());
        applicationContext.register(ma.adria.dispocarte.ApplicationConfiguration.class);
        applicationContext.refresh();
        return applicationContext;
    }


    @Extension(ordinal = 1)
    public static class SpringPlugin implements Contract {

        @Autowired
        private ma.adria.dispocarte.GreetProvider greetProvider;



        @Override
        public String identify() {
            return greetProvider.provide();
        }

        @Override
        public List<Object> mvcControllers() {
            return new ArrayList<Object>() {{
            }};
        }


        @Override
        public int porocess(Object o) {
            log.info("processing...........");
            return 0;
        }

        @Override
        public void processFile(String pathFile) throws IOException {

        }

        @Override
        public List<RouterFunction<?>> reactiveRoutes() {
            return new ArrayList<RouterFunction<?>>() {{
                add(route(GET("/plugin-end-point"),
                        req -> ServerResponse.ok().body(Mono.just(greetings()), String.class))
                );
                add(route(POST("/conversion/{temperatureCelsius}"),
                        req -> ServerResponse.ok().body(Mono.just(convertToDollars(Float.parseFloat(req.pathVariable("temperatureCelsius")))), Float.class))
                );
            }

            };
        }
        public String greetings(){
            return "greetings from spring first plugin";
        }
        public Float convertToDollars(float temperatureCelsius ){
            float temperatureFahrenheit = (temperatureCelsius * 9/5) + 32;
            return temperatureFahrenheit;
        }


    }


}
