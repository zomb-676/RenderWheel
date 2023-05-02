package gl;

import lombok.experimental.UtilityClass;
import org.apache.http.util.Asserts;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.appender.ConsoleAppender;
import org.apache.logging.log4j.core.config.Configurator;
import org.apache.logging.log4j.core.config.DefaultConfiguration;
import org.apache.logging.log4j.core.layout.PatternLayout;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL46;
import org.lwjgl.opengl.GLDebugMessageCallback;

import java.util.stream.IntStream;

@UtilityClass
public class Log {
	public static Logger LOGGER = LogManager.getLogger("ParticleStorm");
	private static final boolean LOG_EXTENSIONS = false;

	public static void init() {
		var level = Level.ALL;
		System.setProperty("log4j.skipJansi", "false");//support colorful output
		Configurator.setRootLevel(level);
		Configurator.reconfigure(new DefaultConfiguration() {
			@Override
			protected void setToDefault() {
				setName(DEFAULT_NAME + "@" + Integer.toHexString(hashCode()));
				var appender = ConsoleAppender.newBuilder()
						.setName("appender")
						.setLayout(
								PatternLayout.newBuilder()
										.withPattern("%highlight{[%d{HH:mm:ss}][%level][%thread/%c]:%msg%n}" +
												"{FATAL=#E199F5, ERROR=#F50700, WARN=#F50700, INFO=#33D8EB, DEBUG=#97FFA9, TRACE=white}")
										.build()
						).build();
				appender.start();
				addAppender(appender);
				getRootLogger().addAppender(appender, level, null);
				getRootLogger().setLevel(level);
			}
		});
		LOGGER.debug("log system init end");
	}

	public static void logGLInfo() {
		LOGGER.debug("COMPILE VERSION STRING" + " -> " + GLFW.glfwGetVersionString());
		checkError();
		LOGGER.debug("RENDERER" + " -> " + GL46.glGetString(GL20.GL_RENDERER));
		checkError();
		LOGGER.debug("VENDOR" + " -> " + GL46.glGetString(GL20.GL_VENDOR));
		checkError();

		LOGGER.debug("VERSION" + " -> " + GL46.glGetString(GL20.GL_VERSION));
		checkError();

		LOGGER.debug("SHADING_LANGUAGE_VERSION" + " -> " + GL46.glGetString(GL20.GL_SHADING_LANGUAGE_VERSION));
		checkError();

		if (LOG_EXTENSIONS) {
			IntStream.range(0, GL46.glGetInteger(GL46.GL_NUM_EXTENSIONS))
					.forEach(i -> LOGGER.debug("EXTENSION " + i + ":" + GL46.glGetStringi(GL46.GL_EXTENSIONS, i)));
		}
	}

	public static void checkError() {
		int error = GL46.glGetError();
		Asserts.check(error == GL46.GL_NO_ERROR, "error code:" + errorToString(error));
	}

	public static String errorToString(int error) {
		return switch (error) {
			case GL46.GL_NO_ERROR -> "NO ERROR";
			case GL46.GL_INVALID_ENUM -> "GL_INVALID_ENUM";
			case GL46.GL_INVALID_VALUE -> "GL_INVALID_VALUE";
			case GL46.GL_INVALID_OPERATION -> "GL_INVALID_OPERATION";
			case GL46.GL_INVALID_FRAMEBUFFER_OPERATION -> "GL_INVALID_FRAMEBUFFER_OPERATION";
			case GL46.GL_OUT_OF_MEMORY -> "GL_OUT_OF_MEMORY";
			case GL46.GL_STACK_UNDERFLOW -> "GL_STACK_UNDERFLOW";
			case GL46.GL_STACK_OVERFLOW -> "GL_STACK_OVERFLOW";
			default -> throw new RuntimeException("unknown error code:" + error);
		};
	}

	public static void registerDebug(boolean synchronize) {
		GL46.glEnable(GL46.GL_DEBUG_OUTPUT);
		if (synchronize) {
			GL46.glEnable(GL46.GL_DEBUG_OUTPUT_SYNCHRONOUS);
		} else {
			GL46.glDisable(GL46.GL_DEBUG_OUTPUT_SYNCHRONOUS);
		}
		GL46.glDebugMessageCallback((source, type, id, severity, length, message, userParam) -> {
			var log = "opengl debug," +
					"source:" +
					sourceToString(source) +
					"," +
					"type:" +
					typeToString(type) +
					"," +
					"severity:" +
					severityToString(severity) +
					"," +
					"message:" +
					GLDebugMessageCallback.getMessage(length, message);
			switch (severity) {
				case GL46.GL_DEBUG_SEVERITY_HIGH -> LOGGER.fatal(log);
				case GL46.GL_DEBUG_SEVERITY_MEDIUM -> LOGGER.error(log);
				case GL46.GL_DEBUG_SEVERITY_LOW -> LOGGER.warn(log);
				case GL46.GL_DEBUG_SEVERITY_NOTIFICATION -> LOGGER.info(log);
				default -> throw new IllegalArgumentException("unknown severity token:" + severity);
			}
		}, 0);
		LOGGER.info("gl debug callback setup");
	}

	private static String sourceToString(int source) {
		return switch (source) {
			case GL46.GL_DEBUG_SOURCE_API -> "API";
			case GL46.GL_DEBUG_SOURCE_WINDOW_SYSTEM -> "WINDOW SYSTEM";
			case GL46.GL_DEBUG_SOURCE_SHADER_COMPILER -> "SHADER COMPILER";
			case GL46.GL_DEBUG_SOURCE_THIRD_PARTY -> "THIRD PARTY";
			case GL46.GL_DEBUG_SOURCE_APPLICATION -> "APPLICATION";
			case GL46.GL_DEBUG_SOURCE_OTHER -> "OTHER";
			default -> throw new IllegalArgumentException("unknown source token:" + source);
		};
	}

	private static String typeToString(int type) {
		return switch (type) {
			case GL46.GL_DEBUG_TYPE_ERROR -> "ERROR";
			case GL46.GL_DEBUG_TYPE_DEPRECATED_BEHAVIOR -> "DEPRECATED BEHAVIOR";
			case GL46.GL_DEBUG_TYPE_UNDEFINED_BEHAVIOR -> "UNDEFINED BEHAVIOR";
			case GL46.GL_DEBUG_TYPE_PORTABILITY -> "PORTABILITY";
			case GL46.GL_DEBUG_TYPE_PERFORMANCE -> "PERFORMANCE";
			case GL46.GL_DEBUG_TYPE_OTHER -> "OTHER";
			case GL46.GL_DEBUG_TYPE_MARKER -> "MARKER";
			default -> throw new IllegalArgumentException("unknown type token:" + type);
		};
	}

	private static String severityToString(int severity) {
		return switch (severity) {
			case GL46.GL_DEBUG_SEVERITY_NOTIFICATION -> "NOTIFICATION";
			case GL46.GL_DEBUG_SEVERITY_HIGH -> "HIGH";
			case GL46.GL_DEBUG_SEVERITY_MEDIUM -> "MEDIUM";
			case GL46.GL_DEBUG_SEVERITY_LOW -> "LOW";
			default -> throw new IllegalArgumentException("unknown severity token:" + severity);
		};
	}

}
