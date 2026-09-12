# Fix Dagger/Hilt MissingBinding for TestUseCase

The build is failing because Hilt cannot find a binding for `TestUseCase`. This is due to several reasons:
1. The implementation modules (`:domain:home:impl`, `:data:card:impl`, etc.) are not included as dependencies in the `:app` module, so Hilt doesn't scan their modules.
2. `CardRepositoryImpl` is missing an `@Inject` constructor, making it non-injectable for its Hilt binding.
3. The `Retrofit` provider in `NetworkModule` is commented out, preventing the provision of `ScryfallApi`, which is a transitive dependency of `TestUseCase`.

## Proposed Changes

### Build Configuration

#### [MODIFY] [app/build.gradle.kts](file:///D:/Documents/Android projects/AndroidGwenTest/app/build.gradle.kts)
- Uncomment domain and implementation dependencies.
- Add missing implementation dependencies for data and network layers to ensure all Hilt modules are included in the dependency graph.

### Domain Implementation

#### [MODIFY] [CardRepositoryImpl.kt](file:///D:/Documents/Android projects/AndroidGwenTest/data/card/impl/src/main/java/com/arcryalis/gwentest/card/impl/CardRepositoryImpl.kt)
- Add `@Inject constructor` to `CardRepositoryImpl` so Hilt can instantiate it.

### Network Configuration

#### [MODIFY] [NetworkModule.kt](file:///D:/Documents/Android projects/AndroidGwenTest/network/src/main/java/com/arcryalis/gwentest/network/di/NetworkModule.kt)
- Uncomment the `Retrofit` provider.
- Add necessary imports for `Retrofit`, `Json`, and `MediaType`.
- Fix the `provideScryfallApi` redundancy (it's already in `RemoteModule`, so I'll keep it commented or remove it from `NetworkModule` if it conflicts).

## Verification Plan

### Automated Tests
- Run `./gradlew :app:hiltJavaCompileDebug` to verify that Hilt code generation succeeds.
- Run a full build: `./gradlew assembleDebug`.

### Manual Verification
- N/A (Build fix)
