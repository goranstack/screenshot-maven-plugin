# Copilot Instructions

## Project Overview

This is a Maven plugin that automates Java Swing GUI screenshot generation for documentation. It scans test classes for methods annotated with `@Screenshot`, invokes them reflectively to get `JComponent` instances, renders them to PNG images, and places them into JavaDoc `doc-files/` directories or AsciiDoc gallery articles.

The plugin provides three goals:
- **`screenshot:javadoc`** – Generates screenshots and embeds them in JavaDoc comments
- **`screenshot:gallery`** – Aggregates screenshots from all modules into an AsciiDoc article
- **`screenshot:splashscreen`** – Renders a single Swing panel to a splash screen image

## Module Structure

| Module | Role |
|--------|------|
| `screenshot-maven-plugin-api` | Public API: `@Screenshot` annotation, `ScreenshotDescriptor`, decorators, sample utilities |
| `screenshot-maven-plugin` | The plugin itself: three Mojo classes + scanning infrastructure |
| `screenshot-examples` | Lightweight example / functional test |
| `swingset3-demo-app` | Full example using SwingSet3 components |
| `worldclock-example` | Additional example |
| `gh-pages` | Documentation site generation |

The example modules act as integration tests — the plugin runs against them during `mvn install`.

## Build Commands

```bash
# Full build (includes all example modules)
mvn clean install

# Build only the core plugin modules
mvn clean install -pl screenshot-maven-plugin-api,screenshot-maven-plugin

# Build a single module
mvn clean install -pl screenshot-maven-plugin

# Run tests (minimal unit tests exist; functional validation happens via examples)
mvn test

# Release build (signs artifacts, requires GPG key and Sonatype OSSRH credentials)
mvn install -P release
```

There is no single-test runner configured — the few unit tests (`TestRegex`, `TestHtmlParser`) are run with standard Surefire. Example modules are the primary functional validation path.

## Architecture

### How the Plugin Works

1. A Mojo receives Maven project metadata and the full test classpath.
2. It builds a `URLClassLoader` from test classpath entries to access user code.
3. The `reflections8` library scans for methods annotated with `@Screenshot`.
4. Each method is invoked reflectively (must be `public`, no-arg, return `JComponent` or `Collection<ScreenshotDescriptor>`).
5. The component is sized to its preferred size, laid out via `SampleUtil.propagateDoLayout()`, then rendered to a `BufferedImage` using `component.print(Graphics2D)`.
6. Optional `ScreenshotDecorator`s (set as Swing client properties) apply callouts or emphasis.
7. The resulting PNG is written to the appropriate output path.

### Class Hierarchy

```
AbstractMojo
├── JavadocMojo        → JavaDocScreenshotScanner
├── GalleryMojo        → GalleryScreenshotScanner
└── SplashScreenMojo   (direct rendering, no scanner)

ScreenshotScanner (abstract)
├── JavaDocScreenshotScanner   → saves to doc-files/, optionally patches source
└── GalleryScreenshotScanner   → saves to gallery directory, writes .adoc
```

### JavaDoc Output Convention

Screenshots for a class `com.example.Foo` are written to:
```
src/main/java/com/example/Foo/doc-files/<scene>.png
```
The `scene` attribute of `@Screenshot` becomes the filename. The scanner can also auto-insert `<img>` tags into Javadoc comments using QDox source parsing.

## Key Conventions

### `@Screenshot` Annotation

Defined in `screenshot-maven-plugin-api`. Usage in test classes:

```java
@Screenshot(scene = "default")
public JComponent createMyPanel() {
    return new MyPanel();
}

// Return multiple variants from one method:
@Screenshot
public Collection<ScreenshotDescriptor> createVariants() {
    return Arrays.asList(
        new ScreenshotDescriptor(new MyPanel(), "variant-a"),
        new ScreenshotDescriptor(new MyPanel(), "variant-b")
    );
}
```

- Annotation retention is `RUNTIME` — scanning happens at plugin execution time.
- Methods must be `public`, no-arg, and return `JComponent` or `Collection<ScreenshotDescriptor>`.
- The `scene` attribute maps 1:1 to the output PNG filename.

### Multi-Look-and-Feel Screenshots

The `@Screenshot` annotation supports a `scene` to distinguish multiple screenshots of the same component under different Look & Feels (see `ButtonDemoTest` in `swingset3-demo-app`).

### Decorator Pattern

`ScreenshotDecorator` implementations are attached to a component as a Swing client property before `print()` is called. Decorators (callouts, frames, emphasis) live in `screenshot-maven-plugin-api/decorate/`.

### Locale Support

`LocaleSpec` (in the plugin module) bridges Maven XML plugin configuration to `java.util.Locale`. Multiple locales can be configured for `javadoc` goal screenshots.

### Classpath Isolation

The plugin loads user test classes via a separate `URLClassLoader`. When adding new scanning or rendering logic, be aware that classes from the user's project are loaded in this child classloader, not the plugin classloader.

## Java & Build Tooling

- **Java source/target**: 1.8
- **Maven**: 3.9.x (installed at `~/tools/maven/apache-maven-3.9.9`); `JAVA_HOME` points to JDK 8 Temurin
- **Project-local Maven settings**: `.mvn/maven.config` + `.mvn/settings.xml` override the corporate `~/.m2/settings.xml` so builds resolve from Maven Central
- **Docs**: AsciiDoc (`readme.adoc`, `gh-pages` module), published to GitHub Pages

## CI/CD (GitHub Actions)

Three workflows in `.github/workflows/`:

| Workflow | Trigger | What it does |
|----------|---------|--------------|
| `ci.yml` | Every push and PR | `mvn clean install` on Java 11 (ubuntu-latest) |
| `pages.yml` | Push to `master` | Full build, deploys `gh-pages/target/generated-docs` to `gh-pages` branch |
| `release.yml` | Tag push (`v*`) | Sets pom version from tag, deploys signed artifacts to Sonatype OSSRH |

All workflows start Xvfb (`:99`) before building because the example modules render Swing components.

### Release process

1. Push a tag matching `v*` (e.g. `v1.0.3`)
2. The release workflow imports the GPG key from the `GPG_PRIVATE_KEY` secret, sets the pom version to match the tag, then runs:
   ```bash
   mvn clean deploy -P release -DskipTests -s .github/release-settings.xml
   ```
3. Artifacts are staged on Sonatype OSSRH and must be promoted to Maven Central via the [Sonatype Nexus UI](https://oss.sonatype.org).

### Required repository secrets

| Secret | Purpose |
|--------|---------|
| `GPG_PRIVATE_KEY` | Artifact signing (`gpg --export-secret-keys --armor <key-id>`) |
| `GPG_PASSPHRASE` | GPG key passphrase |
| `SONATYPE_USERNAME` | Sonatype OSSRH username |
| `SONATYPE_PASSWORD` | Sonatype OSSRH password |
