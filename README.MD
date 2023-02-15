## How to use it

Add this configuration:
```kotlin
@SpringBootApplication(
scanBasePackages = [
"project.base.package",
"es.cpinedo.kotlinbase"]
)
@EnableJpaRepositories(
basePackages = [
"project.base.package",
"es.cpinedo.kotlinbase.security.infrastructure.repository"]
)
@EntityScan(
basePackages = [
"project.base.package",", 
"es.cpinedo.kotlinbase.security.infrastructure.repository.model"
])
```

Implement the createUserCommand Handler

```kotlin
class CreateUserHandler() : Handler<Unit, CreateUserCommand> {
    override fun invoke(request: CreateUserCommand) {
        TODO("User created business logic")
    }
}
```