package config;

import org.aeonbits.owner.Config;
import org.aeonbits.owner.Config.Sources;

@Sources("classpath:config.properties")
public interface ServerConfig extends Config {

	@Key("duckUrl")
	String duckUrl();

	@Key("demoUrl")
	String demoUrl();

	@Key("otusUrl")
	String otusUrl();

	@Key("email")
	String email();

	@Key("password")
	String password();
}
