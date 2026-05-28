# Repository Guidelines

## Project Structure & Module Organization
This is a Maven-based Spring Boot application targeting Java 21. Backend code lives in `src/main/java/org/agty/elfiumexpress`, split by feature areas such as `modules/express`, `modules/security`, `storage`, `api`, and `utils`. Thymeleaf templates are in `src/main/resources/templates`, static frontend assets are in `src/main/resources/static`, and app configuration starts in `src/main/resources/application.properties`. Local database settings are kept in `config/config.ini`. Tests live under `src/test/java`.

## Build, Test, and Development Commands
Use the Maven wrapper so the project builds consistently:

- `./mvnw spring-boot:run` starts the app locally.
- `./mvnw test` runs the JUnit 5 test suite.
- `./mvnw package` compiles, tests, and builds the jar in `target/`.
- `./mvnw -DskipTests compile` is useful for quick compile checks.

The project depends on a PostgreSQL setup defined in `config/config.ini` and custom AGTY artifacts declared in `pom.xml`.

## Coding Style & Naming Conventions
Follow the existing style: 4-space indentation, no tabs, and standard Spring constructor injection for controllers and services. Keep package names lowercase (`org.agty.elfiumexpress.*`), classes in `PascalCase`, methods and fields in `camelCase`, and constants in `UPPER_CASE`. Match existing MVC naming, for example `ExpressPanelController`, `ExpressPanelService`, and `ExpressPanelRepo`. No formatter or linter is configured in the repo, so keep changes consistent with surrounding code.

## Testing Guidelines
Tests use Spring Boot Test with JUnit 5. Add tests under `src/test/java` mirroring the production package structure. Name test classes `*Tests` and prefer focused method names such as `contextLoads()` or `saveExpressPanel_persistsFiles()`. Add controller, service, or repository coverage for new behavior; do not rely only on the existing smoke test.

## Commit & Pull Request Guidelines
Recent history uses short, imperative commit messages such as `Change number of versions` and `First release`. Keep commits small and descriptive, ideally one logical change per commit. For pull requests, include a brief summary, note any config or database impact, link related issues, and attach screenshots when changing Thymeleaf views or static UI assets.

## Security & Configuration Tips
Do not commit real credentials or user content. Treat `config/config.ini` and `content/` as environment-specific data, and keep secrets out of versioned defaults.
